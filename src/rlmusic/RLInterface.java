package rlmusic;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class RLInterface extends JFrame implements WindowListener {
    
    private JMenuBar menuBar;
    private JPanel contentPane;
    private MusicPlayer mp;
    private Workspace ws;
    private MusicCritic music;
    
    public RLInterface(final int episodeRepeat,boolean press) {
        addWindowListener(this);
        mp = new MusicPlayer();
        setSize(800,600);
        setBounds(300,300,800,600);
        setResizable (false);
        menuBar = new JMenuBar();
        contentPane = new JPanel(new BorderLayout());
        JButton button = new JButton("Start Generation");
        
        JButton button2 = new JButton("Start Playing");
        
        JButton button3 = new JButton("Start Recording");
        JButton button4 = new JButton("Stop Recording");
        JButton button5 = new JButton("Stop Playing");
        button.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mp.start();
                music = new MusicCritic(mp,ws,episodeRepeat);
                music.start();
                ws.repaint();
            }
        });    
        button2.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mp.allow();
            }
        }); 
        button3.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mp.record();
            }
        }); 
        button4.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mp.stopRecord();
            }
        }); 
        button5.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mp.stopPlaying();
            }
        }); 
        setContentPane(contentPane);
        ws = new Workspace();
        ws.setLayout(null);
        contentPane.add(menuBar,BorderLayout.NORTH);
        contentPane.add(ws,BorderLayout.EAST);
        ws.add(button);
        ws.add(button2);
        ws.add(button3);
        ws.add(button4);
        ws.add(button5);
        button.setBounds(200,320,200,20);
        button2.setBounds(100,360,200,20);
        button3.setBounds(100,340,200,20);
        button4.setBounds(300,340,200,20);
        button5.setBounds(300,360,200,20);
        if (press) {
        for(ActionListener a: button.getActionListeners()) {
           a.actionPerformed(new ActionEvent(button,1001,""));
        }
        for(ActionListener a: button2.getActionListeners()) {
           a.actionPerformed(new ActionEvent(button2,1001,""));
        }
        }
    
    }
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosing(WindowEvent e) {if (music!= null) music.stopANN();}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    
    public static void main(String[] args) {
        String s = "100";
        boolean b = false;
        if (args.length == 0) {}
        else {s = args[1]; b = true;}
        System.out.println("Initializing with " + s + " episodal repeats");
        RLInterface rl = new RLInterface(Integer.parseInt(s),b);
        rl.setTitle("Reinforcement Learning Music Program");
        rl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rl.pack();
        rl.setVisible(true);
    }
}
