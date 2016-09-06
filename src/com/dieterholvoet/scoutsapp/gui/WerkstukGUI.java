package com.dieterholvoet.scoutsapp.gui;

import com.dieterholvoet.scoutsapp.model.Tak;
import com.dieterholvoet.scoutsapp.model.LeidingTak;
import com.dieterholvoet.scoutsapp.model.Lid;
import com.dieterholvoet.scoutsapp.interfaces.NieuweTakCallback;
import com.dieterholvoet.scoutsapp.interfaces.CSVParserCallback;
import com.dieterholvoet.scoutsapp.interfaces.NieuwLidCallback;
import com.dieterholvoet.scoutsapp.util.HelperFunctions;
import com.dieterholvoet.scoutsapp.util.CSVParser;
import com.dieterholvoet.scoutsapp.util.Styles;
import com.dieterholvoet.scoutsapp.swingmodel.AlgemeenLedenTableModel;
import com.dieterholvoet.scoutsapp.swingmodel.TakLedenTableModel;
import com.dieterholvoet.scoutsapp.db.LeidingDAO;
import com.dieterholvoet.scoutsapp.db.LidDAO;
import com.dieterholvoet.scoutsapp.db.TakDAO;
import com.dieterholvoet.scoutsapp.swingmodel.AlgemeenTakkenTableModel;
import com.dieterholvoet.scoutsapp.swingrenderer.ImportListRenderer;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dieter
 */
public final class WerkstukGUI extends javax.swing.JFrame implements CSVParserCallback, NieuwLidCallback, NieuweTakCallback {
    private final CardLayout ContentViewLayout;
    private DefaultListModel importListModel;
    private CSVParser parser = null;
    private final Preferences prefs = Preferences.userNodeForPackage(com.dieterholvoet.scoutsapp.gui.WerkstukGUI.class);
    
    private ArrayList<Lid> leden;
    private ArrayList<Lid> leiding;
    private ArrayList<Tak> takken;
    
    public WerkstukGUI(ArrayList<Lid> leden, ArrayList<Lid> leiding, ArrayList<Tak> takken) {
        this.leden = leden;
        this.leiding = leiding;
        this.takken = takken;

        initComponents();
        loadPreferences();
        this.ContentViewLayout = (CardLayout)ContentView.getLayout();
        this.ContentViewLayout.show(ContentView, "pnlAlgemeenLeden");

        Splash.setText("Opbouwen van tabellen");
        Splash.setProgress(93);
        loadTables();
        
        Splash.setText("Opbouwen van lijsten");
        Splash.setProgress(95);
        loadLists();

        Splash.setText("Stijlen van de interface");
        Splash.setProgress(99);
        setStyles();
    }
    
    // Source:
    // http://stackoverflow.com/a/6714381
    // http://zetcode.com/gfx/java2d/clipping/

    private ImageIcon prepareProfilePicture(ImageIcon imageIcon, int w, int h) {
        Image image = imageIcon.getImage();
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        
        g2.setClip(new Ellipse2D.Double(0, 0, w, w));
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, w, h, null);
        g2.dispose();

