package rlmusic;

import gnu.trove.list.array.TByteArrayList;
import java.util.Random;

public class BaumWelch extends Thread {
    
    private boolean done = false;
    private short currentNote;
    private int stateNumber = 2;
    private double[][] transitions;
    private double[][] outputMatrix;
    private double[][] forwards;
    private double[][] backwards;
    private TByteArrayList emissions;
    private int numberOfEmissionValues = 25;
    private TByteArrayList backupEmissions;
    private double[][] backupTransitions;
    private double[][] backupOutputMatrix;
    private short backUpNote;
    private boolean firstPass = true;
    private double convergence = 0.01;
    private double learningRate = 0.3;
    private double estimateTransition;
    private double estimateOutput;
    
    
    private double sum;
    private float utility;
    private byte[] baumInput;
    private double[] likelihood;
    private double forwardSum;
    private double[] transitionSums;
    private double[][] forwardProbs;
    private double[][] backupForwardProbs;
    private double[][] backwardProbs;
    private int startState;
    private int endState;
    private int cacheSize = 20;
    private int recognitionMode;
    private double[] nextLikelihood;
    
    public boolean getDone() {return done;}
    
    public BaumWelch(short note,int cache,int mode,int number) {
        stateNumber = 2;
        backUpNote = currentNote = note;cacheSize = cache; recognitionMode = mode;numberOfEmissionValues = number;
        forwardProbs = new double[1][1];
        backupForwardProbs = new double[1][1];
        backupEmissions = new TByteArrayList();
        emissions = new TByteArrayList();
        Random r = new Random();
        startState = 0;
        endState = stateNumber-1;
        transitions = new double[stateNumber][stateNumber];
        backupTransitions = new double[stateNumber][stateNumber];
        double[] gaussians = new double[stateNumber];
        for (int i = 0; i < stateNumber; i++) {
            sum = 0;
            for (int j = 0; j < stateNumber; j++) {
                gaussians[j] = Math.abs(r.nextGaussian());
                sum += Math.abs(gaussians[j]);
            }
            for (int j = 0; j < stateNumber; j++) {
                backupTransitions[i][j] = transitions[i][j] = gaussians[j]/(sum*2) + 0.5;
            }
        }
        outputMatrix = new double[stateNumber][numberOfEmissionValues];
        backupOutputMatrix = new double[stateNumber][numberOfEmissionValues];
        gaussians = new double[numberOfEmissionValues];
        for (int i = 0; i < stateNumber; i++) {
            sum = 0;
            for (int j = 0; j < numberOfEmissionValues; j++) {
                gaussians[j] = Math.abs(r.nextGaussian());
                sum += Math.abs(gaussians[j]);
            }
            for (int j = 0; j < numberOfEmissionValues; j++) {
                backupOutputMatrix[i][j] = outputMatrix[i][j] = gaussians[j]/(sum*2) + 0.5;
            }          
        }
    }
    
    @Override
    public void run() {
    }
    
    public short getCurrentNote() {return currentNote;}
    
    public double[] getNextLikelihood() {return nextLikelihood;}
    
    public void newEpisode () {
            firstPass = true;
            currentNote = backUpNote;
            emissions = new TByteArrayList();
            if (backupEmissions != null) {emissions.addAll(backupEmissions);
            forwardProbs = new double[backupForwardProbs.length][backupForwardProbs[0].length];
            for (int i = 0; i < backupForwardProbs.length; i++) {
                System.arraycopy(backupForwardProbs[i], 0, forwardProbs[i], 0, backupForwardProbs[0].length);
            }
            for (int i = 0; i < backupTransitions.length; i++) {
                System.arraycopy(backupTransitions[i], 0, transitions[i], 0, backupTransitions[0].length);
            }
            for (int i = 0; i < backupOutputMatrix.length; i++) {
                System.arraycopy(backupOutputMatrix[i], 0, outputMatrix[i], 0, backupOutputMatrix[0].length);
            }
            }
    }
    
    public void backUp() {
            backUpNote = currentNote;
            backupEmissions = new TByteArrayList();
            backupEmissions.addAll(emissions);
            backupForwardProbs = new double[forwardProbs.length][forwardProbs[0].length];
            for (int i = 0; i < forwardProbs.length; i++) {
                System.arraycopy(forwardProbs[i], 0, backupForwardProbs[i], 0, forwardProbs[0].length);
            }
            for (int i = 0; i < transitions.length; i++) {
                System.arraycopy(transitions[i], 0, backupTransitions[i], 0, transitions[0].length);
            }
            for (int i = 0; i < outputMatrix.length; i++) {
                System.arraycopy(outputMatrix[i], 0, backupOutputMatrix[i], 0, outputMatrix[0].length);
            }
    }
    
