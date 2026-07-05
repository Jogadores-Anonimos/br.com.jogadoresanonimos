import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

interface EscritorioServico {
  nome: string;
  endereco: string;
  complemento?: string;
  cidade: string;
  emails?: string[];
}

interface GrupoIntergrupo {
  nome: string;
  endereco: string;
  bairro?: string;
  cidade: string;
  telefone?: string;
  horarios: string;
}

interface EstadoIntergrupos {
  estado: string;
  grupos: GrupoIntergrupo[];
}

interface RegiaoIntergrupos {
  regiao: string;
  estados: EstadoIntergrupos[];
}

@Component({
  selector: 'app-escritorios-regionais',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './escritorios-regionais.html',
  styleUrl: './escritorios-regionais.css',
})
export class EscritoriosRegionais {
  readonly escritorios: EscritorioServico[] = [
    {
      nome: 'ERSJAB — Escritório Regional de Serviços de Jogadores Anônimos do Brasil',
      endereco: 'Rua Paulino Guimarães nº 93, Sala 12, Luz (Centro)',
      complemento: 'CEP 01109-020',
      cidade: 'São Paulo - SP',
      emails: ['jogadoresanonimossaopaulo@gmail.com', 'jogadoresanonimos@yahoo.com.br'],
    },
    {
      nome: 'JAERJ — Jogadores Anônimos do Estado do Rio de Janeiro',
      endereco: '[endereço a confirmar]',
      cidade: '[a confirmar]',
    },
  ];

