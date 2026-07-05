import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

type RespostaMembro = 'pendente' | 'nao' | 'sim';

@Component({
  selector: 'app-contribuir',
  standalone: true,
  imports: [ResourceLayout, RouterLink],
  templateUrl: './contribuir.html',
  styleUrl: './contribuir.css',
})
export class Contribuir implements AfterViewInit {
  respostaMembro: RespostaMembro = 'pendente';

  @ViewChild('painel') private painelRef?: ElementRef<HTMLDivElement>;

  ngAfterViewInit(): void {
    this.focarPrimeiroFocavelDoModal();
  }

  registrarResposta(resposta: 'nao' | 'sim'): void {
    this.respostaMembro = resposta;

    if (resposta === 'nao') {
      setTimeout(() => this.focarPrimeiroFocavelDoModal(), 0);
    }
  }

  onModalKeydown(event: KeyboardEvent): void {
    if (event.key !== 'Tab' || !this.painelRef) {
      return;
    }

    const focaveis = Array.from(
      this.painelRef.nativeElement.querySelectorAll<HTMLElement>('button, a[href]'),
    );
    if (focaveis.length === 0) {
      return;
    }

    const primeiro = focaveis[0];
    const ultimo = focaveis[focaveis.length - 1];
    const focoAtual = document.activeElement;

    if (event.shiftKey && focoAtual === primeiro) {
      event.preventDefault();
      ultimo.focus();
    } else if (!event.shiftKey && focoAtual === ultimo) {
      event.preventDefault();
      primeiro.focus();
    }
  }

  private focarPrimeiroFocavelDoModal(): void {
    const primeiroFocavel = this.painelRef?.nativeElement.querySelector<HTMLElement>(
      'button, a[href]',
    );
    primeiroFocavel?.focus();
  }
}
