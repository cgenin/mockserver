package net.christophe.genin.vertx.mock.server.routes;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import net.christophe.genin.vertx.mock.server.Jsons;
import net.christophe.genin.vertx.mock.server.MemoryVerticle;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class MockedRoute {

    private static final Logger logger = LoggerFactory.getLogger(MockedRoute.class);


    private final String context;
    private int status = 200;
    private String contentType;
    private HttpMethod method;
    private Buffer result;

    public MockedRoute(String context) {
        this.context = context;
    }

    public String context() {
        return context;
    }

    public int status() {
        return status;
    }

    public MockedRoute setStatus(int status) {
        this.status = status;
        return this;
    }

    public String contentType() {
        return contentType;

    }

    public MockedRoute setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpMethod method() {
        return method;

    }

    public MockedRoute setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Buffer result() {
        return result;
    }

    public MockedRoute setResult(Buffer result) {
        this.result = result;
        return this;
    }

    public MockedRoute setResult(String result) {
        this.result = Buffer.buffer(result);
        return this;
    }

    public MockedRoute setResult(byte[] result) {
        this.result = Buffer.buffer(result);
        return this;
    }

    public MockedRoute validate() {
        Objects.requireNonNull(result, "Result must be define for route :'" + context + "'");
        return this;
    }

    public Router register(Vertx vertx, Router router) {
        Route route = router.route().path(context);
        if (!Objects.isNull(method)) {
            route.method(method);
        }
        route.handler(rc -> {
            HttpServerRequest request = rc.request();
            JsonObject obj = extractJson(request);
            logger.info(obj.encode());
            vertx.eventBus().publish(MemoryVerticle.ADD, obj);
            HttpServerResponse response = rc.response().setStatusCode(status);
            Optional.ofNullable(contentType)
                    .map(ct -> response.putHeader(HttpHeaders.CONTENT_TYPE, contentType))
                    .orElse(response)
                    .putHeader(HttpHeaders.CACHE_CONTROL, Jsons.NO_CACHE)
                    .end(result);
        });
        String strMethod = (Objects.isNull(method)) ? "ALL" : method.name();
        logger.info("route '" + context + "' for method '" + strMethod + "' registered.");
        return router;
    }

    private JsonObject extractJson(HttpServerRequest request) {
        List<JsonObject> headers = request.headers().entries().stream()
                .map(entry -> new JsonObject().put("key", entry.getKey()).put("value", entry.getValue()))
                .collect(Collectors.toList());
        HttpMethod method = request.method();
        String query = request.query();
        JsonObject params = request.params().entries().stream().reduce(new JsonObject(), (acc, entry) -> acc.put(entry.getKey(), entry.getValue()), JsonObject::mergeIn);
        String path = request.path();
        String absoluteURI = request.absoluteURI();
        return new JsonObject()
                .put("headers", headers)
                .put("method", method)
                .put("query", query)
                .put("params", params)
                .put("path", path)
                .put("absoluteURI", absoluteURI)
                .put("date", new Date().getTime());
    }


}
