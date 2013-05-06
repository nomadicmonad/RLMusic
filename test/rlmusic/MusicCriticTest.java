/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rlmusic;

import static org.junit.Assert.fail;
import org.junit.*;

/**
 *
 * @author user
 */
public class MusicCriticTest {
    
    public MusicCriticTest() {
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
     * Test of runHumanTest method, of class MusicCritic.
     */
    @Test
    public void testRunHumanTest() {
        
        //twinkle twinkle: cc gg aa g, ff ee dd c, gg, ff, ee, d
        //gg,ff,ee,d cc gg aa g, ff, ee, dd ,c
        //24,31,33,31 29,28,26,24
        //31,29,28,26
        //31,29,28,26,24,31,33,31,29,28,26,24
        
        //happy birthday: c c d c f e, c c d c g f, c c c# a bflat, g
        // a a blat f g f
        //24,24,26,24,29,28
        //24,24,26,24,31,29
        //24,24,25,33,34, 31
        //33,33,34,29,31,29
        System.out.println("runHumanTest");
        int[] notes = {12,19,14,10,10,11,10,10,19,10,11,10,17,10,11,10,10,19,14,10,10,11,10,10};
        int[] notes2 = {0,0,14,10,17,11,8,0,14,10,19,10,7,0,13,20,13,9,14,0,13,7,14,10};
        MusicCritic instance = new MusicCritic();
        instance.runHumanTest(48,notes);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
