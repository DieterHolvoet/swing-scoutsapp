/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.swingrenderer;

import com.dieterholvoet.scoutsapp.model.LeidingTak;
import com.dieterholvoet.scoutsapp.util.Styles;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Dieter
 */
public class LeidingTakListRenderer extends JLabel implements ListCellRenderer {
        
    public LeidingTakListRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        
      LeidingTak l = (LeidingTak) value;
      setText(l.toString());
      setFont(Styles.OpenSans.getLIGHT(Styles.P));
      // setBorder(new EmptyBorder(3, 10, 3, 0));

      if (isSelected) {
          setBackground(Styles.BEIGE);
          setForeground(Color.black);

      } else {
          setBackground(Color.white);
          setForeground(Color.black);
      }

      return this;
    }
}
