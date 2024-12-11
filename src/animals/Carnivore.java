package animals;

import biodiversity.Carcass;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.Set;

abstract class Carnivore extends Animal {

    public Carnivore(World world, Location initiallocation, Program program) {
        super(world, initiallocation, program);
        this.program = program;
    }

    protected boolean canHunt(Object prey) {
        return prey instanceof Animal && !(prey instanceof NonBlocking) && !(prey.getClass().equals(this.getClass()));
    }

    public abstract void act(World world);

    protected void hunt() {
        // Få omkringliggende felter i nærheden af rovdyrets position
        Set<Location> surroundingTiles = world.getSurroundingTiles(location, program.getSize() / 5);
        for (Location loc : surroundingTiles) {
            Object prey = world.getTile(loc); // Få objektet på feltet

            // Tjek om det er bytte, der kan jages
            if (prey instanceof Animal && canHunt(prey)) {
                System.out.println(this + " found prey to hunt at location: " + loc);

                try {
                    // Lad byttet dø først
                    ((Animal) prey).dies();

                    // Tjek og flyt rovdyret til en nærliggende, ledig position
                    Location emptyTile = findEmptyAdjacentLocation();
                    if (emptyTile != null) {
                        world.move(this, emptyTile);
                        location = emptyTile; // Opdater rovdyrets interne position
                    } else {
                        // Hvis der ikke findes et ledigt felt, bliver rovdyret stående
                        System.out.println(this + " could not move after killing prey, no adjacent empty location found.");
                    }

                    // Hent det nyoprettede ådsel fra feltet
                    Object carcass = world.getTile(loc);

                    // Spis ådslet for at øge energi
                    if (carcass instanceof Carcass) {
                        eatCarcass((Carcass) carcass, loc);
                    }

                    break; // Afslut loopet efter vellykket jagt
                } catch (IllegalArgumentException e) {
                    System.err.println("Error occurred while hunting: " + e.getMessage());
                }
            }
        }
    }

    private void eatCarcass(Carcass carcass, Location loc) {
        System.out.println(this + " is eating a carcass at location: " + loc);

        if (carcass instanceof Carcass) {
            energy += carcass.getNourishment(); // Tilføj energi fra ådslet
            world.delete(carcass); // Fjern ådslet fra verdenen
            System.out.println(this + " has eaten the carcass and now has energy: " + energy);
        }
    }
}
