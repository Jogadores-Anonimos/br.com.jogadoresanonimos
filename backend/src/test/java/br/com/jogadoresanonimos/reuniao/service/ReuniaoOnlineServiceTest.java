package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.imagem.entity.Imagem;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineAgendaItemRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineResponse;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniaoOnline;
import br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.ReuniaoOnline;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoOnlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoOnlineServiceTest {

    private static final String TELEFONE_INSTITUCIONAL = "21 99472-1933";

    @Mock
    ReuniaoOnlineRepository reuniaoOnlineRepository;

    @Mock
    ImagemRepository imagemRepository;

    @Mock
    StorageClient storageClient;

    ReuniaoOnlineService reuniaoOnlineService;

    @BeforeEach
    void setUp() {
        reuniaoOnlineService = new ReuniaoOnlineService(reuniaoOnlineRepository, imagemRepository, storageClient,
                TELEFONE_INSTITUCIONAL);
    }

    private ReuniaoOnline reuniaoOnline() {
        return new ReuniaoOnline("Grupo Online Segunda", TELEFONE_INSTITUCIONAL, "descricao", null, null,
                List.of(new AgendaItemReuniaoOnline(DiaSemanaReuniao.SEGUNDA, "19h")));
    }

    private CriarReuniaoOnlineRequest criarRequest() {
        return new CriarReuniaoOnlineRequest("Grupo Online Segunda", "11 90000-0000", "descricao", null, null,
                List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h")));
    }

    private EditarReuniaoOnlineRequest editarRequest() {
        return new EditarReuniaoOnlineRequest("Grupo Online Editado", "11 90000-0000", "nova descricao", null, null,
                List.of(new ReuniaoOnlineAgendaItemRequest("TERCA", "20h")));
    }

    @Test
    void listarTodas_deveDelegarParaRepositoryEMapear() {
        when(reuniaoOnlineRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(reuniaoOnline()));
        List<ReuniaoOnlineResponse> resultado = reuniaoOnlineService.listarTodas();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Grupo Online Segunda");
    }

    @Test
    void criar_deveGravarTelefoneInstitucionalIgnorandoTelefoneDoRequest() {
        CriarReuniaoOnlineRequest request = criarRequest();
        when(reuniaoOnlineRepository.save(any(ReuniaoOnline.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoOnlineService.criar(request);
        ArgumentCaptor<ReuniaoOnline> captor = ArgumentCaptor.forClass(ReuniaoOnline.class);
        verify(reuniaoOnlineRepository).save(captor.capture());
        assertThat(captor.getValue().getTelefone()).isEqualTo(TELEFONE_INSTITUCIONAL);
    }

    @Test
    void criar_comTelefoneNuloNoRequest_deveGravarTelefoneInstitucional() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "descricao",
                null, null, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h")));
        when(reuniaoOnlineRepository.save(any(ReuniaoOnline.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoOnlineService.criar(request);
        ArgumentCaptor<ReuniaoOnline> captor = ArgumentCaptor.forClass(ReuniaoOnline.class);
        verify(reuniaoOnlineRepository).save(captor.capture());
        assertThat(captor.getValue().getTelefone()).isEqualTo(TELEFONE_INSTITUCIONAL);
    }

    @Test
    void criar_devePersistirDescricaoNormalizada() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null,
                "  descricao com espacos  ", null, null,
                List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h")));
        when(reuniaoOnlineRepository.save(any(ReuniaoOnline.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoOnlineService.criar(request);
        ArgumentCaptor<ReuniaoOnline> captor = ArgumentCaptor.forClass(ReuniaoOnline.class);
        verify(reuniaoOnlineRepository).save(captor.capture());
        assertThat(captor.getValue().getDescricao()).isEqualTo("descricao com espacos");
    }

    @Test
    void criar_comDescricaoEmBranco_devePersistirNulo() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "   ",
                null, null, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h")));
        when(reuniaoOnlineRepository.save(any(ReuniaoOnline.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoOnlineService.criar(request);
        ArgumentCaptor<ReuniaoOnline> captor = ArgumentCaptor.forClass(ReuniaoOnline.class);
        verify(reuniaoOnlineRepository).save(captor.capture());
        assertThat(captor.getValue().getDescricao()).isNull();
    }

    @Test
    void criar_devePersistirNomeSemEspacosNasBordas() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("  Grupo Online Segunda  ", null,
                "descricao", null, null, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h")));
        when(reuniaoOnlineRepository.save(any(ReuniaoOnline.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoOnlineService.criar(request);
        ArgumentCaptor<ReuniaoOnline> captor = ArgumentCaptor.forClass(ReuniaoOnline.class);
        verify(reuniaoOnlineRepository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Grupo Online Segunda");
    }

    @Test
    void criar_comAgendaDeDiaEHorarioDuplicado_deveRejeitarSemPersistir() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "descricao",
                null, null, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h"),
                        new ReuniaoOnlineAgendaItemRequest("segunda-feira", " 19h ")));
        assertThatThrownBy(() -> reuniaoOnlineService.criar(request))
                .isInstanceOf(ValidationException.class);
        verify(reuniaoOnlineRepository, never()).save(any());
    }

    @Test
    void criar_comAgendaMesmoDiaHorariosDiferentes_devePersistirAmbos() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "descricao",
                null, null, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h"),
                        new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "20h")));
        when(reuniaoOnlineRepository.save(any(ReuniaoOnline.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ReuniaoOnlineResponse resultado = reuniaoOnlineService.criar(request);
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactlyInAnyOrder(tuple("SEGUNDA", "19h"), tuple("SEGUNDA", "20h"));
    }

    @Test
    void criar_comDiaSemanaComAcentoESufixoFeira_deveConverterCorretamente() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Terça", null, "descricao",
                null, null, List.of(new ReuniaoOnlineAgendaItemRequest("Terça-feira", "20h")));
        when(reuniaoOnlineRepository.save(any(ReuniaoOnline.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<ReuniaoOnline> captor = ArgumentCaptor.forClass(ReuniaoOnline.class);
        reuniaoOnlineService.criar(request);
        verify(reuniaoOnlineRepository).save(captor.capture());
        assertThat(captor.getValue().getAgenda()).extracting(AgendaItemReuniaoOnline::getDiaSemana)
                .containsExactly(DiaSemanaReuniao.TERCA);
    }

    @Test
    void criar_comDiaSemanaInvalido_deveRejeitarSemPersistir() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "descricao",
                null, null, List.of(new ReuniaoOnlineAgendaItemRequest("Ferias", "19h")));
        assertThatThrownBy(() -> reuniaoOnlineService.criar(request))
                .isInstanceOf(ValidationException.class);
        verify(reuniaoOnlineRepository, never()).save(any());
    }

    @Test
    void criar_comHorarioEmBranco_deveRejeitarSemPersistir() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "descricao",
                null, null, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "   ")));
        assertThatThrownBy(() -> reuniaoOnlineService.criar(request))
                .isInstanceOf(ValidationException.class);
        verify(reuniaoOnlineRepository, never()).save(any());
    }

    @Test
    void criar_comVideoUrlForaDoPadraoYoutube_deveRejeitarSemPersistir() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "descricao",
                "https://vimeo.com/123", null, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h")));
        assertThatThrownBy(() -> reuniaoOnlineService.criar(request))
                .isInstanceOf(ValidationException.class);
        verify(reuniaoOnlineRepository, never()).save(any());
    }

    @Test
    void criar_comVideoUrlDoYoutube_devePersistir() {
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "descricao",
                "https://youtu.be/abc123", null, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h")));
        when(reuniaoOnlineRepository.save(any(ReuniaoOnline.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ReuniaoOnlineResponse resultado = reuniaoOnlineService.criar(request);
        assertThat(resultado.videoUrl()).isEqualTo("https://youtu.be/abc123");
    }

    @Test
    void criar_comPanfletoImagemIdInexistente_deveRejeitarSemPersistir() {
        UUID imagemId = UUID.randomUUID();
        when(imagemRepository.existsById(imagemId)).thenReturn(false);
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "descricao",
                null, imagemId, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h")));
        assertThatThrownBy(() -> reuniaoOnlineService.criar(request))
                .isInstanceOf(ValidationException.class);
        verify(reuniaoOnlineRepository, never()).save(any());
    }

    @Test
    void criar_comPanfletoImagemIdExistente_devePersistirEResolverUrl() {
        UUID imagemId = UUID.randomUUID();
        Imagem imagem = new Imagem("panfleto.jpg", "chave-panfleto.jpg", 1000L, "alt", UUID.randomUUID());
        when(imagemRepository.existsById(imagemId)).thenReturn(true);
        lenient().when(imagemRepository.findById(imagemId)).thenReturn(Optional.of(imagem));
        lenient().when(storageClient.urlPublicaPara("chave-panfleto.jpg")).thenReturn("https://storage/chave-panfleto.jpg");
        when(reuniaoOnlineRepository.save(any(ReuniaoOnline.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CriarReuniaoOnlineRequest request = new CriarReuniaoOnlineRequest("Grupo Online Segunda", null, "descricao",
                null, imagemId, List.of(new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "19h")));
        ReuniaoOnlineResponse resultado = reuniaoOnlineService.criar(request);
        assertThat(resultado.panfletoImagemId()).isEqualTo(imagemId);
        assertThat(resultado.panfletoImagemUrl()).isEqualTo("https://storage/chave-panfleto.jpg");
    }

    @Test
    void editar_deIdInexistente_deveLancarRecursoNaoEncontradoException() {
        UUID id = UUID.randomUUID();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reuniaoOnlineService.editar(id, editarRequest()))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void editar_deveAplicarTelefoneInstitucionalENovaDescricao() {
        UUID id = UUID.randomUUID();
        ReuniaoOnline reuniaoOnline = reuniaoOnline();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.of(reuniaoOnline));
        ReuniaoOnlineResponse resultado = reuniaoOnlineService.editar(id, editarRequest());
        assertThat(resultado.telefone()).isEqualTo(TELEFONE_INSTITUCIONAL);
        assertThat(resultado.descricao()).isEqualTo("nova descricao");
        assertThat(resultado.nome()).isEqualTo("Grupo Online Editado");
    }

    @Test
    void editar_comAgendaDeDiaEHorarioDuplicado_deveRejeitarSemAlterarEntidade() {
        UUID id = UUID.randomUUID();
        ReuniaoOnline reuniaoOnline = reuniaoOnline();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.of(reuniaoOnline));
        EditarReuniaoOnlineRequest request = new EditarReuniaoOnlineRequest("Grupo Online Editado", null,
                "nova descricao", null, null, List.of(new ReuniaoOnlineAgendaItemRequest("TERCA", "20h"),
                new ReuniaoOnlineAgendaItemRequest("TERCA", "20h")));
        assertThatThrownBy(() -> reuniaoOnlineService.editar(id, request))
                .isInstanceOf(ValidationException.class);
        assertThat(reuniaoOnline.getNome()).isEqualTo("Grupo Online Segunda");
    }

    @Test
    void editar_comDescricaoEmBranco_devePersistirNulo() {
        UUID id = UUID.randomUUID();
        ReuniaoOnline reuniaoOnline = reuniaoOnline();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.of(reuniaoOnline));
        EditarReuniaoOnlineRequest request = new EditarReuniaoOnlineRequest("Grupo Online Editado", null, "   ",
                null, null, List.of(new ReuniaoOnlineAgendaItemRequest("TERCA", "20h")));
        ReuniaoOnlineResponse resultado = reuniaoOnlineService.editar(id, request);
        assertThat(resultado.descricao()).isNull();
    }

    @Test
    void editar_devePermitirConversaoDeDiaSemanaComAcentoESufixoFeira() {
        UUID id = UUID.randomUUID();
        ReuniaoOnline reuniaoOnline = reuniaoOnline();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.of(reuniaoOnline));
        EditarReuniaoOnlineRequest request = new EditarReuniaoOnlineRequest("Grupo Online Editado", null,
                "nova descricao", null, null, List.of(new ReuniaoOnlineAgendaItemRequest("sábado", "10h")));
        ReuniaoOnlineResponse resultado = reuniaoOnlineService.editar(id, request);
        assertThat(resultado.agenda()).extracting("diaSemana")
                .containsExactly("SABADO");
    }

    @Test
    void editar_comVideoUrlForaDoPadraoYoutube_deveRejeitarSemAlterarEntidade() {
        UUID id = UUID.randomUUID();
        ReuniaoOnline reuniaoOnline = reuniaoOnline();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.of(reuniaoOnline));
        EditarReuniaoOnlineRequest request = new EditarReuniaoOnlineRequest("Grupo Online Editado", null,
                "nova descricao", "https://vimeo.com/123", null,
                List.of(new ReuniaoOnlineAgendaItemRequest("TERCA", "20h")));
        assertThatThrownBy(() -> reuniaoOnlineService.editar(id, request))
                .isInstanceOf(ValidationException.class);
        assertThat(reuniaoOnline.getVideoUrl()).isNull();
    }

    @Test
    void editar_comPanfletoImagemIdInexistente_deveRejeitarSemAlterarEntidade() {
        UUID id = UUID.randomUUID();
        UUID imagemId = UUID.randomUUID();
        ReuniaoOnline reuniaoOnline = reuniaoOnline();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.of(reuniaoOnline));
        when(imagemRepository.existsById(imagemId)).thenReturn(false);
        EditarReuniaoOnlineRequest request = new EditarReuniaoOnlineRequest("Grupo Online Editado", null,
                "nova descricao", null, imagemId, List.of(new ReuniaoOnlineAgendaItemRequest("TERCA", "20h")));
        assertThatThrownBy(() -> reuniaoOnlineService.editar(id, request))
                .isInstanceOf(ValidationException.class);
        assertThat(reuniaoOnline.getPanfletoImagemId()).isNull();
    }

    @Test
    void remover_deIdInexistente_deveLancarRecursoNaoEncontradoException() {
        UUID id = UUID.randomUUID();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reuniaoOnlineService.remover(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(reuniaoOnlineRepository, never()).delete(any());
    }

    @Test
    void remover_deIdExistente_deveDelegarParaRepository() {
        UUID id = UUID.randomUUID();
        ReuniaoOnline reuniaoOnline = reuniaoOnline();
        when(reuniaoOnlineRepository.findById(id)).thenReturn(Optional.of(reuniaoOnline));
        reuniaoOnlineService.remover(id);
        verify(reuniaoOnlineRepository).delete(reuniaoOnline);
    }
}
