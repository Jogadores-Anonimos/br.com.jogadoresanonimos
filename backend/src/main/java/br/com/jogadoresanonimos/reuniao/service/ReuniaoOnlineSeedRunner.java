package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.reuniao.dto.CriarReuniaoOnlineRequest;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineAgendaItemRequest;
import br.com.jogadoresanonimos.reuniao.entity.ReuniaoOnline;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoOnlineRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(10)
public class ReuniaoOnlineSeedRunner implements CommandLineRunner {

    private static final List<String> NOMES_GRUPOS = List.of(
            "Um dia de Cada Vez",
            "Vida Nova SP",
            "SalvandO Vidas CG",
            "São Paulo",
            "Reconectando Vidas",
            "Novo Começo",
            "Recomeço SPH",
            "Rio de Janeiro",
            "Só por Hoje",
            "Só Por Hoje SP",
            "Por mais 24 Horas",
            "Renascer"
    );

    private static final List<ReuniaoOnlineAgendaItemRequest> GRADE_AGREGADA = List.of(
            new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "18h30"),
            new ReuniaoOnlineAgendaItemRequest("SEGUNDA", "20h00"),
            new ReuniaoOnlineAgendaItemRequest("TERCA", "18h30"),
            new ReuniaoOnlineAgendaItemRequest("TERCA", "20h00"),
            new ReuniaoOnlineAgendaItemRequest("QUARTA", "18h30"),
            new ReuniaoOnlineAgendaItemRequest("QUARTA", "20h00"),
            new ReuniaoOnlineAgendaItemRequest("QUINTA", "18h00"),
            new ReuniaoOnlineAgendaItemRequest("QUINTA", "20h00"),
            new ReuniaoOnlineAgendaItemRequest("SEXTA", "18h30"),
            new ReuniaoOnlineAgendaItemRequest("SEXTA", "20h00"),
            new ReuniaoOnlineAgendaItemRequest("SABADO", "08h00"),
            new ReuniaoOnlineAgendaItemRequest("SABADO", "10h00"),
            new ReuniaoOnlineAgendaItemRequest("SABADO", "18h00"),
            new ReuniaoOnlineAgendaItemRequest("SABADO", "20h00"),
            new ReuniaoOnlineAgendaItemRequest("DOMINGO", "08h00"),
            new ReuniaoOnlineAgendaItemRequest("DOMINGO", "16h00"),
            new ReuniaoOnlineAgendaItemRequest("DOMINGO", "19h00")
    );

    private final ReuniaoOnlineRepository reuniaoOnlineRepository;
    private final ReuniaoOnlineService reuniaoOnlineService;

    public ReuniaoOnlineSeedRunner(ReuniaoOnlineRepository reuniaoOnlineRepository,
                                    ReuniaoOnlineService reuniaoOnlineService) {
        this.reuniaoOnlineRepository = reuniaoOnlineRepository;
        this.reuniaoOnlineService = reuniaoOnlineService;
    }

    @Override
    public void run(String... args) {
        Set<String> nomesExistentes = reuniaoOnlineRepository.findAllByOrderByNomeAsc().stream()
                .map(ReuniaoOnline::getNome)
                .collect(Collectors.toSet());
        for (String nome : NOMES_GRUPOS) {
            if (nomesExistentes.contains(nome)) {
                continue;
            }
            reuniaoOnlineService.criar(new CriarReuniaoOnlineRequest(nome, "", null, null, null, GRADE_AGREGADA));
        }
    }
}
