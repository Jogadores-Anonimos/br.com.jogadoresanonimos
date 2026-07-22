package br.com.jogadoresanonimos.reflexao.service;

import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaDoMesResponse;
import br.com.jogadoresanonimos.reflexao.dto.ReflexaoDiaResponse;
import br.com.jogadoresanonimos.reflexao.dto.SalvarReflexaoDiaRequest;
import br.com.jogadoresanonimos.reflexao.entity.ReflexaoDia;
import br.com.jogadoresanonimos.reflexao.repository.ReflexaoDiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ReflexaoDiaService {

    private static final int[] DIAS_POR_MES = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private final ReflexaoDiaRepository reflexaoDiaRepository;

    public ReflexaoDiaService(ReflexaoDiaRepository reflexaoDiaRepository) {
        this.reflexaoDiaRepository = reflexaoDiaRepository;
    }

    @Transactional(readOnly = true)
    public ReflexaoDiaResponse buscarDeHoje() {
        LocalDate hoje = LocalDate.now();
        return buscarPorData(hoje.getMonthValue(), hoje.getDayOfMonth());
    }

    @Transactional(readOnly = true)
    public ReflexaoDiaResponse buscarPorData(int mes, int dia) {
        validarMesEDia(mes, dia);
        ReflexaoDia reflexaoDia = reflexaoDiaRepository.findByMesAndDia(mes, dia)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.REFLEXAO_NAO_ENCONTRADA, "Reflexão não cadastrada para esta data."));
        return ReflexaoDiaResponse.from(reflexaoDia);
    }

    @Transactional(readOnly = true)
    public List<ReflexaoDiaDoMesResponse> listarMes(int mes) {
        validarMes(mes);
        Map<Integer, String> textosPorDia = reflexaoDiaRepository.findAllByMesOrderByDiaAsc(mes).stream()
                .collect(Collectors.toMap(ReflexaoDia::getDia, ReflexaoDia::getTexto));
        int totalDias = DIAS_POR_MES[mes - 1];
        return IntStream.rangeClosed(1, totalDias)
                .mapToObj(dia -> new ReflexaoDiaDoMesResponse(dia, textosPorDia.get(dia)))
                .toList();
    }

    @Transactional
    public ReflexaoDiaResponse salvar(int mes, int dia, SalvarReflexaoDiaRequest request) {
        validarMesEDia(mes, dia);
        ReflexaoDia reflexaoDia = reflexaoDiaRepository.findByMesAndDia(mes, dia)
                .orElse(null);
        if (reflexaoDia == null) {
            reflexaoDia = new ReflexaoDia(mes, dia, request.texto().trim());
        } else {
            reflexaoDia.editar(request.texto().trim());
        }
        reflexaoDia = reflexaoDiaRepository.save(reflexaoDia);
        return ReflexaoDiaResponse.from(reflexaoDia);
    }

    private void validarMes(int mes) {
        if (mes < 1 || mes > 12) {
            throw new ValidationException(ErrorCode.REFLEXAO_DATA_INVALIDA, "Mês inválido.");
        }
    }

    private void validarMesEDia(int mes, int dia) {
        validarMes(mes);
        int totalDias = DIAS_POR_MES[mes - 1];
        if (dia < 1 || dia > totalDias) {
            throw new ValidationException(ErrorCode.REFLEXAO_DATA_INVALIDA, "Dia inválido para o mês informado.");
        }
    }
}
