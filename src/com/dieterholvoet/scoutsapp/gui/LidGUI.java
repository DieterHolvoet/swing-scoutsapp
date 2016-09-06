/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dieterholvoet.scoutsapp.gui;

import com.dieterholvoet.scoutsapp.util.Styles;
import com.dieterholvoet.scoutsapp.util.HelperFunctions;
import com.dieterholvoet.scoutsapp.model.Lid;
import com.dieterholvoet.scoutsapp.model.Tak;
import com.dieterholvoet.scoutsapp.swingmodel.ContactPersonenTableModel;
import com.michaelbaranov.microba.calendar.DatePicker;
import com.sun.glass.events.KeyEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Dieter
 */
public class LidGUI extends javax.swing.JDialog {
    private final ArrayList<Tak> takken;
    private final Lid lid;
    private DefaultListModel listModel;
    private final WerkstukGUI parent;
    private final String title;
    
    public LidGUI(WerkstukGUI parent) {
        super(parent, "Lid toevoegen", ModalityType.MODELESS);
        this.title = "Lid toevoegen";
        this.parent = parent;
        this.lid = new Lid();
        this.takken = new ArrayList<>();
        init();
    }
    
    public LidGUI(WerkstukGUI parent, ArrayList<Tak> takken, String title) {
        super(parent, title, ModalityType.MODELESS);
        this.title = title;
        this.lid = new Lid();
        this.takken = takken;
        this.parent = parent;
        init();
    }

    public LidGUI(WerkstukGUI parent, ArrayList<Tak> takken, Lid lid, String title) {
        super(parent, title, ModalityType.MODELESS);
        this.title = title;
        this.lid = lid;
        this.takken = takken;
        this.parent = parent;
        init();
    }
    
    private void init() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel content = (JPanel) this.getContentPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        
        
