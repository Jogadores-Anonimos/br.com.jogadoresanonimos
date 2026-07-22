package br.com.jogadoresanonimos.common.util;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class ReordenacaoUtil {

    private ReordenacaoUtil() {
    }

    public static <T> void moverParaCima(List<T> itensOrdenados, T item, Function<UUID, T> buscarPorIdComLock,
                                          Function<T, UUID> getId, ToIntFunction<T> getOrdem,
                                          BiConsumer<T, Integer> setOrdem) {
        trocarComVizinho(itensOrdenados, item, -1, buscarPorIdComLock, getId, getOrdem, setOrdem);
    }

    public static <T> void moverParaBaixo(List<T> itensOrdenados, T item, Function<UUID, T> buscarPorIdComLock,
                                           Function<T, UUID> getId, ToIntFunction<T> getOrdem,
                                           BiConsumer<T, Integer> setOrdem) {
        trocarComVizinho(itensOrdenados, item, 1, buscarPorIdComLock, getId, getOrdem, setOrdem);
    }

    private static <T> void trocarComVizinho(List<T> itensOrdenados, T item, int deslocamento,
                                              Function<UUID, T> buscarPorIdComLock, Function<T, UUID> getId,
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
        UUID idItem = getId.apply(item);
        UUID idVizinho = getId.apply(vizinho);
        List<UUID> idsEmOrdemDeterministica = List.of(idItem, idVizinho).stream()
                .sorted(Comparator.naturalOrder())
                .toList();
        T primeiroTravado = buscarPorIdComLock.apply(idsEmOrdemDeterministica.get(0));
        T segundoTravado = buscarPorIdComLock.apply(idsEmOrdemDeterministica.get(1));
        T itemTravado = getId.apply(primeiroTravado).equals(idItem) ? primeiroTravado : segundoTravado;
        T vizinhoTravado = getId.apply(primeiroTravado).equals(idVizinho) ? primeiroTravado : segundoTravado;
        int ordemItem = getOrdem.applyAsInt(itemTravado);
        int ordemVizinho = getOrdem.applyAsInt(vizinhoTravado);
        setOrdem.accept(itemTravado, ordemVizinho);
        setOrdem.accept(vizinhoTravado, ordemItem);
    }
}
