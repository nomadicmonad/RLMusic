package rlmusic;

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import gnu.trove.*;
import gnu.trove.list.array.TByteArrayList;

public class MusicCritic extends Thread {
    
    private boolean done = false;
    private MusicPlayer mp;
    private short currentNote;
    private Workspace ws;
    private int stateNumber = 1;
    private double[][] transitions;
    private double[][] outputMatrix;
    private double[][] forwards;
    private double[][] backwards;
    private int episodeRepeat;
    private TByteArrayList emissions;
    private int noteNumber = 25;
    private TByteArrayList backupEmissions;
    private double[][] backupTransitions;
    private double[][] backupOutputMatrix;
    private short backUpNote;
    private boolean firstPass = true;
    private double convergence = 0.02;
    private double learningRate = 0.3;
    private double estimateTransition;
    private double estimateOutput;
    
    
    private double sum;
    private float utility;
    private byte[] baumInput;
    private double norm;
    private float extra = 1.5f;// to make it even more extreme
    private double cauchy;
    private float dissonance;
    private double[] likelihood;
    private double forwardSum;
    private double mostLikely;
    private double difference;
    private double[] transitionSums;
    private double[][] forwardProbs;
    private double[][] backupForwardProbs;
    private double[][] backwardProbs;
    
    public boolean getDone() {return done;}
    
    public MusicCritic(MusicPlayer mp, Workspace ws,int episodeRepeat) { this.mp = mp; this.ws = ws; this.episodeRepeat = episodeRepeat;}
    @Override
    public void run() {
        forwardProbs = new double[1][1];
        backupForwardProbs = new double[1][1];
        backupEmissions = new TByteArrayList();
        emissions = new TByteArrayList();
        Random r = new Random();
        backUpNote = currentNote = (short) (r.nextInt(43) + 42);
        transitions = new double[stateNumber][stateNumber];
        backupTransitions = new double[stateNumber][stateNumber];
        double[] gaussians = new double[stateNumber];
        for (int i = 0; i < stateNumber; i++) {
            sum = 0;
            for (int j = 0; j < stateNumber; j++) {
                gaussians[j] = r.nextGaussian();
                sum += Math.abs(gaussians[j]);
            }
            for (int j = 0; j < stateNumber; j++) {
                backupTransitions[i][j] = transitions[i][j] = gaussians[j]/(sum*2) + 0.5;
            }
        }
        outputMatrix = new double[stateNumber][noteNumber];
        backupOutputMatrix = new double[stateNumber][noteNumber];
        gaussians = new double[noteNumber];
        for (int i = 0; i < stateNumber; i++) {
            sum = 0;
            for (int j = 0; j < noteNumber; j++) {
                gaussians[j] = r.nextGaussian();
                sum += Math.abs(gaussians[j]);
            }
            for (int j = 0; j < noteNumber; j++) {
                backupOutputMatrix[i][j] = outputMatrix[i][j] = gaussians[j]/(sum*2) + 0.5;
            }
        }
        new MusicGenerator(this,mp,ws,episodeRepeat);
    }
    
    public short getCurrentNote() {return currentNote;}
    
    public void newEpisode () {
                firstPass = true;
                currentNote = backUpNote;
                emissions = new TByteArrayList();
                emissions.addAll(backupEmissions);
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
        currentNote += (emission - 12);
        
        norm = currentNote/18.0 - 3;
        cauchy = (1/(extra*(Math.PI*(2*norm*norm + 0.5))));
        dissonance = 1;
        switch (emission) {
            case 0: dissonance = 0; break;
            case 1: dissonance = 0.058f; break;
            case 2: dissonance = 0.068f; break;
            case 3: dissonance = 0.008f; break;
            case 4: dissonance = 0.048f; break;
            case 5: dissonance = 0.011f; break;
            case 6: dissonance = 0.064f; break;
            case 7: dissonance = 0; break;
            case 8: dissonance = 0.008f; break;
            case 9: dissonance = 0.008f; break;
            case 10: dissonance = 0.071f; break;
            case 11: dissonance = 0.06f; break;
            case 12: dissonance = 0f; break;
            case 13: dissonance = 0.06f; break;
            case 14: dissonance = 0.071f; break;
            case 15: dissonance = 0.008f; break;
            case 16: dissonance = 0.008f; break;
            case 17: dissonance = 0; break;
            case 18: dissonance = 0.064f; break;
            case 19: dissonance = 0.011f; break;
            case 20: dissonance = 0.048f; break;
            case 21: dissonance = 0.008f; break;
            case 22: dissonance = 0.068f; break;
            case 23: dissonance = 0.058f; break;
            case 24: dissonance = 0f; break;
        }
        likelihood = new double[noteNumber];
        forwardSum = 0;
        mostLikely = 0;
        transitionSums = new double[noteNumber];
        if (!firstPass) {
            for (int n = 0; n < noteNumber; n++) {
                for (int i = 0; i < stateNumber; i++) {
                    forwardSum += forwards[i][emissions.size() -2];
                }
                for (int i = 0; i < stateNumber; i++) {
                        for (int j = 0; j < stateNumber; j++) {
                            transitionSums[n] += transitions[i][j]*outputMatrix[j][n];
                        }
                    likelihood[n] += (forwards[i][emissions.size() -2]/forwardSum)*transitionSums[n];

                }
            }
        }
            for (double d: likelihood) {mostLikely = (d > mostLikely) ? d : mostLikely;}
            likelihood[emission] = (Double.isNaN(likelihood[emission]))?0:likelihood[emission];
            difference = (likelihood[emission] > 0.1) ? mostLikely - likelihood[emission] : likelihood[emission];
            baumInput = new byte[emissions.size()];
            baumInput = emissions.toArray();
            baumWelch(baumInput);
            utility = (float) (difference*(1-(dissonance*10))*cauchy);
            if (currentNote > 108) {currentNote = 108; utility = -1;}
            if (currentNote < 21) {currentNote = 21; utility = -1;}
        
        if (firstPass) {utility = 0; firstPass = false;}
        return utility;
    }
   
