/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import Config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author DELL
 */
public class Test {
    public static  void main(String[] args) throws SQLException{
        Connection conn=null;
        DatabaseConfig nouv=new DatabaseConfig();
        // conn=nouv.getConnection();
        conn=nouv.getConnection();
        if (conn!=null){
            System.out.println("la connexion est etablie");
        }
    
}
}
