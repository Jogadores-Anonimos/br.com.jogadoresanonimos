import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

/**
 * Layout compartilhado das páginas de "recurso" do site (história, literatura,
 * programa de recuperação, etc.), no formato de duas colunas usado no site
 * americano: sidebar de navegação fixa à esquerda + banner/breadcrumb/artigo
 * à direita.
 *
 * Cada página só precisa fornecer o `titulo` e projetar seu próprio conteúdo
 * via `<ng-content>` — a sidebar, o banner e o breadcrumb são únicos e vivem
 * aqui, para não duplicar essa estrutura em cada página.
 */
@Component({
  selector: 'app-resource-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './resource-layout.html',
  styleUrl: './resource-layout.css',
})
export class ResourceLayout {
  /** Título da página. Aparece em branco sobreposto ao banner e, quando
   * `breadcrumbLabel` não é informado, também é usado como último item do
   * breadcrumb ("Home > {{ titulo }}"). */
  @Input({ required: true }) titulo!: string;

  /** Permite um rótulo de breadcrumb diferente do título completo da página
   * (ex: título longo demais para o breadcrumb). Opcional — usa `titulo` por
   * padrão. */
  @Input() breadcrumbLabel?: string;

  /** Imagem de banner padrão pedida para todas as páginas de recurso.
   * Renderizada em escala de cinza (ver `.resource-banner__image` em
   * resource-layout.css) — pode ser sobrescrita por página caso um dia seja
   * necessário usar outra foto, sem precisar alterar este componente. */
  @Input() bannerSrc = '/images/home/reuniao-discussao.jpg';
}
