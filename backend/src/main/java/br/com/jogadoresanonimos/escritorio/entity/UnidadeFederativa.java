package br.com.jogadoresanonimos.escritorio.entity;

import java.util.Arrays;
import java.util.Optional;

public enum UnidadeFederativa {
    AC("Acre", RegiaoCodigo.NORTE),
    AL("Alagoas", RegiaoCodigo.NORDESTE),
    AP("Amapá", RegiaoCodigo.NORTE),
    AM("Amazonas", RegiaoCodigo.NORTE),
    BA("Bahia", RegiaoCodigo.NORDESTE),
    CE("Ceará", RegiaoCodigo.NORDESTE),
    DF("Distrito Federal", RegiaoCodigo.CENTRO_OESTE),
    ES("Espírito Santo", RegiaoCodigo.SUDESTE),
    GO("Goiás", RegiaoCodigo.CENTRO_OESTE),
    MA("Maranhão", RegiaoCodigo.NORDESTE),
    MT("Mato Grosso", RegiaoCodigo.CENTRO_OESTE),
    MS("Mato Grosso do Sul", RegiaoCodigo.CENTRO_OESTE),
    MG("Minas Gerais", RegiaoCodigo.SUDESTE),
    PA("Pará", RegiaoCodigo.NORTE),
    PB("Paraíba", RegiaoCodigo.NORDESTE),
    PR("Paraná", RegiaoCodigo.SUL),
    PE("Pernambuco", RegiaoCodigo.NORDESTE),
    PI("Piauí", RegiaoCodigo.NORDESTE),
    RJ("Rio de Janeiro", RegiaoCodigo.SUDESTE),
    RN("Rio Grande do Norte", RegiaoCodigo.NORDESTE),
    RS("Rio Grande do Sul", RegiaoCodigo.SUL),
    RO("Rondônia", RegiaoCodigo.NORTE),
    RR("Roraima", RegiaoCodigo.NORTE),
    SC("Santa Catarina", RegiaoCodigo.SUL),
    SP("São Paulo", RegiaoCodigo.SUDESTE),
    SE("Sergipe", RegiaoCodigo.NORDESTE),
    TO("Tocantins", RegiaoCodigo.NORTE);

    private final String nomeCompleto;
    private final RegiaoCodigo codigoRegiao;

    UnidadeFederativa(String nomeCompleto, RegiaoCodigo codigoRegiao) {
        this.nomeCompleto = nomeCompleto;
        this.codigoRegiao = codigoRegiao;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public RegiaoCodigo getCodigoRegiao() {
        return codigoRegiao;
    }

    public static Optional<UnidadeFederativa> deSigla(String sigla) {
        if (sigla == null) {
            return Optional.empty();
        }
        String normalizada = sigla.trim().toUpperCase();
        return Arrays.stream(values()).filter(uf -> uf.name().equals(normalizada)).findFirst();
    }

    public static Optional<UnidadeFederativa> deNomeCompleto(String nome) {
        if (nome == null) {
            return Optional.empty();
        }
        String normalizado = nome.trim();
        return Arrays.stream(values()).filter(uf -> uf.nomeCompleto.equalsIgnoreCase(normalizado)).findFirst();
    }
}
