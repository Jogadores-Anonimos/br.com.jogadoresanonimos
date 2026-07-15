package br.com.jogadoresanonimos.cms.util;

import br.com.jogadoresanonimos.common.exception.ValidationException;
import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlChangeListener;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HtmlSanitizer {

    private static final Pattern SAFE_HREF = Pattern.compile("^(https?://|/(?!/))[^\"'<>\\s]*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern HREF_ATRIBUTO = Pattern.compile("<a\\b[^>]*\\bhref\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

    private static final AttributePolicy HREF_POLICY = (elementName, attributeName, value) ->
            SAFE_HREF.matcher(value).matches() ? value : null;

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
