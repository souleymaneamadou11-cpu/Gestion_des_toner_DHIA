/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui.Panels;
import Dao.MouvementDAO;
import Dao.TonerDAO;
import Ui.MainFrame;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;

/**
 *
 * @author DELL
 */
public class StatistiquesPanel extends JPanel{
    private final MouvementDAO mouvDAO  = new MouvementDAO();
    private final TonerDAO     tonerDAO = new TonerDAO();
    private final MainFrame    parent;
 
    public StatistiquesPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(MainFrame.PAGE_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        build();
    }
 
    private void build() {
        int annee = Calendar.getInstance().get(Calendar.YEAR);
        int moisActuel = Calendar.getInstance().get(Calendar.MONTH) + 1;
 
        // ── KPIs du mois ──────────────────────────────────────
        Map<Integer, Double>  couts    = mouvDAO.coutSortiesParMois(annee);
        Map<Integer, Integer> sorties  = mouvDAO.sortiesParMois(annee);
        double coutMois = couts.getOrDefault(moisActuel, 0.0);
        double coutAnnee = couts.values().stream().mapToDouble(Double::doubleValue).sum();
        int nbSorties = sorties.getOrDefault(moisActuel, 0);
        int nbAlertes = tonerDAO.findEnAlerte().size();
 
        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 0));
        kpis.setBackground(MainFrame.PAGE_BG);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        kpis.setAlignmentX(LEFT_ALIGNMENT);
 
        kpis.add(kpiStat(String.format("%,.0f FCFA", coutMois),  "Coût mois courant",  MainFrame.DHIA_BLUE));
        kpis.add(kpiStat(String.format("%,.0f FCFA", coutAnnee), "Coût total " + annee, new Color(80, 60, 180)));
        kpis.add(kpiStat(String.valueOf(nbSorties),               "Sorties ce mois",    MainFrame.SUCCESS));
        kpis.add(kpiStat(String.valueOf(nbAlertes),               "Toners en alerte",   MainFrame.DANGER));
 
        add(kpis);
        add(Box.createVerticalStrut(16));
 
        // ── Graphiques en 2 colonnes ──────────────────────────
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(MainFrame.PAGE_BG);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
 
        row.add(buildBarChart("Coûts par mois (" + annee + ")", couts, "FCFA",
            new Color(0, 140, 200)));
        row.add(buildBarChart("Sorties par mois (" + annee + ")", convertIntToDouble(sorties), "unités",
            new Color(80, 60, 180)));
 
        add(row);
        add(Box.createVerticalStrut(14));
 
        // ── Consommation par couleur ──────────────────────────
        Map<String, Integer> parCouleur = tonerDAO.stockParCouleur();
        JPanel coulRow = new JPanel(new GridLayout(1, 2, 14, 0));
        coulRow.setBackground(MainFrame.PAGE_BG);
        coulRow.setAlignmentX(LEFT_ALIGNMENT);
        coulRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
 
        coulRow.add(buildCouleurChart(parCouleur));
        coulRow.add(buildFiltreperiode());
 
        add(coulRow);
    }
 
    private JPanel kpiStat(String val, String lbl, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(MainFrame.CARD_BG);
        p.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
 
        JPanel bar = new JPanel(); bar.setBackground(color);
        bar.setPreferredSize(new Dimension(0, 4));
        p.add(bar, BorderLayout.NORTH);
 
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(MainFrame.CARD_BG);
        body.setBorder(new EmptyBorder(10, 14, 10, 14));
 
        JLabel vl = new JLabel(val); vl.setFont(new Font("Segoe UI", Font.BOLD, val.length() > 8 ? 14 : 22));
        vl.setForeground(color); vl.setAlignmentX(LEFT_ALIGNMENT);
        JLabel ll = new JLabel(lbl); ll.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ll.setForeground(MainFrame.TEXT_GREY); ll.setAlignmentX(LEFT_ALIGNMENT);
 
        body.add(vl); body.add(Box.createVerticalStrut(3)); body.add(ll);
        p.add(body, BorderLayout.CENTER);
        return p;
    }
 
    private JPanel buildBarChart(String titre, Map<Integer, Double> data, String unite, Color couleur) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MainFrame.CARD_BG);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
            new EmptyBorder(12, 14, 12, 14)));
 
        JLabel t = new JLabel(titre); t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(t, BorderLayout.NORTH);
 
        JPanel bars = new JPanel(new GridLayout(12, 1, 0, 4));
        bars.setBackground(MainFrame.CARD_BG);
        String[] moisNoms = {"Jan","Fév","Mar","Avr","Mai","Jun","Jul","Aoû","Sep","Oct","Nov","Déc"};
        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        if (max == 0) max = 1;
 
        for (int i = 1; i <= 12; i++) {
            double val = data.getOrDefault(i, 0.0);
            int pct = (int)(val / max * 100);
 
            JPanel row = new JPanel(new BorderLayout(6, 0));
            row.setBackground(MainFrame.CARD_BG);
 
            JLabel moisLbl = new JLabel(moisNoms[i-1]);
            moisLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            moisLbl.setForeground(MainFrame.TEXT_GREY);
            moisLbl.setPreferredSize(new Dimension(30, 0));
 
            JPanel track = new JPanel(new BorderLayout());
            track.setBackground(new Color(240, 240, 248));
            JPanel fill = new JPanel();
            fill.setBackground(couleur);
            fill.setPreferredSize(new Dimension((int)(pct * 1.5), 0));
            track.add(fill, BorderLayout.WEST);
 
            JLabel valLbl = new JLabel(pct > 0 ? (unite.equals("FCFA") ? String.format("%,.0f", val) : String.valueOf((int)val)) : "");
            valLbl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            valLbl.setForeground(MainFrame.TEXT_GREY);
            valLbl.setPreferredSize(new Dimension(70, 0));
 
            row.add(moisLbl, BorderLayout.WEST);
            row.add(track,   BorderLayout.CENTER);
            row.add(valLbl,  BorderLayout.EAST);
            bars.add(row);
        }
        card.add(new JScrollPane(bars) {{ setBorder(BorderFactory.createEmptyBorder()); }}, BorderLayout.CENTER);
        return card;
    }
 
    private JPanel buildCouleurChart(Map<String, Integer> data) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MainFrame.CARD_BG);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
            new EmptyBorder(12, 14, 12, 14)));
 
        JLabel t = new JLabel("Stock par couleur"); t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(t, BorderLayout.NORTH);
 
        JPanel bars = new JPanel(new GridLayout(4, 1, 0, 8));
        bars.setBackground(MainFrame.CARD_BG);
        Color[] clrs = {new Color(26,26,26), new Color(0,174,239), new Color(194,24,91), new Color(249,168,37)};
        String[] noms = {"NOIR","CYAN","MAGENTA","JAUNE"};
        int max = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        if (max == 0) max = 1;
 
        for (int i = 0; i < noms.length; i++) {
            int val = data.getOrDefault(noms[i], 0);
            int pct = (int)((double) val / max * 100);
 
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setBackground(MainFrame.CARD_BG);
 
            JLabel lbl = new JLabel(noms[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setPreferredSize(new Dimension(70, 0));
 
            JPanel track = new JPanel(new BorderLayout());
            track.setBackground(new Color(240, 240, 248));
            track.setPreferredSize(new Dimension(0, 20));
            JPanel fill = new JPanel();
            fill.setBackground(clrs[i]);
            fill.setPreferredSize(new Dimension((int)(pct * 2), 0));
            track.add(fill, BorderLayout.WEST);
 
            JLabel valLbl = new JLabel(String.valueOf(val));
            valLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            valLbl.setForeground(clrs[i]);
            valLbl.setPreferredSize(new Dimension(40, 0));
 
            row.add(lbl,    BorderLayout.WEST);
            row.add(track,  BorderLayout.CENTER);
            row.add(valLbl, BorderLayout.EAST);
            bars.add(row);
        }
        card.add(bars, BorderLayout.CENTER);
        return card;
    }
 
    private JPanel buildFiltreperiode() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MainFrame.CARD_BG);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
            new EmptyBorder(12, 14, 12, 14)));
 
        JLabel t = new JLabel("Coût sur une période"); t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(t, BorderLayout.NORTH);
 
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(MainFrame.CARD_BG);
 
        JTextField txtDeb = MainFrame.createStyledField("dd/MM/yyyy");
        JTextField txtFin = MainFrame.createStyledField("dd/MM/yyyy");
        JLabel lblResultat = new JLabel("—");
        lblResultat.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblResultat.setForeground(MainFrame.DHIA_BLUE);
 
        form.add(new JLabel("Du :") {{ setFont(new Font("Segoe UI",Font.BOLD,11)); setForeground(MainFrame.TEXT_GREY); }});
        form.add(txtDeb);
        form.add(new JLabel("Au :") {{ setFont(new Font("Segoe UI",Font.BOLD,11)); setForeground(MainFrame.TEXT_GREY); }});
        form.add(txtFin);
        form.add(new JLabel("Coût total :") {{ setFont(new Font("Segoe UI",Font.BOLD,11)); setForeground(MainFrame.TEXT_GREY); }});
        form.add(lblResultat);
 
        card.add(form, BorderLayout.CENTER);
 
        JButton btn = MainFrame.createPrimaryButton("📊 Calculer");
        btn.addActionListener(e -> {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                Date d = sdf.parse(txtDeb.getText().trim());
                Date f = sdf.parse(txtFin.getText().trim());
                double cout = mouvDAO.coutEntreDates(d, f);
                lblResultat.setText(String.format("%,.0f FCFA", cout));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez dd/MM/yyyy");
            }
        });
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }
 
    private Map<Integer, Double> convertIntToDouble(Map<Integer, Integer> map) {
        Map<Integer, Double> r = new LinkedHashMap<>();
        map.forEach((k, v) -> r.put(k, v.doubleValue()));
        return r;
    }
    
}
