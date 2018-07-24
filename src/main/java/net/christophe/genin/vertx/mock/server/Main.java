package net.christophe.genin.vertx.mock.server;

import io.vertx.core.http.HttpMethod;
import net.christophe.genin.vertx.mock.server.routes.MockedRoute;

public class Main {

    public static void main(String[] args) {



        Server server = Server.builder()
                .with(new MockedRoute("/ms/test/:id").setResult("test"))
                .with(new MockedRoute("/ms/test").setMethod(HttpMethod.GET).setResult("testZ"))
                .launch();
        while(true) {
            // kill the jvm for
        }
    }
}
