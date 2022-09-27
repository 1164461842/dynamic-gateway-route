package com.yjntc.gateway.dynamic;

import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangkangsheng
 * @email 1164461842@qq.com
 * @create 2022-09-27 08:43
 */
public class DataSizePropertyTypeMapping implements PropertyTypeMapping<DataSize> {

	@Override
	public DataSize map(String text) {
		return DataSize.parse(text, DataUnit.BYTES);
	}

	public static void main(String[] args) {
		ListStringPropMapping typeMapping = new ListStringPropMapping();
		System.out.println(PropertyTypeMapping.targetName(typeMapping));

		MapStringStringPropMapping mapping = new MapStringStringPropMapping();
		System.out.println(PropertyTypeMapping.targetName(mapping));
		// DataSize dataSize = typeMapping.map("100");
		// System.out.println(dataSize.toBytes());

		// System.out.println(dataSize);



	}


}
