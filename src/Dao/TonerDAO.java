/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;
import Config.DatabaseConfig;
import Model.Toner;
import java.sql.*;
import java.util.*;

/**
 *
 * @author DELL
 */
public class TonerDAO {
    public List<Toner> findAll() {
        List<Toner> list = new ArrayList<>();
        String sql = "SELECT * FROM toners ORDER BY marque, couleur";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
 
    /** Récupère un toner par son ID */
    public Toner findById(int id) {
        String sql = "SELECT * FROM toners WHERE id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
 
    /** Toners dont le stock est inférieur ou égal au seuil */
    public List<Toner> findEnAlerte() {
        List<Toner> list = new ArrayList<>();
        String sql = "SELECT * FROM toners WHERE quantite_stock <= seuil_alerte ORDER BY quantite_stock ASC";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
 
    /** Toners en rupture totale (stock = 0) */
    public List<Toner> findEnRupture() {
        List<Toner> list = new ArrayList<>();
        String sql = "SELECT * FROM toners WHERE quantite_stock = 0";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
 
    /** Stock total par couleur — retourne Map<couleur, total> */
    public Map<String, Integer> stockParCouleur() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT couleur, SUM(quantite_stock) AS total FROM toners GROUP BY couleur";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("couleur"), rs.getInt("total"));
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
 
    /** Valeur totale du stock (prix * quantité) */
    public double valeurTotaleStock() {
        String sql = "SELECT SUM(quantite_stock * prix_unitaire) AS total FROM toners";
        try (Statement st = DatabaseConfig.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
 
    /** Insère un nouveau toner */
    public boolean insert(Toner t) {
        String sql = "INSERT INTO toners (reference, marque, modele, couleur, " +
                     "compatibilite, quantite_stock, seuil_alerte, prix_unitaire, date_expiration) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getReference());
            ps.setString(2, t.getMarque());
            ps.setString(3, t.getModele());
            ps.setString(4, t.getCouleur());
            ps.setString(5, t.getCompatibilite());
            ps.setInt   (6, t.getQuantiteStock());
            ps.setInt   (7, t.getSeuilAlerte());
            ps.setDouble(8, t.getPrixUnitaire());
            if (t.getDateExpiration() != null)
                ps.setDate(9, new java.sql.Date(t.getDateExpiration().getTime()));
            else
                ps.setNull(9, Types.DATE);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
 
    /** Modifie un toner existant */
    public boolean update(Toner t) {
        String sql = "UPDATE toners SET reference=?, marque=?, modele=?, couleur=?, " +
                     "compatibilite=?, quantite_stock=?, seuil_alerte=?, prix_unitaire=?, " +
                     "date_expiration=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getReference());
            ps.setString(2, t.getMarque());
            ps.setString(3, t.getModele());
            ps.setString(4, t.getCouleur());
            ps.setString(5, t.getCompatibilite());
            ps.setInt   (6, t.getQuantiteStock());
            ps.setInt   (7, t.getSeuilAlerte());
            ps.setDouble(8, t.getPrixUnitaire());
            if (t.getDateExpiration() != null)
                ps.setDate(9, new java.sql.Date(t.getDateExpiration().getTime()));
            else
                ps.setNull(9, Types.DATE);
            ps.setInt(10, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
 
    /** Met à jour uniquement le stock */
    public boolean updateStock(int id, int nouvelleQuantite) {
        String sql = "UPDATE toners SET quantite_stock = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, nouvelleQuantite);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
 
    /** Supprime un toner */
    public boolean delete(int id) {
        String sql = "DELETE FROM toners WHERE id = ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
 
    /** Mapping ResultSet → Toner */
    private Toner mapper(ResultSet rs) throws SQLException {
        Toner t = new Toner();
        t.setId(rs.getInt("id"));
        t.setReference(rs.getString("reference"));
        t.setMarque(rs.getString("marque"));
        t.setModele(rs.getString("modele"));
        t.setCouleur(rs.getString("couleur"));
        t.setCompatibilite(rs.getString("compatibilite"));
        t.setQuantiteStock(rs.getInt("quantite_stock"));
        t.setSeuilAlerte(rs.getInt("seuil_alerte"));
        t.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        t.setDateExpiration(rs.getDate("date_expiration"));
        t.setDateAjout(rs.getDate("date_ajout"));
        return t;
    }
    
}
