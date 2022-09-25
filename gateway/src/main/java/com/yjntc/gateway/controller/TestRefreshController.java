package com.yjntc.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author WangKangSheng
 * @date 2022-09-24 15:41
 */
@RestController
public class TestRefreshController {

    @Autowired
    private GatewayControllerEndpoint gatewayControllerEndpoint;
    @Autowired
    private StripPrefixGatewayFilterFactory stripPrefixGatewayFilterFactory;


    @RequestMapping("/refresh")
    public void refresh(){


        gatewayControllerEndpoint.refresh().block();
    }

    @RequestMapping("/all")
    public Flux<Map<String, Object>> all(){
        return gatewayControllerEndpoint.routes();
    }

    @RequestMapping("/add")
    public Mono<ResponseEntity<Object>> add(){
        String routeId = "bing-route";
        RouteDefinition route = new RouteDefinition();
        route.setId(routeId);
        route.setUri(URI.create("https://cn.bing.com/"));
        PredicateDefinition predicateDefinition = new PredicateDefinition();
        predicateDefinition.setName("Path");

        // predicateDefinition.addArg("_genkey_0","/bing/**");
        predicateDefinition.addArg("patterns","[/bing/**,/bingg/**]");
        route.setPredicates(List.of(predicateDefinition));

        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setName("StripPrefix");

        filterDefinition.addArg("parts","1");
        route.setFilters(List.of(filterDefinition));

        route.setOrder(0);




        return gatewayControllerEndpoint.save(routeId, route).doOnSuccess(r->gatewayControllerEndpoint.refresh()).log();
    }

    public static void main(String[] args) {
        RouteDefinition routeDefinition = new RouteDefinition("txtID=https://baidu.com/,Path=/qq/**,Path=/xx/**");

        routeDefinition.setFilters(
                List.of(
                        new FilterDefinition(NameUtils.normalizeFilterFactoryName(StripPrefixGatewayFilterFactory.class)+"=2")
                )
        );

        System.out.println(
                routeDefinition
        );
    }

}
