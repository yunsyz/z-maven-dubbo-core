package com.syz.tool.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public abstract class CommentScanner {
	private String resourceRoot;

	public CommentScanner(String resourceRoot) {
		this.resourceRoot = resourceRoot;
	}

	private String root = System.getProperty("user.dir");

	protected String valuePrefixRegex;

	protected String valueSuffixRegex;

	protected String keyRegex;

	private String separator = File.separator;

	public List<CommentEntry> scan(String clazzName) {
		List<CommentEntry> list = new ArrayList<>();
		if (clazzName == null) {
			return list;
		}
		setValuePrefixRegex();
		setValueSuffixRegex();
		setKeyRegex();
		try {
			String path = root + separator + resourceRoot + separator + clazzName.replace(".", separator);
			path = path + ".java";
			File file = new File(path);
			if (file.exists()) {
				List<String> lines = FileUtils.readLines(file, "UTF-8");
				list = parseLines(lines);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<CommentEntry> parseLines(List<String> lines) {
		if (lines == null || lines.size() == 0) {
			return null;
		}
		List<CommentEntry> list = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			line = line.trim();
			if (line.matches(this.valuePrefixRegex)) {
				CommentEntry commentEntry = new CommentEntry();
				boolean flag = false;
				StringBuilder sb = new StringBuilder();
				for (int j = i; j < lines.size(); j++) {
					String valueLine = lines.get(j);
					valueLine = valueLine.trim();
					sb.append(valueLine);
					if (valueLine.matches(this.valueSuffixRegex)) {
						for (int k = j + 1; k < lines.size(); k++) {
							String keyLine = lines.get(k);
							keyLine = keyLine.trim();
							if (keyLine.length() == 0) {
								continue;
							}
							if (keyLine.matches(this.keyRegex)) {
								String key = handleCommentKey(keyLine);
								commentEntry.setKey(key);
								flag = true;
							}
							break;
						}
						break;
					}
				}
				if (flag) {
					String value = handleCommentValue(sb.toString());
					commentEntry.setValue(value);
					list.add(commentEntry);
				}
			}
		}
		return list;
	}

	protected abstract String handleCommentKey(String key);

	protected abstract String handleCommentValue(String value);

	protected abstract void setValuePrefixRegex();

	protected abstract void setValueSuffixRegex();

	protected abstract void setKeyRegex();

	public String getResourceRoot() {
		return resourceRoot;
	}

	public void setResourceRoot(String resourceRoot) {
		this.resourceRoot = resourceRoot;
	}

}
