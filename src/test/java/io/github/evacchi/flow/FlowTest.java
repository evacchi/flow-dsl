package io.github.evacchi.flow;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class FlowTest {
    @Test void testEnum() {
        enum States {
            S1, S2, S3;
        }

        var flow =
                Flow.builder(States.class)
                        .edge(States.S1, States.S2)
                        .edge(States.S2, States.S3).build();


        assertTrue(flow.prev(States.S1).isEmpty());

        assertEquals(flow.next(States.S1), List.of(States.S2));
        assertEquals(flow.next(States.S2), List.of(States.S3));

        assertTrue(flow.next(States.S3).isEmpty());

    }

    @Test void testRunnable() {
        class Env {
            private Map<String, Object> map = new HashMap<>();

            public Env(Map<String, Object> map) {
                this.map.putAll(map);
            }

            public Object put(String key, Object value) {
                return map.put(key, value);
            }
            public <T> T get(Object key) {
                return (T) map.get(key);
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof Env env && Objects.equals(map, env.map);
            }

            @Override
            public int hashCode() {
                return Objects.hash(map);
            }

            @Override
            public String toString() {
                return "Env{" +
                        "map=" + map +
                        '}';
            }
        }
        enum States implements Consumer<Env> {
            S1 {
                public void accept(Env e) {
                    int counter = e.get("counter");
                    e.put("counter", counter + 1);
                }
            },
            S2 {
                public void accept(Env e) {
                    int counter = e.get("counter");
                    e.put("counter", counter + 1);
                }
            },
            S3 {
                public void accept(Env e) {
                    e.put("message", "Final State!");
                    int counter = e.get("counter");
                    e.put("counter", counter + 1);
                }
            };
        }

        var flow =
                Flow.builder(States.class)
                        .edge(States.S1, States.S2)
                        .edge(States.S2, States.S3).build();


        assertTrue(flow.prev(States.S1).isEmpty());

        assertEquals(flow.next(States.S1), List.of(States.S2));
        assertEquals(flow.next(States.S2), List.of(States.S3));

        assertTrue(flow.next(States.S3).isEmpty());

        var e = new Env(Map.of("counter", 10));


        var next = new Stack<States>();
        next.push(States.S1);
        while (!next.empty()) {
            var curr = next.pop();
            curr.accept(e);
            next.addAll(flow.next(curr));
        }

        assertEquals(e, new Env(Map.of("counter", 13, "message", "Final State!")));
    }

}