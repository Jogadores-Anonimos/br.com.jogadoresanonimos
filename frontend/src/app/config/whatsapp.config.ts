export const WHATSAPP_PHONE_NUMBER = '5521994721933';
const DDI_BRASIL = '55';

export function montarLinkWhatsapp(numeroCru: string, mensagem?: string): string {
  const somenteDigitos = numeroCru.replace(/\D/g, '');
  const numeroComDdi = somenteDigitos.startsWith(DDI_BRASIL) ? somenteDigitos : `${DDI_BRASIL}${somenteDigitos}`;
  const query = mensagem ? `?text=${encodeURIComponent(mensagem)}` : '';
  return `https://wa.me/${numeroComDdi}${query}`;
}

export const WHATSAPP_CHAT_URL = montarLinkWhatsapp(WHATSAPP_PHONE_NUMBER, 'Olá!');
