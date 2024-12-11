package biodiversity;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

public class Fungi{
    private Program program;
    private boolean hasFungi;
    private int spreadingRadius;
    protected final Location location;
    protected final World world;


    public Fungi(Program program, Location location, World world) {
        this.program = program;
        this.hasFungi = false;
        this.spreadingRadius = 3;
        this.location = location;
        this.world = world;
        placeFungi(location);
        updateDisplay();
    }

    private void placeFungi(Location location) {
        if (world.getTile(location) == null) {
            world.setTile(location, this);
        }
    }

    private void updateDisplay() {
        DisplayInformation displayInformation = new DisplayInformation(Color.GREEN, "fungi");
        program.setDisplayInformation(Fungi.class, displayInformation);
    }




}
