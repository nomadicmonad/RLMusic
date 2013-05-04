package rlmusic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

public class RLInterface extends JFrame implements WindowListener {
    
    private JMenuBar menuBar;
    private JMenu menu, submenu;
    private JMenuItem menuItem;
    private JRadioButtonMenuItem rbMenuItem;
    private JCheckBoxMenuItem cbMenuItem;
    private JPanel contentPane;
    private MusicPlayer mp;
    private Workspace ws;
    private MusicCritic music;
    private byte[] notes;
    
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
 
            public void actionPerformed(ActionEvent e)
            {
                mp.start();
                music = new MusicCritic(mp,ws,episodeRepeat);
                music.start();
                ws.setCritic(music);
                ws.repaint();
            }
        });    
        button2.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
                mp.allow();
            }
        }); 
        button3.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
                mp.record();
            }
        }); 
        button4.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
                mp.stopRecord();
            }
        }); 
        button5.addActionListener(new ActionListener() {
 
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
        menu = new JMenu("File");
        menuBar.add(menu);
        menuItem = new JMenuItem("New",
                         new ImageIcon("\\icon.gif"));
        menu.add(menuItem);
        menuItem = new JMenuItem("Save",
                         new ImageIcon("\\icon.gif"));
        menu.add(menuItem);
        menuItem = new JMenuItem("Open",
                         new ImageIcon("\\icon.gif"));
        menu.add(menuItem);

        //a submenu
        menu.addSeparator();
        submenu = new JMenu("Recent");

        menuItem = new JMenuItem("Sample File 1");
        submenu.add(menuItem);

        menuItem = new JMenuItem("Sample File 2");
        submenu.add(menuItem);
        menu.add(submenu);

        //Build second menu in the menu bar.
        menu = new JMenu("Options");
        menuBar.add(menu);
        menuItem = new JMenuItem("Menu item");
        menuItem.setEnabled(false);
        menu.add(menuItem);
        ButtonGroup group = new ButtonGroup();
        rbMenuItem = new JRadioButtonMenuItem("Menu item");
        rbMenuItem.setSelected(true);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Option 1");
        group.add(rbMenuItem);
        menu.add(rbMenuItem);
        rbMenuItem = new JRadioButtonMenuItem("Option 2");
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        //a group of check box menu items
        menu.addSeparator();
        cbMenuItem = new JCheckBoxMenuItem("Option 1");
        menu.add(cbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("Option 2");
        menu.add(cbMenuItem);
        cbMenuItem = new JCheckBoxMenuItem("Option 3");
        menu.add(cbMenuItem);
        if (press) {
        for(ActionListener a: button.getActionListeners()) {
           a.actionPerformed(new ActionEvent(button,1001,""));
        }
        for(ActionListener a: button2.getActionListeners()) {
           a.actionPerformed(new ActionEvent(button2,1001,""));
        }
        }
    
    }
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {if (music!= null) music.stopANN();}
    public void windowClosed(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    
    public static void main(String[] args) {
        String s = "50";
        boolean b = false;
        if (args.length == 0) {}
        else {s = args[1]; b = true;}
        System.out.println("Initializing with " + s + " episodal repeats");
        RLInterface rl = new RLInterface(Integer.parseInt(s),b);
        rl.setTitle("Reinforcement Learning Music Program");
        rl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //rl.setIconImage(new ImageIcon("C:\\Users\\Niklas\\Desktop\\icon.jpg").getImage());
        rl.pack();
        rl.setVisible(true);
    }
}