    public void baumWelch(byte[] outputs) {
             
        forwards = forward(outputs);
        backwards = backward(outputs);
        for (int g = 0; g < stateNumber; g++) {
            for (int i = 0; i < stateNumber; i++) {
                for (int n = 0; n < noteNumber; n++) {
                    do {
                        estimateTransition = eTransition(outputs,g,i,n);
                        estimateOutput = eOutput(outputs,g,n);
                        transitions[g][i] += (estimateTransition-transitions[g][i])*learningRate;
                        outputMatrix[i][n] += (estimateOutput-outputMatrix[i][n])*learningRate;
                    } 
                    while ((Math.abs(estimateOutput-outputMatrix[i][n]) > convergence)
                        || (Math.abs(estimateTransition-transitions[g][i]) > convergence));
                }
            }
        }
        //normalize transition probabilities
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
            for (int n = 0; n < noteNumber; n++) {
                sum+= outputMatrix[i][n];
            }
            if (sum == 0) { sum = 1;}
            for (int n = 0; n < noteNumber; n++) {
                outputMatrix[i][n]= outputMatrix[i][n]/sum;
            }
            sum = 0;
        }
    
    }
    public double eTransition(byte[] outputs,int i, int j, int k) {
        double sum = 0;
        for (int x = 0; x < outputs.length; x++) {
            sum += transition(outputs,i,j,k,x,false);
        }
        double sum2 = 0;
        for (int x = 0; x < outputs.length; x++) {
            for (int n = 0; n < stateNumber; n++) {
                sum2 += transition(outputs,i,n,k,x,false);
            }
        }
        double returnValue = (Double.isNaN(sum/sum2)) ? 0 : sum/sum2;
        returnValue = (Double.isInfinite(sum/sum2)) ? 1 : returnValue; 
        return returnValue;
    }
    public double eOutput(byte[] outputs,int j, int k) {
        double sum = 0;
        for (int x = 0; x < outputs.length; x++) {
            for (int n = 0; n < stateNumber; n++) {
                sum += transition(outputs,j,n,k,x,true);
            }
        }
        double sum2 = 0;
        for (int x = 0; x < outputs.length; x++) {
            for (int n = 0; n < stateNumber; n++) {
                for (int h = 0; h < noteNumber; h++) {
                    sum2 += transition(outputs,j,n,h,x,false);
                }
            }
        }
        double returnValue = (Double.isNaN(sum/sum2)) ? 0 : sum/sum2;
        returnValue = (Double.isInfinite(sum/sum2)) ? 1 : returnValue; 
        return returnValue;
    } 
    public double transition(byte[] outputs,int i, int j, int k, int t,boolean match) {
        double result;
        if (match) {
            if (outputs[t] == k) {
                return 1;
            }
            else {
                return 0;
            }
        }
        if (t == 0) {
            result = transitions[i][j]*outputMatrix[j][k];//*backwards[j][t];//what?
        }
        else {
            result = forwards[i][t-1]*transitions[i][j]*outputMatrix[j][k]*backwards[j][t];
        }
        return result;
    }
    
    public double[][] forward(byte[] outputs) {
        double[][] oldForward = new double[0][0];
        if (outputs.length > 1) {
            oldForward = new double[stateNumber][outputs.length - 1];
            for (int i = 0; i < stateNumber; i++) {
                System.arraycopy(forwardProbs[i], 0, oldForward[i], 0, forwardProbs[0].length);
            }
        }
        forwardProbs = new double[stateNumber][outputs.length];
        if (outputs.length > 1 ) {
            for (int i = 0; i < stateNumber; i++) {
                System.arraycopy(oldForward[i], 0, forwardProbs[i], 0, oldForward[0].length);
            }
        }
        double newProb = 0;
            for (int n = 0; n < stateNumber; n++) {
                for (int j = 0; j < stateNumber; j++) {
                    if (outputs.length == 1) {newProb += transitions[j][n]; continue;}
                    newProb += forwardProbs[j][outputs.length-2]*transitions[j][n];
                }
                forwardProbs[n][outputs.length - 1] = newProb*outputMatrix[n][outputs[outputs.length-1]];
            }
        return forwardProbs;
    }
    
    public double[][] backward(byte[] outputs) {
        backwardProbs = new double[stateNumber][outputs.length];
        for(int x = outputs.length - 1; x > -1; x--) {
            double newProb = 0;
            for (int n = 0; n < stateNumber; n++) {
                for (int j = 0; j < stateNumber; j++) {
                    if (outputs.length == 1 && x == 0) {newProb = 0; continue;}
                    if (x == outputs.length - 1) { newProb = 1; continue;}
                    newProb += backwardProbs[j][x+1]*transitions[n][j]*outputMatrix[n][outputs[x+1]];
                }
                backwardProbs[n][x] = newProb;
            }
        }
        return backwardProbs;
    }
    
    
}
