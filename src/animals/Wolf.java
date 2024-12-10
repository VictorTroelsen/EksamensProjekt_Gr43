package animals;

import actions.WolfDen;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import actions.WolfPack;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Wolf extends Carnivore{
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();
    private final int id;
    private int age;
    private int energy;
    private final World world;
    private WolfPack wolfPack;


    public Wolf(World world, Location initialLocation, Program program, int age, WolfPack wolfPack) {
        super(world, initialLocation, program);
        this.id = ID_GENERATOR.incrementAndGet();
        this.age = age;
        this.energy = 100;
        this.isPlaced = placeAnimal(initialLocation);
        this.world = world;
        this.wolfPack = wolfPack;

        if (wolfPack == null) {
            System.out.println("Wolf #" + id + " is currently not part of any pack.");
        }
    }


    @Override
    public void act(World world) {
        if (this.energy <= 0 || !world.contains(this)) {
            System.out.println("[DEBUG] Wolf #" + this.getId() + " is dead or not in the world. Skipping turn.");
            return; // Stop alle handlinger for døde ulve.
        }

        System.out.println("[DEBUG] Wolf #" + this.getId() + " starting act. Energy: " + this.getEnergy());

        // Natlige aktiviteter
        if (world.isNight()) {
            System.out.println("[DEBUG] It's night. Wolf #" + this.getId() + " is acting accordingly.");

            if (wolfPack != null && wolfPack.hasDen() && wolfPack.isAlpha(this)) {
                WolfDen den = wolfPack.getDen();

                // Alpha sørger for, at ulvene restituerer tæt på hulen
                den.restNearDen();

                // Alpha forsøger at reproducere i nærheden af hulen
                den.reproduce();
            }
            decreaseEnergy(); // Energi falder lidt om natten
            return; // Spring dagens handlinger over
        }

        // Daglige aktiviteter (tilføjelse til eksisterende logik)
        if (this.wolfPack == null) {
            System.out.println("[DEBUG] Wolf #" + this.getId() + " has no pack, continuing solo activities.");
            hunt(); // Individuel handling
        } else if (wolfPack.isAlpha(this)) {
            System.out.println("[DEBUG] Wolf #" + this.getId() + " is alpha, acting as alpha.");
            actAsAlpha();
        } else {
            hunt(); // Ikke-alpha ulve jager solo
        }

        decreaseEnergy();
        System.out.println("[DEBUG] Wolf #" + this.getId() + " finished act.");
    }

    private Wolf findAlpha() {
        for (Wolf alpha : wolfPack.getAllAlphas()) {
            if (wolfPack.getPackMembers(alpha).contains(this) || alpha == this) {
                return alpha; // Returner flokkens alpha
            }
        }
        return null; // Ikke i nogen flok
    }

    private void actAsAlpha() {
        // Første tjek: Er alpha stadig i verdenen?
        if (!world.contains(this)) {
            System.out.println("[DEBUG] Alpha Wolf #" + id + " is no longer in the world. Skipping movePack.");
            return; // Afbryd handlingen, hvis ulven ikke længere er i verdenen
        }

        // Hvis ingen WolfDen eksisterer for flokken, opret en ny hule på den nuværende placering
        if (wolfPack != null && !wolfPack.hasDen()) {
            createDenForPack(); // Opret hulen
        }

        // Flyt flokken til en ny lokation
        Location newLocation = calculateNewLocation();
        wolfPack.movePack(this, newLocation, world);

        // Flokken udfører en fælles jagt
        huntWithPack();
    }

    // Metode til at oprette en hule til flokken
    private void createDenForPack() {
        Location currentLocation = world.getLocation(this); // Alpha'ens nuværende placering
        if (currentLocation != null) {
            // Opret hulen
            WolfDen den = new WolfDen(world, currentLocation, wolfPack, program);
            wolfPack.setDen(den); // Tilføj hulen til flokken
            world.setTile(currentLocation, den); // Tilføj hulen til verdenen

            System.out.println("[DEBUG] Wolf #" + id + " has dug a den for the pack at location: " + currentLocation);
        } else {
            System.out.println("[DEBUG] Wolf #" + id + " could not create a den because no location was found.");
        }
    }

    private void huntWithPack() {
        Set<Wolf> packMembers = wolfPack.getPackMembers(this);

        // Jagt sammen med flokken: prioritér større bytte som bjørne
        if (!packMembers.isEmpty()) {
            System.out.println("Alpha Wolf #" + id + " is hunting with " + packMembers.size() + " pack members!");

            Location currentLocation = world.getLocation(this);
            Set<Location> tiles = world.getSurroundingTiles(currentLocation, 5); // Udvid radius
            Set<Bear> bears = world.getAll(Bear.class, tiles);

            if (!bears.isEmpty() && packMembers.size() >= 3) { // Minimum 3 ulve til flokken for at angribe bjørn
                Bear bear = bears.iterator().next(); // Første bjørn
                world.remove(bear); // Fjern bjørnen fra verden
                this.energy += 100; // Alpha får mere energi
                System.out.println("Wolf Pack led by #" + id + " hunted a bear!");
            } else {
                System.out.println("Not enough wolves to hunt larger prey. Continuing individual hunts...");
                hunt(); // Fallback til individuel jagt
            }
        } else {
            hunt(); // Ingen pack = individuel jagt
        }
    }

    private Location calculateNewLocation() {
        // Simuler en bevægelse i en ny retning
        Location currentLocation = world.getLocation(this);
        Set<Location> nearbyEmpty = world.getEmptySurroundingTiles(currentLocation);
        return nearbyEmpty.stream().findFirst().orElse(currentLocation); // Flyt til en tilfældig ledig lokation
    }

    private void decreaseEnergy() {
        this.energy -= 10; // Reducerer energi efter hver handling

        if (this.energy <= 0) {
            System.out.println("Wolf #" + id + " has died due to exhaustion.");
            world.remove(this); // Fjern ulven fra verdenen

            if (wolfPack != null && wolfPack.isAlpha(this)) { // Kontroller, om wolfPack ikke er null
                Set<Wolf> packMembers = wolfPack.getPackMembers(this);

                if (!packMembers.isEmpty()) {
                    Wolf newAlpha = packMembers.iterator().next(); // Vælg ny alpha
                    wolfPack.createPack(newAlpha); // Opret ny pack med ny alpha
                    packMembers.remove(newAlpha); // Fjern den nye alpha fra medlemstlisten

                    for (Wolf member : packMembers) {
                        wolfPack.addToPack(newAlpha, member); // Tilføj de resterende medlemmer til den nye alpha
                    }

                    System.out.println("Wolf #" + newAlpha.getId() + " is the new alpha!");
                } else {
                    wolfPack.disbandPack(this); // Opløs flokken, hvis ingen medlemmer er tilbage
                }
            } else {
                System.out.println("Wolf #" + id + " has no wolfPack or is not the alpha.");
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    protected void hunt() {
        System.out.println("Wolf ID #" + id + " is hunting...");

        Location location = world.getLocation(this);

        Set<Location> surroundingTiles = world.getSurroundingTiles(location, 3);

        Set<Rabbit> rabbits = world.getAll(Rabbit.class, surroundingTiles);

        Set<Bear> bears = world.getAll(Bear.class, surroundingTiles);

        Set<Wolf> wolves = world.getAll(Wolf.class, surroundingTiles);

        if(!rabbits.isEmpty()) {
            Rabbit rabbit = rabbits.iterator().next();
            Location rabbitLocation = world.getLocation(rabbit);
            world.remove(rabbit);
            this.energy += 50;
            System.out.println("Wolf ID #" + id + " ate a rabbit and gained energy! Current energy: " + energy);
        }

        else if (!bears.isEmpty() && wolves.size() >= 3) {
            Bear bear = bears.iterator().next();
            Location bearLocation = world.getLocation(bear);
            world.remove(bear);
            this.energy += 75;
            System.out.println("Wolf ID #" + id + " ate a bear and gained energy! Current energy: " + energy);
        }

        else {
            moveRandomly();
        }

    }

    private void moveRandomly() {
        Set<Location> emptyLocations = world.getEmptySurroundingTiles(world.getLocation(this));
        if (!emptyLocations.isEmpty()) {
            // Flyt til en tilfældig lokation
            Location newLocation = emptyLocations.iterator().next(); // Tag en tilfældig ledig position
            world.move(this, newLocation);
            System.out.println("Wolf ID #" + id + " moved to a new location: " + newLocation);
        } else {
            System.out.println("Wolf ID #" + id + " cannot move because there are no empty tiles nearby.");
        }
    }





    @Override
    public String toString() {
        return "Wolf{id=" + id + ", age=" + age + ", energy=" + energy + "}";
    }
}
