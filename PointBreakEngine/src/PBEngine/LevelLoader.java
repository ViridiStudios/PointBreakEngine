/*
 * The MIT License
 *
 * Copyright 2019 Elias Eskelinen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package PBEngine;

import PBEngine.gameObjects.objectManager;
import PBEngine.gameObjects.gameObject;
import JFUtils.Range;
import JFUtils.point.Point2D;
import JFUtils.point.Point2Int;
import JFUtils.quickTools;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import static java.lang.Math.round;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonnelafin
 */
public class LevelLoader {
    String name = "";
    
    public boolean done = true;
    private directory dir= new directory();
    private Supervisor master;
    private int dotC = 0;
    private boolean inSentence = false;
    public int count = 0;
    int x = 0;
    int y = 0;
    int mass = 1;
    String tag;
    String appereance;
    Color c;
    int id;
    String filePath = dir.levels;
    
    List<String> levels;
    public LevelLoader(){
        
    }
    public void editorLoadLevel(String file, objectManager oM, Supervisor master, String filepath1) throws URISyntaxException{
        LoadLevel(file, oM, master, filepath1, true);
    }
    
    public void LoadLevel(String file, objectManager oM, Supervisor master) throws URISyntaxException{
        FileLoaderConst(file, oM, master, filePath, false);
    }
    public void LoadLevel(String file, objectManager oM, Supervisor master, String filepath1) throws URISyntaxException{
        FileLoaderConst(file, oM, master, filepath1, false);
    }
    public void LoadLevel(String file, objectManager oM, Supervisor master, String filepath1, boolean lightsAsObjects) throws URISyntaxException{
        FileLoaderConst(file, oM, master, filepath1, lightsAsObjects);
    }
    
    
    
    public LevelLoader(String file, objectManager oM, Supervisor master) throws URISyntaxException{
        LoadLevel(file, oM, master, filePath);
        
    }
    public LevelLoader(String file, objectManager oM, Supervisor master, String filepath1) throws URISyntaxException{
        LoadLevel(file, oM, master, filepath1);
        
    }
    
    /**
     * Replace this to make new objects appear in dfferent locations, by defaut this just returns the input
     */
    public Function<Double, Double> coordinateCalculateFuntionX = (t) -> t;
    
