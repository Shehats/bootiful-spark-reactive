package com.sparks.sparkscala;

import com.sparks.sparkscala.config.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@SpringBootApplication
public class AppRunner {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AppRunner.class);
        springApplication.setWebApplicationType(WebApplicationType.REACTIVE);
        springApplication.run(args);
    }
}

@Configuration
class Routing {
    @Autowired private FlightService flightService;
    @Bean public RouterFunction<?> routerFunction() {
        return RouterFunctions.route(GET("/flights"), serverRequest -> flightService.getAll());
    }
}
