package de.tum.gh_connector.client;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class FeignLogConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}