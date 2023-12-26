package unishop;

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

    public int getId() {
        return id;
    }

    public int getPointsTotal() {
        return pointsTotal;
    }

    public float getCoutTotal() {
        return coutTotal;
    }
    public String getAcheteur() {
        return this.acheteur;
    }

    public String getEtat(){
        return etats[etat];
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
            this.reception = Main.obtenirTempsEnSecondes();
            save();
            return 0;
        }
        else if (estLivre())
            return 2;
        else
            return 1;
    }
    public ArrayList<String> getRevendeurs() {
        ArrayList<String> revs = new ArrayList<>();
        for (Produit p : produits) {
            if (!revs.contains(p.nomReven))
                revs.add(p.nomReven);
        }
        return revs;
    }
    public void addPastInfo(int id, String date, String adresse, String acheteur, long reception) {
        this.id = id;
        this.acheteur = acheteur;
        this.date = date;
        this.adresse = adresse;
        this.reception = reception;
    }
    public void addInitial(Produit p) {
        produits.add(p);
    }
    public void addProduit(Produit p) {
        this.produits.add(p);
        this.coutTotal = Main.arrondirPrix(this.coutTotal + p.prix);
        this.pointsTotal += p.getPoints();
        savePanier();
    }
    public void addProduitEchange(Produit p) {
        this.produits.add(p);
        save();
    }
    public void removeProduit(Produit p) {
        this.produits.remove(p);
        this.coutTotal = Main.arrondirPrix(this.coutTotal - p.prix);
        this.pointsTotal -= p.getPoints();
        savePanier();
    }
    public String afficher() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Total: " + coutTotal + "$; Points à accumuler: " + pointsTotal);
        for(Produit p : this.produits)
            sj.add(p.titre +  "; " + p.prix + "$; " + p.getPoints() + " points");
        return sj.toString();
    }
    public Commande passerCommande(String nom, String adresse) {
        String[] ids = Main.lireFichierEnEntier(Main.IDS);
        String[] fs = ids[0].split(",");
        id = Integer.parseInt(fs[0]);
        int produitID = Integer.parseInt(fs[1]);
        date = new java.util.Date().toString();
        StringJoiner sj = new StringJoiner("\n");
        String[] base = new String[] {String.valueOf(id), date, "0", String.valueOf(coutTotal),
                String.valueOf(pointsTotal), adresse, nom};
        sj.add(String.join(",", base));
        for(Produit p : this.produits) {
            sj.add(p.titre + "," + produitID);
            p.commander();
            p.setUniqueId(produitID);
            ++produitID;
        }
        this.adresse = adresse;
        this.acheteur = nom;
        Main.ecrireFichierEntier(Main.IDS, (id + 1) + "," + produitID);
        Main.ecrireFichierEntier(Main.COMMANDES_PATH + id + Main.CSV, sj.toString());
        return this;
    }
    public void savePanier(){
        String path = Main.ACHETEURS_PATH + Main.getConnectedUsername() + "/Panier.csv";
        StringJoiner sj = new StringJoiner("\n");
        sj.add(coutTotal + "," + pointsTotal);
        for(Produit p : produits)
            sj.add(p.titre);
        Main.ecrireFichierEntier(path, sj.toString());
    }
    public void save() {
        StringJoiner sj = new StringJoiner("\n");
        String[] base = new String[] {String.valueOf(id), date, String.valueOf(etat), String.valueOf(coutTotal),
                String.valueOf(pointsTotal), adresse, acheteur, String.valueOf(reception)};
        sj.add(String.join(",", base));
        for(Produit p : this.produits)
            sj.add(p.titre + "," + p.getId());
        Main.ecrireFichierEntier(Main.COMMANDES_PATH + id + Main.CSV, sj.toString());
    }
    public void vider() {
        pointsTotal = 0;
        coutTotal = 0;
        produits.clear();
        savePanier();
    }
    public Produit getChoixProduit(boolean menuOption) {
        System.out.println("\nChoisissez un produit: ");
        Produit[] ps = produits.toArray(new Produit[0]);
        String[] s;
        if (menuOption)
            s = new String[ps.length + 1];
        else
            s = new String[ps.length];
        int i = 0;
        boolean isPanier = id == 0;
        for (Produit p : ps) {
            if (isPanier)
                s[i] = p.titre;
            else
                s[i] = p.titre + "; ID: " + p.getId();
            ++i;
        }
        if (menuOption)
            s[ps.length] = "Retour au menu";
        short c = Main.selectionChoix(s);
        if (c == s.length && menuOption)
            return  null;
        return ps[c - 1];
    }
    public String[] getProduits() {
        Produit[] ps = produits.toArray(new Produit[0]);
        String[] s = new String[produits.size()];
        int i = 0;
        for (Produit p : ps) {
            s[i] = p.titre;
            ++i;
        }
        return s;
    }
    public boolean estVide() {
        return produits.isEmpty();
    }
    public String getMenuDisplay() {
        return "ID: " + id + " ; Date: " + date + " ; Total: " + coutTotal + "$";
    }
    public ArrayList<Produit> getProduitsP() { return new ArrayList<>(this.produits);}
    public long getTempsReception() {
        return this.reception;
    }
    public String getDate() {
        return date;
    }
}
