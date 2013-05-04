package rlmusic;


public class MusicCritic extends Thread {
    
    private boolean done = false;
    private MusicPlayer mp;
    private short currentNote;
    private Workspace ws;
    private int episodeRepeat;
    private short backUpNote;
    private boolean firstPass = true;
    private float weight1 = 0.4f,weight2 = 0.4f,weight3 = 0.2f;
    private int minNote = 21;
    private int maxNote = 108;
    
    private float utility;
    private int cacheSize = 20;
    private BaumWelch absolute,relative,directional;
    private float relativeReward,absoluteReward,directionalReward;
    private byte currentemission;
    private MusicGenerator mg;
    
    public boolean getDone() {return done;}
    
    public MusicCritic(MusicPlayer mp, Workspace ws,int episodeRepeat) { this.mp = mp; this.ws = ws; this.episodeRepeat = episodeRepeat;
    
        backUpNote = currentNote = (short) (60);
        
        relative = new BaumWelch(currentNote,cacheSize,0,25);
        absolute = new BaumWelch(currentNote,cacheSize,1,12);
        directional = new BaumWelch(currentNote,cacheSize,2,3);
        mg = new MusicGenerator(this,mp,ws,episodeRepeat);
    }
    @Override
    public void run() {
        relative.start();
        absolute.start();
        directional.start();
        mg.start();
    }
    
    public short getCurrentNote() {return currentNote;}
    
    public double[][] getNextLikelihoods() {
        double[][] likelihoods = {relative.getNextLikelihood(),absolute.getNextLikelihood(),directional.getNextLikelihood()};
        return likelihoods;
    }
    
    public void newEpisode () {
            relative.newEpisode();
            absolute.newEpisode();
            directional.newEpisode();
            firstPass = true;
            currentNote = backUpNote;
    }
    
    public void backUp() {
            relative.backUp();
            absolute.backUp();
            directional.backUp();
            backUpNote = currentNote;
    }
    
    public float assignUtility(byte emission) {
        currentemission = emission;
        currentNote += (emission - 12);
        relativeReward = relative.assignUtility(emission);
        absoluteReward = absolute.assignUtility((byte)((currentNote+3)%12));
        directionalReward = directional.assignUtility((byte)(Math.signum(emission-12)+1))/4.4f;
            float likelihood = absoluteReward*weight2 + directionalReward*weight3 + relativeReward*weight1;
            utility = (float) (likelihood*(1-(getDissonance(emission)))*getCauchy((byte)0));
        
        if (firstPass) {utility = 0; firstPass = false;}
        if (currentNote > maxNote) {currentNote = (short) maxNote; utility = 0;}
        if (currentNote < minNote) {currentNote = (short) minNote; utility = 0;}
        return utility;
    }
    
    public double[] getInputs() {
        
        double[] inputs = new double[7];
        inputs[0] = relativeReward*weight1;
        inputs[1] = absoluteReward*weight2;
        inputs[2] = directionalReward*weight3;
        inputs[3] = (1-(getDissonance(currentemission)*10));
        inputs[4] = getCauchy((byte)0);
        inputs[5] = (currentNote > maxNote) ? 1 : 0;
        inputs[6] = (currentNote < minNote) ? 1 : 0;
        return inputs;
    }
    
    
    public double getCauchy(byte emission) {
        double cauchy = 0;
        double norm = (currentNote + emission - 21)/14.5 - 3;
        Math.abs(cauchy = (1/Math.PI)*(0.319f/(norm*norm + 0.102f)));
        return cauchy;
    }
    
    public void stopANN() {mg.stopANN();}
    
    public float getDissonance(byte emission) {
        
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
        return dissonance;
    }
    
    
}
