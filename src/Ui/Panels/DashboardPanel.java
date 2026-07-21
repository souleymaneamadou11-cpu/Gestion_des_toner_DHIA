/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui.Panels;
import Dao.TonerDAO;
import Model.Toner;
import Ui.MainFrame;
import Util.SessionManager;
 
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL
 */
public class DashboardPanel extends JPanel{
    private final MainFrame  parent;
    private final TonerDAO   tonerDAO = new TonerDAO();
 
    public DashboardPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(MainFrame.PAGE_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        build();
    }
 
    private void build() {
        // ── Cartes KPI stock par couleur ──────────────────────
        Map<String, Integer> parCouleur = tonerDAO.stockParCouleur();
        add(buildKpiRow(parCouleur));
        add(Box.createVerticalStrut(16));
 
        // ── Alertes (admin seulement) ─────────────────────────
        if (SessionManager.isAdmin()) {
            List<Toner> alertes = tonerDAO.findEnAlerte();
            if (!alertes.isEmpty()) {
                add(buildAlerteStrip(alertes));
                add(Box.createVerticalStrut(16));
            }
        }
 
        // ── Tableau stock ─────────────────────────────────────
        add(buildStockTable());
    }
 
    // ── CARTES KPI ────────────────────────────────────────────
    private JPanel buildKpiRow(Map<String, Integer> parCouleur) {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBackground(MainFrame.PAGE_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.setAlignmentX(LEFT_ALIGNMENT);
 
        row.add(kpiCard("Toners NOIR",    parCouleur.getOrDefault("NOIR",    0), new Color(26,26,26),   "stock.noir"));
        row.add(kpiCard("Toners CYAN",    parCouleur.getOrDefault("CYAN",    0), new Color(0,174,239),  "stock.cyan"));
        row.add(kpiCard("Toners MAGENTA", parCouleur.getOrDefault("MAGENTA", 0), new Color(194,24,91),  "stock.magenta"));
        row.add(kpiCard("Toners JAUNE",   parCouleur.getOrDefault("JAUNE",   0), new Color(249,168,37), "stock.jaune"));
        return row;
    }
 
    private JPanel kpiCard(String label, int valeur, Color couleur, String couleurKey) {
        // Vérification état
        List<Toner> alertes = tonerDAO.findEnAlerte();
        boolean enAlerte = alertes.stream().anyMatch(t -> t.getCouleur().equals(couleurKey.split("\\.")[1].toUpperCase()));
 
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MainFrame.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR, 1));
 
        // Barre couleur en haut
        JPanel barTop = new JPanel();
        barTop.setBackground(couleur);
        barTop.setPreferredSize(new Dimension(0, 4));
        card.add(barTop, BorderLayout.NORTH);
 
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(MainFrame.CARD_BG);
        body.setBorder(new EmptyBorder(12, 14, 12, 14));
 