        return new ImageIcon(resizedImg);
    }

    private void tableRowClickHandler(MouseEvent evt) {
        JTable table = (JTable)evt.getSource();
        Long lidnr;
        Lid lid;
        
        if (evt.getClickCount() == 2) {
            switch(HelperFunctions.getComponentVariableName(table)) {
                case "tblAlgemeenLeden":
                case "tblAlgemeenLeiding":
                    // Source: http://stackoverflow.com/a/14306669
                    AlgemeenLedenTableModel AlgemeenLedenTableModel = (AlgemeenLedenTableModel) table.getModel();
                    lidnr = (Long) AlgemeenLedenTableModel.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), 0);
                    lid = AlgemeenLedenTableModel.getLid(lidnr);
                    editPerson(lid);
                    break;
                    
                case "tblTakLeden":
                    TakLedenTableModel TakLedenTableModel = (TakLedenTableModel) table.getModel();
                    lidnr = (Long) TakLedenTableModel.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), 0);
                    lid = TakLedenTableModel.getLid(lidnr);
                    editPerson(lid);
                    break;
                    
                case "tblAlgemeenTakken":
                    AlgemeenTakkenTableModel AlgemeenTakkenTableModel = (AlgemeenTakkenTableModel) table.getModel();
                    int takID = (int) AlgemeenTakkenTableModel.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), 0);
                    Tak tak = AlgemeenTakkenTableModel.getTak(takID);
                    editTak(tak);
                    break;
            }
        }
    }
    
    class DataLoadWorker extends SwingWorker<Void, Object> {
       @Override
       public Void doInBackground() {
            leden = LidDAO.getLeden(LidDAO.FETCH_ALL);
            leiding = LeidingDAO.getLeiding(LeidingDAO.FETCH_ALL);
            takken = TakDAO.getTakken(TakDAO.FETCH_ALL);
            return null;
       }

       @Override
       protected void done() {
            reloadTables();
       }
    }
    
    public void reloadData() {
        (new DataLoadWorker()).execute();
    }
    
    public void loadTables() {
        tblAlgemeenLeden.setModel(new AlgemeenLedenTableModel(leden, takken));
        tblAlgemeenLeden.setAutoCreateRowSorter(true);
        Splash.setProgress(45);
        tblAlgemeenLeiding.setModel(new AlgemeenLedenTableModel(leiding, takken));
        tblAlgemeenLeiding.setAutoCreateRowSorter(true);
        Splash.setProgress(52);
        tblTakLeden.setModel(new TakLedenTableModel(leden, takken));
        tblTakLeden.setAutoCreateRowSorter(true);
        Splash.setProgress(59);
        tblAlgemeenTakken.setModel(new AlgemeenTakkenTableModel(takken));
        tblAlgemeenTakken.setAutoCreateRowSorter(true);
        tblAlgemeenTakken.getColumnModel().getColumn(0).setMaxWidth(40);
        Splash.setProgress(65);
        setLblTakLedenHeader();
    }
    
    public void setLblTakLedenHeader() {
        String naam;
        if(takken.size() <= 1 || prefs == null) {
            naam = "Tak";
        } else {
            int pref = prefs.getInt("tak", 0);
            naam = pref == 0 ? "Tak" : takken.get(pref - 1).getNaam();
        }
        lblTakLedenHeader.setText(naam);
    }
    
    public void loadLists() {
        DefaultListModel<Tak> takListModel = new DefaultListModel<>();
        lstVoorkeurenTak.setModel(takListModel);
        lstVoorkeurenTak.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        importListModel = new DefaultListModel<>();
        lstImports.setModel(importListModel);
        lstImports.setCellRenderer(new ImportListRenderer());
        lstImports.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        reloadLists();
    }
    
    public void reloadTables() {
        AlgemeenLedenTableModel ledenModel = (AlgemeenLedenTableModel)tblAlgemeenLeden.getModel();
        ledenModel.loadData(leden, takken);
        
        AlgemeenLedenTableModel leidingModel = (AlgemeenLedenTableModel)tblAlgemeenLeiding.getModel();
        leidingModel.loadData(leiding, takken);
        
        TakLedenTableModel takLedenModel = (TakLedenTableModel)tblTakLeden.getModel();
        takLedenModel.loadData(leden, takken);
        
        AlgemeenTakkenTableModel takkenModel = (AlgemeenTakkenTableModel)tblAlgemeenTakken.getModel();
        takkenModel.loadData(takken);
        
        setLblTakLedenHeader();
        
        txtAlgemeenLedenFilter.setText("");
        txtAlgemeenLeidingFilter.setText("");
        txtTakLedenFilter.setText("");
    }
    
    public void reloadLists() {
        DefaultListModel<Tak> takListModel = (DefaultListModel) lstVoorkeurenTak.getModel();
        takListModel.clear();
        for(Tak tak : takken) {
            takListModel.addElement(tak);
        }
    }
    
    private void loadPreferences() {
        boolean isLeiding = prefs.getBoolean("isleiding", false);
        
        txtVoorkeurenVoornaam.setText(prefs.get("voornaam", ""));
        txtVoorkeurenAchternaam.setText(prefs.get("achternaam", ""));
        lstVoorkeurenTak.setSelectedIndex(prefs.getInt("tak", 0) - 1);
        btnVoorkeurenLeiding.setSelected(isLeiding);
        btnVoorkeurenLeiding.setText(prefs.getBoolean("isleiding", false) ? "Leiding" : "Lid");
        
        lblLeiding.setVisible(isLeiding);
        lblLeidingLeiding.setVisible(isLeiding);
        lblLeidingRaden.setVisible(isLeiding);
        lblLeidingActiviteiten.setVisible(isLeiding);
    }
    
    // Source: http://stackoverflow.com/questions/1107911/how-can-i-filter-rows-in-a-jtable
    public void setFilter(JTable table, String filter) {
        RowFilter ledenRowFilter;
        TableRowSorter ledenSorter;

        try {
            ledenRowFilter = RowFilter.regexFilter( "(?i)^" + Pattern.quote(filter));

        } catch (java.util.regex.PatternSyntaxException e) {
            System.out.println(e);
            return;
        }

        ledenSorter = (TableRowSorter) table.getRowSorter();
        ledenSorter.setRowFilter(ledenRowFilter);
        table.setRowSorter(ledenSorter);
    }
    
    private void setStyles() {
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        
        /* SIDEBAR STYLES */
        Sidebar.setBackground(Styles.BIJNAZWART);
        Sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));
        Sidebar.setMinimumSize(new Dimension(230, 0));
        
        for (Component component : Sidebar.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                switch(HelperFunctions.getComponentVariableName(label).substring(0, 3)) {
                    case "lbl":
                        label.setBackground(Styles.BIJNAZWART);
                        label.setOpaque(false);
                        label.setForeground(Styles.BEIGE);
                        label.setText(label.getText().toUpperCase());
                        label.setFont(Styles.OpenSans.getSEMIBOLD(Styles.MENUH2));
                        label.setBorder(new EmptyBorder(8, 10, 0, 10));
                        label.setIconTextGap(10);

                        switch(label.getName()) {
                            case "lblAlgemeen":
                            case "lblTak":
                            case "lblLeiding":
                            case "lblInstellingen":
                                label.setFont(Styles.OpenSans.getLIGHT(Styles.MENUH1));
                                label.setBorder(new EmptyBorder(30, 10, 10, 10));
                                break;
                                
                            case "lblAlgemeenLeden":
                            case "lblAlgemeenLeiding":
                            case "lblTakLeden":
                            case "lblLeidingLeiding":
                                label.setIcon(Styles.icons.getIcon("people", 16));
                                break;
                                
                            case "lblAlgemeenKalender":
                                label.setIcon(Styles.icons.getIcon("calendar", 16));
                                break;
                                
                            case "lblTakVergaderingen":
                            case "lblLeidingActiviteiten":
                                label.setIcon(Styles.icons.getIcon("activity", 16));
                                break;
                                
                            case "lblTakAanwezigheden":
                                label.setIcon(Styles.icons.getIcon("check", 16));
                                break;
                                
                            case "lblAlgemeenFinancien":
                            case "lblTakTakkas":
                                label.setIcon(Styles.icons.getIcon("money", 16));
                                break;
                                
                            case "lblLeidingRaden":
                            case "lblAlgemeenTakken":
                                label.setIcon(Styles.icons.getIcon("meeting", 16));
                                break;
                                
                            case "lblAlgemeenMateriaal":
                                label.setIcon(Styles.icons.getIcon("notepad", 16));
                                break;
                                
                            case "lblInstellingenImporteren":
                                label.setIcon(Styles.icons.getIcon("box", 16));
                                break;
                                
                            case "lblInstellingenVoorkeuren":
                                label.setIcon(Styles.icons.getIcon("wrench", 16));
                                break;
                                
                            default:
                                break;
                        }
                        break;
                    
                    case "img":
                        switch(label.getName()) {
                            case "imgProfileImage":
                                int w = 150;                                    // (int)Sidebar.getSize().getWidth();
                                label.setPreferredSize(new Dimension(w, w));
                                label.setMaximumSize(new Dimension(w, w));
                                label.setMinimumSize(new Dimension(w, w));
                                label.setIcon(prepareProfilePicture(Styles.getPROFILEPIC(), w, w));
                                break;
                        }
                        break;
                }
            }
        }
        
        /* TABLE STYLES */
        for (Component component : HelperFunctions.getAllComponents(ContentView)) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                
                switch(HelperFunctions.getComponentVariableName(label)) {
                    
                    // HEADER LABELS
                    case "lblAlgemeenLedenHeader":
                    case "lblAlgemeenLeidingHeader":
                    case "lblTakLedenHeader":
                        applyHeaderStyles(label);
                        label.setIcon(Styles.icons.getIcon("people", 64));
                        break;
                        
                    case "lblAlgemeenKalenderHeader":
                        applyHeaderStyles(label);
                        label.setIcon(Styles.icons.getIcon("calendar", 64));
                        break;
                        
                    case "lblAlgemeenMateriaalHeader":
                        applyHeaderStyles(label);
                        label.setIcon(Styles.icons.getIcon("notepad", 64));
                        break;
                        
                    case "lblAlgemeenFinancienHeader":
                    case "lblTakTakkasHeader":
                        applyHeaderStyles(label);
                        label.setIcon(Styles.icons.getIcon("money", 64));
                        break;
                        
                    case "lblTakVergaderingenHeader":
                    case "lblLeidingActiviteitenHeader":
                        applyHeaderStyles(label);
                        label.setIcon(Styles.icons.getIcon("activity", 64));
                        break;
                        
                    case "lblTakAanwezighedenHeader":
                        applyHeaderStyles(label);
                        label.setIcon(Styles.icons.getIcon("check", 64));
                        break;
                        
                    case "lblLeidingRadenHeader":
                    case "lblAlgemeenTakkenHeader":
                        applyHeaderStyles(label);
                        label.setIcon(Styles.icons.getIcon("meeting", 64));
                        break;
                        
                    case "lblInstellingenImporterenHeader":
                        applyHeaderStyles(label);
                        label.setIcon(Styles.icons.getIcon("box", 64));
                        break;
                        
                    case "lblInstellingenVoorkeurenHeader":
                        applyHeaderStyles(label);
                        label.setIcon(Styles.icons.getIcon("wrench", 64));
                        break;
                        
                    // NON-IMPLEMENTED FEATURE LABELS
                    case "lblAlgemeenKalenderContent":
                    case "lblAlgemeenMateriaalContent":
                    case "lblAlgemeenFinancienContent":
                    case "lblTakVergaderingenContent":
                    case "lblTakAanwezighedenContent":
                    case "lblTakTakkasContent":
                    case "lblLeidingRadenContent":
                    case "lblLeidingActiviteitenContent":
                        label.setFont(Styles.OpenSans.getLIGHT(Styles.H2));
                        break;
                    
                    // ICONS
                    case "icnAlgemeenLedenReload":
                    case "icnAlgemeenLeidingReload":
                    case "icnTakLedenReload":
                    case "icnAlgemeenTakkenReload":
                        label.setIcon(Styles.icons.getIcon("reload", 24));
                        label.setBorder(new EmptyBorder(13, 0, 10, 9));
                        break;
                        
                    case "icnAlgemeenLedenCreate":
                    case "icnAlgemeenLeidingCreate":
                    case "icnTakLedenCreate":
                        label.setIcon(Styles.icons.getIcon("add", 24));
                        label.setBorder(new EmptyBorder(13, 9, 10, 0));
                        break;
                        
                    case "icnAlgemeenTakkenCreate":
                        label.setIcon(Styles.icons.getIcon("add_2", 24));
                        label.setBorder(new EmptyBorder(13, 9, 10, 0));
                        break;
                        
                    // GENERAL LABELS
                    case "lblStep1":
                    case "lblStep2":
                    case "lblVoorkeurenVoornaam":
                    case "lblVoorkeurenAchternaam":
                    case "lblVoorkeurenTak":
                    case "lblVoorkeurenLeiding":
                        label.setFont(Styles.OpenSans.getLIGHT(Styles.LABEL));

                    default:
                        break;
                }
                
            } else if(component instanceof JTable) {
                JTable table = (JTable) component;
                switch(HelperFunctions.getComponentVariableName(table)) {
                    case "tblAlgemeenLeden":
                    case "tblAlgemeenLeiding":
                    case "tblTakLeden":
                    case "tblAlgemeenTakken":
                        table.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        table.setRowHeight(30);
                        break;

                    default:
                        break;
                }
                
            } else if(component instanceof JButton) {
                JButton button = (JButton) component;
                switch(HelperFunctions.getComponentVariableName(button)) {
                    case "btnFileChooser":
                    case "btnStartImport":
                    case "btnVoorkeurenLeiding":
                        button.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        button.setText(button.getText().toUpperCase());
                        button.setPreferredSize(new Dimension(230, 40));
                        break;

                    default:
                        break;
                }
                
            } else if(component instanceof JToggleButton) {
                JToggleButton button = (JToggleButton) component;
                switch(HelperFunctions.getComponentVariableName(button)) {
                    case "btnFileChooser":
                    case "btnStartImport":
                    case "btnVoorkeurenLeiding":
                        button.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        button.setText(button.getText().toUpperCase());
                        button.setPreferredSize(new Dimension(300, 40));
                        break;

                    default:
                        break;
                }
                
            } else if(component instanceof JTextField) {
                JTextField text = (JTextField) component;
                switch(HelperFunctions.getComponentVariableName(text)) {
                    case "txtVoorkeurenVoornaam":
                    case "txtVoorkeurenAchternaam":
                    case "txtAlgemeenLedenFilter":
                    case "txtAlgemeenLeidingFilter":
                    case "txtTakLedenFilter":
                    case "txtAlgemeenTakkenFilter":
                        // Source: http://stackoverflow.com/a/8792905
                        text.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        text.setBorder(BorderFactory.createCompoundBorder(text.getBorder(), BorderFactory.createEmptyBorder(5, 8, 5, 15)));
                        text.setPreferredSize(new Dimension(300, 40));
                        break;

                    default:
                        break;
                }
                
            } else if(component instanceof JList) {
                JList list = (JList) component;
                switch(HelperFunctions.getComponentVariableName(list)) {
                    case "lstImports":
                    case "lstVoorkeurenTak":
                        list.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        list.setFixedCellHeight(30);
                        break;

                    default:
                        break;
                }
            }
        }
    }
    
    private void applyHeaderStyles(JLabel label) {
        label.setFont(Styles.FjallaOne.getREGULAR(Styles.H1));
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setForeground(Styles.BIJNAZWART);
        label.setBorder(new EmptyBorder(20, 0, 0, 10));
        label.setIconTextGap(7);
    }
    
    // Source: http://www.javaquery.com/2010/11/how-to-pass-variable-values-between.html
    private void newPerson() {
        if (!HelperFunctions.isDialogOpen(this, "LidFrame")) {
            LidGUI nieuwLid = new LidGUI(this, this.takken, "Lid toevoegen");
            nieuwLid.setVisible(true);
        }
    }
    
    private void editPerson(Lid lid) {
        if (!HelperFunctions.isDialogOpen(this, "LidFrame")) {
            LidGUI nieuwLid = new LidGUI(this, this.takken, lid, "Lid aanpassen");
            nieuwLid.setVisible(true);
        }
    }
    
    private void newTak() {
        if (!HelperFunctions.isDialogOpen(this, "TakFrame")) {
            TakGUI nieuweTak = new TakGUI(this, leiding, "Tak toevoegen");
            nieuweTak.setVisible(true);
        }
    }
    
    private void editTak(Tak tak) {
        if (!HelperFunctions.isDialogOpen(this, "TakFrame")) {
            TakGUI nieuweTak = new TakGUI(this, leiding, tak, "Tak aanpassen");
            nieuweTak.setVisible(true);
        }
    }
    
    private void setLabelFocus(JLabel label) {
        label.setFont(Styles.OpenSans.getBOLD(Styles.MENUH2));
    }
    
    private void removeLabelFocus(JLabel label) {
        label.setFont(Styles.OpenSans.getREGULAR(Styles.MENUH2));
    }
    
    @Override
    public void CSVParserCallback() {
        System.out.println("File reading completed!");
        btnFileChooser.setEnabled(true);
        btnStartImport.setEnabled(true);
        this.parser = new CSVParser(this);
        reloadData();
    }
    
    @Override
    public void CSVParserProgressUpdate(String text) {
        importListModel.addElement(text);
        pbrImporteren.setValue((int) parser.getProgress());
        HelperFunctions.scrollToMax(jScrollPaneImport);
    }
    
    @Override
    public void NieuwLidCallback(Lid nieuwLid) {
        Long lidnr = nieuwLid.getLidnr();
        boolean isNieuwLid = nieuwLid.isLeiding() ? !LeidingDAO.leidingExists(lidnr) : !LidDAO.lidExists(lidnr);
        boolean isLeiding = nieuwLid.getTakID() == 1;
        
        if(isNieuwLid) {
            if(isLeiding) {
                LeidingDAO.voegLeidingToe(nieuwLid);
                leiding.add(nieuwLid);
                
            } else {
                LidDAO.voegLidToe(nieuwLid);
                leden.add(nieuwLid);
            }
            
        } else {
            if(isLeiding) {
                int index = leiding.indexOf(nieuwLid);
                if(index == -1) {
                    LidDAO.verwijderLid(lidnr);
                    leden.remove(nieuwLid);
                    LeidingDAO.voegLeidingToe(nieuwLid);
                    leiding.add(nieuwLid);
                    
                } else {
                    LeidingDAO.updateLeiding(nieuwLid);
                    leiding.set(index, nieuwLid);
                }
                
            } else {
                LidDAO.updateLid(nieuwLid);
                leden.set(leden.indexOf(nieuwLid), nieuwLid);
            }
        }

        reloadTables();
    }
    
    @Override
    public void NieuweTakCallback(ArrayList<LeidingTak> huidigeLeiding, ArrayList<LeidingTak> vroegereLeiding) {
        reloadTables();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        SplitPane = new javax.swing.JSplitPane();
        Sidebar = new javax.swing.JPanel();
        imgProfileImage = new javax.swing.JLabel();
        lblAlgemeen = new javax.swing.JLabel();
        lblAlgemeenLeden = new javax.swing.JLabel();
        lblAlgemeenTakken = new javax.swing.JLabel();
        lblAlgemeenKalender = new javax.swing.JLabel();
        lblAlgemeenMateriaal = new javax.swing.JLabel();
        lblAlgemeenFinancien = new javax.swing.JLabel();
        lblTak = new javax.swing.JLabel();
        lblTakLeden = new javax.swing.JLabel();
        lblTakVergaderingen = new javax.swing.JLabel();
        lblTakAanwezigheden = new javax.swing.JLabel();
        lblTakTakkas = new javax.swing.JLabel();
        lblLeiding = new javax.swing.JLabel();
        lblLeidingLeiding = new javax.swing.JLabel();
        lblLeidingRaden = new javax.swing.JLabel();
        lblLeidingActiviteiten = new javax.swing.JLabel();
        lblInstellingen = new javax.swing.JLabel();
        lblInstellingenImporteren = new javax.swing.JLabel();
        lblInstellingenVoorkeuren = new javax.swing.JLabel();
        ContentView = new javax.swing.JPanel();
        pnlHome = new javax.swing.JPanel();
        pnlAlgemeenLeden = new javax.swing.JPanel();
        lblAlgemeenLedenHeader = new javax.swing.JLabel();
        pnlAlgemeenLedenIcn = new javax.swing.JPanel();
        icnAlgemeenLedenReload = new javax.swing.JLabel();
        icnAlgemeenLedenCreate = new javax.swing.JLabel();
        txtAlgemeenLedenFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAlgemeenLeden = new javax.swing.JTable();
        pnlAlgemeenTakken = new javax.swing.JPanel();
        lblAlgemeenTakkenHeader = new javax.swing.JLabel();
        pnlAlgemeenTakkenIcn = new javax.swing.JPanel();
        icnAlgemeenTakkenReload = new javax.swing.JLabel();
        icnAlgemeenTakkenCreate = new javax.swing.JLabel();
        txtAlgemeenTakkenFilter = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblAlgemeenTakken = new javax.swing.JTable();
        pnlAlgemeenKalender = new javax.swing.JPanel();
        lblAlgemeenKalenderHeader = new javax.swing.JLabel();
        lblAlgemeenKalenderContent = new javax.swing.JLabel();
        pnlAlgemeenMateriaal = new javax.swing.JPanel();
        lblAlgemeenMateriaalHeader = new javax.swing.JLabel();
        lblAlgemeenMateriaalContent = new javax.swing.JLabel();
        pnlAlgemeenFinancien = new javax.swing.JPanel();
        lblAlgemeenFinancienHeader = new javax.swing.JLabel();
        lblAlgemeenFinancienContent = new javax.swing.JLabel();
        pnlTakLeden = new javax.swing.JPanel();
        lblTakLedenHeader = new javax.swing.JLabel();
        pnlTakLedenIcn = new javax.swing.JPanel();
        icnTakLedenReload = new javax.swing.JLabel();
        icnTakLedenCreate = new javax.swing.JLabel();
        txtTakLedenFilter = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblTakLeden = new javax.swing.JTable();
        pnlTakVergaderingen = new javax.swing.JPanel();
        lblTakVergaderingenHeader = new javax.swing.JLabel();
        lblTakVergaderingenContent = new javax.swing.JLabel();
        pnlTakAanwezigheden = new javax.swing.JPanel();
        lblTakAanwezighedenHeader = new javax.swing.JLabel();
        lblTakAanwezighedenContent = new javax.swing.JLabel();
        pnlTakTakkas = new javax.swing.JPanel();
        lblTakTakkasHeader = new javax.swing.JLabel();
        lblTakTakkasContent = new javax.swing.JLabel();
        pnlAlgemeenLeiding = new javax.swing.JPanel();
        lblAlgemeenLeidingHeader = new javax.swing.JLabel();
        pnlAlgemeenLeidingIcn = new javax.swing.JPanel();
        icnAlgemeenLeidingReload = new javax.swing.JLabel();
        icnAlgemeenLeidingCreate = new javax.swing.JLabel();
        txtAlgemeenLeidingFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAlgemeenLeiding = new javax.swing.JTable();
        pnlLeidingRaden = new javax.swing.JPanel();
        lblLeidingRadenHeader = new javax.swing.JLabel();
        lblLeidingRadenContent = new javax.swing.JLabel();
        pnlLeidingActiviteiten = new javax.swing.JPanel();
        lblLeidingActiviteitenHeader = new javax.swing.JLabel();
        lblLeidingActiviteitenContent = new javax.swing.JLabel();
        pnlInstellingenImporteren = new javax.swing.JPanel();
        pnlInstellingenImporteren1 = new javax.swing.JPanel();
        lblInstellingenImporterenHeader = new javax.swing.JLabel();
        lblStep1 = new javax.swing.JLabel();
        btnFileChooser = new javax.swing.JButton();
        lblStep2 = new javax.swing.JLabel();
        btnStartImport = new javax.swing.JButton();
        pbrImporteren = new javax.swing.JProgressBar();
        pnlInstellingenImporteren2 = new javax.swing.JPanel();
        jScrollPaneImport = new javax.swing.JScrollPane();
        lstImports = new javax.swing.JList();
        pnlInstellingenVoorkeuren = new javax.swing.JPanel();
        lblInstellingenVoorkeurenHeader = new javax.swing.JLabel();
        lblVoorkeurenVoornaam = new javax.swing.JLabel();
        lblVoorkeurenAchternaam = new javax.swing.JLabel();
        lblVoorkeurenTak = new javax.swing.JLabel();
        lblVoorkeurenLeiding = new javax.swing.JLabel();
        btnVoorkeurenLeiding = new javax.swing.JToggleButton();
        txtVoorkeurenVoornaam = new javax.swing.JTextField();
        txtVoorkeurenAchternaam = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstVoorkeurenTak = new javax.swing.JList();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1300, 900));

        SplitPane.setEnabled(false);

        Sidebar.setBackground(Styles.BIJNAZWART);
        Sidebar.setLayout(new java.awt.GridBagLayout());

        imgProfileImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imgProfileImage.setName("imgProfileImage"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        Sidebar.add(imgProfileImage, gridBagConstraints);

        lblAlgemeen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAlgemeen.setText("Algemeen");
        lblAlgemeen.setName("lblAlgemeen"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        Sidebar.add(lblAlgemeen, gridBagConstraints);

        lblAlgemeenLeden.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAlgemeenLeden.setText("Alle leden");
        lblAlgemeenLeden.setName("lblAlgemeenLeden"); // NOI18N
        lblAlgemeenLeden.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAlgemeenLedenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAlgemeenLedenMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblAlgemeenLedenMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblAlgemeenLeden, gridBagConstraints);

        lblAlgemeenTakken.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAlgemeenTakken.setText("Alle takken");
        lblAlgemeenTakken.setName("lblAlgemeenTakken"); // NOI18N
        lblAlgemeenTakken.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAlgemeenTakkenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAlgemeenTakkenMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblAlgemeenTakkenMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblAlgemeenTakken, gridBagConstraints);

        lblAlgemeenKalender.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAlgemeenKalender.setText("Kalender");
        lblAlgemeenKalender.setName("lblAlgemeenKalender"); // NOI18N
        lblAlgemeenKalender.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAlgemeenKalenderMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAlgemeenKalenderMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblAlgemeenKalenderMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblAlgemeenKalender, gridBagConstraints);

        lblAlgemeenMateriaal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAlgemeenMateriaal.setText("Materiaal");
        lblAlgemeenMateriaal.setName("lblAlgemeenMateriaal"); // NOI18N
        lblAlgemeenMateriaal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAlgemeenMateriaalMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAlgemeenMateriaalMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblAlgemeenMateriaalMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblAlgemeenMateriaal, gridBagConstraints);

        lblAlgemeenFinancien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAlgemeenFinancien.setText("Financien");
        lblAlgemeenFinancien.setMaximumSize(new java.awt.Dimension(34, 50));
        lblAlgemeenFinancien.setMinimumSize(new java.awt.Dimension(34, 10));
        lblAlgemeenFinancien.setName("lblAlgemeenFinancien"); // NOI18N
        lblAlgemeenFinancien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAlgemeenFinancienMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAlgemeenFinancienMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblAlgemeenFinancienMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblAlgemeenFinancien, gridBagConstraints);

        lblTak.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTak.setText("Tak");
        lblTak.setName("lblTak"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        Sidebar.add(lblTak, gridBagConstraints);

        lblTakLeden.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTakLeden.setText("Leden");
        lblTakLeden.setName("lblTakLeden"); // NOI18N
        lblTakLeden.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTakLedenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTakLedenMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblTakLedenMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblTakLeden, gridBagConstraints);

        lblTakVergaderingen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTakVergaderingen.setText("Vergaderingen");
        lblTakVergaderingen.setName("lblTakVergaderingen"); // NOI18N
        lblTakVergaderingen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTakVergaderingenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTakVergaderingenMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblTakVergaderingenMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblTakVergaderingen, gridBagConstraints);

        lblTakAanwezigheden.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTakAanwezigheden.setText("Aanwezigheden");
        lblTakAanwezigheden.setName("lblTakAanwezigheden"); // NOI18N
        lblTakAanwezigheden.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTakAanwezighedenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTakAanwezighedenMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblTakAanwezighedenMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblTakAanwezigheden, gridBagConstraints);

        lblTakTakkas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTakTakkas.setText("Takkas");
        lblTakTakkas.setName("lblTakTakkas"); // NOI18N
        lblTakTakkas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTakTakkasMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTakTakkasMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblTakTakkasMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblTakTakkas, gridBagConstraints);

        lblLeiding.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLeiding.setText("Leiding");
        lblLeiding.setName("lblLeiding"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        Sidebar.add(lblLeiding, gridBagConstraints);

        lblLeidingLeiding.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLeidingLeiding.setText("Leiding");
        lblLeidingLeiding.setName("lblLeidingLeiding"); // NOI18N
        lblLeidingLeiding.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblLeidingLeidingMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblLeidingLeidingMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLeidingLeidingMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblLeidingLeiding, gridBagConstraints);

        lblLeidingRaden.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLeidingRaden.setText("Raden");
        lblLeidingRaden.setName("lblLeidingRaden"); // NOI18N
        lblLeidingRaden.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblLeidingRadenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblLeidingRadenMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLeidingRadenMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblLeidingRaden, gridBagConstraints);

        lblLeidingActiviteiten.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLeidingActiviteiten.setText("Activiteiten");
        lblLeidingActiviteiten.setName("lblLeidingActiviteiten"); // NOI18N
        lblLeidingActiviteiten.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblLeidingActiviteitenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblLeidingActiviteitenMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLeidingActiviteitenMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblLeidingActiviteiten, gridBagConstraints);

        lblInstellingen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblInstellingen.setText("Instellingen");
        lblInstellingen.setName("lblInstellingen"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        Sidebar.add(lblInstellingen, gridBagConstraints);

        lblInstellingenImporteren.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblInstellingenImporteren.setText("Importeren");
        lblInstellingenImporteren.setName("lblInstellingenImporteren"); // NOI18N
        lblInstellingenImporteren.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblInstellingenImporterenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblInstellingenImporterenMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblInstellingenImporterenMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblInstellingenImporteren, gridBagConstraints);

        lblInstellingenVoorkeuren.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblInstellingenVoorkeuren.setText("Voorkeuren");
        lblInstellingenVoorkeuren.setName("lblInstellingenVoorkeuren"); // NOI18N
        lblInstellingenVoorkeuren.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblInstellingenVoorkeurenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblInstellingenVoorkeurenMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblInstellingenVoorkeurenMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Sidebar.add(lblInstellingenVoorkeuren, gridBagConstraints);

        SplitPane.setLeftComponent(Sidebar);

        ContentView.setLayout(new java.awt.CardLayout());

        pnlHome.setName("pnlHome"); // NOI18N

        javax.swing.GroupLayout pnlHomeLayout = new javax.swing.GroupLayout(pnlHome);
        pnlHome.setLayout(pnlHomeLayout);
        pnlHomeLayout.setHorizontalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6351, Short.MAX_VALUE)
        );
        pnlHomeLayout.setVerticalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 922, Short.MAX_VALUE)
        );

        ContentView.add(pnlHome, "pnlHome");

        pnlAlgemeenLeden.setLayout(new java.awt.GridBagLayout());

        lblAlgemeenLedenHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAlgemeenLedenHeader.setText("Leden");
        lblAlgemeenLedenHeader.setName("lblAlgemeenLedenHeader"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        pnlAlgemeenLeden.add(lblAlgemeenLedenHeader, gridBagConstraints);

        pnlAlgemeenLedenIcn.setLayout(new javax.swing.BoxLayout(pnlAlgemeenLedenIcn, javax.swing.BoxLayout.X_AXIS));

        icnAlgemeenLedenReload.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icnAlgemeenLedenReload.setAlignmentX(0.5F);
        icnAlgemeenLedenReload.setName("icnAlgemeenLedenReload"); // NOI18N
        icnAlgemeenLedenReload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                icnAlgemeenLedenReloadMouseReleased(evt);
            }
        });
        pnlAlgemeenLedenIcn.add(icnAlgemeenLedenReload);

        icnAlgemeenLedenCreate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icnAlgemeenLedenCreate.setAlignmentX(0.5F);
        icnAlgemeenLedenCreate.setName("icnAlgemeenLedenCreate"); // NOI18N
        icnAlgemeenLedenCreate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                icnAlgemeenLedenCreateMouseReleased(evt);
            }
        });
        pnlAlgemeenLedenIcn.add(icnAlgemeenLedenCreate);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        pnlAlgemeenLeden.add(pnlAlgemeenLedenIcn, gridBagConstraints);

        txtAlgemeenLedenFilter.setToolTipText("Filter");
        txtAlgemeenLedenFilter.setName("txtAlgemeenLedenFilter"); // NOI18N
        txtAlgemeenLedenFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAlgemeenLedenFilterKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 130, 0, 130);
        pnlAlgemeenLeden.add(txtAlgemeenLedenFilter, gridBagConstraints);

        tblAlgemeenLeden.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblAlgemeenLeden.setName("tblAlgemeenLeden"); // NOI18N
        tblAlgemeenLeden.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAlgemeenLedenMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblAlgemeenLeden);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlAlgemeenLeden.add(jScrollPane1, gridBagConstraints);

        ContentView.add(pnlAlgemeenLeden, "pnlAlgemeenLeden");

        pnlAlgemeenTakken.setLayout(new java.awt.GridBagLayout());

        lblAlgemeenTakkenHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAlgemeenTakkenHeader.setText("Takken");
        lblAlgemeenTakkenHeader.setName("lblAlgemeenLedenHeader"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        pnlAlgemeenTakken.add(lblAlgemeenTakkenHeader, gridBagConstraints);

        pnlAlgemeenTakkenIcn.setLayout(new javax.swing.BoxLayout(pnlAlgemeenTakkenIcn, javax.swing.BoxLayout.X_AXIS));

        icnAlgemeenTakkenReload.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icnAlgemeenTakkenReload.setAlignmentX(0.5F);
        icnAlgemeenTakkenReload.setName("icnAlgemeenLedenReload"); // NOI18N
        icnAlgemeenTakkenReload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                icnAlgemeenTakkenReloadMouseReleased(evt);
            }
        });
        pnlAlgemeenTakkenIcn.add(icnAlgemeenTakkenReload);

        icnAlgemeenTakkenCreate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icnAlgemeenTakkenCreate.setAlignmentX(0.5F);
        icnAlgemeenTakkenCreate.setName("icnAlgemeenLedenCreate"); // NOI18N
        icnAlgemeenTakkenCreate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                icnAlgemeenTakkenCreateMouseReleased(evt);
            }
        });
        pnlAlgemeenTakkenIcn.add(icnAlgemeenTakkenCreate);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        pnlAlgemeenTakken.add(pnlAlgemeenTakkenIcn, gridBagConstraints);

        txtAlgemeenTakkenFilter.setToolTipText("Filter");
        txtAlgemeenTakkenFilter.setName("txtAlgemeenLedenFilter"); // NOI18N
        txtAlgemeenTakkenFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAlgemeenTakkenFilterKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 130, 0, 130);
        pnlAlgemeenTakken.add(txtAlgemeenTakkenFilter, gridBagConstraints);

        tblAlgemeenTakken.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblAlgemeenTakken.setName("tblAlgemeenLeden"); // NOI18N
        tblAlgemeenTakken.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAlgemeenTakkenMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblAlgemeenTakken);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlAlgemeenTakken.add(jScrollPane4, gridBagConstraints);

        ContentView.add(pnlAlgemeenTakken, "pnlAlgemeenTakken");

        pnlAlgemeenKalender.setName("pnlAlgemeenKalender"); // NOI18N
        pnlAlgemeenKalender.setLayout(new java.awt.GridBagLayout());

        lblAlgemeenKalenderHeader.setText("Kalender");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlAlgemeenKalender.add(lblAlgemeenKalenderHeader, gridBagConstraints);

        lblAlgemeenKalenderContent.setText("Deze feature is door een gebrek aan tijd weggelaten.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlAlgemeenKalender.add(lblAlgemeenKalenderContent, gridBagConstraints);

        ContentView.add(pnlAlgemeenKalender, "pnlAlgemeenKalender");

        pnlAlgemeenMateriaal.setName("pnlAlgemeenMateriaal"); // NOI18N
        pnlAlgemeenMateriaal.setLayout(new java.awt.GridBagLayout());

        lblAlgemeenMateriaalHeader.setText("Materiaal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlAlgemeenMateriaal.add(lblAlgemeenMateriaalHeader, gridBagConstraints);

        lblAlgemeenMateriaalContent.setText("Deze feature is door een gebrek aan tijd weggelaten.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlAlgemeenMateriaal.add(lblAlgemeenMateriaalContent, gridBagConstraints);

        ContentView.add(pnlAlgemeenMateriaal, "pnlAlgemeenMateriaal");

        pnlAlgemeenFinancien.setName("pnlAlgemeenFinancien"); // NOI18N
        pnlAlgemeenFinancien.setLayout(new java.awt.GridBagLayout());

        lblAlgemeenFinancienHeader.setText("Financien");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlAlgemeenFinancien.add(lblAlgemeenFinancienHeader, gridBagConstraints);

        lblAlgemeenFinancienContent.setText("Deze feature is door een gebrek aan tijd weggelaten.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlAlgemeenFinancien.add(lblAlgemeenFinancienContent, gridBagConstraints);

        ContentView.add(pnlAlgemeenFinancien, "pnlAlgemeenFinancien");

        pnlTakLeden.setLayout(new java.awt.GridBagLayout());

        lblTakLedenHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTakLedenHeader.setText("Leden");
        lblTakLedenHeader.setName("lblTakLedenHeader"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        pnlTakLeden.add(lblTakLedenHeader, gridBagConstraints);

        pnlTakLedenIcn.setLayout(new javax.swing.BoxLayout(pnlTakLedenIcn, javax.swing.BoxLayout.X_AXIS));

        icnTakLedenReload.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icnTakLedenReload.setAlignmentX(0.5F);
        icnTakLedenReload.setName("icnTakLedenReload"); // NOI18N
        icnTakLedenReload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                icnTakLedenReloadMouseReleased(evt);
            }
        });
        pnlTakLedenIcn.add(icnTakLedenReload);

        icnTakLedenCreate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icnTakLedenCreate.setAlignmentX(0.5F);
        icnTakLedenCreate.setName("icnTakLedenCreate"); // NOI18N
        icnTakLedenCreate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                icnTakLedenCreateMouseReleased(evt);
            }
        });
        pnlTakLedenIcn.add(icnTakLedenCreate);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        pnlTakLeden.add(pnlTakLedenIcn, gridBagConstraints);

        txtTakLedenFilter.setToolTipText("Filter");
        txtTakLedenFilter.setName("txtTakLedenFilter"); // NOI18N
        txtTakLedenFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTakLedenFilterKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 130, 0, 130);
        pnlTakLeden.add(txtTakLedenFilter, gridBagConstraints);

        tblTakLeden.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblTakLeden.setName("tblTakLeden"); // NOI18N
        tblTakLeden.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTakLedenMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblTakLeden);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlTakLeden.add(jScrollPane5, gridBagConstraints);

        ContentView.add(pnlTakLeden, "pnlTakLeden");

        pnlTakVergaderingen.setName("pnlTakVergaderingen"); // NOI18N
        pnlTakVergaderingen.setLayout(new java.awt.GridBagLayout());

        lblTakVergaderingenHeader.setText("Vergaderingen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlTakVergaderingen.add(lblTakVergaderingenHeader, gridBagConstraints);

        lblTakVergaderingenContent.setText("Deze feature is door een gebrek aan tijd weggelaten.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlTakVergaderingen.add(lblTakVergaderingenContent, gridBagConstraints);

        ContentView.add(pnlTakVergaderingen, "pnlTakVergaderingen");

        pnlTakAanwezigheden.setName("pnlTakAanwezigheden"); // NOI18N
        pnlTakAanwezigheden.setLayout(new java.awt.GridBagLayout());

        lblTakAanwezighedenHeader.setText("Aanwezigheden");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlTakAanwezigheden.add(lblTakAanwezighedenHeader, gridBagConstraints);

        lblTakAanwezighedenContent.setText("Deze feature is door een gebrek aan tijd weggelaten.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlTakAanwezigheden.add(lblTakAanwezighedenContent, gridBagConstraints);

        ContentView.add(pnlTakAanwezigheden, "pnlTakAanwezigheden");

        pnlTakTakkas.setName("pnlTakTakkas"); // NOI18N
        pnlTakTakkas.setLayout(new java.awt.GridBagLayout());

        lblTakTakkasHeader.setText("Takkas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlTakTakkas.add(lblTakTakkasHeader, gridBagConstraints);

        lblTakTakkasContent.setText("Deze feature is door een gebrek aan tijd weggelaten.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlTakTakkas.add(lblTakTakkasContent, gridBagConstraints);

        ContentView.add(pnlTakTakkas, "pnlTakTakkas");

        pnlAlgemeenLeiding.setLayout(new java.awt.GridBagLayout());

        lblAlgemeenLeidingHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAlgemeenLeidingHeader.setText("Leiding");
        lblAlgemeenLeidingHeader.setName("lblAlgemeenLeidingHeader"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        pnlAlgemeenLeiding.add(lblAlgemeenLeidingHeader, gridBagConstraints);

        pnlAlgemeenLeidingIcn.setLayout(new javax.swing.BoxLayout(pnlAlgemeenLeidingIcn, javax.swing.BoxLayout.X_AXIS));

        icnAlgemeenLeidingReload.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icnAlgemeenLeidingReload.setAlignmentX(0.5F);
        icnAlgemeenLeidingReload.setName("icnAlgemeenLeidingReload"); // NOI18N
        icnAlgemeenLeidingReload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                icnAlgemeenLeidingReloadMouseReleased(evt);
            }
        });
        pnlAlgemeenLeidingIcn.add(icnAlgemeenLeidingReload);

        icnAlgemeenLeidingCreate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icnAlgemeenLeidingCreate.setAlignmentX(0.5F);
        icnAlgemeenLeidingCreate.setName("icnAlgemeenLeidingCreate"); // NOI18N
        icnAlgemeenLeidingCreate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                icnAlgemeenLeidingCreateMouseReleased(evt);
            }
        });
        pnlAlgemeenLeidingIcn.add(icnAlgemeenLeidingCreate);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        pnlAlgemeenLeiding.add(pnlAlgemeenLeidingIcn, gridBagConstraints);

        txtAlgemeenLeidingFilter.setToolTipText("Filter");
        txtAlgemeenLeidingFilter.setName("txtAlgemeenLeidingFilter"); // NOI18N
        txtAlgemeenLeidingFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAlgemeenLeidingFilterKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 130, 0, 130);
        pnlAlgemeenLeiding.add(txtAlgemeenLeidingFilter, gridBagConstraints);

        tblAlgemeenLeiding.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblAlgemeenLeiding.setName("tblAlgemeenLeiding"); // NOI18N
        tblAlgemeenLeiding.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAlgemeenLeidingMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblAlgemeenLeiding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlAlgemeenLeiding.add(jScrollPane2, gridBagConstraints);

        ContentView.add(pnlAlgemeenLeiding, "pnlAlgemeenLeiding");

        pnlLeidingRaden.setName("pnlLeidingRaden"); // NOI18N
        pnlLeidingRaden.setLayout(new java.awt.GridBagLayout());

        lblLeidingRadenHeader.setText("Raden");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlLeidingRaden.add(lblLeidingRadenHeader, gridBagConstraints);

        lblLeidingRadenContent.setText("Deze feature is door een gebrek aan tijd weggelaten.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlLeidingRaden.add(lblLeidingRadenContent, gridBagConstraints);

        ContentView.add(pnlLeidingRaden, "pnlLeidingRaden");

        pnlLeidingActiviteiten.setName("pnlLeidingActiviteiten"); // NOI18N
        pnlLeidingActiviteiten.setLayout(new java.awt.GridBagLayout());

        lblLeidingActiviteitenHeader.setText("Activiteiten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlLeidingActiviteiten.add(lblLeidingActiviteitenHeader, gridBagConstraints);

        lblLeidingActiviteitenContent.setText("Deze feature is door een gebrek aan tijd weggelaten.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlLeidingActiviteiten.add(lblLeidingActiviteitenContent, gridBagConstraints);

        ContentView.add(pnlLeidingActiviteiten, "pnlLeidingActiviteiten");

        pnlInstellingenImporteren.setName("pnlInstellingenImporteren"); // NOI18N
        pnlInstellingenImporteren.setLayout(new java.awt.GridLayout(0, 2));

        pnlInstellingenImporteren1.setName("pnlInstellingenImporteren"); // NOI18N
        pnlInstellingenImporteren1.setLayout(new java.awt.GridBagLayout());

        lblInstellingenImporterenHeader.setText("Importeren");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlInstellingenImporteren1.add(lblInstellingenImporterenHeader, gridBagConstraints);

        lblStep1.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        lblStep1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblStep1.setText("1. Selecteer een CSV-bestand");
        lblStep1.setName("lblStep1"); // NOI18N
        lblStep1.setPreferredSize(new java.awt.Dimension(250, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlInstellingenImporteren1.add(lblStep1, gridBagConstraints);

        btnFileChooser.setText("Selecteer een bestand");
        btnFileChooser.setName("btnFileChooser"); // NOI18N
        btnFileChooser.setPreferredSize(new java.awt.Dimension(230, 40));
        btnFileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileChooserActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlInstellingenImporteren1.add(btnFileChooser, gridBagConstraints);

        lblStep2.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        lblStep2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblStep2.setText("2. Start het importeren");
        lblStep2.setName("lblStep2"); // NOI18N
        lblStep2.setPreferredSize(new java.awt.Dimension(250, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlInstellingenImporteren1.add(lblStep2, gridBagConstraints);

        btnStartImport.setText("Start!");
        btnStartImport.setEnabled(false);
        btnStartImport.setName("btnStartImport"); // NOI18N
        btnStartImport.setPreferredSize(new java.awt.Dimension(230, 40));
        btnStartImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartImportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pnlInstellingenImporteren1.add(btnStartImport, gridBagConstraints);

        pbrImporteren.setForeground(Styles.BIJNAZWART);
        pbrImporteren.setPreferredSize(new java.awt.Dimension(146, 25));
        pbrImporteren.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 400, 0);
        pnlInstellingenImporteren1.add(pbrImporteren, gridBagConstraints);

        pnlInstellingenImporteren.add(pnlInstellingenImporteren1);

        pnlInstellingenImporteren2.setName("pnlInstellingenImporteren"); // NOI18N
        pnlInstellingenImporteren2.setLayout(new java.awt.GridLayout(1, 0));

        lstImports.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstImports.setName("lstImports"); // NOI18N
        jScrollPaneImport.setViewportView(lstImports);

        pnlInstellingenImporteren2.add(jScrollPaneImport);

        pnlInstellingenImporteren.add(pnlInstellingenImporteren2);

        ContentView.add(pnlInstellingenImporteren, "pnlInstellingenImporteren");

        pnlInstellingenVoorkeuren.setName("pnlInstellingenVoorkeuren"); // NOI18N
        pnlInstellingenVoorkeuren.setLayout(new java.awt.GridBagLayout());

        lblInstellingenVoorkeurenHeader.setText("Voorkeuren");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 10.0;
        pnlInstellingenVoorkeuren.add(lblInstellingenVoorkeurenHeader, gridBagConstraints);

        lblVoorkeurenVoornaam.setText("Voornaam:");
        lblVoorkeurenVoornaam.setName("lblVoorkeurenVoornaam"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlInstellingenVoorkeuren.add(lblVoorkeurenVoornaam, gridBagConstraints);

        lblVoorkeurenAchternaam.setText("Achternaam:");
        lblVoorkeurenAchternaam.setName("lblVoorkeurenAchternaam"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlInstellingenVoorkeuren.add(lblVoorkeurenAchternaam, gridBagConstraints);

        lblVoorkeurenTak.setText("Mijn tak:");
        lblVoorkeurenTak.setName("lblVoorkeurenTak"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlInstellingenVoorkeuren.add(lblVoorkeurenTak, gridBagConstraints);

        lblVoorkeurenLeiding.setText("Ik ben");
        lblVoorkeurenLeiding.setToolTipText("");
        lblVoorkeurenLeiding.setName("lblVoorkeurenLeiding"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlInstellingenVoorkeuren.add(lblVoorkeurenLeiding, gridBagConstraints);

        btnVoorkeurenLeiding.setText("Lid");
        btnVoorkeurenLeiding.setToolTipText("");
        btnVoorkeurenLeiding.setActionCommand("Ik ben lid");
        btnVoorkeurenLeiding.setName("btnVoorkeurenLeiding"); // NOI18N
        btnVoorkeurenLeiding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoorkeurenLeidingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        pnlInstellingenVoorkeuren.add(btnVoorkeurenLeiding, gridBagConstraints);

        txtVoorkeurenVoornaam.setText("Testje");
        txtVoorkeurenVoornaam.setName("txtVoorkeurenVoornaam"); // NOI18N
        txtVoorkeurenVoornaam.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVoorkeurenVoornaamFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        pnlInstellingenVoorkeuren.add(txtVoorkeurenVoornaam, gridBagConstraints);

        txtVoorkeurenAchternaam.setName("txtVoorkeurenAchternaam"); // NOI18N
        txtVoorkeurenAchternaam.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVoorkeurenAchternaamFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        pnlInstellingenVoorkeuren.add(txtVoorkeurenAchternaam, gridBagConstraints);

        lstVoorkeurenTak.setName("lstVoorkeurenTak"); // NOI18N
        lstVoorkeurenTak.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstVoorkeurenTakValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(lstVoorkeurenTak);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        pnlInstellingenVoorkeuren.add(jScrollPane3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 230, 0);
        pnlInstellingenVoorkeuren.add(jSeparator1, gridBagConstraints);

        ContentView.add(pnlInstellingenVoorkeuren, "pnlInstellingenVoorkeuren");

        SplitPane.setRightComponent(ContentView);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SplitPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SplitPane)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblAlgemeenLedenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenLedenMouseReleased
        ContentViewLayout.show(ContentView, "pnlAlgemeenLeden");
    }//GEN-LAST:event_lblAlgemeenLedenMouseReleased

    private void lblAlgemeenKalenderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenKalenderMouseReleased
        ContentViewLayout.show(ContentView, "pnlAlgemeenKalender");
    }//GEN-LAST:event_lblAlgemeenKalenderMouseReleased

    private void lblAlgemeenMateriaalMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenMateriaalMouseReleased
        ContentViewLayout.show(ContentView, "pnlAlgemeenMateriaal");
    }//GEN-LAST:event_lblAlgemeenMateriaalMouseReleased

    private void lblAlgemeenFinancienMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenFinancienMouseReleased
        ContentViewLayout.show(ContentView, "pnlAlgemeenFinancien");
    }//GEN-LAST:event_lblAlgemeenFinancienMouseReleased

    private void lblTakLedenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakLedenMouseReleased
        ContentViewLayout.show(ContentView, "pnlTakLeden");
    }//GEN-LAST:event_lblTakLedenMouseReleased

    private void lblTakVergaderingenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakVergaderingenMouseReleased
        ContentViewLayout.show(ContentView, "pnlTakVergaderingen");
    }//GEN-LAST:event_lblTakVergaderingenMouseReleased

    private void lblTakAanwezighedenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakAanwezighedenMouseReleased
        ContentViewLayout.show(ContentView, "pnlTakAanwezigheden");
    }//GEN-LAST:event_lblTakAanwezighedenMouseReleased

    private void lblTakTakkasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakTakkasMouseReleased
        ContentViewLayout.show(ContentView, "pnlTakTakkas");
    }//GEN-LAST:event_lblTakTakkasMouseReleased

    private void lblLeidingLeidingMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeidingLeidingMouseReleased
        ContentViewLayout.show(ContentView, "pnlAlgemeenLeiding");
    }//GEN-LAST:event_lblLeidingLeidingMouseReleased

    private void lblLeidingRadenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeidingRadenMouseReleased
        ContentViewLayout.show(ContentView, "pnlLeidingRaden");
    }//GEN-LAST:event_lblLeidingRadenMouseReleased

    private void lblLeidingActiviteitenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeidingActiviteitenMouseReleased
        ContentViewLayout.show(ContentView, "pnlLeidingActiviteiten");
    }//GEN-LAST:event_lblLeidingActiviteitenMouseReleased

    private void lblAlgemeenLedenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenLedenMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenLedenMouseEntered

    private void lblAlgemeenLedenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenLedenMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenLedenMouseExited

    private void lblAlgemeenKalenderMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenKalenderMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenKalenderMouseEntered

    private void lblAlgemeenKalenderMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenKalenderMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenKalenderMouseExited

    private void lblAlgemeenMateriaalMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenMateriaalMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenMateriaalMouseEntered

    private void lblAlgemeenMateriaalMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenMateriaalMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenMateriaalMouseExited

    private void lblAlgemeenFinancienMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenFinancienMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenFinancienMouseEntered

    private void lblAlgemeenFinancienMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenFinancienMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenFinancienMouseExited

    private void lblTakLedenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakLedenMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblTakLedenMouseEntered

    private void lblTakLedenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakLedenMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblTakLedenMouseExited

    private void lblTakVergaderingenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakVergaderingenMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblTakVergaderingenMouseEntered

    private void lblTakVergaderingenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakVergaderingenMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblTakVergaderingenMouseExited

    private void lblTakAanwezighedenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakAanwezighedenMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblTakAanwezighedenMouseEntered

    private void lblTakAanwezighedenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakAanwezighedenMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblTakAanwezighedenMouseExited

    private void lblTakTakkasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakTakkasMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblTakTakkasMouseEntered

    private void lblTakTakkasMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTakTakkasMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblTakTakkasMouseExited

    private void lblLeidingLeidingMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeidingLeidingMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblLeidingLeidingMouseEntered

    private void lblLeidingLeidingMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeidingLeidingMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblLeidingLeidingMouseExited

    private void lblLeidingRadenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeidingRadenMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblLeidingRadenMouseEntered

    private void lblLeidingRadenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeidingRadenMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblLeidingRadenMouseExited

    private void lblLeidingActiviteitenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeidingActiviteitenMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblLeidingActiviteitenMouseEntered

    private void lblLeidingActiviteitenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeidingActiviteitenMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblLeidingActiviteitenMouseExited

    private void lblInstellingenImporterenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInstellingenImporterenMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblInstellingenImporterenMouseEntered

    private void lblInstellingenImporterenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInstellingenImporterenMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblInstellingenImporterenMouseExited

    private void lblInstellingenImporterenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInstellingenImporterenMouseReleased
        if(this.parser == null) {
            this.parser = new CSVParser(this);
        }
        ContentViewLayout.show(ContentView, "pnlInstellingenImporteren");
    }//GEN-LAST:event_lblInstellingenImporterenMouseReleased

    private void btnFileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileChooserActionPerformed
        parser.chooseFile();
        btnStartImport.setEnabled(parser.hasSelectedFile());
    }//GEN-LAST:event_btnFileChooserActionPerformed

    private void btnStartImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartImportActionPerformed
        btnFileChooser.setEnabled(false);
        btnStartImport.setEnabled(false);
        importListModel.clear();
        
        parser.readFile();        
    }//GEN-LAST:event_btnStartImportActionPerformed

    private void txtAlgemeenLedenFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAlgemeenLedenFilterKeyReleased
        setFilter(tblAlgemeenLeden, txtAlgemeenLedenFilter.getText());
    }//GEN-LAST:event_txtAlgemeenLedenFilterKeyReleased

    private void icnAlgemeenLedenReloadMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icnAlgemeenLedenReloadMouseReleased
        reloadData();
    }//GEN-LAST:event_icnAlgemeenLedenReloadMouseReleased

    private void icnAlgemeenLeidingReloadMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icnAlgemeenLeidingReloadMouseReleased
        reloadData();
    }//GEN-LAST:event_icnAlgemeenLeidingReloadMouseReleased

    private void txtAlgemeenLeidingFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAlgemeenLeidingFilterKeyReleased
        setFilter(tblAlgemeenLeiding, txtAlgemeenLeidingFilter.getText());
    }//GEN-LAST:event_txtAlgemeenLeidingFilterKeyReleased

    private void lblInstellingenVoorkeurenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInstellingenVoorkeurenMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblInstellingenVoorkeurenMouseEntered

    private void lblInstellingenVoorkeurenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInstellingenVoorkeurenMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblInstellingenVoorkeurenMouseExited

    private void lblInstellingenVoorkeurenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInstellingenVoorkeurenMouseReleased
        reloadLists();
        loadPreferences();
        ContentViewLayout.show(ContentView, "pnlInstellingenVoorkeuren");
    }//GEN-LAST:event_lblInstellingenVoorkeurenMouseReleased

    private void lstVoorkeurenTakValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstVoorkeurenTakValueChanged
        if (lstVoorkeurenTak.getSelectedIndices().length > 0) {
            prefs.putInt("tak", lstVoorkeurenTak.getSelectedIndex() + 1);
            reloadTables();
        }
    }//GEN-LAST:event_lstVoorkeurenTakValueChanged

    private void txtVoorkeurenVoornaamFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVoorkeurenVoornaamFocusLost
        prefs.put("voornaam", txtVoorkeurenVoornaam.getText());
    }//GEN-LAST:event_txtVoorkeurenVoornaamFocusLost

    private void txtVoorkeurenAchternaamFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVoorkeurenAchternaamFocusLost
        prefs.put("achternaam", txtVoorkeurenAchternaam.getText());
    }//GEN-LAST:event_txtVoorkeurenAchternaamFocusLost

    private void btnVoorkeurenLeidingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoorkeurenLeidingActionPerformed
        prefs.putBoolean("isleiding", btnVoorkeurenLeiding.isSelected());        
        loadPreferences();
    }//GEN-LAST:event_btnVoorkeurenLeidingActionPerformed

    private void icnTakLedenReloadMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icnTakLedenReloadMouseReleased
        reloadData();
    }//GEN-LAST:event_icnTakLedenReloadMouseReleased

    private void txtTakLedenFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTakLedenFilterKeyReleased
        setFilter(tblTakLeden, txtTakLedenFilter.getText());
    }//GEN-LAST:event_txtTakLedenFilterKeyReleased

    private void icnAlgemeenLedenCreateMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icnAlgemeenLedenCreateMouseReleased
        newPerson();
    }//GEN-LAST:event_icnAlgemeenLedenCreateMouseReleased

    private void tblAlgemeenLedenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAlgemeenLedenMouseClicked
        tableRowClickHandler(evt);
    }//GEN-LAST:event_tblAlgemeenLedenMouseClicked

    private void tblTakLedenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTakLedenMouseClicked
        tableRowClickHandler(evt);
    }//GEN-LAST:event_tblTakLedenMouseClicked

    private void tblAlgemeenLeidingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAlgemeenLeidingMouseClicked
        tableRowClickHandler(evt);
    }//GEN-LAST:event_tblAlgemeenLeidingMouseClicked

    private void lblAlgemeenTakkenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenTakkenMouseEntered
        setLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenTakkenMouseEntered

    private void lblAlgemeenTakkenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenTakkenMouseExited
        removeLabelFocus((JLabel)evt.getSource());
    }//GEN-LAST:event_lblAlgemeenTakkenMouseExited

    private void lblAlgemeenTakkenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAlgemeenTakkenMouseReleased
        ContentViewLayout.show(ContentView, "pnlAlgemeenTakken");
    }//GEN-LAST:event_lblAlgemeenTakkenMouseReleased

    private void icnAlgemeenTakkenReloadMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icnAlgemeenTakkenReloadMouseReleased
        reloadData();
    }//GEN-LAST:event_icnAlgemeenTakkenReloadMouseReleased

    private void icnAlgemeenTakkenCreateMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icnAlgemeenTakkenCreateMouseReleased
        newTak();
    }//GEN-LAST:event_icnAlgemeenTakkenCreateMouseReleased

    private void txtAlgemeenTakkenFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAlgemeenTakkenFilterKeyReleased
        setFilter(tblAlgemeenTakken, txtAlgemeenTakkenFilter.getText());
    }//GEN-LAST:event_txtAlgemeenTakkenFilterKeyReleased

    private void tblAlgemeenTakkenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAlgemeenTakkenMouseClicked
        tableRowClickHandler(evt);
    }//GEN-LAST:event_tblAlgemeenTakkenMouseClicked

    private void icnTakLedenCreateMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icnTakLedenCreateMouseReleased
        Lid lid = new Lid();
        lid.setTakID(prefs.getInt("tak", 0));
        editPerson(lid);
    }//GEN-LAST:event_icnTakLedenCreateMouseReleased

    private void icnAlgemeenLeidingCreateMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_icnAlgemeenLeidingCreateMouseReleased
        Lid lid = new Lid();
        lid.setTakID(1);
        editPerson(lid);
    }//GEN-LAST:event_icnAlgemeenLeidingCreateMouseReleased
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WerkstukGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WerkstukGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WerkstukGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WerkstukGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Open splash screen */
        Splash.init();
        
        /* Load data */
        ArrayList<Lid> initLeden;
        ArrayList<Lid> initLeiding;
        ArrayList<Tak> initTakken;
        
        Splash.setText("Laden van leden en contacten");
        initLeden = LidDAO.getLeden(LidDAO.FETCH_ALL, 75);

        Splash.setText("Laden van leiding en contacten");
        initLeiding = LeidingDAO.getLeiding(LeidingDAO.FETCH_ALL, 8);

        Splash.setText("Laden van takken en hun leiding");
        initTakken = TakDAO.getTakken(TakDAO.FETCH_ALL, 7);
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new WerkstukGUI(initLeden, initLeiding, initTakken).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ContentView;
    private javax.swing.JPanel Sidebar;
    private javax.swing.JSplitPane SplitPane;
    private javax.swing.JButton btnFileChooser;
    private javax.swing.JButton btnStartImport;
    private javax.swing.JToggleButton btnVoorkeurenLeiding;
    private javax.swing.JLabel icnAlgemeenLedenCreate;
    private javax.swing.JLabel icnAlgemeenLedenReload;
    private javax.swing.JLabel icnAlgemeenLeidingCreate;
    private javax.swing.JLabel icnAlgemeenLeidingReload;
    private javax.swing.JLabel icnAlgemeenTakkenCreate;
    private javax.swing.JLabel icnAlgemeenTakkenReload;
    private javax.swing.JLabel icnTakLedenCreate;
    private javax.swing.JLabel icnTakLedenReload;
    private javax.swing.JLabel imgProfileImage;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPaneImport;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAlgemeen;
    private javax.swing.JLabel lblAlgemeenFinancien;
    private javax.swing.JLabel lblAlgemeenFinancienContent;
    private javax.swing.JLabel lblAlgemeenFinancienHeader;
    private javax.swing.JLabel lblAlgemeenKalender;
    private javax.swing.JLabel lblAlgemeenKalenderContent;
    private javax.swing.JLabel lblAlgemeenKalenderHeader;
    private javax.swing.JLabel lblAlgemeenLeden;
    private javax.swing.JLabel lblAlgemeenLedenHeader;
    private javax.swing.JLabel lblAlgemeenLeidingHeader;
    private javax.swing.JLabel lblAlgemeenMateriaal;
    private javax.swing.JLabel lblAlgemeenMateriaalContent;
    private javax.swing.JLabel lblAlgemeenMateriaalHeader;
    private javax.swing.JLabel lblAlgemeenTakken;
    private javax.swing.JLabel lblAlgemeenTakkenHeader;
    private javax.swing.JLabel lblInstellingen;
    private javax.swing.JLabel lblInstellingenImporteren;
    private javax.swing.JLabel lblInstellingenImporterenHeader;
    private javax.swing.JLabel lblInstellingenVoorkeuren;
    private javax.swing.JLabel lblInstellingenVoorkeurenHeader;
    private javax.swing.JLabel lblLeiding;
    private javax.swing.JLabel lblLeidingActiviteiten;
    private javax.swing.JLabel lblLeidingActiviteitenContent;
    private javax.swing.JLabel lblLeidingActiviteitenHeader;
    private javax.swing.JLabel lblLeidingLeiding;
    private javax.swing.JLabel lblLeidingRaden;
    private javax.swing.JLabel lblLeidingRadenContent;
    private javax.swing.JLabel lblLeidingRadenHeader;
    private javax.swing.JLabel lblStep1;
    private javax.swing.JLabel lblStep2;
    private javax.swing.JLabel lblTak;
    private javax.swing.JLabel lblTakAanwezigheden;
    private javax.swing.JLabel lblTakAanwezighedenContent;
    private javax.swing.JLabel lblTakAanwezighedenHeader;
    private javax.swing.JLabel lblTakLeden;
    private javax.swing.JLabel lblTakLedenHeader;
    private javax.swing.JLabel lblTakTakkas;
    private javax.swing.JLabel lblTakTakkasContent;
    private javax.swing.JLabel lblTakTakkasHeader;
    private javax.swing.JLabel lblTakVergaderingen;
    private javax.swing.JLabel lblTakVergaderingenContent;
    private javax.swing.JLabel lblTakVergaderingenHeader;
    private javax.swing.JLabel lblVoorkeurenAchternaam;
    private javax.swing.JLabel lblVoorkeurenLeiding;
    private javax.swing.JLabel lblVoorkeurenTak;
    private javax.swing.JLabel lblVoorkeurenVoornaam;
    private javax.swing.JList lstImports;
    private javax.swing.JList lstVoorkeurenTak;
    private javax.swing.JProgressBar pbrImporteren;
    private javax.swing.JPanel pnlAlgemeenFinancien;
    private javax.swing.JPanel pnlAlgemeenKalender;
    private javax.swing.JPanel pnlAlgemeenLeden;
    private javax.swing.JPanel pnlAlgemeenLedenIcn;
    private javax.swing.JPanel pnlAlgemeenLeiding;
    private javax.swing.JPanel pnlAlgemeenLeidingIcn;
    private javax.swing.JPanel pnlAlgemeenMateriaal;
    private javax.swing.JPanel pnlAlgemeenTakken;
    private javax.swing.JPanel pnlAlgemeenTakkenIcn;
    private javax.swing.JPanel pnlHome;
    private javax.swing.JPanel pnlInstellingenImporteren;
    private javax.swing.JPanel pnlInstellingenImporteren1;
    private javax.swing.JPanel pnlInstellingenImporteren2;
    private javax.swing.JPanel pnlInstellingenVoorkeuren;
    private javax.swing.JPanel pnlLeidingActiviteiten;
    private javax.swing.JPanel pnlLeidingRaden;
    private javax.swing.JPanel pnlTakAanwezigheden;
    private javax.swing.JPanel pnlTakLeden;
    private javax.swing.JPanel pnlTakLedenIcn;
    private javax.swing.JPanel pnlTakTakkas;
    private javax.swing.JPanel pnlTakVergaderingen;
    private javax.swing.JTable tblAlgemeenLeden;
    private javax.swing.JTable tblAlgemeenLeiding;
    private javax.swing.JTable tblAlgemeenTakken;
    private javax.swing.JTable tblTakLeden;
    private javax.swing.JTextField txtAlgemeenLedenFilter;
    private javax.swing.JTextField txtAlgemeenLeidingFilter;
    private javax.swing.JTextField txtAlgemeenTakkenFilter;
    private javax.swing.JTextField txtTakLedenFilter;
    private javax.swing.JTextField txtVoorkeurenAchternaam;
    private javax.swing.JTextField txtVoorkeurenVoornaam;
    // End of variables declaration//GEN-END:variables
}
