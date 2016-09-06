/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.gui;

import com.dieterholvoet.scoutsapp.util.Styles;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.awt.geom.Rectangle2D;

/**
 * @author Dieter
 * Sources:
        http://wiki.netbeans.org/Splash_Screen_Beginner_Tutorial
        https://docs.oracle.com/javase/tutorial/2d/text/renderinghints.html
 */

public class Splash {
    private static SplashScreen splash;
    private static Graphics2D splashGraphics;
    private static Rectangle2D.Double splashTextArea;
    private static Rectangle2D.Double splashProgressArea;
    private static Font font;
    
    private static String text;
    private static float progress;
    
    public static void init() {
        splash = SplashScreen.getSplashScreen();
        
        if (isNotNull()) {
            Dimension ssDim = splash.getSize();
            int height = ssDim.height;
            int width = ssDim.width;
            double margin = 40.;
            
            // stake out some area for our status information
            splashTextArea = new Rectangle2D.Double(0, height * 0.80, width, 32.);
            splashProgressArea = new Rectangle2D.Double(margin, height * .87, width - (2 * margin), 17);

            // create the Graphics environment for drawing status info
            splashGraphics = splash.createGraphics();
            font = Styles.OpenSans.getBOLD(Styles.SPLASH);
            splashGraphics.setFont(font);
            
            // text ant-aliasing
            splashGraphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // initialize the status info
            setText("Starting");
            setProgress(0);
        }
    }

    public static String getText() {
        return text;
    }

    public static void setText(String text) {
        Splash.text = text;
        if (isNotNull() && splash.isVisible()) {
            
            int x = (int) splashTextArea.getX(),
                y = (int) splashTextArea.getY() + 20,
                textLength = (int) splashGraphics.getFontMetrics().getStringBounds(text, splashGraphics).getWidth(),
                textStart = splash.getSize().width / 2 - textLength / 2;

            // erase the last status text
            splashGraphics.setPaint(Styles.SPLASHGROEN);
            splashGraphics.fill(splashTextArea);

            // draw the text
            splashGraphics.setPaint(Styles.SPLASHBRUIN);
            splashGraphics.drawString(text, textStart + x, y);

            // make sure it's displayed
            splash.update();
        }
    }

    public static float getProgress() {
        return progress;
    }

    public static void setProgress(float pct) {
        Splash.progress = pct;
        if (isNotNull() && splash.isVisible()) {

            // Note: 3 colors are used here to demonstrate steps
            // erase the old one
            splashGraphics.setPaint(Styles.SPLASHGROEN);
            splashGraphics.fill(splashProgressArea);

            // draw an outline
            splashGraphics.setPaint(Styles.SPLASHBRUIN);
            splashGraphics.draw(splashProgressArea);

            // Calculate the width corresponding to the correct percentage
            int x = (int) splashProgressArea.getMinX();
            int y = (int) splashProgressArea.getMinY();
            int wid = (int) splashProgressArea.getWidth();
            int hgt = (int) splashProgressArea.getHeight();

            int doneWidth = Math.round(pct * wid/100.f);
            doneWidth = Math.max(0, Math.min(doneWidth, wid-1));  // limit 0-width

            // fill the done part one pixel smaller than the outline
            splashGraphics.setPaint(Styles.SPLASHBRUIN);
            splashGraphics.fillRect(x, y+1, doneWidth, hgt-1);

            // make sure it's displayed
            splash.update();
        }
    }
    
    public static void updateProgress(Integer[] progress) {
        // Old total + (Percent * Total relative value)
        float pct = (float) progress[0] / (float) progress[1];
        float total = progress[2];
        Splash.setProgress(Splash.getProgress() + (pct / 100 * total));
    }
    
    public static boolean isNotNull() {
        return splash != null;
    }
    
    public static void close() {
        Splash.splash.close();
    }
}
