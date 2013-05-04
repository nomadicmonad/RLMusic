
package rlmusic;

import java.util.ArrayList;
import java.util.Random;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.iterator.TIntIterator;

public class DurationPattern {
    
    private ArrayList<HuffNode> huffStack;
    private ArrayList<Integer[]> patterns;
    private int[] pattern;
    private int counter = -1;
    private Random r;
    private int limit = 3;
    
    public DurationPattern() {
        r = new Random();
        patterns = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            patterns.add(createPattern());
        }
        huffmanEncoding();
    }
    
    public Integer[] createPattern() {
        Integer[] nextPattern;
        TIntArrayList currentStack = new TIntArrayList();
        currentStack.add(1);
        TIntArrayList nextStack = new TIntArrayList();
        for (int i = 0; i < limit; i++) {
            TIntIterator it = currentStack.iterator();
            while (it.hasNext()) {
                int n = it.next();
                if (r.nextBoolean()) {
                    nextStack.add(n*2);
                    nextStack.add(n*2);
                }
                else {
                    nextStack.add(n);
                }
            }
            currentStack = nextStack;
            nextStack = new TIntArrayList();
        }
        nextPattern = new Integer[currentStack.size()];
        for (int i = 0; i < nextPattern.length; i++) {
            nextPattern[i] = currentStack.get(i);
        }
        return nextPattern;
    }
    
    public void huffmanEncoding() {
        int index = -1;
        int biggest = -1;
        for (int i = 0; i < patterns.size(); i++) {
            huffStack = new ArrayList();
            for (Integer intgr : patterns.get(i)) {
                int identification = intgr;
                int frequency = 0;
                HuffNode nextNode = new HuffNode(identification,frequency,null,null);
                for (HuffNode hn : huffStack) {
                    if (hn.id == identification) {
                        nextNode = hn;
                        hn.frequency++;
                        frequency++;
                    }
                }
                if (frequency == 0) huffStack.add(nextNode);
            }
            while (huffStack.size() > 1) {
                int smallest1 = 100;
                int smallest2 = 100;
                int index1 = -1;
                int index2 = -1;
                for (int n = 0; n < huffStack.size(); n++) {
                    if (huffStack.get(n).frequency < smallest1) {
                        smallest2 = smallest1;
                        index2 = index1;
                        smallest1 = huffStack.get(n).frequency;
                        index1 = n;
                        
                    }
                    else if (huffStack.get(n).frequency < smallest2) {
                        smallest2 = huffStack.get(n).frequency;
                        index2 = n;
                    }
                }
                HuffNode left = null;
                HuffNode right = null;
                int r = 0;
                if (index1 != -1) {left = huffStack.remove(index1); if (index1 < index2 ) {r = 1;}}
                if (index2 != -1 && index1 != index2) right = huffStack.remove(index2-r);
                int nextWeight = (left != null) ? left.frequency : 0;
                nextWeight += (right != null) ? right.frequency : 0;
                huffStack.add(new HuffNode(-1,nextWeight,left,right));
            }
            int currentBits = getTotalBits(huffStack.get(0),0);
            if (currentBits > biggest) {
                biggest = currentBits;
                index = i;
            }
        }
        Integer[] biggestHuffman = patterns.get(index);
        pattern = new int[biggestHuffman.length];
        for (int i = 0; i < biggestHuffman.length; i++) {
            pattern[i] = biggestHuffman[i];
        }
    }
    
    public int getTotalBits(HuffNode huffNode, int depth) {
        if (huffNode == null) {return 0;}
        if (huffNode.id == -1) {return getTotalBits(huffNode.leftChild, depth +1) + getTotalBits(huffNode.rightChild,depth +1);}
        else { return (huffNode.frequency * depth) + getTotalBits(huffNode.leftChild,depth +1) + getTotalBits(huffNode.rightChild,depth +1);}
    }
    
    class HuffNode {
        public int id;
        public int frequency;
        public HuffNode leftChild;
        public HuffNode rightChild;
        public HuffNode(int id, int frequency, HuffNode leftChild, HuffNode rightChild) {
            this.id = id;
            this.frequency = frequency;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }
        
    }
    
    public int consume() {
        counter++;
        if (counter > pattern.length - 1) {counter = 0; return -1; }
        else {return pattern[counter];}
    }
    
    
    
}
