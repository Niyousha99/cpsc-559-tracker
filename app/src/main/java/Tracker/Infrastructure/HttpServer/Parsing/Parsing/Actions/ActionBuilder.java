package Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions;

import java.util.function.Supplier;

public class ActionBuilder<T extends Action> {
    private final Supplier<T> supplier;

    public ActionBuilder(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T build() {
        return supplier.get();
    }
}   
