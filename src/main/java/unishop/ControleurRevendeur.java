package unishop;

import unishop.Categories.*;
import unishop.Users.Acheteur;
import unishop.Users.Revendeur;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static unishop.Main.*;

public class ControleurRevendeur {
    private static short choix;
    private static Revendeur revendeur;
    static void menuRevendeur(Revendeur r) {
        revendeur = r;
        while (true) {
            System.out.println("\nVoici le menu Revendeur:");
            choix = selectionChoix(new String[]{"Offrir un nouveau produit", "Accéder aux commandes", "Voir les billets",
                    "Modifier un produit", "Changer les informations du profil", "Afficher les métriques",
                    "Voir les notifications", "Se déconnecter"});
            switch (choix) {
                case 1 -> offrirProduit();
                case 2 -> gererCommandesRevendeur();
                case 3 -> gererBilletsRevendeur();
                case 4 -> modifierProduit();
                case 5 -> changerInformations();
                case 6 -> System.out.println(revendeur.afficherMetriques());
                case 7 -> System.out.println(afficherNotifications());
                case 8 -> {return;}
            }
        }
    }

    static void offrirProduit() {
        System.out.println("\nVeuillez remplir les informations concernant votre produit.");
        System.out.println("Commencez par choisir une catégorie:");
        choix = selectionChoix(Categorie.categories);
        try {
            System.out.print("Quel est le titre de votre produit? (Ne rien mettre pour retourner au menu) ");
            String titre = br.readLine();
            if (titre.isEmpty())
                return;
            List<String> titres = fichiersDansDossier(PRODUITS_PATH);
            while (titres.contains(titre + CSV)) {
                System.out.print("Ce nom de produit existe déjà. Veuillez en entrer un autre: ");
                titre = br.readLine();
            }
            System.out.print("Veuillez entrer une description: ");
            String description = br.readLine();

            System.out.print("Veuillez entrer des liens pour des images (Séparés pas des \",\"): ");
            String[] images = br.readLine().split(",");
            System.out.print("Veuillez entrer des liens pour des vidéos (Séparés pas des \",\"): ");
            String[] videos = br.readLine().split(",");
            Categorie c = null;
            switch(choix) {
                case 1 -> c = offrirLivre();
                case 2 -> c = offrirRessource();
                case 3 -> c = offrirPapeterie();
                case 4 -> c = offrirInfo();
                case 5 -> c = offrirBureau();
            }
            System.out.print("Veuillez entrer un prix: ");
            float prix = demanderFloat("un prix");
            System.out.println("Voulez vous offrir une promotion en points?");
            int points = (int) Math.floor(prix);
            int pointsMax = (int) Math.floor(prix * 19);
            if(choixOuiNon()) {
                System.out.print("Entrez un nombre de points (Plus petit ou égal à " + pointsMax + "): ");
                int pts = demanderIntPositif("un nombre de points");
                while (pts > pointsMax) {
                    System.out.print("Vous avez entré un nombre de points trop grand! Veuillez réessayer: ");
                    pts = demanderIntPositif("un nombre de points");
                }
                points += pts;
            }
            System.out.print("Veuillez entrer une quantité initiale à mettre dans l'inventaire: ");
            int quantite = demanderIntPositif("une quantité");
            Produit p = new Produit(revendeur.getUsername(), titre, description, prix, quantite, points, images, videos,
                    c, new ArrayList<>(), new ArrayList<>());
            p.save();
            revendeur.ajouterProduit(p);
            if (c != null)
                revendeur.ajouterCatVendu(c.getCat());
            System.out.println("Votre nouveau produit " + titre + " a été ajouté avec succès!");

            // TODO NOTIF
            List<String> acheteurs = fichiersDansDossier(USERS_PATH + ACHETEURS);
            for ( String a : acheteurs){
                Acheteur acheteur = initialiserAcheteur(a);
                for (String rList : acheteur.getRevendeursLikes() ){
                    if (rList == revendeur.getUsername()){
                        Notification notifRev = new Notification(1, a, revendeur.getUsername(), p.titre, 0);
                        acheteur.addNotifications(notifRev);
                }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Quelque chose s'est mal passé. Veuillez réessayer.");
            offrirProduit();
        }
    }
    static Categorie offrirLivre() throws IOException {
        System.out.println("Veuillez choisir le genre de votre livre:");
        String genre = CLivres.genres[selectionChoix(CLivres.genres) - 1];
        System.out.print("Entrez le ISBN: ");
        long isbn = demanderLong("un ISBN");
        System.out.print("Entrez l'auteur: ");
        String auteur = br.readLine();
        System.out.print("Entrez la maison d'édition: ");
        String maison = br.readLine();
        System.out.print("Entrez la date de parution (JJ/MM/AAAA): ");
        String date = br.readLine();
        System.out.print("Entrez le numéro d'édition: ");
        int numEdition = demanderIntPositif("un numéro d'édition");
        System.out.print("Entrez le numéro de volume: ");
        int numVolume = demanderIntPositif("un numéro de volume");
        return new CLivres(auteur, maison, genre, isbn, date, numEdition, numVolume);

    }
    static Categorie offrirRessource() throws IOException{
        System.out.println("Est-ce un produit en ligne ou imprimé?");
        String type = CRessources.types[selectionChoix(CRessources.types) - 1];
        System.out.print("Entrez le ISBN: ");
        long isbn = demanderLong("un ISBN");
        System.out.print("Entrez l'auteur: ");
        String auteur = br.readLine();
        System.out.print("Entrez l'organisation: ");
        String organisation = br.readLine();
        System.out.print("Entrez la date de parution (JJ/MM/AAAA): ");
        String date = br.readLine();
        System.out.print("Entrez le numéro d'édition: ");
        int numEdition = demanderIntPositif("un numéro d'édition");
        return new CRessources(auteur, organisation, type, isbn, date, numEdition);
    }
    static Categorie offrirPapeterie() throws IOException {
        System.out.println("Veuillez choisir une sous-catégorie: ");
        String sousCat = CPapeterie.sousCats[selectionChoix(CPapeterie.sousCats) - 1];
        System.out.print("Entrez la marque: ");
        String marque = br.readLine();
        System.out.print("Entrez le modèle: ");
        String modele = br.readLine();
        return new CPapeterie(marque, modele, sousCat);
    }
    static Categorie offrirInfo() throws IOException {
        System.out.println("Veuillez choisir une sous-catégorie: ");
        String sousCat = CInformatique.sousCats[selectionChoix(CInformatique.sousCats) - 1];
        System.out.print("Entrez la marque: ");
        String marque = br.readLine();
        System.out.print("Entrez le modèle: ");
        String modele = br.readLine();
        System.out.print("Entrez la date de parution (JJ/MM/AAAA): ");
        String date = br.readLine();
        return new CInformatique(marque, modele, sousCat, date);
    }
    static Categorie offrirBureau() throws IOException {
        System.out.println("Veuillez choisir une sous-catégorie: ");
        String sousCat = CBureau.sousCats[selectionChoix(CBureau.sousCats) - 1];
        System.out.print("Entrez la marque: ");
        String marque = br.readLine();
        System.out.print("Entrez le modèle: ");
        String modele = br.readLine();
        return new CBureau(marque, modele, sousCat);
    }
    static void gererCommandesRevendeur() {
        System.out.println("\nTODO");
    }
    static void gererBilletsRevendeur() {
        ArrayList<Billet> ba = revendeur.getBillets();
        if (ba.isEmpty()) {
            System.out.println("\nVous n'avez aucun billet!");
            return;
        }
        while (true) {
            System.out.println("\nChoissisez un billet: ");
            String[] bs = new String[ba.size() + 1];
            for(int i = 0; i < ba.size(); ++i) {
                bs[i] = ba.get(i).afficherMenu();
            }
            bs[bs.length - 1] = "Retour au menu";
            choix = selectionChoix(bs);
            if (choix == bs.length)
                return;
            Billet b = ba.get(choix - 1);
            System.out.println("\n" + b.afficher());
            if (!b.comfirmerLivraisonInitial() && !b.pasDeSolution())
                continue;
            System.out.println("\nQue voulez-vous faire?");
            choix = selectionChoix(new String[] {"Donner une solution", "Confirmer l'arrivée du produit problématique",
                    "Retourner au menu"});
            if (choix == 1) {
                if (b.pasDeSolution()) {
                    System.out.print("Entrez votre solution: ");
                    try {
                        String solution = br.readLine();
                        b.setProbRev(solution);
                        Acheteur a = initialiserAcheteur(b.nomAche);
                        a.trouverBillet(b.id).setProbRev(solution);
                        a.save();
                        revendeur.save();
                        System.out.println("\nVous avez ajouté une solution au billet!");
                        //TODO NOTIF

                        Notification notifRev = new Notification(5, a.getUsername(), revendeur.getUsername(), b.produitInitial, 0);
                        a.addNotifications(notifRev);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    System.out.println("\nVous aviez déjà ajouté une solution à ce billet!");
                }
            }
            else if (choix == 2) {
                if (b.comfirmerLivraisonInitial()) {
                    try {
                        Acheteur a = initialiserAcheteur(b.nomAche);
                        a.trouverBillet(b.id).comfirmerLivraisonInitial();
                        a.save();
                        revendeur.save();
                        System.out.println("\nVous avez confirmer la livraison du produit problématique à l'entrepôt!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    System.out.println("\nVous aviez déjà confirmé la réception du produit problématique pour ce " +
                            "billet!");
            }
            else
                break;
        }

    }
    static void modifierProduit () {
        while (true) {
            System.out.println("\nChoisir une option: ");
            choix = selectionChoix(new String[] {"Restocker un produit", "Gérer une promotion", "Retourner au menu"});
            if (choix == 3)
                return;
            Produit p = revendeur.getProduitAvecChoix();
            if (choix == 1) {
                System.out.print("Entrez la quantité que vous voulez ajouter à l'inventaire: ");
                try {
                    p.restocker(demanderIntPositif("une quantité"));
                    System.out.println("\nVous avez maintenant " + p.getQuantite() + " " + p.titre + " en inventaire!");
                } catch(IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            while (true) {
                System.out.println("\nQue voulez-vous faire avec la promotion?");
                choix = selectionChoix(new String[] {"Enlever la promotion", "Modifier la promotion", "" +
                        "Retourner au menu"});
                if (choix == 3)
                    break;
                if (choix == 1) {
                    if (p.estEnPromotion()) {
                        p.enleverPromotion();
                        System.out.println("\nVous avez enlevé la promotion de " + p.titre + "!");
                    }
                    else
                        System.out.println("\nCe produit n'est pas en promotion!");

                }
                else {
                    int pointsMax = (int) Math.floor(p.prix * 19);
                    System.out.print("Entrez un nombre de points (Plus petit ou égal à " + pointsMax + "): ");
                    try {
                        int pts = demanderIntPositif("un nombre de points");
                        while (pts > pointsMax || pts == 0) {
                            System.out.print("Vous avez entré un nombre de points invalide! Veuillez réessayer: ");
                            pts = demanderIntPositif("un nombre de points");
                        }
                        p.changerPromotion((int)Math.floor(p.prix) + pts);
                        System.out.println("\n" + p.titre + " a maintenant une promotion de " + pts + " points!");

                        // TODO NOTIF
                        List<String> acheteurs = fichiersDansDossier(USERS_PATH + ACHETEURS);
                        for ( String a : acheteurs){
                            Acheteur acheteur = initialiserAcheteur(a);
                            for (String rList : acheteur.getRevendeursLikes() ){
                                if (rList == revendeur.getUsername()){
                                    Notification notifRev = new Notification(2, a, revendeur.getUsername(), p.titre, 0);
                                    acheteur.addNotifications(notifRev);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    static void changerInformations() {
        System.out.println("\nTODO");
    }
    static String afficherNotifications() {
        return "\nTODO";
    }
}
