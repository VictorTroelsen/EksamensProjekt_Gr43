package animals;

import actions.WolfDen;
import biodiversity.Carcass;
import biodiversity.NormalCarcass;
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

        if (this.wolfPack == null) {
            System.out.println("[DEBUG] Wolf #" + this.getId() + " has no pack, trying to find or form one.");

            // Forsøg at finde andre nærliggende ulve
            Set<Location> nearbyLocations = world.getSurroundingTiles(world.getLocation(this), 3);
            Set<Wolf> nearbyWolves = world.getAll(Wolf.class, nearbyLocations);

            // Find alphaulve i nærheden
            Wolf nearbyAlpha = nearbyWolves.stream()
                    .filter(wolf -> wolf.wolfPack != null && wolf.wolfPack.isAlpha(wolf))
                    .findFirst()
                    .orElse(null);

            if (nearbyAlpha != null) {
                // Hvis der findes en eksisterende alpha, tilslut pakkens alpha
                System.out.printf("[DEBUG] Wolf #%d found an alpha (Wolf #%d) and joined their pack.%n", this.getId(), nearbyAlpha.getId());
                nearbyAlpha.wolfPack.addToPack(nearbyAlpha, this);
                this.wolfPack = nearbyAlpha.wolfPack;
            } else {
                // Ingen alpha? Find og opret en pack med nærliggende solo-ulve
                Set<Wolf> nearbySoloWolves = nearbyWolves.stream()
                        .filter(wolf -> wolf.wolfPack == null && wolf != this)
                        .collect(java.util.stream.Collectors.toSet());

                if (!nearbySoloWolves.isEmpty()) {
                    // Skab en fælles pack med de nærliggende solo-ulve
                    this.wolfPack = new WolfPack();
                    this.wolfPack.createPack(this); // 'this' bliver alpha
                    System.out.printf("[DEBUG] Wolf #%d created a new pack and is now the alpha.%n", this.getId());

                    for (Wolf soloWolf : nearbySoloWolves) {
                        this.wolfPack.addToPack(this, soloWolf);
                        soloWolf.wolfPack = this.wolfPack;
                        System.out.printf("[DEBUG] Wolf #%d joined pack led by Alpha Wolf #%d.%n", soloWolf.getId(), this.getId());
                    }
                } else {
                    // Ingen andre ulve eller alphaer i nærheden? Fortsæt solitaire aktiviteter.
                    System.out.printf("[DEBUG] Wolf #%d is still solo and continues hunting.%n", this.getId());
                    move();
                    //moveRandomly();
                    hunt();
                }
            }
        } else if (wolfPack.isAlpha(this)) {
            // Hvis ulven er alpha, udfør alpha-specifik adfærd
            System.out.println("[DEBUG] Wolf #" + this.getId() + " is alpha, acting as alpha.");
            actAsAlpha();
        } else {
            // Hvis ulven er i en pack, men ikke alpha, udfører den jagt
            System.out.printf("[DEBUG] Wolf #%d is part of a pack but not alpha. Hunting solo.%n", this.getId());
            hunt();
        }

        decreaseEnergy(); // Reducer energi ved slutningen af handlingen
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
            return;
        }

        // Hvis ingen WolfDen eksisterer for flokken, opret en ny hule
        if (wolfPack != null && !wolfPack.hasDen()) {
            createDenForPack(); // Opret hulen
        }

        // Flyt flokken til en ny lokation
        Location newLocation = calculateNewLocation();
        System.out.println("[DEBUG] Alpha Wolf #" + id + " attempting to move pack to: " + newLocation);

        wolfPack.movePack(this, newLocation, world);

        // Valider igen: Hvis alpha fejlede at flytte, skal "huntWithPack()" ikke kaldes
        if (!world.contains(this)) {
            System.out.println("[ERROR] Alpha Wolf #" + id + " failed to move. Aborting pack hunt.");
            return;
        }

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
        if (!world.contains(this)) {
            System.out.println("[ERROR] Alpha Wolf #" + id + " is not on the map. Aborting huntWithPack.");
            return;
        }

        Set<Wolf> packMembers = wolfPack.getPackMembers(this);
        for (Wolf wolf : packMembers) {
            if(!world.contains(wolf)) {
                world.remove(wolf);
            }
        }
        //packMembers.removeIf(wolf -> !world.contains(wolf)); // Fjern døde eller utilgængelige ulve

        if (packMembers.isEmpty()) {
            System.out.println("[DEBUG] No pack members left. Alpha Wolf #" + id + " hunting solo.");
            hunt();
            return;
        }

        System.out.println("Alpha Wolf #" + id + " is hunting with " + packMembers.size() + " pack members!");

        Location alphaLocation = world.getLocation(this);
        Set<Location> tiles = world.getSurroundingTiles(alphaLocation, 5);
        Set<Bear> bears = world.getAll(Bear.class, tiles);

        if (!bears.isEmpty() && packMembers.size() >= 3) {
            Bear bear = bears.iterator().next();
            world.remove(bear);
            this.energy += 100;
            System.out.println("Wolf Pack led by #" + id + " hunted a bear!");
        } else {
            System.out.println("Not enough wolves to hunt larger prey. Continuing individual hunts...");
            packMembers.forEach(Wolf::hunt);
        }
    }

    private Location calculateNewLocation() {
        // Simuler en bevægelse i en ny retning
        Location currentLocation = world.getCurrentLocation();
        Set<Location> nearbyEmpty = world.getEmptySurroundingTiles(currentLocation);
        return nearbyEmpty.stream().findFirst().orElse(currentLocation); // Flyt til en tilfældig ledig lokation
    }

    private void decreaseEnergy() {
        this.energy -= 10; // Reducerer energi efter hver handling

        if (this.energy <= 0) {
            System.out.println("Wolf #" + id + " has died due to exhaustion.");
            dies(); // Fjern ulven fra verdenen

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

    @Override
    protected Carcass createCarcass() {
        System.out.println(toString() + " turning into a carcass.");
        return new NormalCarcass(world, location, program,false);
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
        if (!world.contains(this)) {
            System.out.println("[ERROR] Wolf ID #" + id + " is not on the map. Aborting hunt.");
            return;
        }

        System.out.println("Wolf ID #" + id + " is hunting...");

        Location location = world.getCurrentLocation();

        Set<Location> surroundingTiles = world.getSurroundingTiles(location, 3);

        Set<Rabbit> rabbits = world.getAll(Rabbit.class, surroundingTiles);

        Set<Bear> bears = world.getAll(Bear.class, surroundingTiles);

        Set<Wolf> wolves = world.getAll(Wolf.class, surroundingTiles);

        if (!rabbits.isEmpty()) {
            Rabbit rabbit = rabbits.iterator().next();
            world.remove(rabbit);
            this.energy += 50;
            System.out.println("Wolf ID #" + id + " ate a rabbit and gained energy! Current energy: " + energy);
        } else if (!bears.isEmpty() && wolves.size() >= 3) {
            Bear bear = bears.iterator().next();
            world.remove(bear);
            this.energy += 75;
            System.out.println("Wolf ID #" + id + " ate a bear and gained energy! Current energy: " + energy);
        } else {
            move();
            //moveRandomly();
        }
    }

    private void moveRandomly() {
        Set<Location> emptyLocations = world.getEmptySurroundingTiles(world.getCurrentLocation());
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
