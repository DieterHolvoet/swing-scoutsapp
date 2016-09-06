/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.util;

import com.dieterholvoet.scoutsapp.gui.WerkstukGUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Dieter
 */
public class Styles {    
    static public final Color BEIGE = new Color(231, 224, 222);
    static public final Color BIJNAZWART = new Color(72, 69, 75);
    static public final Color DONKERGROEN = new Color(116, 141, 38);
    static public final Color BRUIN = new Color(87, 38, 0);
    static public final Color LICHTGROEN = new Color(189, 215, 50);
    static public final Color BLAUW = new Color(37, 111, 145);
    static public final Color ROOD = new Color(237, 28, 35);
    static public final Color ORANJE = new Color(245, 130, 31);
    static public final Color SPLASHGROEN = new Color(189, 208, 55);
    static public final Color SPLASHBRUIN = new Color(95, 43, 13);
    
    static public final float H1 = 40;
    static public final float H2 = 25;
    static public final float SPLASH = 23;
    static public final float MENUH1 = 19;
    static public final float LABEL = 17;
    static public final float MENUH2 = 15;
    static public final float P = 14;
    
    static ImageIcon PROFILEPIC;
    public static ImageIcon getPROFILEPIC() {
        if(PROFILEPIC == null) {
            PROFILEPIC = loadImage("ProfilePicture.jpg");
        }
        return PROFILEPIC;
    }
    
    public static class icons {
        private static ImageIcon ACTIVITY_16;
        private static ImageIcon ACTIVITY_64;
        private static ImageIcon ADD_24;
        private static ImageIcon ADD_2_24;
        private static ImageIcon BOX_16;
        private static ImageIcon BOX_64;
        private static ImageIcon CALENDAR_16;
        private static ImageIcon CALENDAR_64;
        private static ImageIcon CHECK_16;
        private static ImageIcon CHECK_64;
        private static ImageIcon MEETING_16;
        private static ImageIcon MEETING_64;
        private static ImageIcon MONEY_16;
        private static ImageIcon MONEY_64;
        private static ImageIcon NOTEPAD_16;
        private static ImageIcon NOTEPAD_64;
        private static ImageIcon PEOPLE_16;
        private static ImageIcon PEOPLE_64;
        private static ImageIcon RELOAD_24;
        private static ImageIcon PLUS_16;
        private static ImageIcon UPDATE_16;
        private static ImageIcon WRENCH_16;
        private static ImageIcon WRENCH_64;
        
        // Sources:
        // http://stackoverflow.com/a/3239068
        // http://stackoverflow.com/a/4770460
        
        public static ImageIcon getIcon(String name, int size) {
            ImageIcon image = null;
            try {
                
                Field f = Styles.icons.class.getDeclaredField(name.toUpperCase() + "_" + size);
                f.setAccessible(true);
                ImageIcon value = (ImageIcon) f.get(null);
                
                if(value == null) {
                    Method m = Styles.class.getDeclaredMethod("loadImage", String.class);
                    m.setAccessible(true);
                    
                    image = (ImageIcon)m.invoke(null, name.toLowerCase() + "_" + size + ".png");
                    f.set(null, image);
                } else {
                    image = value;
                }
                
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                Logger.getLogger(Styles.class.getName()).log(Level.SEVERE, null, ex);
            }
            return image;
        }     
    }
    
    public static class DCCAsh {
        private static Font REGULAR;

        public static Font getREGULAR(float size) {
            if(REGULAR == null) {
                REGULAR = loadFont("DCCAsh-Regular.ttf", size);
            } else {
                REGULAR = REGULAR.deriveFont(size);
            }
            return REGULAR;
        }
    }
    
    public static class FjallaOne {
        private static Font REGULAR;
        public static Font getREGULAR(float size) {
            if(REGULAR == null) {
                REGULAR = loadFont("FjallaOne-Regular.ttf", size);
            } else {
                REGULAR = REGULAR.deriveFont(size);
            }
            return REGULAR;
        }
    }

    public static class OpenSans {
        private static Font REGULAR;
        private static Font LIGHT;
        private static Font SEMIBOLD;
        private static Font BOLD;
        
        public static Font getREGULAR(float size) {
            if(REGULAR == null) {
                REGULAR = loadFont("OpenSans-Regular.ttf", size);
            } else {
                REGULAR = REGULAR.deriveFont(size);
            }
            return REGULAR;
        }
        
        public static Font getLIGHT(float size) {
            if(LIGHT == null) {
                LIGHT = loadFont("OpenSans-Light.ttf", size);
            } else {
                LIGHT = LIGHT.deriveFont(size);
            }
            return LIGHT;
        }
        
        public static Font getSEMIBOLD(float size) {
            if(SEMIBOLD == null) {
                SEMIBOLD = loadFont("OpenSans-Semibold.ttf", size);
            } else {
                SEMIBOLD = SEMIBOLD.deriveFont(size);
            }
            return SEMIBOLD;
        }
        
        public static Font getBOLD(float size) {
            if(BOLD == null) {
                BOLD = loadFont("OpenSans-Bold.ttf", size);
            } else {
                BOLD = BOLD.deriveFont(size);
            }
            return BOLD;
        }
    }
    
    private static Font loadFont(String path, float fontSize) {
        InputStream myStream = null;
        Font font = null;
        
        try {
            myStream = Styles.class.getResourceAsStream("/resources/fonts/" + path);
            font = Font.createFont(Font.TRUETYPE_FONT, myStream);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Styles.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(Styles.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally {
            try {
                myStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Styles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return font.deriveFont(fontSize);
    }
    
    private static ImageIcon loadImage(String path) {
        InputStream myStream = null;
        BufferedImage imBuff = null;
        ImageIcon image = null;
        
        try {
            myStream = Styles.class.getResourceAsStream("/resources/images/" + path);
            imBuff = ImageIO.read(myStream);
            image = new ImageIcon(imBuff);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Styles.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (IOException ex) {
            Logger.getLogger(WerkstukGUI.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally {
            try {
                myStream.close();
                
            } catch (IOException ex) {
                Logger.getLogger(Styles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return image;
    }
}