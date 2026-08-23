package br.com.jogadoresanonimos.reuniao.entity;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AgendaItemReuniaoComparator {

    private static final int HORARIO_SEM_HORA_RECONHECIDA = 24 * 60;
    private static final Pattern HORA_INICIAL = Pattern.compile(
            "^\\s*([01]?\\d|2[0-3])\\s*(?:h\\s*([0-5]\\d)?|:\\s*([0-5]\\d))(?:h)?",
            Pattern.CASE_INSENSITIVE);
    private static final Comparator<AgendaItemReuniao> POR_DIA_E_HORARIO = Comparator
            .comparingInt((AgendaItemReuniao item) -> item.getDiaSemana().ordinal())
            .thenComparingInt(item -> minutosIniciais(item.getHorario()))
            .thenComparing(item -> textoFallback(item.getHorario()), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(item -> textoFallback(item.getHorario()));

    private AgendaItemReuniaoComparator() {
    }

    public static Comparator<AgendaItemReuniao> porDiaEHorario() {
        return POR_DIA_E_HORARIO;
    }

    private static int minutosIniciais(String horario) {
        if (horario == null) {
            return HORARIO_SEM_HORA_RECONHECIDA;
        }
        Matcher matcher = HORA_INICIAL.matcher(horario);
        if (!matcher.find()) {
            return HORARIO_SEM_HORA_RECONHECIDA;
        }
        int hora = Integer.parseInt(matcher.group(1));
        String minutoCapturado = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
        int minuto = minutoCapturado == null ? 0 : Integer.parseInt(minutoCapturado);
        return hora * 60 + minuto;
    }

    private static String textoFallback(String horario) {
        return horario == null ? "" : horario.trim();
    }
}
