package actions;

import animals.Wolf;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WolfPack {
    private final Map<Wolf, Set<Wolf>> packs;
    private WolfDen den;

    public WolfPack() {
        packs = new HashMap<>();
    }

    public void createPack(Wolf alpha) {
        if (!packs.containsKey(alpha)) {
            packs.put(alpha, new HashSet<>());
        }
    }

    public void addToPack(Wolf alpha, Wolf member) {
        if (packs.containsKey(alpha)) {
            packs.get(alpha).add(member);
        } else {
            throw new IllegalArgumentException("Alpha must be in pack before adding member.");
        }
    }

    public void removeFromPack(Wolf alpha, Wolf member) {
        if (packs.containsKey(alpha)) {
            packs.get(alpha).remove(member);
        } else {
            throw new IllegalArgumentException("Alpha must be in pack before removing member.");
        }
    }

    public void disbandPack(Wolf alpha) {
        packs.remove(alpha);
    }

    // Tjek, om flokken allerede har en hule
    public boolean hasDen() {
        return den != null;
    }

    // Returner hulen
    public WolfDen getDen() {
        return den;
    }

    // Sæt hulen til flokken
    public void setDen(WolfDen den) {
        this.den = den;
    }

    public Set<Wolf> getPackMembers(Wolf alpha) {
        return packs.getOrDefault(alpha, Set.of());
    }

    public void movePack(Wolf alpha, Location newLocation, World world) {
        if (alpha == null || newLocation == null || world == null) {
            throw new IllegalArgumentException("Alpha, location, or world cannot be null");
        }

        // Check if alpha exists in the world
        if (!world.contains(alpha)) {
            System.out.println("[ERROR] Alpha Wolf #" + alpha.getId() + " not found on the map.");
            return;
        }

        // Check if the new location is valid
        if (!world.isTileEmpty(newLocation)) {
            System.out.println("[DEBUG] Cannot move pack led by Alpha Wolf #" + alpha.getId() + " to " + newLocation
                    + ": Target tile is occupied.");
            return;
        }

        // Flyt alpha-ulven
        try {
            world.move(alpha, newLocation);
            System.out.println("[DEBUG] Moved Alpha Wolf #" + alpha.getId() + " to " + newLocation);
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] Failed to move Alpha Wolf #" + alpha.getId() + ": " + e.getMessage());
            return; // Stop handlingen her, da alpha ikke kan flyttes
        }

        // Flyt de øvrige pakkemedlemmer
        Set<Wolf> packMembers = getPackMembers(alpha);
        packMembers.removeIf(wolf -> !world.contains(wolf)); // Fjern ulve, der ikke findes i kortet

        for (Wolf wolf : packMembers) {
            if (!world.contains(wolf)) {
                continue; // Spring ulve uden gyldig status over
            }

            Location currentLocation = world.getLocation(wolf);
            Location followLocation = calculateFollowLocation(currentLocation, newLocation, world);

            if (followLocation != null && world.isTileEmpty(followLocation)) {
                try {
                    world.move(wolf, followLocation);
                    System.out.println("[DEBUG] Moved Pack Member Wolf #" + wolf.getId() + " to " + followLocation);
                } catch (IllegalArgumentException e) {
                    System.out.println("[ERROR] Failed to move Pack Member Wolf #" + wolf.getId() + ": " + e.getMessage());
                }
            } else {
                System.out.println("[DEBUG] No valid location found for Wolf #" + wolf.getId());
            }
        }

        // Hvis alle pack-medlemmer fejlede, markér flokken som opløst
        if (packMembers.isEmpty()) {
            System.out.println("[DEBUG] All pack members are unavailable, disbanding the pack.");
            disbandPack(alpha);
        }
    }

    public boolean isAlpha(Wolf wolf) {
        return packs.containsKey(wolf);
    }

    public Set<Wolf> getAllAlphas() {
        return packs.keySet(); // Returnér alle alpha-ulve som et sæt
    }


    // (Privat) Beregn en lokation tættere på alpha
    private Location calculateFollowLocation(Location member, Location alpha, World world) {
        int xDiff = alpha.getX() - member.getX();
        int yDiff = alpha.getY() - member.getY();

        // Bevægelse mod alpha (x og y retning)
        int xStep = Integer.compare(xDiff, 0);
        int yStep = Integer.compare(yDiff, 0);

        Location newLocation = new Location(member.getX() + xStep, member.getY() + yStep);

        // Tjek først den beregnede position
        if (world.isTileEmpty(newLocation)) {
            return newLocation;
        }

        // Hvis første placering ikke er ledig, prøv nærliggende tiles
        Set<Location> surrounding = world.getSurroundingTiles(alpha, 1); // Få nabotiles
        for (Location loc : surrounding) {
            if (world.isTileEmpty(loc)) {
                return loc;
            }
        }

        return null; // Send null, hvis ingen gyldige placeringer findes
    }

    public boolean isPartOfPack(Wolf wolf) {
        for (Set<Wolf> packMembers : packs.values()) {
            if (packMembers.contains(wolf)) {
                return true; // Ulven findes i mindst én af flokkene
            }
        }
        return false; // Ulven er ikke en del af nogen flok
    }


}
