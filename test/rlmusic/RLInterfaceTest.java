/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rlmusic;

import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author user
 */
public class RLInterfaceTest {
    
    public RLInterfaceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class RLInterface.
     */
    @Test
    public void testMain() {
        System.out.println("main saturation testing for Q-learning");
        String[] args = new String[2];
        assertEquals(1,1);
        long end = System.currentTimeMillis() + 300000;
        args[1] = "50";
        RLInterface.main(args);
        RLInterface.main(args);
        while (System.currentTimeMillis() < end) {}
        end = System.currentTimeMillis() + 30000;
        args[1] = "200";
        RLInterface.main(args);
        while (System.currentTimeMillis() < end) {}
        end = System.currentTimeMillis() + 30000;
        args[1] = "300";
        RLInterface.main(args);
        while (System.currentTimeMillis() < end) {}
        end = System.currentTimeMillis() + 30000;
        args[1] = "400";
        RLInterface.main(args);
        while (System.currentTimeMillis() < end) {}
        end = System.currentTimeMillis() + 30000;
        args[1] = "500";
        RLInterface.main(args);
        while (System.currentTimeMillis() < end) {}
        end = System.currentTimeMillis() + 30000;
        args[1] = "600";
        RLInterface.main(args);
        while (System.currentTimeMillis() < end) {}
        end = System.currentTimeMillis() + 30000;
        args[1] = "700";
        RLInterface.main(args);
        while (System.currentTimeMillis() < end) {}
        // TODO review the generated test code and remove the default call to fail.
    }
}
