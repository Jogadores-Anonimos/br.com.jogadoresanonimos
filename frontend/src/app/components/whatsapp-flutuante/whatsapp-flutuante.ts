import { Component, HostListener } from '@angular/core';
import { WHATSAPP_CHAT_URL } from '../../config/whatsapp.config';

/**
 * Botão flutuante de chat (WhatsApp / Join.chat). Ao clicar, abre um
 * popup simples (não é um modal bloqueante) com um link para o WhatsApp.
 *
 * TODO: quando a conta Join.chat da organização for conectada, este
 * componente deve ser substituído (ou adaptado) para carregar o script
 * oficial de embed do Join.chat em vez do link direto wa.me.
 */
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
}
