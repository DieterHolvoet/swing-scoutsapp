/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.swingrenderer;

import com.dieterholvoet.scoutsapp.util.Styles;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Dieter
 */
public class ImportListRenderer extends JLabel implements ListCellRenderer {
        
    public ImportListRenderer() {
        setOpaque(true);
        setIconTextGap(12);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      String text = (String) value;
      setText(text);
      setFont(Styles.OpenSans.getLIGHT(Styles.P));
      setBorder(new EmptyBorder(3, 10, 3, 0));

      if(text.startsWith("Nieuw")) {
          setIcon(Styles.icons.getIcon("plus", 16));

      } else {
          setIcon(Styles.icons.getIcon("update", 16));
      }

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
