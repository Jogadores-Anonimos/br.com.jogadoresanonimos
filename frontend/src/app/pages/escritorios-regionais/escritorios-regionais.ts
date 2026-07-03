import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface EnderecoServico {
  nome: string;
  endereco: string;
}

@Component({
  selector: 'app-escritorios-regionais',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './escritorios-regionais.html',
  styleUrl: './escritorios-regionais.css',
})
export class EscritoriosRegionais {
  readonly escritorios: EnderecoServico[] = [
    { nome: 'ERSJAB — Escritório Regional de Serviço da JA Brasil', endereco: '[endereço a confirmar]' },
    { nome: 'JAERJ — Jogadores Anônimos do Estado do Rio de Janeiro', endereco: '[endereço a confirmar]' },
  ];

  readonly intergrupos: EnderecoServico[] = [
    { nome: 'Intergrupo São Paulo', endereco: '[endereço a confirmar]' },
    { nome: 'Intergrupo Rio de Janeiro', endereco: '[endereço a confirmar]' },
    { nome: 'Intergrupo Minas Gerais', endereco: '[endereço a confirmar]' },
    { nome: 'Intergrupo Bahia', endereco: '[endereço a confirmar]' },
    { nome: 'Intergrupo Rio Grande do Sul', endereco: '[endereço a confirmar]' },
    { nome: 'Intergrupo Paraná', endereco: '[endereço a confirmar]' },
  ];
}
