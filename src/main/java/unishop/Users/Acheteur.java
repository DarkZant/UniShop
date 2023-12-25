package unishop.Users;

import unishop.*;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringJoiner;

public class Acheteur extends User implements Comparable<Acheteur>{

    final String nom;
    final String prenom;
    private int points;
    private final int likes;
    final ArrayList<String> acheteursSuivis;
    final ArrayList<String> suiveurs;
    final ArrayList<String> revendeursLikes;
    final ArrayList<Evaluation> evaluations;
    public final Commande panier;

    public Acheteur(String u, String p, String em, long phone, String address, String nom,
                    String prenom, int points, int likes, ArrayList<String> acheteursSuivis,
                    ArrayList<String> revendeursLikes, ArrayList<Billet> b, Commande panier, ArrayList<Commande> cmds,
                    ArrayList<Evaluation> es, Stack<Notification> ns){
        super(u, p, em, phone, address, b, cmds, ns);
        this.nom = nom;
        this.prenom = prenom;
        this.points = points;
        this.likes = likes;
        this.acheteursSuivis = new ArrayList<>(acheteursSuivis);
        this.revendeursLikes = new ArrayList<>(revendeursLikes);
        this.suiveurs = new ArrayList<>(); //Suiveurs en input
        this.evaluations = new ArrayList<>(es); //Evaluations en input
        this.panier = panier;
    }

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
        if (billets.isEmpty())
            sj.add("");
        for (Billet b : billets)
            sj.add(b.saveFormat());
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
    public ArrayList<String> getRevendeursLikes() {
        return new ArrayList<>(revendeursLikes);
    }
    public ArrayList<String> getFollowers() {
        return new ArrayList<>(suiveurs);
    }
    public ArrayList<String> getSuivis() {
        return new ArrayList<>(acheteursSuivis);
    }
    public ArrayList<Evaluation> getEvals() { return new ArrayList<>(evaluations);}
    public void ajouterPoints(int pts) {
        this.points += pts;
    }
    public int viderPoints() {
        int pts = this.points;
        this.points = 0;
        return pts;
    }
    public void ajouterEvaluation(Evaluation e) {
        this.evaluations.add(e);
        saveEvals();
    }
    public void saveEvals() {
        String[] evals = new String[evaluations.size()];
        for (int i = 0; i < evals.length; ++i)
            evals[i] = evaluations.get(i).getSaveFormatAcheteur();
        Main.ecrireFichierEntier(Main.ACHETEURS_PATH + username + "/Evaluations.csv",
                String.join("\n", evals));
        save();
    }
    public void annulerCommande(Commande c) {
        this.commandes.remove(c);
    }
    public boolean billetExiste(int id) {
        for (Billet b : this.billets){
            if (b.id == id)
                return true;
        }
        return false;
    }
    @Override
    public void saveNotifications() {
        StringJoiner sj = new StringJoiner("\n");
        for (Notification n : notifications)
            sj.add(n.saveFormat());
        Main.ecrireFichierEntier(Main.ACHETEURS_PATH + username + "/Notifications.csv",
                sj.toString());
    }

    // TEST
    @Override
    public String afficherMetriques() {
        return "\nNombre de points: " + points + "\nNombre total de commandes effectuées: " + commandes.size() +
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
}
