/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rlmusic;

import java.util.Random;
import java.util.ArrayList;
/**
 *
 * @author user
 */
public class DurationPattern {
    
    private int[] pattern;
    private int counter = -1;
    private Random r;
    private int limit = 3;
    
    public DurationPattern() {
        r = new Random();
        ArrayList<Integer> currentStack = new ArrayList();
        currentStack.add(1);
        ArrayList<Integer> nextStack = new ArrayList();
        for (int i = 0; i < limit; i++) {
            for (Integer n : currentStack) {
                if (r.nextBoolean()) {
                    nextStack.add(n*2);
                    nextStack.add(n*2);
                }
                else {
                    nextStack.add(n);
                }
            }
            currentStack = nextStack;
            nextStack = new ArrayList();
        }
        pattern = new int[currentStack.size()];
        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = currentStack.get(i);
            //System.out.println(pattern[i]);
        }
    }
    
    public int consume() {
        counter++;
        if (counter > pattern.length - 1) {counter = 0; return -1; }
        else {return pattern[counter];}
    }
    
    
}
