package animals;

import biodiversity.Bush;
import biodiversity.Carcass;
import biodiversity.NormalCarcass;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Bear extends Carnivore {
    private final Location territoryCenter;
    private static final int TERRITORY_RADIUS = 5; // Radius for bjørnens territorium

    public Bear(World world, Location initialLocation, Program program) {
        super(world, initialLocation, program);
        this.energy = 200;
        this.age = 0;
        this.isPlaced = placeAnimal(initialLocation);
        this.territoryCenter = initialLocation;
        System.out.println("Bear's territory center is at: " + territoryCenter);
    }

    @Override
    public void act(World world) {
        // Bjørnen bevæger sig og mister energi
        move();
        energy -= 5; // Hver bevægelse koster energi

        // Bjørnen jager, hvis energien er lav
        if (energy >= 30 && energy <= 100) {
            forageBerries();
            age++;
        } else if (energy <= 30) {
            hunt(30);
            age++;
        }
        // Tjek om bjørnen skal dø
        if (energy <= 0) {
            dies();
        }
    }

    public void move() {
        if (!world.isOnTile(this)) {
            System.out.println("Bear is not on any tile.");
            return;
        }

        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        List<Location> validLocations = new ArrayList<>();

        for (Location loc : surroundingTiles) {
            if (isTileEmptyOrNonBlocking(loc) && isWithinTerritory(loc)) {
                validLocations.add(loc);
            }
        }

        if (!validLocations.isEmpty()) {
            Random random = new Random();
            Location newLocation = validLocations.get(random.nextInt(validLocations.size()));

            try {
                world.move(this, newLocation);
                location = newLocation;
                System.out.println("Bear moved to location: " + location);
            } catch (IllegalArgumentException e) {
                System.out.println("Move blocked: " + e.getMessage());
            }
        } else {
            System.out.println("Bear could not find a valid move.");
        }
    }

    @Override
    protected boolean canHunt(Object prey) {
        if (prey instanceof Wolf wolf) {
            return age > 10 && (wolf.wolfPack == null || wolf.wolfPack.getPackMembers(wolf).size() <=2);
        } else return prey instanceof Rabbit;
    }


    @Override
    protected Carcass createCarcass() {
        System.out.println(toString() + " turning into a large carcass.");
        return new NormalCarcass(world, location, program,false); // Eller en ny specific Carcass-type
    }

    private boolean isWithinTerritory(Location location) {
        int dx = location.getX() - territoryCenter.getX();
        int dy = location.getY() - territoryCenter.getY();
        return dx * dx + dy * dy <= TERRITORY_RADIUS * TERRITORY_RADIUS; // Pythagoras' sætning
    }

    /*@Override
    protected void hunt() {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location, TERRITORY_RADIUS);
        for (Location loc : surroundingTiles) {
            Object prey = world.getTile(loc);
            if (prey instanceof Rabbit) {
                System.out.println(this + " found a Rabbit at location: " + loc);
                try {
                    world.delete(prey); // Fjern kaninen fra verdenen
                    world.move(this, loc); // Flyt bjørnen til kaninens placering
                    location = loc;

                    energy += 50; // Kaninen giver energi
                    System.out.println(this + " ate a Rabbit and gained energy.");
                    return; // Stop jagten efter at have fundet bytte
                } catch (IllegalArgumentException e) {
                    System.err.println("Error while hunting: " + e.getMessage());
                }
            }
        }
        System.out.println("No prey found near location: " + location);
        //forageBerries(); // Hvis ingen kaniner findes, så fourager bær
    }
    */




    private void forageBerries() {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);
        for (Location loc : surroundingTiles) {
            Object tileContent = world.getTile(loc);
            if (tileContent instanceof Bush) {
                Bush bush = (Bush) tileContent;
                int berriesPicked = bush.pickBerries(3); // Bjørnen plukker fx 3 bær
                energy += berriesPicked * 5; // Bær giver mindre energi end kød

                // Fjern busken og opdater dens tilstand
                world.delete(bush);
                bush.markAsEaten();

                System.out.println("Bear foraged " + berriesPicked + " berries at location: " + loc);
                return;
            }
        }
        System.out.println("No berries found near location: " + location);
    }

    @Override
    public void dies() {
        System.out.println("Bear died at location: " + location);
        world.delete(this);
    }
}