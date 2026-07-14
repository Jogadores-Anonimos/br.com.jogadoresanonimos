package br.com.jogadoresanonimos.cms.util;

import br.com.jogadoresanonimos.common.exception.ValidationException;
import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlChangeListener;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sanitização de markup limitado (seção 2/6 do doc de decisões): allowlist
 * <b> <i> <a href="..."> <p> <br> <h2> <h3> <strong> <ul> <ol> <li>, via
 * OWASP Java HTML Sanitizer.
 *
 * Ampliação de 2026-07-13 (heading/lista/strong): superfície de risco não
 * muda — só ADMIN autenticado escreve conteúdo de CMS (mesma confiança já
 * aceita hoje pras tags originais), e nenhuma das tags novas carrega
 * atributo (sem href/style/script), então não abre vetor de XSS/CSS
 * injection novo. Motivo real era fidelidade visual (headings de seção e
 * listas numeradas/com marcador virando parágrafo solto no seed V9).
 */
@Component
public class HtmlSanitizer {

    // "/" isolado exige path relativo de verdade — "//" no início é
    // protocol-relative URL (resolve pro domínio de outro host), não path.
    private static final Pattern SAFE_HREF = Pattern.compile("^(https?://|/(?!/))[^\"'<>\\s]*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern HREF_ATRIBUTO = Pattern.compile("<a\\b[^>]*\\bhref\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

    private static final AttributePolicy HREF_POLICY = (elementName, attributeName, value) ->
            SAFE_HREF.matcher(value).matches() ? value : null;

    // allowUrlProtocols é indispensável: sem ao menos um protocolo liberado, o
    // builder do OWASP descarta href globalmente (ver javadoc de allowUrlProtocols).
    // HREF_POLICY complementa cobrindo caminho relativo e fechando brechas que a
    // checagem nativa de protocolo sozinha deixaria passar (ex: "//evil.com").
    private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
            .allowElements("b", "i", "p", "br", "a", "h2", "h3", "strong", "ul", "ol", "li")
            .allowAttributes("href").matching(HREF_POLICY).onElements("a")
            .allowUrlProtocols("http", "https")
            .toFactory();

    public String sanitizar(String conteudoBruto) {
        if (conteudoBruto == null) {
            return null;
        }

        MotivoDescarte motivoDescarte = new MotivoDescarte(conteudoBruto);
        String sanitizado = POLICY.sanitize(conteudoBruto, motivoDescarte, null);

        if (motivoDescarte.mensagem != null) {
            throw new ValidationException(motivoDescarte.mensagem);
        }
        return sanitizado;
    }

    // Guarda o primeiro motivo de descarte para lançar a exception depois que
    // sanitize() retornar (não dentro do callback, ver decisão do agente security).
    // Tag <a> sem href válido (ausente ou esquema não permitido) é reportada pelo
    // OWASP como discardedTag, não discardedAttributes — por isso a distinção de
    // mensagem é feita inspecionando o markup original que gerou o descarte.
    private static class MotivoDescarte implements HtmlChangeListener<Void> {
        private final String conteudoOriginal;
        private String mensagem;

        MotivoDescarte(String conteudoOriginal) {
            this.conteudoOriginal = conteudoOriginal;
        }

        @Override
        public void discardedTag(Void context, String elementName) {
            if (mensagem != null) {
                return;
            }
            if ("a".equals(elementName)) {
                mensagem = motivoDescarteAncora();
            } else {
                mensagem = "Markup contém tag não permitida: <" + elementName + ">";
            }
        }

        @Override
        public void discardedAttributes(Void context, String tagName, String... attributeNames) {
            // Não utilizado pelo fluxo do HtmlPolicyBuilder (atributo obrigatório
            // ausente/inválido remove a tag inteira via discardedTag), mantido só
            // por completude da interface.
        }

        private String motivoDescarteAncora() {
            Matcher matcher = HREF_ATRIBUTO.matcher(conteudoOriginal);
            if (matcher.find()) {
                return "Link em markup contém esquema não permitido";
            }
            return "Tag <a> precisa de atributo href";
        }
    }
}
