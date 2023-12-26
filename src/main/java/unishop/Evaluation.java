package unishop;

import java.util.ArrayList;
import java.util.StringJoiner;

public class Evaluation {
    final String nomAcheteur;
    final int note;
    private final String commentaire;
    private ArrayList<String> likes;
    private boolean estInapropprie;

    public Evaluation(String nomAcheteur, int note, String commentaire, boolean estInapropprie,
                      ArrayList<String> likes) {
        this.nomAcheteur = nomAcheteur;
        this.note = note;
        this.commentaire = commentaire;
        this.estInapropprie = estInapropprie;
        this.likes = new ArrayList<>(likes);
    }
    public String getSaveFormatProduit() {
        StringJoiner sj = new StringJoiner(",");
        sj.add(nomAcheteur);
        sj.add(String.valueOf(note));
        sj.add(commentaire);
        sj.add(String.valueOf(estInapropprie));
        for (String u : likes)
            sj.add(u);
        return sj.toString();
    }
    public int getLikes() {
        return likes.size();
    }

    public String getDisplayFormat() {
        return "Acheteur: " + nomAcheteur + "\nNote sur 5: " + note + "\n" + commentaire +
                "\nNombre de likes: " + likes.size() + (estInapropprie ? "\nSignalée comme inappropriée" : "");
    }

    // TEST
    public boolean ajouterLike(String user) {
        if (likes.contains(user))
            return false;
        likes.add(user);
        return true;
    }

    // TEST
    public boolean isEqual(Evaluation e) {
        return e.note == this.note && e.commentaire.equals(this.commentaire) && e.likes == this.likes;
    }

    // TEST
    public boolean signaler() {
        if (this.estInapropprie)
            return false;
        else {
            return this.estInapropprie = true;
        }
    }
}
