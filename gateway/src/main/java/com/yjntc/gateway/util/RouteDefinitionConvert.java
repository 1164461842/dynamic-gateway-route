package com.yjntc.gateway.util;

import com.yjntc.gateway.bean.Factory;
import com.yjntc.gateway.bean.FactoryDefinition;
import com.yjntc.gateway.bean.Route;
import org.springframework.beans.BeansException;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangkangsheng
 * @email 1164461842@qq.com
 * @create 2022-09-26 15:21
 */
@Component
public class RouteDefinitionConvert implements ApplicationContextAware {

	private ApplicationContext ctx;
	private Set<AbstractRoutePredicateFactory> routePredicateFactories = new HashSet<>();

	public RouteDefinition toDefinition(Route route){
		List<Factory> factoryList = route.getFactories();
		Map<String, List<Factory>> factoryTypeMap = factoryList.parallelStream()
				.collect(Collectors.groupingBy(Factory::getFactoryType));



		return new RouteDefinition();
	}


	public PredicateDefinition toDefinition(Factory factory){
		FactoryDefinition factoryDefinition = factory.getFactoryDefinition();
		String factoryClassName = factoryDefinition.getFactoryClassName();


		return new PredicateDefinition();
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
		Map<String, AbstractRoutePredicateFactory> beans = this.ctx.getBeansOfType(AbstractRoutePredicateFactory.class);
		this.routePredicateFactories.clear();
		this.routePredicateFactories.addAll(beans.values());
		Map<String,AbstractRoutePredicateFactory> classFactoryMap = new HashMap<>();
		for (AbstractRoutePredicateFactory routePredicateFactory : beans.values()) {
			Class<? extends AbstractRoutePredicateFactory> factoryClass = routePredicateFactory.getClass();
			classFactoryMap.put(factoryClass.getName(),routePredicateFactory);
		}
	}

	public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Class<StripPrefixGatewayFilterFactory> prefixGatewayFilterFactoryClass = StripPrefixGatewayFilterFactory.class;
		System.out.println(prefixGatewayFilterFactoryClass.getName());
		Constructor<StripPrefixGatewayFilterFactory> constructor = prefixGatewayFilterFactoryClass.getConstructor();
		StripPrefixGatewayFilterFactory factory = constructor.newInstance();
		Class<StripPrefixGatewayFilterFactory.Config> configClass = factory.getConfigClass();
		System.out.println(configClass);
		Constructor<StripPrefixGatewayFilterFactory.Config> configClassConstructor = configClass.getConstructor();
		Field[] declaredFields = configClass.getDeclaredFields();
		for (Field declaredField : declaredFields) {
			System.out.println(declaredField.getName());
		}
		StripPrefixGatewayFilterFactory.Config config = configClassConstructor.newInstance();
		String s = NameUtils.normalizeFilterFactoryName(StripPrefixGatewayFilterFactory.class);
		System.out.println(s);

	}


}
