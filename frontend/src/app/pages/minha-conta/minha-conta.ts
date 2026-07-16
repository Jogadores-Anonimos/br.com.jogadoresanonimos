import {
  Component,
  effect,
  inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth/auth';
import { Arquivo as ArquivoService } from '../../services/arquivo/arquivo';
import { ArquivoResponse } from '../../services/arquivo/arquivo.model';
import { disparaDownloadDeBlob } from '../../services/arquivo/download-blob.util';
import { BibliotecaPessoalService } from '../../services/biblioteca-pessoal/biblioteca-pessoal';
import { ContaAuthForms } from './conta-auth-forms/conta-auth-forms';

type AbaConta = 'dashboard' | 'pedidos' | 'downloads' | 'enderecos' | 'detalhes';

interface PedidoConta {
  numero: string;
  data: string;
  item: string;
  status: 'Processando' | 'Concluído';
  total: string;
}

@Component({
  selector: 'app-minha-conta',
  standalone: true,
  imports: [FormsModule, RouterLink, ResourceLayout, ContaAuthForms],
  templateUrl: './minha-conta.html',
  styleUrl: './minha-conta.css',
})
export class MinhaConta {
  readonly bannerSrc = '/images/home/reuniao-discussao.jpg';

  private readonly bibliotecaPessoal = inject(BibliotecaPessoalService);
  private readonly arquivoService = inject(ArquivoService);
  private readonly router = inject(Router);

  constructor(
    public readonly auth: Auth,
  ) {
    effect(() => {
      const usuario = this.auth.usuarioAtual();
      if (usuario) {
        this.trocaSenhaObrigatoria = usuario.deveTrocarSenha;
        if (usuario.deveTrocarSenha) {
          this.abaAtiva = 'detalhes';
          this.nomeExibicao = usuario.apelido;
          this.carregarDetalhesConta();
          return;
        }
        if (usuario.role === 'ADMIN') {
          this.router.navigateByUrl('/admin');
          return;
        }
        this.nomeExibicao = usuario.apelido;
        this.carregarDownloads();
        this.carregarDetalhesConta();
      } else {
        this.downloads = [];
      }
    });
  }

  private carregarDetalhesConta(): void {
    const detalhesEmCache = this.auth.usuarioAtualDetalhado();
    if (detalhesEmCache) {
      this.nomeExibicao = detalhesEmCache.apelido;
      this.emailDetalhes = detalhesEmCache.email;
      return;
    }
    this.auth.buscarUsuarioLogado().subscribe((resposta) => {
      if (resposta.success && resposta.data) {
        this.nomeExibicao = resposta.data.apelido;
        this.emailDetalhes = resposta.data.email;
      }
    });
  }

  private carregarDownloads(): void {
    this.downloadsCarregando = true;
    this.bibliotecaPessoal.listarMinhaBiblioteca().subscribe((resposta) => {
      this.downloadsCarregando = false;
      if (resposta.success && resposta.data) {
        this.downloads = resposta.data;
      }
    });
  }

  removerDownload(arquivoId: string): void {
    if (this.downloadsRemovendo.has(arquivoId)) {
      return;
    }
    this.downloadsRemovendo.add(arquivoId);
    this.bibliotecaPessoal.removerDaBiblioteca(arquivoId).subscribe((resposta) => {
      this.downloadsRemovendo.delete(arquivoId);
      if (resposta.success) {
        this.downloads = this.downloads.filter((arquivo) => arquivo.id !== arquivoId);
      }
    });
  }

  abaAtiva: AbaConta = 'dashboard';
  trocaSenhaObrigatoria = false;

  selecionarAba(aba: AbaConta): void {
    if (this.trocaSenhaObrigatoria) {
      return;
    }
    this.abaAtiva = aba;
  }

  readonly pedidos: PedidoConta[] = [];

  downloads: ArquivoResponse[] = [];
  downloadsCarregando = false;
  downloadsRemovendo = new Set<string>();
  downloadsBaixando = new Set<string>();
  erroDownloadPorId = new Map<string, string>();

  baixarPdf(arquivo: ArquivoResponse): void {
    if (this.downloadsBaixando.has(arquivo.id)) {
      return;
    }
    this.downloadsBaixando.add(arquivo.id);
    this.erroDownloadPorId.delete(arquivo.id);
    this.arquivoService.baixarArquivo(arquivo.id).subscribe({
      next: (blob) => {
        this.downloadsBaixando.delete(arquivo.id);
        disparaDownloadDeBlob(blob, arquivo.nomeOriginal);
      },
      error: () => {
        this.downloadsBaixando.delete(arquivo.id);
        this.erroDownloadPorId.set(arquivo.id, 'Não foi possível baixar o arquivo agora. Tente novamente.');
      },
    });
  }

  nomeExibicao = '';
  emailDetalhes = '';
  senhaAtualDetalhes = '';
  novaSenhaDetalhes = '';
  confirmarNovaSenhaDetalhes = '';
  detalhesMensagem = '';
  detalhesSucesso = false;
  detalhesEnviando = false;

  onSalvarDetalhes(): void {
    this.detalhesEnviando = true;
    this.detalhesMensagem = '';
    this.detalhesSucesso = false;

    if (this.trocaSenhaObrigatoria) {
      this.salvarSenhaSeInformada();
      return;
    }

    this.auth
      .atualizarConta({ apelido: this.nomeExibicao, email: this.emailDetalhes })
      .subscribe((resposta) => {
        if (!resposta.success) {
          this.detalhesEnviando = false;
          this.detalhesMensagem = resposta.error?.message ?? 'Não foi possível salvar os dados informados.';
          return;
        }

        this.salvarSenhaSeInformada();
      });
  }

  private salvarSenhaSeInformada(): void {
    const querTrocarSenha = this.senhaAtualDetalhes || this.novaSenhaDetalhes || this.confirmarNovaSenhaDetalhes;
    if (!querTrocarSenha) {
      this.detalhesEnviando = false;
      if (this.trocaSenhaObrigatoria) {
        this.detalhesSucesso = false;
        this.detalhesMensagem = 'Por segurança, defina uma nova senha antes de continuar.';
        return;
      }
      this.detalhesSucesso = true;
      this.detalhesMensagem = 'Dados da conta atualizados com sucesso.';
      return;
    }

    this.auth
      .alterarSenha({
        senhaAtual: this.senhaAtualDetalhes,
        novaSenha: this.novaSenhaDetalhes,
        confirmarNovaSenha: this.confirmarNovaSenhaDetalhes,
      })
      .subscribe((resposta) => {
        this.detalhesEnviando = false;
        this.detalhesSucesso = resposta.success;
        if (resposta.success) {
          this.senhaAtualDetalhes = '';
          this.novaSenhaDetalhes = '';
          this.confirmarNovaSenhaDetalhes = '';
          this.detalhesMensagem = this.trocaSenhaObrigatoria
            ? 'Senha alterada com sucesso. Faça login novamente com a nova senha.'
            : 'Dados salvos e senha alterada com sucesso. Faça login novamente.';
          this.auth.logout();
        } else {
          this.detalhesMensagem = this.trocaSenhaObrigatoria
            ? (resposta.error?.message ?? 'Não foi possível alterar a senha.')
            : (resposta.error?.message ?? 'Dados salvos, mas não foi possível alterar a senha.');
        }
      });
  }

  sair(): void {
    this.auth.logout();
    this.abaAtiva = 'dashboard';
  }
}
