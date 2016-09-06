/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.swingmodel;

import com.dieterholvoet.scoutsapp.model.Lid;
import com.dieterholvoet.scoutsapp.util.LidComparator;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Dieter
 */
public class LeidingComboBoxModel extends DefaultComboBoxModel {
    private final String NOT_SELECTABLE_OPTION = "Kies een persoon";
    private final List<Lid> data;
    private String selection;

    public LeidingComboBoxModel(List<Lid> data) {
        Collections.sort(data, new LidComparator());
        data.add(0, new Lid(NOT_SELECTABLE_OPTION, ""));
        this.data = data;
    }
    
    public Lid getSelectedLid() {
        String voornaam = selection.split("\\s+", 2)[0],
               achternaam = selection.split("\\s+", 2)[1];
        
        return data.get(data.indexOf(new Lid(voornaam, achternaam)));
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Object getElementAt(int index) {
        return data.get(index).toString();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selection = (String) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selection;
    }    
}
