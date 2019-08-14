/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PBEngine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author elias
 */
public class devkit extends JFrame{
    JPanel cont = new JPanel();
    boolean tog = false;
    kick k;
    JButton graphic = new JButton("toggle vector");
    JButton rays = new JButton("toggle rays");
    JLabel time = new JLabel("RAYYYS");
    JTextArea log = new JTextArea("PointBreakEngine devkit");
    JTextField lum = new JTextField(20);
    JScrollPane logs = new JScrollPane(log);
    public devkit(kick k) {
        this.setTitle("PointBreakEngine devkit");
        this.k = k;
        this.setSize(400, 550);
        this.setLocationRelativeTo(k.wM);
        this.setLocation(1080, 0);
        
        graphic.addActionListener(new BListener(9, this));
        rays.addActionListener(new BListener(2, this));
        lum.addActionListener(new BListener(0, this));
        logs.setWheelScrollingEnabled(true);
        //log.setColumns(1);
        log.setRows(15);
        logs.setWheelScrollingEnabled(true);
        cont.add(graphic, BorderLayout.NORTH);
        cont.add(rays, BorderLayout.NORTH);
        cont.add(time, BorderLayout.SOUTH);
        cont.add(logs, BorderLayout.NORTH);
        cont.add(lum, BorderLayout.NORTH);
        this.add(cont);
        this.setVisible(true);
    }
    public void togG(){
        if(tog){
            k.wM.vector = 1;
        }
        else{
            k.wM.vector = 0;
        }
        tog = !tog;
    }
    boolean togV = true;
    public void togV(){
        if(togV){
            k.wM.renderRays = 0;
        }
        else{
            k.wM.renderRays = 1;
        }
        togV = !togV;
    }
}
class BListener implements ActionListener{
    boolean abright = false;
    int type;
    devkit k;
    public BListener(int t, devkit d){
        this.type = t;
        this.k = d;
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(type == 9){
            System.out.println("Graphics!");
            k.togG();
        }
        if(type == 2){
            System.out.println("Rays!");
            k.togV();
        }
        try{
            String arr[] = k.lum.getText().split(" ", 2);
            
            if(k.lum.getText().charAt(0) == '/'){
                switch(arr[0]){
                    case "/collisions":
                        if(arr[1].matches("true")){k.k.engine_collisions = true;}
                        else if(arr[1].matches("false")){k.k.engine_collisions = false;}
                        else{
                            quickEffects.alert("devkit", "value :"+ arr[1] +": not understood");
                        }
                        break;
                    case "/blur":
                        k.k.wM.blurStrenght = Integer.parseInt(arr[1]);
                        break;
                    case "/bright":
                        k.k.wM.global_brightness = Float.parseFloat(arr[1]);
                        break;
                    case "/tp":
                        try {
                            String values[] = arr[1].split(" ", 2);
                            int x = Integer.parseInt(values[1].split(" ", 2)[0]);
                            int y = Integer.parseInt(values[1].split(" ", 2)[1]);
                            for(gameObject o : k.k.forwM.getObjectsByTag(values[0])){
                                o.setLocation(new Vector(x, y));
                            }
                            //k.k.forwM.getObjectByTag(values[0]).setLocation(new Vector(x, y));
                        } catch (NumberFormatException numberFormatException) {
                            quickEffects.alert("devkit", "value :"+ arr[1] +": not understood");
                            
                        }
                        break;
                    case "/noclip":
                        k.k.wM.oM.getObjectByTag("player1").noclip();
                        break;
                    case "/abright":
                        abright = !abright;
                        k.k.wM.abright = abright;
                        System.out.println(abright + ", " + k.k.wM.abright);
                        break;
                    case "/add":
                        try {
                            String values[] = arr[1].split(" ", 2);
                            int x = Integer.parseInt(values[1].split(" ", 2)[0]);
                            int y = Integer.parseInt(values[1].split(" ", 2)[1]);
                            String tag = values[0];
                            k.k.forwM.addObject(new gameObject(x, y, 1, tag, "N", 1, Color.black, 1919, k.k));
                            //k.k.forwM.getObjectByTag(values[0]).setLocation(new Vector(x, y));
                        } catch (NumberFormatException numberFormatException) {
                            quickEffects.alert("devkit", "value :"+ arr[1] +": not understood");
                            
                        }
                        break;
                    case "/rm":
                        String values[] = arr[1].split(" ", 2);
                        for(gameObject o : k.k.forwM.getObjectsByTag(values[0])){
                                k.k.forwM.objects.remove(o);
                            }
                        break;
                    default:
                        quickEffects.alert("devkit", "command not understood");
                        break;
                }
            }
        }
        catch(Exception e){}
        k.time.setText(Boolean.toString(k.togV));
    }
    
}