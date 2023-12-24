package unishop;

import unishop.Categories.Categorie;
import unishop.Users.Acheteur;
import unishop.Users.Revendeur;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static unishop.Main.*;

public class ControleurAcheteur {
    private static short choix;
    private static Acheteur acheteur;
    static void menuAcheteur(Acheteur a)  {
        acheteur = a;
        while (true) {
            System.out.println("\nVoici le menu Acheteur:");
            choix = selectionChoix(new String[]{"Accéder aux commandes", "Accéder au panier d'achat", "Voir les billets",
                    "Changer les informations du profil", "Rechercher un produit",
                    "Rechercher un revendeur", "Gérer les acheteur suivis",
                    "Afficher les métriques", "Voir les notifications", "Se déconnecter"});
            switch (choix) {
                case 1 -> gererCommandesAcheteur();
                case 2 -> allerAuPanier();
                case 3 -> gererBilletsAcheteur();
                case 4 -> changerInformations();
                case 5 -> {
                    short c = rechercherProduits();
                    while (c == 1)
                        c = rechercherProduits();
                    if (c == 2)
                        allerAuPanier();
                }
                case 6 -> rechercherRevendeur();
                case 7 -> gererAcheteursSuivis();
                case 8 -> System.out.println(acheteur.afficherMetriques());
                case 9 -> System.out.println(afficherNotifications());
                case 10 -> {
                    return;
                }
            }
        }
    }
    static short rechercherProduits() {
        System.out.println("\nQuel type de recherche voulez-vous faire?");
        try {
            ArrayList<String> pSelect = new ArrayList<>();
            List<String> produits = fichiersDansDossier(PRODUITS_PATH);
            boolean estRecherche = 1 == selectionChoix(new String[] {"Recherche par mots-clés",
                    "Recherche par filtre"});
            String recherche = "";
            short choixCat = 0;
            boolean estPrixPlusGrand = false;
            float floatDemande = 0;
            int nbLikesDemande = 0;
            if (estRecherche) {
                System.out.print("Entrez votre recherche: ");
                recherche = br.readLine();
            }
            else {
                System.out.println("Choisissez votre type de filtre: ");
                choix = selectionChoix(new String[] {"Catégorie", "Prix", "Popularité", "Note moyenne",
                        "Promotion"});
                switch (choix) {
                    case 1 -> {
                        System.out.println("Choisissez une catégorie: ");
                        choixCat = selectionChoix(Categorie.categories);
                        --choixCat;
                    }
                    case 2 -> {
                        System.out.println("Filtrer par plus petit ou plus grand?");
                        estPrixPlusGrand = 1 == selectionChoix(new String[]{"Plus petit", "Plus grand"});
                        System.out.print("Entrez un prix: ");
                        floatDemande = demanderFloat("un prix");
                    }
                    case 3 -> {
                        System.out.print("Entrez un nombre minimum de likes: ");
                        nbLikesDemande = demanderIntPositif("un nombre de likes");
                    }
                    case 4 -> {
                        System.out.print("Entrez une note moyenne minimale: ");
                        floatDemande = demanderFloat("une note");
                    }
                }
            }

            for(String p : produits) {
                String[] contenu = lireFichierEnEntier(PRODUITS_PATH + p);
                String[] infos = contenu[0].split(",");
                String[] cat = contenu[3].split(",");
                String produitPreview = String.join(", ", infos[1],
                        Categorie.getCat(Integer.parseInt(cat[0])), "Revendeur: " + infos[0]);
                if (estRecherche) {
                    if ((infos[1] + infos[2] + cat[1] + cat[2] + cat[3]).contains(recherche))
                        pSelect.add(produitPreview);
                }
                else {
                    switch (choix) {
                        case 1 -> {
                            short pCat = Short.parseShort(cat[0]);
                            if (choixCat == pCat)
                                pSelect.add(produitPreview);
                        }
                        case 2 -> {
                            float prix = Float.parseFloat(infos[3]);
                            if (estPrixPlusGrand) {
                                if (prix <= floatDemande)
                                    pSelect.add(produitPreview);
                            } else {
                                if (prix >= floatDemande)
                                    pSelect.add(produitPreview);
                            }
                        }
                        case 3 -> {
                            String[] nbLikesS = contenu[4].split(",");
                            int nbLikes = nbLikesS.length;
                            if (nbLikesS[0].isEmpty())
                                nbLikes = 0;
                            if (nbLikes >= nbLikesDemande)
                                pSelect.add(produitPreview);
                        }
                        case 4 -> {
                            float noteMoyenne = Float.parseFloat(infos[6]);
                            if (noteMoyenne >= floatDemande)
                                pSelect.add(produitPreview);
                        }
                        case 5 -> {
                            if (Integer.parseInt(infos[5]) != 0)
                                pSelect.add(produitPreview);
                        }
                    }
                }
            }
            if (pSelect.isEmpty()) {
                System.out.println("Aucun résultat pour cette recherche. Veuillez réessayer.");
                return 1;
            }
            pSelect.add("Faire une nouvelle recherche");
            pSelect.add("Retourner au menu acheteur");
            while (true) {
                System.out.println("\nChoisissez un produit: ");
                choix = selectionChoix(pSelect.toArray());
                if (choix == pSelect.size() - 1)
                    return 1;
                else if (choix == pSelect.size())
                    return 0;
                Produit p = initialiserProduit(pSelect.get(choix - 1).split(",")[0]);
                System.out.println("\n" + p.getFormatDisplay());
                while (true) {
                    System.out.println("\nQue voulez-vous faire ensuite?");
                    choix = selectionChoix(new String[] {"Liker le produit", "Ajouter le produit au panier",
                            "Regarder les évaluations", "Écrire une évaluation", "Voir les likes",
                            "Retourner au résultat de la recherche" });
                    if (choix == 6)
                        break;
                    switch (choix) {
                        case 1 -> System.out.println("\n" + p.liker(acheteur.getUsername()));
                        case 2 -> {
                            if (p.getQuantite() == 0) {
                                System.out.println("\nLe produit " + p.titre + " doit être restocké.");
                                continue;
                            }
                            acheteur.panier.addProduit(p);
                            System.out.println("\nVous avez ajouté " + p.titre + " au panier!");
                            System.out.println("\nVoulez-vous aller au panier?");
                            if (1 == selectionChoix(new String[] {"Oui", "Non"}))
                                return 2;
                        }
                        case 3 -> System.out.println("\n" + p.getEvaluationsDisplay());
                        case 4 -> {
                            if (acheteur.aAcheteProduit(p.titre)) {
                                ecrireEvaluation(p);
                            }
                            else {
                                System.out.println("\nVous devez acheter ce produit avant de l'évaluer!");
                            }
                        }
                        case 5 -> {
                            ArrayList<String> ar = p.voirLikes();
                            ar.add("Retourner au produit");
                            String[] as = ar.toArray(new String[0]);
                            if (as.length == 1) {
                                System.out.println("\nCe produit n'a aucun likes.");
                            }
                            else {
                                System.out.println("\nChoisir un acheteur à suivre: ");
                                short c = selectionChoix(as);
                                if (c == as.length)
                                    continue;
                                System.out.println("\n" + acheteur.suivre(p.getLike(c - 1)));
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Quelque chose s'est mal passé. Veuillez réessayer.");
            rechercherProduits();
        }
        return 0;

    }
    static void gererCommandesAcheteur() {
        System.out.println("\nChoisissez une commande: ");
        ArrayList<Commande> cmds = acheteur.getCommandes();
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
        while (true) {
            System.out.println("\nChoisissez une action: ");
            choix = selectionChoix(new String[]{"Confirmer la livraison", "Retourner un produit", "Évaluer un produit",
                    "Retourner au menu"});
            switch (choix) {
                case 1 -> {
                    switch (cmd.confirmerLivraison()) {
                        case 0 -> {
                            System.out.println("\nL'état de votre commande a été changé avec succès!");
                            cmd.saveAfter(USERS_PATH + ACHETEURS + acheteur.getUsername() + "/Commandes/");
                            //TODO Update commande revendeur
                        }
                        case 1 -> System.out.println("\nVotre commande est toujours en production.");
                        case 2 -> System.out.println("\nVous avez déjà confirmé la livraison de cette commande!");

                    }
                }
                case 2 -> {
                    Produit p = cmd.getChoixProduit(true);
                    if (p == null)
                        continue;
                    else if (acheteur.billetExiste(p.getId())) {
                        System.out.println("\nVous avez déjà fait un billet pour ce produit!");
                        continue;
                    }
                    System.out.print("Quel est le problème avec ce produit? ");
                    try {
                        String a = br.readLine();
                        System.out.println("Voulez-vous effectuer un retour ou un échange?");
                        boolean estRetour = 1 == selectionChoix(new String[] {"Retour", "Échange"});
                        Revendeur r = initialiserRevendeur(p.nomReven);
                        String produitRempla = "";
                        if (!estRetour) {
                            System.out.println("Choisissez un produit parmi ceux offert par le revendeur: ");
                            produitRempla = r.getProduitAvecChoix().titre;
                        }
                        Billet b = new Billet(p.getId(), acheteur.getUsername(), p.titre, a, estRetour, false,
                                "", produitRempla, false);
                        acheteur.addBillet(b);
                        r.addBillet(b);
                        //TODO créer nouvelle commande pour échange
                        System.out.println("\nVotre demande " + (b.estRetour ? "de retour" : "d'échange") + " a été " +
                                "traitée avec succès!");
                        System.out.println("Votre ID pour ce billet est: " + p.getId());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case 3 -> {
                    Produit p = cmd.getChoixProduit(true);
                    if (p == null)
                        continue;
                    ecrireEvaluation(p);
                }
                case 4 -> {
                    return;
                }
            }
        }
    }
    static void allerAuPanier() {
        if (acheteur.panier.estVide()) {
            System.out.println("\nVotre panier est vide!");
            return;
        }

        System.out.println("\nVoici votre panier:");
        System.out.println(acheteur.panier.afficher());
        System.out.println("\nChoisissez une action: ");
        choix = selectionChoix(new String[] {"Enlever un produit", "Passer la commande", "Retourner au menu"});
        switch (choix) {
            case 1 -> {
                Produit p = acheteur.panier.getChoixProduit(false);
                acheteur.panier.removeProduit(p);
                System.out.println("\nVous avez enlevé " + p.titre + " du panier.");
                allerAuPanier();
            }
            case 2 -> {
                try {
                    System.out.println("Voulez-vous utiliser la même adresse que votre compte?");
                    String adresse = acheteur.getAddress();
                    if (2 == selectionChoix(new String[] {"Oui", "Non"})) {
                        System.out.print("Entrez une nouvelle adresse: ");
                        adresse = br.readLine();
                    }
                    Commande c = acheteur.panier.passerCommande(USERS_PATH + ACHETEURS +
                            acheteur.getUsername() + "/Commandes", adresse);
                    acheteur.ajouterCommande(c.copy());
                    acheteur.panier.vider();
                    System.out.println("\nVotre commande a été passée avec succès!");
                    System.out.println("Votre identifiant de commanque unique est: " + c.getId());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    static void gererBilletsAcheteur() {
        ArrayList<Billet> ba = acheteur.getBillets();
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
            if (!b.estRetour && !b.isRemplaLivre()) {
                System.out.println("\nQue voulez-vous faire ensuite?");
                choix = selectionChoix(new String[] {"Confirmer l'arrivée du produit de remplacement",
                        "Retourner aux billets", "Retourner au menu"});
                if (choix == 1) {
                    if (b.comfirmerLivraisonRempla()) {
                        try {
                            Revendeur r = initialiserRevendeur(initialiserProduit(b.produitInitial).nomReven);
                            r.trouverBillet(b.id).comfirmerLivraisonRempla();
                            r.save();
                            acheteur.save();
                            System.out.println("\nVous avez confirmé la livraison du produit de remplacement!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        System.out.println("\nVous avez déjà confirmé la réception du produit de remplacement!");
                }
                else if (choix == 3)
                    return;
            }
        }

    }
    static void ecrireEvaluation (Produit p) {
        System.out.println("\nÉvaluation du produit " + p.titre + ":");
        System.out.print("Veuillez entrer une note entière entre 1 et 5: ");
        try {
            int note;
            note = demanderIntPositif("une note");
            while (note > 5 || note == 0) {
                System.out.print("Veuillez entrer une note plus petite ou égale à 5: ");
                note = demanderIntPositif("une note");
            }
            System.out.print("Entrez un commentaire sur le produit: ");
            String comment = br.readLine();
            p.addEvaluation(new Evaluation(acheteur.getUsername(), note, comment));
            System.out.println("\nVotre évaluation a été écrite avec succès!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void changerInformations() {
        System.out.println("\nTODO");
    }
    static String afficherNotifications() {
        return "\nTODO";
    }
    static void gererAcheteursSuivis() { System.out.println("\nTODO"); }
    static void rechercherRevendeur() {
        System.out.println("\nQuel type de recherche voulez-vous faire?");
        try {
            ArrayList<String> revendeurSelect = new ArrayList<>();
            List<String> revendeurs = fichiersDansDossier(USERS_PATH + REVENDEURS);
            String demandeUtilisateur = "";
            boolean estRecherche = 1 == selectionChoix(new String[]{"Recherche par mots-clés",
                    "Recherche par filtre"});
            String adressesDemande = "";

            if (estRecherche) {
                System.out.print("Entrez votre recherche: ");
                demandeUtilisateur = br.readLine();
            } else {
                System.out.println("Choisissez votre type de filtre: ");
                choix = selectionChoix(new String[]{"Catégorie de produits vendues", "Nom", "Adresse"});
                switch (choix) {
                    case 1 -> {
                        System.out.println("Choisissez une catégorie: ");
                        demandeUtilisateur = Categorie.getCat(selectionChoix(Categorie.categories) - 1);

                    }
                    case 2 -> {
                        System.out.println("Choisissez un nom :");
                        demandeUtilisateur = br.readLine();

                    }
                    case 3 -> {
                        System.out.print("Choisissez une adresse ");
                        demandeUtilisateur = br.readLine();
                    }
                }
            }

            for (String r : revendeurs) {
                String[] contenu = lireFichierEnEntier(USERS_PATH + REVENDEURS + r + "/Infos.csv");
                String adresse = contenu[0].split(",")[5];
                List<String> categories = Arrays.asList(contenu[2].split(","));

                if (estRecherche) {
                    if (r.contains(demandeUtilisateur))
                        revendeurSelect.add(r);
                } else {
                    switch (choix) {
                        case 1 -> {
                            if (categories.contains(demandeUtilisateur))
                                revendeurSelect.add(r);
                        }
                        case 2 -> {
                            if (r.contains(demandeUtilisateur))
                                revendeurSelect.add(r);
                        }
                        case 3 -> {
                            if (adresse.contains(demandeUtilisateur))
                                revendeurSelect.add(r);
                        }
                    }
                }
            }
            if (revendeurSelect.isEmpty()) {
                System.out.println("Aucun résultat pour cette recherche. Veuillez réessayer.");
                return;
            }
            revendeurSelect.add("Faire une nouvelle recherche");
            revendeurSelect.add("Retourner au menu acheteur");
            while (true) {
                System.out.println("\nChoisissez un revendeur: ");
                choix = selectionChoix(revendeurSelect.toArray());
                if (choix == revendeurSelect.size() - 1)
                    return;
                else if (choix == revendeurSelect.size())
                    return;
                Revendeur r = initialiserRevendeur(revendeurSelect.get(choix - 1));
                System.out.println("\n" + r.afficherMetriques());
                System.out.println("\nQue voulez-vous faire ensuite?");
                choix = selectionChoix(new String[]{"Liker le Revendeur", "Retourner au résultat de la recherche"});
                if (choix == 2) {
                    return;
                } else {
                    if (followDeja(r.getUsername())) {
                        System.out.println("Vous suivez déjà ce revendeur.");
                        return;
                    } else {
                        r.ajouterFollower(acheteur.getUsername());
                        System.out.println("\nFelicitations !!! Vous suivez maintenant le revendeur.");
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Quelque chose s'est mal passé. Veuillez réessayer.");
            rechercherProduits();
        }
    }

    static Boolean followDeja(String username) {
        List<String> revendeurs = fichiersDansDossier(USERS_PATH + REVENDEURS);
        for (String r : revendeurs){;
            if (username == r) {
                return false;
            }
            else {
                return true;
            }
        }
        return null;
    }
}