package rlmusic;

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

public class MusicCritic extends Thread {
    
    private boolean done = false;
    private MusicPlayer mp;
    private float[] transitionProbabilities;
    private float[] transitionBackup;
    private short currentNote = 60;
    private short backupNote = 60;
    private byte lastTransition = 0;
    private Workspace ws;
    private int stateNumber = 4;
    private int[] outputs = new int[10];
    private int outputCounter = 0;
    private double[][] transitions;
    private double[][] outputMatrix;
    private double[][] forwards;
    private double[][] backwards;
    private int episodeRepeat;
    
    public boolean getDone() {return done;}
    
    public MusicCritic(MusicPlayer mp, Workspace ws,int episodeRepeat) { this.mp = mp; this.ws = ws; this.episodeRepeat = episodeRepeat;}
    @Override
    public void run() {
        baumWelch(new int[]{0,1,0,1,0,1});
        transitionProbabilities = new float[25*26];
        transitionBackup = new float[25*26];
        for (int i = 0; i < 25*25; i++) {
            transitionProbabilities[i] = 0;
        }
        new MusicGenerator(this,mp,ws,episodeRepeat);
    }
    public void backUp() {
        System.arraycopy(transitionProbabilities, 0, transitionBackup, 0, transitionProbabilities.length);
        
    }
    public void loadBackUp() {
        System.arraycopy(transitionBackup, 0, transitionProbabilities, 0, transitionProbabilities.length);
        restoreNote();
        outputs = new int[10]; //reset outputs
        outputCounter = 0;
    }
    
    public short getCurrentNote() {return currentNote;}
    
    public float assignUtility(byte emission) {
        currentNote += (emission - 12);
        
        
        double norm = currentNote/18.0 - 3;
        double cauchy = (1/(Math.PI*(2*norm*norm + 0.5)));
        float dissonance = 1;
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
        double likelihood = 0;
        if (outputCounter > 0) {
            for (int i = 0; i < 3; i++) {baumWelch(Arrays.copyOfRange(outputs,0,outputCounter));}
            //old likelihood of state, transition, and emission
            for (int i = 0; i < stateNumber; i++) {
                double transitionSum = 0;
                for (int j = 0; j < stateNumber; j++) {
                    transitionSum += transitions[i][j]*outputMatrix[j][emission];
                }
                likelihood += forwards[i][outputCounter-1]*transitionSum;
            }
        }
        likelihood = (Double.isNaN(likelihood))?0:likelihood*10000;
        //System.out.println(likelihood);
        outputs[outputCounter] = emission;
        outputCounter++;
        outputCounter = (outputCounter == 10) ? 0: outputCounter;
        lastTransition = emission;
        float utility = (float) (likelihood*(1-dissonance)*cauchy);
        if (currentNote > 108) {currentNote = 108; utility = -1;}
        if (currentNote < 21) {currentNote = 21; utility = -1;}
        return utility;
    }
    /*public float[] getProbability(byte note) {
        float[] returnRow = new float[25]; //row of probabilities for outputs for given former state
        float n = transitionProbabilities[25*note + 26];
        transitionProbabilities[25*note + 26]++;
        for (int i = 0; i < 25; i++) {
            if (i == note) {
                transitionProbabilities[25*note + i] = returnRow[i] = (float) ((float)(transitionProbabilities[25*note + i]*n + 1)/(float) (n + 1));
            }
            else {transitionProbabilities[25*note + i] = returnRow[i] = (float) ((float)(transitionProbabilities[25*note + i]*n)/(float)(n + 1));
            }
            
        }
        return returnRow;
    }*/
    
    public void setBackUpNote() {backupNote = currentNote;}
    
    public void restoreNote() {currentNote = backupNote;}
    
