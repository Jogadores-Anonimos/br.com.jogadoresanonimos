import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Grupo {
  nome: string;
  endereco: string;
  bairro?: string;
  cidade: string;
  tel?: string;
  obs?: string;
}

interface Regiao {
  nome: string;
  estados: { nome: string; grupos: Grupo[] }[];
}

@Component({
  selector: 'app-enderecos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './enderecos.html',
  styleUrl: './enderecos.css',
})
export class Enderecos {
  regiaoAtiva = 0;

  regioes: Regiao[] = [
    {
      nome: 'Sudeste',
      estados: [
        {
          nome: 'São Paulo',
          grupos: [
            { nome: 'Grupo Santa Ifigênia', endereco: 'R. Santa Ifigênia nº 30', bairro: 'Santa Ifigênia', cidade: 'São Paulo - SP' },
            { nome: 'Grupo Pioneiro', endereco: 'Rua Guaporé nº 335', bairro: 'Luz', cidade: 'São Paulo - SP' },
            { nome: 'Grupo Itaim', endereco: 'Rua Clodomiro Amazonas nº 50', bairro: 'Itaim Bibi', cidade: 'São Paulo - SP' },
            { nome: 'Grupo Mauá', endereco: 'Praça Monsenhor Alexandre Venâncio Arminas nº 01', bairro: 'Matriz', cidade: 'Mauá - SP' },
            { nome: 'Grupo São Bernardo do Campo', endereco: 'Estrada Dos Casa, 3230', bairro: 'Dos Casa', cidade: 'São Bernardo do Campo - SP' },
            { nome: 'Grupo Renascer - Campinas', endereco: 'Rua Guaraci nr. 38', bairro: 'Vila Ângela Marta', cidade: 'Campinas - SP' },
            { nome: 'Grupo Jabaquara', endereco: 'Av. Jabaquara nº 2682 - Igreja São Judas Tadeu', bairro: 'Jabaquara', cidade: 'São Paulo - SP' },
            { nome: 'Grupo Buscando a Serenidade', endereco: 'Rua Pedreira de Magalhães nº 100', bairro: 'Artur Alvim', cidade: 'São Paulo - SP' },
            { nome: 'Grupo Centro São José dos Campos', endereco: 'Av. São José, 646 - Jardim Bela Vista', cidade: 'São José dos Campos - SP' },
            { nome: 'Grupo Santos', endereco: 'Av. Dr. Pedro Lessa nº 2262', bairro: 'Ponta da Praia', cidade: 'Santos - SP' },
            { nome: 'Grupo Taubaté', endereco: 'Rua Maria Tereza de Moura, 150 - Estiva - Lar Bom Samaritano', cidade: 'Taubaté - SP' },
            { nome: 'Grupo Nova Chance', endereco: 'Av. João Dias nº 822 - 3º andar da Biblioteca', bairro: 'Santo Amaro', cidade: 'São Paulo - SP' },
            { nome: 'Grupo Semeando Recomeços', endereco: 'Rua Bertioga, 191 - Jardim Satélite', cidade: 'São José dos Campos - SP', tel: '(12) 99738-0690' },
            { nome: 'Grupo Pindamonhangaba', endereco: 'Rua Barão Homem de Melo, s/n - Centro (Embaixo do Viaduto)', cidade: 'Pindamonhangaba - SP' },
            { nome: 'Grupo Limeira', endereco: 'Rua Oscar Buzolin, 1-63 - Jardim Planalto - Paróquia N.Sra. do Rosário de Fátima', cidade: 'Limeira - SP' },
            { nome: 'Grupo Atibaia', endereco: 'Av. Jerônimo de Camargo, 3111 - Vila Salles', cidade: 'Atibaia - SP' },
            { nome: 'Grupo São Miguel Coragem & Sabedoria', endereco: 'Av. Imperador 5831 - Vila Jacui', bairro: 'São Miguel', cidade: 'São Paulo - SP' },
            { nome: 'Grupo Passos de Luz', endereco: 'Rua N.Sra. das Dores, s/nº - Vila Queiroz - Igreja Matriz - Salão Paroquial, 2º andar', cidade: 'Artur Nogueira - SP' },
            { nome: 'Grupo São José do Rio Preto', endereco: 'Rua José Martins Romero, 590 - Ass. Joanna de Angelis', cidade: 'São José do Rio Preto - SP' },
            { nome: 'Grupo Bertioga', endereco: 'Av. Anchieta, 1450 - Sala 08 - Centro', cidade: 'Bertioga - SP' },
            { nome: 'Grupo Sorocaba', endereco: 'Rua José Mesquita Sobrinho, 129 - Jardim Vergueiro', cidade: 'Sorocaba - SP' },
          ]
        },
        {
          nome: 'Rio de Janeiro',
          grupos: [
            { nome: 'Grupo Acre', endereco: 'Rua do Acre nº 47 - Sala 412', bairro: 'Centro', cidade: 'Rio de Janeiro - RJ' },
            { nome: 'Grupo Niterói', endereco: 'Av. Amaral Peixoto nº 171 - Sala 804', bairro: 'Centro', cidade: 'Niterói - RJ' },
            { nome: 'Grupo Nilópolis', endereco: 'Rua Comendador Rodrigues Alves nº 1544', bairro: 'Centro', cidade: 'Nilópolis - RJ' },
            { nome: 'Grupo Flamengo', endereco: 'Rua Senador Vergueiro nº 141 - Prédio anexo (fundos) 2º andar sala 5', bairro: 'Flamengo', cidade: 'Rio de Janeiro - RJ' },
            { nome: 'Grupo Neves', endereco: 'Rua Oliveira Botelho, S/Nº - Igreja N.Sra. das Neves', cidade: 'São Gonçalo - RJ' },
            { nome: 'Grupo Petrópolis', endereco: 'R. Visc. de Souza Franco, 474 - Centro - Associação Casa da Cidadania', cidade: 'Petrópolis - RJ' },
            { nome: 'Grupo Barra', endereco: 'Praça Embaixador Gualberto de Oliveira s/n - Paróquia de São Marcos', bairro: 'Barra', cidade: 'Rio de Janeiro - RJ' },
            { nome: 'Grupo Campo Grande', endereco: 'Estrada do Mendanha nº 208 - Igreja de Sant\'ana', bairro: 'Campo Grande', cidade: 'Rio de Janeiro - RJ' },
            { nome: 'Grupo Pilares', endereco: 'Av. Dom Helder Câmara nº 6695 - Paróquia de São Benedito', bairro: 'Pilares', cidade: 'Rio de Janeiro - RJ' },
            { nome: 'Grupo Copacabana', endereco: 'Rua Francisco Otaviano nº 99 2º A - Igreja da Ressurreição', bairro: 'Copacabana', cidade: 'Rio de Janeiro - RJ' },
            { nome: 'Grupo Arpoador', endereco: 'Rua Francisco Otaviano nº 99 2º A - Igreja da Ressurreição', bairro: 'Copacabana', cidade: 'Rio de Janeiro - RJ' },
            { nome: 'Grupo São Cristóvão', endereco: 'Rua Fonseca Teles, 109 - SL 05 - Paróquia de Santa Edwiges', bairro: 'São Cristóvão', cidade: 'Rio de Janeiro - RJ', tel: '(21) 97751-7734' },
            { nome: 'Grupo Nova Iguaçu', endereco: 'Rua Marechal Floriano Peixoto, 2264 - Catedral de Santo Antonio', bairro: 'Centro', cidade: 'Nova Iguaçu - RJ', tel: '(21) 99472-1933' },
            { nome: 'Grupo Tijuca', endereco: 'Rua Barão de Mesquita nr. 275 - Sala 205 - Igreja Santo Afonso', bairro: 'Tijuca', cidade: 'Rio de Janeiro - RJ' },
          ]
        },
        {
          nome: 'Minas Gerais',
          grupos: [
            { nome: 'Grupo Vida Nova', endereco: 'Av. do Contorno nº 4777 - Sede do Abraço', bairro: 'Funcionários', cidade: 'Belo Horizonte - MG', tel: '(31) 99206-2501' },
            { nome: 'Grupo São Lourenço', endereco: 'Rua Coronel José Justino, 50', cidade: 'São Lourenço - MG' },
            { nome: 'Grupo Juiz de Fora', endereco: 'Rua São Mateus, 629 - Sala 306 - 3º andar - Igreja São Matheus', bairro: 'São Matheus', cidade: 'Juiz de Fora - MG', tel: '(32) 98877-7088' },
            { nome: 'Grupo Varginha', endereco: 'Praça Quintino Bocaiúva, 58 - Sl. 301 - Prédio do Mercado Municipal', bairro: 'Centro', cidade: 'Varginha - MG', tel: '(35) 99989-9997' },
            { nome: 'Grupo Uberlândia', endereco: 'Rua Geraldo Garcia de Almeida, 160 - S.Jorge - Igreja Evangélica Luterana', cidade: 'Uberlândia - MG', tel: '(21) 99472-1933' },
          ]
        },
        {
          nome: 'Espírito Santo',
          grupos: [
            { nome: 'Grupo Vitória', endereco: 'Praça São José Operário nº 221', bairro: 'Maruípe', cidade: 'Vitória - ES' },
          ]
        }
      ]
    },
    {
      nome: 'Sul',
      estados: [
        {
          nome: 'Rio Grande do Sul',
          grupos: [
            { nome: 'Grupo Esperança', endereco: 'Av. Farrapos nº 2611 - Igreja de São Geraldo', bairro: 'Floresta', cidade: 'Porto Alegre - RS', tel: '(51) 98030-2002' },
            { nome: 'Grupo Novo Hamburgo', endereco: 'Rua Dr. Daniel Hillebrand, 1349 - Igreja Evangélica Luterana', bairro: 'Rondônia', cidade: 'Novo Hamburgo - RS', tel: '(51) 98030-2002' },
          ]
        },
        {
          nome: 'Santa Catarina',
          grupos: [
            { nome: 'Grupo Vida Nova Florianópolis', endereco: 'Rua Vitor Konder nº 344', bairro: 'Centro', cidade: 'Florianópolis - SC' },
          ]
        },
        {
          nome: 'Paraná',
          grupos: [
            { nome: 'Grupo Vitória - Curitiba', endereco: 'Av. Theodoro Makiolka nº 64 - Em frente à Escola Estadual Santa Cândida', bairro: 'Santa Cândida', cidade: 'Curitiba - PR', tel: '(41) 99684-8004' },
            { nome: 'Grupo Renovação - Curitiba', endereco: 'Av. N.Sra. Aparecida, 1637 - Salão da Igreja', bairro: 'Seminário', cidade: 'Curitiba - PR', tel: '(41) 99893-6054' },
            { nome: 'Grupo Londrina', endereco: 'Rua Mato Grosso, 1167 - Paróquia Sagrados Corações', bairro: 'Centro', cidade: 'Londrina - PR', tel: '(21) 99472-1933' },
            { nome: 'Grupo Maringá', endereco: 'Pça. Mons. Bernard, 7 Zona7 - Paróquia Divino Espírito Santo', cidade: 'Maringá - PR' },
          ]
        }
      ]
    },
    {
      nome: 'Centro-Oeste',
      estados: [
        {
          nome: 'Mato Grosso do Sul',
          grupos: [
            { nome: 'Grupo Nova Consciência', endereco: 'Rua Maracaju nº 249 Sala 01', bairro: 'Centro', cidade: 'Campo Grande - MS', tel: '(67) 98473-1321' },
          ]
        },
        {
          nome: 'Mato Grosso',
          grupos: [
            { nome: 'Grupo Cuiabá (a partir de 29/07/26)', endereco: 'Av. Desembargador Antônio Quirino de Araújo, 1126', bairro: 'Poções', cidade: 'Cuiabá - MT', tel: '(21) 99472-1933' },
          ]
        },
        {
          nome: 'Distrito Federal',
          grupos: [
            { nome: 'Grupo Brasília', endereco: 'SGAN 609 - Módulo C - Sala 10 - Asa Norte - Paróquia Verbo Divino', cidade: 'Brasília - DF' },
          ]
        },
        {
          nome: 'Goiás',
          grupos: [
            { nome: 'Grupo Goiânia', endereco: 'Rua 24, nº 25 - Centro - Centro Pastoral D.Antônio R. de Oliveira', cidade: 'Goiânia - GO' },
          ]
        }
      ]
    },
    {
      nome: 'Nordeste',
      estados: [
        {
          nome: 'Paraíba',
          grupos: [
            { nome: 'Grupo Paraíba', endereco: 'Rua Almeida Barreto nº 222 Sala 23', bairro: 'Centro', cidade: 'João Pessoa - PB', tel: '(83) 98872-6572' },
          ]
        },
        {
          nome: 'Bahia',
          grupos: [
            { nome: 'Grupo Salvador', endereco: 'Alameda Verona nº 158', bairro: 'Pituba', cidade: 'Salvador - BA', tel: '(71) 98624-0512' },
          ]
        },
        {
          nome: 'Pernambuco',
          grupos: [
            { nome: 'Grupo Boavista', endereco: 'Edifício Tereza Cristina 3º andar Sala 305', bairro: 'Boa Vista', cidade: 'Recife - PE', tel: '(81) 99183-8297' },
            { nome: 'Grupo IPSEP (a partir de 07/04/26)', endereco: 'Rua Jean Emile Trave, s/nº - Escola Eleanor Roosevelt', bairro: 'IPSEP', cidade: 'Recife - PE', tel: '(81) 98693-3867' },
            { nome: 'Grupo Petrolina', endereco: 'Rua São José, 121 - Centro - Casa dos 12 Passos', cidade: 'Petrolina - PE', tel: '(74) 99122-2660' },
            { nome: 'Grupo Pina', endereco: 'Av. Herculano Bandeira - Pastoral João Paulo II, ao lado da Igreja Católica do Pina', bairro: 'Pina', cidade: 'Recife - PE', tel: '(81) 9315-9357' },
            { nome: 'Grupo Caruaru', endereco: 'Av. Prof. José Leão, 567', bairro: 'Maurício de Nassau', cidade: 'Caruaru - PE', tel: '(81) 99921-0731' },
          ]
        },
        {
          nome: 'Ceará',
          grupos: [
            { nome: 'Grupo Fortaleza', endereco: 'Rua São Paulo, 32 Sala 316', bairro: 'Centro', cidade: 'Fortaleza - CE', tel: '(85) 98929-5529' },
            { nome: 'Grupo Iracema', endereco: 'Av. Almirante Barroso, 949 - Praia de Iracema', cidade: 'Fortaleza - CE', tel: '(85) 98929-5529' },
          ]
        },
        {
          nome: 'Alagoas',
          grupos: [
            { nome: 'Grupo Maceió', endereco: 'Av. Fernandes Lima, 869 - Farol (Acesso pela lateral do Guaraná da Praia)', cidade: 'Maceió - AL', tel: '(82) 99620-2998' },
          ]
        },
        {
          nome: 'Sergipe',
          grupos: [
            { nome: 'Grupo Aracaju', endereco: 'Rua Renato Fonseca de Oliveira, 585 - Coroa do Meio - Paróquia N.Sra de Guadalupe', cidade: 'Aracaju - SE', tel: '(11) 99571-6942' },
          ]
        },
        {
          nome: 'Piauí',
          grupos: [
            { nome: 'Grupo Teresina', endereco: 'Rod. 316, 7.200 - Parque Piauí - CMEI Luterano (ao lado da Igreja)', cidade: 'Teresina - PI', tel: '(21) 99472-1933' },
          ]
        },
        {
          nome: 'Rio Grande do Norte',
          grupos: [
            { nome: 'Grupo Natal', endereco: 'Rua Domingos Amado, 3393-D', bairro: 'Candelária', cidade: 'Natal - RN', tel: '(84) 998348964' },
            { nome: 'Grupo Mossoró', endereco: 'Rua Sebastião Saraiva, 42 - Aeroporto - UEI Julio G. Neto', cidade: 'Mossoró - RN' },
          ]
        },
        {
          nome: 'Maranhão',
          grupos: [
            { nome: 'Grupo São Luís', endereco: 'Praça Gonçalves Dias, 696 - Centro - Paróquia N.Sra dos Remédios', cidade: 'São Luís - MA', tel: '(21) 99472-1933' },
          ]
        }
      ]
    },
    {
      nome: 'Norte',
      estados: [
        {
          nome: 'Tocantins',
          grupos: [
            { nome: 'Grupo Palmas', endereco: 'Entre em contato pelo WhatsApp da Linha de Ajuda', cidade: 'Palmas - TO', tel: '(21) 99472-1933' },
          ]
        },
        {
          nome: 'Pará',
          grupos: [
            { nome: 'Grupo Belém do Pará', endereco: 'Av. Ceará, 595', bairro: 'Canudos', cidade: 'Belém - PA', tel: '(91) 99603-0369' },
          ]
        },
        {
          nome: 'Amazonas',
          grupos: [
            { nome: 'Grupo Manaus', endereco: 'Rua Libertador, 55 - N.Sra. das Graças - Paróquia N.Sra das Graças', cidade: 'Manaus - AM' },
          ]
        }
      ]
    }
  ];

  getMapsUrl(grupo: Grupo): string {
    const query = encodeURIComponent(`${grupo.endereco}, ${grupo.cidade}`);
    return `https://www.google.com/maps/search/?api=1&query=${query}`;
  }
}
