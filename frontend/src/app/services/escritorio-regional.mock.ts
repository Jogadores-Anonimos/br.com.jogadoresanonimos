// TODO: substituir por chamada real quando o endpoint estiver disponível
// Mocks centralizados do domínio Escritório Regional / Intergrupos — ver
// contrato em backend/docs/api/contrato-escritorios-regionais.md (Revisão 2026-07-13)
// Conteúdo replicado do array antes hardcoded em
// pages/escritorios-regionais/escritorios-regionais.ts.
import {
  DiaSemana,
  EscritoriosRegionais,
  EstadoIntergrupo,
  GrupoIntergrupo,
  GrupoIntergrupoTelefone,
  RegiaoIntergrupo,
  UNIDADES_FEDERATIVAS,
} from './escritorio-regional.model';

const AGORA = '2026-07-12T14:30:00.000Z';

let contadorId = 0;
function proximoId(prefixo: string): string {
  contadorId += 1;
  return `mock-${prefixo}-${contadorId}`;
}

function nomeUf(uf: string): string {
  return UNIDADES_FEDERATIVAS.find((u) => u.sigla === uf)?.nome ?? uf;
}

interface TelefoneSeed {
  label?: string;
  telefone: string;
}

function telefone(seed: TelefoneSeed): GrupoIntergrupoTelefone {
  return { id: proximoId('telefone'), label: seed.label ?? null, telefone: seed.telefone };
}

interface GrupoSeed {
  nome: string;
  endereco: string;
  bairro?: string;
  cidade: string;
  telefones?: TelefoneSeed[];
  diasSemana: DiaSemana[];
  horario: string;
}

function grupo(estadoId: string, ordem: number, seed: GrupoSeed): GrupoIntergrupo {
  return {
    id: proximoId('grupo'),
    estadoId,
    nome: seed.nome,
    endereco: seed.endereco,
    bairro: seed.bairro ?? null,
    cidade: seed.cidade,
    telefones: (seed.telefones ?? []).map(telefone),
    diasSemana: seed.diasSemana,
    horario: seed.horario,
    ordem,
    criadoEm: AGORA,
    atualizadoEm: AGORA,
  };
}

function estado(regiaoId: string, ordem: number, uf: string, seeds: GrupoSeed[]): EstadoIntergrupo {
  const estadoId = proximoId('estado');
  return {
    id: estadoId,
    regiaoId,
    uf,
    nome: nomeUf(uf),
    ordem,
    grupos: seeds.map((seed, indice) => grupo(estadoId, indice, seed)),
    criadoEm: AGORA,
    atualizadoEm: AGORA,
  };
}

function regiao(ordem: number, nome: string, estadosSeed: [string, GrupoSeed[]][]): RegiaoIntergrupo {
  const regiaoId = proximoId('regiao');
  return {
    id: regiaoId,
    nome,
    ordem,
    estados: estadosSeed.map(([uf, grupos], indice) => estado(regiaoId, indice, uf, grupos)),
    criadoEm: AGORA,
    atualizadoEm: AGORA,
  };
}

