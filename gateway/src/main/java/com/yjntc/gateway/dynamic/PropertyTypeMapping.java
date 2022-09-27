package com.yjntc.gateway.dynamic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author wangkangsheng
 * @email 1164461842@qq.com
 * @create 2022-09-27 08:37
 */
public interface PropertyTypeMapping<T> {


	static String targetName(PropertyTypeMapping<?> propertyTypeMapping){
		Type[] genericInterfaces = propertyTypeMapping.getClass().getGenericInterfaces();
		for (Type type : genericInterfaces) {
			String name = PropertyTypeMapping.class.getTypeName();
			if (type instanceof ParameterizedType pt){
				String typeName = pt.getRawType().getTypeName();
				if (name.equals(typeName)){
					Type argument = pt.getActualTypeArguments()[0];
					if (argument instanceof ParameterizedType ipt){
						return ipt.getTypeName();
					}else if (argument instanceof Class<?> ict){
						return ict.getName();
					}else {
						return type.getTypeName();
					}
				}
			}else if (type instanceof Class<?> ct){
				continue;
			}
		}
		return null;
	};

	T map(String text);


}
