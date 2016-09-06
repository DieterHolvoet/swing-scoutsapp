/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Dieter
 */
public class HelperFunctions {
    
    // Source: http://stackoverflow.com/a/17806704
    static public String getComponentVariableName(Object object) {

        if (object instanceof Component) {
            final Component component = (Component) object;
            final StringBuilder sb = new StringBuilder();

            // find the form where the variable name would be likely to exist
            // final Component parentForm = getParentForm(component);
            //      => didn't work with jDialogs, so using SwingUtilities instead
            final Component parentForm = SwingUtilities.getRoot(component);

            // loop through all of the class fields on that form
            for (Field field : parentForm.getClass().getDeclaredFields()) {

                try {
                    // let us look at private fields, please
                    field.setAccessible(true);

                    // get a potential match
                    final Object potentialMatch = field.get(parentForm);

                    // compare it
                    if (potentialMatch == component) {

                        // return the name of the variable used
                        // to hold this component
                        if (sb.length() > 0) sb.append(",");
                        sb.append(field.getName());
                    }

                } catch (SecurityException | IllegalArgumentException 
                        | IllegalAccessException ex) {
                    ex.printStackTrace();
                    // ignore exceptions
                }
            }

            if (sb.length() > 0) {
                return sb.toString();
            }
        }

        // if we get here, we're probably trying to find the form
        // itself, in which case it may be more useful to print
        // the class name (MyJFrame) than the AWT-assigned name
        // of the form (frame0)
        final String className = object.getClass().getName();
        final String[] split = className.split("\\.");
        final int lastIndex = split.length - 1;
        return (lastIndex >= 0) ? split[lastIndex] : className;

    }

    /**
     * traverses up the component tree to find the top, which i assume is the
     * dialog or frame upon which this component lives.
     * @param sourceComponent
     * @return top level parent component
     */
    static public Component getParentForm(Component sourceComponent) {
        while (sourceComponent.getParent() != null) {
            sourceComponent = sourceComponent.getParent();
        }
        return sourceComponent;
    }
    
    // Source: http://stackoverflow.com/a/6495800
    public static ArrayList<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        ArrayList<Component> compList = new ArrayList<>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container)
                compList.addAll(getAllComponents((Container) comp));
        }
        return compList;
    }
    
    public static boolean isFrameOpen(String name) {
        for (Frame frame : Frame.getFrames()) {
            if (frame.getName().equals(name) && frame.isDisplayable()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isDialogOpen(Component owner, String name) {
        Frame frame = (Frame) owner;
        for(Window window : frame.getOwnedWindows()) {
            if(window instanceof JDialog) {
                JDialog dialog = (JDialog) window;
                if(dialog.getName().equals(name) && dialog.isDisplayable()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Source: http://stackoverflow.com/a/34086741/2637528
    public static void scrollToMax(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        AdjustmentListener scroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(verticalBar.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(scroller);
    }
}
