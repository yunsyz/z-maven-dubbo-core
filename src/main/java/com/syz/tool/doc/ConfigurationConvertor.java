package com.syz.tool.doc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

public class ConfigurationConvertor {

    public static List<String> convert(String clazzName) {
        List<String> list = new ArrayList<>();
        list.add(clazzName);
        return list;
    }

    public static List<String> convert(String[] clazzNames) {
        List<String> list = new ArrayList<>();
        for (String clazzName : clazzNames) {
            list.add(clazzName);
        }
        return list;
    }

    public static List<String> convert(Class<?> clazz) {
        List<String> list = new ArrayList<>();
        list.add(clazz.getName());
        return list;
    }

    public static List<String> convert(Class<?>[] clazzs) {
        List<String> list = new ArrayList<>();
        for (Class<?> clazz : clazzs) {
            list.add(clazz.getName());
        }
        return list;
    }

    public static List<String> convert(String packageName, String[] excludes) {
        String[] packageNames = { packageName };
        return convert(packageNames, excludes);
    }

    public static List<String> convert(String[] packageNames,
            String[] excludes) {
        List<String> list = new ArrayList<>();
        try {
            for (String packageName : packageNames) {
                String config = "classpath:/" + packageName.replace(".", "/")
                        + "/**/*Service.class";
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                MetadataReaderFactory factory = new CachingMetadataReaderFactory(
                        resolver);
                Resource[] resources = resolver.getResources(config);
                for (Resource resource : resources) {
                    MetadataReader reader = factory.getMetadataReader(resource);
                    ClassMetadata data = reader.getClassMetadata();
                    boolean flag = data.isInterface();
                    if (flag) {
                        String clazzName = data.getClassName();
                        boolean exclude = checkExcludes(clazzName, excludes);
                        if (exclude) {
                            continue;
                        }
                        list.add(clazzName);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean checkExcludes(String name, String[] excludes) {
        if (excludes == null) {
            return false;
        }
        for (int i = 0; i < excludes.length; i++) {
            if (excludes[i] == null || excludes[i].length() < 1) {
                continue;
            }
            if (name.indexOf(excludes[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

}
