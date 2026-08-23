package br.com.jogadoresanonimos.escritorio.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("qa-integration")
@Transactional
class GrupoIntergrupoAgendaPersistenceIT {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void v13DevePreservarAgendaExistenteEPermitirSegundoHorarioNoMesmoDia() {
        Map<String, Object> existente = jdbcTemplate.queryForMap("""
                SELECT grupo_intergrupo_id, dia_semana, horario
                FROM grupo_intergrupo_horario
                ORDER BY grupo_intergrupo_id, dia_semana
                LIMIT 1
                """);
        UUID grupoId = (UUID) existente.get("grupo_intergrupo_id");
        String diaSemana = (String) existente.get("dia_semana");
        String horarioOriginal = (String) existente.get("horario");
        Integer quantidadeAntes = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM grupo_intergrupo_horario", Integer.class);

        String horarioAdicional = "23h59 QA V13";
        jdbcTemplate.update("""
                INSERT INTO grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario)
                VALUES (?, ?, ?)
                """, grupoId, diaSemana, horarioAdicional);

        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM grupo_intergrupo_horario", Integer.class))
                .isEqualTo(quantidadeAntes + 1);
        assertThat(jdbcTemplate.queryForObject("""
                SELECT count(*) FROM grupo_intergrupo_horario
                WHERE grupo_intergrupo_id = ? AND dia_semana = ? AND horario = ?
                """, Integer.class, grupoId, diaSemana, horarioOriginal)).isOne();
        assertThatThrownBy(() -> jdbcTemplate.update("""
                INSERT INTO grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario)
                VALUES (?, ?, ?)
                """, grupoId, diaSemana, horarioAdicional))
                .hasMessageContaining("grupo_intergrupo_horario_pkey");
    }
}
