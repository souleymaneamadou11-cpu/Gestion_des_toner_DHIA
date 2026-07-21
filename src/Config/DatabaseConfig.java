/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author DELL
 */
public class DatabaseConfig {
     private static final String URL  = "jdbc:mysql://localhost:3306/gestion_stocks_toners"
                                     + "?connectTimeout=3000"   // timeout connexion 3 sec
                                     + "&socketTimeout=5000"    // timeout socket 5 sec
                                     + "&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = ""; // votre mot de passe MySQL

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                // isValid(2) teste activement la connexion avec timeout 2 sec
                connection = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (SQLException e) {
            connection = null; // reset pour forcer une nouvelle tentative la prochaine fois
            throw e;           // relance l'exception pour que doLogin() la capte
        }
        return connection;
    }

    /** Ferme proprement la connexion */
    public static void fermer() {
        if (connection != null) {
            try { connection.close(); } catch (Exception ignored) {}
            connection = null;
        }
    }

   /* private static final String URL  = "jdbc:mysql://localhost:3306/gestion_stocks_toners";
    private static final String USER = "root";
    private static final String PASS = "";

   private static Connection connection;

    public static Connection getConnection() throws SQLException {
            //Connection connection=null;
        
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASS);
        }
        return connection;
    }*/
}

