package ru.job4j;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestTest extends TestCase {
    public TestTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTest.class);
    }

    public void testApp() {
        assertTrue(true);
    }
}
