package unishop;

import static unishop.Main.*;

import java.util.*;

public class Commande {

    private final String[] etats = new String[]{"en production", "en cours de livraison", "livré"};
    private short etat;
    private float coutTotal;
    private int pointsTotal;
    private String acheteur;
    private String date;
    private long reception;
    private int id;
    private ArrayList<Produit> produits;
    private String adresse;

    public Commande (short etat, float coutTotal, int pointsTotal) {
        this.etat = etat;
        this.coutTotal = coutTotal;
        this.pointsTotal = pointsTotal;
        this.produits = new ArrayList<>();
    }
    public Commande copy() {
        Commande c = new Commande(etat, coutTotal, pointsTotal);
        c.addPastInfo(id, date, adresse, acheteur, reception);
        c.produits = new ArrayList<>(this.produits);
        return c;
    }

    public int getId() { return id; }

    public int getPointsTotal() { return pointsTotal; }

    public float getCoutTotal() { return coutTotal; }
    public String getAcheteur() { return this.acheteur; }

    public String getEtat() { return etats[etat]; }
    public ArrayList<Produit> getProduits() { return new ArrayList<>(this.produits);}
    public long getTempsReception() {
        return this.reception;
    }
    public ArrayList<String> getRevendeurs() {
        ArrayList<String> revs = new ArrayList<>();
        for (Produit p : produits) {
            if (!revs.contains(p.getNomReven()))
                revs.add(p.getNomReven());
        }
        return revs;
    }
    public boolean estVide() {
        return produits.isEmpty();
    }
    public String getMenuDisplay() {
        return "ID: " + id + " ; Date: " + date + " ; Total: " + coutTotal + "$";
    }
    public boolean estEnProduction() {return etat == 0;}
    public boolean estEnLivraison() {return etat == 1;}
    public boolean estLivre() {return etat == 2;}
    public void mettreEnLivraison() {
        ++this.etat;
        save();
    }
    public short confirmerLivraison() {
        if (this.estEnLivraison()) {
            ++this.etat;
            this.reception = obtenirTempsEnSecondes();
            save();
            return 0;
        }
        else if (estLivre())
            return 2;
        else
            return 1;
    }
    public void addPastInfo(int id, String date, String adresse, String acheteur, long reception) {
        this.id = id;
        this.date = date;
        this.adresse = adresse;
        this.acheteur = acheteur;
        this.reception = reception;
    }
    public void addInitial(Produit p) {
        produits.add(p);
    }
    public void addProduit(Produit p) {
        this.produits.add(p);
        this.coutTotal = arrondirPrix(this.coutTotal + p.getPrix());
        this.pointsTotal += p.getPoints();
        savePanier();
    }
    public void removeProduit(Produit p) {
        this.produits.remove(p);
        this.coutTotal = arrondirPrix(this.coutTotal - p.getPrix());
        this.pointsTotal -= p.getPoints();
        savePanier();
    }
    public void setEchange(Produit p, float prix) {
        this.produits.add(p);
        this.coutTotal = prix;
    }

    public String afficher() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Total: " + coutTotal + "$; Points à accumuler: " + pointsTotal);
        for(Produit p : this.produits)
            sj.add(p.getTitre() +  "; " + p.getPrix() + "$; " + p.getPoints() + " points");
        return sj.toString();
    }
    public Commande passerCommande(String nom, String adresse) {
        String[] ids = lireFichierEnEntier(IDS);
        String[] fs = ids[0].split(",");
        id = Integer.parseInt(fs[0]);
        int produitID = Integer.parseInt(fs[1]);
        date = new java.util.Date().toString();
        StringJoiner sj = new StringJoiner("\n");
        String[] base = new String[] {String.valueOf(id), date, "0", String.valueOf(coutTotal),
                String.valueOf(pointsTotal), adresse, nom};
        sj.add(String.join(",", base));
        for(Produit p : this.produits) {
            sj.add(p.getTitre() + "," + produitID);
            p.commander();
            p.setUniqueId(produitID);
            ++produitID;
        }
        this.adresse = adresse;
        this.acheteur = nom;
        ecrireFichierEntier(IDS, (id + 1) + "," + produitID);
        ecrireFichierEntier(COMMANDES_PATH + id + CSV, sj.toString());
        return this;
    }
    public void vider() {
        pointsTotal = 0;
        coutTotal = 0;
        produits.clear();
        savePanier();
    }
    public void savePanier(){
        String path = ACHETEURS_PATH + getConnectedUsername() + "/" + PANIER;
        StringJoiner sj = new StringJoiner("\n");
        sj.add(coutTotal + "," + pointsTotal);
        for(Produit p : produits)
            sj.add(p.getTitre());
        ecrireFichierEntier(path, sj.toString());
    }
    public void save() {
        StringJoiner sj = new StringJoiner("\n");
        String[] base = new String[] {String.valueOf(id), date, String.valueOf(etat), String.valueOf(coutTotal),
                String.valueOf(pointsTotal), adresse, acheteur, String.valueOf(reception)};
        sj.add(String.join(",", base));
        for(Produit p : this.produits)
            sj.add(p.getTitre() + "," + p.getId());
        ecrireFichierEntier(COMMANDES_PATH + id + CSV, sj.toString());
    }
}