  readonly regioes: RegiaoIntergrupos[] = [
    {
      regiao: 'Região Sudeste',
      estados: [
        {
          estado: 'São Paulo',
          grupos: [
            {
              nome: 'Grupo Santa Ifigênia',
              endereco: 'R. Santa Ifigênia nº 30',
              bairro: 'Santa Ifigênia',
              cidade: 'São Paulo - SP',
              horarios: 'Dom 9h-11h',
            },
            {
              nome: 'Grupo Pioneiro',
              endereco: 'Rua Guaporé nº 335',
              bairro: 'Luz',
              cidade: 'São Paulo - SP',
              horarios: 'Seg 20h / Qui 20h',
            },
            {
              nome: 'Grupo Itaim',
              endereco: 'Rua Clodomiro Amazonas nº 50',
              bairro: 'Itaim Bibi',
              cidade: 'São Paulo - SP',
              horarios: 'Ter 20h / Qui 20h',
            },
            {
              nome: 'Grupo Mauá',
              endereco: 'Praça Monsenhor Alexandre Venâncio Arminas nº 01',
              bairro: 'Matriz',
              cidade: 'Mauá - SP',
              horarios: 'Ter 19h30',
            },
            {
              nome: 'Grupo São Bernardo do Campo',
              endereco: 'Estrada Dos Casa 3230',
              bairro: 'Dos Casa',
              cidade: 'São Bernardo do Campo - SP',
              horarios: 'Qui 19h30',
            },
            {
              nome: 'Grupo Renascer - Campinas',
              endereco: 'Rua Paulo Lacerda 309',
              bairro: 'São Bernardo',
              cidade: 'Campinas - SP',
              horarios: 'Dom 10h',
            },
            {
              nome: 'Grupo Jabaquara',
              endereco: 'Av. Jabaquara nº 2682, Igreja São Judas Tadeu',
              bairro: 'Jabaquara',
              cidade: 'São Paulo - SP',
              horarios: 'Sex 19h',
            },
            {
              nome: 'Grupo Buscando a Serenidade',
              endereco: 'Rua Pedreira de Magalhães nº 100',
              bairro: 'Artur Alvim',
              cidade: 'São Paulo - SP',
              horarios: 'Qua 20h / Sex 20h',
            },
            {
              nome: 'Grupo Centro São José dos Campos',
              endereco: 'Av. São José 646',
              bairro: 'Jardim Bela Vista',
              cidade: 'São José dos Campos - SP',
              horarios: 'Ter 19h',
            },
            {
              nome: 'Grupo Santos',
              endereco: 'Av. Dr. Pedro Lessa nº 2262',
              bairro: 'Ponta da Praia',
              cidade: 'Santos - SP',
              horarios: 'Sáb 10h',
            },
            {
              nome: 'Grupo Taubaté',
              endereco: 'Rua Maria Tereza de Moura 150, Lar Bom Samaritano',
              cidade: 'Taubaté - SP',
              horarios: 'Qua 19h-21h',
            },
            {
              nome: 'Grupo Nova Chance',
              endereco: 'Av. João Dias nº 822, 3º andar da Biblioteca',
              bairro: 'Santo Amaro',
              cidade: 'São Paulo - SP',
              horarios: 'Sáb 16h',
            },
            {
              nome: 'Grupo Semeando Recomeços',
              endereco: 'Rua Bertioga 191',
              bairro: 'Jardim Satélite',
              cidade: 'São José dos Campos - SP',
              telefone: '12-99738-0690',
              horarios: 'Sáb 16h',
            },
            {
              nome: 'Grupo Pindamonhangaba',
              endereco: 'Rua Barão Homem de Melo s/n, Embaixo do Viaduto',
              cidade: 'Pindamonhangaba - SP',
              horarios: 'Qua 19h-21h',
            },
            {
              nome: 'Grupo Limeira',
              endereco:
                'Rua Oscar Buzolin 1-63, Paróquia N.Sra. do Rosário de Fátima',
              bairro: 'Jardim Planalto',
              cidade: 'Limeira - SP',
              horarios: 'Seg 19h30-21h30',
            },
            {
              nome: 'Grupo Atibaia',
              endereco: 'Av. Jerônimo de Camargo 3111 esq. Rua São José 197',
              bairro: 'Vila Salles',
              cidade: 'Atibaia - SP',
              horarios: 'Qui 19h-21h',
            },
            {
              nome: 'Grupo São Miguel Coragem & Sabedoria',
              endereco: 'Av. Imperador 5831',
              bairro: 'Vila Jacuí / São Miguel',
              cidade: 'São Paulo - SP',
              horarios: 'Ter 20h-22h',
            },
            {
              nome: 'Grupo Passos de Luz',
              endereco: 'Rua N.Sra. das Dores s/nº, Igreja Matriz 2º andar',
              bairro: 'Vila Queiroz',
              cidade: 'Artur Nogueira - SP',
              horarios: 'Qui 20h',
            },
            {
              nome: 'Grupo São José do Rio Preto',
              endereco: 'Rua José Martins Romero 590, Ass. Joanna de Angelis',
              cidade: 'São José do Rio Preto - SP',
              horarios: 'Sex 19h',
            },
            {
              nome: 'Grupo Bertioga',
              endereco: 'Av. Anchieta 1450 Sala 08',
              bairro: 'Centro',
              cidade: 'Bertioga - SP',
              horarios: 'Qui 19h-21h',
            },
          ],
        },
        {
          estado: 'Rio de Janeiro',
          grupos: [
            {
              nome: 'Grupo Acre',
              endereco: 'Rua do Acre nº 47 Sala 412',
              bairro: 'Centro',
              cidade: 'Rio de Janeiro - RJ',
              horarios: 'Seg/Qua 12h30 e 18h · Ter/Qui/Sex 18h · Sáb 10h-12h',
            },
            {
              nome: 'Grupo Niterói',
              endereco: 'Av. Amaral Peixoto nº 171 Sala 804',
              bairro: 'Centro',
              cidade: 'Niterói - RJ',
              horarios: 'Seg 19h / Qui 19h / Sáb 9h30-11h30',
            },
            {
              nome: 'Grupo Nilópolis',
              endereco: 'Rua Comendador Rodrigues Alves nº 1544',
              bairro: 'Centro',
              cidade: 'Nilópolis - RJ',
              horarios: 'Seg 19h / Qui 19h',
            },
            {
              nome: 'Grupo Flamengo',
              endereco: 'Rua Senador Vergueiro nº 141, prédio anexo 2º andar sala 5',
              bairro: 'Flamengo',
              cidade: 'Rio de Janeiro - RJ',
              horarios: 'Sex 18h30 / Dom 18h30',
            },
            {
              nome: 'Grupo Neves-RJ',
              endereco: 'Rua Oliveira Botelho s/nº, Igreja N.Sra. das Neves',
              cidade: 'São Gonçalo - RJ',
              horarios: 'Qua 18h30-20h30',
            },
            {
              nome: 'Grupo Petrópolis',
              endereco: 'R. Visc. de Souza Franco 474, Associação Casa da Cidadania',
              bairro: 'Centro',
              cidade: 'Petrópolis - RJ',
              horarios: 'Ter 19h',
            },
            {
              nome: 'Grupo Barra',
              endereco: 'Praça Embaixador Gualberto de Oliveira s/n, Paróquia São Marcos',
              bairro: 'Barra',
              cidade: 'Rio de Janeiro - RJ',
              horarios: 'Seg 18h30-20h30 / Qui 19h-21h',
            },
            {
              nome: 'Grupo Campo Grande',
              endereco: "Estrada do Mendanha nº 208, Igreja de Sant'ana",
              bairro: 'Campo Grande',
              cidade: 'Rio de Janeiro - RJ',
              horarios: 'Qua 19h',
            },
            {
              nome: 'Grupo Pilares',
              endereco: 'Av. Dom Helder Câmara nº 6695, Paróquia São Benedito',
              bairro: 'Pilares',
              cidade: 'Rio de Janeiro - RJ',
              horarios: 'Ter 18h / Sex 18h',
            },
            {
              nome: 'Grupo Copacabana',
              endereco: 'Rua Francisco Otaviano nº 99 2ºA, Igreja da Ressurreição',
              bairro: 'Copacabana',
              cidade: 'Rio de Janeiro - RJ',
              horarios: 'Qui 18h-20h',
            },
            {
              nome: 'Grupo Arpoador',
              endereco: 'Rua Francisco Otaviano nº 99 2ºA, Igreja da Ressurreição',
              bairro: 'Copacabana',
              cidade: 'Rio de Janeiro - RJ',
              horarios: 'Qua 18h-20h',
            },
            {
              nome: 'Grupo São Cristóvão',
              endereco: 'Rua Fonseca Teles 109 Sala 05, Paróquia Santa Edwiges',
              bairro: 'São Cristóvão',
              cidade: 'Rio de Janeiro - RJ',
              telefone: '21-97751-7734',
              horarios: 'Qui 18h-20h / Sáb 10h-12h',
            },
            {
              nome: 'Grupo Nova Iguaçu',
              endereco: 'Rua Marechal Floriano Peixoto 2264, Catedral Santo Antônio',
              bairro: 'Centro',
              cidade: 'Nova Iguaçu - RJ',
              telefone: '21-99472-1933',
              horarios: 'Sex 19h',
            },
            {
              nome: 'Grupo Tijuca',
              endereco: 'Rua Barão de Mesquita 275 Sala 205, Igreja Santo Afonso',
              bairro: 'Tijuca',
              cidade: 'Rio de Janeiro - RJ',
              horarios: 'Ter 19h-21h / Qui 19h-21h',
            },
          ],
        },
        {
          estado: 'Minas Gerais',
          grupos: [
            {
              nome: 'Grupo Vida Nova',
              endereco: 'Av. do Contorno nº 4777, Sede do Abraço',
              bairro: 'Funcionários',
              cidade: 'Belo Horizonte - MG',
              telefone: '31-99206-2501',
              horarios: 'Ter 19h15 / Qui 19h15',
            },
            {
              nome: 'Grupo São Lourenço',
              endereco: 'Rua Coronel José Justino 50',
              cidade: 'São Lourenço - MG',
              horarios: 'Ter 18h',
            },
            {
              nome: 'Grupo Juiz de Fora',
              endereco: 'Rua São Mateus 629 Sala 306, Igreja São Matheus',
              bairro: 'São Matheus',
              cidade: 'Juiz de Fora - MG',
              telefone: '32-98877-7088',
              horarios: 'Seg 19h-20h30',
            },
            {
              nome: 'Grupo Varginha',
              endereco: 'Praça Quintino Bocaiúva 58 Sala 301, Prédio Mercado Municipal',
              bairro: 'Centro',
              cidade: 'Varginha - MG',
              telefone: '35-99989-9997',
              horarios: 'Sáb 19h',
            },
            {
              nome: 'Grupo Uberlândia',
              endereco: 'Rua Geraldo Garcia de Almeida 160, Igreja Evangélica Luterana S. Jorge',
              cidade: 'Uberlândia - MG',
              telefone: '21-99472-1933',
              horarios: 'Ter 19h30',
            },
          ],
        },
        {
          estado: 'Espírito Santo',
          grupos: [
            {
              nome: 'Grupo Vitória-ES',
              endereco: 'Praça São José Operário nº 221',
              bairro: 'Maruípe',
              cidade: 'Vitória - ES',
              horarios: 'Seg 19h',
            },
          ],
        },
      ],
    },
    {
      regiao: 'Região Sul',
      estados: [
        {
          estado: 'Rio Grande do Sul',
          grupos: [
            {
              nome: 'Grupo Esperança',
              endereco: 'Av. Farrapos nº 2611, Igreja de São Geraldo',
              bairro: 'Floresta',
              cidade: 'Porto Alegre - RS',
              telefone: '51-98030-2002',
              horarios: 'Ter e Qua 19h',
            },
            {
              nome: 'Grupo Novo Hamburgo',
              endereco: 'Rua Dr. Daniel Hillebrand 1349, Igreja Evangélica Luterana',
              bairro: 'Rondônia',
              cidade: 'Novo Hamburgo - RS',
              telefone: '51-98030-2002',
              horarios: 'Dom 17h-19h / Qui 19h30-21h30',
            },
          ],
        },
        {
          estado: 'Santa Catarina',
          grupos: [
            {
              nome: 'Grupo Vida Nova Florianópolis',
              endereco: 'Rua Vitor Konder nº 344',
              bairro: 'Centro',
              cidade: 'Florianópolis - SC',
              horarios: 'Ter e Qui 19h / Sáb 16h',
            },
          ],
        },
        {
          estado: 'Paraná',
          grupos: [
            {
              nome: 'Grupo Vitória - Curitiba',
              endereco: 'Av. Theodoro Makiolka nº 64',
              bairro: 'Santa Cândida',
              cidade: 'Curitiba - PR',
              telefone: '41-99684-8004',
              horarios: 'Qui 19h30',
            },
            {
              nome: 'Grupo Renovação - Curitiba',
              endereco: 'Av. N.Sra. Aparecida 1637',
              bairro: 'Seminário',
              cidade: 'Curitiba - PR',
              telefone: '41-99893-6054',
              horarios: 'Ter 19h30',
            },
            {
              nome: 'Grupo Londrina',
              endereco: 'Rua Mato Grosso 1167',
              bairro: 'Centro',
              cidade: 'Londrina - PR',
              telefone: '21-99472-1933',
              horarios: 'Ter 19h-21h',
            },
            {
              nome: 'Grupo Maringá',
              endereco: 'Pça. Mons. Bernard 7',
              cidade: 'Maringá - PR',
              horarios: 'Qua 20h',
            },
          ],
        },
      ],
    },
    {
      regiao: 'Região Centro-Oeste',
      estados: [
        {
          estado: 'Mato Grosso do Sul',
          grupos: [
            {
              nome: 'Grupo Nova Consciência',
              endereco: 'Rua Maracaju nº 249 Sala 01',
              bairro: 'Centro',
              cidade: 'Campo Grande - MS',
              telefone: '67-98473-1321',
              horarios: 'Ter e Qui 19h',
            },
          ],
        },
        {
          estado: 'Mato Grosso',
          grupos: [
            {
              nome: 'Grupo Cuiabá',
              endereco: 'Av. Desembargador Antônio Quirino de Araújo 1126',
              bairro: 'Poções',
              cidade: 'Cuiabá - MT',
              telefone: '21-99472-1933',
              horarios: 'Qua 19h30',
            },
          ],
        },
        {
          estado: 'Distrito Federal',
          grupos: [
            {
              nome: 'Grupo Brasília',
              endereco: 'Sgan 609 Módulo C Sala 10, Paróquia Verbo Divino',
              bairro: 'Asa Norte',
              cidade: 'Brasília - DF',
              horarios: 'Qui 19h',
            },
          ],
        },
        {
          estado: 'Tocantins',
          grupos: [
            {
              nome: 'Grupo Palmas',
              endereco: 'Sem endereço fixo confirmado',
              cidade: 'Palmas - TO',
              telefone: '21-99472-1933 (WhatsApp da Linha de Ajuda)',
              horarios: '[a confirmar]',
            },
          ],
        },
        {
          estado: 'Goiás',
          grupos: [
            {
              nome: 'Grupo Central',
              endereco: 'Rua DF-2, Capela N.Sra. da Guia',
              bairro: 'Setor Chácara do Governador',
              cidade: 'Goiânia - GO',
              horarios: 'Seg 19h-21h',
            },
          ],
        },
      ],
    },
    {
      regiao: 'Região Nordeste',
      estados: [
        {
          estado: 'Paraíba',
          grupos: [
            {
              nome: 'Grupo Paraíba',
              endereco: 'Rua Almeida Barreto nº 222 Sala 23',
              bairro: 'Centro',
              cidade: 'João Pessoa - PB',
              telefone: '83-98872-6572',
              horarios: 'Seg e Qua 19h30',
            },
          ],
        },
        {
          estado: 'Bahia',
          grupos: [
            {
              nome: 'Grupo Salvador',
              endereco: 'Alameda Verona nº 158',
              bairro: 'Pituba',
              cidade: 'Salvador - BA',
              telefone: '71-98624-0512 / 71-99275-7068',
              horarios: 'Ter e Qui 19h',
            },
          ],
        },
        {
          estado: 'Pernambuco',
          grupos: [
            {
              nome: 'Grupo Boa Vista',
              endereco: 'Edifício Tereza Cristina 3º andar Sala 305',
              bairro: 'Boa Vista',
              cidade: 'Recife - PE',
              telefone: '81-99183-8297',
              horarios: 'Dom 10h',
            },
            {
              nome: 'Grupo IPSEP',
              endereco: 'Rua Jean Emile Travé, Escola Eleanor Roosevelt',
              cidade: 'Recife - PE',
              telefone: '81-98693-3867',
              horarios: 'Ter 19h-21h',
            },
            {
              nome: 'Grupo Petrolina',
              endereco: 'Rua São José 121',
              bairro: 'Centro',
              cidade: 'Petrolina - PE',
              telefone: '74-99122-2660',
              horarios: 'Sex 19h30',
            },
            {
              nome: 'Grupo Pina',
              endereco: 'Av. Herculano Bandeira',
              bairro: 'Pina',
              cidade: 'Recife - PE',
              telefone: '81-9315-9357',
              horarios: 'Qui 19h',
            },
            {
              nome: 'Grupo Caruaru',
              endereco: 'Av. Prof. José Leão 567',
              bairro: 'Maurício de Nassau',
              cidade: 'Caruaru - PE',
              telefone: '81-99921-0731',
              horarios: 'Ter 18h30',
            },
          ],
        },
        {
          estado: 'Ceará',
          grupos: [
            {
              nome: 'Grupo Fortaleza',
              endereco: 'Rua São Paulo 32 Sala 316',
              bairro: 'Centro',
              cidade: 'Fortaleza - CE',
              telefone: '85-98929-5529',
              horarios: 'Seg e Qua 19h30-21h30',
            },
            {
              nome: 'Grupo Iracema',
              endereco: 'Av. Almirante Barroso 949',
              bairro: 'Praia de Iracema',
              cidade: 'Fortaleza - CE',
              telefone: '85-98929-5529',
              horarios: 'Qui 19h30-21h30 / Sáb 10h-12h',
            },
          ],
        },
        {
          estado: 'Alagoas',
          grupos: [
            {
              nome: 'Grupo Maceió',
              endereco: 'Av. Fernandes Lima 869',
              bairro: 'Farol',
              cidade: 'Maceió - AL',
              telefone: '82-99620-2998',
              horarios: 'Qua 19h-21h / Sáb 16h-18h',
            },
          ],
        },
        {
          estado: 'Sergipe',
          grupos: [
            {
              nome: 'Grupo Aracaju',
              endereco: 'Rua Renato Fonseca de Oliveira 585',
              bairro: 'Coroa do Meio',
              cidade: 'Aracaju - SE',
              telefone: '11-99571-6942',
              horarios: 'Qui 19h',
            },
            {
              nome: 'Grupo Terezina',
              endereco: 'Rod. 316 nº 7.200',
              bairro: 'Parque Piauí',
              cidade: '[cidade não especificada na fonte]',
              telefone: '21-99472-1933',
              horarios: 'Ter 19h',
            },
          ],
        },
        {
          estado: 'Rio Grande do Norte',
          grupos: [
            {
              nome: 'Grupo Natal',
              endereco: 'Rua Domingos Amado 3393-D',
              bairro: 'Candelária',
              cidade: 'Natal - RN',
              telefone: '84-998348964',
              horarios: 'Seg 19h',
            },
            {
              nome: 'Grupo Mossoró',
              endereco: 'Rua Sebastião Saraiva 42',
              bairro: 'Aeroporto',
              cidade: 'Mossoró - RN',
              horarios: 'Ter 19h',
            },
          ],
        },
        {
          estado: 'Maranhão',
          grupos: [
            {
              nome: 'Grupo São Luís',
              endereco: 'Praça Gonçalves Dias 696',
              bairro: 'Centro',
              cidade: 'São Luís - MA',
              telefone: '21-99472-1933',
              horarios: 'Ter 19h-21h',
            },
            {
              nome: 'Grupo SLZ-Bequimão',
              endereco: 'Rua 32 nº 1',
              bairro: 'Bequimão',
              cidade: 'São Luís - MA',
              telefone: '21-99472-1933',
              horarios: 'Qui 19h-21h',
            },
          ],
        },
      ],
    },
    {
      regiao: 'Região Norte',
      estados: [
        {
          estado: 'Pará',
          grupos: [
            {
              nome: 'Grupo Belém do Pará',
              endereco: 'Av. Ceará 595',
              bairro: 'Canudos',
              cidade: 'Belém - PA',
              telefone: '91-99603-0369',
              horarios: 'Dom 9h-11h',
            },
          ],
        },
        {
          estado: 'Amazonas',
          grupos: [
            {
              nome: 'Grupo Manaus',
              endereco: 'Rua Libertador 55, Paróquia N.Sra. das Graças',
              bairro: 'N.Sra. das Graças',
              cidade: 'Manaus - AM',
              horarios: 'Sex 19h-21h',
            },
          ],
        },
      ],
    },
  ];

  regiaoSelecionada: string | null = null;

  estadoSelecionado: string | null = null;

  get regiaoAtual(): RegiaoIntergrupos | undefined {
    return this.regioes.find((r) => r.regiao === this.regiaoSelecionada);
  }

  get estadoAtual(): EstadoIntergrupos | undefined {
    return this.regiaoAtual?.estados.find((e) => e.estado === this.estadoSelecionado);
  }

  selecionarRegiao(regiao: RegiaoIntergrupos): void {
    this.regiaoSelecionada = regiao.regiao;
    this.estadoSelecionado = regiao.estados[0]?.estado ?? null;
  }

  selecionarEstado(estado: string): void {
    this.estadoSelecionado = estado;
  }
}
