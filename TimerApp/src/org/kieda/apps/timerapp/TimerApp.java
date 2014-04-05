package org.kieda.apps.timerapp;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * 
 * @author kieda
 */
public class TimerApp extends JPanel{
    private static final String SEP = ":";
    private JLabel countDownLabel;
    private JLabel taskLabel;
    private JButton startPauseButton;
    private JButton resetButton;
    private JPanel buttonPanel;
    private final Timer countDownTimer;
    private final java.awt.Color runningColor = java.awt.Color.decode("0x5BBD61");
    private final java.awt.Color stoppedColor = java.awt.Color.decode("0x72C2C2");
    private final java.awt.Color resetColor = java.awt.Color.decode("0xBD5B5B");
    private String task = null;
    
    //time left
    private int hours, mins, secs, decs;
    //default
    private final int HOURS_LEFT, MINS_LEFT, SECS_LEFT, DECS_LEFT;

    private boolean running = false;

    private final Object lock = new Object();
    
    private void resetClock(){
        synchronized(lock){
            hours = HOURS_LEFT;
            mins = MINS_LEFT;
            secs = SECS_LEFT;
            decs = DECS_LEFT;
        }
    }
    public void start() {
        startPauseOnStopped();
    }
    
    private void resetOnRunning(){
        resetClock();
        updateElems();
    }
    private void resetOnStopped(){
        resetClock();
        updateElems();
    }

    private void startPauseOnRunning(){
        synchronized(lock){
            running = false;
            countDownTimer.stop();
            startPauseButton.setText("Start");
            startPauseButton.setSelected(false);
            startPauseButton.setBackground(stoppedColor);
        }
    }
    private void startPauseOnStopped(){
        synchronized(lock){
            running = true;
            countDownTimer.start();

            startPauseButton.setText("Pause");
            startPauseButton.setBackground(runningColor);
        }
    }

    private void onTimeOver(){
        startPauseOnRunning();
        
        new Thread(new Runnable(){
            public void run(){
                JOptionPane.showMessageDialog(null, "Time is up!");
            }
        }).start();
    }
    private static String format(int tim) {
       return (tim<10)?"0"+tim:String.valueOf(tim);
    }
    
    private String makeLabel(){
        return format(hours)+SEP+format(mins)+SEP+format(secs) + SEP + decs;
    }
    private void updateElems(){
        countDownLabel.setText(makeLabel());
    }
    public boolean hasTask(){
        return task != null;
    }
    private void initComponents() {
        countDownLabel = new JLabel(makeLabel(), JLabel.CENTER);
        countDownLabel.setFont(countDownLabel.getFont().deriveFont(Font.PLAIN, 40));
        if(hasTask()){
            taskLabel = new JLabel(task, JLabel.CENTER);
            taskLabel.setFont(taskLabel.getFont().deriveFont(Font.BOLD, 50));
        }
        startPauseButton = new JButton("Start");
        resetButton = new JButton("Reset");
        resetButton.setBackground(resetColor);
        startPauseButton.setBackground(stoppedColor);
        buttonPanel = new JPanel();
        buttonPanel.add(startPauseButton, BorderLayout.EAST);
        buttonPanel.add(resetButton, BorderLayout.WEST);
        
        setLayout(new BorderLayout());
        
        resetButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if(running){
                        resetOnRunning();
                    }else{
                        resetOnStopped();
                    }
                }
            });
        startPauseButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if(running){
                        startPauseOnRunning();
                    }else{
                        startPauseOnStopped();
                    }
                }
            });
    }
    
    private void decrementTime(){
        synchronized(lock){
            if(decs==0){
                if(secs==0){
                    if(mins==0){
                        if(hours==0) onTimeOver();
                        else {
                           hours--;
                           mins = 59;
                           secs = 59;
                           decs = 9;
                        }
                    } else {
                        mins--;
                        secs = 59;
                        decs = 9;
                    }
                } else {
                    decs = 9;
                    secs--;
                }
            }else{
                decs--;
            }
        }
    }

    public TimerApp() {
        this(null, 0,15,0);
    }
    public TimerApp(int hrs, int mins, int secs) {
        this(null, hrs, mins, secs);
    }
    public TimerApp(String task, int hrs, int mins, int secs) {
        super(true);
        this.hours = this.HOURS_LEFT = hrs;
        this.mins = this.MINS_LEFT = mins;
        this.secs = this.SECS_LEFT = secs;
        this.decs = this.DECS_LEFT = 0;
        this.task = task;
        
        initComponents();        
        
        add(countDownLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if(hasTask())
            add(taskLabel, BorderLayout.NORTH);
        
        //update every second
        countDownTimer = new Timer(100, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(running)
                    decrementTime();
                updateElems();
            }});
        
    }
}
