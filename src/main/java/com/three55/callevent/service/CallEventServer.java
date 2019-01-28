package com.three55.callevent.service;

import com.three55.callevent.AppConfig;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class CallEventServer   {

    private static final Logger logger = LogManager.getLogger(CallEventServer.class);

    @Autowired
    public AppConfig config;

    public List<BindableService> services;

    private Server server;

    public void init() throws IOException, InterruptedException {
        logger.info("Initializing");

        int port = this.config.getServerListenPort();

        ServerBuilder serverbuilder = ServerBuilder.forPort(port);
        server = serverbuilder
                .addService(new EventCollectorService())
                .build();

        server.start();
        logger.info("Server started, listening on " + port);

    }

    public void destroy() {
        logger.info("Server shutting down");
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

    public AppConfig getConfig() {
        return config;
    }

    public void setConfig(AppConfig config) {
        this.config = config;
    }

    public List<BindableService> getServices() {
        return services;
    }

    public void setServices(List<BindableService> services) {
        this.services = services;
    }
}
