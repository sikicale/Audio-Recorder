import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class GUI extends JFrame implements ActionListener {
    JFrame frame = new JFrame();
    JPanel titlePanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JLabel textField = new JLabel();
    JButton buttonRecord;
    JButton buttonPlay;
    TargetDataLine targetLine; // ovo sam morao da izvučem za Recorder, da bi mogao da ga ugasim
    Clip clip;
    boolean startPlay = false;
    boolean startRecord = false;

    public GUI(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,300);
        frame.getContentPane().setBackground(new Color(50,50,50));// menja boju okvira objekta(film oko objekta)
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        textField.setBackground(new Color(25,25,25));
        textField.setForeground(new Color(25,255,0));
        textField.setFont(new Font("Ink Free",Font.BOLD,50));
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setText("Diktafon");
        textField.setOpaque(true);      //kad se podešava pozadina mora i ovo

        titlePanel.setLayout(new BorderLayout());  //raširi pozadinu po celom naslovu
        titlePanel.setBounds(0,0,500,100);

        buttonPanel.setLayout(new GridLayout(1,2));
        buttonPanel.setBackground(new Color(150,150,150));

        buttonRecord = new JButton();
        buttonPanel.add(buttonRecord);
        buttonRecord.setFont(new Font("MV Boli",Font.BOLD,50));
        buttonRecord.setText("Record");
        buttonRecord.setFocusable(false);
        buttonRecord.addActionListener(this);

        buttonPlay = new JButton();
        buttonPanel.add(buttonPlay);
        buttonPlay.setFont(new Font("MV Boli",Font.BOLD,50));
        buttonPlay.setText("Play");
        buttonPlay.setFocusable(false);
        buttonPlay.addActionListener(this);

        titlePanel.add(textField);
        frame.add(titlePanel,BorderLayout.NORTH);
        frame.add(buttonPanel);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if((e.getSource() == buttonRecord)&&!startRecord){
            buttonRecord.setForeground(Color.red);
            startRecord = true;
            try{
                AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100,16,2,4,44100,false);

                DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class,audioFormat);
                if(!AudioSystem.isLineSupported(dataInfo)){
                    System.out.println("Not supported");
                }
                targetLine = (TargetDataLine) AudioSystem.getLine(dataInfo);
                targetLine.open();

               // JOptionPane.showMessageDialog(null,"Hit ok to start recording");
                targetLine.start();

                Thread audioRecordThread = new Thread(){
                    @Override
                    public void run(){
                        AudioInputStream recordingStream = new AudioInputStream(targetLine);
                        File outputFile = new File("Adrijana.wav");     // napravili novi fajl
                        try{
                            AudioSystem.write(recordingStream,AudioFileFormat.Type.WAVE,outputFile);
                        }catch(IOException io){
                            System.out.println(io);
                        }
                        System.out.println("Stopped recording");
                    }
                };
                audioRecordThread.start();
               // JOptionPane.showMessageDialog(null,"Hit ok to stop recording");
               // targetLine.stop();
               // targetLine.close();
            }catch(LineUnavailableException ex){
                System.out.println(ex);
            }
        }else if((e.getSource() == buttonRecord) && startRecord){
            buttonRecord.setForeground(Color.black);
            startRecord = false;
            targetLine.stop();
            targetLine.close();


        }else if((e.getSource() == buttonPlay) && !startPlay){
            buttonPlay.setForeground(Color.blue);
            startPlay = true;
            try {
                Scanner scanner = new Scanner(System.in);

                File file = new File("Adrijana.wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            }catch (IOException | UnsupportedAudioFileException | LineUnavailableException eIO){
                System.out.println(eIO);
            }
        }else if((e.getSource() == buttonPlay) && startPlay){
            buttonPlay.setForeground(Color.black);
            startPlay = false;
            clip.close();
        }
    }
}
