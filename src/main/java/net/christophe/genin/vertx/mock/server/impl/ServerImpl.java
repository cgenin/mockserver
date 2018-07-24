package net.christophe.genin.vertx.mock.server.impl;


import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import net.christophe.genin.vertx.mock.server.Jsons;
import net.christophe.genin.vertx.mock.server.MemoryVerticle;
import net.christophe.genin.vertx.mock.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.Optional;

class ServerImpl implements Server {
    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);

    private final BuilderImpl builder;
    private HttpServer runnable;

    ServerImpl(BuilderImpl builder) {
        this.builder = builder;
    }

    private HttpServer listen(HttpServer instance) {
        if (Objects.isNull(builder.host)) {
            if (Objects.isNull(builder.port)) {
                return instance.listen(getAvailablePort());
            } else {
                return instance.listen(builder.port);
            }
        } else if (Objects.isNull(builder.port)) {
            return instance.listen(getAvailablePort());
        }
        return instance.listen(builder.port, builder.host);
    }

    private int getAvailablePort() {

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public Integer port() {
        return Optional.ofNullable(runnable)
                .map(HttpServer::actualPort)
                .orElse(null);
    }

    @Override
    public Integer start() {


        return Optional.ofNullable(runnable)
                .orElseGet(() -> {
                    Vertx vertx = Vertx.vertx();
                    vertx.deployVerticle(new MemoryVerticle());
                    HttpServer instance = vertx.createHttpServer();
                    final Router router = Router.router(vertx);
                    router.route().handler(CorsHandler.create("*"));
                    router.route().handler(BodyHandler.create());
                    router.mountSubRouter("/___admin", staticRoutes(vertx));
                    builder.routes.forEach(route -> route.validate().register(vertx, router));
                    runnable = listen(instance.requestHandler(router::accept));
                    logger.info("Mock Server started on port :" + runnable.actualPort());
                    return runnable;
                })
                .actualPort();
    }

    private Router staticRoutes(Vertx vertx) {
        final Router router = Router.router(vertx);
        Router apiRouter = Router.router(vertx);
        apiRouter.get("/health").handler(rc -> new Jsons(rc).send(new JsonObject().put("ready", true)));
        apiRouter.get("/calls").handler(rc-> vertx.eventBus()
                .<JsonArray>send(MemoryVerticle.LIST, new JsonObject(), m -> new Jsons(rc).send(m.result().body())));
        router.mountSubRouter("/api", apiRouter);
        return router;
    }

    @Override
    public void stop() {
        if (!Objects.isNull(runnable)) {
            runnable.close();
            runnable = null;
            logger.info("Mock Server stopped.");
        }
    }

}
