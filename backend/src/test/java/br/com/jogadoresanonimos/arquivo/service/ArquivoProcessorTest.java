package br.com.jogadoresanonimos.arquivo.service;

import br.com.jogadoresanonimos.common.exception.UnsupportedMediaException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionLaunch;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArquivoProcessorTest {

    private final ArquivoProcessor processor = new ArquivoProcessor();

    @Test
    void processar_deveAceitarPdfValidoSimples() throws Exception {
        byte[] pdf = gerarPdfSimples();

        var resultado = processor.processar(pdf);

        assertThat(resultado.contentType()).isEqualTo("application/pdf");
        assertThat(resultado.extensao()).isEqualTo("pdf");
    }

    @Test
    void processar_deveRejeitarArquivoSemMagicBytesNoInicio() {
        byte[] bytes = "não é um pdf".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> processor.processar(bytes))
                .isInstanceOf(UnsupportedMediaException.class);
    }

    @Test
    void processar_deveRejeitarPdfComMagicBytesForaDoOffsetZero() {
        byte[] pdfValido = "%PDF-1.7\n%%EOF".getBytes(StandardCharsets.UTF_8);
        byte[] comLixoNoInicio = new byte[10 + pdfValido.length];
        System.arraycopy(pdfValido, 0, comLixoNoInicio, 10, pdfValido.length);

        assertThatThrownBy(() -> processor.processar(comLixoNoInicio))
                .isInstanceOf(UnsupportedMediaException.class);
    }

    @Test
    void processar_deveRejeitarBytesExtrasAposEof() throws Exception {
        byte[] pdf = gerarPdfSimples();
        byte[] lixo = "<script>oi</script>".getBytes(StandardCharsets.UTF_8);
        byte[] comLixoDepois = new byte[pdf.length + lixo.length];
        System.arraycopy(pdf, 0, comLixoDepois, 0, pdf.length);
        System.arraycopy(lixo, 0, comLixoDepois, pdf.length, lixo.length);

        assertThatThrownBy(() -> processor.processar(comLixoDepois))
                .isInstanceOf(UnsupportedMediaException.class);
    }

    @Test
    void processar_deveRejeitarPdfComOpenAction() throws Exception {
        try (PDDocument documento = new PDDocument()) {
            documento.addPage(new PDPage());
            documento.getDocumentCatalog().setOpenAction(new PDActionJavaScript("app.alert('oi')"));
            byte[] pdf = escrever(documento);

            assertThatThrownBy(() -> processor.processar(pdf))
                    .isInstanceOf(UnsupportedMediaException.class);
        }
    }

    @Test
    void processar_deveRejeitarPdfComEmbeddedFile() throws Exception {
        try (PDDocument documento = new PDDocument()) {
            documento.addPage(new PDPage());

            PDEmbeddedFile embedded = new PDEmbeddedFile(documento, new java.io.ByteArrayInputStream("conteudo".getBytes()));
            PDComplexFileSpecification fs = new PDComplexFileSpecification();
            fs.setFile("malicioso.txt");
            fs.setEmbeddedFile(embedded);

            PDEmbeddedFilesNameTreeNode tree = new PDEmbeddedFilesNameTreeNode();
            Map<String, PDComplexFileSpecification> names = new HashMap<>();
            names.put("malicioso.txt", fs);
            tree.setNames(names);

            var namesDict = new org.apache.pdfbox.pdmodel.PDDocumentNameDictionary(documento.getDocumentCatalog());
            namesDict.setEmbeddedFiles(tree);
            documento.getDocumentCatalog().setNames(namesDict);

            byte[] pdf = escrever(documento);

            assertThatThrownBy(() -> processor.processar(pdf))
                    .isInstanceOf(UnsupportedMediaException.class);
        }
    }

    @Test
    void processar_deveRejeitarPdfComLaunchAction() throws Exception {
        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);
            documento.getDocumentCatalog().setOpenAction(new PDActionLaunch());
            byte[] pdf = escrever(documento);

            assertThatThrownBy(() -> processor.processar(pdf))
                    .isInstanceOf(UnsupportedMediaException.class);
        }
    }

    private byte[] gerarPdfSimples() throws Exception {
        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);
            try (PDPageContentStream stream = new PDPageContentStream(documento, pagina)) {
                stream.beginText();
                stream.newLineAtOffset(50, 700);
                stream.endText();
            }
            return escrever(documento);
        }
    }

    private byte[] escrever(PDDocument documento) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            documento.save(out);
            return out.toByteArray();
        }
    }
}
