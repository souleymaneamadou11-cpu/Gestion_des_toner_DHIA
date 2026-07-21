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
public class Toner {
     private int    id;
    private String reference;
    private String marque;
    private String modele;
    private String couleur;        // NOIR, CYAN, MAGENTA, JAUNE
    private String compatibilite;
    private int    quantiteStock;
    private int    seuilAlerte;
    private double prixUnitaire;
    private Date   dateExpiration;
    private Date   dateAjout;
 
    public Toner() {}
 
    public Toner(int id, String reference, String marque, String modele,
                 String couleur, String compatibilite, int quantiteStock,
                 int seuilAlerte, double prixUnitaire,
                 Date dateExpiration, Date dateAjout) {
        this.id             = id;
        this.reference      = reference;
        this.marque         = marque;
        this.modele         = modele;
        this.couleur        = couleur;
        this.compatibilite  = compatibilite;
        this.quantiteStock  = quantiteStock;
        this.seuilAlerte    = seuilAlerte;
        this.prixUnitaire   = prixUnitaire;
        this.dateExpiration = dateExpiration;
        this.dateAjout      = dateAjout;
    }
 
    // Getters
    public int    getId()             { return id; }
    public String getReference()      { return reference; }
    public String getMarque()         { return marque; }
    public String getModele()         { return modele; }
    public String getCouleur()        { return couleur; }
    public String getCompatibilite()  { return compatibilite; }
    public int    getQuantiteStock()  { return quantiteStock; }
    public int    getSeuilAlerte()    { return seuilAlerte; }
    public double getPrixUnitaire()   { return prixUnitaire; }
    public Date   getDateExpiration() { return dateExpiration; }
    public Date   getDateAjout()      { return dateAjout; }
 
    // Setters
    public void setId(int id)                      { this.id = id; }
    public void setReference(String r)             { this.reference = r; }
    public void setMarque(String m)                { this.marque = m; }
    public void setModele(String m)                { this.modele = m; }
    public void setCouleur(String c)               { this.couleur = c; }
    public void setCompatibilite(String c)         { this.compatibilite = c; }
    public void setQuantiteStock(int q)            { this.quantiteStock = q; }
    public void setSeuilAlerte(int s)              { this.seuilAlerte = s; }
    public void setPrixUnitaire(double p)          { this.prixUnitaire = p; }
    public void setDateExpiration(Date d)          { this.dateExpiration = d; }
    public void setDateAjout(Date d)               { this.dateAjout = d; }
 
    /** true si le stock est sous ou égal au seuil */
    public boolean isEnAlerte() {
        return quantiteStock <= seuilAlerte;
    }
 
    /** true si le stock est à zéro */
    public boolean isEnRupture() {
        return quantiteStock == 0;
    }
 
    @Override
    public String toString() {
        return reference + " — " + marque + " " + couleur;
    }
    
}
