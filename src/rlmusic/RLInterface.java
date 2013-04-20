package rlmusic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RLInterface extends JFrame {
    
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
        mp = new MusicPlayer();
        setSize(800,600);
        setBounds(300,300,800,600);
        setResizable (false);
        menuBar = new JMenuBar();
        contentPane = new JPanel(new BorderLayout());
        JButton button = new JButton("Start Generation");
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
        setContentPane(contentPane);
        ws = new Workspace();
        ws.setLayout(null);
        contentPane.add(menuBar,BorderLayout.NORTH);
        contentPane.add(ws,BorderLayout.EAST);
        ws.add(button);
        button.setBounds(300,360,200,20);
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
        }}
    
    }
    
    public static void main(String[] args) {
        String s = "500";
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
