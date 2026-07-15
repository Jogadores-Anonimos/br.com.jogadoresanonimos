package br.com.jogadoresanonimos.escritorio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class AgendaItemGrupo {

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DiaSemana diaSemana;

    @Column(nullable = false, length = 50)
    private String horario;

    protected AgendaItemGrupo() {
    }

    public AgendaItemGrupo(DiaSemana diaSemana, String horario) {
        this.diaSemana = diaSemana;
        this.horario = horario;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public String getHorario() {
        return horario;
    }
}
