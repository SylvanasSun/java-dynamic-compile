### Java-Dynamic-Compile

在内存中动态编译Java代码.

将String当成源码,它可以输出byte[]作为class的内容并动态生成类.

### Example

```java
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
```
