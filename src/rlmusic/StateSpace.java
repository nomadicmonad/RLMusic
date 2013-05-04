
package rlmusic;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.list.array.TDoubleArrayList;

public class StateSpace {
    public Node root;
    public ArrayList<Node> nodes;
    public Node currentNode;
    private float lr = 0.1f;//learning rate
    private float df = 0.5f; //discount
    private MusicCritic mc;
    private double temp = 1;
    private Random r;
    private byte count = 0;
    private float lambda = 0.9f;
    private double tempDiscount;
    private Workspace ws;
    private int rewardcount = 0;
    private int episodeCount = 0;
    private int episodeRepeat = 0;
    public boolean doneHere = false;
    private int theEpisodeRepeat;
    private int episodeLength = 8;
    private int noteNumber = 25;
    
    private boolean skip;
    private double[] actionProbs;
    private float reward;
    private Node iterNode;
    private int[] greedy;
    
    private float sum;
    private byte action = 0;
    private boolean bigger;
    private boolean done;
    private double numerator;
    private double denominator;
    private float prob;
    
    public TIntArrayList nodeIDs;
    public TFloatArrayList nodeValues;
    public TByteArrayList nodeNoteValues;
    public TFloatArrayList nodeRewards;
    public TByteArrayList nodeExpands;
    public TFloatArrayList nodeETraces;
    public ArrayList<Integer[]> children;
    public TIntArrayList parents;
    private int currentNodeID;
    private int rootID = 0;
    private NeuralNetwork ann;
    private TDoubleArrayList inputs,nextinputs;
    
    private int theaction;
    private int nextaction;
    private PatternTracking patternTracking;
    private float[] greedyRewards;
    private int[] greedyAbsolutes;
    private float weight1 = 0.4f,weight2 = 0.4f,weight3 = 0.2f;
    private int minNote = 21; 
    private int maxNote = 108;
    
    public StateSpace(MusicCritic mc, Workspace ws, double tempDiscount) {
        this.mc = mc;
        nodes = new ArrayList<>();
        
        ann = new NeuralNetwork();
        patternTracking = new PatternTracking(tempDiscount);
        inputs = new TDoubleArrayList();
        nextinputs = new TDoubleArrayList();
        
        /*parents = new TIntArrayList();
        children = new ArrayList();
        nodeIDs = new TIntArrayList();
        nodeValues = new TFloatArrayList();
        nodeRewards = new TFloatArrayList();
        nodeETraces = new TFloatArrayList();
        nodeNoteValues = new TByteArrayList();
        nodeExpands = new TByteArrayList(); //boolean*/
        
        this.tempDiscount = tempDiscount;
        this.ws = ws;
        actionProbs = new double[noteNumber];
        r = new Random();
    }

    /*public Node getCurrentNode() {
        return currentNode;
    }*/
    
    public void stopANN() {
        ann.stopWriting();
    }
    
    public int getCurrentNodeID() {
        return currentNodeID;
    }
    
    public void setCurrentNodeID(int currentNodeID) {
        this.currentNodeID = currentNodeID;
    }
    
    public int getRootID() {
        return rootID;
    }
    
    /*public void setRootID(int rootID) {
        this.rootID = rootID;
        parents.add(0);
        nodeNoteValues.add((byte)12);
        nodeValues.add(0.0f);
        nodeExpands.add((byte)0);
        Integer[] ints = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        children.add(ints);
        nodeIDs.add(0);
        nodeRewards.add(0);
        nodeETraces.add(0);
    }*/

    /*public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }*/

    /*public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }*/
    /*public byte[] getGreedy2() {
        greedy = new byte[episodeLength];
        int iterID = rootID;
        for (int i = 0; i < episodeLength; i++) {
            greedy[i] = nodeNoteValues.get(iterID);
            iterID = getMaxQ2(iterID);
        }
        return greedy;
    }*/
    