    public void baumWelch(int[] outputs) {
        transitions = new double[stateNumber][stateNumber];
        for (int i = 0; i < stateNumber; i++) {
            for (int j = 0; j < stateNumber; j++) {
                transitions[i][j] = 1.0f/stateNumber;
            }
        }
        outputMatrix = new double[stateNumber][25];
        for (int i = 0; i < stateNumber; i++) {
            for (int j = 0; j < 25; j++) {
                outputMatrix[i][j] = 1.0f/25.0f;
            }
        }
        
        forwards = forward(outputs);        
        backwards = backward(outputs);
        double convergence = 0.01f/stateNumber;
        double formerTransition;
        double formerOutput;
        for (int g = 0; g < stateNumber; g++) {
            for (int i = 0; i < stateNumber; i++) {
                for (int n = 0; n < 25; n++) {
                    double estimateTransition = 1;
                    double estimateOutput = 1;
                    do {
                        formerTransition = estimateTransition;
                        formerOutput = estimateOutput;
                        estimateTransition = eTransition(outputs,g,i,n);
                        estimateOutput = eOutput(outputs,g,i,n);
                        transitions[g][i] = estimateTransition;
                        outputMatrix[i][n] = estimateOutput;
                    } 
                    while ((Math.abs(estimateTransition - formerTransition)) > convergence || (Math.abs(estimateOutput-formerOutput) > convergence));
                }
            }
        }
        //normalize transition probabilities
        double sum = 0;
        for (int g = 0; g < stateNumber; g++) {
            for (int i = 0; i < stateNumber; i++) { 
                sum += transitions[g][i];
            }
            for (int i = 0; i < stateNumber; i++) { 
                transitions[g][i] /= sum;
            }
            sum = 0;
        }
        for (int i = 0; i < stateNumber; i++) { 
            for (int n = 0; n < 25; n++) {
                sum+= outputMatrix[i][n];
            }
            for (int n = 0; n < 25; n++) {
                outputMatrix[i][n]/=sum;
            }
            sum = 0;
        }
    
    }
    public double eTransition(int[] outputs,int i, int j, int k) {
        double sum = 0;
        for (int x = 0; x < outputs.length; x++) {
            sum += transition(outputs,i,j,k,x);
        }
        double sum2 = 0;
        for (int x = 0; x < outputs.length; x++) {
            for (int n = 0; n < stateNumber; n++) {
                sum2 += transition(outputs,i,n,k,x);
            }
        }
        return sum/sum2;
    }
    public double eOutput(int[] outputs, int i, int j, int k) {
        double sum = 0;
        for (int x = 0; x < outputs.length; x++) {
            for (int n = 0; n < stateNumber; n++) {
                sum += transition(outputs,j,n,k,x);
            }
        }
        double sum2 = 0;
        for (int x = 0; x < outputs.length; x++) {
            for (int n = 0; n < stateNumber; n++) {
                for (int h = 0; h < 25; h++) {
                    sum2 += transition(outputs,j,n,h,x);
                }
            }
        }
        return sum/sum2;
    } 
    public double transition(int[] outputs,int i, int j, int k, int t) {
        double result;
        if (t == 0) {
            result = transitions[i][j]*outputMatrix[j][k]*backwards[j][t];
        }
        else {
            result = forwards[i][t-1]*transitions[i][j]*outputMatrix[j][k]*backwards[j][t];
        }
        return result;
    }
    
    public double[][] forward(int[] outputs) {
    double[][] forwardProbs = new double[stateNumber][outputs.length];
    for(int x = 0; x < outputs.length; x++) {
        double newProb = 0;
        for (int n = 0; n < stateNumber; n++) {
            for (int j = 0; j < stateNumber; j++) {
                if ( x == 0) {newProb += transitions[j][n]; continue;}
                newProb += forwardProbs[j][x-1]*transitions[j][n];
            }
            forwardProbs[n][x] = newProb*outputMatrix[n][outputs[x]];
        }
    }
    return forwardProbs;
    }
    public double[][] backward(int[] outputs) {
    double[][] backwardProbs = new double[stateNumber][outputs.length];
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
