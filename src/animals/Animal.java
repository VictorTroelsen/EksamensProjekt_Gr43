package animals;

import biodiversity.Carcass;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.Random;
import java.util.Set;

public class Animal implements Actor {
    Location location;
    static World world;
    int energy;
    int age;
    boolean isPlaced;
    Program program;
    String Animal;

    public Animal(World world, Location initialLocation, Program program) {
        this.world = world;
        this.energy = 100;
        this.age = 0;
        this.program = program;
        this.location = initialLocation;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName(); // Returner klassens navn som dyrets navn
    }



    protected int maximumAge() {
        // Angiv et standard maximum alder for dyr, der kan overskrives af specifikke arter
        return 20;
    }

    private int initialEnergy() {
        return 100 - age;
    }


    public boolean isPlaced() {
        return isPlaced;
    }

    boolean isTileEmptyOrNonBlocking(Location location) {
        Object tile = world.getTile(location);
        return tile == null || tile instanceof NonBlocking;
    }

    public void dies() {
        System.out.println(this + " died at location: " + location);

        world.delete(this);

        Carcass carcass = createCarcass();

        if (carcass != null) {
            try {
                world.setTile(location, carcass);
            } catch (IllegalArgumentException e) {
                System.out.println("Failed to place carcass: " + e.getMessage());
                // Optionally handle carcass placement failure (e.g., discard it)
            }
        }
    }

    protected Carcass createCarcass() {
        // Base implementation opretter ingen Carcass
        return null;
    }

    /**
     * @param world providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World world) {
        move();
        energy -= 5;
        updateEnergy();
    }

    void updateEnergy() {
        energy -= 2 * age;
    }


    Location findEmptyAdjacentLocation() {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        for (Location loc : surroundingTiles) {
            if (isTileEmptyOrNonBlocking(loc)) {
                return loc;
            }
        }
        return null;
    }

    public void move() {
        if (!world.isOnTile(this)) {
            System.out.println(this + " is not on any tile.");
            return;
        }

        Random random = new Random();
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        Location[] shuffledTiles = surroundingTiles.toArray(new Location[0]);

        for (int i = 0; i < shuffledTiles.length; i++) {
            int randomIndex = random.nextInt(shuffledTiles.length);
            Location temp = shuffledTiles[i];
            shuffledTiles[i] = shuffledTiles[randomIndex];
            shuffledTiles[randomIndex] = temp;
        }

        for (Location newLocation : shuffledTiles) {
            System.out.println(this + " attempting to move from " + location + " to " + newLocation);

            Object tileContent = world.getTile(newLocation);

            if (tileContent == null || tileContent instanceof NonBlocking) {
                System.out.println("Move valid, proceeding...");
                try {

                    world.move(this, newLocation);
                    location = newLocation;


                    System.out.println(this + " moved to location: " + newLocation);
                    return;
                } catch (IllegalArgumentException e) {
                    System.out.println("Move blocked: " + e.getMessage());
                }
            } else {
                System.out.println("Move blocked: Location is occupied or the same as the current location.");
            }
        }
    }

    public int getEnergy() {
        return energy;
    }

    public Object getLocation() {
        return location;
    }

    boolean placeAnimal(Location initialLocation) {
        int attempts = 0;
        int maxAttempts = 2; // Eller brug en dynamisk beregning, som er blevet kommenteret
        Random random = new Random();

        while (attempts < maxAttempts) {
            if (initialLocation != null && isTileEmptyOrNonBlocking(initialLocation)) {
                this.location = initialLocation;
                world.setTile(initialLocation, this);
                System.out.println(this + " placed at location: " + initialLocation);
                return true;
            }

            int x = random.nextInt(world.getSize());
            int y = random.nextInt(world.getSize());
            Location location = new Location(x, y);

            if (isTileEmptyOrNonBlocking(location)) {
                this.location = location;
                world.setTile(location, this);
                System.out.println(this + " placed at location: " + location);
                return true;
            }
            attempts++;
        }
        System.out.println(this + " could not be placed after " + attempts + " attempts");
        return false;
    }
}
