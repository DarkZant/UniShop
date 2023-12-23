package unishop;

import unishop.Users.*;
import unishop.Categories.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static unishop.ControleurAcheteur.menuAcheteur;
import static unishop.ControleurInvite.menuInvite;
import static unishop.ControleurRevendeur.menuRevendeur;
//import java.util.regex.*;

public class Main {

    public final static String DATABASE_PATH = "Database/";
    public final static String PRODUITS_PATH = DATABASE_PATH + "Produits/";
    public final static String USERS_PATH = DATABASE_PATH + "Users/";
    public final static String ACHETEURS = "Acheteurs/";
    public final static String REVENDEURS = "Revendeurs/";
    public final static String CSV = ".csv";
    public final static String IDS = DATABASE_PATH + "IDs.csv";

    public final static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private static User connectedUser = null;
    private static short choix;

    public static void main(String[] args) {
        menuPrincipal();
        System.out.println("\nAu plaisir de vous revoir!");
    }
    static void menuPrincipal(){
        while (true) {
            System.out.println("\nBienvenue sur UniShop!");
            choix = selectionChoix(new String[]{"Se connecter", "Créer un compte", "Continuer en tant qu'invité",
                    "Quitter UniShop"});
            switch (choix) {
                case 1 -> {
                    connectedUser = connecterUser();
                    if (connectedUser == null)
                        continue;
                    System.out.print("\nRebonjour " + connectedUser.getUsername() + " ! ");
                    if (connectedUser.isAcheteur())
                        menuAcheteur((Acheteur) connectedUser);
                    else
                        menuRevendeur((Revendeur) connectedUser);
                    connectedUser = null;
                }
                case 2 -> creerCompte();
                case 3 -> menuInvite();
                case 4 -> {return;}
            }
        }
    }
    public static short selectionChoix(Object[] choix) {
        int nbChoix = choix.length;
        for (int i = 0; i < nbChoix; ++i) {
            System.out.println(i + 1 + ". " + choix[i]);
        }
        try {
            while (true) {
                System.out.print("Choisir une option: ");
                String reponseS = br.readLine();
                try {
                    short reponse = Short.parseShort(reponseS);
                    if (reponse < nbChoix + 1 && reponse > 0)
                        return reponse;
                    else
                        System.out.println("Choix invalide! Veuillez entrer un choix de 1 à " + nbChoix);
                }
                catch(NumberFormatException e){
                    System.out.println("Veuillez entrer un chiffre de 1 à " + nbChoix);
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    static void creerCompte() {
        System.out.println("\nChoisissez le type de compte à créer:");
        choix = selectionChoix(new String[]{"Acheteur", "Revendeur", "Revenir au menu principal"});
        if (choix == 3)
            return;
        System.out.println("Remplissez le formulaire pour créer un compte:");
        System.out.print("Entrez votre username: ");
        try {
            String username = br.readLine();
            List<String> acheteurs = fichiersDansDossier(USERS_PATH + ACHETEURS);
            List<String> revendeurs = fichiersDansDossier(USERS_PATH + REVENDEURS);
            while (acheteurs.contains(username) || revendeurs.contains(username)) {
                System.out.print("Ce Username existe déja. Veuillez entrer un autre Username: ");
                username = br.readLine();
            }
            System.out.print("Entrez votre mot de passe: ");
            String motDePasse = br.readLine();
            System.out.print("Entrez votre courriel: ");
            String courriel = br.readLine();
            System.out.print("Entrez votre téléphone: ");
            long telephone = demanderLong("un téléphone");
            System.out.print("Entrez votre adresse: ");
            String adresse = br.readLine();
            if (choix == 1) {
                String basePath =  USERS_PATH + ACHETEURS + username;
                System.out.print("Entrez votre nom: ");
                String nom = br.readLine();
                System.out.print("Entrez votre prénom: ");
                String prenom = br.readLine();
                if (new File(basePath).mkdir() && new File(basePath + "/Commandes").mkdir()) {
                    String[] infos = new String[] {motDePasse, courriel, "" + telephone , adresse, nom, prenom,
                            "0,0"};
                    ecrireFichierEntier(basePath + "/Infos.csv", String.join(",", infos) + "\n\n\n");
                    ecrireFichierEntier(basePath + "/Panier.csv", "0,0");
                    System.out.println("Inscription du compte acheteur " + username + " réussi!");
                }
                else
                    System.out.println("Erreur lors de la création du dossier. Veuillez recommencer");

            } else {
                String basePath = USERS_PATH + REVENDEURS + username;
                if (new File(basePath).mkdir() && new File(basePath + "/Commandes").mkdir()) {
                    String[] infos = new String[] {motDePasse, courriel, "" + telephone , adresse, "0,0,0" };
                    ecrireFichierEntier(basePath + "/Infos.csv", String.join(",", infos) + "\n\n");
                    System.out.println("Inscription du compte revendeur " + username + " réussi!");
                }
                else
                    System.out.println("Erreur lors de la création du dossier. Veuillez recommencer");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static User connecterUser() {
        try {
            System.out.println("\nConnection (Ne rien rentrer retourne au menu principal): ");
            String categorie = "";
            String username = "";
            while (categorie.isEmpty()) {
                System.out.print("Entrez votre Username: ");
                username = br.readLine();
                if (username.isEmpty())
                    return null;
                File dossier = new File (USERS_PATH + ACHETEURS + username);
                if (dossier.exists()) {
                    categorie = ACHETEURS;
                }
                dossier = new File (USERS_PATH + REVENDEURS + username);
                if (dossier.exists()) {
                    categorie = REVENDEURS;
                }
                if (categorie.isEmpty()) {
                    System.out.println("Username inconnu. Veuillez réessayer");
                }
            }

            String[] data = lireFichierEnEntier(USERS_PATH + categorie + username + "/Infos.csv");
            String[] infos = data[0].split(",");
            String password = infos[0];
            System.out.print("Entrez votre mot de passe: ");
            String passwordEntre = br.readLine();
            if (passwordEntre.isEmpty())
                return null;
            while (!(password.equals(passwordEntre))) {
                System.out.print("Mauvais mot de passe! Veuillez recommencer: ");
                passwordEntre = br.readLine();
            }
            if (categorie.equals(ACHETEURS)){
                return initialiserAcheteur(username);
            }
            else {
                return initialiserRevendeur(username);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    static Acheteur initialiserAcheteur(String username) throws IOException {
        String path = USERS_PATH + ACHETEURS + username + "/";
        String[] data = lireFichierEnEntier( path+ "Infos.csv");
        String[] infos = data[0].split(",");
        ArrayList<String> as =  iniArrayList(data[1]);
        ArrayList<String> rl =  iniArrayList(data[2]);
        ArrayList<Billet> bis = new ArrayList<>();
        for (int i = 3; i < data.length; ++i) {
            String[] bs = data[i].split(",");
            bis.add(new Billet(Integer.parseInt(bs[0]), bs[1], bs[2], bs[3], Boolean.parseBoolean(bs[4]),
                     Boolean.parseBoolean(bs[5]) , bs[6], bs[7], Boolean.parseBoolean(bs[8])));
        }
        String[] s = lireFichierEnEntier(path +"Panier.csv");
        String[] i = s[0].split(",");
        float coutT = Float.parseFloat(i[0]);
        int pts = Integer.parseInt(i[1]);
        Commande panier = new Commande((short) 0, coutT, pts);
        for (int j = 1; j < s.length; ++j) {
            Produit p = initialiserProduit(s[j]);
            panier.addInitial(p);
        }

        ArrayList<Commande> cmds = new ArrayList<>();
        String commandesPath = path + "Commandes/";
        for (String l : fichiersDansDossier(commandesPath)) {
            String[] all = lireFichierEnEntier(commandesPath + l);
            String[] fstLine = all[0].split(",");
            Commande c = new Commande(Short.parseShort(fstLine[2]), Float.parseFloat(fstLine[3]),
                    Integer.parseInt(fstLine[4]));
            c.addPastInfo(Integer.parseInt(fstLine[0]), fstLine[1], fstLine[5]);
            for (int j = 1; j < all.length; ++j) {
                String[] line = all[j].split(",");
                Produit p = initialiserProduit(line[0]);
                p.setUniqueId(Integer.parseInt(line[1]));
                c.addInitial(p);
            }
            cmds.add(c);
        }
        return new Acheteur(username, infos[0], infos[1], Long.parseLong(infos[2]),
                infos[3], infos[4], infos[5], Integer.parseInt(infos[6]), Integer.parseInt(infos[7]), as, rl, bis,
                panier, cmds);
    }
    static Revendeur initialiserRevendeur(String username) throws IOException {
        String path = USERS_PATH + REVENDEURS + username + "/";
        String[] data = lireFichierEnEntier( path+ "Infos.csv");
        String[] infos = data[0].split(",");
        ArrayList<String> followers = iniArrayList(data[1]);
        ArrayList<Billet> bis = new ArrayList<>();
        for (int i = 2; i < data.length; ++i) {
            String[] bs = data[i].split(",");
            bis.add(new Billet(Integer.parseInt(bs[0]), bs[1], bs[2], bs[3], Boolean.parseBoolean(bs[4]),
                    Boolean.parseBoolean(bs[5]) , bs[6], bs[7], Boolean.parseBoolean(bs[8])));
        }
        ArrayList<Produit> ps = new ArrayList<>();
        for(String pc : fichiersDansDossier(PRODUITS_PATH)){
            String r = lireFichierEnEntier(PRODUITS_PATH + pc)[0].split(",")[0];
            if (r.equals(username))
                ps.add(initialiserProduit(pc));
        }
        return new Revendeur(username, infos[0], infos[1], Long.parseLong(infos[2]), infos[3],
                Float.parseFloat(infos[4]), Integer.parseInt(infos[5]), followers, bis, ps, new ArrayList<>());
    }
    static Produit initialiserProduit(String titreProduit) throws IOException{
        String path = PRODUITS_PATH + titreProduit;
        if (!titreProduit.endsWith(CSV))
            path += CSV;
        String[] s = lireFichierEnEntier(path);
        String[] f = s[0].split(",");
        String[] images = s[1].split(",");
        String[] videos = s[2].split(",");
        String[] cs = s[3].split(",");
        Categorie c = null;
        switch (Short.parseShort(cs[0])) {
            case 0 -> c = new CLivres(cs[1], cs[2], cs[3], Long.parseLong(cs[4]), cs[5], Integer.parseInt(cs[6]),
                    Integer.parseInt(cs[7]));
            case 1 -> c = new CRessources(cs[1], cs[2], cs[3], Long.parseLong(cs[4]), cs[5], Integer.parseInt(cs[6]));
            case 2 -> c = new CPapeterie(cs[1], cs[2], cs[3]);
            case 3 -> c = new CInformatique(cs[1], cs[2], cs[3], cs[4]);
            case 4 -> c = new CBureau(cs[1], cs[2], cs[3]);
        }
        ArrayList<String> likes =  iniArrayList(s[4]);
        Evaluation[] evals = new Evaluation[s.length - 5];
        for (int i = 5; i < s.length; ++i) {
            String[] e = s[i].split(",");
            evals[i - 5] = new Evaluation(e[0], Byte.parseByte(e[1]), e[2]);
        }
        ArrayList<Evaluation> evalsL = new ArrayList<>(Arrays.asList(evals));
        return new Produit(f[0], f[1], f[2], Float.parseFloat(f[3]), Integer.parseInt(f[4]), Integer.parseInt(f[5]),
                images, videos, c, likes, evalsL);
    }
    static int demanderIntPositif(String demande) throws IOException {
        int i;
        while (true) {
            try {
                i = Integer.parseInt(br.readLine());
                if (i >= 0)
                    return i;
                else {
                    System.out.print("Veuillez entrer un entier positif: ");
                }
            }
            catch (NumberFormatException e) {
                System.out.print("Veuillez entrer " + demande + " valide: ");
            }
        }
    }
    static long demanderLong(String demande) throws IOException {
        long l;
        while (true) {
            try {
                l = Long.parseLong(br.readLine());
                return l;
            }
            catch (NumberFormatException e) {
                System.out.print("Veuillez entrer " + demande + " valide: ");
            }
        }
    }
    static float demanderFloat(String demande) throws IOException {
        float prix;
        while (true) {
            try {
                prix = arrondirPrix(Float.parseFloat(br.readLine()));
                if (prix >= 0)
                    return arrondirPrix(prix);
                else
                    System.out.print("Veuillez entrer un chiffre à virgule positif: ");
            }
            catch (NumberFormatException e) {
                System.out.print("Veuillez entrer " + demande + " valide (Nombre à virgule): ");
            }
        }
    }
    public static float arrondirPrix(float prix) {
        return Math.round((prix) * 100) / 100f;
    }

    public static String[] lireFichierEnEntier(String path) throws IOException {
        return Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8).toArray(new String[0]);
    }
    public static void ecrireFichierEntier(String path, String toWrite) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(toWrite);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static List<String> fichiersDansDossier(String path) {
        return Arrays.asList(Objects.requireNonNull(new File(path).list()));
    }
    static ArrayList<String> iniArrayList(String s) {
        String[] tab = s.split(",");
        if (tab[0].isEmpty())
            return new ArrayList<>(Arrays.asList(tab).subList(1, tab.length));
        else
            return new ArrayList<>(Arrays.asList(tab));

    }
    public static String getConnectedUsername() {
        return connectedUser.getUsername();
    }
}