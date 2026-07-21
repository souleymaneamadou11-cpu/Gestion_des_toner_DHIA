/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui.Panels;
import Dao.TonerDAO;
import Model.Toner;
import Service.StockService;
import Ui.MainFrame;
import Util.BonSortiePDF;
import Util.SessionManager;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.util.*;

/**
 *
 * @author DELL
 */
public class SortiePanel extends JPanel {
    private final MainFrame    parent;
    private final TonerDAO     tonerDAO     = new TonerDAO();
    private final StockService stockService = new StockService();
 
    private JTextField    txtDept;
    private JTextField    txtMotif;
    private Map<Toner, JSpinner> lignes = new LinkedHashMap<>();
 
    public SortiePanel(MainFrame parent) {
        this.parent = parent;
        setBackground(MainFrame.PAGE_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        build();
    }
 
    private void build() {
        JLabel titre = new JLabel("Nouvelle sortie de toners");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titre.setForeground(MainFrame.TEXT_DARK);
        titre.setAlignmentX(LEFT_ALIGNMENT);
        add(titre);
        add(Box.createVerticalStrut(16));
 
        // ── Formulaire département / motif ─────────────────────
        JPanel formCard = new JPanel(new GridLayout(1, 2, 14, 0));
        formCard.setBackground(MainFrame.CARD_BG);
        formCard.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
            new EmptyBorder(14, 16, 14, 16)));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        formCard.setAlignmentX(LEFT_ALIGNMENT);
 
        JPanel g1 = fieldGroup("Département / Localisation *");
        txtDept = MainFrame.createStyledField("Ex : Ressources Humaines...");
        g1.add(txtDept);
 
        JPanel g2 = fieldGroup("Motif de la sortie");
        txtMotif = MainFrame.createStyledField("Ex : Remplacement toner vide");
        g2.add(txtMotif);
 
        formCard.add(g1);
        formCard.add(g2);
        add(formCard);
        add(Box.createVerticalStrut(12));
 
        // ── Liste des toners avec spinners ─────────────────────
        JPanel listCard = new JPanel();
        listCard.setLayout(new BoxLayout(listCard, BoxLayout.Y_AXIS));
        listCard.setBackground(MainFrame.CARD_BG);
        listCard.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        listCard.setAlignmentX(LEFT_ALIGNMENT);
 
