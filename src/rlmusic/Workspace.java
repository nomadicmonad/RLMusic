package rlmusic;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class Workspace extends JPanel {
    private TreeMap<Integer,float[]> mapping;
    private int episodeRepeat = 0;
    private float accumulatedReward = 0;
    
    public Workspace() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        mapping = new TreeMap<>();
    }
    public void setEpisodeRepeat(int episodeRepeat) {this.episodeRepeat = episodeRepeat;}
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600,400);
    }
    
    public void setReward(int rewardcount, int episodeRepeatNum, float reward) {
        float[] rewards = new float[episodeRepeat];
        accumulatedReward += reward;
        if (mapping.containsKey(rewardcount)) {rewards = (float[]) mapping.remove(rewardcount);}
        rewards[episodeRepeatNum] = reward;
        mapping.put(rewardcount,rewards);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);       
        g.drawString("Reinforcement Learning Music Program v-1.0",10,20);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1.0f));
        Rectangle2D.Double rect = new Rectangle2D.Double(98,98,404,204);
        g2.draw(rect);
        g2.fill(rect);
        rect = new Rectangle2D.Double(100,100,400,200);
        g2.draw(rect);
        g2.setPaint(Color.WHITE);
        g2.fill(rect);
        g2.setPaint(Color.GRAY);
        for (int i = 0; i < 40; i++) {
            g2.drawLine(100 + i*10,100,100 + i*10,300);
        }
        for (int i = 0; i < 20; i++) {
            g2.drawLine(100,100 + i*10,500,100 + i*10);
        }
        g2.setPaint(Color.BLACK);
        g2.drawString("Time step", 275, 315);
        g2.drawString("Reward value", 20, 200);
        g2.drawString("Accumulated Reward: " + String.format("%.3f",accumulatedReward), 230,90);
        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(Color.DARK_GRAY);
        g2.setColor(Color.LIGHT_GRAY);
        g2.setPaint(Color.BLACK);
        g2.setColor(Color.BLUE);
        for (int n = 0; n < mapping.size(); n++) {
            float[] rewards = mapping.get(n);
            float[] previousRewards = new float[2];
            if (rewards != null) {
            if (rewards.length > 0) previousRewards[0] = rewards[0];
            if (n > 0) previousRewards = mapping.get(n-1);
            if (n == 0) previousRewards[0] = rewards[0];
            int index = (n==0)? n: n-1;
            if (previousRewards != null) {
                g2.drawLine(100 + (index)*5,Math.round(300 - previousRewards[0]*225), 100 + n*5,300 - Math.round(rewards[0]*225));
                if (rewards[0] > 0.05) g2.drawString("" + String.format("%.3f",rewards[0]), 90 + n*5, 300 - Math.round(rewards[0]*225)-10);
            }}
        }
    }  
    
}
