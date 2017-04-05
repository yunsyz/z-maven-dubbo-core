package com.syz.tool.build;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.syz.base.msg.BaseRequestMsg;
import com.syz.base.msg.BaseResponseMsg;

public class MsgFileBuilder {

	private static String root = System.getProperty("user.dir");
	private static String resourceRoot ="/src/main/java/";

	private static String encoding = "UTF-8";

	private static String reg = "\\s+";

	private static String baseReqClassName = BaseRequestMsg.class.getName();

	private static String baseRespClassName = BaseResponseMsg.class.getName();

	public static void main(String[] args) throws IOException {
		String packageName = "com.syz.service.msg";
		String methodDescFilePath = resourceRoot+"com/syz/tool/build/template/desc.txt";
		methodDescFilePath = root.concat(methodDescFilePath);
		List<Map<String, String>> mehtodDescList = covert(methodDescFilePath);
		build(mehtodDescList, packageName);
	}

	public static void build(List<Map<String, String>> mehtodDescList, String packageName) {
		for (Map<String, String> map : mehtodDescList) {
			generateFile(map, 0, packageName);
			generateFile(map, 1, packageName);
			printServiceMethod(map);
		}
	}

	public static void printServiceMethod(Map<String, String> map) {
		String _className = map.get("_className");
		String _description = map.get("_description");
		StringBuilder sb = new StringBuilder();
		sb.append("  /**\n");
		sb.append("  * ");
		sb.append(_description);
		sb.append("\n");
		sb.append("  * @param req\n");
		sb.append("  * @return");
		sb.append("\n");
		sb.append("  */\n");
		sb.append(" ");
		sb.append(_className);
		sb.append("Resp ");
		sb.append(_className.toLowerCase());
		sb.append("(");
		sb.append(_className);
		sb.append("Req req);\n");
		System.out.println(sb.toString());
	}

	public static void generateFile(Map<String, String> map, int type, String packageName) {
		String _className = map.get("_className");
		String _description = map.get("_description");
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("package ");
			sb.append(packageName);
			sb.append(";").append("\n\n");
			sb.append("import ");
			if (type == 0) {
				sb.append(baseReqClassName);
			} else {
				sb.append(baseRespClassName);
			}
			sb.append(";").append("\n\n");
			sb.append("/**\n");
			sb.append(" * @ClassName: ");
			sb.append(_className);
			sb.append("\n");
			sb.append(" * @Description: ");
			sb.append(_description);
			if (type == 0) {
				sb.append(" 请求参数");
			} else {
				sb.append(" 返回参数");
			}
			sb.append("\n");
			sb.append(" * @author: syz\n");
			sb.append(" * @date: ");
			sb.append(date);
			sb.append("\n");
			sb.append(" */\n");
			sb.append("public class ");
			sb.append(_className);
			if (type == 0) {
				sb.append("Req");
			} else {
				sb.append("Resp");
			}
			sb.append(" extends ");
			if (type == 0) {
				sb.append(baseReqClassName.substring(baseReqClassName.lastIndexOf(".") + 1));
			} else {
				sb.append(baseRespClassName.substring(baseRespClassName.lastIndexOf(".") + 1));
			}
			sb.append(" {\n\n");
			sb.append("    private static final long serialVersionUID = 1L;");
			sb.append("\n\n");
			sb.append("}");
			String str = sb.toString();
			String path = root.concat(resourceRoot).concat(packageName.replace(".", "/"));
			if (type == 0) {
				path = path + "/" + _className + "Req.java";
			} else {
				path = path + "/" + _className + "Resp.java";
			}
			FileUtils.writeStringToFile(new File(path), str, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Map<String, String>> covert(String methodDescFilePath) {
		List<Map<String, String>> list = new ArrayList<>();
		try {
			List<String> lines = FileUtils.readLines(new File(methodDescFilePath), encoding);
			for (String line : lines) {
				String str = line;
				str = str.trim();
				if (str.length() == 0) {
					continue;
				}
				str = str.replaceAll(reg, ",");
				String[] temp = str.split(",");
				String _className = temp[0];
				_className = _className.toUpperCase();
				String _description = temp[1];
				Map<String, String> map = new LinkedHashMap<>();
				map.put("_className", _className);
				map.put("_description", _description);
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}