    /*public byte[] getGreedy() {
        greedy = new byte[episodeLength];
        iterNode = root;
        for (int i = 0; i < episodeLength; i++) {
            greedy[i] = iterNode.getNoteValue();
            iterNode = getMaxQ(iterNode);
        }
        return greedy;
    }*/
    
    
    /*public void episode2(int theEpisodeRepeat) {
        this.theEpisodeRepeat = theEpisodeRepeat;
        currentNode = root;
        count = 0;
        rewardcount = 0;
        while (count < episodeLength) {
            expandNode(currentNode);
            currentNode = currentNode.getChild(chooseAction());
            fadeTraces();
            currentNode.seteTrace(1);
            qLearn();
            count++;
        }
        mc.newEpisode();
    }*/
    
    /*public void episode(int theEpisodeRepeat) {
        this.theEpisodeRepeat = theEpisodeRepeat;
        currentNodeID = rootID;
        count = 0;
        rewardcount = 0;
        while (count < episodeLength) {
            expandNode2(currentNodeID);
            theaction = patternTracking.getAction();//chooseAction2();
            int newNodeID = children.get(currentNodeID)[theaction];
            if (newNodeID == -1) {
                parents.add(currentNodeID);//adds new parent value on spot of newID
                nodeNoteValues.add((byte)theaction);
                nodeValues.add(0.0f);
                nodeExpands.add((byte)0);
                Integer[] ints = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
                children.add(ints);
                nodeIDs.add(newNodeID = nodeIDs.size());
                nodeRewards.add(0);
                nodeETraces.add(0);
                children.get(currentNodeID)[theaction] = nodeIDs.size()-1;
            }
            fadeTraces2();
            nodeETraces.set(currentNodeID,1);
            qLearn2();
            count++;
            currentNodeID = newNodeID;
        }
        mc.newEpisode();
    }
    */
    public int[] getGreedyANN() {
        greedy = new int[episodeLength];
        greedyRewards = new float[episodeLength];
        greedyAbsolutes = new int[episodeLength];
        for (int i = 0; i < episodeLength; i++) {
            greedy[i] = getHighest();
            greedyRewards[i] = mc.assignUtility((byte)greedy[i]);
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
            double max1,max2,max3;
            max1 = max2 = max3 = 0;
            double[] likelihoods1 = nextLikelihoods[0];
            double[] likelihoods2 = nextLikelihoods[1];
            double[] likelihoods3 = nextLikelihoods[2];
            for (int n = 0; n < likelihoods1.length; n++) {
                if (likelihoods1[n] > max1) {max1 = likelihoods1[n];}
            }
            for (int n = 0; n < likelihoods2.length; n++) {
                if (likelihoods2[n] > max2) {max2 = likelihoods2[n];}
            }
            for (int n = 0; n < likelihoods3.length; n++) {
                if (likelihoods3[n] > max3) {max3 = likelihoods3[n];}
            }
            double likelihood1 = weight1*Math.abs(nextLikelihoods[0][i]-max1);
            double likelihood2 = weight2*Math.abs(nextLikelihoods[1][(mc.getCurrentNote() + (i-12)+3)%12]-max2);
            double likelihood3 = weight3*Math.abs(nextLikelihoods[2][(int)Math.signum(i-12)+1]-max3)/4.4f;
            double dissonance = mc.getDissonance(i);
            double cauchy = mc.getCauchy(i);
            double over = (mc.getCurrentNote() + (i-12) > maxNote) ? 1:0;
            double under = (mc.getCurrentNote() + (i-12) < minNote) ? 1:0;
            candidateInputs.addAll(new double[]{likelihood1,likelihood2,likelihood3,dissonance,cauchy,over,under});
            ann.setInputs(candidateInputs);
            ann.feedForward();
            double value = ann.predict();
            if (highest < value) {highest = value; highestIndex = i;}
            candidateInputs = new TDoubleArrayList();
        }
        return highestIndex;
    }
    
