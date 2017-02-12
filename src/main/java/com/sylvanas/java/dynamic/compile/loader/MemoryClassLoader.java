package com.sylvanas.java.dynamic.compile.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据classBytes加载内存中的类.
 * <p>
 * Created by SylvanasSun on 2017/2/12.
 */
public class MemoryClassLoader extends URLClassLoader {

    private Map<String, byte[]> classBytes = new HashMap<>();

    public MemoryClassLoader(Map<String, byte[]> classBytes) {
        super(new URL[0], MemoryClassLoader.class.getClassLoader());
        this.classBytes.putAll(classBytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] buffer = classBytes.get(name);
        if (buffer == null) return super.findClass(name);
        classBytes.remove(name);
        return defineClass(name, buffer, 0, buffer.length);
    }
}
