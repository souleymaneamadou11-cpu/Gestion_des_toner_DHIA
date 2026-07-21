/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Config.DatabaseConfig;
import Model.Utilisateur;
import java.sql.*;

/**
 *
 * @author DELL
 */
public class UtilisateurDAO {
    public Utilisateur findByLoginAndPassword(String login, String hash, String role) {
        String sql = "SELECT * FROM utilisateurs WHERE login=? AND mot_de_passe=? AND role=? AND actif=1";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, hash);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("login"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }  
}
