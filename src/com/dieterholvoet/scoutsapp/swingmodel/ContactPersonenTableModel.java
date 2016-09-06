/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.swingmodel;

import com.dieterholvoet.scoutsapp.model.ContactPersoon;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * @author Dieter
 * Source: http://www.java2s.com/Code/Java/Swing-JFC/TablewithacustomTableModel.htm
 */
public final class ContactPersonenTableModel extends AbstractTableModel {
    private ArrayList<ContactPersoon> contactpersonen;
    private final String[] columnNames = {
        "Voornaam",
        "Achternaam",
        "Telefoonnummer",
        "E-mailadres"
    };

    public ContactPersonenTableModel(ArrayList<ContactPersoon> contactpersonen) {
        loadData(contactpersonen);
    }
    
    public void loadData(ArrayList<ContactPersoon> contactpersonen) {
        this.contactpersonen = contactpersonen;
        this.fireTableDataChanged();
    }
    
    /*public Lid getLid(long lidnr) {
        return contactpersonen.get(contactpersonen.indexOf(new Lid(lidnr)));
    }*/

    @Override
    public int getRowCount() {
      return contactpersonen.size();
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
        ContactPersoon contactpersoon = contactpersonen.get(row);
        
        switch (col) {
            case 0:
                value = contactpersoon.getVoornaam();
                break;
                
            case 1:
                value = contactpersoon.getAchternaam();
                break;
                
            case 2:
                value = contactpersoon.getTelefoon();
                break;
                
            case 3:
                value = contactpersoon.getEmail();
                break;

            default:
                value = "";
                break;
        }

        return value;
    }
}
