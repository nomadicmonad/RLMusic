package rlmusic;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import java.util.ArrayList;

public class MusicPlayer extends Thread {
    private short currentNote = 60;
    private int tempo = 1000;
    private Receiver synthRcvr;
    private MusicGenerator mg;
    //private ArrayList<Byte> noteCache;

    @Override
    public void run() {
        //noteCache = new ArrayList<>();
        startUp();
    }

    public void startUp() {
        try {
            MidiDevice midiDevice = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[3]);
            if (!midiDevice.isOpen()) {
                Sequencer seq = MidiSystem.getSequencer();
                Synthesizer synth = MidiSystem.getSynthesizer();
                synthRcvr = synth.getReceiver();
                midiDevice.open();
                synth.open();
                seq.open();
                boolean runIt = true;
                int i = 0;
                short next = -1;
                while (runIt) {
                    i++;
                    if (mg != null && (next = mg.accessNote((short)0,false)) == -1) continue;
                    try {
                        //dont interrupt if continuation
                        if (currentNote != -2) synthRcvr.send(new ShortMessage(ShortMessage.NOTE_OFF,0,currentNote,70),-1);
                        System.out.println("Current MIDI note: " + currentNote);
                        synthRcvr.send(new ShortMessage(ShortMessage.NOTE_ON,0,currentNote,70),-1);
                    } catch (InvalidMidiDataException e) {System.out.println(e);}
                    if (next == -3) {} //-3 == take a longer break;
                    else {
                        if (next == -1) {currentNote = 60;}
                        else {
                            currentNote = next;
                        }
                    }
                    if (seq.isRunning()) { 
                    } else {
                        try {
                            Thread.sleep(tempo);
                        } catch (InterruptedException e) {System.out.println(e);}
                        if (i == 1000) {
                        }
                    }
                }
                seq.close();
                synth.close();
                midiDevice.close();
                System.exit(0);
            }
        } catch (MidiUnavailableException e) {System.out.println(e);}
    }
    public void setGenerator(MusicGenerator mg) {this.mg = mg;}
    /*public void addNote(byte note) {
        noteCache.add(note);
    }*/
    
    /*public int getCacheSize() {return noteCache.size();}*/
}
