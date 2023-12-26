package unishop.Users;

import unishop.*;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringJoiner;

public class Acheteur extends User implements Comparable<Acheteur>{

    private String nom;
    private String prenom;
    private int points;
    private final int likes;
    final ArrayList<String> acheteursSuivis;
    final ArrayList<String> suiveurs;
    final ArrayList<String> revendeursLikes;
    public final Commande panier;

    public Acheteur(String u, String p, String em, long phone, String address, String nom,
                    String prenom, int points, int likes, ArrayList<String> acheteursSuivis,
                    ArrayList<String> revendeursLikes, ArrayList<Billet> b, Commande panier, ArrayList<Commande> cmds,
                    Stack<Notification> ns){
        super(u, p, em, phone, address, b, cmds, ns);
        this.nom = nom;
        this.prenom = prenom;
        this.points = points;
        this.likes = likes;
        this.acheteursSuivis = new ArrayList<>(acheteursSuivis);
        this.revendeursLikes = new ArrayList<>(revendeursLikes);
        this.suiveurs = new ArrayList<>(); //Suiveurs en input
        this.panier = panier;
    }

    public void setNom (String nom) { this.nom = nom; }

    public void setPrenom (String prenom) { this.prenom = prenom; }

    @Override
    public boolean isAcheteur() {
        return true;
    }
    public void ajouterCommande(Commande c) {
        commandes.add(c);
        points += c.getPointsTotal();
        save();
    }

    @Override
    public void save() {
        StringJoiner sj = new StringJoiner("\n");
        String[] infos = new String[] {this.password, this.email, String.valueOf(this.phone), this.address, nom,
                prenom, String.valueOf(points), String.valueOf(likes)};
        sj.add(String.join(",", infos));
        sj.add(String.join(",", acheteursSuivis));
        sj.add(String.join(",", revendeursLikes));
        sj.add(formatSaveCommande());
        sj.add(formatSaveBillet());
        sj.add(formatSaveNotifications());
        Main.ecrireFichierEntier(Main.ACHETEURS_PATH + this.username + "/Infos.csv", sj.toString());
    }

    // TEST
     public String suivre(String acheteur) {
        if (this.username.equals(acheteur))
            return "Vous ne pouvez pas vous suivre vous-même!";
        else if (acheteursSuivis.contains(acheteur))
            return "Vous suivez déjà cet acheteur!";
        else {
            acheteursSuivis.add(acheteur);
            save();
            return "Vous suivez maintenant " + acheteur + "!";
        }
    }
    public boolean aAcheteProduit(String nomProduit) {
        for(Commande c : commandes) {
            for(String pt : c.getProduits()) {
                if (nomProduit.equals(pt))
                    return true;
            }
        }
        return false;
    }
    public ArrayList<String> getFollowers() {
        return new ArrayList<>(suiveurs);
    }
    public ArrayList<String> getSuivis() {
        return new ArrayList<>(acheteursSuivis);
    }
    public void ajouterPoints(int pts) {
        this.points += pts;
    }
    public int viderPoints() {
        int pts = this.points;
        this.points = 0;
        return pts;
    }
    public boolean billetExiste(int id) {
        for (Billet b : this.billets){
            if (b.id == id)
                return true;
        }
        return false;
    }
    // TEST
    @Override
    public String afficherMetriques() {
        return "\nNombre de points: " + points + "\nNombre de produits commandés" +
                "\nNombre total de commandes effectuées: " + commandes.size() +
                "\nNombre de followers: " + suiveurs.size();
    }
    @Override
    public int compareTo(Acheteur a){
        if (this.points > a.points)
            return 1;
        else if (this.points < a.points)
            return -1;
        return 0;
    }

    public int getPoints() {return points;}
}
