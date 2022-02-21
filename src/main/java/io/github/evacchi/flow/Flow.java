package io.github.evacchi.flow;

import java.util.ArrayList;
import java.util.Collection;

public class Flow<T> {
    private final Collection<Edge<T>> edges;

    public static class Builder<T> {
        private Builder() {}
        private final Collection<Edge<T>> edges = new ArrayList<>();

        public Flow<T> build() {
            return new Flow<>(edges);
        }

        public Builder<T> edge(T from, T to) {
            edges.add(new Edge<>(from, to));
            return this;
        }
    }

    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>();
    }

    private Flow(Collection<Edge<T>> edges) {
        this.edges = edges;
    }

    public Collection<T> next(T current) {
        return edges.stream().filter(e -> e.from.equals(current)).map(Edge::to).toList();
    }

    public Collection<T> prev(T current) {
        return edges.stream().filter(e -> e.to.equals(current)).map(Edge::from).toList();
    }

    private record Edge<T>(T from, T to) {}

}
