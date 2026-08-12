import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable, forkJoin } from 'rxjs';

import { ApiEnvelope, mapearErrosCampo } from '../../../services/api-envelope';
import { EnderecoLookupService } from '../../../services/endereco-lookup/endereco-lookup';
import { HotlineService } from '../../../services/hotline/hotline';
import { HotlineRegiao, HotlineTelefone, UNIDADES_FEDERATIVAS } from '../../../services/hotline/hotline.model';

interface FormularioTelefone {
  id: string;
  idOriginal: string | null;
  telefone: string;
}

interface FormularioRegiao {
  uf: string;
  cidade: string;
}

let contadorTelefoneFormulario = 0;
function proximoIdTelefoneFormulario(): string {
  contadorTelefoneFormulario += 1;
  return `form-telefone-${contadorTelefoneFormulario}`;
}

function telefoneFormularioVazio(): FormularioTelefone {
  return { id: proximoIdTelefoneFormulario(), idOriginal: null, telefone: '' };
}

function formularioRegiaoVazio(): FormularioRegiao {
  return { uf: '', cidade: '' };
}

function ordenarRegioes(regioes: HotlineRegiao[]): HotlineRegiao[] {
  return [...regioes].sort((a, b) => a.uf.localeCompare(b.uf) || a.cidade.localeCompare(b.cidade));
}

@Component({
  selector: 'app-admin-hotlines',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-hotlines.html',
  styleUrl: './admin-hotlines.css',
})
export class AdminHotlines implements OnInit {
  private readonly hotlineService = inject(HotlineService);
  private readonly enderecoLookupService = inject(EnderecoLookupService);

  readonly unidadesFederativas = UNIDADES_FEDERATIVAS;

  cidadesDaUf: string[] = [];
  carregandoCidades = false;
  erroCarregamentoCidades = '';

  carregando = true;
  erroCarregamento = '';
  regioes: HotlineRegiao[] = [];

  modoFormularioRegiao: 'fechado' | 'novo' | 'edicao' = 'fechado';
  regiaoEmEdicao: HotlineRegiao | null = null;
  formularioRegiao: FormularioRegiao = formularioRegiaoVazio();
  telefonesNovaRegiao: FormularioTelefone[] = [];
  salvandoRegiao = false;
  erroFormularioRegiao = '';
  errosCampoRegiao: Record<string, string> = {};

  confirmandoExclusaoRegiaoId: string | null = null;
  excluindoRegiao = false;
  erroExclusaoRegiao = '';

  regiaoEmEdicaoId: string | null = null;
  telefonesFormulario: FormularioTelefone[] = [];
  salvandoTelefones = false;
  erroFormularioTelefones = '';
  idsTelefoneInvalido = new Set<string>();

  ngOnInit(): void {
    this.carregarRegioes();
  }

