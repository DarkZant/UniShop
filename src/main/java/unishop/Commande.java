package unishop;

import java.io.IOException;
import java.util.*;

/**
 * Cette classe représente une commande avec ses différents états, coûts, points et les produits associés.
 */
public class Commande {

    private final String[] etats = new String[]{"en production", "en cours de livraison", "livré"};
    private short etat;
    private float coutTotal;
    private int pointsTotal;
    private String date;
    private long reception;
    private int id;
    private ArrayList<Produit> produits;
    private String adresse;

    /**
     * Constructeur de la classe Commande.
     *
     * @param etat       L'état initial de la commande.
     * @param coutTotal  Le coût total de la commande.
     * @param pointsTotal Le nombre total de points générés par la commande.
     */
    public Commande (short etat, float coutTotal, int pointsTotal) {
        this.etat = etat;
        this.coutTotal = coutTotal;
        this.pointsTotal = pointsTotal;
        this.produits = new ArrayList<>();
    }

    /**
     * Crée une copie de la commande.
     *
     * @return Une copie de la commande.
     */
    public Commande copy() {
        Commande c = new Commande(etat, coutTotal, pointsTotal);
        c.addPastInfo(id, date, adresse, reception);
        c.produits = new ArrayList<>(this.produits);
        return c;
    }
    /**
     * Renvoie l'identifiant de la commande.
     *
     * @return L'identifiant de la commande.
     */
    public int getId() {
        return id;
    }

    /**
     * Renvoie les points totals de la commande
     *
     * @return les points totals de la commande
     */
    public int getPointsTotal() {
        return pointsTotal;
    }

    /**
     * Renvoie le coût total de la commande
     *
     * @return le coût total de la commande
     */
    public float getCoutTotal() {
        return coutTotal;
    }

    /**
     * Renvoie l'état de la commande
     * @return l'état de la commande
     */
    public String getEtat(){
        return etats[etat];
    }

    /**
     * Vérifie si la commande est en production
     *
     * @return True si la commande est en production, sinon False
     */
    public boolean estEnProduction() {return etat == 0;}

    /**
     * Vérifie si la commande est en cours de livraison
     *
     * @return True si la commande est en cours de livraison et False sinon
     */
    public boolean estEnLivraison() {return etat == 1;}

    /** Vérifie si la commande a été livré
     *
     * @return True si la commande a été livré et False sinon
     */
    public boolean estLivre() {return etat == 2;}

    /**
     * Met la commande est état de livraison
     *
     * @return 0 si la commande est en cours de livraison, 1 si elle était déja en cours de livraison et 2 si ce n'est pas le cas
     */
    public short mettreEnLivraison() {
        if (this.estEnProduction()) {
            ++this.etat;
            save();
            return 0;
        }
        else if (estEnLivraison())
            return 1;
        else
            return 2;
    }

    /**
     * Confirme la livraison de la commande
     *
     * @return 0 si la commande est en cours de livraison, 2 si la commande à été livré et 1 sinon
     */
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

    /**
     * Retourne l'ensemble des revendeurs
     *
     * @return Renvoies l'ensemble des revendeurs
     */
    public ArrayList<String> getRevendeurs() {
        ArrayList<String> revs = new ArrayList<>();
        for (Produit p : produits) {
            if (!revs.contains(p.nomReven))
                revs.add(p.nomReven);
        }
        return revs;
    }

    /**
     * Ajoutes les informations mises en paramètre au produit
     * @param id L'id que l'on souhaite ajouter
     * @param date La date que l'on souhaite ajouter
     * @param adresse L'adresse que l'on souhaite ajouter
     * @param reception La réception que l'on souhaite ajouter
     */
    public void addPastInfo(int id, String date, String adresse, long reception) {
        this.id = id;
        this.date = date;
        this.adresse = adresse;
        this.reception = reception;
    }

    /**
     * Ajoutes un prooduit au panier en mettant à jour le coût total, le nombre de points total de celui-ci
     * @param p Le produit que l'on souhaite ajouter au panier
     */
    public void addInitial(Produit p) {
        produits.add(p);
    }
    public void addProduit(Produit p) {
        this.produits.add(p);
        this.coutTotal = Main.arrondirPrix(this.coutTotal + p.prix);
        this.pointsTotal += p.getPoints();
        savePanier();
    }

    /**
     * Ajoutes un produit à echanger au panier
     * @param p Le produit que l'on souhaite ajouter au panier
     */
    public void addProduitEchange(Produit p) {
        this.produits.add(p);
    }

