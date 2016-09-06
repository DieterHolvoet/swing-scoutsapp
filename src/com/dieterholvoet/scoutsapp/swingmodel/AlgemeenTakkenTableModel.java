/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.swingmodel;

import com.dieterholvoet.scoutsapp.model.LeidingTak;
import com.dieterholvoet.scoutsapp.model.Tak;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * @author Dieter
 * Source: http://www.java2s.com/Code/Java/Swing-JFC/TablewithacustomTableModel.htm
 */
public final class AlgemeenTakkenTableModel extends AbstractTableModel {
    private ArrayList<Tak> takken;
    private final String[] columnNames = {
        "ID",
        "Taknaam",
        "Huidige leiding",
        "Vroegere leiding"
    };

    public AlgemeenTakkenTableModel(ArrayList<Tak> takken) {
        loadData(takken);
    }
    
    public Tak getTak(int takID) {
        return takken.get(takken.indexOf(new Tak(takID)));
    }
    
    public void loadData(ArrayList<Tak> takken) {
        this.takken = takken;
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
      return takken.size();
    }

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }
    
    @Override
    public String getColumnName(int col) {
      return columnNames[col];
    }
    
    // Source: http://stackoverflow.com/a/12559437
    @Override
    public Object getValueAt(int row, int col) {
        Object value;
        Tak tak = takken.get(row);
        String s = "";
        
        switch (col) {
            case 0:
                value = tak.getTakID();
                break;
                
            case 1:
                value = tak.getNaam();
                break;
                
            case 2:
                for(LeidingTak l : tak.getHuidigeLeiding()) {
                    s += l.toString() + ", ";
                    if(s.length() >= 50) break;
                }
                
                if(s.length() > 2) {
                    value = s.substring(0, s.length() - 2);
                    
                } else {
                    value = "Geen info beschikbaar";
                }                
                break;
                
            case 3:
                for(LeidingTak l : tak.getVroegereLeiding()) {
                    s += l.toString() + ", ";
                    if(s.length() >= 50) break;
                }
                
                if(s.length() > 2) {
                    value = s.substring(0, s.length() - 2);
                    
                } else {
                    value = "Geen info beschikbaar";
                }
                break;
                
            default:
                value = "";
                break;
        }

        return value;
    }
}
