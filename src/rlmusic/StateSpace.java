
package rlmusic;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.Random;

public class StateSpace {
    private float lr = 0.1f;
    private float df = 0.5f;
    private MusicCritic mc;
    private Random r;
    private byte count = 0;
    private int episodeCount = 0;
    private int episodeRepeat = 0;
    public boolean doneHere = false;
    private int episodeLength = 8;
    private int[] greedy;
    public ArrayList<Integer[]> children;
    public TIntArrayList parents;
    private int rootID = 0;
    private NeuralNetwork ann;
    private TDoubleArrayList inputs,nextinputs;
    private int nextaction;
    private PatternTracking patternTracking;
    private float[] greedyRewards;
    private int[] greedyAbsolutes;
    private float weight1 = 0.4f,weight2 = 0.4f,weight3 = 0.2f;
    private int minNote = 21; 
    private int maxNote = 108;
    
    public StateSpace(MusicCritic mc, Workspace ws, double tempDiscount) {
        this.mc = mc;
        ann = new NeuralNetwork();
        patternTracking = new PatternTracking(tempDiscount);
        inputs = new TDoubleArrayList();
        nextinputs = new TDoubleArrayList();
        r = new Random();
    }
    
    public void stopANN() {
        ann.stopWriting();
    }
    
    public int getRootID() {
        return rootID;
    }
    public int[] getGreedyANN() {
        greedy = new int[episodeLength];
        greedyRewards = new float[episodeLength];
        greedyAbsolutes = new int[episodeLength];
        for (int i = 0; i < episodeLength; i++) {
            greedy[i] = getHighest();
            greedyRewards[i] = mc.assignUtility((byte)greedy[i],false,true);
            greedyAbsolutes[i] = mc.getCurrentNote();
        }
        return greedy;
    }
    
    public int[] getGreedyAbsolutes() {
        return greedyAbsolutes;
    }
    
    public float[] getGreedyRewards() {
        return greedyRewards;
    }
    
    public byte getHighest() {
        double[][] nextLikelihoods = mc.getNextLikelihoods();
        TDoubleArrayList candidateInputs = new TDoubleArrayList();
        byte highestIndex = (byte) r.nextInt(25);
        double highest = 0;
        for (byte i = 0; i < 25; i++) {
            double likelihood1 = weight1*Math.abs(nextLikelihoods[0][i]);
            double likelihood2 = weight2*Math.abs(nextLikelihoods[1][(mc.getCurrentNote() + (i-12))%12]);
            double likelihood3 = weight3*Math.abs(nextLikelihoods[2][(int)Math.signum(i-12)+1])/4.4f;
            double dissonance = mc.getDissonance(i);
            double distance = (mc.getCurrentNote()-64.5)/43.5;
            double over = (mc.getCurrentNote() + (i-12) > maxNote) ? 1:0;
            double under = (mc.getCurrentNote() + (i-12) < minNote) ? 1:0;
            candidateInputs.addAll(new double[]{likelihood1,likelihood2,likelihood3,dissonance,distance,over,under});
            ann.setInputs(candidateInputs);
            ann.feedForward();
            double value = ann.predict();
            if (highest < value) {highest = value; highestIndex = i;}
            candidateInputs = new TDoubleArrayList();
        }
        return highestIndex;
    }
    
    public void annEpisode(int theEpisodeRepeat) {
        float reward;
        float currentreward = 0;
        count = 0;
        nextaction = patternTracking.getAction();
        inputs = new TDoubleArrayList();
        inputs.addAll(mc.getInputs());
        while (count < episodeLength) {
            reward = mc.assignUtility((byte)nextaction,false,true);
            double[][] nextLikelihoods = mc.getNextLikelihoods();
            double likelihood1 = weight1*Math.abs(nextLikelihoods[0][nextaction]);
            double likelihood2 = weight2*Math.abs(nextLikelihoods[1][(mc.getCurrentNote() + (nextaction-12))%12]);
            double likelihood3 = weight3*Math.abs(nextLikelihoods[2][(int)Math.signum(nextaction-12)+1])/4.4f;
            double dissonance = mc.getDissonance((byte)nextaction);
            double distance = (mc.getCurrentNote()-64.5)/43.5;
            double over = (mc.getCurrentNote() + (nextaction-12) > maxNote) ? 1:0;
            double under = (mc.getCurrentNote() + (nextaction-12) < minNote) ? 1:0;
            nextinputs.addAll(new double[]{likelihood1,likelihood2,likelihood3,dissonance,distance,over,under});
            patternTracking.addReward(reward);
            ann.setInputs(inputs);
            ann.feedForward();
            double current = ann.predict();
            if (Double.isNaN(current)) System.err.println("NaN error in StateSpace");
            ann.setInputs(nextinputs);
            ann.feedForward();
            double next = ann.predict();
            double qValue = current + lr*(currentreward + df*(next-current));
            ann.setInputs(inputs);
            ann.train(qValue);
            currentreward = reward;
            inputs = nextinputs;
            nextinputs = new TDoubleArrayList();
            count++;
        }
        mc.newEpisode();
    }
    
    public TIntArrayList getParents() {
        return parents;
    }
    
    public void resetTemp() {patternTracking.resetTemp();}
    public void decrementTemp() {patternTracking.decrementTemp();
    }
    
    public void incrementEpisodeCount() {episodeCount++;}
    public void incrementEpisodeRepeat() {episodeRepeat++;}
    
    
}
