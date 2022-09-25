package com.yjntc.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

/**
 * @author WangKangSheng
 * @date 2022-09-24 15:55
 */
@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        StripPrefixGatewayFilterFactory stripPrefixGatewayFilterFactory = new StripPrefixGatewayFilterFactory();
        return builder
                .routes()
                .route("rid",r->
                        r.path("/baidu/**")
                                .filters(f-> {
                                    StripPrefixGatewayFilterFactory.Config config = new StripPrefixGatewayFilterFactory.Config();
                                    config.setParts(1);
                                    return f.filters(stripPrefixGatewayFilterFactory.apply(config));
                                })
                                .uri("http://www.baidu.com"))
                .build();
    }

}