    public void annEpisode(int theEpisodeRepeat) {
        this.theEpisodeRepeat = theEpisodeRepeat;
        float reward;
        float currentreward = 0;
        count = 0;
        theaction = patternTracking.getAction();
        inputs = new TDoubleArrayList();
        inputs.addAll(mc.getInputs());
        while (count < episodeLength) {
            /*  
            * inputs: likelihood[emission]*0.4 + 0.2 0.4, then next values try the next action and save it to be inputs for the next round
            * cauchy, dissonance
            */
            reward = mc.assignUtility((byte)nextaction);
            double[][] nextLikelihoods = mc.getNextLikelihoods();
            double max1,max2,max3;
            max1 = max2 = max3 = 0;
            double[] likelihoods1 = nextLikelihoods[0];
            double[] likelihoods2 = nextLikelihoods[1];
            double[] likelihoods3 = nextLikelihoods[2];
            for (int n = 0; n < likelihoods1.length; n++) {
                if (likelihoods1[n] > max1) {max1 = likelihoods1[n];}
            }
            for (int n = 0; n < likelihoods2.length; n++) {
                if (likelihoods2[n] > max2) {max2 = likelihoods2[n];}
            }
            for (int n = 0; n < likelihoods3.length; n++) {
                if (likelihoods3[n] > max3) {max3 = likelihoods3[n];}
            }
            double likelihood1 = weight1*Math.abs(nextLikelihoods[0][nextaction]-max1);
            double likelihood2 = weight2*Math.abs(nextLikelihoods[1][(mc.getCurrentNote() + (nextaction-12)+3)%12]-max2);
            double likelihood3 = weight3*Math.abs(nextLikelihoods[2][(int)Math.signum(nextaction-12)+1]-max3)/4.4f;
            double dissonance = mc.getDissonance((byte)nextaction);
            double cauchy = mc.getCauchy((byte)(nextaction-12));
            double over = (mc.getCurrentNote() + (nextaction-12) > maxNote) ? 1:0;
            double under = (mc.getCurrentNote() + (nextaction-12) < minNote) ? 1:0;
            nextinputs.addAll(new double[]{likelihood1,likelihood2,likelihood3,dissonance,cauchy,over,under});
            patternTracking.addReward(reward);
            ann.setInputs(inputs);
            ann.feedForward();
            double current = ann.predict();
            if (Double.isNaN(current)) System.err.println("NaN error");
            ann.setInputs(nextinputs);
            ann.feedForward();
            double next = ann.predict();
            double qValue = current + lr*(currentreward + df*(next-current));
            ann.setInputs(inputs);
            ann.train(qValue);
            theaction = nextaction;
            currentreward = reward;
            inputs = nextinputs;
            nextinputs = new TDoubleArrayList();
            count++;
        }
        mc.newEpisode();
    }
    
    /*public void expandNode(Node n) {
        Node newNode;
        if (!n.isExpanded()) {
            n.setExpanded(true);
            for (byte i = 0; i < noteNumber; i++) {
                    newNode = new Node(n,i,0.0f);
                    skip = false;
                    for (int j = 0; j < noteNumber; j++) {
                        if (n.getChildren().length > j && n.getChild(j) != null) {
                            if (n.getChild(j).getNoteValue() == i) {
                                skip = true;
                            }
                        }
                    }
                    if (!skip) {
                        nodes.add(newNode);
                        n.putChild(newNode);
                    }
            }
        }
    }*/
    
    /*public void expandNode2(int nID) {
        int newID;
        if (nodeExpands.get(nID) == 0) {
            nodeExpands.set(nID,(byte)1);
            for (byte i = 0; i < noteNumber; i++) {
                    newID = nodeIDs.size();
                    skip = false;
                    for (int j = 0; j < noteNumber; j++) {
                        if (children.get(nID)[j] != -1) {
                            if (nodeNoteValues.get(children.get(nID)[j]) == i) {
                                    skip = true;
                            }
                        }
                    }
                    if (!skip) {
                        parents.add(nID);//adds new parent value on spot of newID
                        nodeNoteValues.add(i);
                        nodeValues.add(0.0f);
                        nodeExpands.add((byte)0);
                        Integer[] ints = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
                        children.add(ints);
                        nodeIDs.add(newID);
                        nodeRewards.add(0);
                        nodeETraces.add(0);
                        children.get(nID)[i] = newID;
                    }
            }
        }
    }*/
    
