package com.hqh.graduationthesisserver.configuration;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final Logger log = LoggerFactory.getLogger(SwaggerConfiguration.class);

    @Bean
    public Docket swaggerSpringfoxDocket() {
        log.debug("Starting Swagger");
        Contact contact = new Contact(
                "Ho Quang Huy",
                "https://www.facebook.com/hqyuh",
                "hoquanghuy0123@gmail.com");

        ApiInfo apiInfo = new ApiInfo(
                "Quiz App",
                "API of Quiz",
                "1.0",
                "https://justrocket.de",
                contact,
                "MIT",
                "https://justrocket.de",
                Collections.emptyList());

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .genericModelSubstitutes(ResponseEntity.class)
                .securityContexts(Lists.newArrayList(Collections.singleton(securityContext())))
                .securitySchemes(Lists.newArrayList(Collections.singleton(apiKey())));

        docket = docket.select()
                       .paths(PathSelectors.any())
                       .build();
        return docket;
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                              .securityReferences(defaultAuth())
                              .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference("JWT", authorizationScopes));
    }

}