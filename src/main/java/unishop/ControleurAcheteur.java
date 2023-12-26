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
                case 1 -> gererCommandes();
                case 2 -> allerAuPanier();
                case 3 -> gererBillets();
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
                case 9 -> System.out.println("\n" + acheteur.voirNotifications());
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
                            if (choixOuiNon())
                                return 2;
                        }
                        case 3 -> {
                            if (p.getEvaluations().isEmpty()) {
                                System.out.println("\nCe produit n'a aucune évaluations!");
                                break;
                            }
                            System.out.println("\nChoississez une évaluation:");
                            ArrayList<Evaluation> es = p.getEvaluations();
                            ArrayList<String> ess = new ArrayList<>();
                            for (Evaluation e : es)
                                ess.add(e.getDisplayFormat() + "\n");
                            ess.add("Retourner au produit");
                            choix = selectionChoix(ess.toArray());
                            if (choix == ess.size())
                                continue;
                            Evaluation e = es.get(choix - 1);
                            System.out.println("\n" + e.getDisplayFormat());
                            if (e.nomAcheteur.equals(acheteur.getUsername())) {
                                System.out.println("\nC'est votre propre évaluation!");
                                continue;
                            }
                            System.out.println("\nQue voulez-vous faire?");
                            choix = selectionChoix(new String[] {"Liker l'évaluation",
                                    "Signaler l'évaluation comme inappropriée", "Retourner au produit"});
                            if (choix == 3)
                                continue;
                            Acheteur a = initialiserAcheteur(e.nomAcheteur);
                            if (choix == 1) {
                                if (e.getLikes() == 0)
                                    a.ajouterPoints(10);
                                if (e.ajouterLike(acheteur.getUsername())) {
                                    p.save();
                                    System.out.println("\nVous avez liké l'évaluation de " + a.getUsername() + "!");
                                }
                                else
                                    System.out.println("\nVous avez déjà liké cette évaluation!");
                            }
                            else {
                                if (e.signaler()) {
                                    if (e.getLikes() == 0)
                                        a.ajouterPoints(-10);
                                    e.signaler();
                                    p.save();
                                    System.out.println("\nVous avez signalé cette évaluation!");
                                }
                                else
                                    System.out.println("\nCette évaluation était déjà signalée!");
                            }

                        }
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
    static void gererCommandes() {
        ArrayList<Commande> cmds = acheteur.getCommandes();
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
        while (true) {
            System.out.println("\nChoisissez une action: ");
            choix = selectionChoix(new String[]{"Confirmer la livraison", "Retourner un produit", "Évaluer un produit",
                    "Annuler la commande", "Regarder la date d'arrivée estimée", "Retourner au menu"});
            switch (choix) {
                case 1 -> {
                    switch (cmd.confirmerLivraison()) {
                        case 0 -> {
                            System.out.println("\nL'état de votre commande a été changé avec succès!");
                        }
                        case 1 -> System.out.println("\nVotre commande est toujours en production.");
                        case 2 -> System.out.println("\nVous avez déjà confirmé la livraison de cette commande!");

                    }
                }
                case 2 -> {
                    if (!cmd.estLivre()) {
                        System.out.println("\nVotre commande n'a pas encore été livrée!");
                        continue;
                    }
                    if (obtenirTempsEnSecondes() - cmd.getTempsReception() > 2592000) {
                        System.out.println("\nVous avez passé le délai de 30 jours pour retourner une commande!");
                        continue;
                    }

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
                        Produit pro = initialiserProduit(produitRempla);
                        float differencePrix = arrondirPrix(pro.prix - p.prix);
                        System.out.println("La différence de prix est de " + differencePrix + "$.");
                        Commande echange = new Commande((short)0, differencePrix, 0);
                        echange.addProduit(pro);
                        echange.passerCommande(acheteur.getAddress());
                        acheteur.ajouterCommande(echange);
                        r.ajouterCommande(echange);
                        System.out.println("\nVotre demande " + (b.estRetour ? "de retour" : "d'échange") + " a été " +
                                "traitée avec succès!");

                        //TODO NOTIF
                        Notification notifRev = new Notification(8, acheteur.getUsername(), p.nomReven,
                                p.titre, echange.getId());
                        Revendeur rev = initialiserRevendeur(p.nomReven);
                        rev.addNotifications(notifRev);

                        System.out.println("Votre ID pour ce billet est: " + p.getId());
                        System.out.println("L'ID de votre commande est " + echange.getId());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case 3 -> {
                    if (!cmd.estLivre()) {
                        System.out.println("\nVotre commande n'a pas encore été livrée!");
                        continue;
                    }
                    Produit p = cmd.getChoixProduit(true);
                    if (p == null)
                        continue;
                    ecrireEvaluation(p);
                }
                case 4 -> {
                    if (cmd.estEnProduction()) {
                        System.out.println("\nVoulez-vous vraiment annuler votre commande?");
                        if (choixOuiNon()) {
                            effacerFichier(COMMANDES_PATH + cmd.getId() + CSV);
                            acheteur.annulerCommande(cmd);
                            try {
                                for (String re : cmd.getRevendeurs()) {
                                    Revendeur r = initialiserRevendeur(re);
                                    r.annulerCommande(r.trouverCommande(cmd.getId()));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            System.out.println("\nVous avez annulé votre commande #" + cmd.getId() + "!");
                            return;
                        }
                    }
                    else
                        System.out.println("\nVous ne pouvez plus annuler votre commande!");

                }
                case 5 -> {
                    if (cmd.estLivre())
                        System.out.println("\nVotre commande est déjà livrée!");
                    else
                        System.out.println("\nVotre commande devrait arriver aujourd'hui le " + new java.util.Date());
                }
                case 6 -> {return;}
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
                    if (!choixOuiNon()) {
                        System.out.print("Entrez une nouvelle adresse: ");
                        adresse = br.readLine();
                    }
                    System.out.println("Voulez-vous payer avec vos points?");
                    if (choixOuiNon()) {
                        float pointsEnDollars = arrondirPrix(acheteur.viderPoints() / 100f);
                        float nouveauTotal = acheteur.panier.getCoutTotal() - pointsEnDollars;
                        System.out.println("Vous avez l'équivalent de " + pointsEnDollars + "$ en points, ce qui " +
                                "amène votre total à " + nouveauTotal + "$.");
                    }
                    System.out.print("Entrez votre numéro de carte de crédit: ");
                    demanderLong("un numéro de carte de crédit");
                    System.out.print("Entrez la date d'expiration de votre carte en format MMAA: ");
                    demanderIntPositif("une date d'expiration");
                    System.out.print("Entrez le CVV: ");
                    demanderIntPositif("un CVV");
                    Commande c = acheteur.panier.passerCommande(adresse);
                    acheteur.ajouterCommande(c.copy());

                    ArrayList<String> revens = new ArrayList<>();
                    for (Produit p : c.getProduitsP()) {
                        Revendeur r = initialiserRevendeur(p.nomReven);
                        Notification notifRev = new Notification(6, acheteur.getUsername(), p.nomReven,
                                p.titre, c.getId());
                        r.addNotifications(notifRev);
                        if (!revens.contains(p.nomReven)) {
                            r.ajouterCommande(c);
                            revens.add(r.getUsername());
                        }
                    }
                    acheteur.panier.vider();
                    System.out.println("\nVotre commande a été passée avec succès!");
                    //TODO NOTIF
                    System.out.println("Votre identifiant de commanque unique est: " + c.getId());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    static void gererBillets() {
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
                    if (b.comfirmerLivraisonRempla())
                        System.out.println("\nVous avez confirmé la livraison du produit de remplacement!");
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
            Evaluation e = new Evaluation(acheteur.getUsername(), note, comment, false, new ArrayList<>());
            p.addEvaluation(e);
            System.out.println("\nVotre évaluation a été écrite avec succès!");

            //TODO NOTIF
            Notification notif = new Notification(7, acheteur.getUsername(), p.nomReven, p.titre, -1);
            Revendeur r = initialiserRevendeur(p.nomReven);
            r.addNotifications(notif);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void changerInformations() {
        System.out.println("\nTODO");
    }
    static void gererAcheteursSuivis() { System.out.println("\nTODO"); }
    static void rechercherRevendeur() {
        while (true) {
            System.out.println("\nQuel type de recherche voulez-vous faire?");
            try {
                ArrayList<String> revendeurSelect = new ArrayList<>();
                List<String> revendeurs = fichiersDansDossier(REVENDEURS_PATH);
                String demandeUtilisateur = "";
                boolean estRecherche = 1 == selectionChoix(new String[]{"Recherche par mots-clés",
                        "Recherche par filtre"});

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
                            System.out.print("Entrez un nom: ");
                            demandeUtilisateur = br.readLine();

                        }
                        case 3 -> {
                            System.out.print("Entrez une adresse: ");
                            demandeUtilisateur = br.readLine();
                        }
                    }
                }

                for (String r : revendeurs) {
                    String[] contenu = lireFichierEnEntier(REVENDEURS_PATH + r + "/" + INFOS);
                    String adresse = contenu[0].split(",")[3];
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
                    System.out.println("\nAucun résultat pour cette recherche. Veuillez réessayer.");
                    continue;
                }
                revendeurSelect.add("Faire une nouvelle recherche");
                revendeurSelect.add("Retourner au menu acheteur");
                while (true) {
                    System.out.println("\nChoisissez un revendeur: ");
                    choix = selectionChoix(revendeurSelect.toArray());
                    if (choix == revendeurSelect.size() - 1)
                        break;
                    else if (choix == revendeurSelect.size())
                        return;
                    Revendeur r = initialiserRevendeur(revendeurSelect.get(choix - 1));
                    System.out.println(r.afficherMetriques());
                    System.out.println("\nQue voulez-vous faire ensuite?");
                    choix = selectionChoix(new String[]{"Liker le revendeur", "Retourner au résultat de la recherche"});
                    if (choix == 1) {
                        if (r.ajouterFollower(acheteur.getUsername()))
                            System.out.println("\nFélicitations! Vous suivez maintenant le revendeur.");
                        else
                            System.out.println("\nVous suivez déjà ce revendeur.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Quelque chose s'est mal passé. Veuillez réessayer.");
                rechercherProduits();
            }
        }
    }

}