package com.three55.callevent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Configuration client facing related classes, e.g. CallEventServer
 */
@ConfigurationProperties(prefix="app")
public class AppConfig {

    private static final Logger logger = LogManager.getLogger(AppConfig.class);

    /**
     * the port client-facing server listens to.
     */
    private int serverListenPort;
    private String neo4jUri;// = "bolt://localhost:7687";
    private String neo4jUser;// = "neo4j";
    private String neo4jPassword;// = "password";

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

    public String getNeo4jUri() {
        return neo4jUri;
    }

    public void setNeo4jUri(String neo4jUri) {
        logger.info("neo4jUri = " + neo4jUri);
        this.neo4jUri = neo4jUri;
    }

    public String getNeo4jUser() {
        return neo4jUser;
    }

    public void setNeo4jUser(String neo4jUser) {
        logger.info("neo4jUser = " + neo4jUser);
        this.neo4jUser = neo4jUser;
    }

    public String getNeo4jPassword() {
        return neo4jPassword;
    }

    public void setNeo4jPassword(String neo4jPassword) {
        logger.info("neo4jPassword = " + neo4jPassword);
        this.neo4jPassword = neo4jPassword;
    }
}
