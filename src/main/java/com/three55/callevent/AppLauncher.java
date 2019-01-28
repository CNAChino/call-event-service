package com.three55.callevent;

import com.three55.callevent.service.CallEventServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AppLauncher {

    private static final Logger logger = LogManager.getLogger(AppLauncher.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("Running Application");
        // run
        ConfigurableApplicationContext context = (ConfigurableApplicationContext)SpringApplication.run(AppLauncher.class, args);

        CallEventServer server = (CallEventServer) context.getBean("serverBean");

        // wait for termination
        server.blockUntilShutdown();

    } // end main


}
