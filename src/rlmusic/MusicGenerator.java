package rlmusic;
import java.util.ArrayList;
import gnu.trove.iterator.TIntIterator;
public class MusicGenerator extends Thread {
    
    private StateSpace states;
    private int episodeRepeat;
    private int episodeNumber = 10;
    private MusicCritic mc;
    private short nextNote = -1;
    private ArrayList<Short> notes;
    public boolean access = false;
    private Workspace ws;
    private long time;
    private int episodeLength = 8;
    private int noteCounter = 0;
    //array of groups with utilities?        
    @Override
    public void run() {
    }
    
    public MusicGenerator(MusicCritic mc, MusicPlayer mp, Workspace ws,int episodeRepeat) {
        this.episodeRepeat = episodeRepeat;
        this.mc = mc;
        this.ws = ws;
        ws.setEpisodeRepeat(episodeRepeat);
        mp.setGenerator(this);
        states = new StateSpace(mc,ws,Math.pow(0.01,(1.0/(float)(episodeRepeat))));
        //states.setRoot(new Node(null,(byte) 12,0.05f));
        states.setRootID(0);
        notes = new ArrayList<>();
        time = System.currentTimeMillis();
        generation();
        access = true;
    }
    
    public void generation() {
        for (int n = 0; n < episodeNumber; n++) {
                states.incrementEpisodeCount();
                for (int i = 0; i < episodeRepeat; i++) {
                    mc.newEpisode();
                    states.episode(episodeRepeat);
                    states.decrementTemp();
                }
                byte[] greedy = states.getGreedy2();
                for (int j = 0; j < episodeLength; j++) {
                    float finalreward = mc.assignUtility(greedy[j]);
                    ws.setReward(n*10 + j, 0, finalreward);
                    ws.repaint();
                    nextNote = mc.getCurrentNote();
                    accessNote(nextNote,true);
                }
                mc.backUp();
                System.out.println("Time elapsed: " + Math.round((System.currentTimeMillis()-time)/1000) + " seconds");
                TIntIterator it = states.getNodeIDs().iterator();
                int nextRoot = -1;
                while (it.hasNext()) {
                    int next = it.next();
                    if (states.getNodeNoteValues().get(next) == greedy[episodeLength - 1] && states.getNodeNoteValues().get(states.getParents().get(next)) == greedy[episodeLength - 2]) {
                        nextRoot = next;
                    }
                }
                states.resetTemp();
                states.setRootID(nextRoot);
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
}
