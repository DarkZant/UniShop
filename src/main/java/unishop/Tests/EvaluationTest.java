package unishop.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unishop.Evaluation;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationTest {

    private Evaluation evaluation;

    @BeforeEach
    void setUp() {
        evaluation = new Evaluation("Etienne", 3, "moyen", 6, false);
    }

    @Test
    void isEqual() {
        boolean resultat = evaluation.isEqual(evaluation);
        assertTrue(resultat);
    }

    @Test
    void signaler() {
        boolean resultat = evaluation.signaler();
        assertTrue(resultat);
    }

    @Test
    void ajouterLike() {
        evaluation.ajouterLike();
        assertEquals(7, evaluation.getLikes());
    }
}