    /**
     * Replace this to make new objects appear in dfferent locations, by defaut this just returns the input
     */
    public Function<Double, Double> coordinateCalculateFuntionY = (t) -> t;
    /**
     * you can replace this function to determine custom locations for loaded objects.
     * (this funtion is run in the fetch funtion of the LevelLoader.)
     * @param in the raw object coordinates loaded from the file
     * @return the processed final coordinates
     */
    
    
    public Point2D customCoordinateFormula(Point2Int in){
        double xn = coordinateCalculateFuntionX.apply((double)in.x);
        double yn = coordinateCalculateFuntionX.apply((double)in.x);
        return new Point2D(xn, yn);
    }
    private void FileLoaderConst(String file, objectManager oM, Supervisor master, String filepath1, boolean lightsASObjects) throws URISyntaxException{
        this.done = false;
        levels = getLevels(filePath);
        this.master = master;
        if(file == "null" || file == null){
            fetch("", oM, master.Logic.rads, file);
            System.out.println("Level ["+filePath.concat(file)+"] loaded with " + count + " objects!");
            return;
        }
        
        String arr[] = file.split(" ", 2);
        
        
        String text = file;
        
        try {
            if(!arr[0].equals("!random")){
                Scanner in = new Scanner(new FileReader(filepath1 + file));
                text = "";
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    text = text + line;
                }
                in.close();
            }
        }
        catch (FileNotFoundException ex) {
                try{
                    fetch(fallback, oM, master.Logic.rads, "preloaded fallback level");
                    System.out.println("!!! level " + filePath + file + " FAILED TO LOAD!!!");
                    quickTools.alert("Level not found!", "!!! level " + filePath + file + " FAILED TO LOAD!!!");
                    System.out.println("fallback level loaded with " + count + " objects!");
    //            fetch(in.toString(), oM);
                }
                catch(Exception o){
                    Logger.getLogger(LevelLoader.class.getName()).log(Level.SEVERE, null, o);
                    quickTools.alert("LevelLoading", o.getMessage());
                }
            }
        if(arr[0].equals("!random")){
            text = file;
        }
        fetch(text, oM, master.Logic.rads, lightsASObjects, file);
        System.out.println("Level ["+filePath.concat(file)+"] loaded with " + count + " objects!");
//            fetch(in.toString(), oM);
        
    }
    public LinkedList<gameObject> level = new LinkedList<>();
    
    public void fetch(String i, objectManager oM, VSRadManager rads, String filename){
        fetch(i, oM, rads, false, filename);
    }
    
    public void fetch(String i, objectManager oM, VSRadManager rads, boolean loadLightsAsObjects, String filename){
        LinkedList<gameObject> newObjects = new LinkedList<>();
        boolean meta = false;
        int metachar = 0;
        char version = 0;
        String name = "";
        String tmp = "";
        String arr[] = i.split(" ", 2);
        dotC = 0;
        int xd = rads.masterParent.xd;
        int yd = rads.masterParent.yd;
        if(arr[0].equals("!random")){
            arr[1] = arr[1].replaceAll(".pblevel", "");
            Random rnd = new Random(Integer.parseInt(arr[1]));
            rnd.setSeed(Integer.parseInt(arr[1]));
            for(int y : new Range(Integer.parseInt(arr[1]))){
                int xp = (int) (Math.random() * ( xd - 0 ));
                int yp = (int) (Math.random() * ( yd - 0 ));
                
                //xd = rnd.nextInt(xd);
                //yd = rnd.nextInt(yd);
                
                gameObject tm = new gameObject(xp, yp, 1, "static", this.appereance, this.mass, Color.black, this.id, master);
                tm.imageName = dir.textures + "walls/walls0.png";
                newObjects.add(tm);
                id++;
                count++;
            }
            return;
        }
        int charNum = 0;
        for(char x : i.toCharArray()){
            if(meta){
                switch(metachar){
                    case 0:
                        version = x;metachar++;
                        break;
                    case 13:
                        meta = false;
                        System.out.println("Loading level that was created with version " + version +", level: " + name);
                        break;
                    default:
                        if(x != '*'){
                            name = name + x;
                        }metachar++;
                    
                }
            }
            switch (x) {
                case '#':
                    meta = true;
                    break;
                case ':':
                    //this.c
                    Point2D pz = customCoordinateFormula(new Point2Int(this.x, this.y));
                    if(tag.equals("light") && !loadLightsAsObjects){rads.add((int)pz.x, (int)pz.y, mass, c, 1, false);}
                    else if (tag.equals("light")){
                        gameObject tml = new gameObject((int)pz.x, (int)pz.y, 1, this.tag, this.appereance, this.mass, Color.black, this.id, master);
                        tml.setLocation(pz);
                        newObjects.add(tml);
                    }
                    if(tag.equals("static")){gameObject tm = new gameObject(this.x, this.y, 1, this.tag, this.appereance, this.mass, Color.black, this.id, master);
                    tm.imageName = dir.textures + "walls/walls0.png";
                    newObjects.add(tm);}
                    count++;
                    //System.out.println(tm.getTag());
                    dotC = 0;
                    tmp = "";
                    //System.out.println(this.tag);
                    break;
                case '.':
                    //                System.out.println(tmp);
                    //System.out.println("DOTCOUNT [" + dotC + "], value [" + tmp + "]");
                    if(dotC == 0){
                        this.x = toInt(tmp);
                    }   if(dotC == 1){
                        this.y = toInt(tmp);
                    }   if(dotC == 2){
                        this.tag = tmp;
                    }   if(dotC == 3){
                        this.appereance = tmp;
                    }   if(dotC == 4){
                        this.mass = toInt(tmp);
                    }   if(dotC == 5){
                            try {
                                this.c = getColorByName(tmp);
                            } catch (Exception e) {
                                String err = "Error parsing color [" + tmp + "] (character " + charNum + " in the file " + filename + "): " + e;
                                System.out.println(err);
                                quickTools.alert(err);
                                
                            }
                    }   if(dotC == 6){
                        this.id = toInt(tmp);
                    }   dotC++;
                    tmp = "";
                    break;
                default:
                    tmp = tmp + x;
                    break;
                
            }
            charNum++;
            
        }
        this.level = newObjects;
        this.done = true;
        //try{rads.recalculate();}catch(Exception e){quickTools.alert("FAILED TO RECALCULATE VSRAD, nullpointerexception?", e.getMessage());throw e;}
    }
    public void write(LinkedList<gameObject> g, String file) throws FileNotFoundException, UnsupportedEncodingException, IOException{

        System.out.println("Saving level to: "+dir.levels + file + "...");
        System.out.println("");

        String tmp = "";
        int idi = 90;
        for(gameObject p : g){
            if(p.getTag().contains("static")){
                tmp = tmp + round(p.x) + "." + round(p.y) + ".static.█.1.green." + idi + ".:";
                System.out.print(".");
                idi++;
            }
            else if(p.getTag().contains("light")){
                tmp = tmp + round(p.x) + "." + round(p.y) + ".light.█."+(int)(p.mass)+".green." + idi + ".:";
                System.out.print(".");
                idi++;
            }
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir.levels + file), "utf-8"))) {
            writer.write(tmp);
        }
    }
    public void write(LinkedList<gameObject> g, String file, String filepath1) throws FileNotFoundException, UnsupportedEncodingException, IOException{

        System.out.println("Saving level to: "+filepath1 + file + "...");
        System.out.println("");

        String tmp = "";
        int idi = 90;
        for(gameObject p : g){
            if(p.getTag().contains("light")){
                tmp = tmp + round(p.x) + "." + round(p.y) + ".light.█."+(int)(p.mass)+".green." + idi + ".:";
                System.out.print("*");
                idi++;
            }
            else if(p.getTag().contains("static")){
                tmp = tmp + round(p.x) + "." + round(p.y) + ".static.█.1.green." + idi + ".:";
                System.out.print(".");
                idi++;
            }
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath1 + file), "utf-8"))) {
            writer.write(tmp);
        }
    }
    public void writeObject(Object o, String file) throws FileNotFoundException, IOException{
        writeObject(o, file, dir.levels);
    }
    public void writeObject(Object o, String file, String path) throws FileNotFoundException, IOException{
        System.out.println("Writing file [" + path + file + "]...");
      ObjectOutputStream objOut = new ObjectOutputStream(new
              ///"out_lights.txt"
      FileOutputStream(path + file));
      objOut.writeObject(o);
      objOut.close();
      System.out.print("OK");
    }
    public Object readObject(String file) throws IOException, ClassNotFoundException{
        Object out;
        FileInputStream fileIn =new FileInputStream(dir.levels + file);
        ObjectInputStream in = new ObjectInputStream(fileIn);
            out = in.readObject();
            in.close();
            fileIn.close();
        return out;
    }
    public int toInt(String som){
        String result = "";
        for (int i = 0; i < som.length(); i++) {
            Character character = som.charAt(i);
            if (Character.isDigit(character)) {
                result = result + character;
            }
        }
        int out = 0;
        try {
            out = Integer.parseInt(result);
        } catch (NumberFormatException numberFormatException) {
            System.out.println("ERROR WITH toInt: "+numberFormatException);
        }
        return out;
    }
    public static Color getColorByName(String name) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        try {
            return (Color)Color.class.getField(name.toUpperCase()).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            //quickTools.alert("Failed to parse color: " + name);
            throw e;
        }
    }
    static String readFile(String path, Charset encoding) throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    public static LinkedList<String> readConfig(String FileName){
        LinkedList<String> out = new LinkedList<>();
        try {
            
            String outa = readFile(new directory().root + FileName, Charset.defaultCharset());
            String current = "";
            for(int x : new Range(outa.length())){
                char i = outa.charAt(x);
                if(i == '\n'){
                    out.add(current);
                    current = "";
                }
                else{
                    current = current + i;
                }
            }
            
        } catch (IOException iOException) {
        }return out;
    }
    public static List<String> getLevels(String dir){
        List<String> results = new ArrayList<String>();


        File[] files = new File(dir).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null. 
        
        System.out.println("Probing directory "+dir+" for levels:");
        for (File file : files) {
            if (file.isFile()) {
                try {
                    if (file.getName().endsWith(".pblevel")) {
                        System.out.println("    "+file.getName());
                    }
                } catch (Exception e) {
                }
                results.add(file.getName());
            }
        }
        System.out.println("Probe complete.");
        return results;
    }
    public static String getLevelMap(String name){
        try {
            return readFile(new directory().levels + name, Charset.defaultCharset());
        } catch (IOException ex) {
            System.out.println("Unable to load levelmap!");
            return "";
        }
    }
    
    String empty = "";
    String fallback = "21.24.static.█.1.green.90.:21.23.static.█.1.green.91.:21.22.static.█.1.green.92.:21.21.static.█.1.green.93.:21.20.static.█.1.green.94.:21.19.static.█.1.green.95.:22.19.static.█.1.green.96.:23.19.static.█.1.green.97.:24.19.static.█.1.green.98.:25.19.static.█.1.green.99.:26.19.static.█.1.green.100.:27.19.static.█.1.green.101.:28.19.static.█.1.green.102.:29.19.static.█.1.green.103.:30.19.static.█.1.green.104.:30.20.static.█.1.green.105.:30.21.static.█.1.green.106.:30.22.static.█.1.green.107.:30.23.static.█.1.green.108.:30.24.static.█.1.green.109.:49.19.static.█.1.green.110.:48.19.static.█.1.green.111.:47.19.static.█.1.green.112.:46.19.static.█.1.green.113.:45.19.static.█.1.green.114.:44.19.static.█.1.green.115.:43.19.static.█.1.green.116.:43.18.static.█.1.green.117.:43.17.static.█.1.green.118.:43.16.static.█.1.green.119.:43.15.static.█.1.green.120.:43.14.static.█.1.green.121.:44.14.static.█.1.green.122.:45.14.static.█.1.green.123.:46.14.static.█.1.green.124.:47.14.static.█.1.green.125.:48.14.static.█.1.green.126.:49.14.static.█.1.green.127.:14.0.static.█.1.green.128.:13.1.static.█.1.green.129.:12.2.static.█.1.green.130.:11.3.static.█.1.green.131.:11.20.static.█.1.green.132.:11.19.static.█.1.green.133.:10.18.static.█.1.green.134.:9.17.static.█.1.green.135.:8.16.static.█.1.green.136.:8.15.static.█.1.green.137.:33.11.static.█.1.green.138.:32.11.static.█.1.green.139.:31.11.static.█.1.green.140.:30.11.static.█.1.green.141.:29.11.static.█.1.green.142.:28.11.static.█.1.green.143.:49.4.static.█.1.green.144.:48.4.static.█.1.green.145.:47.4.static.█.1.green.146.:46.4.static.█.1.green.147.:45.4.static.█.1.green.148.:44.4.static.█.1.green.149.:44.3.static.█.1.green.150.:45.3.static.█.1.green.151.:46.3.static.█.1.green.152.:47.3.static.█.1.green.153.:48.3.static.█.1.green.154.:49.3.static.█.1.green.155.:49.5.static.█.1.green.156.:48.5.static.█.1.green.157.:47.5.static.█.1.green.158.:46.5.static.█.1.green.159.:45.5.static.█.1.green.160.:44.5.static.█.1.green.161.:43.5.static.█.1.green.162.:43.4.static.█.1.green.163.:43.3.static.█.1.green.164.:";
}
