package code;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;

public class Musica
{
    Config cfg = Config.getInstance();
    Sequencer seq;
    
    File midiFile = new File(cfg.musica_);
    File midiFileGO = new File(cfg.GO_);
    File midiFileVO = new File(cfg.VO_);
    
    public void setSeq(File mid){
        try {
            seq = MidiSystem.getSequencer();
            seq.setSequence(MidiSystem.getSequence(mid));
            seq.open();
        } catch (MidiUnavailableException 
                | InvalidMidiDataException| IOException mue) { }
    }
    public void play(){
        setSeq(midiFile);
        if (cfg.MusicOn) seq.start();
    }
    public void GO(){
        seq.stop(); setSeq(midiFileGO);  seq.start();
    }
    public void VO(){
        seq.stop(); setSeq(midiFileVO); seq.start();
    }
}