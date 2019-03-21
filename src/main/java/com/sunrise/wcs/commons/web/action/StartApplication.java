package com.sunrise.wcs.commons.web.action;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.sunrise.wcs.commons.*"})
public class StartApplication {
    
    private static Logger logger = Logger.getLogger(StartApplication.class);
    
    public static void main(String[] args) {
        logger.info("========SpringBoot Starting========");
        SpringApplication.run(StartApplication.class, args);
        logger.info("========SpringBoot Started========");
    }
}
