
package rlmusic;

import java.util.Random;

public class Chord {
    
    public int index2 = -1, index3 = -1;
    
    public Chord(int previous,int current) {
        Random r = new Random();
        int secondGuess,thirdGuess;
        float temp2,temp3,secondDisso = 1, thirdDisso = 1, dissoBetween;
        for (int i = 0; i < 24; i++) {
            secondGuess = r.nextInt(12)+1;
            thirdGuess = r.nextInt(12)+1;
            dissoBetween = getDissonance((thirdGuess + secondGuess)%12);
            temp2 = (getDissonance(secondGuess) + getDissonance(Math.abs((current - previous) - secondGuess)) + dissoBetween)/3;
            if (temp2 < secondDisso) {index2 = secondGuess; secondDisso = temp2; dissoBetween = getDissonance((thirdGuess + secondGuess)%12);}
            temp3 = (getDissonance(thirdGuess) + getDissonance(Math.abs((current + thirdGuess) - previous)%12) + dissoBetween)/3;
            if (temp3 < thirdDisso) {index3 = thirdGuess; thirdDisso = temp3;}
        }
    }
    
    public float getDissonance(int steps) {
        float dissonance = 1;
        
        switch (steps) {
            case 1: dissonance =  0.06f; break;
            case 2: dissonance =  0.071f; break;
            case 3: dissonance =  0.008f; break;
            case 4: dissonance =  0.008f; break;
            case 5: dissonance =  0; break;
            case 6: dissonance =  0.064f; break;
            case 7: dissonance =  0.011f; break;
            case 8: dissonance =  0.048f; break;
            case 9: dissonance =  0.008f; break;
            case 10: dissonance =  0.068f; break;
            case 11: dissonance =  0.058f; break;
            case 12: dissonance =  0; break;
            default: dissonance =  0.08f;
        }
        return dissonance;
    }
    
}
