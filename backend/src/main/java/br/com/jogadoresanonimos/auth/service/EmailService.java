package br.com.jogadoresanonimos.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final int MAX_TENTATIVAS = 3;
    private static final long DELAY_INICIAL_MS = 2000;
    private static final long MULTIPLICADOR = 3;
    private static final String RESEND_URL = "https://api.resend.com/emails";
    private static final String REMETENTE = "Jogadores Anônimos do Brasil <naoresponda@jogadoresanonimos.com.br>";

    private final RestTemplate restTemplate;
    private final String resendApiKey;

    public EmailService(RestTemplate restTemplate, @Value("${app.email.resend-api-key}") String resendApiKey) {
        this.restTemplate = restTemplate;
        this.resendApiKey = resendApiKey;
    }

    @Async
    public void enviarLinkRedefinicaoSenha(String usuarioIdHash, String emailDestino, String linkComTokenBruto) {
        String assunto = "Redefinição de senha - Jogadores Anônimos do Brasil";
        String corpoHtml = """
                <body style="margin: 0; padding: 0; background-color: #ffffff;">
                <div style="max-width: 560px; margin: 0 auto; padding: 24px; font-family: Arial, sans-serif;">
                <p style="font-size: 18px; font-weight: 600; color: #742C24; margin: 0 0 24px 0;">Jogadores Anônimos do Brasil</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Olá,</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Recebemos uma solicitação para redefinir a senha da sua conta.</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Se foi você, clique no botão abaixo para criar uma nova senha:</p>
                <p style="margin: 0 0 16px 0;"><a href="%s" style="background: #D4A843; color: #742C24; text-decoration: none; padding: 12px 24px; border-radius: 6px; display: inline-block; font-size: 15px;">Criar nova senha</a></p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Este link é válido por tempo limitado, por segurança.</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Se você não fez essa solicitação, não é necessário fazer nada, pode ignorar este e-mail com tranquilidade, sua conta permanece protegida.</p>
                <p style="font-size: 12px; color: #777777; padding-top: 24px; margin: 0;">Jogadores Anônimos do Brasil</p>
                <p style="font-size: 12px; color: #777777; margin: 4px 0 0 0;">Dúvidas? Fale conosco pelo site oficial.</p>
                </div>
                </body>
                """.formatted(linkComTokenBruto);
        long delay = DELAY_INICIAL_MS;
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            try {
                enviarViaProvedor(emailDestino, assunto, corpoHtml);
                return;
            } catch (Exception ex) {
                log.warn("falha_envio_email tentativa={} usuarioHash={} causa={}", tentativa, usuarioIdHash, ex.getClass().getSimpleName());
                if (tentativa == MAX_TENTATIVAS) {
                    log.error("falha_definitiva_envio_email usuarioHash={}", usuarioIdHash);
                    return;
                }
                sleep(delay);
                delay *= MULTIPLICADOR;
            }
        }
    }

    @Async
    public void enviarCodigoRedefinicaoSenhaAdmin(String usuarioIdHash, String emailDestino, String codigo) {
        String assunto = "Código de redefinição de senha - Jogadores Anônimos do Brasil";
        String corpoHtml = """
                <body style="margin: 0; padding: 0; background-color: #ffffff;">
                <div style="max-width: 560px; margin: 0 auto; padding: 24px; font-family: Arial, sans-serif;">
                <p style="font-size: 18px; font-weight: 600; color: #742C24; margin: 0 0 24px 0;">Jogadores Anônimos do Brasil — Administração</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Olá,</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Foi solicitada a redefinição de senha da sua conta de administrador. Use o código abaixo para continuar:</p>
                <p style="text-align: center; font-size: 28px; font-weight: 700; letter-spacing: 4px; background: #D4A843; color: #742C24; padding: 12px; border-radius: 6px; margin: 0 0 16px 0;">%s</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Este código é válido por tempo limitado, por segurança.</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Se você não fez essa solicitação, ignore este e-mail e, se possível, avise outro administrador da associação.</p>
                <p style="font-size: 12px; color: #777777; padding-top: 24px; margin: 0;">Jogadores Anônimos do Brasil</p>
                <p style="font-size: 12px; color: #777777; margin: 4px 0 0 0;">Fale conosco pelo site oficial.</p>
                </div>
                </body>
                """.formatted(codigo);
        long delay = DELAY_INICIAL_MS;
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            try {
                enviarViaProvedor(emailDestino, assunto, corpoHtml);
                return;
            } catch (Exception ex) {
                log.warn("falha_envio_email_admin tentativa={} usuarioHash={} causa={}", tentativa, usuarioIdHash, ex.getClass().getSimpleName());
                if (tentativa == MAX_TENTATIVAS) {
                    log.error("falha_definitiva_envio_email_admin usuarioHash={}", usuarioIdHash);
                    return;
                }
                sleep(delay);
                delay *= MULTIPLICADOR;
            }
        }
    }

    @Async
    public void enviarLinkConfirmacaoEmail(String usuarioIdHash, String emailDestino, String linkComTokenBruto) {
        String assunto = "Confirme seu e-mail - Jogadores Anônimos do Brasil";
        String corpoHtml = """
                <body style="margin: 0; padding: 0; background-color: #ffffff;">
                <div style="max-width: 560px; margin: 0 auto; padding: 24px; font-family: Arial, sans-serif;">
                <p style="font-size: 18px; font-weight: 600; color: #742C24; margin: 0 0 24px 0;">Jogadores Anônimos do Brasil</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Olá,</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Recebemos seu cadastro. Para confirmar que este é o seu e-mail, clique no botão abaixo:</p>
                <p style="margin: 0 0 16px 0;"><a href="%s" style="background: #D4A843; color: #742C24; text-decoration: none; padding: 12px 24px; border-radius: 6px; display: inline-block; font-size: 15px;">Confirmar e-mail</a></p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Este link é válido por 24 horas.</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Se você não fez esse cadastro, pode ignorar este e-mail com tranquilidade.</p>
                <p style="font-size: 12px; color: #777777; padding-top: 24px; margin: 0;">Jogadores Anônimos do Brasil</p>
                <p style="font-size: 12px; color: #777777; margin: 4px 0 0 0;">Dúvidas? Fale conosco pelo site oficial.</p>
                </div>
                </body>
                """.formatted(linkComTokenBruto);
        long delay = DELAY_INICIAL_MS;
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            try {
                enviarViaProvedor(emailDestino, assunto, corpoHtml);
                return;
            } catch (Exception ex) {
                log.warn("falha_envio_email_confirmacao tentativa={} usuarioHash={} causa={}", tentativa, usuarioIdHash, ex.getClass().getSimpleName());
                if (tentativa == MAX_TENTATIVAS) {
                    log.error("falha_definitiva_envio_email_confirmacao usuarioHash={}", usuarioIdHash);
                    return;
                }
                sleep(delay);
                delay *= MULTIPLICADOR;
            }
        }
    }

    private void enviarViaProvedor(String emailDestino, String assunto, String corpoHtml) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);
        Map<String, Object> corpo = Map.of(
                "from", REMETENTE,
                "to", List.of(emailDestino),
                "subject", assunto,
                "html", corpoHtml);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(corpo, headers);
        restTemplate.postForObject(RESEND_URL, request, String.class);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
