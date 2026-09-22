import { Component } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { WHATSAPP_PHONE_NUMBER, montarLinkWhatsapp } from '../../config/whatsapp.config';

@Component({
  selector: 'app-grupos-em-formacao',
  standalone: true,
  imports: [ResourceLayout],
  templateUrl: './grupos-em-formacao.html',
  styleUrl: './grupos-em-formacao.css',
})
export class GruposEmFormacao {
  linkWhatsapp(): string {
    return montarLinkWhatsapp(
      WHATSAPP_PHONE_NUMBER,
      'Olá! Quero saber como abrir um grupo de JA na minha cidade.',
    );
  }
}
