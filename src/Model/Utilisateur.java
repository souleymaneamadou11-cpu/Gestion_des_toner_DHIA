/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author DELL
 */
public class Utilisateur  {
     private int id;
    private String nom;
    private String prenom;
    private String login;
    private String role;
    private boolean actif = true;

    public Utilisateur() {}

    public Utilisateur(int id, String nom, String prenom, String login, String role) {
        this.id = id; this.nom = nom; this.prenom = prenom;
        this.login = login; this.role = role;
    }

    public int getId()       { return id; }
    public String getNom()   { return nom; }
    public String getPrenom(){ return prenom; }
    public String getLogin() { return login; }
    public String getRole()  { return role; }
    public boolean isActif()   { return actif; }
    
    
    public void setId(int id)         { this.id = id; }
    public void setNom(String nom)    { this.nom = nom; }
    public void setPrenom(String p)   { this.prenom = p; }
    public void setLogin(String l)    { this.login = l; }
    public void setRole(String r)     { this.role = r; }
    public void setActif(boolean a)   { this.actif = a; }

    
}
