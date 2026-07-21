/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui.Panels;
import Dao.MouvementDAO;
import Model.Mouvement;
import Ui.MainFrame;
 
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
/**
 *
 * @author DELL
 */
public class HistoriquePanel extends JPanel {
    private final MainFrame     parent;
    private final MouvementDAO  mouvDAO = new MouvementDAO();
    private DefaultTableModel   model;
 
    public HistoriquePanel(MainFrame parent) {
        this.parent = parent;
        setBackground(MainFrame.PAGE_BG);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        build();
    }
 
    private void build() {
        // ── Filtres ────────────────────────────────────────────
        JPanel filtres = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtres.setBackground(MainFrame.CARD_BG);
        filtres.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
            new EmptyBorder(10, 14, 10, 14)));
 
        JTextField txtDebut = MainFrame.createStyledField("dd/MM/yyyy");
        JTextField txtFin   = MainFrame.createStyledField("dd/MM/yyyy");
        txtDebut.setPreferredSize(new Dimension(120, 32));
        txtFin.setPreferredSize(new Dimension(120, 32));
 
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Tous","ENTREE","SORTIE"});
        cbType.setPreferredSize(new Dimension(110, 32));
        cbType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
 
        JButton btnFiltrer = MainFrame.createPrimaryButton("🔍 Filtrer");
        JButton btnExport  = MainFrame.createSecondaryButton("📊 Export Excel");
 
        filtres.add(new JLabel("Du :"){ { setFont(new Font("Segoe UI",Font.BOLD,11)); setForeground(MainFrame.TEXT_GREY); } });
        filtres.add(txtDebut);
        filtres.add(new JLabel("Au :"){ { setFont(new Font("Segoe UI",Font.BOLD,11)); setForeground(MainFrame.TEXT_GREY); } });
        filtres.add(txtFin);
        filtres.add(new JLabel("Type :"){ { setFont(new Font("Segoe UI",Font.BOLD,11)); setForeground(MainFrame.TEXT_GREY); } });
        filtres.add(cbType);
        filtres.add(btnFiltrer);
        filtres.add(btnExport);
        add(filtres, BorderLayout.NORTH);
 
        // ── Tableau ────────────────────────────────────────────
        String[] cols = {"Date","Type","Toner","Quantité","Motif","Utilisateur","Bon N°"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        chargerDonnees(null, null, null);
 
        JTable table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                String type = String.valueOf(getValueAt(row, 1));
                if ("ENTREE".equals(type)) c.setBackground(new Color(240, 250, 240));
                else                        c.setBackground(new Color(240, 245, 255));
                c.setForeground(MainFrame.TEXT_DARK);
                if (isRowSelected(row)) c.setBackground(new Color(220, 235, 255));
                return c;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(245, 245, 252));
        table.getTableHeader().setForeground(MainFrame.TEXT_GREY);
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        add(scroll, BorderLayout.CENTER);
 
        // Actions filtres
        btnFiltrer.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date debut = txtDebut.getText().trim().isEmpty() ? null : sdf.parse(txtDebut.getText().trim());
                Date fin   = txtFin.getText().trim().isEmpty()   ? null : sdf.parse(txtFin.getText().trim());
                String type = cbType.getSelectedIndex() == 0 ? null : (String) cbType.getSelectedItem();
                chargerDonnees(type, debut, fin);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez dd/MM/yyyy");
            }
        });
 
        btnExport.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Export Excel : intégrez Apache POI pour cette fonctionnalité.", "Info", JOptionPane.INFORMATION_MESSAGE));
    }
 
    private void chargerDonnees(String type, Date debut, Date fin) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<Mouvement> liste = (type == null && debut == null && fin == null)
            ? mouvDAO.findAll()
            : mouvDAO.findFiltre(type, debut, fin);
        for (Mouvement m : liste) {
            model.addRow(new Object[]{
                m.getDateMouvement() != null ? sdf.format(m.getDateMouvement()) : "—",
                m.getTypeMouvement(),
                m.getTonerRef(),
                (m.getTypeMouvement().equals("ENTREE") ? "+" : "-") + m.getQuantite(),
                m.getMotif(),
                m.getUtilisateurLogin(),
                m.getNumeroBon() != null ? m.getNumeroBon() : "—"
            });
        }
    }
}
