/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

import Model.Utilisateur;

/**
 *
 * @author DELL
 */
public class SessionManager {
    private static Utilisateur utilisateurConnecte;

    public static void setUtilisateur(Utilisateur u) { utilisateurConnecte = u; }
    public static Utilisateur getUtilisateur()        { return utilisateurConnecte; }
    public static boolean isAdmin() {
        return utilisateurConnecte != null && "ADMIN".equals(utilisateurConnecte.getRole());
    }
    public static void deconnecter() { utilisateurConnecte = null; }

    
}
