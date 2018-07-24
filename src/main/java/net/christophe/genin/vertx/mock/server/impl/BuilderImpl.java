package net.christophe.genin.vertx.mock.server.impl;

import net.christophe.genin.vertx.mock.server.routes.MockedRoute;
import net.christophe.genin.vertx.mock.server.Server;

import java.util.ArrayList;
import java.util.List;

public class BuilderImpl implements Server.Builder {

    List<MockedRoute> routes = new ArrayList<>();
    String host;
    Integer port;

    @Override
    public Server.Builder with(MockedRoute route) {
        routes.add(route);
        return this;
    }

    @Override
    public Server.Builder with(List<MockedRoute> routes) {
        this.routes.addAll(routes);
        return this;
    }

    @Override
    public Server launch() {
        ServerImpl server = new ServerImpl(this);
        server.start();
        return server;
    }

    @Override
    public Server launch(String host, int port) {
        this.host = host;
        this.port = port;
        return launch();
    }

    @Override
    public Server launch(String host) {
        this.host = host;
        return launch();
    }

    @Override
    public Server launch(int port) {
        this.port = port;
        return launch();
    }
}
