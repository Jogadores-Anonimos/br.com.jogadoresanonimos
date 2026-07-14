package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
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
                "19h30", "endereco", "5599999999999", "Segunda", "Noite", -23.5505, -46.6333);
    }

    @Test
    void listarTodas_deveDelegarParaRepositoryEMapear() {
        when(reuniaoRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(reuniao()));

        List<ReuniaoResponse> resultado = reuniaoService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Grupo Centro");
    }

    @Test
    void criar_deveSalvarComTextoTrimadoERetornarResponse() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("  Grupo Centro  ", CategoriaReuniao.presencial,
                "  descricao  ", "Segunda-feira", "19h30", "  endereco  ", "5599999999999", "Segunda", "Noite",
                -23.5505, -46.6333);
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReuniaoResponse resultado = reuniaoService.criar(request);

        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Grupo Centro");
        assertThat(captor.getValue().getDescricao()).isEqualTo("descricao");
        assertThat(captor.getValue().getLocal()).isEqualTo("endereco");
        assertThat(resultado.nome()).isEqualTo("Grupo Centro");
    }

    @Test
    void criar_deveNormalizarCamposOpcionaisEmBrancoParaNulo() {
        CriarReuniaoRequest request = new CriarReuniaoRequest("Grupo Virtual", CategoriaReuniao.virtual,
                "descricao", "Segunda-feira", "20h00", "link", "  ", "", null, null, null);
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        reuniaoService.criar(request);

        ArgumentCaptor<Reuniao> captor = ArgumentCaptor.forClass(Reuniao.class);
        verify(reuniaoRepository).save(captor.capture());
        assertThat(captor.getValue().getWhatsapp()).isNull();
        assertThat(captor.getValue().getDiaSemana()).isNull();
    }

    @Test
    void editar_deveAtualizarReuniaoExistente() {
        UUID id = UUID.randomUUID();
        Reuniao reuniao = reuniao();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.of(reuniao));
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Novo", CategoriaReuniao.virtual,
                "nova descricao", "Terça-feira", "20h00", "link", null, null, null, null, null);

        ReuniaoResponse resultado = reuniaoService.editar(id, request);

        assertThat(resultado.nome()).isEqualTo("Grupo Novo");
        assertThat(resultado.categoria()).isEqualTo(CategoriaReuniao.virtual);
    }

    @Test
    void editar_deveLancarExcecaoQuandoReuniaoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(reuniaoRepository.findById(id)).thenReturn(Optional.empty());
        EditarReuniaoRequest request = new EditarReuniaoRequest("Grupo Novo", CategoriaReuniao.virtual,
                "nova descricao", "Terça-feira", "20h00", "link", null, null, null, null, null);

        assertThatThrownBy(() -> reuniaoService.editar(id, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
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
