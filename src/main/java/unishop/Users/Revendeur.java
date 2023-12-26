package unishop.Users;

import unishop.*;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringJoiner;

public class Revendeur extends User{

    private float revenu;
    private int nbProduitsVendus;
    ArrayList<String> followers;
    ArrayList<Produit> produits;
    ArrayList<String> categorieVendu;

    public Revendeur(String u, String p, String email, long phone, String address, float revenu,
                     int nbProduitsVendus, ArrayList<String> followers, ArrayList<Billet> b, ArrayList<Produit> ps,
                     ArrayList<Commande> cmds, ArrayList<String> cV, Stack<Notification> ns) {
        super(u, p, email, phone, address, b, cmds, ns);
        this.revenu = revenu;
        this.nbProduitsVendus = nbProduitsVendus;
        this.followers = new ArrayList<>(followers);
        this.produits = new ArrayList<>(ps);
        this.categorieVendu = new ArrayList<>(cV);
    }

    @Override
    public boolean isAcheteur() {
        return false;
    }
    public void ajouterProduit(Produit p) {
        this.produits.add(p);
    }
    public void ajouterCatVendu(String c) {this.categorieVendu.add(c);}
    public ArrayList<String> getFollowers() {
        return new ArrayList<>(followers);
    }
    @Override
    public void save() {
        StringJoiner sj = new StringJoiner("\n");
        String[] infos = new String[]{this.password, this.email, String.valueOf(this.phone), this.address,
               String.valueOf(revenu), String.valueOf(nbProduitsVendus)};
        sj.add(String.join(",", infos));
        sj.add(String.join(",", followers));
        sj.add(String.join(",", categorieVendu));
        sj.add(formatSaveCommande());
        sj.add(formatSaveBillet());
        sj.add(formatSaveNotifications());
        Main.ecrireFichierEntier(Main.REVENDEURS_PATH + this.username + "/Infos.csv", sj.toString());
    }
    public Produit getProduitAvecChoix() {
        String[] s = new String[produits.size() + 1];
        int i = 0;
        for (Produit p : produits) {
            s[i] = p.getQuickDisplay();
            ++i;
        }
        s[produits.size()] = "Retourner au menu";
        short choix = Main.selectionChoix(s);
        if (choix == s.length)
            return null;
        return produits.get(choix - 1);
    }
    @Override
    public void ajouterCommande(Commande c) {
        commandes.add(c);
        save();
    }
    @Override
    public String afficherMetriques () {
        return "\nNombre de produits offerts: " + produits.size() + "\nRevenu: " + revenu +
                "$\nNombre de produits vendus: " + nbProduitsVendus +
                "\nCatégorie vendues : " + String.join(", ", categorieVendu);
    }
    public boolean ajouterFollower(String acheteur) {
        if (followers.contains(acheteur))
            return false;
        followers.add(acheteur);
        save();
        return true;
    }
}