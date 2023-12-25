package unishop.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unishop.Billet;
import unishop.Commande;
import unishop.Users.Acheteur;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AcheteurTest {

    final ArrayList<String> acheteursSuivis = new ArrayList<>();

    private Acheteur javier;

    @BeforeEach
    public void setUp() {
        acheteursSuivis.add("Pedro");
        acheteursSuivis.add("Jose");
        acheteursSuivis.add("Mitchell");

        // Nouveau acheteur
        javier = new Acheteur("Javier", "123","123",1232323332,"123","Medina","Javier"
                ,100,50,acheteursSuivis,new ArrayList<String>(),new ArrayList<Billet>(),
                new Commande((short) 1,1.5f,1),new ArrayList<>(),new ArrayList<>());
    }

    @Test
    void suivre_ERREUR1() {
        String acheteur = "Javier";

        String resultat = javier.suivre(acheteur);

        // Retour correct
        assertEquals("Vous ne pouvez pas vous suivre vous-même!", resultat);
    }

    @Test
    void suivre_ERREUR2() {
        String acheteur = "Pedro";

        String resultat = javier.suivre(acheteur);

        // Retour correct
        assertEquals("Vous suivez déjà cet acheteur!", resultat);
    }

    @Test
    void suivre_SUCCES() {
        String acheteur = "Martin";

        ArrayList<String> expectedArray = new ArrayList<>(3);
        expectedArray.add("Pedro");
        expectedArray.add("Jose");
        expectedArray.add("Mitchell");
        expectedArray.add(acheteur);

        String resultat = javier.suivre(acheteur);

        // ArrayList correct
        assertEquals(expectedArray, javier.getSuivis());
        // Retour correct
        assertEquals("Vous suivez maintenant Martin!", resultat);
    }

    @Test
    void afficherMetriques() {
        String resultat = javier.afficherMetriques();

        assertEquals("\nNombre de points: 100\nNombre total de commandes effectuées: 0\nNombre de followers: 0", resultat);
    }
}