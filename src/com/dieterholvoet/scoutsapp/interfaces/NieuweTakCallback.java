/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.interfaces;

import com.dieterholvoet.scoutsapp.model.LeidingTak;
import java.util.ArrayList;

/**
 *
 * @author Dieter
 */
public interface NieuweTakCallback {
    public void NieuweTakCallback(ArrayList<LeidingTak> huidigeLeiding, ArrayList<LeidingTak> vroegereLeiding);
}