        // En-tête
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 245, 252));
        header.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.BORDER_CLR),
            new EmptyBorder(8, 14, 8, 14)));
        JLabel hTitre = new JLabel("Sélectionnez les toners et les quantités à sortir");
        hTitre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hTitre.setForeground(MainFrame.TEXT_DARK);
        header.add(hTitre, BorderLayout.WEST);
        listCard.add(header);
 
        // Lignes toners
        for (Toner t : tonerDAO.findAll()) {
            JPanel row = buildTonerRow(t);
            listCard.add(row);
            lignes.put(t, getSpinnerFromRow(row));
        }
 
        JScrollPane scroll = new JScrollPane(listCard);
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
        add(scroll);
        add(Box.createVerticalStrut(14));
 
        // ── Boutons ────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setBackground(MainFrame.PAGE_BG);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
 
        JButton btnValider = MainFrame.createPrimaryButton("✅  Valider et générer bon PDF");
        btnValider.addActionListener(e -> validerSortie());
 
        JButton btnAnnuler = MainFrame.createSecondaryButton("✕  Annuler");
        btnAnnuler.addActionListener(e -> parent.navigateTo("dashboard", null));
        btnAnnuler.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
            new EmptyBorder(7, 14, 7, 14)));
 
        btnRow.add(btnValider);
        btnRow.add(Box.createHorizontalStrut(10));
        btnRow.add(btnAnnuler);
        add(btnRow);
    }
 
    private JPanel buildTonerRow(Toner t) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(MainFrame.CARD_BG);
        row.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.BORDER_CLR),
            new EmptyBorder(10, 14, 10, 14)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
 
        // Infos toner à gauche
        JPanel info = new JPanel(new BorderLayout());
        info.setBackground(MainFrame.CARD_BG);
 
        JLabel refLbl = new JLabel(t.getReference() + "   —   " + t.getMarque() + " " + t.getModele());
        refLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refLbl.setForeground(t.isEnRupture() ? MainFrame.DANGER :
                              t.isEnAlerte()  ? MainFrame.WARNING : MainFrame.TEXT_DARK);
 
        JLabel detailLbl = new JLabel(t.getCouleur() + "   |   Compatible : " + t.getCompatibilite());
        detailLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailLbl.setForeground(MainFrame.TEXT_GREY);
 
        info.add(refLbl,    BorderLayout.NORTH);
        info.add(detailLbl, BorderLayout.SOUTH);
 
        // Stock + spinner à droite
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(MainFrame.CARD_BG);
 
        // Badge stock
        JLabel stockBadge;
        if (t.isEnRupture()) {
            stockBadge = MainFrame.createBadge("Rupture", MainFrame.BADGE_DANGER, MainFrame.DANGER);
        } else if (t.isEnAlerte()) {
            stockBadge = MainFrame.createBadge("Stock : " + t.getQuantiteStock(), MainFrame.BADGE_WARN, MainFrame.WARNING);
        } else {
            stockBadge = MainFrame.createBadge("Stock : " + t.getQuantiteStock(), MainFrame.BADGE_OK, MainFrame.SUCCESS);
        }
 
        // Spinner quantité
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, t.getQuantiteStock(), 1));
        spinner.setName("spinner_" + t.getId());
        spinner.setPreferredSize(new Dimension(80, 30));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (t.isEnRupture()) spinner.setEnabled(false);
 
        right.add(stockBadge);
        right.add(spinner);
 
        row.add(info,  BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }
 
    private JSpinner getSpinnerFromRow(JPanel row) {
        // Cherche le spinner dans la ligne
        for (Component c : ((JPanel) row.getComponent(1)).getComponents()) {
            if (c instanceof JSpinner) return (JSpinner) c;
        }
        return new JSpinner();
    }
 
    private void validerSortie() {
        String dept = txtDept.getText().trim();
        if (dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez indiquer le département.", "Champ requis", JOptionPane.WARNING_MESSAGE);
            txtDept.requestFocus();
            return;
        }
 
        // Récupérer les toners sélectionnés
        Map<Toner, Integer> selection = new LinkedHashMap<>();
        for (Map.Entry<Toner, JSpinner> entry : lignes.entrySet()) {
            int qty = (int) entry.getValue().getValue();
            if (qty > 0) selection.put(entry.getKey(), qty);
        }
 
        if (selection.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sélectionnez au moins un toner avec une quantité > 0.", "Sélection vide", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        // Confirmation
        StringBuilder recap = new StringBuilder("Confirmer la sortie suivante :\n\n");
        recap.append("Département : ").append(dept).append("\n\n");
        selection.forEach((t, q) -> recap.append("  • ").append(t.getReference()).append(" × ").append(q).append("\n"));
 
        int conf = JOptionPane.showConfirmDialog(this, recap.toString(), "Confirmation sortie", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
 
        // Numéro bon unique
        String numBon  = stockService.genererNumeroBon();
        String motif   = "Sortie → " + dept + (txtMotif.getText().trim().isEmpty() ? "" : " | " + txtMotif.getText().trim());
 
        // Enregistrement de chaque ligne
        StringBuilder erreurs = new StringBuilder();
        for (Map.Entry<Toner, Integer> entry : selection.entrySet()) {
            String res = stockService.sortieStock(entry.getKey().getId(), entry.getValue(), motif, numBon);
            if (!res.equals("OK")) erreurs.append("• ").append(entry.getKey().getReference()).append(" : ").append(res).append("\n");
        }
 
        if (erreurs.length() > 0) {
            JOptionPane.showMessageDialog(this, "Erreurs :\n" + erreurs, "Problèmes", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        // Génération PDF bon de sortie
        try {
            String pdfPath = BonSortiePDF.generer(numBon, SessionManager.getUtilisateur(), selection, dept);
            JOptionPane.showMessageDialog(this,
                "✅ Sortie enregistrée avec succès !\nBon de sortie : " + numBon + "\nFichier : " + pdfPath,
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            // Ouvrir PDF automatiquement
            Desktop.getDesktop().open(new File(pdfPath));
            parent.navigateTo("dashboard", null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Sortie enregistrée mais erreur PDF :\n" + ex.getMessage(), "Avertissement", JOptionPane.WARNING_MESSAGE);
            ex.printStackTrace();
            parent.navigateTo("dashboard", null);
        }
    }
 
    private JPanel fieldGroup(String label) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(MainFrame.CARD_BG);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(MainFrame.TEXT_GREY);
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        p.add(lbl);
        return p;
    }
}
