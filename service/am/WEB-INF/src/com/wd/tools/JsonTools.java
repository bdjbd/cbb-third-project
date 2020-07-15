package com.wd.tools;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Json工具类
 * 
 * @author llm
 * @time 2012-4-20
 */
public class JsonTools {
	/**
	 * 简单对象转JSON
	 * 
	 * @param o
	 *            简单对象
	 * @return JSON
	 */
	public static String objectToJson(Object o) {
		GsonBuilder builder = new GsonBuilder();
		// 不转换没有 @Expose 注解的字段
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		String stuStr = gson.toJson(o);
		return stuStr;
	}

	/**
	 * JSON转简单对象
	 * 
	 * @param json
	 *            JSON
	 * @return 简单对象
	 */
	public static Object jsonToObject(String json) {
		GsonBuilder builder = new GsonBuilder();
		// 不转换没有 @Expose 注解的字段
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		return gson.fromJson(json, Object.class);
	}

	/**
	 * List转JSON
	 * 
	 * @param lv
	 *            List<Object>
	 * @return JSON
	 */
	public static String listToJson(List<Object> lv) {
		GsonBuilder builder = new GsonBuilder();
		// 不转换没有 @Expose 注解的字段
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		Type type = new TypeToken<List<Object>>() {
		}.getType(); // 指定集合对象属性
		return gson.toJson(lv, type);
	}

	/**
	 * JSON转List
	 * 
	 * @param json
	 *            JSON
	 * @return List<Object>
	 */
	public static List<Object> jsonToList(String json) {
		GsonBuilder builder = new GsonBuilder();
		// 不转换没有 @Expose 注解的字段
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		Type type = new TypeToken<List<Object>>() {
		}.getType(); // 指定集合对象属性
		return gson.fromJson(json, type);
	}

	/**
	 * Map转JSON
	 * 
	 * @param Map
	 *            Map<String, String>
	 * @return JSON
	 */
	public static String mapToJson(Map<String, Object> map) {
		GsonBuilder builder = new GsonBuilder();
		// 不转换没有 @Expose 注解的字段
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		return gson.toJson(map);
	}

	/**
	 * JSON转Map
	 * 
	 * @param json
	 *            JSON
	 * @return Map<String, String>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String json) {
		GsonBuilder builder = new GsonBuilder();
		// 不转换没有 @Expose 注解的字段
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		return (Map<String, Object>) gson.fromJson(json,
				new TypeToken<Map<String, Object>>() {
				}.getType());
	}
}
