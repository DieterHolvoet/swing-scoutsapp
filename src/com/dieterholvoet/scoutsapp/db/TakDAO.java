/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.db;

import com.dieterholvoet.scoutsapp.gui.Splash;
import com.dieterholvoet.scoutsapp.model.Lid;
import com.dieterholvoet.scoutsapp.model.Tak;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Dieter
 */
public class TakDAO {
    public static final int FETCH_NOTHING = 0;
    public static final int FETCH_HUIDIGE_LEIDING = 1;
    public static final int FETCH_VROEGERE_LEIDING = 2;
    public static final int FETCH_ALL = 3;
    
    public static ArrayList<Tak> getTakken(int status) {
        return getTakken(status, -1);
    }
    
    public static ArrayList<Tak> getTakken(int status, int totalSize) {
        ArrayList<Tak> resultaat = new ArrayList<>();
        boolean doProgress = totalSize != -1;
        int size = 0;
        
        try {
            ResultSet sizeSet = Database.voerSqlUitEnHaalResultaatOp("SELECT COUNT(*) AS count FROM takken");
            ResultSet takkenSet = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM takken");
            
            if(sizeSet != null) {
                if (sizeSet.isBeforeFirst() ) {
                    sizeSet.first();
                    size = sizeSet.getInt("count");
                }
            }
            
            if (takkenSet != null) {
                int index = 0;
                while (takkenSet.next()) {
                    Tak tak = converteerHuidigeRijNaarObject(takkenSet);
                    if(status == FETCH_ALL || status == FETCH_HUIDIGE_LEIDING) {
                        tak.setHuidigeLeiding(LeidingDAO.getCurrentLeidingByTakID(tak.getTakID()));
                    }
                    if(status == FETCH_ALL || status == FETCH_VROEGERE_LEIDING) {
                        tak.setVroegereLeiding(LeidingDAO.getPastLeidingByTakID(tak.getTakID()));
                    }
                    if(doProgress) {
                        Splash.updateProgress(new Integer[] {index, size, totalSize});
                    }
                    resultaat.add(tak);
                    index++;
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return resultaat;
    }
    
    public static int getTakkenCount() {
        int aantal = -1;
        try {
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT COUNT(takID) AS count from takken");
            if (mijnResultset != null) {
                if (mijnResultset.isBeforeFirst() ) {
                    mijnResultset.first();
                    aantal = mijnResultset.getInt("count");
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return aantal;
    }

    public static Tak getTakByID(int ID, int status) {
        Tak resultaat = null;
        try {
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM takken WHERE takID = ?", new Object[] { ID });
            if (mijnResultset != null) {
                if (mijnResultset.isBeforeFirst() ) {
                    mijnResultset.first();
                    resultaat = converteerHuidigeRijNaarObject(mijnResultset);
                    if(status == FETCH_ALL || status == FETCH_HUIDIGE_LEIDING) {
                        resultaat.setHuidigeLeiding(LeidingDAO.getCurrentLeidingByTakID(resultaat.getTakID()));
                    }
                    if(status == FETCH_ALL || status == FETCH_VROEGERE_LEIDING) {
                        resultaat.setVroegereLeiding(LeidingDAO.getPastLeidingByTakID(resultaat.getTakID()));
                    }
                }
            }
                
        } catch (SQLException ex) {
                ex.printStackTrace();
                // Foutafhandeling naar keuze
        }

        return resultaat;
    }

    public static Tak getTakByName(String naam, int status) {
        Tak resultaat = null;
        try {
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM takken WHERE naam = ?", new Object[] { naam });
            if (mijnResultset != null) {
                if(mijnResultset.isBeforeFirst()) {
                    mijnResultset.first();
                    resultaat = converteerHuidigeRijNaarObject(mijnResultset);
                    if(status == FETCH_ALL || status == FETCH_HUIDIGE_LEIDING) {
                        resultaat.setHuidigeLeiding(LeidingDAO.getCurrentLeidingByTakID(resultaat.getTakID()));
                    }
                    if(status == FETCH_ALL || status == FETCH_VROEGERE_LEIDING) {
                        resultaat.setVroegereLeiding(LeidingDAO.getPastLeidingByTakID(resultaat.getTakID()));
                    }
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return resultaat;
    }

    public static int getTakIDByCurrentLeiding(Lid leiding) {
        int takID = 0;

        try {
            ResultSet mijnResultset = Database.voerSqlUitEnHaalResultaatOp("SELECT * FROM leiding_takken WHERE lidnr = ? AND end_date is null", new Object[] { leiding.getLidnr() });
            if (mijnResultset != null) {
                takID = mijnResultset.getInt("takID");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return takID;
    }

    public static int voegTakToe(Tak nieuweTak) {
        int takID = 0;
        try {
            Object[] values = new Object[] { nieuweTak.getNaam() };
            String insertString = "INSERT INTO takken (naam) VALUES (?)";
            takID = Database.voerSqlUitEnHaalIDOp(insertString, values);
                
        } catch (SQLException ex) {
                ex.printStackTrace();
                // Foutafhandeling naar keuze
        }
        
        return takID;
    }

    public static int updateTak(Tak nieuweTak) {
        int aantalAangepasteRijen = 0;
        try {
            aantalAangepasteRijen = Database.voerSqlUitEnHaalAantalAangepasteRijenOp("UPDATE takken SET naam = ? WHERE takId = ?", new Object[] { nieuweTak.getNaam(), nieuweTak.getTakID() });
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return aantalAangepasteRijen;
    }

    public static int verwijderTak(int takID) {
        int aantalAangepasteRijen = 0;
        try {
            aantalAangepasteRijen = Database.voerSqlUitEnHaalAantalAangepasteRijenOp("DELETE FROM takken WHERE takID = ?", new Object[] { takID });
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return aantalAangepasteRijen;
    }
    
    private static Tak converteerHuidigeRijNaarObject(ResultSet mijnResultset) throws SQLException {
        return new Tak(mijnResultset.getInt("takID"), mijnResultset.getString("naam"));
    }
}
