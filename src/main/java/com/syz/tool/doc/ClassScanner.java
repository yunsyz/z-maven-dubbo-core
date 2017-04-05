package com.syz.tool.doc;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassScanner {
	private String resourceRoot;

	public ClassScanner(String resourceRoot) {
		this.resourceRoot = resourceRoot;
	}

	public String scan(Class<?> clazz, boolean isMarkDown) {
		StringBuilder sb = new StringBuilder();
		try {
			CommentScanner classCommentScanner = new ClassCommentScanner(resourceRoot);
			List<CommentEntry> classCommentList = classCommentScanner.scan(clazz.getName());
			String classComment = getCommentValue(classCommentList, clazz.getSimpleName());
			if (isMarkDown) {
				sb.append("#");
			}
			if (classComment.length() > 0) {
				sb.append(classComment).append(":");
			}
			sb.append(clazz.getSimpleName()).append("\n");
			Method[] methods = clazz.getDeclaredMethods();
			sortMethods(methods);
			CommentScanner methodCommentScanner = new MethodCommentScanner(resourceRoot);
			List<CommentEntry> methodCommentList = methodCommentScanner.scan(clazz.getName());
			for (Method method : methods) {
				Class<?>[] inClazzs = method.getParameterTypes();
				Type[] inTypes = method.getGenericParameterTypes();
				Class<?> outClazz = method.getReturnType();
				Type outType = method.getGenericReturnType();
				String methodName = method.getName();
				String methodComment = getCommentValue(methodCommentList, methodName);
				if (isMarkDown) {
					sb.append("##");
				}
				if (methodComment.length() > 0) {
					sb.append(methodComment).append(":");
				}
				sb.append(methodName).append("\n");
				if (isMarkDown) {
					sb.append("###");
				}
				sb.append("请求参数：\n");
				if (isMarkDown) {
					sb.append("```\n");
				}
				for (int i = 0; i < inClazzs.length; i++) {
					Class<?> inClazz = inClazzs[i];
					sb.append("参数").append(i).append(":\n");
					Type inType = inTypes[i];
					Map<String, Class<?>> fieldClazzMap = new HashMap<>();
					List<CommentEntry> commentList = getFieldCommentList(inClazz);
					scanField(inClazz, inType, "", fieldClazzMap, sb, commentList);
				}
				if (isMarkDown) {
					sb.append("```\n");
				}
				if (isMarkDown) {
					sb.append("###");
				}
				sb.append("返回参数：\n");
				Map<String, Class<?>> fieldClazzMap = new HashMap<>();
				List<CommentEntry> commentList = getFieldCommentList(outClazz);
				if (isMarkDown) {
					sb.append("```\n");
				}
				scanField(outClazz, outType, "", fieldClazzMap, sb, commentList);
				if (isMarkDown) {
					sb.append("```\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private void sortMethods(Method[] methods) {
		for (int i = 1; i < methods.length; i++) {
			for (int j = i; j > 0; j--) {
				String s1 = methods[j].getName();
				String s2 = methods[j - 1].getName();
				if (s1.compareTo(s2) < 0) {
					Method temp = methods[j];
					methods[j] = methods[j - 1];
					methods[j - 1] = temp;
				} else {
					break;
				}
			}
		}
	}

	private void scanField(Class<?> clazz, Type type, String prefix, Map<String, Class<?>> fieldClazzMap,
			StringBuilder sb, List<CommentEntry> commentList) {
		try {
			if (isPrimitive(clazz)) {
				sb.append(prefix).append("\t").append(clazz.getName()).append("\n");
			} else if (isMap(clazz)) {
				sb.append(prefix).append("\t").append("{}//map\n");
			} else if (isList(clazz)) {
				sb.append("[\n");
				if (type instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) type;
					Type subType = pt.getActualTypeArguments()[0];
					if (subType instanceof ParameterizedType) {
						sb.append(prefix).append("{\n");
						sb.append(prefix).append("\tParameterizedType").append("\t").append(subType.toString())
								.append("\n");
						sb.append(prefix).append("}\n");
					} else {
						Class<?> subClazz = (Class<?>) subType;
						List<CommentEntry> subCommentList = getFieldCommentList(subClazz);
						scanField(subClazz, subType, "", fieldClazzMap, sb, subCommentList);
					}
				}
				sb.append("]\n");
			} else {
				BeanInfo info = Introspector.getBeanInfo(clazz);
				PropertyDescriptor[] props = info.getPropertyDescriptors();
				sb.append(prefix).append("{\n");
				for (PropertyDescriptor prop : props) {
					String fieldName = prop.getName();
					if (fieldName.equals("class")) {
						continue;
					}
					Method readMethod = prop.getReadMethod();
					Class<?> fieldClazz = readMethod.getReturnType();
					Type fieldType = readMethod.getGenericReturnType();
					String fieldClazzName = fieldClazz.getName();
					String fieldComment = getCommentValue(commentList, fieldName);
					if (isPrimitive(fieldClazz)) {
						sb.append(prefix).append("\t").append(prop.getName()).append("\t").append(fieldClazzName);
						if (fieldComment.length() > 0) {
							sb.append("//").append(fieldComment);
						}
						sb.append("\n");
					} else if (isMap(fieldClazz)) {
						sb.append(prefix).append("\t").append(prop.getName()).append("\t").append(fieldClazzName);
						if (fieldComment.length() > 0) {
							sb.append("//").append(fieldComment);
						}
						sb.append("\n");
					} else if (isList(fieldClazz)) {
						sb.append(prefix).append("\t").append(prop.getName()).append(":[");
						if (fieldComment.length() > 0) {
							sb.append("//").append(fieldComment);
						}
						sb.append("\n");
						if (fieldType instanceof ParameterizedType) {
							ParameterizedType pt = (ParameterizedType) fieldType;
							Type subType = pt.getActualTypeArguments()[0];
							if (subType instanceof ParameterizedType) {
								sb.append(prefix).append("\t{\n");
								sb.append(prefix).append("\t\tParameterizedType").append("\t")
										.append(subType.toString()).append("\n");
								sb.append(prefix).append("\t}\n");
							} else {
								Class<?> subClazz = (Class<?>) subType;
								Class<?> oldClazz = fieldClazzMap.get(subClazz.getName());
								if (oldClazz == null) {
									fieldClazzMap.put(subClazz.getName(), subClazz);
									List<CommentEntry> subCommentList = getFieldCommentList(subClazz);
									scanField(subClazz, subType, prefix + "\t", fieldClazzMap, sb, subCommentList);
								}
							}
						}
						sb.append(prefix).append("\t]\n");
					} else {
						Class<?> oldClazz = fieldClazzMap.get(fieldClazz.getName());
						if (oldClazz == null) {
							fieldClazzMap.put(fieldClazz.getName(), fieldClazz);
							sb.append(prefix).append("\t").append(prop.getName()).append(":");
							List<CommentEntry> subCommentList = getFieldCommentList(fieldClazz);
							if (fieldComment.length() > 0) {
								sb.append("//").append(fieldComment);
							}
							sb.append("\n");
							scanField(fieldClazz, fieldType, prefix + "\t", fieldClazzMap, sb, subCommentList);
						} else {
							sb.append(prefix).append("\t").append(prop.getName()).append("\t").append(fieldClazzName);
							if (fieldComment.length() > 0) {
								sb.append("//").append(fieldComment);
							}
							sb.append("\n");
						}
					}

				}
				sb.append(prefix).append("}\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isPrimitive(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return true;
		}
		if (clazz.getName().startsWith("java.lang.")) {
			return true;
		}
		if (clazz.equals(java.util.Date.class)) {
			return true;
		}
		if (clazz.equals(java.sql.Date.class)) {
			return true;
		}
		if (clazz.equals(BigDecimal.class)) {
			return true;
		}
		if (clazz.equals(BigInteger.class)) {
			return true;
		}
		return false;
	}

	private boolean isMap(Class<?> clazz) {
		String clazzName = clazz.getName();
		return clazzName.indexOf("Map") > 0;
	}

	private boolean isList(Class<?> clazz) {
		if ("java.util.List".equals(clazz.getName())) {
			return true;
		}
		Class<?>[] interfaceClazzs = clazz.getInterfaces();
		for (Class<?> interfaceClazz : interfaceClazzs) {
			if ("java.util.List".equals(interfaceClazz.getName())) {
				return true;
			}
		}
		return false;
	}

	private List<CommentEntry> getFieldCommentList(Class<?> type) {
		List<CommentEntry> list = new ArrayList<>();
		CommentScanner fieldCommentScanner = new FieldCommentScanner(resourceRoot);
		List<String> clazzNameList = getAllClassName(type);
		for (String clazzName : clazzNameList) {
			List<CommentEntry> subList = fieldCommentScanner.scan(clazzName);
			for (CommentEntry commentEntry : subList) {
				list.add(commentEntry);
			}
		}
		return list;
	}

	private List<String> getAllClassName(Class<?> clazz) {
		List<String> list = new ArrayList<>();
		searchSuperClassName(clazz, list);
		list.add(clazz.getName());
		return list;
	}

	private void searchSuperClassName(Class<?> clazz, List<String> list) {
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null) {
			String superClazzName = superClazz.getName();
			if (superClazzName.equals("java.lang.Object")) {
				return;
			} else {
				list.add(superClazzName);
				searchSuperClassName(superClazz, list);
			}
		} else {
			return;
		}
	}

	private String getCommentValue(List<CommentEntry> commentList, String key) {
		String commentValue = "";
		if (commentList == null || commentList.size() == 0) {
			return commentValue;
		}
		for (CommentEntry commentEntry : commentList) {
			String commentKey = commentEntry.getKey();
			if (key.equals(commentKey)) {
				commentValue = commentEntry.getValue();
				break;
			}
		}
		return commentValue;
	}

	public String getResourceRoot() {
		return resourceRoot;
	}

	public void setResourceRoot(String resourceRoot) {
		this.resourceRoot = resourceRoot;
	}

}
