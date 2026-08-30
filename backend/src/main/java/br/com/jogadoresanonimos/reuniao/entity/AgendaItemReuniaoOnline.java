package br.com.jogadoresanonimos.reuniao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class AgendaItemReuniaoOnline {
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DiaSemanaReuniao diaSemana;
    @Column(nullable = false, length = 50)
    private String horario;
    protected AgendaItemReuniaoOnline() {
    }
    public AgendaItemReuniaoOnline(DiaSemanaReuniao diaSemana, String horario) {
        this.diaSemana = diaSemana;
        this.horario = horario;
    }
    public DiaSemanaReuniao getDiaSemana() {
        return diaSemana;
    }
    public String getHorario() {
        return horario;
    }
}
