package draylar.goml.api.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Small loader-independent callback container used by GOML's public API.
 */
public final class SimpleEvent<T> {
    private final List<T> listeners = new CopyOnWriteArrayList<>();
    private final Function<List<T>, T> invokerFactory;

    public SimpleEvent(Function<List<T>, T> invokerFactory) {
        this.invokerFactory = invokerFactory;
    }

    public void register(T listener) {
        listeners.add(listener);
    }

    public T invoker() {
        return invokerFactory.apply(List.copyOf(listeners));
    }
}
