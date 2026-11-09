package org.fzl.eventcqrsaxonapp.commands.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.conversion.GeneralConverter;
import org.axonframework.conversion.jackson2.Jackson2Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public Jackson2Converter generalConverter(ObjectMapper objectMapper) {
        // This uses the Jackson 2 ObjectMapper provided by Spring Boot 3
        return new Jackson2Converter(objectMapper);
    }
}