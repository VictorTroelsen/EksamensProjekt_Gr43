package biodiversity;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;

import java.awt.Color;

public class NormalCarcass extends Carcass {
    private static final int NORMAL_CARCASS_NOURISHMENT = 60;

    public NormalCarcass(World world, Location location, Program program, boolean autoPlace) {
        super(world, location,program, NORMAL_CARCASS_NOURISHMENT, autoPlace);
        updateDisplay();
    }
    @Override
    protected void updateDisplay() {
        DisplayInformation displayInfo = new DisplayInformation(Color.ORANGE, "carcass");
        if (program != null) {
            program.setDisplayInformation(NormalCarcass.class, displayInfo);
        }
    }


}