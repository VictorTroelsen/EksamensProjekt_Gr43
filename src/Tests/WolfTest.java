/*package animals;

import actions.WolfPack;
import biodiversity.Carcass;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WolfTest {
    private Wolf wolf1;
    private Wolf wolf2;
    private World world;

    @BeforeEach
    void setUp() {
        // Opret en testverden på 10x10
        world = new World(10);

        // Initialiser to ulve med placeringer og energi
        wolf1 = new Wolf(world, new Location(1, 1), null, 5, null);
        wolf2 = new Wolf(world, new Location(2, 2), null, 3, null);

        // Placér ulvene i verdenen
        world.setTile(new Location(1, 1), wolf1);
        world.setTile(new Location(2, 2), wolf2);
    }

    @Test
    void testWolfJoinsOrFormsPack() {
        // Tjek at ulve starter uden en flok
        assertNull(wolf1.wolfPack, "Wolf1 should not belong to any pack initially.");
        assertNull(wolf2.wolfPack, "Wolf2 should not belong to any pack initially.");

        // Kør handlingen (act), hvor Wolf1 forsøger at finde eller danne en flok
        wolf1.act(world);

        // Wolf1 skal have dannet en flok, og Wolf2 skal have tilsluttet sig den
        assertNotNull(wolf1.wolfPack, "Wolf1 should have formed a pack.");
        assertEquals(wolf1.wolfPack, wolf2.wolfPack, "Wolf2 should join the same pack as Wolf1.");
        assertEquals(wolf1, wolf1.wolfPack.getAlpha(), "Wolf1 should be the alpha of the pack.");
    }

    @Test
    void testWolfEnergyDecreasesWithActions() {
        // Startenergi
        int initialEnergy = wolf1.getEnergy();

        // Kør handling (act), som vil reducere energien
        wolf1.act(world);

        // Tjek at energien er reduceret
        assertTrue(wolf1.getEnergy() < initialEnergy, "Wolf1's energy should decrease after acting.");
    }

    @Test
    void testAlphaWolfCreatesDen() {
        // Tving Wolf1 til at være alpha og give den en pack
        WolfPack wolfPack = new WolfPack();
        wolfPack.createPack(wolf1); // Wolf1 bliver alpha
        wolf1.wolfPack = wolfPack;

        // Tjek at der ikke er nogen hule endnu
        assertFalse(wolfPack.hasDen(), "The pack should not have a den initially.");

        // Kør handling for alpha wolf, hvor den forsøger at bygge en hule
        wolf1.act(world);

        // Tjek at der nu er oprettet en hule
        assertTrue(wolfPack.hasDen(), "The pack should now have a den.");
    }

    @Test
    void testWolfHuntsAndEatsPrey() {
        // Lav et bytte (Rabbit) nær Wolf1
        Rabbit rabbit = new Rabbit(world, new Location(1, 2), null);
        world.setTile(new Location(1, 2), rabbit);

        // Reducér Wolf1's energi, så jagt bliver nødvendig
        wolf1.setEnergy(50);

        // Kør jagtmetoden
        wolf1.hunt(80); // Tærskel for jagt: 80

        // Tjek at byttet er væk, og Wolf1's energi er steget
        assertFalse(world.contains(rabbit), "Rabbit should have been hunted and removed from the world.");
        assertTrue(wolf1.getEnergy() > 50, "Wolf1's energy should increase after eating the rabbit.");
    }

    @Test
    void testEnergyDecreaseCausesDeath() {
        // Sæt energi til 0
        wolf1.setEnergy(0);

        // Kør handling
        wolf1.act(world);

        // Tjek at ulven er død og fjernet fra verden
        assertFalse(world.contains(wolf1), "Wolf1 should be removed from the world after dying.");
    }
}
*/