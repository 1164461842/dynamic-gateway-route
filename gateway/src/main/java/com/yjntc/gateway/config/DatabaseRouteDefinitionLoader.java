package com.yjntc.gateway.config;

import com.yjntc.gateway.bean.ConfigDefinition;
import com.yjntc.gateway.dao.ConfigDefinitionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author WangKangSheng
 * @date 2022-09-25 20:55
 */
@Component
public class DatabaseRouteDefinitionLoader implements RouteDefinitionLocator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConfigDefinitionDao configDefinitionDao;



    @Override public Flux<RouteDefinition> getRouteDefinitions() {

        List<ConfigDefinition> all =
                configDefinitionDao.findAll();

        System.out.println(all);

        return Flux.fromIterable(all)
                .map(configDefinition -> new RouteDefinition());
    }
}
