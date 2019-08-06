import javax.sound.midi.*;


/**
 * MusicPlayer
 */
public class MusicPlayer {
    public static void main(String[] args) {
        MusicPlayer player = new MusicPlayer();
        if(args.length < 2){
            System.out.println("Don't forget the instrument and note args");
        }else{
            int instrument = Integer.parseInt(args[0]);
            int note = Integer.parseInt(args[1]);
            player.play(instrument, note);
        }
    }

    public void play(int instrument, int note) {
        try {
            Sequencer player = MidiSystem.getSequencer();
            player.open();

            Sequence sequence = new Sequence(Sequence.PPQ, 4);
            Track track = sequence.createTrack();

            ShortMessage firstMessage = new ShortMessage();
            firstMessage.setMessage(192, 1, instrument, 0);
            MidiEvent changeInstrumentEvent = new MidiEvent(firstMessage, 1);
            track.add(changeInstrumentEvent);

            ShortMessage shortMessage1 = new ShortMessage();
            shortMessage1.setMessage(144, 1, note, 100);
            MidiEvent noteOnEvent = new MidiEvent(shortMessage1, 1);
            track.add(noteOnEvent);

            ShortMessage shortMessage2 = new ShortMessage();
            shortMessage2.setMessage(128, 1, note, 100);
            MidiEvent noteOffEvent = new MidiEvent(shortMessage2, 16);
            track.add(noteOffEvent);

            player.setSequence(sequence);
            player.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
