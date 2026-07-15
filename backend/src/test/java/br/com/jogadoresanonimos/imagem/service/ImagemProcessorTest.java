package br.com.jogadoresanonimos.imagem.service;

import br.com.jogadoresanonimos.common.exception.UnsupportedMediaException;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImagemProcessorTest {

    private static final String WEBP_LOSSLESS_4X4_VALIDO_BASE64 =
            "UklGRh4AAABXRUJQVlA4TBEAAAAvA8AAAAdQnuIVuf+BiOh/AAA=";

    private final ImagemProcessor processor = new ImagemProcessor();

    @Test
    void processar_deveAceitarJpegValidoEManterFormato() throws Exception {
        byte[] jpeg = gerarImagemComoBytes("jpg");
        var resultado = processor.processar(jpeg);
        assertThat(resultado.contentType()).isEqualTo("image/jpeg");
        assertThat(resultado.extensao()).isEqualTo("jpg");
    }

    @Test
    void processar_deveAceitarPngValidoEManterFormato() throws Exception {
        byte[] png = gerarImagemComoBytes("png");
        var resultado = processor.processar(png);
        assertThat(resultado.contentType()).isEqualTo("image/png");
        assertThat(resultado.extensao()).isEqualTo("png");
    }

    @Test
    void processar_deveAceitarWebpValidoEReencodarComoPng() {
        byte[] webp = Base64.getDecoder().decode(WEBP_LOSSLESS_4X4_VALIDO_BASE64);
        var resultado = processor.processar(webp);
        assertThat(resultado.contentType()).isEqualTo("image/png");
        assertThat(resultado.extensao()).isEqualTo("png");
        assertThat(resultado.bytes()).isNotEmpty();
        assertThat(assinaturaPng(resultado.bytes())).isTrue();
    }

    @Test
    void processar_deveRejeitarArquivoComExtensaoWebpMasSemMagicBytesReais() {
        byte[] fakeWebp = "isto nao e um webp de verdade, so tem extensao .webp".getBytes();
        assertThatThrownBy(() -> processor.processar(fakeWebp))
                .isInstanceOf(UnsupportedMediaException.class);
    }

    @Test
    void processar_deveRejeitarArquivoComRiffValidoMasSemAssinaturaWebpInterna() {
        byte[] riffFalsoWebp = new byte[16];
        riffFalsoWebp[0] = 'R';
        riffFalsoWebp[1] = 'I';
        riffFalsoWebp[2] = 'F';
        riffFalsoWebp[3] = 'F';
        riffFalsoWebp[8] = 'A';
        riffFalsoWebp[9] = 'V';
        riffFalsoWebp[10] = 'I';
        riffFalsoWebp[11] = ' ';
        assertThatThrownBy(() -> processor.processar(riffFalsoWebp))
                .isInstanceOf(UnsupportedMediaException.class);
    }

    @Test
    void processar_deveRejeitarWebpComMagicBytesValidosMasPayloadCorrompido() {
        byte[] webp = Base64.getDecoder().decode(WEBP_LOSSLESS_4X4_VALIDO_BASE64);
        byte[] corrompido = webp.clone();
        corrompido[20] = (byte) 0xFF;
        corrompido[21] = (byte) 0xFF;
        corrompido[22] = (byte) 0xFF;
        corrompido[23] = (byte) 0xFF;
        assertThatThrownBy(() -> processor.processar(corrompido))
                .isInstanceOf(UnsupportedMediaException.class);
    }

    @Test
    void processar_deveRejeitarWebpTruncadoLogoAposMagicBytes() {
        byte[] webp = Base64.getDecoder().decode(WEBP_LOSSLESS_4X4_VALIDO_BASE64);
        byte[] truncado = new byte[12];
        System.arraycopy(webp, 0, truncado, 0, 12);
        assertThatThrownBy(() -> processor.processar(truncado))
                .isInstanceOf(UnsupportedMediaException.class);
    }

    @Test
    void processar_deveRejeitarArquivoSemMagicBytesConhecidos() {
        byte[] bytes = "não é uma imagem".getBytes();
        assertThatThrownBy(() -> processor.processar(bytes))
                .isInstanceOf(UnsupportedMediaException.class);
    }

    private byte[] gerarImagemComoBytes(String formato) throws Exception {
        BufferedImage imagem = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(imagem, formato, out);
            return out.toByteArray();
        }
    }

    private boolean assinaturaPng(byte[] bytes) {
        return bytes.length >= 8
                && (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 'P'
                && bytes[2] == 'N'
                && bytes[3] == 'G';
    }
}
