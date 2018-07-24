package net.christophe.genin.vertx.mock.server.routes;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MockedJsonRoute extends MockedRoute {

    public static List<MockedRoute> from(JsonObject... objs) {
        return from(Arrays.stream(objs));
    }

    public static List<MockedRoute> from(String objsTxt) {
        return from(new JsonArray(objsTxt));
    }

    @SuppressWarnings("unchecked")
    public static List<MockedRoute> from(JsonArray objs) {
        List<JsonObject> list = objs.getList();
        return from(list);
    }

    public static List<MockedRoute> from(List<JsonObject> objs) {
        return from(objs.stream());
    }

    public static List<MockedRoute> from(Stream<JsonObject> objs) {
        return objs.map(MockedJsonRoute::just)
                .collect(Collectors.toList());
    }

    public static MockedRoute just(String jsonTxt) {
        return just(new JsonObject(jsonTxt));
    }


    public static MockedRoute just(JsonObject jsonObject) {
        String context = jsonObject.getString("context");
        Objects.requireNonNull(context, "Context must not be null : " + jsonObject.encode());
        MockedJsonRoute mockedJsonRoute = new MockedJsonRoute(context);
        Optional.ofNullable(jsonObject.getInteger("status")).ifPresent(mockedJsonRoute::setStatus);
        Optional.ofNullable(jsonObject.getString("contentType")).ifPresent(mockedJsonRoute::setContentType);
        Optional.ofNullable(jsonObject.getString("method"))
                .map(str -> {
                    try {
                        return HttpMethod.valueOf(str);
                    } catch (Exception ex) {
                        throw new IllegalStateException(ex);
                    }
                })
                .ifPresent(mockedJsonRoute::setMethod);
        String result = Optional.ofNullable(jsonObject.getString("result"))
                .orElseThrow(() -> new IllegalStateException("result field required for " + jsonObject.encode()));
        return mockedJsonRoute.setResult(result);
    }

    private MockedJsonRoute(String context) {
        super(context);
    }
}
