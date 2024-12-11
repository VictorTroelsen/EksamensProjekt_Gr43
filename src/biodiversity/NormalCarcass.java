package biodiversity;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;

import java.awt.Color;

public class NormalCarcass extends Carcass {
    private final Program program;

    public NormalCarcass(World world, Location location, Program program) {
        super(world, location);
        this.program = program;
        updateDisplay();
    }

    private void updateDisplay() {
        DisplayInformation displayInfo = new DisplayInformation(Color.ORANGE, "carcass");
        program.setDisplayInformation(NormalCarcass.class, displayInfo);
    }
}