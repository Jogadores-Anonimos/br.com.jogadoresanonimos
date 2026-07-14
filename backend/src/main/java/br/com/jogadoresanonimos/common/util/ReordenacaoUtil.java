package br.com.jogadoresanonimos.common.util;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

public final class ReordenacaoUtil {

    private ReordenacaoUtil() {
    }

    public static <T> void moverParaCima(List<T> itensOrdenados, T item, ToIntFunction<T> getOrdem,
                                          BiConsumer<T, Integer> setOrdem) {
        trocarComVizinho(itensOrdenados, item, -1, getOrdem, setOrdem);
    }

    public static <T> void moverParaBaixo(List<T> itensOrdenados, T item, ToIntFunction<T> getOrdem,
                                           BiConsumer<T, Integer> setOrdem) {
        trocarComVizinho(itensOrdenados, item, 1, getOrdem, setOrdem);
    }

    private static <T> void trocarComVizinho(List<T> itensOrdenados, T item, int deslocamento,
                                              ToIntFunction<T> getOrdem, BiConsumer<T, Integer> setOrdem) {
        int posicaoAtual = itensOrdenados.indexOf(item);
        if (posicaoAtual < 0) {
            return;
        }
        int posicaoVizinho = posicaoAtual + deslocamento;
        if (posicaoVizinho < 0 || posicaoVizinho >= itensOrdenados.size()) {
            return;
        }
        T vizinho = itensOrdenados.get(posicaoVizinho);
        int ordemItem = getOrdem.applyAsInt(item);
        int ordemVizinho = getOrdem.applyAsInt(vizinho);
        setOrdem.accept(item, ordemVizinho);
        setOrdem.accept(vizinho, ordemItem);
    }
}
