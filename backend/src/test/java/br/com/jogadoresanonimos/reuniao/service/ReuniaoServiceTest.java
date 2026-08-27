package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoAgendaItemRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoResponse;
import br.com.jogadoresanonimos.reuniao.entity.AgendaItemReuniao;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.DiaSemanaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReuniaoServiceTest {

    @Mock
    ReuniaoRepository reuniaoRepository;

    ReuniaoService reuniaoService;

    @BeforeEach
    void setUp() {
        reuniaoService = new ReuniaoService(reuniaoRepository);
    }

    private Reuniao reuniao() {
        return new Reuniao("Grupo Centro", CategoriaReuniao.presencial, "descricao", "Segunda-feira",
                "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP", "5599999999999", "Segunda", "Noite",
                -23.5505, -46.6333, "01310-100", "123", "Rua das Flores", "Centro", "São Paulo", "SP", null);
    }

    private Reuniao reuniaoLegadaSemEnderecoEstruturado() {
        return new Reuniao("Grupo Centro", CategoriaReuniao.presencial, "descricao", "Segunda-feira",
                "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP", "5599999999999", "Segunda", "Noite",
                -23.5505, -46.6333, null, null, null, null, null, null, null);
    }

    private CriarReuniaoRequest criarRequestPresencial() {
        return new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial, "descricao", "Segunda-feira",
                "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP", "5599999999999", "Segunda", "Noite",
                -23.5505, -46.6333, "01310-100", "123", "Rua das Flores", "Centro", "São Paulo", "SP", null);
    }

    private CriarReuniaoRequest criarRequestVirtual() {
        return new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual, "descricao", "Segunda-feira",
                "20h00", "link", "  ", "", null, null, null, null, null, null, null, null, "SP",
                "https://meet.google.com/abc-defg-hij");
    }

    private CriarReuniaoRequest criarRequestVirtualSemEstado() {
        return new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual, "descricao", "Segunda-feira",
                "20h00", "link", "  ", "", null, null, null, null, null, null, null, null, null,
                "https://meet.google.com/abc-defg-hij");
    }

    @Test
    void listarTodas_deveDelegarParaRepositoryEMapear() {
        when(reuniaoRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(reuniao()));
        List<ReuniaoResponse> resultado = reuniaoService.listarTodas();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Grupo Centro");
    }

    @Test
    void criar_presencial_deveManterLocalExatamenteComoEnviadoSemDerivarDoEndereco() {
        CriarReuniaoRequest request = criarRequestPresencial();
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getLocal()).isEqualTo("Rua das Flores, 123 - Centro, São Paulo - SP");
        assertThat(captor.getValue().getCep()).isEqualTo("01310-100");
        assertThat(captor.getValue().getEstado()).isEqualTo("SP");
    }

    @Test
    void criar_presencial_deveNormalizarEstadoParaMaiusculo() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "local qualquer", null, null, null, null, null,
                "01310-100", "123", "Rua das Flores", "Centro", "São Paulo", "sp", null);
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo("SP");
    }

    @Test
    void criar_presencial_devePermitirCriacaoSemNenhumCampoDeEnderecoEstruturado() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "Rua das Flores, 123", null, null, null, null, null,
                null, null, null, null, null, null, null);
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getLocal()).isEqualTo("Rua das Flores, 123");
        assertThat(captor.getValue().getCep()).isNull();
        assertThat(captor.getValue().getEstado()).isNull();
    }

    @Test
    void criar_presencial_devePermitirCampoDeEnderecoParcialmentePreenchido() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "Rua das Flores, 123", null, null, null, null, null,
                "01310-100", null, "Rua das Flores", "Centro", "São Paulo", "SP", null);
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getCep()).isEqualTo("01310-100");
        assertThat(captor.getValue().getNumero()).isNull();
    }

    @Test
    void criar_virtual_deveManterLocalComoTextoLivreSemCamposDeEndereco() {
        CriarReuniaoRequest request = criarRequestVirtual();
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getLocal()).isEqualTo("link");
        assertThat(captor.getValue().getCep()).isNull();
        assertThat(captor.getValue().getEstado()).isEqualTo("SP");
        assertThat(captor.getValue().getLinkReuniao()).isEqualTo("https://meet.google.com/abc-defg-hij");
    }

    @Test
    void criar_deveNormalizarLinkReuniaoEmBrancoParaNulo() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual,
                "descricao", "Segunda-feira", "20h00", "link", null, null, null, null, null,
                null, null, null, null, null, "SP", "   ");
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getLinkReuniao()).isNull();
    }

    @Test
    void criar_deveNormalizarCamposOpcionaisEmBrancoParaNulo() {
        CriarReuniaoRequest request = criarRequestVirtual();
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getDiaSemana()).isNull();
    }

    @Test
    void criar_deveAplicarWhatsappPadraoQuandoNaoInformado() {
        CriarReuniaoRequest request = criarRequestVirtual();
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getWhatsapp()).isEqualTo("11 99571-6942");
    }

    @Test
    void editar_deveAplicarWhatsappPadraoQuandoNaoInformado() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP", null, null,
                null, null, null, "01310-100", "123", "Rua das Flores", "Centro", "São Paulo", "SP", null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.whatsapp()).isEqualTo("11 99571-6942");
    }

    @Test
    void editar_presencialParaVirtualComCamposDeEnderecoNulosNoRequest_devePersistirNulos() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Novo", CategoriaReuniao.virtual,
                "nova descricao", "Terça-feira", "20h00", "link", null, null, null, null, null,
                null, null, null, null, null, "SP", "https://meet.google.com/abc-defg-hij");
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.nome()).isEqualTo("Grupo Novo");
        assertThat(resultado.categoria()).isEqualTo(CategoriaReuniao.virtual);
        assertThat(resultado.local()).isEqualTo("link");
        assertThat(resultado.cep()).isNull();
        assertThat(resultado.linkReuniao()).isEqualTo("https://meet.google.com/abc-defg-hij");
    }

    @Test
    void editar_reuniaoPresencialLegadaSemEnderecoEstruturado_devePermitirEdicaoSemPreencherOsSeisCampos() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniaoLegadaSemEnderecoEstruturado();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro Atualizado", CategoriaReuniao.presencial,
                "descricao atualizada", "Segunda-feira", "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP",
                null, null, null, null, null, null, null, null, null, null, null, null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.nome()).isEqualTo("Grupo Centro Atualizado");
        assertThat(resultado.local()).isEqualTo("Rua das Flores, 123 - Centro, São Paulo - SP");
        assertThat(resultado.cep()).isNull();
        assertThat(resultado.estado()).isNull();
    }

    @Test
    void editar_deveLancarExcecaoQuandoReuniaoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.empty());
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Novo", CategoriaReuniao.virtual,
                "nova descricao", "Terça-feira", "20h00", "link", null, null, null, null, null,
                null, null, null, null, null, null, null);
        assertThatThrownBy(() -> reuniaoService.editar(id, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void criar_virtual_devePermitirDescricaoAusenteEPersistirVazia() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual,
                null, "Segunda-feira", "20h00", "link", null, null, null, null, null,
                null, null, null, null, null, "SP", "https://meet.google.com/abc-defg-hij");
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getDescricao()).isEmpty();
    }

    @Test
    void criar_virtual_devePermitirDescricaoEmBrancoEPersistirVazia() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual,
                "   ", "Segunda-feira", "20h00", "link", null, null, null, null, null,
                null, null, null, null, null, "SP", "https://meet.google.com/abc-defg-hij");
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getDescricao()).isEmpty();
    }

    @Test
    void criar_virtual_devePermitirLocalAusenteEPersistirVazio() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual,
                "descricao", "Segunda-feira", "20h00", null, null, null, null, null, null,
                null, null, null, null, null, "SP", "https://meet.google.com/abc-defg-hij");
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getLocal()).isEmpty();
    }

    @Test
    void criar_virtual_devePermitirLocalEmBrancoEPersistirVazio() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual,
                "descricao", "Segunda-feira", "20h00", "   ", null, null, null, null, null,
                null, null, null, null, null, "SP", "https://meet.google.com/abc-defg-hij");
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getLocal()).isEmpty();
    }

    @Test
    void editar_virtual_devePermitirLocalAusenteEPersistirVazio() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Novo", CategoriaReuniao.virtual,
                "nova descricao", "Terça-feira", "20h00", null, null, null, null, null, null,
                null, null, null, null, null, "SP", "https://meet.google.com/abc-defg-hij");
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.local()).isEmpty();
    }

    @Test
    void criar_virtual_semEstado_deveLancarExcecaoValidationException() {
        CriarReuniaoRequest request = criarRequestVirtualSemEstado();
        assertThatThrownBy(() -> reuniaoService.criar(request))
                .isInstanceOf(ValidationException.class);
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    void criar_virtual_comEstado_devePersistirComSucesso() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual,
                "descricao", "Segunda-feira", "20h00", "link", null, null, null, null, null,
                null, null, null, null, null, "sp", "https://meet.google.com/abc-defg-hij");
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo("SP");
    }

    @Test
    void editar_virtual_semEstado_deveLancarExcecaoValidationException() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Novo", CategoriaReuniao.virtual,
                "nova descricao", "Terça-feira", "20h00", "link", null, null, null, null, null,
                null, null, null, null, null, null, "https://meet.google.com/abc-defg-hij");
        assertThatThrownBy(() -> reuniaoService.editar(id, request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void editar_virtual_comEstado_devePersistirComSucesso() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Novo", CategoriaReuniao.virtual,
                "nova descricao", "Terça-feira", "20h00", "link", null, null, null, null, null,
                null, null, null, null, null, "rj", "https://meet.google.com/abc-defg-hij");
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.estado()).isEqualTo("RJ");
    }

    @Test
    void criar_presencial_semEstado_deveContinuarPermitido() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "Rua das Flores, 123", null, null, null, null, null,
                null, null, null, null, null, null, null);
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isNull();
    }

    @Test
    void criar_presencial_deveLancarExcecaoQuandoDescricaoAusente() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                null, "Segunda-feira", "19h30", "Rua das Flores, 123", null, null, null, null, null,
                null, null, null, null, null, null, null);
        assertThatThrownBy(() -> reuniaoService.criar(request))
                .isInstanceOf(ValidationException.class);
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    void editar_presencial_deveLancarExcecaoQuandoDescricaoEmBranco() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "   ", "Segunda-feira", "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP", null, null,
                null, null, null, "01310-100", "123", "Rua das Flores", "Centro", "São Paulo", "SP", null);
        assertThatThrownBy(() -> reuniaoService.editar(id, request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void editar_devePersistirEGerarAvisoQuandoCidadeMudaSemLocalMudar() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP", null, null,
                null, null, null, "01310-100", "123", "Rua das Flores", "Centro", "Guarulhos", "SP", null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.avisos()).hasSize(1);
        assertThat(resultado.avisos().get(0).codigo()).isEqualTo("ENDERECO_LOCAL_DIVERGENTE");
        assertThat(resultado.cidade()).isEqualTo("Guarulhos");
    }

    @Test
    void editar_devePersistirEGerarAvisoQuandoEstadoMudaSemLocalMudar() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP", null, null,
                null, null, null, "01310-100", "123", "Rua das Flores", "Centro", "São Paulo", "RJ", null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.avisos()).hasSize(1);
    }

    @Test
    void editar_naoDeveGerarAvisoQuandoCidadeMudaELocalTambemMuda() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "Rua Nova, 45 - Centro, Guarulhos - SP", null, null,
                null, null, null, "01310-100", "123", "Rua das Flores", "Centro", "Guarulhos", "SP", null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.avisos()).isEmpty();
    }

    @Test
    void editar_naoDeveGerarAvisoQuandoEnderecoEstruturadoNaoMuda() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro Atualizado", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP", null, null,
                null, null, null, "01310-100", "123", "Rua das Flores", "Centro", "São Paulo", "SP", null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.avisos()).isEmpty();
    }

    @Test
    void editar_naoDeveGerarAvisoParaReuniaoLegadaSemEnderecoEstruturadoQuandoNadaMuda() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniaoLegadaSemEnderecoEstruturado();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h30", "Rua das Flores, 123 - Centro, São Paulo - SP",
                null, null, null, null, null, null, null, null, null, null, null, null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.avisos()).isEmpty();
    }

    @Test
    void remover_deveDeletarQuandoExiste() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        reuniaoService.remover(id);
        verify(reuniaoRepository).delete(reuniao);
    }

    @Test
    void remover_deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reuniaoService.remover(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(reuniaoRepository, never()).delete(any());
    }

    @Test
    void criar_comAgendaDeMultiplosDias_devePersistirOrdenadaEDualWriteDoPrimeiroItem() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", null, null, "local", null, null, null, null, null, null, null, null,
                null, null, null, null, List.of(
                new ReuniaoAgendaItemRequest("QUINTA", "20h"),
                new ReuniaoAgendaItemRequest("SEGUNDA", "19h")));
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ReuniaoResponse resultado = reuniaoService.criar(request);
        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getDia()).isEqualTo("Segunda");
        assertThat(captor.getValue().getHorario()).isEqualTo("19h");
        assertThat(captor.getValue().getDiaSemana()).isEqualTo("Segunda");
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("SEGUNDA", "19h"), tuple("QUINTA", "20h"));
    }

    @Test
    void editar_comAgendaNova_deveSubstituirAgendaAnterior() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", null, null, "Rua das Flores, 123 - Centro, São Paulo - SP", null, null, null,
                null, null, "01310-100", "123", "Rua das Flores", "Centro", "São Paulo", "SP", null,
                List.of(new ReuniaoAgendaItemRequest("SABADO", "10h"),
                        new ReuniaoAgendaItemRequest("TERCA", "19h30")));
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("TERCA", "19h30"), tuple("SABADO", "10h"));
        assertThat(resultado.dia()).isEqualTo("Terça");
        assertThat(resultado.horario()).isEqualTo("19h30");
    }

    @Test
    void criar_comMultiplosHorariosNoMesmoDia_devePersistirOrdenado() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", null, null, "local", null, null, null, null, null, null, null, null,
                null, null, null, null, List.of(
                new ReuniaoAgendaItemRequest("SEGUNDA", "20h"),
                new ReuniaoAgendaItemRequest("segunda-feira", "19h")));
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ReuniaoResponse resultado = reuniaoService.criar(request);
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("SEGUNDA", "19h"), tuple("SEGUNDA", "20h"));
        assertThat(resultado.horario()).isEqualTo("19h");
    }

    @Test
    void criar_comFormatosMistos_deveOrdenarCronologicamenteEFazerDualWriteDoPrimeiro() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", null, null, "local", null, null, null, null, null, null, null, null,
                null, null, null, null, List.of(
                new ReuniaoAgendaItemRequest("SEGUNDA", "texto livre"),
                new ReuniaoAgendaItemRequest("SEGUNDA", "19h30"),
                new ReuniaoAgendaItemRequest("SEGUNDA", "19:30h"),
                new ReuniaoAgendaItemRequest("SEGUNDA", "09:15"),
                new ReuniaoAgendaItemRequest("SEGUNDA", "9h às 11h"),
                new ReuniaoAgendaItemRequest("SEGUNDA", "9h")));
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ReuniaoResponse resultado = reuniaoService.criar(request);
        assertThat(resultado.agenda()).extracting("horario")
                .containsExactly("9h", "9h às 11h", "09:15", "19:30h", "19h30", "texto livre");
        assertThat(resultado.horario()).isEqualTo("9h");
    }

    @Test
    void response_deveOrdenarFormatosMistosMesmoQuandoEntidadeEstaForaDeOrdem() {
        Reuniao reuniao = reuniao();
        reuniao.substituirAgenda(List.of(
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "sem hora"),
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "19:30h"),
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "9h às 11h"),
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "09:15")));
        assertThat(ReuniaoResponse.from(reuniao).agenda()).extracting("horario")
                .containsExactly("9h às 11h", "09:15", "19:30h", "sem hora");
    }

    @Test
    void criar_comMaisDe336Horarios_deveRejeitarNoServiceSemPersistir() {
        List<ReuniaoAgendaItemRequest> agenda = IntStream.range(0, 337)
                .mapToObj(indice -> new ReuniaoAgendaItemRequest("SEGUNDA", "horario-" + indice))
                .toList();
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", null, null, "local", null, null, null, null, null, null, null, null,
                null, null, null, null, agenda);
        assertThatThrownBy(() -> reuniaoService.criar(request)).isInstanceOf(ValidationException.class);
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    void criar_comMesmoHorarioRepetidoNoMesmoDia_deveRejeitarSemPersistir() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", null, null, "local", null, null, null, null, null, null, null, null,
                null, null, null, null, List.of(
                new ReuniaoAgendaItemRequest("SEGUNDA", "19h"),
                new ReuniaoAgendaItemRequest("segunda-feira", " 19h ")));
        assertThatThrownBy(() -> reuniaoService.criar(request)).isInstanceOf(ValidationException.class);
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    void criar_semAgendaESemCamposLegados_deveRejeitarSemPersistir() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", null, null, "local", null, null, null, null, null, null, null, null,
                null, null, null, null, null);
        assertThatThrownBy(() -> reuniaoService.criar(request)).isInstanceOf(ValidationException.class);
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    void editar_comPayloadLegado_deveAtualizarDiaPrimarioEPreservarItensAdicionais() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        reuniao.substituirAgenda(List.of(
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "19h"),
                new AgendaItemReuniao(DiaSemanaReuniao.QUINTA, "20h")));
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "21h", "Rua das Flores, 123 - Centro, São Paulo - SP",
                null, "Segunda", "Noite", null, null, "01310-100", "123", "Rua das Flores", "Centro",
                "São Paulo", "SP", null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("SEGUNDA", "21h"), tuple("QUINTA", "20h"));
    }

    @Test
    void editar_comPayloadLegadoParaDiaAdicionalExistente_deveAtualizarConflitoSemApagarPrimario() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        reuniao.substituirAgenda(List.of(
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "19h"),
                new AgendaItemReuniao(DiaSemanaReuniao.QUINTA, "20h")));
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Quinta-feira", "22h", "Rua das Flores, 123 - Centro, São Paulo - SP",
                null, "Quinta", "Noite", null, null, "01310-100", "123", "Rua das Flores", "Centro",
                "São Paulo", "SP", null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("SEGUNDA", "19h"), tuple("QUINTA", "22h"));
        assertThat(resultado.diaSemana()).isEqualTo("Segunda");
        assertThat(resultado.horario()).isEqualTo("19h");
    }

    @Test
    void editar_comPayloadLegado_devePreservarOutroHorarioDoMesmoDia() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        reuniao.substituirAgenda(List.of(
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "18h"),
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "20h"),
                new AgendaItemReuniao(DiaSemanaReuniao.QUINTA, "19h")));
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "19h", "Rua das Flores, 123 - Centro, São Paulo - SP",
                null, "Segunda", "Noite", null, null, "01310-100", "123", "Rua das Flores", "Centro",
                "São Paulo", "SP", null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("SEGUNDA", "19h"), tuple("SEGUNDA", "20h"),
                        tuple("QUINTA", "19h"));
    }

    @Test
    void editar_comPayloadLegadoJaExistente_devePreservarAgendaSemDuplicar() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        reuniao.substituirAgenda(List.of(
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "18h"),
                new AgendaItemReuniao(DiaSemanaReuniao.SEGUNDA, "20h")));
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Centro", CategoriaReuniao.presencial,
                "descricao", "Segunda-feira", "20h", "Rua das Flores, 123 - Centro, São Paulo - SP",
                null, "Segunda", "Noite", null, null, "01310-100", "123", "Rua das Flores", "Centro",
                "São Paulo", "SP", null);
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.agenda()).extracting("diaSemana", "horario")
                .containsExactly(tuple("SEGUNDA", "18h"), tuple("SEGUNDA", "20h"));
    }
}