const REGIOES_SEED: RegiaoIntergrupo[] = [
  regiao(0, 'Região Sudeste', [
    [
      'SP',
      [
        {
          nome: 'Grupo Santa Ifigênia',
          endereco: 'R. Santa Ifigênia nº 30',
          bairro: 'Santa Ifigênia',
          cidade: 'São Paulo - SP',
          diasSemana: ['DOMINGO'],
          horario: '9h-11h',
        },
        {
          nome: 'Grupo Pioneiro',
          endereco: 'Rua Guaporé nº 335',
          bairro: 'Luz',
          cidade: 'São Paulo - SP',
          diasSemana: ['SEGUNDA', 'QUINTA'],
          horario: '20h',
        },
        {
          nome: 'Grupo Itaim',
          endereco: 'Rua Clodomiro Amazonas nº 50',
          bairro: 'Itaim Bibi',
          cidade: 'São Paulo - SP',
          diasSemana: ['TERCA', 'QUINTA'],
          horario: '20h',
        },
        {
          nome: 'Grupo Taubaté',
          endereco: 'Rua Maria Tereza de Moura 150, Lar Bom Samaritano',
          cidade: 'Taubaté - SP',
          diasSemana: ['QUARTA'],
          horario: '19h-21h',
        },
      ],
    ],
    [
      'RJ',
      [
        {
          nome: 'Grupo Acre',
          endereco: 'Rua do Acre nº 47 Sala 412',
          bairro: 'Centro',
          cidade: 'Rio de Janeiro - RJ',
          telefones: [{ telefone: '21-98765-4321' }],
          diasSemana: ['SEGUNDA', 'QUARTA', 'TERCA', 'QUINTA', 'SEXTA', 'SABADO'],
          horario: 'Seg/Qua 12h30 e 18h · Ter/Qui/Sex 18h · Sáb 10h-12h',
        },
        {
          nome: 'Grupo Niterói',
          endereco: 'Av. Amaral Peixoto nº 171 Sala 804',
          bairro: 'Centro',
          cidade: 'Niterói - RJ',
          diasSemana: ['SEGUNDA', 'QUINTA', 'SABADO'],
          horario: 'Seg/Qui 19h · Sáb 9h30-11h30',
        },
        {
          nome: 'Grupo Tijuca',
          endereco: 'Rua Barão de Mesquita 275 Sala 205, Igreja Santo Afonso',
          bairro: 'Tijuca',
          cidade: 'Rio de Janeiro - RJ',
          diasSemana: ['TERCA', 'QUINTA'],
          horario: '19h-21h',
        },
      ],
    ],
    [
      'MG',
      [
        {
          nome: 'Grupo Vida Nova',
          endereco: 'Av. do Contorno nº 4777, Sede do Abraço',
          bairro: 'Funcionários',
          cidade: 'Belo Horizonte - MG',
          telefones: [{ telefone: '31-99206-2501' }],
          diasSemana: ['TERCA', 'QUINTA'],
          horario: '19h15',
        },
        {
          nome: 'Grupo Juiz de Fora',
          endereco: 'Rua São Mateus 629 Sala 306, Igreja São Matheus',
          bairro: 'São Matheus',
          cidade: 'Juiz de Fora - MG',
          telefones: [{ telefone: '32-98877-7088' }],
          diasSemana: ['SEGUNDA'],
          horario: '19h-20h30',
        },
      ],
    ],
    [
      'ES',
      [
        {
          nome: 'Grupo Vitória-ES',
          endereco: 'Praça São José Operário nº 221',
          bairro: 'Maruípe',
          cidade: 'Vitória - ES',
          diasSemana: ['SEGUNDA'],
          horario: '19h',
        },
      ],
    ],
  ]),
  regiao(1, 'Região Sul', [
    [
      'RS',
      [
        {
          nome: 'Grupo Esperança',
          endereco: 'Av. Farrapos nº 2611, Igreja de São Geraldo',
          bairro: 'Floresta',
          cidade: 'Porto Alegre - RS',
          telefones: [{ telefone: '51-98030-2002' }],
          diasSemana: ['TERCA', 'QUARTA'],
          horario: '19h',
        },
      ],
    ],
    [
      'SC',
      [
        {
          nome: 'Grupo Vida Nova Florianópolis',
          endereco: 'Rua Vitor Konder nº 344',
          bairro: 'Centro',
          cidade: 'Florianópolis - SC',
          diasSemana: ['TERCA', 'QUINTA', 'SABADO'],
          horario: 'Ter/Qui 19h · Sáb 16h',
        },
      ],
    ],
    [
      'PR',
      [
        {
          nome: 'Grupo Vitória - Curitiba',
          endereco: 'Av. Theodoro Makiolka nº 64',
          bairro: 'Santa Cândida',
          cidade: 'Curitiba - PR',
          telefones: [{ telefone: '41-99684-8004' }],
          diasSemana: ['QUINTA'],
          horario: '19h30',
        },
        {
          nome: 'Grupo Londrina',
          endereco: 'Rua Mato Grosso 1167',
          bairro: 'Centro',
          cidade: 'Londrina - PR',
          telefones: [{ label: 'Central', telefone: '43-99472-1933' }, { label: 'Plantão', telefone: '43-99101-2020' }],
          diasSemana: ['TERCA'],
          horario: '19h-21h',
        },
      ],
    ],
  ]),
  regiao(2, 'Região Centro-Oeste', [
    [
      'MS',
      [
        {
          nome: 'Grupo Nova Consciência',
          endereco: 'Rua Maracaju nº 249 Sala 01',
          bairro: 'Centro',
          cidade: 'Campo Grande - MS',
          telefones: [{ telefone: '67-98473-1321' }],
          diasSemana: ['TERCA', 'QUINTA'],
          horario: '19h',
        },
      ],
    ],
    [
      'MT',
      [
        {
          nome: 'Grupo Cuiabá',
          endereco: 'Av. Desembargador Antônio Quirino de Araújo 1126',
          bairro: 'Poções',
          cidade: 'Cuiabá - MT',
          telefones: [{ telefone: '65-99472-1933' }],
          diasSemana: ['QUARTA'],
          horario: '19h30',
        },
      ],
    ],
    [
      'DF',
      [
        {
          nome: 'Grupo Brasília',
          endereco: 'Sgan 609 Módulo C Sala 10, Paróquia Verbo Divino',
          bairro: 'Asa Norte',
          cidade: 'Brasília - DF',
          diasSemana: ['QUINTA'],
          horario: '19h',
        },
      ],
    ],
    [
      'GO',
      [
        {
          nome: 'Grupo Central',
          endereco: 'Rua DF-2, Capela N.Sra. da Guia',
          bairro: 'Setor Chácara do Governador',
          cidade: 'Goiânia - GO',
          diasSemana: ['SEGUNDA'],
          horario: '19h-21h',
        },
      ],
    ],
  ]),
  regiao(3, 'Região Nordeste', [
    [
      'BA',
      [
        {
          nome: 'Grupo Salvador',
          endereco: 'Alameda Verona nº 158',
          bairro: 'Pituba',
          cidade: 'Salvador - BA',
          telefones: [{ telefone: '71-98624-0512' }, { telefone: '71-99275-7068' }],
          diasSemana: ['TERCA', 'QUINTA'],
          horario: '19h',
        },
      ],
    ],
    [
      'PE',
      [
        {
          nome: 'Grupo Boa Vista',
          endereco: 'Edifício Tereza Cristina 3º andar Sala 305',
          bairro: 'Boa Vista',
          cidade: 'Recife - PE',
          telefones: [{ telefone: '81-99183-8297' }],
          diasSemana: ['DOMINGO'],
          horario: '10h',
        },
        {
          nome: 'Grupo IPSEP',
          endereco: 'Rua Jean Emile Travé, Escola Eleanor Roosevelt',
          cidade: 'Recife - PE',
          telefones: [{ telefone: '81-98693-3867' }],
          diasSemana: ['TERCA'],
          horario: '19h-21h',
        },
      ],
    ],
    [
      'CE',
      [
        {
          nome: 'Grupo Fortaleza',
          endereco: 'Rua São Paulo 32 Sala 316',
          bairro: 'Centro',
          cidade: 'Fortaleza - CE',
          telefones: [{ telefone: '85-98929-5529' }],
          diasSemana: ['SEGUNDA', 'QUARTA'],
          horario: '19h30-21h30',
        },
      ],
    ],
  ]),
  regiao(4, 'Região Norte', [
    [
      'PA',
      [
        {
          nome: 'Grupo Belém do Pará',
          endereco: 'Av. Ceará 595',
          bairro: 'Canudos',
          cidade: 'Belém - PA',
          telefones: [{ telefone: '91-99603-0369' }],
          diasSemana: ['DOMINGO'],
          horario: '9h-11h',
        },
      ],
    ],
    [
      'AM',
      [
        {
          nome: 'Grupo Manaus',
          endereco: 'Rua Libertador 55, Paróquia N.Sra. das Graças',
          bairro: 'N.Sra. das Graças',
          cidade: 'Manaus - AM',
          diasSemana: ['SEXTA'],
          horario: '19h-21h',
        },
      ],
    ],
  ]),
];

export const ESCRITORIOS_REGIONAIS_MOCK: EscritoriosRegionais = {
  escritorios: [
    {
      id: proximoId('escritorio'),
      nome: 'ERSJAB — Escritório Regional de Serviços de Jogadores Anônimos do Brasil',
      endereco: 'Rua Paulino Guimarães nº 93, Sala 12, Luz (Centro)',
      complemento: 'CEP 01109-020',
      bairro: 'Luz',
      cidade: 'São Paulo - SP',
      emails: ['jogadoresanonimossaopaulo@gmail.com', 'jogadoresanonimos@yahoo.com.br'],
      telefones: [telefone({ telefone: '(11) 3333-4444' })],
      ordem: 0,
      criadoEm: AGORA,
      atualizadoEm: AGORA,
    },
    {
      id: proximoId('escritorio'),
      nome: 'JAERJ — Jogadores Anônimos do Estado do Rio de Janeiro',
      endereco: '[endereço a confirmar]',
      complemento: null,
      bairro: null,
      cidade: '[a confirmar]',
      emails: [],
      telefones: [],
      ordem: 1,
      criadoEm: AGORA,
      atualizadoEm: AGORA,
    },
  ],
  regioes: REGIOES_SEED,
};
