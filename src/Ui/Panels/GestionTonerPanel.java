/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui.Panels;
import Dao.TonerDAO;
import Model.Toner;
import Ui.MainFrame;
import Ui.Panels.GestionTonerPanel;
 
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 *
 * @author DELL
 */
public class GestionTonerPanel extends JPanel {
    private final MainFrame parent;
    private final TonerDAO  tonerDAO = new TonerDAO();
    private JTable  table;
    private DefaultTableModel model;
 
    public GestionTonerPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(MainFrame.PAGE_BG);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        build();
    }
 
    private void build() {
        // Barre supérieure
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(MainFrame.PAGE_BG);
 
        JTextField txtSearch = MainFrame.createStyledField("🔍 Rechercher par référence, marque...");
        txtSearch.setMaximumSize(new Dimension(300, 34));
        txtSearch.setPreferredSize(new Dimension(300, 34));
 
        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftBar.setBackground(MainFrame.PAGE_BG);
        leftBar.add(txtSearch);
 
        JButton btnAjouter = MainFrame.createPrimaryButton("➕  Nouveau toner");
        btnAjouter.addActionListener(e -> ouvrirFormulaire(null));
 
        topBar.add(leftBar,    BorderLayout.WEST);
        topBar.add(btnAjouter, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
 
        // Tableau
        String[] cols = {"Référence","Marque","Modèle","Couleur","Compatibilité","Stock","Seuil","Prix (FCFA)","État"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        chargerDonnees();
 
        table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                String etat = String.valueOf(getValueAt(row, 8));
                if      (etat.contains("Rupture")) c.setBackground(new Color(252,235,235));
                else if (etat.contains("Alerte"))  c.setBackground(new Color(250,248,220));
                else                                c.setBackground(Color.WHITE);
                c.setForeground(MainFrame.TEXT_DARK);
                return c;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(245,245,252));
        table.getTableHeader().setForeground(MainFrame.TEXT_GREY);
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        add(scroll, BorderLayout.CENTER);
 
        // Boutons d'action sous le tableau
        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionsBar.setBackground(MainFrame.PAGE_BG);
 
        JButton btnEdit = MainFrame.createSecondaryButton("✏️  Modifier");
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un toner à modifier."); return; }
            String ref = String.valueOf(model.getValueAt(row, 0));
            List<Toner> all = tonerDAO.findAll();
            Toner t = all.stream().filter(x -> x.getReference().equals(ref)).findFirst().orElse(null);
            ouvrirFormulaire(t);
        });
 
        JButton btnSupp = new JButton("🗑️  Supprimer");
        btnSupp.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        btnSupp.setForeground(MainFrame.DANGER);
        btnSupp.setBackground(MainFrame.BADGE_DANGER);
        btnSupp.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(240,180,180)),
            new EmptyBorder(7,14,7,14)));
        btnSupp.setFocusPainted(false);
        btnSupp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSupp.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un toner à supprimer."); return; }
            String ref = String.valueOf(model.getValueAt(row, 0));
            int conf = JOptionPane.showConfirmDialog(this,
                "Supprimer le toner \"" + ref + "\" définitivement ?", "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (conf == JOptionPane.YES_OPTION) {
                List<Toner> all = tonerDAO.findAll();
                Toner t = all.stream().filter(x -> x.getReference().equals(ref)).findFirst().orElse(null);
                if (t != null) { tonerDAO.delete(t.getId()); chargerDonnees(); }
            }
        });
 
        actionsBar.add(btnEdit);
        actionsBar.add(btnSupp);
        add(actionsBar, BorderLayout.SOUTH);
 
        // Recherche en temps réel
        txtSearch.addCaretListener(e -> filtrer(txtSearch.getText().trim()));
    }
 
    private void chargerDonnees() {
        model.setRowCount(0);
        for (Toner t : tonerDAO.findAll()) {
            model.addRow(new Object[]{
                t.getReference(), t.getMarque(), t.getModele(), t.getCouleur(),
                t.getCompatibilite(), t.getQuantiteStock(), t.getSeuilAlerte(),
                String.format("%,.0f", t.getPrixUnitaire()),
                t.isEnRupture() ? "🔴 Rupture" : t.isEnAlerte() ? "🟠 Alerte" : "🟢 OK"
            });
        }
    }
 
    private void filtrer(String texte) {
        model.setRowCount(0);
        for (Toner t : tonerDAO.findAll()) {
            if (t.getReference().toLowerCase().contains(texte.toLowerCase()) ||
                t.getMarque().toLowerCase().contains(texte.toLowerCase()) ||
                t.getCouleur().toLowerCase().contains(texte.toLowerCase())) {
                model.addRow(new Object[]{
                    t.getReference(), t.getMarque(), t.getModele(), t.getCouleur(),
                    t.getCompatibilite(), t.getQuantiteStock(), t.getSeuilAlerte(),
                    String.format("%,.0f", t.getPrixUnitaire()),
                    t.isEnRupture() ? "🔴 Rupture" : t.isEnAlerte() ? "🟠 Alerte" : "🟢 OK"
                });
            }
        }
    }
 
    private void ouvrirFormulaire(Toner toner) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            toner == null ? "Nouveau toner" : "Modifier — " + toner.getReference(), true);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(MainFrame.CARD_BG);
        form.setBorder(new EmptyBorder(18, 20, 18, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5, 0, 5, 10); g.weightx = 1;
 
        JTextField fRef   = MainFrame.createStyledField("Ex: HP CF400A");
        JTextField fMarq  = MainFrame.createStyledField("Ex: HP, Canon, Brother");
        JTextField fModl  = MainFrame.createStyledField("Ex: LaserJet Pro M252");
        JComboBox<String> fCoul = new JComboBox<>(new String[]{"NOIR","CYAN","MAGENTA","JAUNE"});
        fCoul.setPreferredSize(new Dimension(0, 34));
        JTextField fComp  = MainFrame.createStyledField("Ex: M252/M277");
        JSpinner   fStock = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        JSpinner   fSeuil = new JSpinner(new SpinnerNumberModel(5, 0, 9999, 1));
        JTextField fPrix  = MainFrame.createStyledField("Ex: 28500");
 
        if (toner != null) {
            fRef.setText(toner.getReference()); fMarq.setText(toner.getMarque());
            fModl.setText(toner.getModele()); fCoul.setSelectedItem(toner.getCouleur());
            fComp.setText(toner.getCompatibilite()); fSeuil.setValue(toner.getSeuilAlerte());
            fPrix.setText(String.valueOf((int)toner.getPrixUnitaire()));
        }
 
        String[][] rows = {{"Référence *",fRef.getText()},{"Marque *",""},{"Modèle",""},{"Couleur *",""},{"Compatibilité",""},{"Stock initial",""},{"Seuil alerte",""},{"Prix (FCFA)",""}};
        JComponent[] fields = {fRef, fMarq, fModl, fCoul, fComp, fStock, fSeuil, fPrix};
        String[] labels = {"Référence *","Marque *","Modèle","Couleur *","Compatibilité","Stock initial","Seuil alerte","Prix (FCFA)"};
        for (int i = 0; i < labels.length; i++) {
            g.gridy = i; g.gridx = 0; g.weightx = 0.35;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(MainFrame.TEXT_GREY);
            form.add(lbl, g);
            g.gridx = 1; g.weightx = 0.65;
            form.add(fields[i], g);
        }
 
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setBackground(MainFrame.CARD_BG);
        JButton btnSave = MainFrame.createPrimaryButton("💾  Enregistrer");
        btnSave.addActionListener(e -> {
            if (fRef.getText().trim().isEmpty() || fMarq.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Référence et Marque sont obligatoires.");
                return;
            }
            Toner t = toner != null ? toner : new Toner();
            t.setReference(fRef.getText().trim());
            t.setMarque(fMarq.getText().trim());
            t.setModele(fModl.getText().trim());
            t.setCouleur((String) fCoul.getSelectedItem());
            t.setCompatibilite(fComp.getText().trim());
            t.setQuantiteStock((int) fStock.getValue());
            t.setSeuilAlerte((int) fSeuil.getValue());
            try { t.setPrixUnitaire(Double.parseDouble(fPrix.getText().trim())); } catch (Exception ex) { t.setPrixUnitaire(0); }
 
            boolean ok = toner == null ? tonerDAO.insert(t) : tonerDAO.update(t);
            if (ok) { chargerDonnees(); dlg.dispose(); }
            else JOptionPane.showMessageDialog(dlg, "Erreur lors de l'enregistrement.");
        });
        JButton btnAnnuler2 = MainFrame.createSecondaryButton("Annuler");
        btnAnnuler2.addActionListener(e -> dlg.dispose());
        btnRow.add(btnAnnuler2);
        btnRow.add(btnSave);
 
        dlg.setLayout(new BorderLayout());
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
