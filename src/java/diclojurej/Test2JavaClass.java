package diclojurej;


import javax.inject.Inject;

public class Test2JavaClass implements Test2Java {

    @Inject
    public TestJava testJava;

//    public Test2JavaClass(TestJava testJava) {
//        this.testJava = testJava;
//    }

    @Override
    public void test2java() {
        System.out.println("Test 2 java: " + testJava.getTestMessage());
    }
}
