import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-resource-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './resource-layout.html',
  styleUrl: './resource-layout.css',
})
export class ResourceLayout {
  @Input({ required: true }) titulo!: string;

  @Input() breadcrumbLabel?: string;

  @Input() bannerSrc = '/images/home/reuniao-discussao.jpg';

  // false para paginas sem sidebar (ex: Minha Conta)
  @Input() mostrarSidebar = true;
}
