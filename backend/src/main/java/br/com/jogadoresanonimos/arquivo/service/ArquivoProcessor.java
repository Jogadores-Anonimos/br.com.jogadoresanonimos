package br.com.jogadoresanonimos.arquivo.service;

import br.com.jogadoresanonimos.common.exception.UnsupportedMediaException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Valida upload de literatura em PDF (spec security 2026-07-12):
 * - magic bytes `%PDF-` estritamente no byte 0 (mais rígido que a spec PDF,
 *   que tolera offset até 1024 — fechamos essa folga de propósito).
 * - sem bytes extra depois do envelope PDF (`%%EOF`), o que descarta
 *   polyglot (arquivo que é PDF válido E outro formato ao mesmo tempo,
 *   ex. PDF+ZIP/PDF+HTML).
 * - parsing estrutural real via PDFBox (não é sniff de string) e allowlist
 *   negativa contra `/JavaScript`, `/OpenAction`, `/AA`, `/EmbeddedFile`,
 *   `/Launch` — recursos que permitem execução de código ou exfiltração
 *   de arquivo embutido.
 * Diferente de ImagemProcessor, NÃO reencoda o PDF (reencodar destruiria
 * o documento) — só valida e passa os bytes originais adiante.
 */
@Component
public class ArquivoProcessor {

    private static final Logger log = LoggerFactory.getLogger(ArquivoProcessor.class);

    private static final byte[] MAGIC = "%PDF-".getBytes(StandardCharsets.ISO_8859_1);
    private static final float THUMBNAIL_DPI = 96f;

    public record ArquivoProcessado(byte[] bytes, String contentType, String extensao, byte[] thumbnailPng) {
    }

    public ArquivoProcessado processar(byte[] original) {
        validarMagicBytesNoInicio(original);

        byte[] thumbnail;
        try (PDDocument documento = Loader.loadPDF(original)) {
            validarSemBytesExtras(original);
            validarSemRecursosPerigosos(documento);
            thumbnail = gerarThumbnail(documento);
        } catch (IOException e) {
            throw new UnsupportedMediaException("Não foi possível processar o arquivo PDF enviado");
        }

        return new ArquivoProcessado(original, "application/pdf", "pdf", thumbnail);
    }

    // Capa/preview pro card de literatura — imagem da primeira página.
    // Falha em gerar não deve derrubar o upload inteiro (a validação de
    // segurança já passou nesse ponto, o PDF em si é válido e seguro);
    // sem thumbnail o card cai de volta pro ícone genérico no frontend.
    private byte[] gerarThumbnail(PDDocument documento) {
        try {
            if (documento.getNumberOfPages() == 0) {
                return null;
            }
            PDFRenderer renderer = new PDFRenderer(documento);
            BufferedImage imagem = renderer.renderImageWithDPI(0, THUMBNAIL_DPI, ImageType.RGB);
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            ImageIO.write(imagem, "png", saida);
            return saida.toByteArray();
        } catch (IOException | RuntimeException e) {
            log.warn("falha_gerar_thumbnail_pdf");
            return null;
        }
    }

    private void validarMagicBytesNoInicio(byte[] bytes) {
        if (bytes.length < MAGIC.length) {
            throw new UnsupportedMediaException("Tipo de arquivo não permitido — apenas PDF");
        }
        for (int i = 0; i < MAGIC.length; i++) {
            if (bytes[i] != MAGIC[i]) {
                throw new UnsupportedMediaException("Tipo de arquivo não permitido — apenas PDF");
            }
        }
    }

    private void validarSemBytesExtras(byte[] original) {
        String texto = new String(original, StandardCharsets.ISO_8859_1);
        int ultimoEof = texto.lastIndexOf("%%EOF");
        if (ultimoEof < 0) {
            throw new UnsupportedMediaException("Arquivo PDF sem marcador de final de documento (%%EOF)");
        }
        int fimLinha = ultimoEof + "%%EOF".length();
        while (fimLinha < original.length && (original[fimLinha] == '\r' || original[fimLinha] == '\n')) {
            fimLinha++;
        }
        if (fimLinha != original.length) {
            throw new UnsupportedMediaException("Arquivo PDF contém dados extras após o final do documento (possível polyglot)");
        }
    }

    private void validarSemRecursosPerigosos(PDDocument documento) throws IOException {
        var catalogo = documento.getDocumentCatalog();

        if (catalogo.getOpenAction() != null) {
            throw new UnsupportedMediaException("PDF rejeitado — contém OpenAction");
        }

        if (catalogo.getCOSObject().containsKey(COSName.getPDFName("AA"))) {
            throw new UnsupportedMediaException("PDF rejeitado — contém ações automáticas (/AA)");
        }

        PDNameTreeNode<?> embeddedFiles = catalogo.getNames() != null ? catalogo.getNames().getEmbeddedFiles() : null;
        if (embeddedFiles != null) {
            throw new UnsupportedMediaException("PDF rejeitado — contém arquivo embutido (/EmbeddedFile)");
        }

        PDPageTree paginas = catalogo.getPages();
        for (PDPage pagina : paginas) {
            if (pagina.getCOSObject().containsKey(COSName.getPDFName("AA"))) {
                throw new UnsupportedMediaException("PDF rejeitado — contém ações automáticas de página (/AA)");
            }
        }

        // Varredura de todos os objetos indiretos do documento — cobre casos
        // em que /JavaScript, /EmbeddedFile ou /Launch aparecem soltos, fora
        // do grafo alcançável a partir do catálogo (ex: objeto órfão inserido
        // deliberadamente para escapar de uma checagem só-pelo-catálogo).
        for (COSObjectKeyEntry entry : todosOsObjetos(documento)) {
            if (!(entry.objeto() instanceof COSDictionary dict)) {
                continue;
            }
            if (dict.containsKey(COSName.getPDFName("JS")) || dict.containsKey(COSName.getPDFName("JavaScript"))) {
                throw new UnsupportedMediaException("PDF rejeitado — contém /JavaScript");
            }
            COSName tipo = dict.getCOSName(COSName.TYPE);
            if (tipo != null && "EmbeddedFile".equals(tipo.getName())) {
                throw new UnsupportedMediaException("PDF rejeitado — contém arquivo embutido (/EmbeddedFile)");
            }
            COSName subtipo = dict.getCOSName(COSName.S);
            if (subtipo != null && ("JavaScript".equals(subtipo.getName()) || "Launch".equals(subtipo.getName()))) {
                throw new UnsupportedMediaException("PDF rejeitado — contém ação /JavaScript ou /Launch");
            }
        }
    }

    private record COSObjectKeyEntry(org.apache.pdfbox.cos.COSBase objeto) {
    }

    private java.util.List<COSObjectKeyEntry> todosOsObjetos(PDDocument documento) {
        java.util.List<COSObjectKeyEntry> resultado = new java.util.ArrayList<>();
        for (var chave : documento.getDocument().getXrefTable().keySet()) {
            COSObject obj = documento.getDocument().getObjectFromPool(chave);
            if (obj != null && obj.getObject() != null) {
                resultado.add(new COSObjectKeyEntry(obj.getObject()));
            }
        }
        return resultado;
    }
}
