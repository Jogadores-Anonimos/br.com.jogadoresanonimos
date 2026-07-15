import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth/auth';
import { BibliotecaPessoalService } from '../../services/biblioteca-pessoal/biblioteca-pessoal';
import { Arquivo as ArquivoService } from '../../services/arquivo/arquivo';
import { ArquivoResponse } from '../../services/arquivo/arquivo.model';
import { disparaDownloadDeBlob } from '../../services/arquivo/download-blob.util';

@Component({
  selector: 'app-literatura',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './literatura.html',
  styleUrl: './literatura.css',
})
export class Literatura implements OnInit {
  private readonly arquivoService = inject(ArquivoService);
  private readonly bibliotecaPessoal = inject(BibliotecaPessoalService);
  readonly auth = inject(Auth);

  arquivos: ArquivoResponse[] = [];
  idsNaBiblioteca = new Set<string>();
  idsSalvando = new Set<string>();
  idsBaixando = new Set<string>();
  erroDownloadPorId = new Map<string, string>();

  ngOnInit(): void {
    this.arquivoService.listarArquivos().subscribe((resposta) => {
      if (resposta.success && resposta.data) {
        this.arquivos = resposta.data;
      }
    });

    if (this.auth.usuarioAtual()) {
      this.bibliotecaPessoal.listarMinhaBiblioteca().subscribe((resposta) => {
        if (resposta.success && resposta.data) {
          this.idsNaBiblioteca = new Set(resposta.data.map((arquivo) => arquivo.id));
        }
      });
    }
  }

  baixarPdf(arquivo: ArquivoResponse): void {
    if (this.idsBaixando.has(arquivo.id)) {
      return;
    }
    this.idsBaixando.add(arquivo.id);
    this.erroDownloadPorId.delete(arquivo.id);
    this.arquivoService.baixarArquivo(arquivo.id).subscribe({
      next: (blob) => {
        this.idsBaixando.delete(arquivo.id);
        disparaDownloadDeBlob(blob, arquivo.nomeOriginal);
      },
      error: () => {
        this.idsBaixando.delete(arquivo.id);
        this.erroDownloadPorId.set(arquivo.id, 'Não foi possível baixar o arquivo agora. Tente novamente.');
      },
    });
  }

  adicionarABiblioteca(arquivoId: string): void {
    if (this.idsNaBiblioteca.has(arquivoId) || this.idsSalvando.has(arquivoId)) {
      return;
    }
    this.idsSalvando.add(arquivoId);
    this.bibliotecaPessoal.adicionarABiblioteca(arquivoId).subscribe((resposta) => {
      this.idsSalvando.delete(arquivoId);
      if (resposta.success) {
        this.idsNaBiblioteca.add(arquivoId);
      }
    });
  }
}
