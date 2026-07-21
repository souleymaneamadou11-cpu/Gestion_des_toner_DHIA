/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ui.Panels;
import Config.DatabaseConfig;
import Model.Utilisateur;
import Ui.MainFrame;
import Util.PasswordUtil;
import Util.SessionManager;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author DELL
 */
public class ProfilPanel extends JPanel{
    private final MainFrame   parent;
    private final Utilisateur user;
 
    public ProfilPanel(MainFrame parent) {
        this.parent = parent;
        this.user   = SessionManager.getUtilisateur();
        setBackground(MainFrame.PAGE_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        build();
    }
 
    private void build() {
        // Avatar
        JPanel avatarCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        avatarCard.setBackground(MainFrame.CARD_BG);
        avatarCard.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
            new EmptyBorder(16, 20, 16, 20)));
        avatarCard.setMaximumSize(new Dimension(500, 80));
        avatarCard.setAlignmentX(LEFT_ALIGNMENT);
 
        // Cercle initiales
        JPanel circle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainFrame.DHIA_BLUE);
                g2.fillOval(0, 0, 48, 48);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String initials = String.valueOf(user.getPrenom().charAt(0)) + user.getNom().charAt(0);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, (48 - fm.stringWidth(initials)) / 2, 32);
                g2.dispose();
            }
        };
        circle.setPreferredSize(new Dimension(48, 48));
        circle.setBackground(MainFrame.CARD_BG);
 
        JPanel infos = new JPanel();
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBackground(MainFrame.CARD_BG);
        infos.setBorder(new EmptyBorder(0, 14, 0, 0));
 
        JLabel nom = new JLabel(user.getPrenom() + " " + user.getNom());
        nom.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nom.setForeground(MainFrame.TEXT_DARK);
 
        JLabel role = new JLabel(SessionManager.isAdmin() ? "👑 Administrateur" : "👤 Utilisateur");
        role.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        role.setForeground(MainFrame.TEXT_GREY);
 
        infos.add(nom); infos.add(role);
        avatarCard.add(circle); avatarCard.add(infos);
        add(avatarCard);
        add(Box.createVerticalStrut(16));
 
        // Formulaire changement login/mot de passe
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(MainFrame.CARD_BG);
        form.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
            new EmptyBorder(18, 20, 18, 20)));
        form.setMaximumSize(new Dimension(500, 220));
        form.setAlignmentX(LEFT_ALIGNMENT);
 
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(7, 0, 7, 12); g.weightx = 1;
 
        JTextField     fLogin = MainFrame.createStyledField("Nouveau login");
        JPasswordField fPwd1  = new JPasswordField();
        JPasswordField fPwd2  = new JPasswordField();
        fLogin.setText(user.getLogin());
        for (JPasswordField pf : new JPasswordField[]{fPwd1, fPwd2}) {
            pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            pf.setPreferredSize(new Dimension(0, 34));
            pf.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER_CLR), new EmptyBorder(0, 10, 0, 10)));
            pf.setBackground(new Color(248, 248, 252));
        }
 
        String[] labels = {"Nouveau login", "Nouveau mot de passe", "Confirmer le mot de passe"};
        JComponent[] fields = {fLogin, fPwd1, fPwd2};
        for (int i = 0; i < labels.length; i++) {
            g.gridy = i; g.gridx = 0; g.weightx = 0.35;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(MainFrame.TEXT_GREY);
            form.add(lbl, g);
            g.gridx = 1; g.weightx = 0.65;
            form.add(fields[i], g);
        }
        add(form);
        add(Box.createVerticalStrut(12));
 
        // Bouton enregistrer
        JButton btnSave = MainFrame.createPrimaryButton("💾  Enregistrer les modifications");
        btnSave.setAlignmentX(LEFT_ALIGNMENT);
        btnSave.addActionListener(e -> {
            String newLogin = fLogin.getText().trim();
            String pwd1 = new String(fPwd1.getPassword()).trim();
            String pwd2 = new String(fPwd2.getPassword()).trim();
 
            if (newLogin.isEmpty()) { JOptionPane.showMessageDialog(this, "Le login ne peut pas être vide."); return; }
            if (!pwd1.isEmpty()) {
                if (pwd1.length() < 6) { JOptionPane.showMessageDialog(this, "Mot de passe trop court (min. 6 caractères)."); return; }
                if (!pwd1.equals(pwd2)) { JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas."); return; }
            }
 
            try {
                String sql = pwd1.isEmpty()
                    ? "UPDATE utilisateurs SET login = ? WHERE id = ?"
                    : "UPDATE utilisateurs SET login = ?, mot_de_passe = ? WHERE id = ?";
                PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql);
                ps.setString(1, newLogin);
                if (!pwd1.isEmpty()) { ps.setString(2, PasswordUtil.sha256(pwd1)); ps.setInt(3, user.getId()); }
                else ps.setInt(2, user.getId());
                ps.executeUpdate();
 
                JOptionPane.showMessageDialog(this, "✅ Profil mis à jour avec succès !");
                fPwd1.setText(""); fPwd2.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });
        add(btnSave);
    }
    
}
