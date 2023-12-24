package unishop.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unishop.Main;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void selectionChoix() {
        Object[] choix = {"Option 1", "Option 2", "Option 3"};

        // Input Valide
        String inputValide = "2\n";

        // Creation du "fake" input et son output
        java.io.ByteArrayInputStream fakeInput = new java.io.ByteArrayInputStream(inputValide.getBytes());
        System.setIn(fakeInput);
        java.io.ByteArrayOutputStream fakeOutput = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(fakeOutput));

        short resultat = Main.selectionChoix(choix);

        System.setIn(System.in);
        System.setOut(System.out);

        // Affichage des choix correct
        assertEquals(fakeOutput.toString(), "1. Option 1\n2. Option 2\n3. Option 3\nChoisir une option: ");

        // Retour correct
        assertEquals(2, resultat);
    }

    @Test
    public void demanderIntPositif() {
    }

    @Test
    public void demanderLong() {
    }

    @Test
    public void demanderFloat() {
    }

    @Test
    public void arrondirPrix() {
    }

    @Test
    public void iniArrayList() {
    }

}