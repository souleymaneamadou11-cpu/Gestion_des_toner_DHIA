/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui.Panels;
import Dao.TonerDAO;
import Model.Toner;
import Service.StockService;
import Ui.MainFrame;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 *
 * @author DELL
 */
public class EntreeStockPanel extends JPanel {
    private final MainFrame    parent;
    private final TonerDAO     tonerDAO     = new TonerDAO();
    private final StockService stockService = new StockService();
 
    private JComboBox<String> cbToner;
    private JSpinner          spQty;
    private JTextField        txtFournisseur;
    private JTextField        txtBonCmd;
    private JTextField        txtMotif;
    private List<Toner>       toners;
 
    public EntreeStockPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(MainFrame.PAGE_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        build();
    }
 
    private void build() {
        // Titre
        JLabel titre = new JLabel("Entrée en stock");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titre.setAlignmentX(LEFT_ALIGNMENT);
        add(titre);
        add(Box.createVerticalStrut(6));
        JLabel sousTitre = new JLabel("Réceptionner des toners et mettre à jour le stock.");
        sousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sousTitre.setForeground(MainFrame.TEXT_GREY);
        sousTitre.setAlignmentX(LEFT_ALIGNMENT);
        add(sousTitre);
        add(Box.createVerticalStrut(18));
 
        // Formulaire principal
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(MainFrame.CARD_BG);
        form.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
            new EmptyBorder(18, 20, 18, 20)));
        form.setAlignmentX(LEFT_ALIGNMENT);
        form.setMaximumSize(new Dimension(700, 400));
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.insets    = new Insets(6, 0, 6, 12);
        gbc.weightx   = 1.0;
 
        // Chargement toners
        toners = tonerDAO.findAll();
        String[] items = toners.stream()
            .map(t -> t.getReference() + " — " + t.getMarque() + " " + t.getCouleur() + " [stock : " + t.getQuantiteStock() + "]")
            .toArray(String[]::new);
 
        cbToner = new JComboBox<>(items);
        cbToner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbToner.setPreferredSize(new Dimension(0, 34));
        // Actualise l'aperçu quand on change de toner
        cbToner.addActionListener(e -> actualiserApercu());
 
        spQty = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spQty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spQty.setPreferredSize(new Dimension(0, 34));
        spQty.addChangeListener(e -> actualiserApercu());
 
        txtFournisseur = MainFrame.createStyledField("Ex : Bureau Direct, Unitex...");
        txtBonCmd      = MainFrame.createStyledField("Ex : BC-2026-0142");
        txtMotif       = MainFrame.createStyledField("Observations éventuelles...");
 
        addFormRow(form, gbc, 0, "Toner à réceptionner *",  cbToner);
        addFormRow(form, gbc, 1, "Quantité réceptionnée *", spQty);
        addFormRow(form, gbc, 2, "Fournisseur",             txtFournisseur);
        addFormRow(form, gbc, 3, "N° bon de commande",      txtBonCmd);
        addFormRow(form, gbc, 4, "Motif / Observations",    txtMotif);
 
        add(form);
        add(Box.createVerticalStrut(12));
 
        // Aperçu avant/après
        add(buildApercu());
        add(Box.createVerticalStrut(14));
 
        // Boutons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setBackground(MainFrame.PAGE_BG);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
 
        JButton btnValider = MainFrame.createPrimaryButton("✅  Valider l'entrée");
        btnValider.addActionListener(e -> valider());
        JButton btnAnnuler = MainFrame.createSecondaryButton("✕  Annuler");
        btnAnnuler.addActionListener(e -> parent.navigateTo("dashboard", null));
 
        btnRow.add(btnValider);
        btnRow.add(Box.createHorizontalStrut(10));
        btnRow.add(btnAnnuler);
        add(btnRow);
 
        actualiserApercu();
    }
 
    private JLabel lblAvant, lblApres;
 
    private JPanel buildApercu() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setBackground(MainFrame.PAGE_BG);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(700, 70));
 
        JPanel avant = new JPanel(new BorderLayout());
        avant.setBackground(new Color(252, 235, 235));
        avant.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 180, 180)),
            new EmptyBorder(10, 14, 10, 14)));
        lblAvant = new JLabel("Avant : —");
        lblAvant.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        avant.add(lblAvant);
 
        JPanel apres = new JPanel(new BorderLayout());
        apres.setBackground(new Color(234, 243, 222));
        apres.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 160)),
            new EmptyBorder(10, 14, 10, 14)));
        lblApres = new JLabel("Après : —");
        lblApres.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        apres.add(lblApres);
 
        p.add(avant);
        p.add(apres);
        return p;
    }
 
    private void actualiserApercu() {
        if (cbToner == null || toners == null || toners.isEmpty()) return;
        int idx = cbToner.getSelectedIndex();
        if (idx < 0 || idx >= toners.size()) return;
        Toner t = toners.get(idx);
        int qty = (int) spQty.getValue();
        int stockApres = t.getQuantiteStock() + qty;
        lblAvant.setText("🔴  Avant : " + t.getReference() + " — " + t.getQuantiteStock() + " unité(s)");
        lblApres.setText("🟢  Après : " + t.getReference() + " — " + stockApres + " unité(s)");
    }
 
    private void valider() {
        int idx = cbToner.getSelectedIndex();
        if (idx < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un toner."); return; }
        Toner t = toners.get(idx);
        int qty = (int) spQty.getValue();
        String fournisseur = txtFournisseur.getText().trim();
        String motif = txtMotif.getText().trim();
        if (!txtBonCmd.getText().trim().isEmpty()) motif += " | BC: " + txtBonCmd.getText().trim();
 
        boolean ok = stockService.entreeStock(t.getId(), qty, motif, fournisseur);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "✅ " + qty + " toner(s) \" " + t.getReference() + "\" réceptionné(s) avec succès !\nNouveau stock : " + (t.getQuantiteStock() + qty),
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            parent.mettreAJourBadgeNotif();
            parent.navigateTo("dashboard", null);
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy  = row;
        gbc.gridx  = 0; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(MainFrame.TEXT_GREY);
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
          form.add(field, gbc);
    }
    
}
