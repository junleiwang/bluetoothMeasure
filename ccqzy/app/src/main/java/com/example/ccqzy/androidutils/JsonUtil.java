package com.example.ccqzy.androidutils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.umeng.message.proguard.T;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSON Mapper
 *
 * @author jade (originally)
 * @author zhe.yangz imported.
 */
public class JsonUtil {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private static final JsonFactory jf = new JsonFactory();
	static {
		jf.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		jf.configure(Feature.ALLOW_SINGLE_QUOTES, true);
	}
	private static final ObjectMapper objectMapper = new ObjectMapper(jf);
	static {
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// objectMapper.configure(Feature.ALLOW_COMMENTS, true);
		// objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// objectMapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		// objectMapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		// objectMapper.configure(Feature.INTERN_FIELD_NAMES, true);
		// objectMapper.configure(Feature.CANONICALIZE_FIELD_NAMES, true);
		// SerializationConfig sf = objectMapper.getSerializationConfig();
		// objectMapper.configure(sf.with(new SimpleDateFormat(DATE_FORMAT)),
		// true);
		// DeserializationConfig df = objectMapper.getDeserializationConfig();
		// objectMapper.setDeserializationConfig(df.with(new
		// SimpleDateFormat(DATE_FORMAT)));

	}

	public static <T> T json2pojo(String jsonAsString, Class<T> pojoClass)
			throws JsonMappingException, JsonParseException, IOException {
		return objectMapper.readValue(jsonAsString, pojoClass);
	}

	public static Map<?, ?> json2map(String jsonAsString)
			throws JsonMappingException, JsonParseException, IOException {
		return objectMapper.readValue(jsonAsString, Map.class);
	}

	public static Map<?, ?> json2map(InputStream istream)
			throws JsonMappingException, JsonParseException, IOException {
		return objectMapper.readValue(istream, Map.class);
	}

	public static JsonNode json2node(String jsonAsString)
			throws JsonProcessingException, IOException {
		return objectMapper.readTree(jsonAsString);
	}

	public static JsonNode json2node(InputStream istream)
			throws JsonProcessingException, IOException {
		return objectMapper.readTree(istream);
	}

	public static JsonNode json2node(Reader reader)
			throws JsonProcessingException, IOException {
		return objectMapper.readTree(reader);
	}

	public static <T> T json2value(Reader reader, Class<T> type)
			throws IOException, JsonParseException, JsonMappingException {
		return objectMapper.readValue(reader, type);
	}

