package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.entity.TipoEmailConfirmacaoToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final int MAX_TENTATIVAS = 3;
    private static final long DELAY_INICIAL_MS = 2000;
    private static final long MULTIPLICADOR = 3;
    private static final String RESEND_URL = "https://api.resend.com/emails";
    private static final String REMETENTE = "Jogadores Anônimos do Brasil <naoresponda@jogadoresanonimos.com.br>";
    private static final String LOGO_PATH = "/images/header/logo-jogadores-anonimos-monograma.png";

    private final RestTemplate restTemplate;
    private final String resendApiKey;
    private final String logoUrl;

    public EmailService(RestTemplate restTemplate,
                         @Value("${app.email.resend-api-key}") String resendApiKey,
                         @Value("${app.frontend-url}") String frontendUrl) {
        this.restTemplate = restTemplate;
        this.resendApiKey = resendApiKey;
        this.logoUrl = UriComponentsBuilder.fromUriString(frontendUrl).replacePath(LOGO_PATH).toUriString();
    }

    @Async
    public void enviarLinkRedefinicaoSenha(String usuarioIdHash, String emailDestino, String linkComTokenBruto) {
        String assunto = "Redefinição de senha - Jogadores Anônimos do Brasil";
        String corpoHtml = """
                <body style="margin: 0; padding: 0; background-color: #ffffff;">
                <div style="max-width: 560px; margin: 0 auto; padding: 24px; font-family: Arial, sans-serif;">
                <p style="text-align: center; margin: 0 0 24px 0;"><img src="%s" alt="Jogadores Anônimos do Brasil" width="72" style="max-width: 72px; height: auto;"></p>
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
                """.formatted(logoUrl, linkComTokenBruto);
        String chamadaId = UUID.randomUUID().toString();
        long delay = DELAY_INICIAL_MS;
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            log.info("resend_chamada_disparada fluxo=redefinicao_senha chamadaId={} tentativa={} usuarioHash={}", chamadaId, tentativa, usuarioIdHash);
            try {
                HttpStatusCode status = enviarViaProvedor(emailDestino, assunto, corpoHtml);
                log.info("resend_chamada_sucesso fluxo=redefinicao_senha chamadaId={} tentativa={} usuarioHash={} statusHttp={}", chamadaId, tentativa, usuarioIdHash, status.value());
                return;
            } catch (Exception ex) {
                log.warn("resend_chamada_falha fluxo=redefinicao_senha chamadaId={} tentativa={} usuarioHash={} causa={} statusHttp={}", chamadaId, tentativa, usuarioIdHash, ex.getClass().getSimpleName(), statusHttpDe(ex));
                if (tentativa == MAX_TENTATIVAS) {
                    log.error("resend_chamada_falha_definitiva fluxo=redefinicao_senha chamadaId={} usuarioHash={}", chamadaId, usuarioIdHash);
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
                <p style="text-align: center; margin: 0 0 24px 0;"><img src="%s" alt="Jogadores Anônimos do Brasil" width="72" style="max-width: 72px; height: auto;"></p>
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
                """.formatted(logoUrl, codigo);
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
    public void enviarLinkConfirmacaoEmail(String usuarioIdHash, String emailDestino, String linkComTokenBruto,
                                            TipoEmailConfirmacaoToken tipo) {
        String fluxo = tipo == TipoEmailConfirmacaoToken.TROCA_EMAIL ? "troca_email" : "confirmacao_cadastro";
        String assunto = tipo == TipoEmailConfirmacaoToken.TROCA_EMAIL
                ? "Confirme seu novo e-mail - Jogadores Anônimos do Brasil"
                : "Confirme seu e-mail - Jogadores Anônimos do Brasil";
        String textoIntroducao = tipo == TipoEmailConfirmacaoToken.TROCA_EMAIL
                ? "Recebemos uma solicitação para trocar o e-mail da sua conta para este endereço. Para confirmar a troca, clique no botão abaixo:"
                : "Recebemos seu cadastro. Para confirmar que este é o seu e-mail, clique no botão abaixo:";
        String textoIgnorar = tipo == TipoEmailConfirmacaoToken.TROCA_EMAIL
                ? "Se você não solicitou essa troca, não é necessário fazer nada, pode ignorar este e-mail com tranquilidade, seu e-mail atual permanece inalterado."
                : "Se você não fez esse cadastro, pode ignorar este e-mail com tranquilidade.";
        String corpoHtml = """
                <body style="margin: 0; padding: 0; background-color: #ffffff;">
                <div style="max-width: 560px; margin: 0 auto; padding: 24px; font-family: Arial, sans-serif;">
                <p style="text-align: center; margin: 0 0 24px 0;"><img src="%s" alt="Jogadores Anônimos do Brasil" width="72" style="max-width: 72px; height: auto;"></p>
                <p style="font-size: 18px; font-weight: 600; color: #742C24; margin: 0 0 24px 0;">Jogadores Anônimos do Brasil</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Olá,</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">%s</p>
                <p style="margin: 0 0 16px 0;"><a href="%s" style="background: #D4A843; color: #742C24; text-decoration: none; padding: 12px 24px; border-radius: 6px; display: inline-block; font-size: 15px;">Confirmar e-mail</a></p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">Este link é válido por 24 horas.</p>
                <p style="font-size: 15px; line-height: 1.6; color: #000000; margin: 0 0 16px 0;">%s</p>
                <p style="font-size: 12px; color: #777777; padding-top: 24px; margin: 0;">Jogadores Anônimos do Brasil</p>
                <p style="font-size: 12px; color: #777777; margin: 4px 0 0 0;">Dúvidas? Fale conosco pelo site oficial.</p>
                </div>
                </body>
                """.formatted(logoUrl, textoIntroducao, linkComTokenBruto, textoIgnorar);
        String chamadaId = UUID.randomUUID().toString();
        long delay = DELAY_INICIAL_MS;
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            log.info("resend_chamada_disparada fluxo={} chamadaId={} tentativa={} usuarioHash={}", fluxo, chamadaId, tentativa, usuarioIdHash);
            try {
                HttpStatusCode status = enviarViaProvedor(emailDestino, assunto, corpoHtml);
                log.info("resend_chamada_sucesso fluxo={} chamadaId={} tentativa={} usuarioHash={} statusHttp={}", fluxo, chamadaId, tentativa, usuarioIdHash, status.value());
                return;
            } catch (Exception ex) {
                log.warn("resend_chamada_falha fluxo={} chamadaId={} tentativa={} usuarioHash={} causa={} statusHttp={}", fluxo, chamadaId, tentativa, usuarioIdHash, ex.getClass().getSimpleName(), statusHttpDe(ex));
                if (tentativa == MAX_TENTATIVAS) {
                    log.error("resend_chamada_falha_definitiva fluxo={} chamadaId={} usuarioHash={}", fluxo, chamadaId, usuarioIdHash);
                    return;
                }
                sleep(delay);
                delay *= MULTIPLICADOR;
            }
        }
    }

    private HttpStatusCode enviarViaProvedor(String emailDestino, String assunto, String corpoHtml) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);
        Map<String, Object> corpo = Map.of(
                "from", REMETENTE,
                "to", List.of(emailDestino),
                "subject", assunto,
                "html", corpoHtml);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(corpo, headers);
        ResponseEntity<String> resposta = restTemplate.postForEntity(RESEND_URL, request, String.class);
        return resposta.getStatusCode();
    }

    private String statusHttpDe(Exception ex) {
        if (ex instanceof org.springframework.web.client.RestClientResponseException responseEx) {
            return String.valueOf(responseEx.getStatusCode().value());
        }
        return "indisponivel";
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
