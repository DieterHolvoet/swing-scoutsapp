/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.swingmodel;

import com.dieterholvoet.scoutsapp.model.Lid;
import com.dieterholvoet.scoutsapp.model.Tak;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.table.AbstractTableModel;

/**
 * @author Dieter
 * Source: http://www.java2s.com/Code/Java/Swing-JFC/TablewithacustomTableModel.htm
 */
public final class TakLedenTableModel extends AbstractTableModel {
    private final Preferences prefs = Preferences.userNodeForPackage(com.dieterholvoet.scoutsapp.gui.WerkstukGUI.class);
    private int takID;
    private ArrayList<Lid> leden;
    private ArrayList<Tak> takken;
    private final String[] columnNames = {
        "Lidnummer",
        "Voornaam",
        "Achternaam",
        "Geslacht",
        "Tak",
        "Totem",
        "Adjectief",
        "Geboortedatum",
        "Telefoonnummer",
        "GSM-nummer",
        "E-mailadres",
        "Adres",
        "Heeft betaald?",
        "Neemt windroos?",
        "Aandachtspunten"
    };

    public TakLedenTableModel(ArrayList<Lid> leden, ArrayList<Tak> takken) {
        loadData(leden, takken);
    }
    
    public Lid getLid(long lidnr) {
        return leden.get(leden.indexOf(new Lid(lidnr)));
    }
    
    public void loadData(ArrayList<Lid> leden, ArrayList<Tak> takken) {
        this.takID = prefs.getInt("tak", 0);
        
        ArrayList<Lid> tak = new ArrayList<>();        
        for(Lid lid : leden) {
            if(lid.getTakID() == this.takID) {
                tak.add(lid);
            }
        }
        this.leden = tak;
        this.takken = takken;
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
      return leden.size();
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
        Lid lid = leden.get(row);
        
        switch (col) {
            case 0:
                value = lid.getLidnr();
                break;
                
            case 1:
                value = lid.getVoornaam();
                break;
                
            case 2:
                value = lid.getAchternaam();
                break;
                
            case 3:
                value = lid.getGeslacht();
                break;
                
            case 4:
                value = takken.get(lid.getTakID() - 1).getNaam();
                break;
                
            case 5:
                value = lid.getTotem();
                break;
                
            case 6:
                value = lid.getAdjectief();
                break;
                
            case 7:
                value = lid.getGeboortedatum().toString();
                break;
                
            case 8:
                value = lid.getTelefoon();
                break;
                
            case 9:
                value = lid.getGsm();
                break;
                
            case 10:
                value = lid.getEmail();
                break;
                
            case 11:
                value = lid.getAdres();
                break;
                
            case 12:
                value = lid.getHeeftBetaald() ? "Ja" : "Nee";
                break;
                
            case 13:
                value = lid.getNeemtWindroos() ? "Ja" : "Nee";
                break;
                
            case 14:
                value = lid.getAandachtspunten();
                break;
                
            default:
                value = "";
                break;
        }

        return value;
    }
}
