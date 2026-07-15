package br.com.jogadoresanonimos.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final int MAX_TENTATIVAS = 3;
    private static final long DELAY_INICIAL_MS = 2000;
    private static final long MULTIPLICADOR = 3;

    @Async
    public void enviarLinkRedefinicaoSenha(String usuarioIdHash, String linkComTokenBruto) {
        long delay = DELAY_INICIAL_MS;
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            try {
                enviarViaProvedor(linkComTokenBruto);
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

    private void enviarViaProvedor(String link) {
        log.info("email_enviado_simulado");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
