package biodiversity;

import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;
import itumulator.executable.DisplayInformation;
import java.awt.*;

public class SmallCarcass extends Carcass {
    private static final int SMALL_CARCASS_NOURISHMENT = 10;


    public SmallCarcass(World world, Location location, Program program) {
        super(world, location,program, SMALL_CARCASS_NOURISHMENT, false);
        updateDisplay();
    }
    @Override
    protected void updateDisplay() {
        DisplayInformation displayInfo = new DisplayInformation(Color.ORANGE, "carcass-small");
        program.setDisplayInformation(SmallCarcass.class, displayInfo);
    }
}
