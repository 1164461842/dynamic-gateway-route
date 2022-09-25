package com.yjntc.gateway.dygr;

import org.junit.Test;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.support.NameUtils;

/**
 * @author WangKangSheng
 * @date 2022-09-25 15:57
 */
public class NameUtilsTests {

    @Test
    public void namedTest(){

        String name = NameUtils.normalizeFilterFactoryName(StripPrefixGatewayFilterFactory.class);
        System.out.println(name);

        String nameAsProperty = NameUtils.normalizeFilterFactoryNameAsProperty(StripPrefixGatewayFilterFactory.class);
        System.out.println(nameAsProperty);

        String predicateName = NameUtils.normalizeRoutePredicateName(PathRoutePredicateFactory.class);
        System.out.println(predicateName);

        String predicateNameAsProperty = NameUtils.normalizeRoutePredicateNameAsProperty(PathRoutePredicateFactory.class);
        System.out.println(predicateNameAsProperty);
    }

}
