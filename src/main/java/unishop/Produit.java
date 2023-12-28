package unishop;

import unishop.Categories.Categorie;
import static unishop.Main.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

public class Produit {

    private final String nomReven;
    private final String titre;
    private final String description;
    private final float prix;
    private int points;
    private int quantite;
    final ArrayList<String> images;
    final ArrayList<String> videos;
    final Categorie categorie;
    final ArrayList<String> likes;
    final ArrayList<Evaluation> evaluations;
    private float noteMoyenne;
    private int id;

    public Produit(String nomReven, String titre, String description, float prix, int quantite, int points,
                   String[] images, String[] videos, Categorie categorie, ArrayList<String> likes,
                   ArrayList<Evaluation> evaluations) {
        this.nomReven = nomReven;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.quantite = quantite;
        this.points = points;
        this.images = new ArrayList<>(Arrays.asList(images));
        this.videos = new ArrayList<>(Arrays.asList(videos));
        this.categorie = categorie;
        this.likes = new ArrayList<>(likes);
        this.evaluations = new ArrayList<>(evaluations);
        this.noteMoyenne = getNoteMoyenne();
    }

    public String getTitre() { return titre; }
    public String getNomReven() { return nomReven; }
    public float getPrix() { return prix; }
    public int getPoints() {return this.points;}
    public ArrayList<String> getLikes() { return new ArrayList<>(likes); }
    public int getId() { return id; }
    public int getQuantite() { return quantite; }
    public ArrayList<Evaluation> getEvaluations() { return new ArrayList<>(evaluations); }
    public void setUniqueId(int id) { this.id = id; }

    public boolean estEnPromotion() {return this.points > Math.floor(prix);}
    public float getNoteMoyenne() {
        float n = 0;
        for (Evaluation e : evaluations) {
            n = arrondirPrix(n + e.note);
        }
        return arrondirPrix(n / evaluations.size());
    }
    public void save() {
        StringJoiner sj = new StringJoiner(",");
        sj.add(nomReven);
        sj.add(titre);
        sj.add(description);
        sj.add(String.valueOf(prix));
        sj.add(String.valueOf(quantite));
        sj.add(String.valueOf(points));
        sj.add(String.valueOf(noteMoyenne));
        String fst = sj.toString();

        sj = new StringJoiner("\n");
        sj.add(fst);
        sj.add(String.join(",", images));
        sj.add(String.join(",", videos));
        sj.add(categorie.getFormatSauvegarde());
        sj.add(String.join(",", likes));
        if(evaluations.isEmpty())
            sj.add("");
        for(Evaluation e : evaluations)
            sj.add(e.getSaveFormat());
        ecrireFichierEntier(PRODUITS_PATH + titre + CSV, sj.toString());
    }
    public String getFormatDisplay() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add(titre);
        sj.add("Images: " + String.join(",", images));
        sj.add("Vidéos: " + String.join(",", videos));
        sj.add(description);
        sj.add("Revendeur: " + nomReven);
        sj.add("Prix: " + prix + "$");
        sj.add(points + " points par unité");
        sj.add("Quantité en inventaire: " + quantite);
        sj.add(categorie.getFormatDisplay());
        sj.add(likes.size() + " likes");
        sj.add(evaluations.size() + " évaluations");
        if (!evaluations.isEmpty())
            sj.add("Note moyenne: " + noteMoyenne);
        return sj.toString();
    }
    public String getQuickDisplay() {
        StringJoiner sj = new StringJoiner("; ");
        sj.add(titre);
        sj.add(categorie.getCat());
        sj.add(prix + "$");
        sj.add(points + " points");
        sj.add(quantite + " disponibles");
        if (id != 0)
            sj.add("ID: " + id);
        return sj.toString();
    }

    public boolean liker(String nomAcheteur) {
        if (likes.contains(nomAcheteur))
            return false;
        this.likes.add(nomAcheteur);
        save();
        return true;
    }
    public void addEvaluation(Evaluation e) {
        this.evaluations.add(e);
        this.noteMoyenne = getNoteMoyenne();
        save();
    }
    public String getEvaluationsDisplay() {
        if (evaluations.isEmpty())
            return "Ce produit n'a aucune évaluation pour le moment.";
        else {
            StringJoiner sj = new StringJoiner("\n\n");
            for (Evaluation e : evaluations)
                sj.add(e.getDisplayFormat());
            return sj.toString();
        }
    }
    public void commander() {
        --this.quantite;
        save();
    }
    public void enleverPromotion() {
        this.points = (int) Math.floor(prix);
        save();
    }
    public void changerPromotion(int pts) {
        this.points = pts;
        save();
    }

    public void restocker(int quantite) {
        this.quantite += quantite;
        save();
    }
    public void ajouterVideos(String[] vids) {
        this.videos.addAll(Arrays.asList(vids));
        save();
    }
    public void ajouterImages(String[] imgs) {
        this.videos.addAll(Arrays.asList(imgs));
        save();
    }
}