    public float assignUtility(byte emission) {
        emissions.add(emission);
        while (emissions.size() >= cacheSize) {emissions.removeAt(0);}
        if (recognitionMode == 0) currentNote += (emission - 12);
        else if (recognitionMode == 1) {currentNote = emission;}
        likelihood = new double[numberOfEmissionValues];
        forwardSum = 0;
        transitionSums = new double[numberOfEmissionValues];
        nextLikelihood = new double[numberOfEmissionValues];
        double nextForwardSum = 0;
        double[] nextTransitionSums = new double[numberOfEmissionValues];
        double[] nextForwards = new double[stateNumber];
        
        if (!firstPass) {
            
            for (int l = 0; l < stateNumber; l++) {
                float nextSum = 0;
                for (int s = 0; s < stateNumber; s++) {
                    nextSum += forwards[s][emissions.size() -2]*transitions[s][l];
                }
                nextForwards[l] = nextSum*outputMatrix[l][emission];
            }
            
            
            for (int n = 0; n < numberOfEmissionValues; n++) {
                for (int i = 0; i < stateNumber; i++) {
                    forwardSum += forwards[i][emissions.size() -2];
                    nextForwardSum += nextForwards[i];
                }
                for (int i = 0; i < stateNumber; i++) {
                        for (int j = 0; j < stateNumber; j++) {
                            transitionSums[n] += transitions[i][j]*outputMatrix[j][n];
                            nextTransitionSums[n] += transitions[i][j]*outputMatrix[j][n];
                        }
                    likelihood[n] += (forwards[i][emissions.size() -2]/forwardSum)*transitionSums[n];
                    double intermediate = (nextForwardSum == 0 || nextForwards[i] == 0) ? 0: nextForwards[i]/nextForwardSum;
                    nextLikelihood[n] +=(intermediate)*nextTransitionSums[n];
                }
            }
        }
            baumInput = new byte[emissions.size()];
            baumInput = emissions.toArray();
            baumWelch(baumInput);
            utility = (float) (Math.abs(likelihood[emission]));
        
        if (firstPass) {utility = 0; firstPass = false;}
        return utility;
    }
   
    public double baumWelch(byte[] outputs) {
        forwards = forward(outputs);
        backwards = backward(outputs);
        for (int g = 0; g < stateNumber; g++) {
            for (int i = 0; i < stateNumber; i++) {
                for (int n = 0; n < numberOfEmissionValues; n++) {
                    int count = 0;
                    do {
                        estimateTransition = eTransition(outputs,g,i,n);
                        estimateOutput = eOutput(outputs,i,n);
                        transitions[g][i] += (estimateTransition - transitions[g][i])*learningRate;
                        outputMatrix[i][n] += (estimateOutput - outputMatrix[i][n])*learningRate;
                        count++;
                        normalize(outputs);
                    }
                   while ((Math.abs(estimateOutput-outputMatrix[i][n]) > convergence)
                        || (Math.abs(estimateTransition-transitions[g][i]) > convergence)
                            );
                    
                    
                }
            }
        }
        return forwards[endState][outputs.length-1];
    
    }
    public void normalize(byte[] outputs) {
        sum = 0;
        for (int g = 0; g < stateNumber; g++) {
            for (int i = 0; i < stateNumber; i++) { 
                sum += transitions[g][i];
            }
            if (sum == 0) { sum = 1;}
            for (int i = 0; i < stateNumber; i++) { 
                
                transitions[g][i]= transitions[g][i]/sum;
            }
            sum = 0;
        }
        for (int i = 0; i < stateNumber; i++) { 
            for (int n = 0; n < numberOfEmissionValues; n++) {
                sum+= outputMatrix[i][n];
            }
            if (sum == 0) { sum = 1;}
            for (int n = 0; n < numberOfEmissionValues; n++) {
                outputMatrix[i][n]/=sum;
            }
            sum = 0;
        }
    }
    public double eTransition(byte[] outputs,int i, int j, int k) {
        double sum1 = 0;
        for (int x = 0; x < outputs.length; x++) {
            sum1 += transition(outputs,i,j,k,x,false);
        }
        double sum2 = 0;
        for (int x = 0; x < outputs.length; x++) {
            for (int n = 0; n < stateNumber; n++) {
                sum2 += transition(outputs,i,n,k,x,false);
            }
        }
        double returnValue;
        if (sum1 == 0) {returnValue = 0;}
        else {returnValue = sum1/sum2;}
        return returnValue;
    }
    public double eOutput(byte[] outputs,int j, int k) {
        double sum1 = 0;
        for (int x = 0; x < outputs.length; x++) {
            if (outputs[x] == (k)) {
                
                for (int n = 0; n < stateNumber; n++) {
                    sum1 += transition(outputs,j,n,k,x,true);
                }
            }
        }
        double sum2 = 0;
        for (int x = 0; x < outputs.length; x++) {
            for (int n = 0; n < stateNumber; n++) {
                for (int h = 0; h < numberOfEmissionValues; h++) {
                    sum2 += transition(outputs,j,n,h,x,false);
                }
            }
        }
        double returnValue;
        if (sum1 == 0) {returnValue = 0;}
        else {returnValue = sum1/sum2;}
        return returnValue;
    } 
    public double transition(byte[] outputs,int i, int j, int k, int t,boolean match) {
        double result;
        if (t == 0) {
            result = transitions[i][j]*outputMatrix[j][k]*backwards[j][t];
        }
        else {
            result = forwards[i][t-1]*transitions[i][j]*outputMatrix[j][k]*backwards[j][t];
        }
        return result;
    }
    
