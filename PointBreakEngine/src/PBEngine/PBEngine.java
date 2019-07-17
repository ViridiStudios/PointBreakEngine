
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PBEngine;

import java.awt.Color;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Elias
 */
public class PBEngine {

    /**
     * @param args the command line arguments
     */
    static LinkedList<Object> list;
    static public kick k;
    public Engine main;
    Editor editor;
    boolean running;
    public static void main(String[] args) {
        // TODO code application logic here
        boolean demo = true;
        
        if (args.length > 0) {
            System.out.println("Arguments: ");
            System.out.println("////");
            for (String arg : args) {
                System.out.println("    " + arg);
                if(arg.matches("nodemo")){
                    demo = false;
                }
            }
            System.out.println("////");
        } else {
            System.out.println("Starting without arguments");
        }
        if(demo){
            System.out.println("If you wish not to use the demo, please add the 'nodemo' argument");
            quickEffects.alert("demo", "If you wish not to use the demo, please add the 'nodemo' argument");
        //if(args.length != 0){if(args[0].equals("template")){
            k = new kick(3);
        }else{
            k = new kick(0);
        }
//        else{
//            k = new kick(0);
    }
    public void start(){
        running = true;
        main = k.wM;
        editor = k.ea;
        float c = 0F;
        k = new kick(0);
        while(!main.ready){
            System.out.println("initializing main... " + c);
            c++;
        }
        try {
            main.loadLevel("out.txt");
        } catch (URISyntaxException ex) {
            Logger.getLogger(PBEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        main.oM.addObject(new Player(5, 5, 2, "player1", "█", 1F, Color.black, 1, main.k));
    }
    public void stop(){
        k.stop();
    }
    public void continu(){
        k.continu();
    }
}
