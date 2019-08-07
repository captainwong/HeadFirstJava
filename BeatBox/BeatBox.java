import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * BeatBox
 */
public class BeatBox {
    JFrame theFrame;
    JPanel mainPanel;
    JList incomingList;
    JTextField userMsg;
    ArrayList<JCheckBox> checkBoxs;
    int nextNum;
    Vector<String> listVector = new Vector<>();
    String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    HashMap<String, boolean[]> otherSeqsMap = new HashMap<>();

    Sequencer sequencer;
    Sequence sequence;
    Sequence mySequence = null;
    Track track;

    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
            "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
            "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};

    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        String name = "Jack";
        String ip = "127.0.0.1";
        int port = 4242;
        if (args.length > 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            name = args[2];
        }
        new BeatBox().startUp(name, ip, port);
    }

    public void startUp(String name, String ip, int port){
        userName = name;

        try{
            Socket socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Thread remote = new Thread(new RemoteReader());
            remote.start();
        }catch(Exception ex){ex.printStackTrace();}
    }

    public void buildGUI() {
        theFrame = new JFrame("Cyber BeatBox");
        //theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkBoxs = new ArrayList<>();

        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton serialize = new JButton("Serialize");
        serialize.addActionListener(new MySerializeListener());
        buttonBox.add(serialize);

        JButton restore = new JButton("Restore");
        restore.addActionListener(new MyRestoreListener());
        buttonBox.add(restore);

        JButton sendIt = new JButton("Send It");
        sendIt.addActionListener(new MySendItListener());
        buttonBox.add(sendIt);

        userMsg = new JTextField();
        buttonBox.add(userMsg);

        incomingList = new JList();
        incomingList.addListSelectionListener(new MyListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        incomingList.setListData(listVector);


        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxs.add(c);
            mainPanel.add(c);
        }

        setupMidi();

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    public void setupMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart() {
        int[] trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) {
            trackList = new int[16];
            int key = instruments[i];

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) checkBoxs.get(j + (16 * i));
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }

            makeTracks(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        }

        track.add(makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        }
    }

    public class MySerializeListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            boolean[] checkBoxState = new boolean[256];
            for (int i = 0; i < 256; i++) {
                JCheckBox checkBox = (JCheckBox) checkBoxs.get(i);
                checkBoxState[i] = checkBox.isSelected();
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File("Checkbox.ser"));
                ObjectOutputStream os = new ObjectOutputStream(fileOutputStream);
                os.writeObject(checkBoxState);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MyRestoreListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            boolean[] checkBoxState = null;
            try {
                FileInputStream fileInputStream = new FileInputStream(new File("Checkbox.ser"));
                ObjectInputStream is = new ObjectInputStream(fileInputStream);
                checkBoxState = (boolean[]) is.readObject();
                is.close();
                for (int i = 0; i < 256; i++) {
                    JCheckBox checkBox = (JCheckBox) checkBoxs.get(i);
                    checkBox.setSelected(checkBoxState[i]);
                }
                sequencer.stop();
                buildTrackAndStart();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class MySendItListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean[] checkBoxState = new boolean[256];
            for(int i = 0; i < 256;i++){
                checkBoxState[i] = ((JCheckBox)checkBoxs.get(i)).isSelected();
            }
            String msg = null;
            try{
                out.writeObject(userName + nextNum++ + ": " + userMsg.getText);
                out.writeObject(checkBoxState);
            }catch(Exception ex){ex.printStackTrace();}
            userMsg.setText("");
        }
    }

    public class MyListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if(!e.getValueIsAdjusting()){
                String selected = (String)incomingList.getSelectedValue();
                if(selected != null){
                    boolean[] selectedState = (boolean[])otherSeqsMap.get(selected);
                    changeSequence(selectedState);
                    sequencer.stop();
                    buildTrackAndStart();
                }
            }
        }
    }

    public class RemoteReader implements Runnable{
        boolean[] checkBoxState = null;
        String name = null;
        Object obj = null;

        @Override
        public void run() {
            try{
                while((obj = in.readObject()) != null){
                    System.out.println("Got an object from server");
                    System.out.println(obj.getClass());
                    name = (String)obj;
                    checkBoxState = (boolean[])in.readObject();
                    otherSeqsMap.put(name, checkBoxState);
                    listVector.add(name);
                    incomingList.setListData(listVector);
                }
            }catch(Exception ex){ex.printStackTrace();}
        }
    }

    public void makeTracks(int[] list) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];
            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i + 1));
            }
        }
    }

    public MidiEvent makeEvent(int command, int channel, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }
}
