import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { WHATSAPP_PHONE_NUMBER, montarLinkWhatsapp } from '../../config/whatsapp.config';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './footer.html',
  styleUrl: './footer.css'
})
export class Footer {
  protected readonly whatsappChatUrl = montarLinkWhatsapp(WHATSAPP_PHONE_NUMBER, 'Olá!');
}
