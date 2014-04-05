package org.kieda.apps.timerapp;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.JFrame;
import java.awt.event.*;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * @author kieda
 */
public class Main {
    private static Rectangle center(int w, int h, Dimension siz) {
        return new Rectangle(siz.width/2 - w/2, siz.height/2 - h/2, w, h);
    }
    public static final int WIDTH = 300;
    public static final int HEIGHT = 120;
    private static boolean printed = false;
    private static boolean start = false;
    private static void printMsg(){
        if(!printed){
            System.out.println("usage : [-t|--task 'task description] [--time|-T 'hours?,minutes?,seconds?'] [--start|-s]\n"
                        +  "        You may leave out hours, minutes, or seconds. For example, without\n"
                        +  "        hours and seconds the query will look like\n"
                        +  "            -T ,15,\n"
                        +  "        to set a 15 minute clock. By default, hours, minutes, and seconds are 0 when\n"
                        +  "        the --time or -T flag is specified. When it is not, the hours is 0, minutes \n"
                        +  "        15, and seconds 0.\n"
                        +  "        If the -s or --start flag is set, we start the clock immediately."
            );
            printed = true;
        }
        
    }
    public static void main(String[] args) {
        boolean hasTask = false;
        String task = null;
        
        String time = ",15,";
        int hrs, mins, secs;
        
        for(int i = 0; i < args.length; i++){
            switch(args[i]){
                case "--task":
                case "-t":
                    if(i==args.length-1) {printMsg(); return;}
                    task = args[++i];
                    hasTask = true;
                    break;
                case "--time":
                case "-T":
                    if(i==args.length-1) {printMsg(); return;}
                    time = args[++i];
                    break;
                case "-s":
                case "--start":
                    start = true;
            }
        }
        try{
           String[] times = time.split(",");

           hrs = (times[0].length()==0)?0:Integer.parseInt(times[0]);
           mins = (times[1].length()==0)?0:Integer.parseInt(times[1]);
           
           //if the user entered a string ending with a comma, String#split 
           //removes it
           secs = ((times.length<3 && time.endsWith(","))||times[2].length()==0)
                   ?0:Integer.parseInt(times[2]);

           if((hrs|mins|secs)<0){
              printMsg();
              System.out.println("Invalid request : negative time : "+time);
              return;
           }
        } catch(NumberFormatException|ArrayIndexOutOfBoundsException e){
            printMsg();
            return;
        }

        if(task==null){
            task = "New "+((hrs>0)?hrs+" hr ":"")
                         +((mins>0)?mins+" min ":"")
                         +((secs>0)?secs+" sec ":"")
                        // 'New task.' sounds better than 'New instant task.'
                        // +((hrs|mins|secs)==0?"instant ":"")
                         +"task.";
        }
        
        TimerApp jp = new TimerApp(hasTask?task:null,hrs, mins, secs);
        final JFrame jf = new JFrame(task);
        jf.add(jp);
        jp.getInputMap().put(KeyStroke.getKeyStroke("F2"),
                            "alwaysOnTop");
        jp.getActionMap().put("alwaysOnTop",
                             new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.setAlwaysOnTop(!jf.isAlwaysOnTop());
            }
        });
        
        //set bounds
        jf.pack();
        jf.setBounds(center(jf.getWidth(), jf.getHeight(), Toolkit.getDefaultToolkit().getScreenSize()));
        
        //usually on top
        jf.setAlwaysOnTop(true);
        
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        
        //start immediately
        if(start)
            jp.start();
    }
}
