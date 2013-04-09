package rlmusic;
import java.util.ArrayList;
import java.util.Iterator;

public class MusicGenerator extends Thread {
    
    private StateSpace states;
    private int episodeRepeat;
    private int episodeNumber = 5;
    private MusicCritic mc;
    private short nextNote = -1;
    private ArrayList<Short> notes;
    public boolean access = false;
    private Workspace ws;
    private long time;
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
        states.setRoot(new Node(null,(byte) 0,0.05f));
        notes = new ArrayList<>();
        time = System.currentTimeMillis();
        generation();
        access = true;
    }
    
    public void generation() {
        for (int n = 0; n < episodeNumber; n++) {
            //System.out.println("new episode");
                states.incrementEpisodeCount();
                mc.backUp();
                mc.setBackUpNote();
                for (int i = 0; i < episodeRepeat; i++) {
                    //System.out.println("new repeat");
                    states.episode(episodeRepeat);
                    mc.loadBackUp();
                    states.decrementTemp();
                }
                byte[] greedy = states.getGreedy();
                for (int j = 0; j < 10; j++) {
                    float finalreward = mc.assignUtility(greedy[j]);
                    ws.setReward(n*10 + j, 0, finalreward);
                    //System.out.println("action: " + greedy[j]);
                    ws.repaint();
                    nextNote = mc.getCurrentNote();
                    accessNote(nextNote,true);
                }
                System.out.println("Time elapsed: " + Math.round((System.currentTimeMillis()-time)/1000) + " seconds");
                Iterator it = states.getNodes().iterator();
                Node nextRoot = null;
                while (it.hasNext()) {
                    Node nextNode = (Node) it.next();
                    if (nextNode.getNoteValue() == greedy[9] && nextNode.getParent().getNoteValue() == greedy[8]) {
                        nextRoot = nextNode;
                    }
                }
                states.resetTemp();
                states.setRoot(nextRoot);
            }
        }
    
    
    public synchronized short accessNote(short s, boolean add) {
        if (add) {
            notes.add(s);
            return -1;
        }
        else {
        if (!notes.isEmpty()) {return notes.remove(0);}
        else {return -1;}}
    }
}
