package rlmusic;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;

public class MusicPlayer extends Thread {
    private short currentNote = -5;
    private int tempo = 1000;
    
    private Receiver synthRcvr;
    private MusicGenerator mg;
    private Track track;
    private MidiEvent event,event2,event3;
    private ShortMessage message,message2,message3;
    private Sequence sequence;
    private int speed = 1;
    
    @Override
    public void run() {
        try {
            startUp();
        } catch (InvalidMidiDataException ex) {
        }
    }

    public void startUp() throws InvalidMidiDataException {
        try {
            MidiDevice midiDevice = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[3]);
            if (!midiDevice.isOpen()) {
                Synthesizer synth;
                try (Sequencer seq = MidiSystem.getSequencer()) {
                    synth = MidiSystem.getSynthesizer();
                    synthRcvr = synth.getReceiver();
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
                        if (mg != null && (next = mg.accessNote((short)0,false)) == -1) continue;
                        try {
                            System.out.println("Current MIDI note: " + currentNote);
                            if (currentNote != -5) {
                                //i++;
                                message = new ShortMessage(ShortMessage.NOTE_ON,0,currentNote,70);
                                message2 = new ShortMessage(ShortMessage.NOTE_ON,1,currentNote+5,70);
                                message3 = new ShortMessage(ShortMessage.NOTE_ON,2,currentNote-3,70);
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
                        currentNote = next;
                        if (seq.isRunning()) { 
                        } else {
                           /* try {
                                Thread.sleep(tempo);
                            } catch (InterruptedException e) {System.out.println(e);}*/
                        }
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
