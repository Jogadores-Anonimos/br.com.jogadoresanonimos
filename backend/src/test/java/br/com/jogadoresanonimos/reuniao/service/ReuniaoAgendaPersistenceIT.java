package br.com.jogadoresanonimos.reuniao.service;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
@SpringBootTest
@ActiveProfiles("qa-integration")
@Transactional
class ReuniaoAgendaPersistenceIT {
    @Autowired
    ReuniaoService reuniaoService;
    @Autowired
    ReuniaoRepository reuniaoRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Validator validator;
    @Autowired
    EntityManager entityManager;
    @Test
    void payloadNovoDevePersistirAgendaEAtualizarCamposLegados() throws Exception {
        CriarReuniaoRequest request = objectMapper.readValue("""
                {"nome":"Grupo QA Agenda","categoria":"presencial","descricao":"Massa sintética","local":"Local sintético","agenda":[{"diaSemana":"QUINTA","horario":"20h"},{"diaSemana":"SEGUNDA","horario":"19h"}]}
                """, CriarReuniaoRequest.class);
        assertThat(validator.validate(request)).isEmpty();
        var criado = reuniaoService.criar(request);
        entityManager.flush();
        entityManager.clear();
        Reuniao persistida = reuniaoRepository.findById(criado.id()).orElseThrow();
        assertThat(persistida.getAgenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple(br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao.SEGUNDA, "19h"), tuple(br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao.QUINTA, "20h"));
        assertThat(persistida.getDia()).isEqualTo("Segunda");
        assertThat(persistida.getHorario()).isEqualTo("19h");
        assertThat(persistida.getDiaSemana()).isEqualTo("Segunda");
    }
    @Test
    void payloadLegadoDeveContinuarPersistindoUmaAgenda() throws Exception {
        CriarReuniaoRequest request = objectMapper.readValue("""
                {"nome":"Grupo QA Legado","categoria":"virtual","descricao":"Massa sintética","dia":"Terça-feira","horario":"21h","local":"Sala sintética"}
                """, CriarReuniaoRequest.class);
        assertThat(validator.validate(request)).isEmpty();
        var criado = reuniaoService.criar(request);
        entityManager.flush();
        entityManager.clear();
        Reuniao persistida = reuniaoRepository.findById(criado.id()).orElseThrow();
        assertThat(persistida.getAgenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple(br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao.TERCA, "21h"));
        assertThat(persistida.getDia()).isEqualTo("Terça-feira");
        assertThat(persistida.getHorario()).isEqualTo("21h");
    }
    @Test
    void edicaoNovaDeveSubstituirAgendaPersistida() throws Exception {
        CriarReuniaoRequest criacao = objectMapper.readValue("""
                {"nome":"Grupo QA Edição","categoria":"presencial","descricao":"Massa sintética","dia":"Segunda","horario":"18h","local":"Local sintético"}
                """, CriarReuniaoRequest.class);
        var criada = reuniaoService.criar(criacao);
        entityManager.flush();
        EditarReuniaoRequest edicao = objectMapper.readValue("""
                {"nome":"Grupo QA Edição","categoria":"presencial","descricao":"Massa sintética","local":"Local sintético","agenda":[{"diaSemana":"TERCA","horario":"19h30"},{"diaSemana":"SABADO","horario":"10h"}]}
                """, EditarReuniaoRequest.class);
        assertThat(validator.validate(edicao)).isEmpty();
        reuniaoService.editar(criada.id(), edicao);
        entityManager.flush();
        entityManager.clear();
        Reuniao persistida = reuniaoRepository.findById(criada.id()).orElseThrow();
        assertThat(persistida.getAgenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple(br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao.TERCA, "19h30"), tuple(br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao.SABADO, "10h"));
    }
}
