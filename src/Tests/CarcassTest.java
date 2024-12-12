/*

import biodiversity.NormalCarcass;
import biodiversity.SmallCarcass;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarcassTest {

    private World world;
    private Program program;
    private Location location;

    @BeforeEach
    void setUp() {
        // Eksempel på opsætning af en simpel verden
        world = new World(10); // Lav en verden på 10x10
        program = new Program(10,800,100);
        location = new Location(5, 5); // Testplads midt i verden
    }

    @Test
    void testNormalCarcassPlacement() {
        // Opret et NormalCarcass-objekt
        NormalCarcass carcass = new NormalCarcass(world, location, program, true);

        // Kontrollér, om det blev korrekt placeret i verden
        assertEquals(carcass, world.getTile(location), "Carcass should be placed at the location in the world.");
    }

    @Test
    void testSmallCarcassPlacement() {
        // Opret et SmallCarcass-objekt
        SmallCarcass carcass = new SmallCarcass(world, location, program);

        // Kontrollér, om det ikke blev placeret automatisk uden autoPlace
        assertNotEquals(carcass, world.getTile(location), "SmallCarcass should not be auto-placed.");
    }

    @Test
    void testNormalCarcassNourishment() {
        // Test for næringsværdi
        NormalCarcass carcass = new NormalCarcass(world, location, program, true);

        assertEquals(60, carcass.getNourishment(), "NormalCarcass should provide 60 nourishment.");
    }

    @Test
    void testSmallCarcassNourishment() {
        // Test for næringsværdi
        SmallCarcass carcass = new SmallCarcass(world, location, program);

        assertEquals(30, carcass.getNourishment(), "SmallCarcass should provide 30 nourishment.");
    }

    @Test
    void testDecompositionOverTime() {
        // Test for nedbrydningsprocessen
        NormalCarcass carcass = new NormalCarcass(world, location, program, true);

        // Simuler maksimalt antal nedbrydningscyklusser
        for (int i = 0; i < 10; i++) {
            carcass.act(world); // Hvert 'act' reducerer nedbrydningstiden
        }

        assertTrue(carcass.isDecomposed, "Carcass should be decomposed after max decomposition time.");

        // Kontrollér, at carcassen ikke længere er i verden
        assertNull(world.getTile(location), "Decomposed carcass should be removed from the world.");
    }

    @Test
    void testNoPrematureDecomposition() {
        // Test for at carcassen ikke nedbrydes for tidligt
        NormalCarcass carcass = new NormalCarcass(world, location, program, true);

        // Simuler færre cyklusser end den maksimale tid
        for (int i = 0; i < 5; i++) {
            carcass.act(world); // Hvert 'act' reducerer nedbrydningstiden
        }

        assertFalse(carcass.isDecomposed, "Carcass should not be decomposed before max decomposition time.");
    }
}

 */