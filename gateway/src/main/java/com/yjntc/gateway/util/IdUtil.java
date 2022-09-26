package com.yjntc.gateway.util;

import java.util.Deque;
import java.util.UUID;

/**
 * @author wangkangsheng
 * @email 1164461842@qq.com
 * @create 2022-09-26 16:48
 */
public class IdUtil {

	public static String simpleUuid(){
		return uuid().replace("-","");
	}

	public static String uuid(){
		return UUID.randomUUID().toString();
	}




}
