import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

@Component({
  selector: 'app-programa-de-unidade',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './programa-de-unidade.html',
  styleUrl: './programa-de-unidade.css',
})
export class ProgramaDeUnidade {
  readonly principios: string[] = [
    'O bem-estar comum deve vir em primeiro lugar.',
    'Nossos líderes são servidores de confiança; eles não governam.',
    'O único requisito para ser membro é o desejo de parar de jogar.',
    'Cada grupo deve ser autônomo, exceto em assuntos que afetem outros grupos ou a Jogadores Anônimos como um todo.',
    'Cada grupo tem um único propósito primário: levar a mensagem ao jogador compulsivo que ainda sofre.',
    'Um grupo não deve endossar, financiar ou emprestar o nome da Jogadores Anônimos a nenhuma entidade externa.',
    'Todo grupo deve ser autossustentável, recusando contribuições externas.',
    'A Jogadores Anônimos deve permanecer sempre não-profissional.',
    'A Jogadores Anônimos nunca deve ter uma estrutura organizacional formal, mas pode criar juntas ou comitês de serviço diretamente responsáveis aos que servem.',
    'A Jogadores Anônimos não tem opinião sobre questões externas; o nome da Jogadores Anônimos nunca deve ser envolvido em controvérsias públicas.',
    'Nossa política de relações públicas se baseia na atração, não na promoção; mantemos anonimato pessoal perante a mídia.',
    'O anonimato é a base espiritual de todas as nossas tradições.',
  ];
}
