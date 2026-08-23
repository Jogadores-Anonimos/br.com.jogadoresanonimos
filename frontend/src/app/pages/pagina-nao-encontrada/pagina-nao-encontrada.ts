import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-pagina-nao-encontrada',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './pagina-nao-encontrada.html',
  styleUrl: './pagina-nao-encontrada.css',
})
export class PaginaNaoEncontrada implements AfterViewInit {
  @ViewChild('titulo') private titulo?: ElementRef<HTMLHeadingElement>;

  ngAfterViewInit(): void {
    this.titulo?.nativeElement.focus();
  }
}
