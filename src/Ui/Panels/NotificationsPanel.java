/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui.Panels;
import Dao.TonerDAO;
import Model.Toner;
import Ui.MainFrame;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 *
 * @author DELL
 */
public class NotificationsPanel extends JPanel{
    private final MainFrame parent;
    private final TonerDAO  tonerDAO = new TonerDAO();
 
    public NotificationsPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(MainFrame.PAGE_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        build();
    }
 
    private void build() {
        List<Toner> alertes  = tonerDAO.findEnAlerte();
        List<Toner> ruptures = tonerDAO.findEnRupture();
 
        // KPIs
        JPanel kpis = new JPanel(new GridLayout(1, 3, 12, 0));
        kpis.setBackground(MainFrame.PAGE_BG);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        kpis.setAlignmentX(LEFT_ALIGNMENT);
 
        kpis.add(kpi(ruptures.size(), "Rupture(s) totale(s)", MainFrame.DANGER));
        kpis.add(kpi(alertes.size() - ruptures.size(), "Alerte(s) seuil", MainFrame.WARNING));
        kpis.add(kpi(alertes.size(), "Total alertes actives", MainFrame.DHIA_BLUE));
 
        add(kpis);
        add(Box.createVerticalStrut(16));
 
        // Bouton tout lire
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(MainFrame.PAGE_BG);
        topRow.setAlignmentX(LEFT_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
 /**/   JButton btnLire = MainFrame.createSecondaryButton("✔  Tout marquer comme lu");
        btnLire.addActionListener(e -> {
            parent.navigateTo("dashboard", null);
            JOptionPane.showMessageDialog(this, "Notifications marquées comme lues.");
        });
        topRow.add(btnLire, BorderLayout.EAST);
        add(topRow);
        add(Box.createVerticalStrut(10));
 
        // Liste des notifications
        JPanel liste = new JPanel();
        liste.setLayout(new BoxLayout(liste, BoxLayout.Y_AXIS));
        liste.setBackground(MainFrame.CARD_BG);
        liste.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        liste.setAlignmentX(LEFT_ALIGNMENT);
 
        if (alertes.isEmpty()) {
            JLabel ok = new JLabel("  ✅  Aucune alerte — tous les stocks sont au-dessus des seuils.", SwingConstants.CENTER);
            ok.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            ok.setForeground(MainFrame.SUCCESS);
            ok.setBorder(new EmptyBorder(20, 20, 20, 20));
            liste.add(ok);
        } else {
            // Ruptures en premier
            for (Toner t : ruptures) liste.add(buildNotifItem(t, true));
            // Alertes seuil
            for (Toner t : alertes) {
                if (!t.isEnRupture()) liste.add(buildNotifItem(t, false));
            }
        }
 
        add(new JScrollPane(liste) {{ setBorder(BorderFactory.createEmptyBorder()); }});
    }
 
    private JPanel buildNotifItem(Toner t, boolean rupture) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(rupture ? new Color(252, 235, 235) : new Color(250, 248, 220));
        row.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.BORDER_CLR),
            new EmptyBorder(12, 14, 12, 14)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
 
        // Icône
        JLabel ico = new JLabel(rupture ? "🔴" : "🟠");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
 
        // Corps texte
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(row.getBackground());
 
        String titre = rupture
            ? "Rupture de stock — " + t.getReference() + " (" + t.getCouleur() + ")"
            : "Alerte seuil — " + t.getReference() + " (" + t.getCouleur() + ")";
        String msg = rupture
            ? "Stock à 0 unité. Commande urgente requise pour éviter tout blocage."
            : "Stock : " + t.getQuantiteStock() + " unité(s). Seuil configuré : " + t.getSeuilAlerte() + " unité(s).";
 
        JLabel lTitre = new JLabel(titre);
        lTitre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lTitre.setForeground(rupture ? new Color(120, 30, 30) : new Color(100, 65, 10));
 
        JLabel lMsg = new JLabel(msg);
        lMsg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lMsg.setForeground(rupture ? new Color(150, 50, 50) : new Color(120, 80, 10));
 
        body.add(lTitre);
        body.add(Box.createVerticalStrut(3));
        body.add(lMsg);
 
        // Bouton action
        JButton btnAction = MainFrame.createPrimaryButton(rupture ? "➕ Faire une entrée" : "👁 Voir le toner");
        btnAction.addActionListener(e -> {
            if (rupture) parent.navigateTo("entree", null);
            else         parent.navigateTo("toners", null);
        });
 
        row.add(ico,       BorderLayout.WEST);
        row.add(body,      BorderLayout.CENTER);
        row.add(btnAction, BorderLayout.EAST);
        return row;
    }
 
    private JPanel kpi(int val, String lbl, Color c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(MainFrame.CARD_BG);
        p.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        JPanel bar = new JPanel(); bar.setBackground(c);
        bar.setPreferredSize(new Dimension(0, 4));
        p.add(bar, BorderLayout.NORTH);
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(MainFrame.CARD_BG);
        body.setBorder(new EmptyBorder(10, 14, 10, 14));
        JLabel vl = new JLabel(String.valueOf(val));
        vl.setFont(new Font("Segoe UI", Font.BOLD, 26)); vl.setForeground(c); vl.setAlignmentX(LEFT_ALIGNMENT);
        JLabel ll = new JLabel(lbl);
        ll.setFont(new Font("Segoe UI", Font.PLAIN, 11)); ll.setForeground(MainFrame.TEXT_GREY); ll.setAlignmentX(LEFT_ALIGNMENT);
        body.add(vl); body.add(Box.createVerticalStrut(3)); body.add(ll);
        p.add(body, BorderLayout.CENTER);
        return p;
    }
}
