package br.com.jogadoresanonimos.cms.util;

import br.com.jogadoresanonimos.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HtmlSanitizerTest {

    private final HtmlSanitizer sanitizer = new HtmlSanitizer();

    @Test
    void devePermitirTagsDaAllowlist() {
        String entrada = "<p>texto com <b>negrito</b> e <i>itálico</i><br></p>";
        String esperado = "<p>texto com <b>negrito</b> e <i>itálico</i><br /></p>";
        assertThat(sanitizer.sanitizar(entrada)).isEqualTo(esperado);
    }

    @Test
    void devePermitirLinkComHrefHttpsSeguro() {
        String entrada = "<a href=\"https://jogadoresanonimos.org.br/ajuda\">clique aqui</a>";
        assertThat(sanitizer.sanitizar(entrada)).isEqualTo(entrada);
    }

    @Test
    void deveRejeitarTagForaDaAllowlist() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<script>alert(1)</script>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deveRejeitarLinkComEsquemaJavascript() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<a href=\"javascript:alert(1)\">clique</a>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deveRejeitarAncoraSemHref() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<a>sem link</a>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deveRejeitarImgComOnerror() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<img src=x onerror=alert(1)>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deveEscaparTextoPuroComScriptDigitadoSemFormaDeTag() {
        String entrada = "<p>relato: 1 &lt; 2, cuidado com &#34;script&#34; alert(1)</p>";
        String resultado = sanitizer.sanitizar("<p>relato: 1 < 2, cuidado com \"script\" alert(1)</p>");
        assertThat(resultado).isEqualTo(entrada);
    }

    @Test
    void deveEscaparAmpersandSoltoNoTextoPuro() {
        assertThat(sanitizer.sanitizar("<p>Alcoólicos & Jogadores Anônimos</p>"))
                .isEqualTo("<p>Alcoólicos &amp; Jogadores Anônimos</p>");
    }

    @Test
    void naoDeveDuploEscaparEntidadeJaEscapada() {
        assertThat(sanitizer.sanitizar("<p>&amp; &lt; &gt; &quot; &#x27;</p>"))
                .isEqualTo("<p>&amp; &lt; &gt; &#34; &#39;</p>");
    }

    @Test
    void deveEscaparAspaSimplesNoTextoPuro() {
        assertThat(sanitizer.sanitizar("<p>d'água</p>"))
                .isEqualTo("<p>d&#39;água</p>");
    }

    @Test
    void deveFecharTagComFechamentoCruzado() {
        String resultado = sanitizer.sanitizar("<b><i>texto</b></i>");
        assertThat(resultado).isEqualTo("<b><i>texto</i></b>");
    }

    @Test
    void deveTratarAberturaDuplicadaDeParagrafoSemFechamento() {
        String resultado = sanitizer.sanitizar("<p><p>texto</p>");
        assertThat(resultado).isEqualTo("<p></p><p>texto</p>");
    }

    @Test
    void deveFecharTagAbertaNuncaFechadaNoFimDaString() {
        String resultado = sanitizer.sanitizar("<p>texto sem fechamento");
        assertThat(resultado).isEqualTo("<p>texto sem fechamento</p>");
    }

    @Test
    void deveRemoverAtributoOnclickDeParagrafoPermitidoSemLancarExcecao() {
        String resultado = sanitizer.sanitizar("<p onclick=\"alert(1)\">texto</p>");
        assertThat(resultado).isEqualTo("<p>texto</p>");
    }

    @Test
    void deveRemoverAtributoOnmouseoverDeAncoraValidaMantendoHref() {
        String resultado = sanitizer.sanitizar("<a href=\"https://x.com\" onmouseover=\"alert(1)\">link</a>");
        assertThat(resultado).isEqualTo("<a href=\"https://x.com\">link</a>");
    }

    @Test
    void deveRemoverAtributoStyleComJavascriptDeNegritoPermitido() {
        String resultado = sanitizer.sanitizar("<b style=\"background:url(javascript:alert(1))\">negrito</b>");
        assertThat(resultado).isEqualTo("<b>negrito</b>");
    }

    @Test
    void deveRejeitarHrefComEsquemaDisfarcadoEmEntityDecimal() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<a href=\"jav&#97;script:alert(1)\">clique</a>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deveRejeitarHrefComEsquemaDisfarcadoEmEntityDecimalNoInicio() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<a href=\"&#106;avascript:alert(1)\">clique</a>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void devePreservarEntidadesNumericasEHexMisturadasNoTextoPuro() {
        String resultado = sanitizer.sanitizar("<p>caf&#233; com a&#x87;o</p>");
        assertThat(resultado).isEqualTo("<p>café com ao</p>");
    }

    @Test
    void deveRejeitarTagComNomeQuebradoPorAninhamentoDeScript() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<p><scr<script>ipt>alert(1)</scr</script>ipt></p>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deveRejeitarScriptAninhadoDentroDeAncoraValida() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<a href=\"https://x.com\"><script>alert(1)</script></a>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void devePermitirHrefRelativoComBarraInicial() {
        String entrada = "<a href=\"/pagina\">link</a>";
        assertThat(sanitizer.sanitizar(entrada)).isEqualTo(entrada);
    }

    @Test
    void deveRejeitarHrefProtocolRelativo() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<a href=\"//evil.com\">link</a>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deveRejeitarHrefSemBarraENemEsquema() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<a href=\"caminho-sem-barra\">link</a>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void deveRejeitarHrefComEsquemaData() {
        assertThatThrownBy(() -> sanitizer.sanitizar("<a href=\"data:text/html,<script>alert(1)</script>\">link</a>"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void devePermitirHeadingsStrongEListas() {
        String entrada = "<h2>Título</h2><h3>Subtítulo</h3><p><strong>destaque</strong></p>"
                + "<ul><li>item a</li></ul><ol><li>item 1</li></ol>";
        assertThat(sanitizer.sanitizar(entrada)).isEqualTo(entrada);
    }

    @Test
    void deveRemoverAtributoOnclickDeHeadingPermitidoSemLancarExcecao() {
        String resultado = sanitizer.sanitizar("<h2 onclick=\"alert(1)\">título</h2>");
        assertThat(resultado).isEqualTo("<h2>título</h2>");
    }
}
