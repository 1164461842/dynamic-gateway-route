package com.yjntc.gateway.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.yjntc.gateway.bean.FactoryDefinition;
import org.springframework.cloud.gateway.support.Configurable;

import java.lang.reflect.*;
import java.util.Set;

/**
 * @author wangkangsheng
 * @email 1164461842@qq.com
 * @create 2022-09-26 16:59
 */
public class ConfigTypeUtil {

	public String getType(Class<?> clazz){

		return "";
	}


	public static void main(String[] args) throws NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Set<Class<?>> classSet = ClassUtil.scanPackageBySuper("org.springframework.cloud.gateway", Configurable.class);
		int count = 0;
		for (Class<?> aClass : classSet) {
			System.out.println("-----------");
			System.out.println(aClass.getName());
			if (aClass.isInterface()){
				continue;
			}
			if (Modifier.isAbstract(aClass.getModifiers())){
				continue;
			}
			Constructor<?> constructor = null;
			try {
				constructor = aClass.getConstructor();
			} catch (Throwable e) {
				System.out.println("*****************************" + aClass.getName());
				continue;
			}
			count++;
			Configurable configurable = (Configurable)constructor.newInstance();
			Class configClass = configurable.getConfigClass();
			System.out.println("conf class："+configClass);
			Field[] declaredFields = configClass.getDeclaredFields();
			for (Field field : declaredFields) {
				Type genericType = field.getGenericType();
				System.out.println("ftype：" + field.getName() +  "--"+genericType);
			}
		}
		System.out.println("===================");
		Class<FactoryDefinition> factoryDefinitionClass = FactoryDefinition.class;
		Field[] declaredFields = factoryDefinitionClass.getDeclaredFields();
		for (Field field : declaredFields) {
			Type genericType = field.getGenericType();
			System.out.println(genericType);
		}
		System.out.println("--------");
		Field field = factoryDefinitionClass.getDeclaredField("configDefinitions");
		Type genericType = field.getGenericType();
		System.out.println(genericType);
		System.out.println("总数："+count);
	}


}
