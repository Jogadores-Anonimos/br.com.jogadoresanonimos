package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.EditarReuniaoRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoResponse;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        assertThat(captor.getValue().getEstado()).isNull();
        assertThat(captor.getValue().getLinkReuniao()).isEqualTo("https://meet.google.com/abc-defg-hij");
    }

    @Test
    void criar_deveNormalizarLinkReuniaoEmBrancoParaNulo() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual,
                "descricao", "Segunda-feira", "20h00", "link", null, null, null, null, null,
                null, null, null, null, null, null, "   ");
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
                null, null, null, null, null, null, "https://meet.google.com/abc-defg-hij");
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
                null, null, null, null, null, null, "https://meet.google.com/abc-defg-hij");
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
                null, null, null, null, null, null, "https://meet.google.com/abc-defg-hij");
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
                null, null, null, null, null, null, "https://meet.google.com/abc-defg-hij");
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
                null, null, null, null, null, null, "https://meet.google.com/abc-defg-hij");
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
                null, null, null, null, null, null, "https://meet.google.com/abc-defg-hij");
        ReuniaoResponse resultado = reuniaoService.editar(id, request);
        assertThat(resultado.local()).isEmpty();
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
}
