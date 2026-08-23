package br.com.jogadoresanonimos.reuniao.service;

public record DadosEnderecoReuniao(
        String cep,
        String numero,
        String logradouro,
        String bairro,
        String cidade,
        String estado,
        Double latitude,
        Double longitude,
        boolean estruturaDetalhadaFornecida
) {
}
