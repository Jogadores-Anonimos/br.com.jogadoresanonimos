package br.com.jogadoresanonimos.reuniao.dto;

public record ReuniaoAvisoResponse(String codigo, String mensagem) {

    public static ReuniaoAvisoResponse enderecoLocalDivergente() {
        return new ReuniaoAvisoResponse("ENDERECO_LOCAL_DIVERGENTE",
                "Cidade/estado do endereço estruturado mudaram, mas o campo \"local\" não foi atualizado junto. Confira se o texto ainda reflete o endereço correto.");
    }
}
