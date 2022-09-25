package com.yjntc.gateway.dygr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Map;

/**
 * @author WangKangSheng
 * @date 2022-09-24 18:31
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SubClassScanTest implements ApplicationContextAware {

    private ApplicationContext ctx;
    //
    @Autowired(required = false)
    @Lazy
    private RouteLocatorBuilder routeLocatorBuilder;

    @Test
    public void scan(){
        Class<AbstractGatewayFilterFactory> factoryClass = AbstractGatewayFilterFactory.class;
        String[] beanNamesForType = ctx.getBeanNamesForType(factoryClass);
        Map<String, AbstractGatewayFilterFactory> beans = ctx.getBeansOfType(factoryClass);
        System.out.println(Arrays.toString(beanNamesForType));
        for (AbstractGatewayFilterFactory filterFactory : beans.values()) {
            Class configClass = filterFactory.getConfigClass();
            System.out.println("---------------");
            Class<? extends AbstractGatewayFilterFactory> aClass = filterFactory.getClass();
            System.out.println(aClass);
            System.out.println(configClass);
        }
        System.out.println(beans);
    }


    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
