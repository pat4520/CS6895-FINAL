package edu.columbia.pm.dashboard.web;

import edu.columbia.pm.dashboard.messaging.KafkaConsumerFactory;
import edu.columbia.pm.dashboard.helper.TradeBootstrap;
import io.vertx.core.AbstractVerticle;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class WebServerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        WebSocketHandler webSocketHandler = new WebSocketHandler();

        vertx.createHttpServer()
                .websocketHandler(webSocketHandler)
                .requestHandler(new HttpRequestHandler())
                .listen(8080);

        KafkaConsumerFactory.startConsumer(webSocketHandler);

        vertx.setPeriodic(10000, id -> TradeBootstrap.run());

    }

}