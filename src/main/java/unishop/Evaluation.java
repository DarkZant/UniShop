package unishop;

public class Evaluation {
    final String nomAcheteur;
    final int note;
    private final String commentaire;
    private int likes;
    private boolean estInapropprie;

    public Evaluation(String nomAcheteur, int note, String commentaire, int likes, boolean estInapropprie) {
        this.nomAcheteur = nomAcheteur;
        this.note = note;
        this.commentaire = commentaire;
        this.likes = likes;
        this.estInapropprie = estInapropprie;
    }
    public String getSaveFormatProduit() {
        return nomAcheteur + "," + note + "," + commentaire + "," + likes + "," + estInapropprie;
    }
    public String getSaveFormatAcheteur() {
        return note + "," + commentaire + "," + likes + "," + estInapropprie;
    }

    public int getLikes() {
        return likes;
    }

    public String getDisplayFormat() {
        return "Acheteur: " + nomAcheteur + "\nNote sur 5: " + note + "\n" + commentaire +
                "\nNombre de likes: " + likes + (estInapropprie ? "\nSignalée comme inappropriée" : "");
    }

    // TEST
    public void ajouterLike() {
        ++likes;
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
