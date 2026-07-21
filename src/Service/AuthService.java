/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Dao.UtilisateurDAO;
import Model.Utilisateur;
import Util.PasswordUtil;

/**
 *
 * @author DELL
 */
public class AuthService {
     private final UtilisateurDAO dao = new UtilisateurDAO();

    public Utilisateur authentifier(String login, String motDePasse, String role) {
        String hash = PasswordUtil.sha256(motDePasse);
        return dao.findByLoginAndPassword(login, hash, role);
    }

}