	public static Map<?, ?> node2map(JsonNode json)
			throws JsonProcessingException, IOException {
		if (json == null) {
			return null;
		}
		JsonParser jp = null;
		try {
			jp = json.traverse();
			return objectMapper.readValue(jp, Map.class);
		} finally {
			if (jp != null) {
				try {
					jp.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	public static <T> T node2pojo(JsonNode json, Class<T> pojoClass)
			throws JsonProcessingException, IOException {
		if (json == null) {
			return null;
		}
		JsonParser jp = null;
		try {
			jp = json.traverse();
			return objectMapper.readValue(jp, pojoClass);
		} finally {
			if (jp != null) {
				try {
					jp.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	public static <T> T node2pojo(JsonNode json, TypeReference<T> type)
			throws JsonProcessingException, IOException {

		if (json == null) {
			return null;
		}
		JsonParser jp = null;
		try {
			jp = json.traverse();
			return objectMapper.readValue(jp, type);
		} finally {
			if (jp != null) {
				try {
					jp.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	public static String pojo2json(Object pojo) throws JsonGenerationException,
			JsonMappingException, IOException {
		final StringWriter sw = new StringWriter();
		JsonGenerator jg = null;
		try {
			jg = jf.createGenerator(sw);
			objectMapper.writeValue(jg, pojo);
			return sw.toString();
		} finally {
			if (jg != null) {
				try {
					jg.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static String node2json(JsonNode node)
			throws JsonProcessingException, IOException {
		final StringWriter sw = new StringWriter();
		JsonGenerator jg = null;
		try {
			jg = jf.createGenerator(sw);
			objectMapper.writeTree(jg, node);
			return sw.toString();
		} finally {
			if (jg != null) {
				try {
					jg.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static void node2json(JsonNode node, Writer w)
			throws JsonGenerationException, JsonMappingException, IOException {
		JsonGenerator jg = null;
		try {
			jg = jf.createGenerator(w);
			objectMapper.writeTree(jg, node);
		} finally {
			if (jg != null) {
				try {
					jg.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static ObjectNode createObjectNode() {
		return objectMapper.createObjectNode();
	}

	public static ArrayNode createArrayNode() {
		return objectMapper.createArrayNode();
	}

	public static JsonNode parser2node(JsonParser jp)
			throws JsonProcessingException, IOException {
		return objectMapper.readTree(jp);
	}

	public static <T> List<T> json2list(String jsonArrayStr, Class<T> clazz)
			throws Exception {
		List<Map<String, Object>> list = objectMapper.readValue(jsonArrayStr,
				new TypeReference<List<T>>() {
				});
		List<T> result = new ArrayList<T>();
		for (Map<String, Object> map : list) {
			result.add(map2pojo(map, clazz));
		}
		return result;
	}

	public static <T> T map2pojo(Map map, Class<T> clazz) {
		return objectMapper.convertValue(map, clazz);
	}

	public static String object2json(Object obj) {
		// StringBuilder json = new StringBuilder();
		// if (obj == null) {
		// json.append("\"");
		// } else if (obj instanceof String || obj instanceof Integer || obj
		// instanceof Float
		// || obj instanceof Boolean || obj instanceof Short || obj instanceof
		// Double
		// || obj instanceof Long || obj instanceof BigDecimal || obj instanceof
		// BigInteger
		// || obj instanceof Byte) {
		// json.append("\"").append(string2json(obj.toString())).append("\"");
		// } else if (obj instanceof Object[]) {
		// json.append(array2json((Object[]) obj));
		// } else if (obj instanceof List) {
		// json.append(list2json((List<?>) obj));
		// } else if (obj instanceof Map) {
		// json.append(map2json((Map<?, ?>) obj));
		// } else if (obj instanceof Set) {
		// json.append(set2json((Set<?>) obj));
		// } else {
		// json.append(bean2json(obj));
		// }
		try {
			final StringWriter sw = new StringWriter();
			JsonGenerator jg = null;
			try {
				jg = jf.createGenerator(sw);
				objectMapper.writeValue(jg, obj);
				return sw.toString();
			} finally {
				if (jg != null) {
					try {
						jg.close();
					} catch (IOException e1) {
					}
				}
			}
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return json.toString();
		return null;
	}

	public static String bean2json(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		Field[] fileds = null;
		try {
			fileds = bean.getClass().getDeclaredFields();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fileds != null) {
			for (int i = 0; i < fileds.length; i++) {
				try {
					String name = object2json(fileds[i].getName());

					Method m = (Method) bean.getClass().getMethod(
							"get" + getMethodName(fileds[i].getName()));
					String value = object2json(m.invoke(bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	public static String list2json(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String array2json(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static List array2List2(Object[] arr) {
		List list = new ArrayList();
		if (arr == null)
			return list;
		list = Arrays.asList(arr);
		return list;
	}

	public static Object[] jsonArray2Array(List<T> list, String jsonStr, Class<T> pojoClass) {

		ObjectMapper objectMapper = new ObjectMapper();
		 try {
			objectMapper.readValue(jsonStr,pojoClass).getClass();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		list = list.getStudent();
		return null;
	}

	public static String map2json(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(object2json(key));
				json.append(":");
				json.append(object2json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	public static String set2json(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String string2json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	private static String getMethodName(String fildeName) throws Exception {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}


}
