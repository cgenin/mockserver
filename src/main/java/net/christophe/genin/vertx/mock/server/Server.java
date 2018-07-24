package net.christophe.genin.vertx.mock.server;

import net.christophe.genin.vertx.mock.server.impl.BuilderImpl;
import net.christophe.genin.vertx.mock.server.routes.MockedRoute;

import java.util.List;

public interface Server {

    static Builder builder() {
        return new BuilderImpl();
    }

    Integer port();

    Integer start();

    void stop();

    interface Builder {

        Builder with(MockedRoute route);

        Builder with(List<MockedRoute> routes);

        Server launch();

        Server launch(String host, int port);

        Server launch(String host);

        Server launch(int port);
    }
}
