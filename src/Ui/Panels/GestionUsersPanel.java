/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui.Panels;
import Config.DatabaseConfig;
import Model.Utilisateur;
import Ui.MainFrame;
import Util.PasswordUtil;
 
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
/**
 *
 * @author DELL
 */
public class GestionUsersPanel extends JPanel {
    private final MainFrame    parent;
    private DefaultTableModel  model;
    private List<Utilisateur>  users = new ArrayList<>();
 
    public GestionUsersPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(MainFrame.PAGE_BG);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        build();
    }
 
    private void build() {
        // Barre supérieure
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(MainFrame.PAGE_BG);
        JButton btnAjouter = MainFrame.createPrimaryButton("👤  Nouvel utilisateur");
        btnAjouter.addActionListener(e -> ouvrirFormulaire(null));
        top.add(btnAjouter, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
 
        // Tableau
        String[] cols = {"Nom","Prénom","Login","Rôle","Statut","Créé le","Actions"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        chargerUsers();
 
        JTable table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                String statut = String.valueOf(getValueAt(row, 4));
                c.setBackground(statut.equals("Actif") ? Color.WHITE : new Color(248, 248, 252));
                c.setForeground(statut.equals("Actif") ? MainFrame.TEXT_DARK : MainFrame.TEXT_GREY);
                return c;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(245, 245, 252));
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        add(scroll, BorderLayout.CENTER);
 
        // Boutons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setBackground(MainFrame.PAGE_BG);
 
        JButton btnEdit = MainFrame.createSecondaryButton("✏️  Modifier");
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur."); return; }
            ouvrirFormulaire(users.get(row));
        });
 
        JButton btnToggle = MainFrame.createSecondaryButton("🔄  Activer / Désactiver");
        btnToggle.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur."); return; }
            toggleActif(users.get(row));
        });
 
        JButton btnResetPwd = MainFrame.createSecondaryButton("🔑  Réinit. mot de passe");
        btnResetPwd.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur."); return; }
            reinitMotDePasse(users.get(row));
        });
 
        btns.add(btnEdit); btns.add(btnToggle); btns.add(btnResetPwd);
        add(btns, BorderLayout.SOUTH);
    }
 
    private void chargerUsers() {
        model.setRowCount(0);
        users.clear();
        String sql = "SELECT * FROM utilisateurs ORDER BY role, nom";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                Utilisateur u = new Utilisateur(
                    rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"),
                    rs.getString("login"), rs.getString("role"));
                u.setActif(rs.getInt("actif") == 1);
                users.add(u);
                model.addRow(new Object[]{
                    u.getNom(), u.getPrenom(), u.getLogin(),
                    u.getRole().equals("ADMIN") ? "👑 ADMIN" : "👤 Utilisateur",
                    u.isActif() ? "Actif" : "Inactif",
                    rs.getDate("date_creation") != null ? sdf.format(rs.getDate("date_creation")) : "—",
                    "—"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
 
    private void ouvrirFormulaire(Utilisateur user) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            user == null ? "Nouvel utilisateur" : "Modifier — " + user.getLogin(), true);
        dlg.setSize(420, 340);
        dlg.setLocationRelativeTo(this);
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(MainFrame.CARD_BG);
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(6, 0, 6, 10); g.weightx = 1;
 
        JTextField fNom    = MainFrame.createStyledField("Nom de famille");
        JTextField fPrenom = MainFrame.createStyledField("Prénom");
        JTextField fLogin  = MainFrame.createStyledField("Identifiant de connexion");
        JPasswordField fPwd = new JPasswordField();
        fPwd.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fPwd.setPreferredSize(new Dimension(0, 34));
        fPwd.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR), new EmptyBorder(0, 10, 0, 10)));
        JComboBox<String> fRole = new JComboBox<>(new String[]{"UTILISATEUR","ADMIN"});
        fRole.setPreferredSize(new Dimension(0, 34));
 
        if (user != null) {
            fNom.setText(user.getNom()); fPrenom.setText(user.getPrenom());
            fLogin.setText(user.getLogin()); fRole.setSelectedItem(user.getRole());
        }
 
        String[][] rows = {};
        JComponent[] fields = {fNom, fPrenom, fLogin, fPwd, fRole};
        String[] labels = {"Nom *","Prénom *","Login *",user==null?"Mot de passe *":"Nouveau mot de passe","Rôle *"};
 
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
            if (fNom.getText().trim().isEmpty() || fLogin.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nom et Login sont obligatoires.");
                return;
            }
            String pwd = new String(fPwd.getPassword()).trim();
            if (user == null && pwd.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Le mot de passe est obligatoire.");
                return;
            }
            try {
                if (user == null) {
                    String sql = "INSERT INTO utilisateurs (nom, prenom, login, mot_de_passe, role) VALUES (?,?,?,?,?)";
                    PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql);
                    ps.setString(1, fNom.getText().trim());
                    ps.setString(2, fPrenom.getText().trim());
                    ps.setString(3, fLogin.getText().trim());
                    ps.setString(4, PasswordUtil.sha256(pwd));
                    ps.setString(5, (String) fRole.getSelectedItem());
                    ps.executeUpdate();
                } else {
                    String sql = pwd.isEmpty()
                        ? "UPDATE utilisateurs SET nom=?, prenom=?, login=?, role=? WHERE id=?"
                        : "UPDATE utilisateurs SET nom=?, prenom=?, login=?, role=?, mot_de_passe=? WHERE id=?";
                    PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql);
                    ps.setString(1, fNom.getText().trim());
                    ps.setString(2, fPrenom.getText().trim());
                    ps.setString(3, fLogin.getText().trim());
                    ps.setString(4, (String) fRole.getSelectedItem());
                    if (!pwd.isEmpty()) { ps.setString(5, PasswordUtil.sha256(pwd)); ps.setInt(6, user.getId()); }
                    else ps.setInt(5, user.getId());
                    ps.executeUpdate();
                }
                chargerUsers();
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Erreur : " + ex.getMessage());
            }
        });
        JButton btnAnnuler3 = MainFrame.createSecondaryButton("Annuler");
        btnAnnuler3.addActionListener(e -> dlg.dispose());
        btnRow.add(btnAnnuler3);
        btnRow.add(btnSave);
 
        dlg.setLayout(new BorderLayout());
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
 
    private void toggleActif(Utilisateur u) {
        String sql = "UPDATE utilisateurs SET actif = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, u.isActif() ? 0 : 1);
            ps.setInt(2, u.getId());
            ps.executeUpdate();
            chargerUsers();
        } catch (SQLException e) { e.printStackTrace(); }
    }
 
    private void reinitMotDePasse(Utilisateur u) {
        String pwd = JOptionPane.showInputDialog(this, "Nouveau mot de passe pour " + u.getLogin() + " :");
        if (pwd == null || pwd.trim().isEmpty()) return;
        if (pwd.length() < 6) { JOptionPane.showMessageDialog(this, "6 caractères minimum."); return; }
        String sql = "UPDATE utilisateurs SET mot_de_passe = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.sha256(pwd));
            ps.setInt(2, u.getId());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Mot de passe réinitialisé avec succès !");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    
}
