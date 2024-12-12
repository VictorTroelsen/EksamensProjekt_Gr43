/*package animals;

import biodiversity.Carcass;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarnivoreTest {
    private Carnivore carnivore;
    private World world;

    @BeforeEach
    void setUp() {
        // Initialiser en testverden med størrelse på 10x10
        world = new World(10);

        // Placér en Carnivore på location (5,5)
        carnivore = new Carnivore(world, new Location(5, 5), null) {
            @Override
            public void act(World world) {
                // Dummy implementation for abstract method
            }
        };

        // Sæt Carnivore's startenergi
        carnivore.setEnergy(30);
        world.setTile(new Location(5, 5), carnivore);
    }

    @Test
    void testCarnivoreHuntsAndEatsCarcass() {
        // Placer en prey (Animal) i nærheden af Carnivoren
        Location preyLocation = new Location(6, 5);
        Animal prey = new Animal(world, preyLocation, null);
        world.setTile(preyLocation, prey);

        // Prey's energi reduceres når den dør, og den skaber et carcass
        prey.dies();

        // Verificér at byttet er blevet slettet fra verden
        assertTrue(!world.contains(prey), "Prey should no longer exist in the world after dying.");

        // Kør jagt (hunt) for Carnivore
        carnivore.hunt(50); // Energitærskel for jagt: 50

        // Verificér at Carnivoren har spist ådslet
        Carcass carcass = (Carcass) world.getTile(preyLocation); // Hent ådsel på preyLocation
        assertTrue(carcass == null, "Carcass should have been eaten by the Carnivore.");

        // Verificér at energien er forøget korrekt
        int expectedEnergy = 30 + 50; // 30 startenergi + 50 fra ådslet
        assertEquals(expectedEnergy, carnivore.getEnergy(), "Carnivore should gain energy from eating carcass.");
    }

    @Test
    void testCarnivoreDoesNotHuntWhenEnergyIsHigh() {
        // Sæt Carnivore's energi til over tærsklen
        carnivore.setEnergy(60);

        // Placer en prey (Animal) i nærheden
        Location preyLocation = new Location(6, 5);
        Animal prey = new Animal(world, preyLocation, null);
        world.setTile(preyLocation, prey);

        // Kør jagt (hunt) for Carnivore
        carnivore.hunt(50); // Energitærskel for jagt: 50

        // Verificér at byttet stadig eksisterer
        assertTrue(world.contains(prey), "Prey should still exist in the world, as Carnivore did not need to hunt.");

        // Verificér at Carnivorens energi forblev uændret
        assertEquals(60, carnivore.getEnergy(), "Carnivore's energy should remain unchanged when no hunt occurs.");
    }
}

 */