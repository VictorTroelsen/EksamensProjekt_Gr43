/*
import animals.Rabbit;
import org.junit.Before;

import org.junit.Test;
import static org.junit.Assert.*;

// Importer de relevante klasser for at teste Dyr.Rabbit
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;

public class RabbitTest {

    private World world;
    private Location initialLocation;
    private Rabbit rabbit;
    private Program program;

    @Before
    public void setUp() {
        // Initialiserer verden og programobjekter
        world = new World(10);  // Størrelsen på verden er sat til 10x10
        initialLocation = new Location(0, 0);

        // Initialiser `Program` med de korrekte argumenter
        String arg1 = "exampleArg";
        int arg2 = 42;
        boolean arg3 = true;
        program = new Program(5,1000,100);

        // Initialiser kaninen
        rabbit = new Rabbit(world, initialLocation, program);
    }

    @Test
    public void testRabbitPlacement() {
        assertTrue("Dyr.Rabbit should be placed in the world", rabbit.isPlaced());
    }

    @Test
    public void testEatGrass() {
        Location grassLocation = new Location(1, 1);
        world.setTile(grassLocation, new biodiversity.Grass(grassLocation));

        rabbit.eatGrass(grassLocation);

        assertNull("biodiversity.Grass should be removed after being eaten", world.getTile(grassLocation));
        assertEquals("Dyr.Rabbit should gain energy after eating", 120, rabbit.getEnergy());
    }

    @Test
    public void testMove() {
        Location emptyLocation = new Location(1, 0);
        rabbit.move();

        assertTrue("Dyr.Rabbit should have moved to a new location", !rabbit.getLocation().equals(initialLocation));
    }

    @Test
    public void testReproduce() {
        rabbit.setAge(6);
        rabbit.setEnergy(40);

        Location emptyLocation = new Location(1, 1);
        world.setTile(emptyLocation, null);

        rabbit.reproduce();

        Object newActor = world.getTile(emptyLocation);

        assertTrue("Dyr.Rabbit should reproduce if conditions are met", newActor instanceof Dyr.Rabbit);
    }

    @Test
    public void testDigHole() {
        rabbit.setEnergy(20);
        rabbit.digHole();

        Object tileContent = world.getTile(rabbit.getLocation());

        assertTrue("Dyr.Rabbit should dig a hole and place it on the current location", tileContent instanceof actions.RabbitHole);
        assertEquals("Dyr.Rabbit's energy should decrease after digging", 10, rabbit.getEnergy());
    }
}
*/