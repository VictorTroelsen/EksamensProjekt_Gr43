package biodiversity;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public abstract class Carcass {
    protected final Location location;
    protected final World world;
    protected final Program program;
    protected boolean isDecomposed;
    protected int decompositionTime;
    private static final int MAX_DECOMPOSITION_TIME = 5;

    public Carcass(World world, Location location, Program program) {
        this.world = world;
        this.location = location;
        this.decompositionTime = MAX_DECOMPOSITION_TIME;
        this.isDecomposed = false;
        this.program = program;

        placeCarcass(location);
        updateDisplay();
    }

    private void placeCarcass(Location location) {
        // Placering af ådsel i spillverden
        if (world.getTile(location) == null) {
            world.setTile(location, this);
        }
    }

    public void act() {
        if (!isDecomposed) {
            decompositionTime--;
            if (decompositionTime <= 0) {
                decompose();
            }
        }
    }

    private void decompose() {
        isDecomposed = true;
        world.setTile(location, null); // Fjern ådselet fra verden
    }

    // Skal implementeres af underklasser for unikt display
    protected abstract void updateDisplay();
}