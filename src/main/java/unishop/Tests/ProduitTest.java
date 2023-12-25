package unishop.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unishop.Categories.CPapeterie;
import unishop.Categories.Categorie;
import unishop.Evaluation;
import unishop.Produit;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProduitTest {

    private final ArrayList<String> likes = new ArrayList<>();
    private Produit cahierUltime;

    @BeforeEach
    void setUp() {
        likes.add("Juan");
        likes.add("Pedro");
        likes.add("Mandi");

        cahierUltime = new Produit("Juan", "CahierUltime", "blablabla", 8.5f, 10,
                20, new String[3], new String[3], new CPapeterie("Nike", "Ultime", "Cahier"),
                likes, new ArrayList<Evaluation>());
    }

    @Test
    void liker_ERREUR() {
        String acheteur = "Mandi";
        String resultat = cahierUltime.liker(acheteur);
        assertEquals("Vous avez déjà liké ce produit!", resultat);
    }

    @Test
    void liker_SUCCES() {
        String acheteur = "Cedric";
        String resultat = cahierUltime.liker(acheteur);
        assertEquals("Vous avez liké CahierUltime!", resultat);
    }

    @Test
    void estEnPromotion() {
        boolean resultat = cahierUltime.estEnPromotion();
        assertTrue(resultat);
    }

    @Test
    void aDesEvaluations() {
        boolean resultat = cahierUltime.aDesEvaluations();
        assertFalse(resultat);
    }
}