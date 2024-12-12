package biodiversity;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

public abstract class Carcass implements Actor {
    protected final Location location;
    protected final World world;
    Program program;
    public boolean isDecomposed;
    protected int decompositionTime;
    private static final int MAX_DECOMPOSITION_TIME = 10;
    private final int nourishment;

    public Carcass(World world, Location location, Program program, int nourishment, boolean autoPlace) {
        this.world = world;
        this.location = location;
        this.decompositionTime = MAX_DECOMPOSITION_TIME;
        this.isDecomposed = false;
        this.program = program;
        this.nourishment = nourishment;
        if (autoPlace) {
            placeCarcass(location);
        }
        updateDisplay();
    }

    private void placeCarcass(Location location) {
        // Placering af ådsel i spillverden
        if (world.getTile(location) == null) {
            world.setTile(location, this);
        }
    }
    @Override
    public void act(World world) {
        if (!isDecomposed) {
            decompositionTime--;
            if (decompositionTime <= 0) {
                decompose();
            }
        }
    }

    private void decompose() {
        isDecomposed = true;
        world.delete(this); // Fjern ådselet fra verden
    }

    public int getNourishment() {
        System.out.println("Carcass nourishment value: " + nourishment);
        return nourishment;
    }

    // Skal implementeres af underklasser for unikt display
    protected abstract void updateDisplay();
}