package unishop;

import unishop.Users.Acheteur;

import java.io.IOException;
import java.util.ArrayList;

import static unishop.Main.*;

public class ControleurInvite {
    static short choix;
    static void menuInvite(){
        while (true) {
            System.out.println("\nVoici le menu Invité:");
            choix = selectionChoix(new String[]{"Trouver un acheteur", "Trouver un revendeur", "Trouver un produit",
                    "Retourner au menu principal"});
            switch (choix) {
                case 1 -> trouverAcheteur();
                case 2 -> trouverRevendeur();
                case 3 -> trouverProduits();
                case 4 -> {
                    return;
                }
            }
        }
    }
    static void trouverAcheteur() {
        try {
            while (true) {
                System.out.print("\nEntrer le nom d'un acheteur (N'entrez rien pour la liste de tous les acheteurs): ");
                Acheteur acheteur;
                String a = br.readLine();
                ArrayList<String> as = new ArrayList<>(fichiersDansDossier(USERS_PATH + ACHETEURS));
                as.add("Faire une nouvelle recherche");
                as.add("Retourner au menu");
                if (a.isEmpty()) {
                    System.out.println("Sélectionnez une option: ");
                    choix = selectionChoix(as.toArray());
                    if (choix == as.size() - 2)
                        continue;
                    else if (choix == as.size() - 1)
                        return;
                    acheteur = initialiserAcheteur(as.get(choix));
                }
                else {
                    if (as.contains(a)) {
                        acheteur = initialiserAcheteur(a);
                    }
                    else {
                        System.out.println("\nCet acheteur n'existe pas! Veuillez réessayer.");
                        continue;
                    }
                }
                while (true) {
                    System.out.println("\nVoici les informations sur " + acheteur.getUsername() + ": " +
                            acheteur.afficherMetriques());
                    System.out.println("\nQue voulez-vous faire ensuite?");
                    choix = selectionChoix(new String[] {"Voir les followers", "Voir les suivis", "Faire une autre recherche",
                            "Retourner au menu principal"});
                    if (choix == 3)
                        break;
                    else if (choix == 4)
                        return;
                    if (choix == 1)
                        as = acheteur.getFollowers();
                    else
                        as = acheteur.getSuivis();
                    if (as.isEmpty()) {
                        if (choix == 1)
                            System.out.println("\n" + acheteur.getUsername() + " n'a aucun follower!");
                        else
                            System.out.println("\n" + acheteur.getUsername() + " ne suit aucun acheteur!");
                        continue;
                    }
                    System.out.println("\nChoississez un acheteur: ");
                    choix = selectionChoix(as.toArray());
                    acheteur = initialiserAcheteur(as.get(choix));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    static void trouverProduits() {

    }
    static void trouverRevendeur() {

    }
}
