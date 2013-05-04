package rlmusic;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;
import java.io.File;
import java.net.URI;

public class MusicPlayer extends Thread {
    
    private short currentNote = -5;
    private short previousNote = 0;
    private MusicGenerator mg;
    private Track track;
    private MidiEvent event,event2,event3;
    private ShortMessage message,message2,message3;
    private Sequence sequence;
    private int speed = 1;
    private boolean allowed = false;
    private boolean record = false;
    private boolean hasBeenOpen = false;
    @Override
    public void run() {
        try {
            startUp();
        } catch (InvalidMidiDataException ex) {
        }
    }
    
    public synchronized void allow() {allowed = true;}
    public synchronized void stopPlaying() {allowed = false;}
    public synchronized boolean isAllowed() {return allowed;}
    public synchronized void record() {record = true;}
    public synchronized void stopRecord() {record = false;}

    public void startUp() throws InvalidMidiDataException {
        try {
            MidiDevice midiDevice = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[3]);
            if (!midiDevice.isOpen()) {
                Synthesizer synth;
                try (Sequencer seq = MidiSystem.getSequencer()) {
                    synth = MidiSystem.getSynthesizer();
                    midiDevice.open();
                    synth.open();
                    seq.open();
                    sequence = new Sequence(Sequence.PPQ,speed);
                    track = sequence.createTrack();
                    seq.setSequence(sequence);
                    DurationPattern durationPattern = new DurationPattern();
                    boolean runIt = true;
                    int i = 0;
                    int dur = 1;
                    short next = -1;
                    while (runIt) {
                        if (!isAllowed()) {if (hasBeenOpen){seq.close();} continue;}
                        else if (record && !seq.isRecording()) {seq.startRecording();}
                        else
                        { 
                            if (seq.isRecording() && !record) {
                                //seq.stopRecording();
                                //record = false;
                                try {
                                    java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("_MM_dd_HH_mm_ss");
                                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                                    String result = dateFormat.format(calendar.getTime());
                                    File f = new File("recording"+result+".mid");
                                    f.createNewFile();
                                    MidiSystem.write(sequence, MidiSystem.getMidiFileTypes(sequence)[0], f);
                                }
                                catch (java.io.IOException e) { System.err.println(e);}
                            }
                        }
                        if (hasBeenOpen) {
                            if (!seq.isOpen()){
                            try {
                            seq.open();
                            seq.setSequence(sequence);
                        } catch (MidiUnavailableException e) {}}}
                        hasBeenOpen = true;
                        if (mg != null && (next = mg.accessNote((short)0,false)) == -1) continue;
                        
                        try {
                            System.out.println("Current MIDI note: " + currentNote);
                            if (currentNote != -5) {
                                //i++;
                                if (previousNote == -5) {previousNote = 0;}
                                Chord chord = new Chord(previousNote,currentNote);
                                int before, after;
                                before = chord.index2;
                                after = chord.index3;
                                message = new ShortMessage(ShortMessage.NOTE_ON,0,currentNote,70);
                                message2 = new ShortMessage(ShortMessage.NOTE_ON,1,currentNote+after,70);
                                message3 = new ShortMessage(ShortMessage.NOTE_ON,2,currentNote-before,70);
                                dur = durationPattern.consume();
                                if (dur == -1) {
                                    durationPattern = new DurationPattern();
                                    dur = durationPattern.consume();
                                }
                                i+=(8/dur);
                                event = new MidiEvent(message,i*speed);
                                event2 = new MidiEvent(message2,i*speed);
                                event3 = new MidiEvent(message3,i*speed);
                                track.add(event);  
                                track.add(event2);
                                track.add(event3);
                                seq.start();
                                //synthRcvr.send(new ShortMessage(ShortMessage.NOTE_ON,0,currentNote,70),-1);
                            }
                        } catch (InvalidMidiDataException e) {System.out.println(e);}
                        previousNote = currentNote;
                        currentNote = next;
                    }
                }
                synth.close();
                midiDevice.close();
                System.exit(0);
            }
        } catch (MidiUnavailableException e) {System.out.println(e);}
    }
    public void setGenerator(MusicGenerator mg) {this.mg = mg;}
}
