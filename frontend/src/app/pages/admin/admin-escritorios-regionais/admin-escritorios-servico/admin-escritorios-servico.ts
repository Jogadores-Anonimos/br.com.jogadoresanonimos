import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { mapearErrosCampo } from '../../../../services/api-envelope';
import { EnderecoLookupService } from '../../../../services/endereco-lookup/endereco-lookup';
import { EscritorioRegionalService } from '../../../../services/escritorio-regional/escritorio-regional';
import { EscritorioServico } from '../../../../services/escritorio-regional/escritorio-regional.model';

interface FormularioTelefone {
  id: string;
  label: string;
  telefone: string;
}

interface FormularioEscritorio {
  nome: string;
  endereco: string;
  complemento: string;
  bairro: string;
  cidade: string;
  emailsTexto: string;
  telefones: FormularioTelefone[];
}

function formularioEscritorioVazio(): FormularioEscritorio {
  return { nome: '', endereco: '', complemento: '', bairro: '', cidade: '', emailsTexto: '', telefones: [] };
}

let contadorTelefoneFormulario = 0;
function proximoIdTelefoneFormulario(): string {
  contadorTelefoneFormulario += 1;
  return `form-escritorio-telefone-${contadorTelefoneFormulario}`;
}

@Component({
  selector: 'app-admin-escritorios-servico',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-escritorios-servico.html',
  styleUrl: './admin-escritorios-servico.css',
})
export class AdminEscritoriosServico {
  private readonly service = inject(EscritorioRegionalService);
  private readonly enderecoLookupService = inject(EnderecoLookupService);

  @Input({ required: true }) escritorios: EscritorioServico[] = [];
  @Output() readonly escritorioSalvo = new EventEmitter<EscritorioServico>();

  salvandoEscritorio = false;
  erroFormularioEscritorio = '';
  errosCampoEscritorio: Record<string, string> = {};
  modoFormularioEscritorio: 'fechado' | 'novo' | 'edicao' = 'fechado';
  escritorioEmEdicao: EscritorioServico | null = null;
  formularioEscritorio: FormularioEscritorio = formularioEscritorioVazio();

  cepEscritorio = '';
  numeroEscritorio = '';
  buscandoCepEscritorio = false;
  erroCepEscritorio = '';

  get escritoriosOrdenados(): EscritorioServico[] {
    return [...this.escritorios].sort((a, b) => a.ordem - b.ordem);
  }

  abrirEdicaoEscritorio(escritorio: EscritorioServico): void {
    this.escritorioEmEdicao = escritorio;
    this.formularioEscritorio = {
      nome: escritorio.nome,
      endereco: escritorio.endereco,
      complemento: escritorio.complemento ?? '',
      bairro: escritorio.bairro ?? '',
      cidade: escritorio.cidade,
      emailsTexto: escritorio.emails.join(', '),
      telefones: escritorio.telefones.map((t) => ({ id: proximoIdTelefoneFormulario(), label: t.label ?? '', telefone: t.telefone })),
    };
    this.cepEscritorio = '';
    this.numeroEscritorio = '';
    this.erroCepEscritorio = '';
    this.erroFormularioEscritorio = '';
    this.errosCampoEscritorio = {};
    this.modoFormularioEscritorio = 'edicao';
  }

  cancelarFormularioEscritorio(): void {
    this.modoFormularioEscritorio = 'fechado';
    this.escritorioEmEdicao = null;
    this.erroFormularioEscritorio = '';
    this.errosCampoEscritorio = {};
  }

  adicionarTelefoneEscritorio(): void {
    this.formularioEscritorio.telefones = [
      ...this.formularioEscritorio.telefones,
      { id: proximoIdTelefoneFormulario(), label: '', telefone: '' },
    ];
  }

  removerTelefoneEscritorio(id: string): void {
    this.formularioEscritorio.telefones = this.formularioEscritorio.telefones.filter((t) => t.id !== id);
  }

  buscarCepEscritorio(): void {
    const cepLimpo = this.cepEscritorio.replace(/\D/g, '');
    if (cepLimpo.length !== 8) {
      this.erroCepEscritorio = 'CEP inválido — precisa ter 8 dígitos.';
      return;
    }

    this.buscandoCepEscritorio = true;
    this.erroCepEscritorio = '';

    this.enderecoLookupService
      .buscarCep(cepLimpo)
      .subscribe((endereco) => {
        this.buscandoCepEscritorio = false;

        if (!endereco || endereco.erro) {
          this.erroCepEscritorio = 'CEP não encontrado.';
          return;
        }

        const logradouroComNumero = [endereco.logradouro, this.numeroEscritorio.trim()].filter(Boolean).join(', ');
        this.formularioEscritorio.endereco = logradouroComNumero || this.formularioEscritorio.endereco;
        this.formularioEscritorio.bairro = endereco.bairro ?? this.formularioEscritorio.bairro;
        this.formularioEscritorio.cidade =
          [endereco.localidade, endereco.uf].filter(Boolean).join(' - ') || this.formularioEscritorio.cidade;
      });
  }

  salvarEscritorio(): void {
    this.errosCampoEscritorio = {};

    if (!this.formularioEscritorio.nome.trim()) {
      this.errosCampoEscritorio['nome'] = 'Nome é obrigatório.';
    }
    if (!this.formularioEscritorio.endereco.trim()) {
      this.errosCampoEscritorio['endereco'] = 'Endereço é obrigatório.';
    }
    if (!this.formularioEscritorio.cidade.trim()) {
      this.errosCampoEscritorio['cidade'] = 'Cidade é obrigatória.';
    }
    if (Object.keys(this.errosCampoEscritorio).length > 0) {
      this.erroFormularioEscritorio = 'Preencha todos os campos obrigatórios.';
      return;
    }

    const telefonesInvalidos = this.formularioEscritorio.telefones.some((t) => !t.telefone.trim());
    if (telefonesInvalidos) {
      this.erroFormularioEscritorio = 'Remova ou preencha os telefones incompletos.';
      this.errosCampoEscritorio['telefones'] = this.erroFormularioEscritorio;
      return;
    }

    this.salvandoEscritorio = true;
    this.erroFormularioEscritorio = '';

    const emails = this.formularioEscritorio.emailsTexto
      .split(',')
      .map((email) => email.trim())
      .filter((email) => email.length > 0);

    const request = {
      nome: this.formularioEscritorio.nome.trim(),
      endereco: this.formularioEscritorio.endereco.trim(),
      complemento: this.formularioEscritorio.complemento.trim() || undefined,
      bairro: this.formularioEscritorio.bairro.trim() || undefined,
      cidade: this.formularioEscritorio.cidade.trim(),
      emails,
      telefones: this.formularioEscritorio.telefones.map((t) => ({
        label: t.label.trim() || undefined,
        telefone: t.telefone.trim(),
      })),
    };

    if (!this.escritorioEmEdicao) {
      this.salvandoEscritorio = false;
      return;
    }

    this.service.editarEscritorioServicoAdmin(this.escritorioEmEdicao.id, request).subscribe((envelope) => {
      this.salvandoEscritorio = false;

      if (envelope.success && envelope.data) {
        this.escritorioSalvo.emit(envelope.data);
        this.modoFormularioEscritorio = 'fechado';
        this.escritorioEmEdicao = null;
      } else {
        this.erroFormularioEscritorio = envelope.error?.message ?? 'Não foi possível salvar o escritório.';
        this.errosCampoEscritorio = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }
}
