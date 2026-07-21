/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;
import Config.DatabaseConfig;
import Model.Mouvement;
import java.sql.*;
import java.util.*;
import java.util.Date;
 
/**
 *
 * @author DELL
 */
public class MouvementDAO {
    public boolean insert(Mouvement m) {
    String sql = "INSERT INTO mouvements " +
                 "(toner_id, type_mouvement, quantite, motif, numero_bon, utilisateur_id) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
        ps.setInt   (1, m.getTonerId());
        ps.setString(2, m.getTypeMouvement());
        ps.setInt   (3, m.getQuantite());
        ps.setString(4, m.getMotif() != null ? m.getMotif() : "");
        // numero_bon peut être null pour les entrées
        if (m.getNumeroBon() != null && !m.getNumeroBon().isEmpty())
            ps.setString(5, m.getNumeroBon());
        else
            ps.setNull(5, java.sql.Types.VARCHAR);
        ps.setInt(6, m.getUtilisateurId());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
 
    /** Récupère tous les mouvements avec infos toner et utilisateur */
    public List<Mouvement> findAll() {
    List<Mouvement> list = new ArrayList<>();
    String sql = "SELECT m.*, " +
                 "t.reference AS toner_ref, " +
                 "u.login AS user_login " +
                 "FROM mouvements m " +
                 "LEFT JOIN toners t ON m.toner_id = t.id " +
                 "LEFT JOIN utilisateurs u ON m.utilisateur_id = u.id " +
                 "ORDER BY m.date_mouvement DESC";
    try (Statement st = DatabaseConfig.getConnection().createStatement();
         ResultSet rs = st.executeQuery(sql)) {
        while (rs.next()) list.add(mapper(rs));
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
 
    /** Filtre par type et/ou dates */
    public List<Mouvement> findFiltre(String type, Date debut, Date fin) {
    List<Mouvement> list = new ArrayList<>();
    StringBuilder sql = new StringBuilder(
        "SELECT m.*, t.reference AS toner_ref, u.login AS user_login " +
        "FROM mouvements m " +
        "LEFT JOIN toners t ON m.toner_id = t.id " +
        "LEFT JOIN utilisateurs u ON m.utilisateur_id = u.id " +
        "WHERE 1=1");

    if (type != null && !type.isEmpty())
        sql.append(" AND m.type_mouvement = '").append(type).append("'");
    if (debut != null)
        sql.append(" AND DATE(m.date_mouvement) >= '")
           .append(new java.sql.Date(debut.getTime())).append("'");
    if (fin != null)
        sql.append(" AND DATE(m.date_mouvement) <= '")
           .append(new java.sql.Date(fin.getTime())).append("'");

    sql.append(" ORDER BY m.date_mouvement DESC");

    try (Statement st = DatabaseConfig.getConnection().createStatement();
         ResultSet rs = st.executeQuery(sql.toString())) {
        while (rs.next()) list.add(mapper(rs));
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
 
    /** Quantité totale sortie par mois pour une année */
    public Map<Integer, Integer> sortiesParMois(int annee) {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) map.put(i, 0);
        String sql = "SELECT MONTH(date_mouvement) AS mois, SUM(quantite) AS total " +
                     "FROM mouvements WHERE type_mouvement='SORTIE' " +
                     "AND YEAR(date_mouvement) = ? GROUP BY MONTH(date_mouvement)";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, annee);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getInt("mois"), rs.getInt("total"));
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
 
    /** Coût total des sorties par mois pour une année */
    public Map<Integer, Double> coutSortiesParMois(int annee) {
        Map<Integer, Double> map = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) map.put(i, 0.0);
        String sql = "SELECT MONTH(m.date_mouvement) AS mois, " +
                     "SUM(m.quantite * t.prix_unitaire) AS cout " +
                     "FROM mouvements m JOIN toners t ON m.toner_id = t.id " +
                     "WHERE m.type_mouvement='SORTIE' AND YEAR(m.date_mouvement) = ? " +
                     "GROUP BY MONTH(m.date_mouvement)";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setInt(1, annee);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getInt("mois"), rs.getDouble("cout"));
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
 
    /** Coût total entre deux dates */
    public double coutEntreDates(Date debut, Date fin) {
        String sql = "SELECT SUM(m.quantite * t.prix_unitaire) AS cout " +
                     "FROM mouvements m JOIN toners t ON m.toner_id = t.id " +
                     "WHERE m.type_mouvement='SORTIE' " +
                     "AND m.date_mouvement BETWEEN ? AND ?";
        try (PreparedStatement ps = DatabaseConfig.getConnection().prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(debut.getTime()));
            ps.setDate(2, new java.sql.Date(fin.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("cout");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
 
    private Mouvement mapper(ResultSet rs) throws SQLException {
    Mouvement m = new Mouvement();
    m.setId(rs.getInt("id"));
    m.setTonerId(rs.getInt("toner_id"));
    m.setTonerRef(rs.getString("toner_ref"));
    m.setTypeMouvement(rs.getString("type_mouvement"));
    m.setQuantite(rs.getInt("quantite"));
    m.setMotif(rs.getString("motif"));
    m.setNumeroBon(rs.getString("numero_bon")); // peut être null, c'est OK
    m.setUtilisateurId(rs.getInt("utilisateur_id"));
    m.setUtilisateurLogin(rs.getString("user_login"));
    m.setDateMouvement(rs.getTimestamp("date_mouvement"));
    return m;
}
}
