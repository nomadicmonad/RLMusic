
package rlmusic;

import java.util.ArrayList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.Random;

public class PatternTracking {
    
    private Random r;
    private int patternLength = 4;
    private ArrayList<Integer[]> patterns;
    private TDoubleArrayList averageRewards;
    private TIntArrayList frequencies;
    private float averageAll = 0;
    private int frequencyTotal = 0;
    private Integer[] currentPattern;
    private int patternIndex;
    private boolean changePattern = true;
    private double temp = 1;
    private double tempDiscount = 1;
    private float patternReward = 0;
    private int currentPatternIndex = -1;
    
    public PatternTracking(double tempDiscount) {
        this.tempDiscount = tempDiscount;
        r = new Random();
        patterns = new ArrayList();
        frequencies = new TIntArrayList();
        averageRewards = new TDoubleArrayList();
    }
    
    public Integer[] generatePattern() {
        Integer[] pattern = new Integer[patternLength];
        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = r.nextInt(25);
        }
        patterns.add(pattern);
        averageRewards.add(averageAll);
        frequencies.add(1);
        return pattern;
    }
    
    public void addReward(float reward) {
        patternReward +=reward;
        if (patternIndex == 3) {
            int frequency = frequencies.get(currentPatternIndex);
            frequencies.set(currentPatternIndex, frequencies.get(currentPatternIndex)+1);
            averageRewards.set(currentPatternIndex,(averageRewards.get(currentPatternIndex)*frequency + patternReward)/(frequency+1));
            averageAll = ((averageAll*frequencyTotal) + patternReward)/(frequencyTotal+1);
            frequencyTotal++;
        }
    }
    
    public Integer getAction() {
        if (patternIndex == patternLength) {changePattern = true; patternIndex = 0;}
        if (changePattern) {
            if (averageRewards.isEmpty() || averageRewards.max()/2 < averageAll) {
                currentPattern = generatePattern();
                currentPatternIndex = patterns.size()-1;
            } else {
                double sum = 0;
                double[] actionProbs = new double[averageRewards.size()];
                boolean done = false;
                boolean bigger = false;
                float prob = r.nextFloat();
                int action = -1;
                out:
                for (int i = 0; i < averageRewards.size(); i++) {
                    if (done) {break out;}
                    double numerator = Math.pow(Math.E,averageRewards.get(i)/temp);
                    double denominator = 0;
                    for (int j = 0; j < averageRewards.size(); j++) {
                        denominator = Math.pow(Math.E,averageRewards.get(j)/temp);
                    }
                    sum += (actionProbs[i] = (numerator/denominator));
                    if ((bigger && (sum > prob)) || i == averageRewards.size()-1) {done = true; action = i;}
                    if (sum < prob) {bigger = true;}
                }
                    currentPattern = patterns.get(action);
                    currentPatternIndex = action;
            }
            changePattern = false;
        }
        return currentPattern[patternIndex++];
    
    }
    
    public void decrementTemp() {temp = temp*tempDiscount;}
    public void resetTemp() {temp = 1;}
    
    
}
