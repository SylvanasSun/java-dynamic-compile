package com.sylvanas.java.dynamic.compile.test;

import com.sylvanas.java.dynamic.compile.JavaStringDynamicCompiler;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by SylvanasSun on 2017/2/12.
 */
public class JavaStringDynamicCompilerTest {

    JavaStringDynamicCompiler compiler;

    @Before
    public void setUp() {
        compiler = JavaStringDynamicCompiler.getInstance();
    }

    static final String MULTIPLY_JAVA = "/* a simple multiply class */ "
            + "package com.sylvanas.java.dynamic.compile.test.clazz;   "
            + "public class Multiply {                                 "
            + "     private int sum;                                   "
            + "     public int compute(int a,int b) {                  "
            + "         sum = a * b;                                   "
            + "         return sum;                                    "
            + "     }                                                  "
            + "}                                                       ";

    @Test
    public void testMultiplyClass() throws Exception {
        Map<String, byte[]> results = compiler.compile("Multiply.java", MULTIPLY_JAVA);
        assertEquals(1, results.size());
        assertTrue(results.containsKey("com.sylvanas.java.dynamic.compile.test.clazz.Multiply"));
        Class<?> multiply = compiler.loadClass("com.sylvanas.java.dynamic.compile.test.clazz.Multiply", results);
        Method m = multiply.getMethod("compute", int.class, int.class);
        int mResult = (int) m.invoke(multiply.newInstance(), 2, 2);
        assertEquals(mResult, 4);
    }
}
