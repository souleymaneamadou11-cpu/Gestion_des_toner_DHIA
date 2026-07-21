/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Date;
/**
 *
 * @author DELL
 */
public class Mouvement {
    private int    id;
    private int    tonerId;
    private String tonerRef;       // Pour affichage (jointure)
    private String typeMouvement;  // ENTREE ou SORTIE
    private int    quantite;
    private String motif;
    private String numeroBon;      // Numéro bon de sortie (null pour entrée)
    private int    utilisateurId;
    private String utilisateurLogin; // Pour affichage
    private Date   dateMouvement;
 
    public Mouvement() {}
 
    // Getters
    public int    getId()                { return id; }
    public int    getTonerId()           { return tonerId; }
    public String getTonerRef()          { return tonerRef; }
    public String getTypeMouvement()     { return typeMouvement; }
    public int    getQuantite()          { return quantite; }
    public String getMotif()             { return motif; }
    public String getNumeroBon()         { return numeroBon; }
    public int    getUtilisateurId()     { return utilisateurId; }
    public String getUtilisateurLogin()  { return utilisateurLogin; }
    public Date   getDateMouvement()     { return dateMouvement; }
 
    // Setters
    public void setId(int id)                           { this.id = id; }
    public void setTonerId(int tonerId)                 { this.tonerId = tonerId; }
    public void setTonerRef(String tonerRef)            { this.tonerRef = tonerRef; }
    public void setTypeMouvement(String t)              { this.typeMouvement = t; }
    public void setQuantite(int quantite)               { this.quantite = quantite; }
    public void setMotif(String motif)                  { this.motif = motif; }
    public void setNumeroBon(String numeroBon)          { this.numeroBon = numeroBon; }
    public void setUtilisateurId(int utilisateurId)     { this.utilisateurId = utilisateurId; }
    public void setUtilisateurLogin(String login)       { this.utilisateurLogin = login; }
    public void setDateMouvement(Date dateMouvement)    { this.dateMouvement = dateMouvement; }

    
}
