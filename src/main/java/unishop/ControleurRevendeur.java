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
                case 2 -> gererCommandes();
                case 3 -> gererBillets();
                case 4 -> modifierProduit();
                case 5 -> changerInformations();
                case 6 -> System.out.println(revendeur.afficherMetriques());
                case 7 -> System.out.println("\n" + revendeur.voirNotifications());
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
            for (String a : revendeur.getFollowers()){
                Acheteur acheteur = initialiserAcheteur(a);
                Notification notifRev = new Notification(1, a, revendeur.getUsername(), p.titre, 0);
                acheteur.addNotifications(notifRev);
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
    static void gererCommandes() {
        ArrayList<Commande> cmds = revendeur.getCommandes();
        if (cmds.isEmpty()) {
            System.out.println("\nVous n'avez aucunes commandes!");
            return;
        }
        System.out.println("\nChoisissez une commande: ");
        String[] cs = new String[cmds.size() + 1];
        for (int i = 0; i < cmds.size(); ++i) {
            cs[i] = cmds.get(i).getMenuDisplay();
        }
        cs[cmds.size()] = "Retourner au menu";
        choix = selectionChoix(cs);
        if (choix == cs.length)
            return;
        Commande cmd = cmds.get(choix - 1);
        System.out.println("\nCommande #" + cmd.getId());
        System.out.println(cmd.afficher());
        System.out.println("Votre commande est " + cmd.getEtat() + ".");
        System.out.println("\nChoisissez une action: ");
        choix = selectionChoix(new String[]{"Changer l'état de la commande",  "Retourner au menu"});
        if (choix == 1) {
            switch (cmd.confirmerLivraison()) {
                case 0 -> System.out.println("\nL'état de votre commande a été changé avec succès!");
                case 1 -> System.out.println("\nVotre commande est déjà en livraison!");
                case 2 -> System.out.println("\nLa commande a déjà été livrée à l'acheteur!");
            }
        }
    }
    static void gererBillets() {
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
                        //TODO NOTIF
                        a.addNotifications(new Notification(5, a.getUsername(), revendeur.getUsername(),
                                b.produitInitial, 0));
                        System.out.println("\nVous avez ajouté une solution au billet!");

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
                    System.out.println("\nVous avez confirmer la livraison du produit problématique à l'entrepôt!");
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
            choix = selectionChoix(new String[] {"Restocker un produit", "Gérer une promotion", "Ajouter des médias",
                    "Retourner au menu"});
            if (choix == 4)
                return;
            Produit p = revendeur.getProduitAvecChoix();
            if (p == null)
                return;
            switch (choix) {
                case 1 -> {
                    System.out.print("Entrez la quantité que vous voulez ajouter à l'inventaire: ");
                    try {
                        p.restocker(demanderIntPositif("une quantité"));
                        System.out.println("\nVous avez maintenant " + p.getQuantite() + " " + p.titre +
                                " en inventaire!");
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                case 2 -> {
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
                                    System.out.print("Vous avez entré un nombre de points invalide! " +
                                            "Veuillez réessayer: ");
                                    pts = demanderIntPositif("un nombre de points");
                                }
                                p.changerPromotion((int)Math.floor(p.prix) + pts);
                                // TODO NOTIF
                                for (String a : revendeur.getFollowers()) {
                                    Acheteur acheteur = initialiserAcheteur(a);
                                    Notification notifRev = new Notification(2, a, revendeur.getUsername(),
                                            p.titre, 0);
                                    acheteur.addNotifications(notifRev);
                                }
                                System.out.println("\n" + p.titre + " a maintenant une promotion de " + pts +
                                        " points!");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                case 3 -> {
                    try {
                        System.out.println("Voulez-vous ajouter des images ou des vidéos?");
                        if (1 == selectionChoix(new String[] {"Images", "Vidéos"})) {
                            System.out.print("Entrer les liens vers les images séparés par des virgules: ");
                            String[] imgs = br.readLine().split(",");
                            p.ajouterImages(imgs);
                        }
                        else {
                            System.out.print("Entrer les liens vers les vidéos séparés par des virgules: ");
                            String[] vids = br.readLine().split(",");
                            p.ajouterVideos(vids);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    static void changerInformations() {
        try {
            choix = selectionChoix(new String[]{"Modifier Téléphone", "Modifier Adresse", "Modifier Mot de passe",
                                                "Modifier Adresse courriel"});
            switch (choix) {
                case 1 -> {
                    System.out.println("Rentrer votre nouveau numéro de téléphone: ");
                    long telephone = demanderLong("un numéro");
                    revendeur.setPhone(telephone);
                }
                case 2 -> {
                    System.out.println("Rentrer votre nouvelle adresse: ");
                    String adresse = br.readLine();
                    revendeur.setPassword(adresse);
                }
                case 3 -> {
                    System.out.println("Rentrer votre nouveau mot de passe: ");
                    String password = br.readLine();
                    revendeur.setPassword(password);
                }
                case 4 -> {
                    System.out.println("Rentrer votre nouvelle adresse de courriel: ");
                    String email = br.readLine();
                    revendeur.setEmail(email);
                }
        }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