    /**
     * Retires un produit du panier
     * @param p Le produit que l'on souhaite retirer du panier
     */
    public void removeProduit(Produit p) {
        this.produits.remove(p);
        this.coutTotal = Main.arrondirPrix(this.coutTotal - p.prix);
        this.pointsTotal -= p.getPoints();
        savePanier();
    }

    /** Affiche le coût et le nombre de points du panier. En plus du coût et du nombre de points des produits du panier
     * @return Renvoies le coût et le nombre de points du panier. En plus du coût et du nombre de points des produits du panier
     */
    public String afficher() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Total: " + coutTotal + "$; Points à accumuler: " + pointsTotal);
        for(Produit p : this.produits)
            sj.add(p.titre +  "; " + p.prix + "$; " + p.getPoints() + " points");
        return sj.toString();
    }

    /**
     * Passe une commande
     * @param adresse
     * @return
     * @throws IOException
     */
    public Commande passerCommande(String adresse) throws IOException {
        String[] ids = Main.lireFichierEnEntier(Main.IDS);
        String[] fs = ids[0].split(",");
        id = Integer.parseInt(fs[0]);
        int produitID = Integer.parseInt(fs[1]);
        date = new java.util.Date().toString();
        StringJoiner sj = new StringJoiner("\n");
        String[] base = new String[] {String.valueOf(id), date, "0", String.valueOf(coutTotal),
                String.valueOf(pointsTotal), adresse};
        sj.add(String.join(",", base));
        for(Produit p : this.produits) {
            sj.add(p.titre + "," + produitID);
            p.commander();
            p.setUniqueId(produitID);
            ++produitID;
        }
        this.adresse = adresse;
        Main.ecrireFichierEntier(Main.IDS, (id + 1) + "," + produitID);
        Main.ecrireFichierEntier(Main.COMMANDES_PATH + id + Main.CSV, sj.toString());
        return this;
    }

    /**
     * Retourne les informations sur le revendendeur
     *
     * @return Les informations sur le renvendeur
     */
    public String formatSaveRevendeur() {
        StringJoiner sj = new StringJoiner("\n");
        String[] base = new String[] {String.valueOf(id), date, String.valueOf(etat), String.valueOf(coutTotal),
                String.valueOf(pointsTotal), adresse};
        sj.add(String.join(",", base));
        for(Produit p : this.produits)
            sj.add(p.titre + "," + p.getId());
        return sj.toString();
    }

    /**
     * Enregistre le panier
     */
    public void savePanier(){
        String path = Main.ACHETEURS_PATH + Main.getConnectedUsername() + "/Panier.csv";
        StringJoiner sj = new StringJoiner("\n");
        sj.add(coutTotal + "," + pointsTotal);
        for(Produit p : produits)
            sj.add(p.titre);
        Main.ecrireFichierEntier(path, sj.toString());
    }

    /**
     *
     */
    public void save() {
        StringJoiner sj = new StringJoiner("\n");
        String[] base = new String[] {String.valueOf(id), date, String.valueOf(etat), String.valueOf(coutTotal),
                String.valueOf(pointsTotal), adresse, String.valueOf(reception)};
        sj.add(String.join(",", base));
        for(Produit p : this.produits) {
            sj.add(p.titre + "," + p.getId());
        }
        Main.ecrireFichierEntier(Main.COMMANDES_PATH + id + Main.CSV, sj.toString());
    }

    /**
     * Vide le panier
     */
    public void vider() {
        pointsTotal = 0;
        coutTotal = 0;
        produits.clear();
        savePanier();
    }

    /**
     * Obtient le produit choisi
     * @param menuOption L'option du menu selectionnée
     * @return Le produit choisi
     */
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

    /**
     * Recense tout les produits sélectionné par le client
     * @return tout les produits sélectionné par le client
     */
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

    /**
     * Verifie si le panier est vide
     *
     * @return True si le panier est vide, False sinon
     */
    public boolean estVide() {
        return produits.isEmpty();
    }

    /**
     * Retoune les informations du menu
     * @return L'id, la date et le coût total
     */
    public String getMenuDisplay() {
        return "ID: " + id + " ; Date: " + date + " ; Total: " + coutTotal + "$";
    }

    /**
     * Recense tout les produits sélectionné par le client
     *
     * @return tout les produits sélectionné par le client sous forme de arraylist
     */
    public ArrayList<Produit> getProduitsP() { return new ArrayList<>(this.produits);}

    /**
     * Recense le temps de réception
     *
     * @return le temps de réception
     */
    public long getTempsReception() {
        return this.reception;
    }
    public String getDate() {
        return date;
    }
}
