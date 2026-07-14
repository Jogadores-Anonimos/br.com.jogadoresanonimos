package br.com.jogadoresanonimos.escritorio.dto;

import br.com.jogadoresanonimos.escritorio.entity.EscritorioServico;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EscritorioServicoResponse(UUID id, String nome, String endereco, String complemento, String bairro,
                                         String cidade, List<String> emails,
                                         List<EscritorioTelefoneResponse> telefones, int ordem,
                                         Instant criadoEm, Instant atualizadoEm) {

    public static EscritorioServicoResponse from(EscritorioServico escritorio) {
        return new EscritorioServicoResponse(escritorio.getId(), escritorio.getNome(), escritorio.getEndereco(),
                escritorio.getComplemento(), escritorio.getBairro(), escritorio.getCidade(), escritorio.getEmails(),
                escritorio.getTelefones().stream().map(EscritorioTelefoneResponse::from).toList(),
                escritorio.getOrdem(), escritorio.getCriadoEm(), escritorio.getAtualizadoEm());
    }
}