   /* public byte chooseAction() {
        action = 0;
        sum = 0;
        bigger = false;
        done = false;
        prob = r.nextFloat();
        out:
        for (byte i = 0; i < noteNumber; i++) {
            if (done) {break out;}
            numerator = Math.pow(Math.E,currentNode.getChild(i).getValue()/temp);
            denominator = 0;
            for (int j = 0; j < noteNumber; j++) {
                denominator += Math.pow(Math.E,currentNode.getChild(j).getValue()/temp);
            }
            sum += (actionProbs[i] = (numerator/denominator));
            if ((bigger && (sum > prob)) || i == 24) {done = true; action = i;}
            if (sum < prob) {bigger = true;}
        }
        return action;
    }*/
    
   /* public byte chooseAction2() {
        
        //change so it works with patterns
        action = 0;
        sum = 0;
        bigger = false;
        done = false;
        prob = r.nextFloat();
        out:
        for (byte i = 0; i < noteNumber; i++) {
            if (done) {break out;}
            int m = children.get(currentNodeID)[i];
            if (m != -1) {numerator = Math.pow(Math.E,nodeValues.get(children.get(currentNodeID)[i])/temp);}
            denominator = 0;
            for (int j = 0; j < noteNumber; j++) {
                int n = children.get(currentNodeID)[j];
                if (n != -1 ) denominator += Math.pow(Math.E,nodeValues.get(n)/temp);
            }
            sum += (actionProbs[i] = (numerator/denominator));
            if ((bigger && (sum > prob)) || i == 24) {done = true; action = i;}
            if (sum < prob) {bigger = true;}
        }
        return action;
    }*/
    
    /*public void qLearn() {
        Node next = getMaxQ(currentNode);
        rewardcount++;
        currentNode.setReward(reward = mc.assignUtility(currentNode.getNoteValue()));
        currentNode.setValue(currentNode.getValue() + currentNode.geteTrace()*lr*(reward + df*(next.getValue() - currentNode.getValue())));
    }  */ 
    
    /*public void qLearn2() {
        int next = getMaxQ2(currentNodeID);
        rewardcount++;
        nodeRewards.set(currentNodeID,reward = mc.assignUtility(nodeNoteValues.get(currentNodeID)));
        patternTracking.addReward(reward);
       */
    
    /*public Node getMaxQ(Node n) {
        expandNode(n);
        int nextInt = r.nextInt(noteNumber);
        while (!n.isChild(nextInt = r.nextInt(noteNumber))) {}
        iterNode = n.getChild(nextInt);
        for (int i = 0; i < n.getChildren().length; i++) {
            iterNode = (n.getChild(i).getValue() > iterNode.getValue()) ? n.getChild(i) : iterNode;
        }
        return iterNode;
    }*/
    
    /*public int getMaxQ2(int nID) {
        expandNode2(nID);
        int nextInt = r.nextInt(noteNumber);
        int iterID;
        while (children.get(nID)[nextInt%noteNumber] == -1) {nextInt++;}
        iterID = children.get(nID)[nextInt];
        for (int i = 0; i < noteNumber; i++) {
            int next = children.get(nID)[i];
            if (next != -1) {iterID = (nodeValues.get(next) > nodeValues.get(iterID)) ? next : iterID;}
        }
        return iterID;
    }*/
    
   /* public void fadeTraces() {
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            iterNode = (Node) it.next();
            iterNode.seteTrace(iterNode.geteTrace()*lambda*df);
        }
    
    }*/
    /*public void fadeTraces2() {
        for (int i = 0; i < nodeETraces.size(); i++) {
            nodeETraces.set(i,nodeETraces.get(i)*lambda*df);
        }
    }*/

    /*public ArrayList<Node> getNodes() {
        return nodes;
    }*/
    
    public TIntArrayList getNodeIDs() {
        return nodeIDs;
    }
    
    public TByteArrayList getNodeNoteValues() {
        return nodeNoteValues;
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
