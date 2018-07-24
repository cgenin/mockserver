package net.christophe.genin.vertx.mock.server;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public final class Jsons {
    private static final String CONTENT_TYPE_JSON = "application/json";
    public static final String NO_CACHE = "private, no cache";

    private final RoutingContext rc;


    public Jsons(RoutingContext rc) {
        this.rc = rc;
    }

    public void send(JsonArray array) {
        send(array.encode());
    }

    public void send(JsonObject obj) {
        send(obj.encode());
    }

    public void send(String data) {
        rc.response()
                .setStatusCode(200)
                .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
                .putHeader(HttpHeaders.CACHE_CONTROL, NO_CACHE)
                .end(data);
    }
}
