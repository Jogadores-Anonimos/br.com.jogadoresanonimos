package br.com.jogadoresanonimos.imagem.service;

import br.com.jogadoresanonimos.common.exception.UnsupportedMediaException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Valida tipo real do arquivo por magic bytes (nunca por extensão/Content-Type
 * declarado) e remove metadados (EXIF) reencodando a imagem via ImageIO —
 * reencodar descarta os metadados originais, incluindo geolocalização/
 * dispositivo, sem depender de biblioteca externa de manipulação de EXIF.
 *
 * LIMITAÇÃO CONHECIDA: ImageIO puro do JDK não lê WebP nativamente (sem
 * plugin adicional) — hoje o magic-byte check aceita WebP no contrato, mas
 * o ImageIO.read() vai falhar e cair em UnsupportedMediaException. Antes de
 * liberar upload de WebP em produção é preciso decidir com o time: (a)
 * adicionar uma lib de imagem (ex. TwelveMonkeys ImageIO plugin — dependência
 * nova, precisa aprovação) ou (b) restringir upload a jpeg/png só nesta fase.
 * Documentado aqui para não ser surpresa depois — não decidido sozinho.
 */
@Component
public class ImagemProcessor {

    public record ImagemProcessada(byte[] bytes, String contentType, String extensao) {
    }

    public ImagemProcessada processar(byte[] original) {
        TipoImagem tipo = detectarTipo(original);
        BufferedImage imagem;
        try {
            imagem = ImageIO.read(new ByteArrayInputStream(original));
        } catch (IOException e) {
            throw new UnsupportedMediaException("Não foi possível processar o arquivo enviado");
        }
        if (imagem == null) {
            throw new UnsupportedMediaException("Não foi possível processar o arquivo enviado");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            boolean escreveu = ImageIO.write(imagem, tipo.formatoImageIO(), out);
            if (!escreveu) {
                throw new UnsupportedMediaException("Formato de imagem não suportado para reprocessamento");
            }
            return new ImagemProcessada(out.toByteArray(), tipo.contentType(), tipo.extensao());
        } catch (IOException e) {
            throw new UnsupportedMediaException("Não foi possível processar o arquivo enviado");
        }
    }

    private TipoImagem detectarTipo(byte[] bytes) {
        if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF) {
            return TipoImagem.JPEG;
        }
        if (bytes.length >= 8 && (bytes[0] & 0xFF) == 0x89 && bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
            return TipoImagem.PNG;
        }
        if (bytes.length >= 12 && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return TipoImagem.WEBP;
        }
        throw new UnsupportedMediaException("Tipo de arquivo não permitido — apenas jpeg, png ou webp");
    }

    private enum TipoImagem {
        JPEG("jpg", "image/jpeg"),
        PNG("png", "image/png"),
        WEBP("png", "image/png");

        private final String extensao;
        private final String contentType;

        TipoImagem(String extensao, String contentType) {
            this.extensao = extensao;
            this.contentType = contentType;
        }

        String formatoImageIO() {
            return extensao.equals("jpg") ? "jpg" : "png";
        }

        String extensao() {
            return extensao;
        }

        String contentType() {
            return contentType;
        }
    }
}
