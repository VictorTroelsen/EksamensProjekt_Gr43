package actions;

import animals.Wolf;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.Program;

import java.awt.*;
import java.util.Set;

public class WolfDen implements NonBlocking {
    private final Location denLocation;
    private final World world;
    private final WolfPack wolfPack;
    Program program;


    public WolfDen(World world, Location location, WolfPack pack, Program program) {
        if (world == null || location == null || pack == null) {
            throw new IllegalArgumentException("world, location and pack must not be null");
        }
        this.world = world;
        this.denLocation = location;
        this.wolfPack = pack;
        this.program = program;

        DisplayInformation displayInformation = new DisplayInformation(Color.DARK_GRAY, "hole");
        program.setDisplayInformation(WolfDen.class, displayInformation);
    }

    public void restNearDen() {
        Set<Location> nearbyLocations = world.getSurroundingTiles(denLocation, 3); //Område tæt på hulen
        Set<Wolf> nearbyWolves = world.getAll(Wolf.class, nearbyLocations);

        for (Wolf wolf : nearbyWolves) {
            if (wolfPack.isPartOfPack(wolf)) {
                wolf.setEnergy(Math.min(wolf.getEnergy() + 20, 100));
                System.out.println("Wolf #" + wolf.getId() + " rested near the den and regained energy!");
            }
        }
    }


    // Reproduktion i hulen
    public void reproduce(int energyRequirement, int maxPackSize, int minAgeForReproduction) {
        // Find ulve tæt på hulen
        Set<Location> nearbyLocations = world.getSurroundingTiles(denLocation, 3);
        Set<Wolf> nearbyWolves = world.getAll(Wolf.class, nearbyLocations);

        // Tæl alpha-ulve og andre ulve
        Wolf alpha = null;
        Wolf mate = null;

        for (Wolf wolf : nearbyWolves) {
            // Alpha-ulven findes først
            if (wolfPack.isAlpha(wolf) && wolf.getEnergy() >= energyRequirement) {
                alpha = wolf;
            }
            // Find en "partner", hvis partneren opfylder energikravet og alderskravet
            else if (wolfPack.isPartOfPack(wolf)
                    && wolf.getEnergy() >= energyRequirement
                    && wolf.getAge() >= minAgeForReproduction
                    && mate == null) {
                mate = wolf; // Find den første mulige "mate" ulv
            }
        }

        // Ekstra kontrol: Maksimum flokstørrelse
        if (wolfPack != null && wolfPack.packSize() >= maxPackSize) {
            System.out.println("Reproduction cannot occur. Pack size has reached the maximum limit (" + maxPackSize + ").");
            return;
        }

        // Reproduktion: Skal ske mellem alpha og en anden ulv
        if (alpha != null && mate != null) {
            // Generer en ny ulv med forældre i flokken
            Location emptyTile = findEmptyTileNearDen();
            if (emptyTile != null) {
                Wolf newWolf = new Wolf(world, emptyTile, null, 0, wolfPack); // Nyfødt ulv med alder 0
                wolfPack.addToPack(alpha, newWolf); // Tilføj den nye ulv til alpha's flok
                System.out.println("A new wolf (ID #" + newWolf.getId() + ") was born in the pack led by Alpha Wolf #" + alpha.getId());

                // Reducér energi for alpha og partner som "pris" for reproduktion
                alpha.setEnergy(alpha.getEnergy() - 50);
                mate.setEnergy(mate.getEnergy() - 50);
            } else {
                System.out.println("No empty tile was found near the den to place the new wolf.");
            }
        } else {
            System.out.println("Reproduction cannot occur. Conditions are not met near the den (energy, age, or partner missing).");
        }
    }

    // Find en tom placering tæt på hulen
    private Location findEmptyTileNearDen() {
        Set<Location> nearbyEmpty = world.getEmptySurroundingTiles(denLocation);
        return nearbyEmpty.isEmpty() ? null : nearbyEmpty.iterator().next(); // Returnér en tom lokation, hvis det er muligt
    }

    // Getters
    public Location getDenLocation() {
        return denLocation;
    }

    public WolfPack getWolfPack() {
        return wolfPack;
    }

}