        content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "escape");
        content.getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Frame frame : Frame.getFrames()) {
                    if(frame.getClass().equals(LidGUI.class)) {
                        frame.dispose();
                    }
                }
            }
        });
        
        initComponents();
        setStyles();
        loadLists();
        loadTables();
        setInitValues();
    }
    
    public void loadLists() {
        listModel = new DefaultListModel();
        listModel.clear();
        lstTakken.setModel(listModel);
        lstTakken.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION); 
        
        for(Tak tak : takken) {
            listModel.addElement(tak);
        }
    }
    
    public void loadTables() {
        tblContactPersonen.setModel(new ContactPersonenTableModel(lid.getContactpersonen()));
        tblContactPersonen.setAutoCreateRowSorter(true);
    }
    
    private boolean checkInput() {
        for(Component component : HelperFunctions.getAllComponents(this)) {
            if(component instanceof JTextField) {
                JTextField text = (JTextField) component;
                String name = HelperFunctions.getComponentVariableName(component);
                
                switch(name) {
                    case "txtVoornaam":
                    case "txtAchternaam":
                    case "txtLidnummer":
                        if(text.getText().equals("")) {
                            return false;
                        }
                }
            }
        }
        
        if(btngrpGeslacht.getSelection() == null) {
            return false;
        }
        
        if(dateGeboortedatum.getDate() == null) {
            return false;
        }
        
        if(lstTakken.getSelectedIndices().length == 0) {
            return false;
        }
        
        return true;
    }
    
    private void saveInput() {
        lid.setVoornaam(txtVoornaam.getText());
        lid.setAchternaam(txtAchternaam.getText());
        lid.setGsm(txtGSM.getText());
        lid.setTelefoon(txtTelefoon.getText());
        lid.setLidnr(Long.parseLong(txtLidnummer.getText()));
        lid.setTotem(txtTotem.getText());
        lid.setAdjectief(txtAdjectief.getText());
        lid.setAdres(txtAdres.getText());
        lid.setEmail(txtEmail.getText());
        
        if(rbtMan.isSelected()) {
            lid.setGeslacht("M");
            
        } else {
            lid.setGeslacht("V");
        }
        
        if (lstTakken.getSelectedIndices().length > 0) {
            Tak tak = (Tak) lstTakken.getSelectedValue();
            lid.setTakID(tak.getTakID());
        }
        
        lid.setGeboortedatum(new java.sql.Date(dateGeboortedatum.getDate().getTime()));
    }
    
    private void setInitValues() {
        lblHeader.setText(title);
        
        String voornaam = lid.getVoornaam() == null ? "" : lid.getVoornaam(),
               achternaam = lid.getAchternaam() == null ? "" : lid.getAchternaam(),
               gsm = lid.getGsm() == null ? "" : lid.getGsm(),
               telefoon = lid.getTelefoon() == null ? "" : lid.getTelefoon(),
               lidnr = lid.getLidnr() == 0 ? "" : Long.toString(lid.getLidnr()),
               totem = lid.getTotem() == null ? "" : lid.getTotem(),
               adjectief = lid.getAdjectief() == null ? "" : lid.getAdjectief(),
               adres = lid.getAdres() == null ? "" : lid.getAdres(),
               email = lid.getEmail() == null ? "" : lid.getEmail(),
               geslacht = lid.getGeslacht() == null ? "" : lid.getGeslacht();
        
        Date geboortedatum = lid.getGeboortedatum() == null ? new Date() : lid.getGeboortedatum();
        int takid = lid.getTakID() == 0 ? 0 : lid.getTakID();
                
        txtVoornaam.setText(voornaam);
        txtAchternaam.setText(achternaam);
        txtGSM.setText(gsm);
        txtTelefoon.setText(telefoon);
        txtLidnummer.setText(lidnr);
        txtTotem.setText(totem);
        txtAdjectief.setText(adjectief);
        txtAdres.setText(adres);
        txtEmail.setText(email);
        
        switch(geslacht) {
            case "M":
                rbtMan.setSelected(true);
                break;
                
            case "V":
                rbtVrouw.setSelected(true);
                break;
                
            default:
                break;
        }
        
        Calendar date = Calendar.getInstance();
        date.setTime(geboortedatum);
        
        try {
            dateGeboortedatum.setDate(geboortedatum);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(LidGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lstTakken.setSelectedIndex(takid - 1);
    }
    
    private void setStyles() {
        for (Component component : HelperFunctions.getAllComponents(this)) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                Dimension size = new Dimension(150, 40);
                
                switch(HelperFunctions.getComponentVariableName(label)) {
                    
                    // HEADER LABEL
                    case "lblHeader":
                        label.setFont(Styles.FjallaOne.getREGULAR(Styles.H1));
                        label.setHorizontalTextPosition(JLabel.CENTER);
                        label.setVerticalTextPosition(JLabel.BOTTOM);
                        label.setForeground(Styles.BIJNAZWART);
                        label.setBorder(new EmptyBorder(40, 0, 40, 0));
                        break;
                        
                    // REGULAR LABELS
                    case "lblVoornaam":
                    case "lblAchternaam":
                    case "lblGeslacht":
                    case "lblGeboortedatum":
                    case "lblTelefoon":
                    case "lblGSM":
                    case "lblLidnummer":
                    case "lblTotem":
                    case "lblAdjectief":
                    case "lblAdres":
                    case "lblEmail":
                    case "lblTakken":
                        label.setFont(Styles.OpenSans.getLIGHT(Styles.LABEL));
                        label.setMinimumSize(size);
                        label.setMaximumSize(size);
                        label.setPreferredSize(size);
                        break;
   
                    default:
                        break;
                }

            } else if(component instanceof JTextField) {
                JTextField text = (JTextField) component;
                Dimension size = new Dimension(200, 40);
                
                switch(HelperFunctions.getComponentVariableName(text)) {
                    case "txtVoornaam":
                    case "txtAchternaam":
                    case "txtTelefoon":
                    case "txtGSM":
                    case "txtLidnummer":
                    case "txtTotem":
                    case "txtAdjectief":
                    case "txtAdres":
                    case "txtEmail":
                        text.setMinimumSize(size);
                        text.setMaximumSize(size);
                        text.setPreferredSize(size);
                        break;

                    default:
                        break;
                }
                
            } else if(component instanceof JButton) {
                JButton button = (JButton) component;
                Dimension size = new Dimension(230, 40);
                
                switch(HelperFunctions.getComponentVariableName(button)) {
                    case "btnSubmit":
                        button.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        button.setText(button.getText().toUpperCase());
                        button.setMinimumSize(size);
                        button.setMaximumSize(size);
                        button.setPreferredSize(size);
                        break;

                    default:
                        break;
                }

            } else if(component instanceof JRadioButton) {
                JRadioButton radio = (JRadioButton) component;
                Dimension size = new Dimension(100, 40);
                
                switch(HelperFunctions.getComponentVariableName(radio)) {
                    case "rbtMan":
                    case "rbtVrouw":
                        radio.setMinimumSize(size);
                        radio.setMaximumSize(size);
                        radio.setPreferredSize(size);
                        break;

                    default:
                        break;
                }

            } else if(component instanceof DatePicker) {
                DatePicker date = (DatePicker) component;
                Dimension size = new Dimension(200, 40);
                
                switch(HelperFunctions.getComponentVariableName(date)) {
                    case "dateGeboortedatum":
                        date.setMinimumSize(size);
                        date.setMaximumSize(size);
                        date.setPreferredSize(size);
                        break;

                    default:
                        break;
                }

            } else if(component instanceof JScrollPane) {
                JScrollPane list = (JScrollPane) component;
                Dimension size = new Dimension(200, 200);
                
                switch(HelperFunctions.getComponentVariableName(list)) {
                    case "scrollPane":
                        list.setMinimumSize(size);
                        list.setPreferredSize(size);
                        break;

                    case "scrlContactPersonen":
                        list.setMinimumSize(new Dimension(200, 140));
                        break;
                        
                    default:
                        break;
                }
            } else if(component instanceof JList) {
                JList list = (JList) component;
                
                switch(HelperFunctions.getComponentVariableName(list)) {
                    case "lstTakken":
                        list.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        list.setFixedCellHeight(30);
                        break;

                    default:
                        break;
                }
                
            } else if(component instanceof JTable) {
                JTable table = (JTable) component;
                Dimension size = new Dimension(200, 200);
                
                switch(HelperFunctions.getComponentVariableName(table)) {
                    case "tblContactPersonen":
                        table.setFont(Styles.OpenSans.getLIGHT(Styles.P));
                        table.setRowHeight(30);
                        table.setMinimumSize(new Dimension(200, 140));
                        break;

                    default:
                        break;
                }
            }   
        }
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

        btngrpGeslacht = new javax.swing.ButtonGroup();
        lblHeader = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lblVoornaam = new javax.swing.JLabel();
        lblAchternaam = new javax.swing.JLabel();
        lblGeslacht = new javax.swing.JLabel();
        lblGeboortedatum = new javax.swing.JLabel();
        dateGeboortedatum = new com.michaelbaranov.microba.calendar.DatePicker();
        txtVoornaam = new javax.swing.JTextField();
        txtAchternaam = new javax.swing.JTextField();
        rbtMan = new javax.swing.JRadioButton();
        rbtVrouw = new javax.swing.JRadioButton();
        lblLidnummer = new javax.swing.JLabel();
        txtLidnummer = new javax.swing.JTextField();
        lblTotem = new javax.swing.JLabel();
        txtTotem = new javax.swing.JTextField();
        lblAdjectief = new javax.swing.JLabel();
        txtAdjectief = new javax.swing.JTextField();
        lblTelefoon = new javax.swing.JLabel();
        txtTelefoon = new javax.swing.JTextField();
        lblGSM = new javax.swing.JLabel();
        txtGSM = new javax.swing.JTextField();
        lblAdres = new javax.swing.JLabel();
        txtAdres = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        lblTakken = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        lstTakken = new javax.swing.JList();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        scrlContactPersonen = new javax.swing.JScrollPane();
        tblContactPersonen = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(214, 217, 223));
        setMinimumSize(new java.awt.Dimension(1000, 800));
        setName("LidFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 800));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("Lid toevoegen");
        lblHeader.setName("lblHeader"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(lblHeader, gridBagConstraints);

        jPanel5.setPreferredSize(new java.awt.Dimension(3000, 500));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        lblVoornaam.setText("Voornaam");
        lblVoornaam.setName("lblVoornaam"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblVoornaam, gridBagConstraints);

        lblAchternaam.setText("Achternaam");
        lblAchternaam.setName("lblAchternaam"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblAchternaam, gridBagConstraints);

        lblGeslacht.setText("Geslacht");
        lblGeslacht.setName("lblGeslacht"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblGeslacht, gridBagConstraints);

        lblGeboortedatum.setText("Geboortedatum");
        lblGeboortedatum.setName("lblGeboortedatum"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblGeboortedatum, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel5.add(dateGeboortedatum, gridBagConstraints);

        txtVoornaam.setText("jTextField1");
        txtVoornaam.setName("txtVoornaam"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(txtVoornaam, gridBagConstraints);

        txtAchternaam.setText("jTextField1");
        txtAchternaam.setName("txtAchternaam"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(txtAchternaam, gridBagConstraints);

        btngrpGeslacht.add(rbtMan);
        rbtMan.setLabel("M");
        rbtMan.setName("rbtMan"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(rbtMan, gridBagConstraints);

        btngrpGeslacht.add(rbtVrouw);
        rbtVrouw.setText("V");
        rbtVrouw.setName("rbtVrouw"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(rbtVrouw, gridBagConstraints);

        lblLidnummer.setText("Lidnummer");
        lblLidnummer.setName("lblLidnummer"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblLidnummer, gridBagConstraints);

        txtLidnummer.setText("jTextField1");
        txtLidnummer.setName("txtLidnummer"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(txtLidnummer, gridBagConstraints);

        lblTotem.setText("Totem");
        lblTotem.setName("lblTotem"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblTotem, gridBagConstraints);

        txtTotem.setText("jTextField1");
        txtTotem.setName("txtTotem"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(txtTotem, gridBagConstraints);

        lblAdjectief.setText("Adjectief");
        lblAdjectief.setName("lblAdjectief"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblAdjectief, gridBagConstraints);

        txtAdjectief.setText("jTextField1");
        txtAdjectief.setName("txtAdjectief"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(txtAdjectief, gridBagConstraints);

        lblTelefoon.setText("Telefoonnummer");
        lblTelefoon.setName("lblTelefoon"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblTelefoon, gridBagConstraints);

        txtTelefoon.setText("jTextField1");
        txtTelefoon.setName("txtTelefoon"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(txtTelefoon, gridBagConstraints);

        lblGSM.setText("GSM-nummer");
        lblGSM.setName("lblGSM"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblGSM, gridBagConstraints);

        txtGSM.setText("jTextField1");
        txtGSM.setName("txtGSM"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(txtGSM, gridBagConstraints);

        lblAdres.setText("Adres");
        lblAdres.setName("lblAdres"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblAdres, gridBagConstraints);

        txtAdres.setText("jTextField1");
        txtAdres.setName("txtAdres"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(txtAdres, gridBagConstraints);

        lblEmail.setText("E-mailadres");
        lblEmail.setName("lblEmail"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(lblEmail, gridBagConstraints);

        txtEmail.setText("jTextField1");
        txtEmail.setName("txtEmail"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(txtEmail, gridBagConstraints);

        btnSubmit.setText("Opslaan");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 50, 0);
        jPanel5.add(btnSubmit, gridBagConstraints);

        lblTakken.setText("Tak");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        jPanel5.add(lblTakken, gridBagConstraints);

        lstTakken.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstTakken.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstTakkenValueChanged(evt);
            }
        });
        scrollPane.setViewportView(lstTakken);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel5.add(scrollPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.insets = new java.awt.Insets(50, 0, 0, 0);
        jPanel5.add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 0);
        jPanel5.add(jSeparator2, gridBagConstraints);

        tblContactPersonen.setModel(new javax.swing.table.DefaultTableModel(
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
        scrlContactPersonen.setViewportView(tblContactPersonen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(50, 0, 0, 0);
        jPanel5.add(scrlContactPersonen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(jPanel5, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        if(checkInput()) {
            saveInput();
            this.dispose();
            parent.NieuwLidCallback(lid);
            
        } else {
            JOptionPane.showMessageDialog(this, "Niet alle velden zijn ingevuld.", "Fout bij het opslaan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void lstTakkenValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstTakkenValueChanged
        if (lstTakken.getSelectedIndices().length > 0) {
            Tak tak = (Tak) lstTakken.getSelectedValue();
            lid.setTakID(tak.getTakID());
        }
    }//GEN-LAST:event_lstTakkenValueChanged

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
            java.util.logging.Logger.getLogger(LidGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LidGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LidGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LidGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LidGUI dialog = new LidGUI((WerkstukGUI) new javax.swing.JFrame());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSubmit;
    private javax.swing.ButtonGroup btngrpGeslacht;
    private com.michaelbaranov.microba.calendar.DatePicker dateGeboortedatum;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblAchternaam;
    private javax.swing.JLabel lblAdjectief;
    private javax.swing.JLabel lblAdres;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGSM;
    private javax.swing.JLabel lblGeboortedatum;
    private javax.swing.JLabel lblGeslacht;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblLidnummer;
    private javax.swing.JLabel lblTakken;
    private javax.swing.JLabel lblTelefoon;
    private javax.swing.JLabel lblTotem;
    private javax.swing.JLabel lblVoornaam;
    private javax.swing.JList lstTakken;
    private javax.swing.JRadioButton rbtMan;
    private javax.swing.JRadioButton rbtVrouw;
    private javax.swing.JScrollPane scrlContactPersonen;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblContactPersonen;
    private javax.swing.JTextField txtAchternaam;
    private javax.swing.JTextField txtAdjectief;
    private javax.swing.JTextField txtAdres;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtGSM;
    private javax.swing.JTextField txtLidnummer;
    private javax.swing.JTextField txtTelefoon;
    private javax.swing.JTextField txtTotem;
    private javax.swing.JTextField txtVoornaam;
    // End of variables declaration//GEN-END:variables
}
