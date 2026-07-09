/**
 * Configuração centralizada do canal de WhatsApp usado pelo botão
 * flutuante de chat do site.
 *
 * TODO: quando a conta Join.chat da organização for conectada,
 * substituir este link direto por wa.me pelo script oficial de embed
 * do Join.chat (que troca o botão flutuante e o popup pelo widget deles).
 */

const WHATSAPP_PHONE_NUMBER = '5521994721933';

export const WHATSAPP_CHAT_URL =
  `https://web.whatsapp.com/send?phone=${WHATSAPP_PHONE_NUMBER}&text=Ol%C3%A1%21`;
