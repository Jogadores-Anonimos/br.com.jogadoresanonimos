import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

interface Passo {
  numero: number;
  texto: string;
}

@Component({
  selector: 'app-programa-de-unidade',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './programa-de-unidade.html',
  styleUrl: './programa-de-unidade.css',
})
export class ProgramaDeUnidade {
  readonly passos: Passo[] = [
    { numero: 1, texto: 'O bem-estar comum deve vir em primeiro lugar.' },
    { numero: 2, texto: 'Nossos líderes são servidores de confiança; eles não governam.' },
    { numero: 3, texto: 'O único requisito para ser membro é o desejo de parar de jogar.' },
    { numero: 4, texto: 'Cada grupo deve ser autônomo, exceto em assuntos que afetem outros grupos ou a Jogadores Anônimos como um todo.' },
    { numero: 5, texto: 'Cada grupo tem um único propósito primário: levar a mensagem ao jogador compulsivo que ainda sofre.' },
    { numero: 6, texto: 'Um grupo não deve endossar, financiar ou emprestar o nome da Jogadores Anônimos a nenhuma entidade externa.' },
    { numero: 7, texto: 'Todo grupo deve ser autossustentável, recusando contribuições externas.' },
    { numero: 8, texto: 'A Jogadores Anônimos deve permanecer sempre não-profissional.' },
    { numero: 9, texto: 'A Jogadores Anônimos nunca deve ter uma estrutura organizacional formal, mas pode criar juntas ou comitês de serviço diretamente responsáveis aos que servem.' },
    { numero: 10, texto: 'A Jogadores Anônimos não tem opinião sobre questões externas; o nome da Jogadores Anônimos nunca deve ser envolvido em controvérsias públicas.' },
    { numero: 11, texto: 'Nossa política de relações públicas se baseia na atração, não na promoção; mantemos anonimato pessoal perante a mídia.' },
    { numero: 12, texto: 'O anonimato é a base espiritual de todas as nossas tradições.' },
  ];
}
