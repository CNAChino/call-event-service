package com.three55.callevent.service;

import com.three55.callevent.AppConfig;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class CallEventServer implements ApplicationContextAware  {

    private static final Logger logger = LogManager.getLogger(CallEventServer.class);

    private ApplicationContext context;

    @Autowired
    AppConfig config;

    private int port = 9000;

    private Server server;

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        logger.info("CallEventServer initializing");

        this.port = config.getServerListenPort();

        start();

        blockUntilShutdown();
    }


    public void start() throws IOException {
        logger.info("Creating Server");
        ServerBuilder serverbuilder = ServerBuilder.forPort(this.port);
        server = serverbuilder
                .addService(new EventCollectorService())
                .build();


        logger.info("GRPC Server created.  Starting...");
        server.start();

        logger.info("Adding shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                logger.info("JVM is shutting down");
                CallEventServer.this.stop();
            }
        });
        logger.info("Server started, listening on " + port);
    }

    public void stop() {
        server.shutdown();
        logger.info("Server shutdown completed");
    }

    public void blockUntilShutdown() throws InterruptedException {

        if (server != null) {
            logger.info("Blocking until shutdown");
            server.awaitTermination();
        } else {
            throw new RuntimeException("blockUntilShutdown failed.  Server is null");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("Setting context");
        this.context = context;
    }
}
