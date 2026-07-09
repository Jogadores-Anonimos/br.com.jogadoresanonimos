import { Component, ElementRef, HostListener } from '@angular/core';
import { WHATSAPP_CHAT_URL } from '../../config/whatsapp.config';

@Component({
  selector: 'app-whatsapp-flutuante',
  standalone: true,
  imports: [],
  templateUrl: './whatsapp-flutuante.html',
  styleUrl: './whatsapp-flutuante.css'
})
export class WhatsappFlutuante {
  readonly whatsappChatUrl = WHATSAPP_CHAT_URL;

  popupAberto = false;

  constructor(private readonly elementRef: ElementRef<HTMLElement>) {}

  togglePopup(): void {
    this.popupAberto = !this.popupAberto;
  }

  fecharPopup(): void {
    this.popupAberto = false;
  }

  abrirChat(): void {
    window.open(this.whatsappChatUrl, '_blank', 'noopener,noreferrer');
    this.fecharPopup();
  }

  @HostListener('document:keydown.escape')
  aoPressionarEsc(): void {
    if (this.popupAberto) {
      this.fecharPopup();
    }
  }

  @HostListener('document:click', ['$event'])
  aoClicarFora(event: MouseEvent): void {
    if (!this.popupAberto) {
      return;
    }

    const alvo = event.target as Node | null;
    if (alvo && !this.elementRef.nativeElement.contains(alvo)) {
      this.fecharPopup();
    }
  }
}
