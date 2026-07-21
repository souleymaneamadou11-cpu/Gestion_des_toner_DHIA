/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;
import Dao.MouvementDAO;
import Dao.TonerDAO;
import Model.Mouvement;
import Model.Toner;
import Util.SessionManager;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL
 */
public class StockService {
    private final TonerDAO     tonerDAO     = new TonerDAO();
    private final MouvementDAO mouvementDAO = new MouvementDAO();
 
    public List<Toner>  getTousLesToners()       { return tonerDAO.findAll(); }
    public List<Toner>  getTonersEnAlerte()       { return tonerDAO.findEnAlerte(); }
    public List<Toner>  getTonersEnRupture()      { return tonerDAO.findEnRupture(); }
    public double       getValeurTotaleStock()    { return tonerDAO.valeurTotaleStock(); }
    public Map<String,Integer> getStockParCouleur(){ return tonerDAO.stockParCouleur(); }
 
    /**
     * Enregistre une entrée en stock :
     * 1. Ajoute le mouvement ENTREE
     * 2. Met à jour la quantité du toner
     */
    public boolean entreeStock(int tonerId, int quantite, String motif, String fournisseur) {
        Toner t = tonerDAO.findById(tonerId);
        if (t == null) return false;
 
        Mouvement m = new Mouvement();
        m.setTonerId(tonerId);
        m.setTypeMouvement("ENTREE");
        m.setQuantite(quantite);
        m.setMotif((motif != null ? motif : "") +
                   (fournisseur != null && !fournisseur.isEmpty() ? " | Fournisseur: " + fournisseur : ""));
        m.setUtilisateurId(SessionManager.getUtilisateur().getId());
 
        boolean ok = mouvementDAO.insert(m);
        if (ok) {
            t.setQuantiteStock(t.getQuantiteStock() + quantite);
            tonerDAO.updateStock(tonerId, t.getQuantiteStock());
        }
        return ok;
    }
 
    /**
     * Enregistre une sortie de stock :
     * 1. Vérifie la disponibilité
     * 2. Ajoute le mouvement SORTIE
     * 3. Décrémente le stock
     * @return "OK" si succès, message d'erreur sinon
     */
    public String sortieStock(int tonerId, int quantite, String motif, String numeroBon) {
        Toner t = tonerDAO.findById(tonerId);
        if (t == null) return "Toner introuvable.";
        if (t.getQuantiteStock() < quantite)
            return "Stock insuffisant : " + t.getQuantiteStock() + " disponible(s).";
 
        Mouvement m = new Mouvement();
        m.setTonerId(tonerId);
        m.setTypeMouvement("SORTIE");
        m.setQuantite(quantite);
        m.setMotif(motif != null ? motif : "");
        m.setNumeroBon(numeroBon);
        m.setUtilisateurId(SessionManager.getUtilisateur().getId());
 
        boolean ok = mouvementDAO.insert(m);
        if (ok) {
            int nouveau = Math.max(0, t.getQuantiteStock() - quantite);
            tonerDAO.updateStock(tonerId, nouveau);
        }
        return ok ? "OK" : "Erreur lors de l'enregistrement.";
    }
 
    /** Génère un numéro de bon unique */
    public String genererNumeroBon() {
        return "BS-" + System.currentTimeMillis();
    }
    
}
