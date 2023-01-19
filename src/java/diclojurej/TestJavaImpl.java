package diclojurej;

//import diclojure.di.PostConstructable;

import javax.annotation.PostConstruct;

public class TestJavaImpl implements TestJava {
    static int count = 0;
    public TestJavaImpl() {
        count++;
    }


    @Override
    public String getTestMessage() {
        return "Hello " + count + " worldddd!";
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("POST CONSTRUCT TestJavaImpl");
    }
}