    public double[][] forward(byte[] outputs) {
        double[][] oldForward = new double[0][0];
        if (outputs.length > 1) {
            oldForward = new double[stateNumber][outputs.length-1];
            if (forwardProbs[0].length == cacheSize-1) {oldForward = new double[stateNumber][cacheSize-1];}
            for (int i = 0; i < stateNumber; i++) {
                System.arraycopy(forwardProbs[i], 0, oldForward[i], 0, forwardProbs[0].length);}
        }
        forwardProbs = new double[stateNumber][outputs.length];
        if (outputs.length > 1 ) {
            for (int i = 0; i < stateNumber; i++) {
                System.arraycopy(oldForward[i], 0, forwardProbs[i], 0, oldForward[0].length);
                
            }
        }
        double newProb = 0;
        for (int len = 0; len < outputs.length; len++) {
            for (int n = 0; n < stateNumber; n++) {
                if (len == 0 && n == startState) {forwardProbs[n][len] = 1;}
                else if (len == 0 && n != startState) {forwardProbs[n][len] = 0;}
                else {
                    double forwardSum1 = 0;
                    for (int j = 0; j < stateNumber; j++) {
                        forwardSum1 += forwardProbs[j][len-1];
                    }
                    for (int j = 0; j < stateNumber; j++) {
                        double summation;
                        if (forwardProbs[j][len-1] == 0) {summation = 0;}
                        else {summation = forwardProbs[j][len-1]/forwardSum1;}
                        newProb += (summation)*transitions[j][n];
                    }
                    forwardProbs[n][len] = newProb*outputMatrix[n][outputs[len-1]];
                    newProb = 0;
                }
            }
        }
        return forwardProbs;
    }
    
    public double[][] backward(byte[] outputs) {
        backwardProbs = new double[stateNumber][outputs.length];
        for(int x = outputs.length - 1; x > -1; x--) {
            double newProb = 0;
            for (int n = 0; n < stateNumber; n++) {
                if (x == outputs.length - 1 && n == endState) {newProb = 1.0;}
                else if (n != endState && x == outputs.length - 1) { newProb = 0;}
                else {
                    double backwardSum = 0;
                    for (int j = 0; j < stateNumber; j++) {
                        backwardSum += backwardProbs[j][x+1];
                    }
                    for (int j = 0; j < stateNumber; j++) {
                        double summation;
                        if (backwardProbs[j][x+1] == 0) {summation = 0;}
                        else {summation = backwardProbs[j][x+1]/backwardSum;}
                        newProb += summation*transitions[n][j]*outputMatrix[j][outputs[x+1]];
                    }
                }
                backwardProbs[n][x] = newProb;
                newProb = 0;
            }
        }
        return backwardProbs;
    }
    
    
    
    
}
