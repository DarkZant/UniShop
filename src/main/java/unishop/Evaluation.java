package unishop;

import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Cette classe représente une évaluation associée à un produit avec le nom de l'acheteur, la note et le commentaire
 */
public class Evaluation {
    final String nomAcheteur;
    final int note;
    private final String commentaire;
    private ArrayList<String> likes;
    private boolean estInapropprie;

    /**
     * Crée une évaluation associée à un produit avec le nom de l'acheteur, la note et le commentaire
     * @param nomAcheteur Nom de l'acheteur qui évalue le produit
     * @param note Note que l'acheteur souhaite donner le produit
     * @param commentaire Commentaire que l'acheteur souhaite donner le produit
     * @param estInapropprie Est-ce que le commentaire est inapproprié?
     * @param likes Liste des likes
     */
    public Evaluation(String nomAcheteur, int note, String commentaire, boolean estInapropprie,
                      ArrayList<String> likes) {
        this.nomAcheteur = nomAcheteur;
        this.note = note;
        this.commentaire = commentaire;
        this.estInapropprie = estInapropprie;
        this.likes = new ArrayList<>(likes);
    }

    /**
     * Obtenir le format long des informations qui caractérise l'évaluation
     * @return le format long des informations qui caractérise l'évaluation
     */
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

    /**
     * Obtenir le nombre de likes
     * @return Le nombre de likes
     */
    public int getLikes() {
        return likes.size();
    }

    /**
     * Obtenir le format display des informations qui caractérise l'évaluation
     * @return le format display des informations qui caractérise l'évaluation
     */
    public String getDisplayFormat() {
        return "Acheteur: " + nomAcheteur + "\nNote sur 5: " + note + "\n" + commentaire +
                "\nNombre de likes: " + likes.size() + (estInapropprie ? "\nSignalée comme inappropriée" : "");
    }

    /**
     * Permet d'ajouter les likes
     * @param user Le nom de l'acheteur qui souhaite liker
     * @return True si le like a été ajouté et false sinon
     */
    // TEST
    public boolean ajouterLike(String user) {
        if (likes.contains(user))
            return false;
        likes.add(user);
        return true;
    }

    /**
     * Vérifies si deux evaluations sont similaires
     * @param e L'évaluation que l'on souhaite comparer
     * @return True si les deux evaluations sont similaires, false sinon
     */
    // TEST
    public boolean isEqual(Evaluation e) {
        return e.note == this.note && e.commentaire.equals(this.commentaire) && e.likes == this.likes;
    }

    /**
     * Permets de signaler une évaluation
     * @return True si l'évaluation est signalée et False si l'évaluation était déja signalée.
     */
    // TEST
    public boolean signaler() {
        if (this.estInapropprie)
            return false;
        else {
            return this.estInapropprie = true;
        }
    }
}
