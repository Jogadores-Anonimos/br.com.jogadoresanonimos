package br.com.jogadoresanonimos.reuniao.service;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoResponse;
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
        assertThat(ReuniaoResponse.from(persistida).agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("SEGUNDA", "19h"), tuple("QUINTA", "20h"));
        assertThat(persistida.getDia()).isEqualTo("Segunda");
        assertThat(persistida.getHorario()).isEqualTo("19h");
        assertThat(persistida.getDiaSemana()).isEqualTo("Segunda");
    }
    @Test
    void payloadNovoDevePersistirMultiplosHorariosNoMesmoDia() throws Exception {
        CriarReuniaoRequest request = objectMapper.readValue("""
                {"nome":"Grupo QA Multi","categoria":"presencial","descricao":"Massa sintética","local":"Local sintético","agenda":[{"diaSemana":"SEGUNDA","horario":"20h"},{"diaSemana":"SEGUNDA","horario":"18h"},{"diaSemana":"QUINTA","horario":"19h"}]}
                """, CriarReuniaoRequest.class);
        assertThat(validator.validate(request)).isEmpty();
        var criado = reuniaoService.criar(request);
        entityManager.flush();
        entityManager.clear();
        Reuniao persistida = reuniaoRepository.findById(criado.id()).orElseThrow();
        assertThat(ReuniaoResponse.from(persistida).agenda()).extracting("diaSemana", "horario")
                .containsExactly(
                        tuple("SEGUNDA", "18h"), tuple("SEGUNDA", "20h"), tuple("QUINTA", "19h"));
        assertThat(persistida.getHorario()).isEqualTo("18h");
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
        assertThat(ReuniaoResponse.from(persistida).agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("TERCA", "21h"));
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
        assertThat(ReuniaoResponse.from(persistida).agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("TERCA", "19h30"), tuple("SABADO", "10h"));
    }
    @Test
    void edicaoLegadaDevePreservarHorarioAdicionalDoMesmoDia() throws Exception {
        CriarReuniaoRequest criacao = objectMapper.readValue("""
                {"nome":"Grupo QA Legado Multi","categoria":"presencial","descricao":"Massa sintética","local":"Local sintético","agenda":[{"diaSemana":"SEGUNDA","horario":"18h"},{"diaSemana":"SEGUNDA","horario":"20h"},{"diaSemana":"QUINTA","horario":"19h"}]}
                """, CriarReuniaoRequest.class);
        var criada = reuniaoService.criar(criacao);
        entityManager.flush();
        EditarReuniaoRequest edicao = objectMapper.readValue("""
                {"nome":"Grupo QA Legado Multi","categoria":"presencial","descricao":"Massa sintética","dia":"Segunda-feira","diaSemana":"Segunda","horario":"19h","local":"Local sintético"}
                """, EditarReuniaoRequest.class);
        reuniaoService.editar(criada.id(), edicao);
        entityManager.flush();
        entityManager.clear();
        Reuniao persistida = reuniaoRepository.findById(criada.id()).orElseThrow();
        assertThat(ReuniaoResponse.from(persistida).agenda()).extracting("diaSemana", "horario")
                .containsExactly(
                        tuple("SEGUNDA", "19h"), tuple("SEGUNDA", "20h"), tuple("QUINTA", "19h"));
    }
}