        JLabel numLbl = new JLabel(String.valueOf(valeur));
        numLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        numLbl.setForeground(couleur);
        numLbl.setAlignmentX(LEFT_ALIGNMENT);
 
        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLbl.setForeground(MainFrame.TEXT_GREY);
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);
 
        // Indicateur état
        String etatTxt = valeur == 0 ? "🔴 Rupture" : enAlerte ? "🟠 Alerte seuil" : "🟢 Stock OK";
        JLabel etatLbl = new JLabel(etatTxt);
        etatLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        etatLbl.setAlignmentX(LEFT_ALIGNMENT);
 
        body.add(numLbl);
        body.add(Box.createVerticalStrut(3));
        body.add(nameLbl);
        body.add(Box.createVerticalStrut(4));
        body.add(etatLbl);
        card.add(body, BorderLayout.CENTER);
        return card;
    }
 
    // ── BANDE D'ALERTES ───────────────────────────────────────
    private JPanel buildAlerteStrip(List<Toner> alertes) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(MainFrame.PAGE_BG);
        p.setAlignmentX(LEFT_ALIGNMENT);
 
        for (Toner t : alertes) {
            JPanel strip = new JPanel(new BorderLayout(10, 0));
            strip.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.BORDER_CLR),
                new EmptyBorder(8, 12, 8, 12)));
 
            if (t.isEnRupture()) {
                strip.setBackground(new Color(252, 235, 235));
                JLabel ico = new JLabel("🔴");
                ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                JLabel msg = new JLabel("<html><b>Rupture de stock</b> — " + t.getReference() +
                    " (" + t.getCouleur() + ") — 0 unité restante. Commande urgente requise.</html>");
                msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                msg.setForeground(new Color(120, 30, 30));
                strip.add(ico, BorderLayout.WEST);
                strip.add(msg, BorderLayout.CENTER);
            } else {
                strip.setBackground(new Color(250, 238, 218));
                JLabel ico = new JLabel("🟠");
                ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                JLabel msg = new JLabel("<html><b>Alerte seuil</b> — " + t.getReference() +
                    " (" + t.getCouleur() + ") — " + t.getQuantiteStock() +
                    " unité(s) / seuil : " + t.getSeuilAlerte() + ".</html>");
                msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                msg.setForeground(new Color(120, 80, 10));
                strip.add(ico, BorderLayout.WEST);
                strip.add(msg, BorderLayout.CENTER);
            }
            p.add(strip);
        }
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(MainFrame.PAGE_BG);
        wrap.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        wrap.add(p);
        wrap.setAlignmentX(LEFT_ALIGNMENT);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        return wrap;
    }
 
    // ── TABLEAU STOCK ─────────────────────────────────────────
    private JPanel buildStockTable() {
      /*  List<Toner> toners = tonerDAO.findAll();
    boolean isAdmin = SessionManager.isAdmin();

    // Colonnes selon le rôle
    String[] cols = isAdmin
        ? new String[]{"Référence","Marque","Couleur","Compatibilité","Stock","Seuil","Prix","État","Actions"}
        : new String[]{"Référence","Marque","Couleur","Compatibilité","Stock","Seuil","État"};

    // Données
    Object[][] data = new Object[toners.size()][cols.length];
    for (int i = 0; i < toners.size(); i++) {
        Toner t = toners.get(i);
        data[i][0] = t.getReference();
        data[i][1] = t.getMarque();
        data[i][2] = t.getCouleur();
        data[i][3] = t.getCompatibilite();
        data[i][4] = t.getQuantiteStock();
        data[i][5] = t.getSeuilAlerte();
        if (isAdmin) {
            data[i][6] = String.format("%,.0f FCFA", t.getPrixUnitaire());
            data[i][7] = t.isEnRupture() ? "Rupture" : t.isEnAlerte() ? "Alerte" : "OK";
            data[i][8] = ""; // colonne boutons
        } else {
            data[i][6] = t.isEnRupture() ? "Rupture" : t.isEnAlerte() ? "Alerte" : "OK";
        }
    }

    JTable table = new JTable(data, cols) {
        public boolean isCellEditable(int r, int c) { return false; }
        public Component prepareRenderer(TableCellRenderer r, int row, int col) {
            Component c = super.prepareRenderer(r, row, col);
            String etat = String.valueOf(getValueAt(row, isAdmin ? 7 : 6));
            if      (etat.equals("Rupture")) c.setBackground(new Color(252, 235, 235));
            else if (etat.equals("Alerte"))  c.setBackground(new Color(250, 248, 220));
            else                              c.setBackground(Color.WHITE);
            c.setForeground(MainFrame.TEXT_DARK);
            if (this.isRowSelected(row)) c.setBackground(new Color(232, 242, 252));
            return c;
        }
    };

    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.setRowHeight(36);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
    table.getTableHeader().setBackground(new Color(245, 245, 252));
    table.getTableHeader().setForeground(MainFrame.TEXT_GREY);
    table.getTableHeader().setPreferredSize(new Dimension(0, 34));
    table.setSelectionBackground(new Color(232, 242, 252));

    // ── Renderer et Editor pour la colonne Actions (admin seulement) ──
    if (isAdmin) {
        int colActions = cols.length - 1;
        table.getColumnModel().getColumn(colActions).setPreferredWidth(110);
        table.getColumnModel().getColumn(colActions).setMinWidth(110);

        // Renderer — dessine les boutons dans la cellule
        table.getColumnModel().getColumn(colActions)
            .setCellRenderer(new TableCellRenderer() {
                public Component getTableCellRendererComponent(JTable t, Object val,
                        boolean sel, boolean foc, int row, int col) {
                    return buildActionPanel(toners, row);
                }
            });

        // Editor — rend les boutons cliquables
        table.getColumnModel().getColumn(colActions)
            .setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                private JPanel panel;
                public Component getTableCellEditorComponent(JTable t, Object val,
                        boolean sel, int row, int col) {
                    panel = buildActionPanel(toners, row);
                    return panel;
                }
                public Object getCellEditorValue() { return ""; }
            });
    }

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

    // Titre + bouton Ajouter
    JLabel titre = new JLabel("État du stock — tous les toners");
    titre.setFont(new Font("Segoe UI", Font.BOLD, 14));
    titre.setForeground(MainFrame.TEXT_DARK);

    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setBackground(MainFrame.PAGE_BG);
    topRow.setBorder(new EmptyBorder(0, 0, 8, 0));
    topRow.add(titre, BorderLayout.WEST);

    if (isAdmin) {
        JButton btnAjout = MainFrame.createPrimaryButton("➕ Ajouter un toner");
        btnAjout.addActionListener(e -> parent.navigateTo("toners", null));
        topRow.add(btnAjout, BorderLayout.EAST);
    }

    JPanel wrap = new JPanel(new BorderLayout(0, 8));
    wrap.setBackground(MainFrame.PAGE_BG);
    wrap.setAlignmentX(LEFT_ALIGNMENT);
    wrap.add(topRow, BorderLayout.NORTH);
    wrap.add(scroll, BorderLayout.CENTER);
    return wrap;
}

/*
 * Construit le panel avec les 2 boutons ✏️ et 🗑️ pour une ligne donnée
 */
/*private JPanel buildActionPanel(List<Toner> toners, int row) {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
    p.setBackground(Color.WHITE);

    // Bouton modifier
    JButton btnEdit = new JButton("✏️");
    btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
    btnEdit.setPreferredSize(new Dimension(36, 28));
    btnEdit.setToolTipText("Modifier ce toner");
    btnEdit.setFocusPainted(false);
    btnEdit.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
    btnEdit.setBackground(Color.WHITE);
    btnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnEdit.addActionListener(e -> {
        if (row < toners.size()) parent.navigateTo("toners", null);
    });

    // Bouton supprimer
    JButton btnDel = new JButton("🗑️");
    btnDel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
    btnDel.setPreferredSize(new Dimension(36, 28));
    btnDel.setToolTipText("Supprimer ce toner");
    btnDel.setFocusPainted(false);
    btnDel.setBorder(BorderFactory.createLineBorder(new Color(240, 180, 180)));
    btnDel.setBackground(new Color(252, 235, 235));
    btnDel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnDel.addActionListener(e -> {
        if (row < toners.size()) {
            Toner t = toners.get(row);
            int conf = JOptionPane.showConfirmDialog(null,
                "Supprimer \"" + t.getReference() + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (conf == JOptionPane.YES_OPTION) {
                tonerDAO.delete(t.getId());
                parent.navigateTo("dashboard", null);
            }
        }
    });

    p.add(btnEdit);
    p.add(btnDel);
    return p;
    */
    
    
        List<Toner> toners = tonerDAO.findAll();
        boolean isAdmin = SessionManager.isAdmin();
 
        String[] cols = isAdmin
            ? new String[]{"Référence", "Marque", "Couleur", "Compatibilité", "Stock", "Seuil", "Prix", "État", "Actions"}
            : new String[]{"Référence", "Marque", "Couleur", "Compatibilité", "Stock", "Seuil", "État"};
 
        Object[][] data = new Object[toners.size()][cols.length];
        for (int i = 0; i < toners.size(); i++) {
            Toner t = toners.get(i);
            data[i][0] = t.getReference();
            data[i][1] = t.getMarque();
            data[i][2] = t.getCouleur();
            data[i][3] = t.getCompatibilite();
            data[i][4] = t.getQuantiteStock();
            data[i][5] = t.getSeuilAlerte();
            if (isAdmin) {
                data[i][6] = String.format("%,.0f FCFA", t.getPrixUnitaire());
                data[i][7] = t.isEnRupture() ? "Rupture" : t.isEnAlerte() ? "Alerte" : "OK";
                data[i][8] = "Actions";
            } else {
                data[i][6] = t.isEnRupture() ? "Rupture" : t.isEnAlerte() ? "Alerte" : "OK";
            }
        }
        final JTable[] tableRef = {null};
        JTable table = new JTable(data, cols) {
        public boolean isCellEditable(int r, int c) { return false; }
        public Component prepareRenderer(TableCellRenderer r, int row, int col) {
        Component c = super.prepareRenderer(r, row, col);
        String etat = String.valueOf(getValueAt(row, isAdmin ? 7 : 6));
        if      (etat.equals("Rupture")) c.setBackground(new Color(252, 235, 235));
        else if (etat.equals("Alerte"))  c.setBackground(new Color(250, 248, 220));
        else                              c.setBackground(Color.WHITE);
        c.setForeground(MainFrame.TEXT_DARK);
        // Correction ici — utiliser isRowSelected sur this (la JTable elle-même)
        if (this.isRowSelected(row)) c.setBackground(new Color(232, 242, 252));
        return c;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(245, 245, 252));
        table.getTableHeader().setForeground(MainFrame.TEXT_GREY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.setSelectionBackground(new Color(232, 242, 252));
        // Masquer colonne Actions (gérée séparément)
        if (isAdmin) {
            TableColumn actCol = table.getColumnModel().getColumn(cols.length - 1);
            actCol.setPreferredWidth(90);
        }
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
 
        JLabel titre = new JLabel("État du stock — tous les toners");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titre.setForeground(MainFrame.TEXT_DARK);
 
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(MainFrame.PAGE_BG);
        topRow.setBorder(new EmptyBorder(0, 0, 8, 0));
        topRow.add(titre, BorderLayout.WEST);
        if (isAdmin) {
            JButton btnAct = MainFrame.createPrimaryButton("➕ Ajouter un toner");
            btnAct.addActionListener(e -> parent.navigateTo("toners", null));
            topRow.add(btnAct, BorderLayout.EAST);
        }
 
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setBackground(MainFrame.PAGE_BG);
        wrap.setAlignmentX(LEFT_ALIGNMENT);
        wrap.add(topRow,  BorderLayout.NORTH);
        wrap.add(scroll,  BorderLayout.CENTER);
        return wrap;
    }
}
