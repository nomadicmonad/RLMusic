
package rlmusic;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;

public class StateSpace {
    public Node root;
    public ArrayList<Node> nodes;
    public Node currentNode;
    private float lr = 0.1f;//learning rate
    private float df = 0.9f; //discount
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
    
    
    public StateSpace(MusicCritic mc, Workspace ws, double tempDiscount) {
        this.mc = mc;
        nodes = new ArrayList<>();
        this.tempDiscount = tempDiscount;
        this.ws = ws;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }
    
    public byte[] getGreedy() {
        byte[] greedy = new byte[10];
        Node iterNode = root;
        for (int i = 0; i < 10; i++) {
            greedy[i] = iterNode.getNoteValue();
            iterNode = getMaxQ(iterNode);
        }
        return greedy;
    }
    
    public void episode(int theEpisodeRepeat) {
        this.theEpisodeRepeat = theEpisodeRepeat;
        currentNode = root;
        count = 0;
        rewardcount = 0;
        while (count < 10) {
            expandNode(currentNode);
            currentNode = currentNode.getChild(chooseAction());
            fadeTraces();
            currentNode.seteTrace(1);
            qLearn();
            count++;
        }
    }
    
    public void expandNode(Node n) {
        if (!n.isExpanded()) {
            n.setExpanded(true);
            for (byte i = 0; i < 25; i++) {
                Node newNode = new Node(n,i,0.0f);
                    boolean skip = false;
                    for (int j = 0; j < 25; j++) {
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
    }
    
    public byte chooseAction() {
        double[] actionProbs = new double[25];
        float sum = 0;
        byte action = 0;
        boolean bigger = false;
        boolean done = false;
        r = new Random();
        float prob = r.nextFloat();
        for (byte i = 0; i < 25; i++) {
            if (done) {continue;}
            double numerator = Math.pow(Math.E,currentNode.getChild(i).getValue()/temp);
            double denominator = 0;
            for (int j = 0; j < 25; j++) {
                denominator += Math.pow(Math.E,currentNode.getChild(j).getValue()/temp);
            }
            actionProbs[i] = numerator/denominator;
            sum += actionProbs[i];
            if ((bigger && sum > prob) || i == 24) {done = true; action = i;}
            if (sum < prob) {bigger = true;}
        }
        return action;
    }
    
    public void qLearn() {
        Node next = getMaxReward(currentNode);
        float reward = mc.assignUtility(currentNode.getNoteValue());
        rewardcount++;
        currentNode.setReward(reward);
        float q = currentNode.getValue() + currentNode.geteTrace()*lr*(reward + df*(next.getValue() - currentNode.getValue()));
        currentNode.setValue(q);
    }   
    
    public Node getMaxQ(Node n) {
        expandNode(n);
        r = new Random();
        int nextInt = r.nextInt(25);
        Node iterNode = n.getChild(nextInt);
        if (iterNode == null) {
            int counter = nextInt + 1;
            while (counter != nextInt) {
                if ((iterNode = n.getChild(counter)) != null) {
                    counter = nextInt;
                }
                counter = (counter+1)%25;
            }
        }
        for (int i = 0; i < n.getChildren().length; i++) //chose node with highest value
            {
                if (n.getChild(i).getValue() > iterNode.getValue()) {
                    iterNode = n.getChild(i);
                }
            }
        
        return iterNode;
    }
    
    public Node getMaxReward(Node n) {
        expandNode(n);
        r = new Random();
        int nextInt = r.nextInt(25);
        Node iterNode = n.getChild(nextInt);
        if (iterNode == null) {
            int counter = nextInt + 1;
            while (counter != nextInt) {
                if ((iterNode = n.getChild(counter)) != null) {
                    counter = nextInt;
                }
                counter = (counter+1)%25;
            }
        }
        float testingValue = 0;
        float rewardCount = 0;
        for (int i = 0; i < n.getChildren().length; i++) //chose node with highest value
            {
                testingValue++;
                if (n.getChild(i).getReward() > iterNode.getReward()) {
                    iterNode = n.getChild(i);
                    rewardCount++;
                }
            }
        if (rewardCount/testingValue > 0.8) {doneHere = true; System.out.println("Saturation reached with episode repetition of: " + theEpisodeRepeat); System.exit(0);
        }
        return iterNode;
    }
    
    public void fadeTraces() {
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            Node n = (Node) it.next();
            n.seteTrace(n.geteTrace()*lambda*df);
        }
    
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }
    
    public void resetTemp() {temp = 1;}
    public void decrementTemp() {temp = temp*tempDiscount;
    
    }
    
    public void incrementEpisodeCount() {episodeCount++;}
    public void incrementEpisodeRepeat() {episodeRepeat++;}
    
    
}
