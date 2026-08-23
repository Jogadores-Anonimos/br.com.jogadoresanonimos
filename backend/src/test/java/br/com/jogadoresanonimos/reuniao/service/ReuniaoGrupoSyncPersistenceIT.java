package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.escritorio.dto.AgendaItemRequest;
import br.com.jogadoresanonimos.escritorio.dto.CriarGrupoRequest;
import br.com.jogadoresanonimos.escritorio.dto.EditarGrupoRequest;
import br.com.jogadoresanonimos.escritorio.dto.GrupoIntergrupoResponse;
import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.GrupoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.service.EscritorioRegionalService;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoAgendaItemRequest;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("qa-integration")
@Transactional
class ReuniaoGrupoSyncPersistenceIT {

    @Autowired EscritorioRegionalService escritorioService;
    @Autowired ReuniaoService reuniaoService;
    @Autowired EstadoIntergrupoRepository estadoRepository;
    @Autowired GrupoIntergrupoRepository grupoRepository;
    @Autowired ReuniaoRepository reuniaoRepository;
    @Autowired EntityManager entityManager;

    @Test
    void criarEditarNosDoisFluxosEExcluirDeveManterAgregadoCoerente() {
        var estado = estadoRepository.findAll().getFirst();
        String uf = estado.getUf().name();
        GrupoIntergrupoResponse grupoCriado = escritorioService.criarGrupo(estado.getId(),
                new CriarGrupoRequest("Grupo Sync QA", "Rua Inicial, 10", "Centro", "Cidade QA",
                        List.of(new AgendaItemRequest("SEGUNDA", "18h")), List.of(),
                        "01310-100", "10", "Rua Inicial", uf, -23.5, -46.6));
        entityManager.flush();
        entityManager.clear();

        Reuniao reuniaoCriada = reuniaoRepository.findByGrupoIntergrupoId(grupoCriado.id()).orElseThrow();
        assertThat(reuniaoCriada.getNome()).isEqualTo("Grupo Sync QA");
        assertThat(reuniaoCriada.getLocal()).isEqualTo("Rua Inicial, 10");
        assertThat(reuniaoCriada.getCep()).isEqualTo("01310-100");
        assertThat(reuniaoCriada.getNumero()).isEqualTo("10");
        assertThat(reuniaoCriada.getLogradouro()).isEqualTo("Rua Inicial");
        assertThat(reuniaoCriada.getLatitude()).isEqualTo(-23.5);
        assertThat(reuniaoCriada.getLongitude()).isEqualTo(-46.6);
        assertThat(reuniaoCriada.getPeriodo()).isEqualTo("Noite");

        escritorioService.editarGrupo(estado.getId(), grupoCriado.id(),
                new EditarGrupoRequest("Grupo Sync Endereços", "Rua Nova, 20", "Bairro Novo", "Cidade Nova",
                        List.of(new AgendaItemRequest("SEGUNDA", "20h"),
                                new AgendaItemRequest("SEGUNDA", "18h")), List.of(),
                        null, null, null, uf, null, null, grupoCriado.atualizadoEm()));
        entityManager.flush();
        entityManager.clear();

        Reuniao aposEndereco = reuniaoRepository.findByGrupoIntergrupoId(grupoCriado.id()).orElseThrow();
        assertThat(aposEndereco.getNome()).isEqualTo("Grupo Sync Endereços");
        assertThat(aposEndereco.getAgenda()).extracting("diaSemana", "horario")
                .containsExactlyInAnyOrder(tuple(br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao.SEGUNDA, "18h"),
                        tuple(br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao.SEGUNDA, "20h"));
        assertThat(aposEndereco.getCep()).isNull();
        assertThat(aposEndereco.getNumero()).isNull();
        assertThat(aposEndereco.getLogradouro()).isNull();
        assertThat(aposEndereco.getLatitude()).isNull();
        assertThat(aposEndereco.getLongitude()).isNull();

        reuniaoService.editar(aposEndereco.getId(), new EditarReuniaoRequest(
                "Grupo Sync Reuniões", CategoriaReuniao.presencial, "Descrição atualizada",
                "Terça", "19h", "Avenida Canônica, 30", null, "Terça", null,
                null, null, null, null, null, "Bairro Canônico", "Cidade Canônica", uf, null,
                List.of(new ReuniaoAgendaItemRequest("TERCA", "19h")), aposEndereco.getAtualizadoEm()));
        entityManager.flush();
        entityManager.clear();

        GrupoIntergrupo grupoEspelhado = grupoRepository.findById(grupoCriado.id()).orElseThrow();
        assertThat(grupoEspelhado.getNome()).isEqualTo("Grupo Sync Reuniões");
        assertThat(grupoEspelhado.getEndereco()).isEqualTo("Avenida Canônica, 30");
        assertThat(grupoEspelhado.getAgenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple(br.com.jogadoresanonimos.escritorio.entity.DiaSemana.TERCA, "19h"));

        Reuniao vinculada = reuniaoRepository.findByGrupoIntergrupoId(grupoCriado.id()).orElseThrow();
        reuniaoService.remover(vinculada.getId());
        entityManager.flush();

        assertThat(reuniaoRepository.findById(vinculada.getId())).isEmpty();
        assertThat(grupoRepository.findById(grupoCriado.id())).isEmpty();
    }
}
