package com.sylvanas.java.dynamic.compile;

import com.sylvanas.java.dynamic.compile.loader.MemoryClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * 在内存中动态编译String格式的源码.
 * <p>
 * Created by SylvanasSun on 2017/2/12.
 */
public class JavaStringDynamicCompiler {

    private JavaCompiler compiler;

    private StandardJavaFileManager stdFileManager;

    private JavaStringDynamicCompiler() {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.stdFileManager = compiler.getStandardFileManager(null, null, null);
    }

    private static final class JavaStringDynamicCompilerHolder {
        private static final JavaStringDynamicCompiler INSTANCE = new JavaStringDynamicCompiler();
    }

    public static JavaStringDynamicCompiler getInstance() {
        return JavaStringDynamicCompilerHolder.INSTANCE;
    }

    /**
     * 在内存中编译Java代码.
     * <p>1. 使用MemoryJavaFileManager替代JDK默认的StandardJavaFileManager,达到在编译器请求源码内容时,
     * 不是从文件读取,而是直接返回String.</p>
     * <p>
     * <p>2. 使用MemoryOutputJavaFileObject替代JDK默认的SimpleJavaFileObject,达到在接收到编译器
     * 生成的byte[]内容时,不写入class文件,而是直接保存在内存中.</p>
     *
     * @param filename Java文件名称.
     * @param source   源码,使用String类型.
     * @return 将结果放入Map<String,byte[]>,key对应的是类名(内部静态类,匿名类...),value是class的二进制内容.
     * @throws IOException 抛出IOException.
     */
    public Map<String, byte[]> compile(String filename, String source) throws IOException {
        try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdFileManager)) {
            JavaFileObject javaFileObject = manager.makeStringSource(filename, source);
            JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null,
                    null, null, Collections.singletonList(javaFileObject));
            Boolean result = task.call();
            if (result == null || !result) {
                throw new RuntimeException(this.getClass().getName() + ":" + filename + " Compilation failed.");
            }
            return manager.getClassBytes();
        }
    }

    /**
     * 使用MemoryClassLoader加载内存中编译的classes.
     *
     * @param name       完全类名
     * @param classBytes compiled result as a Map.
     * @return class instance.
     */
    public Class<?> loadClass(String name, Map<String, byte[]> classBytes)
            throws ClassNotFoundException, IOException {
        try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
            return classLoader.loadClass(name);
        }
    }
}
