/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.util;

import com.dieterholvoet.scoutsapp.model.Lid;
import java.util.Comparator;

/**
 *
 * @author Dieter
 */
public class LidComparator implements Comparator<Lid> {
    @Override
    public int compare(Lid lid1, Lid lid2) {
        return lid1.toString().compareTo(lid2.toString());
    }
}