  private carregarRegioes(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.hotlineService.listarHotlinesAdmin().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.regioes = envelope.data;
      } else {
        this.regioes = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar as linhas de ajuda.';
      }
    });
  }

  abrirNovaRegiao(): void {
    this.regiaoEmEdicao = null;
    this.formularioRegiao = formularioRegiaoVazio();
    this.erroFormularioRegiao = '';
    this.errosCampoRegiao = {};
    this.telefonesNovaRegiao = [telefoneFormularioVazio()];
    this.cidadesDaUf = [];
    this.erroCarregamentoCidades = '';
    this.modoFormularioRegiao = 'novo';
  }

  onUfSelecionada(uf: string): void {
    this.formularioRegiao.cidade = '';
    this.carregarCidadesDaUf(uf);
  }

  private carregarCidadesDaUf(uf: string, cidadeParaManter = ''): void {
    this.cidadesDaUf = [];
    this.erroCarregamentoCidades = '';
    if (!uf) {
      return;
    }
    this.carregandoCidades = true;
    this.enderecoLookupService
      .buscarMunicipiosPorUf(uf)
      .subscribe((municipios) => {
        this.carregandoCidades = false;
        if (!municipios) {
          this.erroCarregamentoCidades = 'Não foi possível carregar as cidades. Digite a cidade manualmente.';
          return;
        }
        this.cidadesDaUf = municipios.map((m) => m.nome);
        if (cidadeParaManter) {
          this.formularioRegiao.cidade = cidadeParaManter;
        }
      });
  }

  abrirEdicaoRegiao(regiao: HotlineRegiao): void {
    this.regiaoEmEdicao = regiao;
    this.formularioRegiao = { uf: regiao.uf, cidade: regiao.cidade };
    this.erroFormularioRegiao = '';
    this.errosCampoRegiao = {};
    this.telefonesNovaRegiao = [];
    this.modoFormularioRegiao = 'edicao';
    this.carregarCidadesDaUf(regiao.uf, regiao.cidade);
  }

  cancelarFormularioRegiao(): void {
    this.modoFormularioRegiao = 'fechado';
    this.regiaoEmEdicao = null;
    this.erroFormularioRegiao = '';
    this.errosCampoRegiao = {};
    this.telefonesNovaRegiao = [];
    this.cidadesDaUf = [];
    this.erroCarregamentoCidades = '';
  }

  adicionarTelefoneNovaRegiao(): void {
    this.telefonesNovaRegiao = [...this.telefonesNovaRegiao, telefoneFormularioVazio()];
  }

  removerTelefoneNovaRegiao(id: string): void {
    this.telefonesNovaRegiao = this.telefonesNovaRegiao.filter((t) => t.id !== id);
  }

  private regiaoJaExiste(uf: string, cidade: string): boolean {
    return this.regioes.some(
      (r) => r.id !== this.regiaoEmEdicao?.id && r.uf === uf && r.cidade.toLowerCase() === cidade.toLowerCase(),
    );
  }

  salvarRegiao(): void {
    this.errosCampoRegiao = {};

    if (!this.formularioRegiao.uf) {
      this.errosCampoRegiao['uf'] = 'Selecione uma UF.';
    }
    const cidade = this.formularioRegiao.cidade.trim();
    if (!cidade) {
      this.errosCampoRegiao['cidade'] = 'Cidade é obrigatória.';
    }
    if (Object.keys(this.errosCampoRegiao).length > 0) {
      this.erroFormularioRegiao = this.errosCampoRegiao['uf'] ?? this.errosCampoRegiao['cidade'];
      return;
    }
    if (this.regiaoJaExiste(this.formularioRegiao.uf, cidade)) {
      this.erroFormularioRegiao =
        'Já existe uma região com esta UF e cidade. Edite a região existente em vez de criar outra.';
      this.errosCampoRegiao = { uf: this.erroFormularioRegiao, cidade: this.erroFormularioRegiao };
      return;
    }
    const telefonesValidos = this.telefonesNovaRegiao.filter((t) => t.telefone.trim());

    this.salvandoRegiao = true;
    this.erroFormularioRegiao = '';

    const request = { uf: this.formularioRegiao.uf, cidade };

    if (this.regiaoEmEdicao) {
      this.hotlineService.editarRegiaoAdmin(this.regiaoEmEdicao.id, request).subscribe((envelope) => {
        this.salvandoRegiao = false;
        if (envelope.success && envelope.data) {
          const salva = envelope.data;
          this.regioes = ordenarRegioes(this.regioes.map((r) => (r.id === salva.id ? salva : r)));
          this.modoFormularioRegiao = 'fechado';
          this.regiaoEmEdicao = null;
        } else if (envelope.error?.code === 'CONFLICT') {
          this.erroFormularioRegiao =
            'Já existe uma região com esta UF e cidade. Edite a região existente em vez de criar outra.';
          this.errosCampoRegiao = { uf: this.erroFormularioRegiao, cidade: this.erroFormularioRegiao };
        } else {
          this.erroFormularioRegiao = envelope.error?.message ?? 'Não foi possível salvar a região.';
          this.errosCampoRegiao = mapearErrosCampo(envelope.error?.fields);
        }
      });
      return;
    }

    this.hotlineService.criarRegiaoAdmin(request).subscribe((envelopeRegiao) => {
      if (!envelopeRegiao.success || !envelopeRegiao.data) {
        this.salvandoRegiao = false;
        if (envelopeRegiao.error?.code === 'CONFLICT') {
          this.erroFormularioRegiao =
            'Já existe uma região com esta UF e cidade. Edite a região existente em vez de criar outra.';
          this.errosCampoRegiao = { uf: this.erroFormularioRegiao, cidade: this.erroFormularioRegiao };
        } else {
          this.erroFormularioRegiao = envelopeRegiao.error?.message ?? 'Não foi possível salvar a região.';
          this.errosCampoRegiao = mapearErrosCampo(envelopeRegiao.error?.fields);
        }
        return;
      }

      const regiaoCriada = envelopeRegiao.data;
      if (telefonesValidos.length === 0) {
        this.salvandoRegiao = false;
        this.regioes = ordenarRegioes([...this.regioes, regiaoCriada]);
        this.modoFormularioRegiao = 'fechado';
        return;
      }

      const chamadas = telefonesValidos.map((t) =>
        this.hotlineService.adicionarTelefoneAdmin(regiaoCriada.id, { telefone: t.telefone.trim() }),
      );

      forkJoin(chamadas).subscribe((envelopes) => {
        this.salvandoRegiao = false;
        const falha = envelopes.find((e) => !e.success);
        if (!falha) {
          const ultimaRegiao = envelopes[envelopes.length - 1]?.data ?? regiaoCriada;
          this.regioes = ordenarRegioes([...this.regioes, ultimaRegiao]);
          this.modoFormularioRegiao = 'fechado';
        } else {
          this.erroFormularioRegiao = falha.error?.message ?? 'Região criada, mas não foi possível salvar os telefones.';
        }
      });
    });
  }

  pedirConfirmacaoExclusaoRegiao(regiao: HotlineRegiao): void {
    this.confirmandoExclusaoRegiaoId = regiao.id;
    this.erroExclusaoRegiao = '';
  }

  cancelarExclusaoRegiao(): void {
    this.confirmandoExclusaoRegiaoId = null;
    this.erroExclusaoRegiao = '';
  }

  confirmarExclusaoRegiao(regiao: HotlineRegiao): void {
    this.excluindoRegiao = true;
    this.erroExclusaoRegiao = '';

    this.hotlineService.excluirRegiaoAdmin(regiao.id).subscribe((envelope) => {
      this.excluindoRegiao = false;
      this.confirmandoExclusaoRegiaoId = null;

      if (envelope.success) {
        this.regioes = this.regioes.filter((r) => r.id !== regiao.id);
        if (this.regiaoEmEdicaoId === regiao.id) {
          this.fecharFormularioTelefones();
        }
      } else {
        this.erroExclusaoRegiao = envelope.error?.message ?? 'Não foi possível remover a região.';
      }
    });
  }

  abrirFormularioTelefones(regiao: HotlineRegiao): void {
    this.regiaoEmEdicaoId = regiao.id;
    this.telefonesFormulario = regiao.telefones.map((t) => ({
      id: proximoIdTelefoneFormulario(),
      idOriginal: t.id,
      telefone: t.telefone,
    }));
    this.erroFormularioTelefones = '';
    this.idsTelefoneInvalido = new Set();
  }

  fecharFormularioTelefones(): void {
    this.regiaoEmEdicaoId = null;
    this.telefonesFormulario = [];
    this.erroFormularioTelefones = '';
    this.idsTelefoneInvalido = new Set();
  }

  adicionarTelefoneFormulario(): void {
    this.telefonesFormulario = [...this.telefonesFormulario, telefoneFormularioVazio()];
  }

  removerTelefoneFormulario(id: string): void {
    this.telefonesFormulario = this.telefonesFormulario.filter((t) => t.id !== id);
  }

  salvarTelefones(regiao: HotlineRegiao): void {
    this.idsTelefoneInvalido = new Set(
      this.telefonesFormulario.filter((t) => !t.telefone.trim()).map((t) => t.id),
    );
    if (this.idsTelefoneInvalido.size > 0) {
      this.erroFormularioTelefones = 'Preencha o telefone, ou remova a linha incompleta.';
      return;
    }

    this.salvandoTelefones = true;
    this.erroFormularioTelefones = '';

    const originais = new Map(regiao.telefones.map((t) => [t.id, t]));
    const idsNoFormulario = new Set(this.telefonesFormulario.map((t) => t.idOriginal).filter((id): id is string => !!id));

    const chamadasAdicao: Observable<ApiEnvelope<HotlineRegiao>>[] = [];
    const chamadasEdicao: { idOriginal: string; numero: string; chamada: Observable<ApiEnvelope<HotlineTelefone>> }[] = [];
    const chamadasExclusao: Observable<ApiEnvelope<unknown>>[] = [];

    for (const telefone of this.telefonesFormulario) {
      const numero = telefone.telefone.trim();

      if (!telefone.idOriginal) {
        chamadasAdicao.push(this.hotlineService.adicionarTelefoneAdmin(regiao.id, { telefone: numero }));
        continue;
      }

      const original = originais.get(telefone.idOriginal);
      if (original && original.telefone !== numero) {
        chamadasEdicao.push({
          idOriginal: telefone.idOriginal,
          numero,
          chamada: this.hotlineService.editarTelefoneAdmin(telefone.idOriginal, { telefone: numero }),
        });
      }
    }

    for (const original of regiao.telefones) {
      if (!idsNoFormulario.has(original.id)) {
        chamadasExclusao.push(this.hotlineService.excluirTelefoneAdmin(original.id));
      }
    }

    const idsExcluidos = new Set(
      regiao.telefones.filter((original) => !idsNoFormulario.has(original.id)).map((original) => original.id),
    );

    const aoFinalizar = (sucesso: boolean, mensagemErro: string | undefined, telefonesAdicionados: HotlineTelefone[]) => {
      this.salvandoTelefones = false;

      if (sucesso) {
        const editados = new Map(chamadasEdicao.map((c) => [c.idOriginal, c.numero]));
        const mantidos = regiao.telefones
          .filter((t) => !idsExcluidos.has(t.id))
          .map((t) => ({ id: t.id, telefone: editados.get(t.id) ?? t.telefone }));
        const regiaoAtualizada: HotlineRegiao = {
          ...regiao,
          telefones: [...mantidos, ...telefonesAdicionados],
        };
        this.regioes = this.regioes.map((r) => (r.id === regiao.id ? regiaoAtualizada : r));
        this.fecharFormularioTelefones();
      } else {
        this.erroFormularioTelefones = mensagemErro ?? 'Não foi possível salvar os telefones.';
      }
    };

    const chamadas = [...chamadasAdicao, ...chamadasEdicao.map((c) => c.chamada), ...chamadasExclusao];

    if (chamadas.length === 0) {
      aoFinalizar(true, undefined, []);
      return;
    }

    forkJoin(chamadas).subscribe((envelopes) => {
      const falha = envelopes.find((e) => !e.success);
      if (falha) {
        aoFinalizar(false, falha.error?.message, []);
        return;
      }
      const regioesRetornadasAdicao = envelopes.slice(0, chamadasAdicao.length) as ApiEnvelope<HotlineRegiao>[];
      const telefonesAdicionados = regioesRetornadasAdicao
        .map((e) => e.data)
        .filter((r): r is HotlineRegiao => !!r)
        .flatMap((r) => r.telefones)
        .filter((t) => !originais.has(t.id));
      aoFinalizar(true, undefined, telefonesAdicionados);
    });
  }

  totalTelefones(regiao: HotlineRegiao): number {
    return regiao.telefones.length;
  }
}
