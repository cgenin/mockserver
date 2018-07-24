package net.christophe.genin.vertx.mock.server;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryVerticle extends AbstractVerticle {

    public static final String ADD = MemoryVerticle.class.getName() + ".add";
    public static final String CLEAR = MemoryVerticle.class.getName() + ".clear";
    public static final String LIST = MemoryVerticle.class.getName() + ".list";

    private final List<JsonObject> list = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void start() {
        vertx.eventBus().<JsonObject>consumer(ADD, msg -> list.add(msg.body()));
        vertx.eventBus().consumer(CLEAR, msg -> list.clear());
        vertx.eventBus().consumer(LIST, msg -> msg.reply(new JsonArray(list)));
    }
}
