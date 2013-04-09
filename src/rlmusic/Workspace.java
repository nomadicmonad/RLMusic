package rlmusic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Iterator;
import java.util.TreeMap;

public class Workspace extends JPanel {
    private MusicCritic music;
    private TreeMap<Integer,float[]> mapping;
    private int episodeRepeat = 0;
    
    public Workspace() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        mapping = new TreeMap<>();
    }
    public void setCritic(MusicCritic music) {this.music = music;}
    public void setEpisodeRepeat(int episodeRepeat) {this.episodeRepeat = episodeRepeat;};
    
    public Dimension getPreferredSize() {
        return new Dimension(600,400);
    }
    
    public void setReward(int rewardcount, int episodeRepeatNum, float reward) {
        float[] rewards = new float[episodeRepeat]; //30 repeats
        if (mapping.containsKey(rewardcount)) {rewards = (float[]) mapping.remove(rewardcount);}
        rewards[episodeRepeatNum] = reward;
        mapping.put(rewardcount,rewards);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);       

        // Draw Text
        
        g.drawString("Reinforcement Learning Music Program v-0.1",10,20);
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
        g2.drawString("Time step", 275, 340);
        g2.drawString("Reward value", 25, 200);
        g2.setStroke(new BasicStroke(3.0f));
        g2.setColor(Color.DARK_GRAY);
        g2.setColor(Color.LIGHT_GRAY);
        g2.setPaint(Color.BLACK);
        g2.setColor(Color.RED);
        for (int n = 0; n < mapping.size(); n++) {
            float[] rewards = mapping.get(n);
            if (rewards == null) {continue;}
            for (int j = 0; j < rewards.length; j++) {
                g2.drawLine(100 + n*5,Math.round(300 - rewards[j]*300), 100 + n*5,300 - Math.round(rewards[j]*300));
            }
        }
        /*for (int i = 5; i > 0; i++) {
            g2.drawString("0." + (i*2), 80,105 + i*40);
        }
        for (int i = 10; i > -1; i--) {
            g2.drawString("" + (i*10), 90 + i*40,320);
        }
        repaint();*/
    }  
    
}
