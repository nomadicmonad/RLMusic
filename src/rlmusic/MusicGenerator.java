package rlmusic;
import java.util.ArrayList;
public class MusicGenerator extends Thread {
    
    private StateSpace states;
    private int episodeRepeat;
    private int episodeNumber = 10;
    private MusicCritic mc;
    private ArrayList<Short> notes;
    public boolean access = false;
    private Workspace ws;
    private long time;
    private int episodeLength = 8;
    private int noteCounter = 0;     
    @Override
    public void run() {
        generation();
    }
    
    public MusicGenerator(MusicCritic mc, MusicPlayer mp, Workspace ws,int episodeRepeat) {
        this.episodeRepeat = episodeRepeat;
        this.mc = mc;
        this.ws = ws;
        ws.setEpisodeRepeat(episodeRepeat);
        mp.setGenerator(this);
        states = new StateSpace(mc,ws,Math.pow(0.01,(1.0/(float)(episodeRepeat))));
        notes = new ArrayList<>();
        time = System.currentTimeMillis();
        access = true;
    }
    
    public void generation() {
        for (int n = 0; n < episodeNumber; n++) {
                states.incrementEpisodeCount();
                for (int i = 0; i < episodeRepeat; i++) {
                    mc.newEpisode();
                    states.annEpisode(episodeRepeat);
                    states.decrementTemp();
                }
                float[] greedyRewards = states.getGreedyRewards();
                int[] greedyAbsolutes = states.getGreedyAbsolutes();
                for (int j = 0; j < episodeLength; j++) {
                    ws.setReward(n*10 + j, 0, greedyRewards[j]);
                    ws.repaint();
                    accessNote((short)greedyAbsolutes[j],true);
                }
                mc.backUp();
                System.out.println("Time elapsed: " + Math.round((System.currentTimeMillis()-time)/1000) + " seconds");
                states.resetTemp();
            }
        }
    
    public synchronized short accessNote(short s, boolean add) {
        if (add) {
            noteCounter++;
            notes.add(s);
            return -1;
        }
        else {
        if (!notes.isEmpty() && noteCounter > 0) {return notes.remove(0);}
        else {return -1;}}
    }
    
    public void stopANN() {states.stopANN();}
}
