import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';

import { ApiEnvelope } from '../../../services/api-envelope';
import { HotlineService } from '../../../services/hotline';
import { HotlineRegiao } from '../../../services/hotline.model';

interface FormularioTelefone {
  id: string;
  idOriginal: string | null;
  label: string;
  telefone: string;
}

let contadorTelefoneFormulario = 0;
function proximoIdTelefoneFormulario(): string {
  contadorTelefoneFormulario += 1;
  return `form-telefone-${contadorTelefoneFormulario}`;
}

function telefoneFormularioVazio(): FormularioTelefone {
  return { id: proximoIdTelefoneFormulario(), idOriginal: null, label: '', telefone: '' };
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

  carregando = true;
  erroCarregamento = '';
  regioes: HotlineRegiao[] = [];

  novaRegiaoAberta = false;
  novaRegiaoNome = '';
  criandoRegiao = false;
  erroNovaRegiao = '';

  confirmandoExclusaoRegiaoId: string | null = null;
  excluindoRegiao = false;
  erroExclusaoRegiao = '';

  regiaoEmEdicaoId: string | null = null;
  telefonesFormulario: FormularioTelefone[] = [];
  salvandoTelefones = false;
  erroFormularioTelefones = '';

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
    this.novaRegiaoAberta = true;
    this.novaRegiaoNome = '';
    this.erroNovaRegiao = '';
  }

  cancelarNovaRegiao(): void {
    this.novaRegiaoAberta = false;
    this.novaRegiaoNome = '';
    this.erroNovaRegiao = '';
  }

  criarRegiao(): void {
    if (!this.novaRegiaoNome.trim()) {
      this.erroNovaRegiao = 'Nome da região é obrigatório.';
      return;
    }

    this.criandoRegiao = true;
    this.erroNovaRegiao = '';

    this.hotlineService.criarRegiaoAdmin({ nome: this.novaRegiaoNome.trim() }).subscribe((envelope) => {
      this.criandoRegiao = false;

      if (envelope.success) {
        this.novaRegiaoAberta = false;
        this.novaRegiaoNome = '';
        this.carregarRegioes();
      } else {
        this.erroNovaRegiao = envelope.error?.message ?? 'Não foi possível criar a região.';
      }
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

  // --- lista dinâmica de telefones dentro do formulário da região ---

  abrirFormularioTelefones(regiao: HotlineRegiao): void {
    this.regiaoEmEdicaoId = regiao.id;
    this.telefonesFormulario = regiao.telefones.map((t) => ({
      id: proximoIdTelefoneFormulario(),
      idOriginal: t.id,
      label: t.label,
      telefone: t.telefone,
    }));
    this.erroFormularioTelefones = '';
  }

  fecharFormularioTelefones(): void {
    this.regiaoEmEdicaoId = null;
    this.telefonesFormulario = [];
    this.erroFormularioTelefones = '';
  }

  adicionarTelefoneFormulario(): void {
    this.telefonesFormulario = [...this.telefonesFormulario, telefoneFormularioVazio()];
  }

  removerTelefoneFormulario(id: string): void {
    this.telefonesFormulario = this.telefonesFormulario.filter((t) => t.id !== id);
  }

  salvarTelefones(regiao: HotlineRegiao): void {
    const algumIncompleto = this.telefonesFormulario.some((t) => !t.label.trim() || !t.telefone.trim());
    if (algumIncompleto) {
      this.erroFormularioTelefones = 'Preencha descrição e telefone, ou remova a linha incompleta.';
      return;
    }

    this.salvandoTelefones = true;
    this.erroFormularioTelefones = '';

    // O contrato de API atual (hotline.model.ts) só expõe CRUD unitário de
    // telefone, sem endpoint para salvar a lista inteira da região de uma
    // vez (diferente de escritorios-regionais, onde o grupo salva a lista
    // completa em uma chamada). Até o backend/frontend-services expor um
    // endpoint equivalente, aqui fazemos o diff entre o estado original e
    // o formulário e disparamos as chamadas unitárias correspondentes.
    const originais = new Map(regiao.telefones.map((t) => [t.id, t]));
    const idsNoFormulario = new Set(this.telefonesFormulario.map((t) => t.idOriginal).filter((id): id is string => !!id));

    const chamadas: Observable<ApiEnvelope<unknown>>[] = [];

    for (const telefone of this.telefonesFormulario) {
      const label = telefone.label.trim();
      const numero = telefone.telefone.trim();

      if (!telefone.idOriginal) {
        chamadas.push(this.hotlineService.adicionarTelefoneAdmin(regiao.id, { label, telefone: numero }));
        continue;
      }

      const original = originais.get(telefone.idOriginal);
      if (original && (original.label !== label || original.telefone !== numero)) {
        chamadas.push(this.hotlineService.editarTelefoneAdmin(telefone.idOriginal, { label, telefone: numero }));
      }
    }

    for (const original of regiao.telefones) {
      if (!idsNoFormulario.has(original.id)) {
        chamadas.push(this.hotlineService.excluirTelefoneAdmin(original.id));
      }
    }

    const aoFinalizar = (sucesso: boolean, mensagemErro?: string) => {
      this.salvandoTelefones = false;

      if (sucesso) {
        this.fecharFormularioTelefones();
        this.carregarRegioes();
      } else {
        this.erroFormularioTelefones = mensagemErro ?? 'Não foi possível salvar os telefones.';
      }
    };

    if (chamadas.length === 0) {
      aoFinalizar(true);
      return;
    }

    forkJoin(chamadas)
      .pipe(
        map((envelopes) => {
          const falha = envelopes.find((e) => !e.success);
          return { sucesso: !falha, mensagemErro: falha?.error?.message };
        }),
      )
      .subscribe(({ sucesso, mensagemErro }) => aoFinalizar(sucesso, mensagemErro));
  }

  totalTelefones(regiao: HotlineRegiao): number {
    return regiao.telefones.length;
  }
}
