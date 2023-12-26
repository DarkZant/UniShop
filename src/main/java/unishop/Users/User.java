package unishop.Users;

import unishop.*;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringJoiner;

public abstract class User {

    protected final String username;
    protected String password;
    protected String email;
    protected long phone;
    protected String address;
    protected final ArrayList<Billet> billets;
    protected final ArrayList<Commande> commandes;
    protected Stack<Notification> notifications;
    protected User(String username, String password, String email, long phone, String address,
                   ArrayList<Billet> billets, ArrayList<Commande> commandes, Stack<Notification> ns) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.billets = new ArrayList<>(billets);
        this.commandes = new ArrayList<>(commandes);
        this.notifications = ns;

    }
    public abstract void save();
    public abstract boolean isAcheteur();
    public abstract String afficherMetriques();
    public void addBillet(Billet b) {
        this.billets.add(b);
        save();
    }
    public String voirNotifications() {
        if (notifications.isEmpty())
            return "Vous n'avez aucunes notifications!";
        StringJoiner sj = new StringJoiner("\n");
        for (int i = 0; i < notifications.size(); ++i)
            sj.add(notifications.pop().afficher());
        save();
        return sj.toString();
    }
    public ArrayList<Billet> getBillets() {
        return new ArrayList<>(billets);
    }
    public Billet trouverBillet(int id) {
        for (Billet b : billets) {
            if (b.id == id)
                return b;
        }
        return null;
    }
    public Commande trouverCommande(int id) {
        for (Commande c : commandes)
            if (c.getId() == id)
                return c;
        return null;
    }
    public ArrayList<Commande> getCommandes() {
        return new ArrayList<>(commandes);
    }
    public void annulerCommande(Commande c) {
        this.commandes.remove(c);
        save();
    }
    public abstract void ajouterCommande(Commande c);
    public String formatSaveCommande() {
        if (commandes.isEmpty())
            return "";
        StringJoiner sj = new StringJoiner(",");
        for(Commande c : commandes)
            sj.add(String.valueOf(c.getId()));
        return sj.toString();
    }
    public String formatSaveBillet() {
        if (billets.isEmpty())
            return "";
        StringJoiner sj = new StringJoiner(",");
        for(Billet b : billets)
            sj.add(String.valueOf(b.id));
        return sj.toString();
    }
    public String formatSaveNotifications() {
        if (notifications.isEmpty())
            return "";
        StringJoiner sj = new StringJoiner("\n");
        for(Notification n : notifications)
            sj.add(n.saveFormat());
        return sj.toString();
    }


    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPassword (String password) { this.password = password; }

    public void addNotifications(Notification n) {
        this.notifications.push(n);
        save();
    }
}
