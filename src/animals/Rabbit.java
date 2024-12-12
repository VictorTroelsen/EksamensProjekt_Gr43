package animals;

import actions.RabbitHole;
import biodiversity.Carcass;
import biodiversity.Grass;
import biodiversity.SmallCarcass;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.Program;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Rabbit extends Animal {//implements Actor {

    private RabbitHole homeHole;
    private static final int MAX_ACCEPTABLE_DISTANCE = 10;
    private static Set<Rabbit> rabbitsInBurrow = new HashSet<>();

    public Rabbit(World world, Location initialLocation, Program program) {
        super(world, initialLocation, program);
        this.energy = 100;
        this.age = 0;
        this.isPlaced = placeAnimal(initialLocation);
    }

    private int initialEnergy() {
        return 100 - age;
    }

    @Override
    public int maximumAge() {
        return 25;
    }

    @Override
    public void act(World world) {
        if(world.isNight()) {
            moveToBurrow();
        } else {
            leaveBurrow();

            move();
            age++;
            if (energy <= 50) {
                eatGrass(location);
            }
            updateEnergyRabbit();
            reproduce();

        if (energy <= 0 || age == maximumAge()) {
            dies();
            if (homeHole != null) {
                world.delete(homeHole);
            }
        }



            if (homeHole == null && energy >= 20) {
                digHole();
            }
        }
    }

    @Override
    protected Carcass createCarcass() {
        System.out.println(toString() + " turning into a small carcass.");
        return new SmallCarcass(world, location, program);
    }

    private void updateEnergyRabbit() {
        energy -= 2 * age;
        if (world.getTile(location) instanceof Grass) {
            energy += 20;
            //System.out.println("Dyr.Rabbit ate grass at location: " + location);
        }
        checkAndUpdateLocation();
    }

    private void checkAndUpdateLocation() {
        if(world.getEntities().containsKey(this)) {
            Location entityLocation = world.getEntities().get(this);
            if (entityLocation != null && !entityLocation.equals(this.location)) {
                throw new IllegalStateException("Kaninen prøver at sætte sig på en position, hvor den allerede er.");
            }
        } else {
            if (world.getTile(this.location) == null) {
                world.getEntities().put(this, this.location);
                world.setTile(this.location, this);
            } else {
                throw new IllegalArgumentException("Entity already exists in the world.");
            }
        }
    }

    private void reproduce() {
        if (age > 3 && energy > 60) {
            Location babyLocation = findEmptyAdjacentLocation();
            if (babyLocation != null) {
                Rabbit baby = new Rabbit(world, babyLocation, program);
                //System.out.println(this + " reproduced at location: " + babyLocation);
                energy -= 15;
            }
        }
    }


    private void moveToBurrow() {
        if (homeHole != null) {
            Location burrowLocation = homeHole.getLocation();
            if (!location.equals(burrowLocation)) {
                Set<Location> pathToBurrow = homeHole.getPath(world, location, burrowLocation);
                if(pathToBurrow != null && pathToBurrow.size() <= MAX_ACCEPTABLE_DISTANCE) {
                    try {
                        world.move(this, burrowLocation);
                        location = burrowLocation;

                        rabbitsInBurrow.add(this);
                        world.remove(this);

                        program.setDisplayInformation(Rabbit.class, new DisplayInformation(Color.GRAY,"rabbit-large-sleeping"));


                        //System.out.println("Dyr.Rabbit moved to burrow at location: " + burrowLocation);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Move to burrow failed: " + e.getMessage());
                        sleepOutside();
                    }
                }
            }
        }
    }

    private void sleepOutside() {
        program.setDisplayInformation(Rabbit.class, new DisplayInformation(Color.GRAY,"rabbit-large-sleeping"));
        //System.out.println("Dyr.Rabbit is sleeping outside at location: " + location);
    }

    private void leaveBurrow() {
        if (rabbitsInBurrow.contains(this)) {
            try {
                Location exitLocation = homeHole.getLocation();
                world.setTile(exitLocation, this);   // Placer kaninen tilbage på kortet
                location = exitLocation;

                // Fjern kaninen fra burrow track
                rabbitsInBurrow.remove(this);

                program.setDisplayInformation(Rabbit.class, new DisplayInformation(Color.GRAY,"rabbit-large"));


                //System.out.println("Dyr.Rabbit left burrow and moved to location: " + exitLocation);
            } catch (IllegalArgumentException e) {
                System.out.println("Leaving burrow failed: " + e.getMessage());
            }
        }
    }

    private void digHole() {
        if (world.isDay() && homeHole == null && energy >= 10) {
            Set<Location> surroundingTiles = world.getSurroundingTiles(location);

            for (Location loc : surroundingTiles) {
                if (world.getTile(loc) == null) {
                    homeHole = new RabbitHole(loc, new ArrayList<>());
                    homeHole.addRabbit(this); // Tilføj kaninen til rabbit hole
                    world.setTile(loc, homeHole);

                    // Setup display information for rabbit hole
                    DisplayInformation displayInformation = new DisplayInformation(Color.GRAY, "hole-small");
                    // Assuming there's a method to set display information, you might need to adjust this as per your actual setup.
                    program.setDisplayInformation(RabbitHole.class, displayInformation);

                    energy -= 10;
                    //System.out.println("Dyr.Rabbit dug a new hole at location: " + loc);
                    break;
                }
            }

            if (homeHole == null) {
                //System.out.println("Dyr.Rabbit could not find a nearby location to dig a hole.");
            }
        }
    }

    private void eatGrass(Location location) {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location);

        for (Location loc : surroundingTiles) {
            Object tileContent = world.getNonBlocking(loc);
            if (tileContent instanceof Grass) {
                energy += 30; // Kaninen får energi
                //System.out.println("Dyr.Rabbit ate grass at location: " + location);
                world.delete(tileContent); // Fjern græsset fra verden
                break;
            }
        }
    }
}