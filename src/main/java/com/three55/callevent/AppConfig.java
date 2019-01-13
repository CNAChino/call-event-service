package com.three55.callevent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration client facing related classes, e.g. CallEventServer
 */
@Component
@ConfigurationProperties("app")
public class AppConfig {

    private static final Logger logger = LogManager.getLogger(AppConfig.class);

    /**
     * the port client-facing server listens to.
     */
    private int serverListenPort;

    public AppConfig() {
        logger.info("AppConfig()");
    }

    public int getServerListenPort() {
        return serverListenPort;
    }

    public void setServerListenPort(int serverListenPort) {
        logger.info("serverListenPort = " + serverListenPort);
        this.serverListenPort = serverListenPort;
    }
}
