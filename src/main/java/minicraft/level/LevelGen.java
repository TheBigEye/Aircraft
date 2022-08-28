package minicraft.level;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.level.tile.Tiles;

public class LevelGen {

    private static long worldSeed = 0; // Always is 0
    private static final Random random = new Random(worldSeed); // Initializes the random class
    private double[] values; // An array of doubles, used to help making noise for the map

    private static int tundra_desert_separator = 6;
    private static int beaches_thickness = 1;

    // width and height of the map
    private final int w, h;

    private static final int stairRadius = 15;

    /**
     * This creates noise to create random values for level generation
     */
    private LevelGen(int w, int h, int featureSize) {
        this.w = w; // assigns the width of the map
        this.h = h; // assigns the height of the map

        values = new double[w * h]; // Creates the size of the value array (width * height)

        /// to be 16 or 32, in the code below.
        for (int y = 0; y < w; y += featureSize) {
            for (int x = 0; x < w; x += featureSize) {

                // This method sets the random value from -1 to 1 at the given coordinate.
                setSample(x, y, random.nextFloat() * 2 - 1);
            }
        }

        int stepSize = featureSize;
        double scale = 1.4 / w;
        double scaleMod = 1.0;

        do {
            int halfStep = stepSize / 2;
            for (int y = 0; y < h; y += stepSize) {
                for (int x = 0; x < w; x += stepSize) { // this loops through the values again, by a given increment...

                    double a = sample(x, y); // fetches the value at the coordinate set previously (it fetches the exact same ones that were just set above)
                    double b = sample(x + stepSize, y); // fetches the value at the next coordinate over. This could possibly loop over at the end, and fetch the first value in the row instead.
                    double c = sample(x, y + stepSize); // fetches the next value down, possibly looping back to the top of the column.
                    double d = sample(x + stepSize, y + stepSize); // fetches the value one down, one right.

                    /*
                    * This could probably use some explaining... Note: the number values are
                    * probably only good the first time around...
                    *
                    * This starts with taking the average of the four numbers from before (they
                    * form a little square in adjacent tiles), each of which holds a value from -1
                    * to 1. Then, it basically adds a 5th number, generated the same way as before.
                    * However, this 5th number is multiplied by a few things first... ...by
                    * stepSize, aka featureSize, and scale, which is 2/size the first time.
                    * featureSize is 16 or 32, which is a multiple of the common level size, 128.
                    * Precisely, it is 128 / 8, or 128 / 4, respectively with 16 and 32. So, the
                    * equation becomes size / const * 2 / size, or, simplified, 2 / const. For a
                    * feature size of 32, stepSize * scale = 2 / 4 = 1/2. featureSize of 16, it's 2
                    * / 8 = 1/4. Later on, this gets closer to 4 / 4 = 1, so... the 5th value may
                    * not change much at all in later iterations for a feature size of 32, which
                    * means it has an effect of 1, which is actually quite significant to the value
                    * that is set. So, it tends to decrease the 5th -1 or 1 number, sometimes
                    * making it of equal value to the other 4 numbers, sort of. It will usually
                    * change the end result by 0.5 to 0.25, perhaps; at max.
                     */
                    double e = (a + b + c + d) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale;

                    /*
                    * This sets the value that is right in the middle of the other 4 to an average
                    * of the four, plus a 5th number, which makes it slightly off, differing by
                    * about 0.25 or so on average, the first time around.
                     */
                    setSample(x + halfStep, y + halfStep, e);
                }
            }

            // This loop does the same as before, but it takes into account some of the half Steps we set in the last loop.
            for (int y = 0; y < h; y += stepSize) {
                for (int x = 0; x < w; x += stepSize) {

                    double a = sample(x, y); // middle (current) tile
                    double b = sample(x + stepSize, y); // right tile
                    double c = sample(x, y + stepSize); // bottom tile
                    double d = sample(x + halfStep, y + halfStep); // mid-right, mid-bottom tile
                    double e = sample(x + halfStep, y - halfStep); // mid-right, mid-top tile
                    double f = sample(x - halfStep, y + halfStep); // mid-left, mid-bottom tile

                    // The 0.5 at the end is because we are going by half-steps..?
                    // The H is for the right and surrounding mids, and g is the bottom and
                    // surrounding mids.
                    double H = (a + b + d + e) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5; // adds middle, right, mr-mb, mr-mt, and random.
                    double g = (a + c + d + f) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5; // adds middle, bottom, mr-mb, ml-mb, and random.

                    setSample(x + halfStep, y, H); // Sets the H to the mid-right
                    setSample(x, y + halfStep, g); // Sets the g to the mid-bottom
                }
            }

            /**
             * THEN... this stuff is set to repeat the system all over again! The featureSize is halved, allowing access to further unset
             * mids, and the scale changes... The scale increases the first time, x1.8, but the second time it's x1.1, and after that
             * probably a little less than 1. So, it generally increases a bit, maybe to 4 / w at tops. This results in the 5th random value
             * being more significant than the first 4 ones in later iterations.
             */
            stepSize /= 2;
            scale *= (scaleMod + 0.8);
            scaleMod *= 0.4;

        } while (stepSize > 1); // This stops when the stepsize is < 1, aka 0 b/c it's an int. At this point there are no more mid values.
    }

    private double sample(int x, int y) {
        return values[(x & (w - 1)) + (y & (h - 1)) * w];
    } // This merely returns the value, like Level.getTile(x, y).

    private void setSample(int x, int y, double value) {
        /**
         * This method is short, but difficult to understand. This is what I think it does: The values array is like a 2D array, but
         * formatted into a 1D array; so the basic "x + y * w" is used to access a given value. The value parameter is a random number,
         * above set to be a random decimal from -1 to 1. From above, we can see that the x and y values passed in range from 0 to the
         * width/height, and increment by a certain constant known as the "featureSize". This implies that the locations chosen from this
         * array, to put the random value in, somehow determine the size of biomes, perhaps. The x/y value is taken and AND'ed with the
         * size-1, which could be 127. This just caps the value at 127; however, it shouldn't be higher in the first place, so it is merely
         * a safety measure.
         *
         * In other words, this is just "values[x + y * w] = value;"
         */
        values[(x & (w - 1)) + (y & (h - 1)) * w] = value;
    }

    @Nullable
	static short[][] createAndValidateMap(int w, int h, int level, long seed) {
		worldSeed = seed;

        Logger.debug("Checking level index for {} ", level);

        if (level == 1) return createAndValidateSkyMap(w, h);
        if (level == 0) return createAndValidateTopMap(w, h);
        if (level == -4) return createAndValidateDungeon(w, h);
        if ((level > -4) && (level < 0)) return createAndValidateUndergroundMap(w, h, -level);
        if (level == 2) return createAndValidateVoidMap(w, h);  // World.java is 2 as here

        System.err.println("LevelGen ERROR: level index is not valid. Could not generate a level.");
        return null;
    }

    private static short[][] createAndValidateTopMap(int w, int h) {
        random.setSeed(worldSeed);

        Logger.debug("Generating surface level, {}x{}...", w, h);

        do {
            short[][] result = createTopMap(w, h);

            int[] count = new int[256];

            for (int i = 0; i < w * h; i++) {
                count[result[0][i] & 0xffff]++;
            }

            if (count[Tiles.get("rock").id & 0xffff] < 100) continue;
            if (count[Tiles.get("sand").id & 0xffff] < 100) continue;
            if (count[Tiles.get("grass").id & 0xffff] < 100) continue;
            if (count[Tiles.get("Oak tree").id & 0xffff] < 100) continue;
            if (count[Tiles.get("flower").id & 0xffff] < 100) continue;
            if (count[Tiles.get("Stairs Down").id & 0xffff] == 0) continue; // size 128 = 6 stairs min

            return result;
        } while (true);
    }

    private static @Nullable
    short[][] createAndValidateUndergroundMap(int w, int h, int depth) {
        random.setSeed(worldSeed);

        Logger.debug("Genereting underground level, {}x{}...", w, h);

        do {
            short[][] result = createUndergroundMap(w, h, depth);

            int[] count = new int[256];

            for (int i = 0; i < w * h; i++) {
                count[result[0][i] & 0xffff]++;
            }

            if (count[Tiles.get("rock").id & 0xffff] < 100) continue;
            if (count[Tiles.get("dirt").id & 0xffff] < 100) continue;
            if (count[(Tiles.get("iron Ore").id & 0xffff) + depth - 1] < 20) continue;
            if (depth < 3 && count[Tiles.get("Stairs Down").id & 0xffff] < w / 32) continue; // size 128 = 4 stairs min
            return result;

        } while (true);
    }

    private static short[][] createAndValidateDungeon(int w, int h) {
        random.setSeed(worldSeed);

        Logger.debug("Genereting dungeon level, {}x{}...", w, h);

        do {
            short[][] result = createDungeon(w, h);

            int[] count = new int[256];

            for (int i = 0; i < w * h; i++) {
                count[result[0][i] & 0xffff]++;
            }

            if (count[Tiles.get("Obsidian").id & 0xffff] < 100) continue;
            if (count[Tiles.get("Obsidian Wall").id & 0xffff] < 100) continue;
            if (count[Tiles.get("Raw obsidian").id & 0xffff] < 100) continue;

            return result;

        } while (true);
    }

    private static @Nullable
    short[][] createAndValidateSkyMap(int w, int h) {
        random.setSeed(worldSeed);

        Logger.debug("Genereting sky level, {}x{}...", w, h);

        do {
            short[][] result = createSkyMap(w, h);

            int[] count = new int[256];

            for (int i = 0; i < w * h; i++) {
                count[result[0][i] & 0xffff]++;
            }

            if (count[Tiles.get("cloud").id & 0xffff] < 2000) continue;
            if (count[Tiles.get("Stairs Down").id & 0xffff] < w / 64) continue; // size 128 = 2 stairs min
            
            return result;

        } while (true);
    }

    public static @Nullable
    short[][] createAndValidateVoidMap(int w, int h) {
        random.setSeed(worldSeed);

        Logger.debug("Genereting void level, {}x{}...", w, h);

        do {
            short[][] result = createVoidMap(w, h);

            int[] count = new int[256];

            for (int i = 0; i < w * h; i++) {
                count[result[0][i] & 0xffff]++;
            }
            if (count[Tiles.get("rock").id & 0xffff] < 100) {
                continue;
            }
            if (count[Tiles.get("grass").id & 0xffff] < 100) {
                continue;
            }
            if (count[Tiles.get("Oak tree").id & 0xffff] < 100) {
                continue;
            }

            return result;

        } while (true);
    }

    // Surface generation code
    private static short[][] createTopMap(int w, int h) { // create surface map

        // creates a bunch of value maps, some with small size...
        LevelGen mnoise1 = new LevelGen(w, h, 16);
        LevelGen mnoise2 = new LevelGen(w, h, 16);
        LevelGen mnoise3 = new LevelGen(w, h, 16);

        // ...and some with larger size..
        LevelGen noise1 = new LevelGen(w, h, 32);
        LevelGen noise2 = new LevelGen(w, h, 32);

        LevelGen jnoise1 = new LevelGen(w, h, 8);
        LevelGen jnoise2 = new LevelGen(w, h, 4);
        // LevelGen jnoise3 = new LevelGen(w, h, 8);

        short[] map = new short[w * h];
        short[] data = new short[w * h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = x + y * w;

                double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;
                double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
                mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

                double jval = Math.abs(jnoise1.values[i] - jnoise2.values[i]) * 3 - 2;

                // this calculates a sort of distance based on the current coordinate.
                double xd = x / (w - 1.0) * 2 - 1;
                double yd = y / (h - 1.0) * 2 - 1;

                if (xd < 0) {
                    xd = -xd;
                }
                if (yd < 0) {
                    yd = -yd;
                }

                double dist = xd >= yd ? xd : yd;
                dist = dist * dist * dist * dist;
                dist = dist * dist * dist * dist;
                val += 1 - dist * 20;

                // Code of the type of terrain, this according to the user's option
                switch ((String) Settings.get("Type")) {
                    case "Island":

                        if (val < -0.5) {
                            if (Settings.get("Theme").equals("Hell")) {
                                map[i] = Tiles.get("lava").id;
                            } else {
                                map[i] = Tiles.get("water").id;
                            }

                        } else if (val > 0.5 && mval < -1.5) {

                            map[i] = Tiles.get("up rock").id;

                        } else if (val > 0.1 && mval < -1.1) {

                            map[i] = Tiles.get("rock").id;

                        } else if (val > 0.1 && mval < -0.3 && jval < -0.4) { // 0.4 offsset, 0.3 sspesor

                            // If there is sand nearby, the generation is cut
                            if (x > 4 && y > 4 && x < w - 2 && y < h - 2) {
                                if (map[i] == Tiles.get("sand").id) {
                                    map[i] = Tiles.get("grass").id;
                                } else {
                                    map[i] = Tiles.get("grass").id;
                                }
                            } else {
                                if (x < 4 && y < 4 && x > w - 2 && y > h - 2) {
                                    map[i] = Tiles.get("snow").id;
                                }
                            }

                        } else {
                            map[i] = Tiles.get("grass").id;

                        }

                        break;
                    case "Box":

                        if (val < -1.5) {
                            if (Settings.get("Theme").equals("Hell")) {
                                map[i] = Tiles.get("lava").id;
                            } else {
                                map[i] = Tiles.get("water").id;
                            }

                        } else if (val > 0.5 && mval < -1.5) {

                            map[i] = Tiles.get("up rock").id;

                        } else if (val > 0.1 && mval < -1.1) {

                            map[i] = Tiles.get("rock").id;

                        } else {
                            map[i] = Tiles.get("grass").id;

                        }

                        break;
                    case "Mountain":

                        if (val < -0.4) {
                            map[i] = Tiles.get("grass").id;
                        } else if (val > 0.5 && mval < -1.5) {
                            if (Settings.get("Theme").equals("Hell")) {
                                map[i] = Tiles.get("lava").id;
                            } else {
                                map[i] = Tiles.get("water").id;
                            }
                        } else {
                            map[i] = Tiles.get("rock").id;
                        }

                        break;
                    case "Irregular":

                        if (val < -0.5 && mval < -0.5) {
                            if (Settings.get("Theme").equals("Hell")) {
                                map[i] = Tiles.get("lava").id;
                            }
                            if (!Settings.get("Theme").equals("Hell")) {
                                map[i] = Tiles.get("water").id;
                            }

                        } else if (val > 0.5 && mval < -1.5) {
                            map[i] = Tiles.get("rock").id;
                        } else {
                            map[i] = Tiles.get("grass").id;
                        }
                        break;

                    default:
                        // meh
                        break;
                }
            }
        }

        // These biomes are established :
        // Desert (big) biome
        if (Settings.get("Theme").equals("Desert")) {
            for (int i = 0; i < w * h / 800; i++) {
                int xs = random.nextInt(w);
                int ys = random.nextInt(h);
                for (int k = 0; k < 16; k++) {
                    int x = xs + random.nextInt(28) - 10 + random.nextInt(5);
                    int y = ys + random.nextInt(28) - 10 + random.nextInt(5);
                    for (int j = 0; j < 100; j++) {
                        int xo = x + random.nextInt(6) - random.nextInt(4) + random.nextInt(2);
                        int yo = y + random.nextInt(6) - random.nextInt(4) + random.nextInt(2);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("grass").id) {
                                        map[xx + yy * w] = Tiles.get("sand").id;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tundra (big) biome
        if (Settings.get("Theme").equals("Snow")) {
            for (int i = 0; i < w * h / 2800; i++) {
                int xs = random.nextInt(w);
                int ys = random.nextInt(h);
                for (int k = 0; k < 20; k++) {
                    int x = xs + random.nextInt(21) - 10;
                    int y = ys + random.nextInt(21) - 10;
                    for (int j = 0; j < 100; j++) {
                        int xo = x + random.nextInt(5) - random.nextInt(5);
                        int yo = y + random.nextInt(5) - random.nextInt(5);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("grass").id) {
                                        map[xx + yy * w] = Tiles.get("snow").id;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Desert (Medium) biome
        if (!Settings.get("Theme").equals("Desert")) {
            for (int i = 0; i < w * h / 2800; i++) {
                int xs = random.nextInt(w);
                int ys = random.nextInt(h);
                for (int k = 0; k < 10; k++) {
                    int x = xs + random.nextInt(21) - 8 + random.nextInt(5);
                    int y = ys + random.nextInt(21) - 8 + random.nextInt(5);
                    for (int j = 0; j < 100; j++) {
                        int xo = x + random.nextInt(5) - random.nextInt(4) + random.nextInt(3);
                        int yo = y + random.nextInt(5) - random.nextInt(4) + random.nextInt(3);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("grass").id) {
                                        map[xx + yy * w] = Tiles.get("sand").id;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tundra (Medium) biome
        if (!Settings.get("Theme").equals("Snow")) {
            for (int i = 0; i < w * h / 2800; i++) {
                int xs = random.nextInt(w);
                int ys = random.nextInt(h);
                for (int k = 0; k < 100; k++) {
                    int x = xs + random.nextInt(21) - 10 + random.nextInt(5);
                    int y = ys + random.nextInt(21) - 10 + random.nextInt(5);
                    for (int j = 0; j < 200; j++) {
                        int xo = x + random.nextInt(6) - random.nextInt(5) + random.nextInt(3);
                        int yo = y + random.nextInt(6) - random.nextInt(5) + random.nextInt(3);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("grass").id) {
                                        map[xx + yy * w] = Tiles.get("snow").id;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add trees to biomes
        // Classic forest biome
        if (Settings.get("Theme").equals("Forest")) {
            for (int i = 0; i < w * h / 200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 200; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(14);
                    int yy = y + random.nextInt(15) - random.nextInt(14);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("grass").id) {
                            map[xx + yy * w] = Tiles.get("Oak tree").id;
                        }
                    }
                }
            }
        }

        // Plain biome, add trees
        if (!Settings.get("Theme").equals("Forest") && !Settings.get("Theme").equals("Plain")) {
            for (int i = 0; i < w * h / 1200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 200; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(14);
                    int yy = y + random.nextInt(15) - random.nextInt(14);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("grass").id) {
                            map[xx + yy * w] = Tiles.get("Oak tree").id;
                        }
                    }
                }
            }
        }

        // Tundra biome, add fir trees
        if (Settings.get("Theme").equals("Snow")) {
            for (int i = 0; i < w * h / 200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 60; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(12);
                    int yy = y + random.nextInt(15) - random.nextInt(12);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("snow").id) {
                            map[xx + yy * w] = Tiles.get("Fir tree").id;
                        }
                    }
                }
            }
        }

        // Tundra biome, add pine trees
        if (Settings.get("Theme").equals("Snow")) {
            for (int i = 0; i < w * h / 200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 60; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(14);
                    int yy = y + random.nextInt(15) - random.nextInt(14);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("snow").id) {
                            map[xx + yy * w] = Tiles.get("Pine tree").id;
                        }
                    }
                }
            }
        }

        // Plain biome, Add less trees
        if (Settings.get("Theme").equals("Plain")) {
            for (int i = 0; i < w * h / 2800; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 200; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(12) + random.nextInt(4);
                    int yy = y + random.nextInt(15) - random.nextInt(12) + random.nextInt(4);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("grass").id) {
                            map[xx + yy * w] = Tiles.get("Oak tree").id;
                        }
                    }
                }
            }
        }

        // Tundra biome, Add less fir trees
        if (!Settings.get("Theme").equals("Snow")) {
            for (int i = 0; i < w * h / 200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 50; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(12) + random.nextInt(4);
                    int yy = y + random.nextInt(15) - random.nextInt(12) + random.nextInt(4);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("snow").id) {
                            map[xx + yy * w] = Tiles.get("Fir tree").id;
                        }
                    }
                }
            }
        }

        // Tundra biome, Add less pine trees
        if (!Settings.get("Theme").equals("Snow")) {
            for (int i = 0; i < w * h / 200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 50; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(14) + random.nextInt(5);
                    int yy = y + random.nextInt(15) - random.nextInt(14) + random.nextInt(5);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("snow").id) {
                            map[xx + yy * w] = Tiles.get("Pine tree").id;
                        }
                    }
                }
            }
        }

        // Makes a separation with the tundra and desert biome adding the plains biome between these
        for (int j = 0; j < h; j++) {
            for (int x = 0; x < w; x++) {
                // tiles SIDE A and SIDE B
                if (map[x + j * w] != Tiles.get("snow").id && map[x + j * w] == Tiles.get("sand").id) {
                    boolean replace2 = false;
                    int tx;
                    check_biome_a:
                    for (tx = x - tundra_desert_separator - random.nextInt(4); tx <= x + tundra_desert_separator + random.nextInt(4); tx++) {
                        for (int ty = j - tundra_desert_separator + random.nextInt(3); ty <= j + tundra_desert_separator - random.nextInt(3); ty++) {
                            if (tx >= 0 && ty >= 0 && tx <= w && ty <= h && (tx != x || ty != j) && map[tx + ty * w] == Tiles.get("snow").id) { // start in the SIDE A
                                replace2 = true;
                                break check_biome_a;
                            }
                        }
                    }

                    if (replace2) {
                        map[x + j * w] = Tiles.get("grass").id; // Add the separation
                    }

                }
            }
        }

        if (!Settings.get("Theme").equals("Plain")) {
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 200; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(12) + random.nextInt(5) - random.nextInt(2);
                    int yy = y + random.nextInt(15) - random.nextInt(12) + random.nextInt(5) - random.nextInt(2);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("grass").id) {
                            map[xx + yy * w] = Tiles.get("Oak tree").id;
                        }
                    }
                }
            }
        }

        // Plain biome, add birch tree
        if (!Settings.get("Theme").equals("Plain")) {
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 60; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(14);
                    int yy = y + random.nextInt(15) - random.nextInt(14);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("grass").id) {
                            map[xx + yy * w] = Tiles.get("Birch tree").id;
                        }
                    }
                }
            }
        }

        if (!Settings.get("Theme").equals("Snow")) {
            for (int i = 0; i < w * h / 200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 50; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(12);
                    int yy = y + random.nextInt(15) - random.nextInt(12);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("snow").id) {
                            map[xx + yy * w] = Tiles.get("Fir tree").id;
                        }
                    }
                }
            }
        }

        if (!Settings.get("Theme").equals("Snow")) {
            for (int i = 0; i < w * h / 200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 50; j++) {
                    int xx = x + random.nextInt(15) - random.nextInt(14);
                    int yy = y + random.nextInt(15) - random.nextInt(14);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("snow").id) {
                            map[xx + yy * w] = Tiles.get("Fir tree").id;
                        }
                    }
                }
            }
        }

        // Add flower and plants to biomes
        for (int i = 0; i < w * h / 400; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int col = random.nextInt(4);
            for (int j = 0; j < 30; j++) {
                int xx = x + random.nextInt(5) - random.nextInt(5);
                int yy = y + random.nextInt(5) - random.nextInt(5);
                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                    if (map[xx + yy * w] == Tiles.get("grass").id) {
                        map[xx + yy * w] = Tiles.get("flower").id;
                        data[xx + yy * w] = (short) (col + random.nextInt(4) * 16); // data determines which way the flower faces
                    }
                }
            }
        }

        // Add lawn to grass
        for (int i = 0; i < w * h / 400; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int col = random.nextInt(4);
            for (int j = 0; j < 100; j++) {
                int xx = x + random.nextInt(5) - random.nextInt(5);
                int yy = y + random.nextInt(5) - random.nextInt(5);
                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                    if (map[xx + yy * w] == Tiles.get("grass").id) {
                        map[xx + yy * w] = Tiles.get("lawn").id;
                        data[xx + yy * w] = (short) (col + random.nextInt(4) * 16);
                    }
                }
            }
        }

        // add orange tulip to grass
        for (int i = 0; i < w * h / 100; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int col = random.nextInt(4);
            for (int j = 0; j < 20; j++) {
                int xx = x + random.nextInt(4) - random.nextInt(4);
                int yy = y + random.nextInt(4) - random.nextInt(4);
                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                    if (map[xx + yy * w] == Tiles.get("grass").id) {
                        map[xx + yy * w] = Tiles.get("orange tulip").id;
                        data[xx + yy * w] = (byte) (col + random.nextInt(4) * 16);
                    }
                }
            }
        }

        // add cactus to sand
        for (int i = 0; i < w * h / 148; i++) {
            int xx = random.nextInt(w);
            int yy = random.nextInt(h);
            if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                if (map[xx + yy * w] == Tiles.get("sand").id) {
                    map[xx + yy * w] = Tiles.get("cactus").id;
                }
            }
        }

        // add ice spikes to snow
        for (int i = 0; i < w * h / 100; i++) {
            int xx = random.nextInt(w);
            int yy = random.nextInt(h);
            if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                if (map[xx + yy * w] == Tiles.get("snow").id) {
                    map[xx + yy * w] = Tiles.get("ice spike").id;
                }
            }
        }

        // same...
        for (int i = 0; i < w * h / 100; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            for (int j = 0; j < 20; j++) {
                int xx = x + random.nextInt(2) - random.nextInt(2);
                int yy = y + random.nextInt(2) - random.nextInt(2);
                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                    if (map[xx + yy * w] == Tiles.get("snow").id) {
                        map[xx + yy * w] = Tiles.get("ice spike").id;
                    }
                }
            }
        }

        /*
		 * for (int i = 0; i < w * h / 400; i++) { int x = random.nextInt(w); int y =
		 * random.nextInt(h); for (int j = 0; j < 80; j++) { int xx = x +
		 * random.nextInt(6) - random.nextInt(3); int yy = y + random.nextInt(6) -
		 * random.nextInt(3); if (xx >= 0 && yy >= 0 && xx < w && yy < h) { if (map[xx +
		 * yy * w] == Tiles.get("sand").id) { map[xx + yy * w] =
		 * Tiles.get("sand rock").id;
		 *
		 * } } } }
         */
        // Generate the stairs inside the rock
        int count = 0;

        if (Game.debug) {
            System.out.println("Generating stairs for surface level...");
        }

        stairsLoop:
        for (int i = 0; i < w * h / 100; i++) { // loops a certain number of times, more for bigger world

            // sizes.
            int x = random.nextInt(w - 2) + 1;
            int y = random.nextInt(h - 2) + 1;

            // the first loop, which checks to make sure that a new stairs tile will be
            // completely surrounded by rock.
            for (int yy = y - 1; yy <= y + 1; yy++) {
                for (int xx = x - 1; xx <= x + 1; xx++) {
                    if (map[xx + yy * w] != Tiles.get("rock").id) {
                        continue stairsLoop;
                    }
                }
            }

            // this should prevent any stairsDown tile from being within 30 tiles of any
            // other stairsDown tile.
            for (int yy = Math.max(0, y - stairRadius); yy <= Math.min(h - 1, y + stairRadius); yy++) {
                for (int xx = Math.max(0, x - stairRadius); xx <= Math.min(w - 1, x + stairRadius); xx++) {
                    if (map[xx + yy * w] == Tiles.get("Stairs Down").id) {
                        continue stairsLoop;
                    }
                }
            }

            map[x + y * w] = Tiles.get("Stairs Down").id;

            count++;
            if (count >= w / 21) {
                break;
            }
        }

        // generate the beaches
        for (int j = 0; j < h; j++) {
            for (int x = 0; x < w; x++) {
                if (map[x + j * w] != Tiles.get("water").id && map[x + j * w] == Tiles.get("grass").id || map[x + j * w] != Tiles.get("water").id && map[x + j * w] == Tiles.get("Oak tree").id || map[x + j * w] != Tiles.get("water").id && map[x + j * w] == Tiles.get("flower").id || map[x + j * w] != Tiles.get("water").id && map[x + j * w] == Tiles.get("Birch tree").id || map[x + j * w] != Tiles.get("water").id && map[x + j * w] == Tiles.get("lawn").id || map[x + j * w] != Tiles.get("water").id && map[x + j * w] == Tiles.get("orange tulip").id) {
                    boolean replace = false;
                    int tx;
                    check_ocean:
                    for (tx = x - beaches_thickness; tx <= x + beaches_thickness; tx++) {
                        for (int ty = j - beaches_thickness; ty <= j + beaches_thickness; ty++) {
                            if (tx >= 0 && ty >= 0 && tx <= w && ty <= h && (tx != x || ty != j) && map[tx + ty * w] == Tiles.get("water").id) {
                                replace = true;
                                break check_ocean;
                            }
                        }
                    }

                    if (replace) {
                        map[x + j * w] = Tiles.get("sand").id;
                    }

                }
            }
        }

        return new short[][]{map, data};
    }

    // Dungeons generation code
    private static short[][] createDungeon(int w, int h) {

        LevelGen noise1 = new LevelGen(w, h, 8);
        LevelGen noise2 = new LevelGen(w, h, 8);

        short[] map = new short[w * h];
        short[] data = new short[w * h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = x + y * w;

                double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;

                double xd = x / (w - 1.1) * 2 - 1;
                double yd = y / (h - 1.1) * 2 - 1;

                if (xd < 0) {
                    xd = -xd;
                }
                if (yd < 0) {
                    yd = -yd;
                }

                double dist = xd >= yd ? xd : yd;
                dist = dist * dist * dist * dist;
                dist = dist * dist * dist * dist;

                val = -val * 1 - 2.2;
                val += 1 - dist * 2;

                if (val < -0.35) {
                    map[i] = Tiles.get("Obsidian Wall").id;
                } else {
                    map[i] = Tiles.get("Obsidian").id;
                }
            }
        }

        lavaLoop:
        for (int i = 0; i < w * h / 450; i++) {
            int x = random.nextInt(w - 2) + 1;
            int y = random.nextInt(h - 2) + 1;

            for (int yy = y - 1; yy <= y + 1; yy++) {
                for (int xx = x - 1; xx <= x + 1; xx++) {
                    if (map[xx + yy * w] != Tiles.get("Obsidian Wall").id) {
                        continue lavaLoop;
                    }
                }
            }

            // Generate structure (lava pool)
            Structure.lavaPool.draw(map, x, y, w);
        }

        for (int i = 0; i < w * h / 100; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            for (int j = 0; j < 20; j++) {
                int xx = x + random.nextInt(3) - random.nextInt(3);
                int yy = y + random.nextInt(3) - random.nextInt(3);
                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                    if (map[xx + yy * w] == Tiles.get("Obsidian").id) {
                        map[xx + yy * w] = Tiles.get("Raw obsidian").id;
                    }

                }
            }
        }

        return new short[][]{map, data};
    }

    // Generate cave system
    private static short[][] createUndergroundMap(int w, int h, int depth) {

        LevelGen mnoise1 = new LevelGen(w, h, 16);
        LevelGen mnoise2 = new LevelGen(w, h, 16);
        LevelGen mnoise3 = new LevelGen(w, h, 16);

        LevelGen nnoise1 = new LevelGen(w, h, 16);
        LevelGen nnoise2 = new LevelGen(w, h, 16);
        LevelGen nnoise3 = new LevelGen(w, h, 16);

        LevelGen wnoise1 = new LevelGen(w, h, 16);
        LevelGen wnoise2 = new LevelGen(w, h, 16);
        LevelGen wnoise3 = new LevelGen(w, h, 16);

        LevelGen noise1 = new LevelGen(w, h, 32);
        LevelGen noise2 = new LevelGen(w, h, 32);

        /*
		 * This generates the 3 levels of cave, iron, gold and gem
         */
        short[] map = new short[w * h];
        short[] data = new short[w * h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = x + y * w;

                /// for the x=0 or y=0 i's, values[i] is always between -1 and 1.
                /// so, val is between -2 and 4.
                /// the rest are between -2 and 7.
                double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;

                double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
                mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

                double nval = Math.abs(nnoise1.values[i] - nnoise2.values[i]);
                nval = Math.abs(nval - nnoise3.values[i]) * 3 - 2;

                double wval = Math.abs(wnoise1.values[i] - wnoise2.values[i]);
                wval = Math.abs(wval - wnoise3.values[i]) * 3 - 2;

                double xd = x / (w - 1.0) * 2 - 1;
                double yd = y / (h - 1.0) * 2 - 1;

                if (xd < 0) {
                    xd = -xd;
                }
                if (yd < 0) {
                    yd = -yd;
                }

                double dist = xd >= yd ? xd : yd;
                dist = Math.pow(dist, 8);
                val += 1 - dist * 20;

                if (val > -1 && wval < -4 + (depth) / 2 * 3) {
                    switch (depth) {
                        case 3:
                            map[i] = Tiles.get("lava").id;
                            break;
                        case 1:
                            map[i] = Tiles.get("dirt").id;
                            break;
                        default:
                            map[i] = Tiles.get("water").id;
                            break;
                    }
                } else if (val > -2 && (mval < -1.7 || nval < -1.4)) {
                    map[i] = Tiles.get("dirt").id;
                } else {
                    map[i] = Tiles.get("rock").id;
                }
            }
        }

        // Generate ores
        {
            // Iron ore
            int r = 2;
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 30; j++) {
                    int xx = x + random.nextInt(5) - random.nextInt(5);
                    int yy = y + random.nextInt(5) - random.nextInt(5);
                    if (xx >= r && yy >= r && xx < w - r && yy < h - r) {
                        if (map[xx + yy * w] == Tiles.get("rock").id) {
                            map[xx + yy * w] = (short) ((Tiles.get("iron Ore").id & 0xffff) + depth - 1);
                        }
                    }
                }

                // Lapizlazuli ore
                for (int j = 0; j < 10; j++) {
                    int xx = x + random.nextInt(3) - random.nextInt(2);
                    int yy = y + random.nextInt(3) - random.nextInt(2);
                    if (xx >= r && yy >= r && xx < w - r && yy < h - r) {
                        if (map[xx + yy * w] == Tiles.get("rock").id) {
                            map[xx + yy * w] = (short) (Tiles.get("Lapis").id & 0xffff);
                        }
                    }
                }
            }
        }

        if (depth > 2) {
            int r = 1;
            int xx = 60;
            int yy = 60;
            for (int i = 0; i < w * h / 380; i++) {
                for (int j = 0; j < 10; j++) {
                    if (xx < w - r && yy < h - r) {

                        Structure.dungeonLock.draw(map, xx, yy, w);

                        /// The "& 0xffff" is a common way to convert a short to an unsigned int, which basically prevents negative values... except... this doesn't do anything if you flip it back to a short again...
                        map[xx + yy * w] = (short) (Tiles.get("Stairs Down").id & 0xffff);
                    }
                }
            }
        }

        if (depth < 3) {
            int count = 0;
            stairsLoop:
            for (int i = 0; i < w * h / 100; i++) {
                int x = random.nextInt(w - 20) + 10;
                int y = random.nextInt(h - 20) + 10;

                for (int yy = y - 1; yy <= y + 1; yy++) {
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        if (map[xx + yy * w] != Tiles.get("rock").id) {
                            continue stairsLoop;
                        }
                    }
                }

                // This should prevent any stairsDown tile from being within 30 tiles of any other stairsDown tile.
                for (int yy = Math.max(0, y - stairRadius); yy <= Math.min(h - 1, y + stairRadius); yy++) {
                    for (int xx = Math.max(0, x - stairRadius); xx <= Math.min(w - 1, x + stairRadius); xx++) {
                        if (map[xx + yy * w] == Tiles.get("Stairs Down").id) {
                            continue stairsLoop;
                        }
                    }
                }

                map[x + y * w] = Tiles.get("Stairs Down").id;
                count++;
                if (count >= w / 32) {
                    break;
                }
            }
        }

        return new short[][]{map, data};
    }

    // Sky dimension generation
    private static short[][] createSkyMap(int w, int h) {
        LevelGen noise1 = new LevelGen(w, h, 8);
        LevelGen noise2 = new LevelGen(w, h, 8);

        short[] map = new short[w * h];
        short[] data = new short[w * h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = x + y * w;

                double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;
                double xd = x / (w - 1.0) * 2 - 1;
                double yd = y / (h - 1.0) * 2 - 1;

                if (xd < 0) xd = -xd;
                if (yd < 0) yd = -yd;
                
                double dist = xd >= yd ? xd : yd;
                dist = dist * dist * dist * dist;
                dist = dist * dist * dist * dist;

                val = -val * 1 - 2.2;
                val += 1 - dist * 20;

                if (val < -0.27) {
                    map[i] = Tiles.get("Infinite Fall").id;
                } else {
                    map[i] = Tiles.get("cloud").id;
                }
            }
        }

        if (!(w == 128) && !(h == 128)) { // For 256x or more size worlds

            // Generate skygrass in cloud tile
            for (int i = 0; i < w * h / 1024; i++) {
                int xs = w / 2 - 30;
                int ys = h / 2 - 30;
                for (int k = 0; k < 90; k++) {
                    int x = xs; // + random.nextInt(28) - 10;
                    int y = ys; // + random.nextInt(30) - 10;
                    for (int j = 0; j < 190; j++) {
                        int xo = x + random.nextInt(60) - random.nextInt(16) + random.nextInt(8);
                        int yo = y + random.nextInt(60) - random.nextInt(16) + random.nextInt(8);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("cloud").id) {
                                        map[xx + yy * w] = Tiles.get("Sky Grass").id;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Generate skygrass in cloud tile
            for (int i = 0; i < w * h / 2800; i++) {
                int xs = w / 2 - 30;
                int ys = h / 2 - 30;
                for (int k = 0; k < 90; k++) {
                    int x = xs + random.nextInt(28) - random.nextInt(10);
                    int y = ys + random.nextInt(30) - random.nextInt(10);
                    for (int j = 0; j < 190; j++) {
                        int xo = x + random.nextInt(60) - random.nextInt(16) + random.nextInt(8);
                        int yo = y + random.nextInt(60) - random.nextInt(16) + random.nextInt(8);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("cloud").id) {
                                        map[xx + yy * w] = Tiles.get("Sky Grass").id;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Generate skygrass in cloud tile
            for (int i = 0; i < w * h / 2800; i++) {
                int xs = w / 2 - 15;
                int ys = h / 2 - 15;
                for (int k = 0; k < 90; k++) {
                    int x = xs + random.nextInt(28) - random.nextInt(10);
                    int y = ys + random.nextInt(30) - random.nextInt(10);
                    for (int j = 0; j < 190; j++) {
                        int xo = x + random.nextInt(30) - random.nextInt(16) + random.nextInt(8);
                        int yo = y + random.nextInt(30) - random.nextInt(16) + random.nextInt(8);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("Infinite Fall").id) {
                                        map[xx + yy * w] = Tiles.get("Sky high Grass").id;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Generate skygrass in cloud tile
            for (int i = 0; i < w * h / 2800; i++) {
                int xs = w / 2 - 40;
                int ys = h / 2 - 40;
                for (int k = 0; k < 90; k++) {
                    int x = xs + random.nextInt(28) - random.nextInt(10);
                    int y = ys + random.nextInt(30) - random.nextInt(10);
                    for (int j = 0; j < 190; j++) {
                        int xo = x + random.nextInt(80) - random.nextInt(30) + random.nextInt(12);
                        int yo = y + random.nextInt(80) - random.nextInt(30) + random.nextInt(12);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("Cloud").id) {
                                        map[xx + yy * w] = Tiles.get("Ferrosite").id;

                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Generate Sky high grass in sky grass tile
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 80; j++) {
                    int xx = x + random.nextInt(10) - random.nextInt(5) + random.nextInt(3);
                    int yy = y + random.nextInt(10) - random.nextInt(5) + random.nextInt(3);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky Grass").id) {
                            map[xx + yy * w] = Tiles.get("Sky High Grass").id;
                        }
                    }
                }
            }

            for (int i = 0; i < w * h / 800; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 100; j++) {
                    int xx = x + random.nextInt(6) + random.nextInt(4);
                    int yy = y + random.nextInt(6) + random.nextInt(4);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky Grass").id) {
                            map[xx + yy * w] = Tiles.get("Sky High Grass").id;
                        }
                    }
                }
            }

            // Generate sky lawn in Sky grass
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                int col = random.nextInt(4);
                for (int j = 0; j < 100; j++) {
                    int xx = x + random.nextInt(5) - random.nextInt(5);
                    int yy = y + random.nextInt(5) - random.nextInt(5);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("sky grass").id) {
                            map[xx + yy * w] = Tiles.get("sky lawn").id;
                            data[xx + yy * w] = (short) (col + random.nextInt(4) * 16);
                        }
                    }
                }
            }

            // Generate sky lawn in Sky grass
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 75; j++) {
                    int xx = x + random.nextInt(3) - random.nextInt(3);
                    int yy = y + random.nextInt(3) - random.nextInt(3);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("sky high grass").id) {
                            map[xx + yy * w] = Tiles.get("sky fern").id;
                        }
                    }
                }
            }

            // Generate Normal cloud trees
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 75; j++) {
                    int xx = x + random.nextInt(14) - random.nextInt(12) + random.nextInt(4);
                    int yy = y + random.nextInt(14) - random.nextInt(12) + random.nextInt(4);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky grass").id) {
                            map[xx + yy * w] = Tiles.get("Cloud tree").id;
                        }
                    }
                }
            }

            // Generate Normal blue cloud trees
            for (int i = 0; i < w * h / 200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 75; j++) {
                    int xx = x + random.nextInt(14) - random.nextInt(12) + random.nextInt(4);
                    int yy = y + random.nextInt(14) - random.nextInt(12) + random.nextInt(4);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky grass").id) {
                            map[xx + yy * w] = Tiles.get("Blue cloud tree").id;
                        }
                    }
                }
            }

            // Generate Golden cloud trees
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 75; j++) {
                    int xx = x + random.nextInt(14) - random.nextInt(12);
                    int yy = y + random.nextInt(14) - random.nextInt(12);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky high grass").id) {
                            map[xx + yy * w] = Tiles.get("Golden cloud tree").id;
                        }
                    }
                }
            }

            /*
            for (int i = 0; i < w * h / 800; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 200; j++) {
                    int xx = x + random.nextInt(4) - random.nextInt(3);
                    int yy = y + random.nextInt(4) - random.nextInt(3);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky high grass").id) {
                            map[xx + yy * w] = Tiles.get("Holy rock").id;
                        }
                    }
                }
            } */

            for (int i = 0; i < w * h / 150; i++) {
                int xx = random.nextInt(w);
                int yy = random.nextInt(h);
                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                    if (map[xx + yy * w] == Tiles.get("Ferrosite").id) {
                        map[xx + yy * w] = Tiles.get("Cloud cactus").id;
                    }
                }
            }

        }

        if ((w == 128) && (h == 128)) { // for 128x worlds

            // Generate skygrass in cloud tile
            for (int i = 0; i < w * h / 2800; i++) {
                int xs = random.nextInt(w - 2);
                int ys = random.nextInt(h - 2);
                for (int k = 0; k < 90; k++) {
                    int x = xs + random.nextInt(28) - 10;
                    int y = ys + random.nextInt(30) - 10;
                    for (int j = 0; j < 190; j++) {
                        int xo = x + random.nextInt(10) - random.nextInt(5) + random.nextInt(3);
                        int yo = y + random.nextInt(10) - random.nextInt(5) + random.nextInt(3);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("cloud").id) {
                                        map[xx + yy * w] = Tiles.get("Sky Grass").id;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Generate Sky high grass in sky grass tile
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 80; j++) {
                    int xx = x + random.nextInt(10) - random.nextInt(5) + random.nextInt(3);
                    int yy = y + random.nextInt(10) - random.nextInt(5) + random.nextInt(3);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky Grass").id) {
                            map[xx + yy * w] = Tiles.get("Sky High Grass").id;
                        }
                    }
                }
            }

            for (int i = 0; i < w * h / 800; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 100; j++) {
                    int xx = x + random.nextInt(6) + random.nextInt(4);
                    int yy = y + random.nextInt(6) + random.nextInt(4);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky Grass").id) {
                            map[xx + yy * w] = Tiles.get("Sky High Grass").id;

                        }
                    }
                }
            }

            // Generate ferrosite randomly in cloud tile
            for (int i = 0; i < w * h / 2800; i++) {
                int xs = random.nextInt(w - 2);
                int ys = random.nextInt(h - 2);
                for (int k = 0; k < 90; k++) {
                    int x = xs + random.nextInt(21) - 10;
                    int y = ys + random.nextInt(24) - 10;
                    for (int j = 0; j < 190; j++) {
                        int xo = x + random.nextInt(8) - random.nextInt(5) + random.nextInt(2);
                        int yo = y + random.nextInt(8) - random.nextInt(5) + random.nextInt(2);
                        for (int yy = yo - 1; yy <= yo + 1; yy++) {
                            for (int xx = xo - 1; xx <= xo + 1; xx++) {
                                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                                    if (map[xx + yy * w] == Tiles.get("cloud").id) {
                                        map[xx + yy * w] = Tiles.get("Ferrosite").id;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Generate sky lawn in Sky grass
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                int col = random.nextInt(4);
                for (int j = 0; j < 100; j++) {
                    int xx = x + random.nextInt(5) - random.nextInt(5);
                    int yy = y + random.nextInt(5) - random.nextInt(5);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("sky grass").id) {
                            map[xx + yy * w] = Tiles.get("sky lawn").id;
                            data[xx + yy * w] = (short) (col + random.nextInt(4) * 16);
                        }
                    }
                }
            }

            // Generate Normal cloud trees
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 100; j++) {
                    int xx = x + random.nextInt(16) - random.nextInt(16) + random.nextInt(5);
                    int yy = y + random.nextInt(16) - random.nextInt(16) + random.nextInt(5);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky grass").id) {
                            map[xx + yy * w] = Tiles.get("Cloud tree").id;
                        }
                    }
                }
            }

            // Generate Normal blue cloud trees
            for (int i = 0; i < w * h / 200; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 100; j++) {
                    int xx = x + random.nextInt(16) - random.nextInt(16) + random.nextInt(5);
                    int yy = y + random.nextInt(16) - random.nextInt(16) + random.nextInt(5);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky grass").id) {
                            map[xx + yy * w] = Tiles.get("Blue cloud tree").id;
                        }
                    }
                }
            }

            // Generate Golden cloud trees
            for (int i = 0; i < w * h / 400; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 100; j++) {
                    int xx = x + random.nextInt(14) - random.nextInt(14) + random.nextInt(5);
                    int yy = y + random.nextInt(14) - random.nextInt(14) + random.nextInt(5);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Sky high grass").id) {
                            map[xx + yy * w] = Tiles.get("Golden cloud tree").id;
                        }
                    }
                }
            }

            for (int i = 0; i < w * h / 800; i++) {
                int x = random.nextInt(w);
                int y = random.nextInt(h);
                for (int j = 0; j < 100; j++) {
                    int xx = x + random.nextInt(6);
                    int yy = y + random.nextInt(6);
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tiles.get("Ferrosite").id) {
                            map[xx + yy * w] = Tiles.get("Holy rock").id;
                        }
                    }
                }
            }
        }

        int count = 0;
        stairsLoop:
        for (int i = 0; i < w * h; i++) {
            int x = random.nextInt(w - 2) + 1;
            int y = random.nextInt(h - 2) + 1;

            for (int yy = y - 1; yy <= y + 1; yy++) {
                for (int xx = x - 1; xx <= x + 1; xx++) {
                    if (map[xx + yy * w] != Tiles.get("cloud").id) {
                        continue stairsLoop;
                    }
                }
            }

            // this should prevent any stairsDown tile from being within 30 tiles of any
            // other stairsDown tile.
            for (int yy = Math.max(0, y - stairRadius); yy <= Math.min(h - 1, y + stairRadius); yy++) {
                for (int xx = Math.max(0, x - stairRadius); xx <= Math.min(w - 1, x + stairRadius); xx++) {
                    if (map[xx + yy * w] == Tiles.get("Stairs Down").id) {
                        continue stairsLoop;
                    }
                }
            }

            map[x + y * w] = Tiles.get("Stairs Down").id;
            count++;
            if (count >= w / 64) {
                break;
            }
        }

        return new short[][]{map, data};

    }

    public static short[][] createVoidMap(int w, int h) {
        // creates a bunch of value maps, some with small size...
        LevelGen mnoise1 = new LevelGen(w, h, 16);
        LevelGen mnoise2 = new LevelGen(w, h, 16);
        LevelGen mnoise3 = new LevelGen(w, h, 16);
        // ...and some with larger size.
        LevelGen noise1 = new LevelGen(w, h, 32);
        LevelGen noise2 = new LevelGen(w, h, 32);

        short[] map = new short[w * h];
        short[] data = new short[w * h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = x + y * w;

                double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;
                double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
                mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

                // this calculates a sort of distance based on the current coordinate.
                double xd = x / (w - 1.0) * 2 - 1;
                double yd = y / (h - 1.0) * 2 - 1;
                if (xd < 0) {
                    xd = -xd;
                }
                if (yd < 0) {
                    yd = -yd;
                }
                double dist = xd >= yd ? xd : yd;
                dist = dist * dist * dist * dist;
                dist = dist * dist * dist * dist;
                val += 1 - dist * 20;

                if (val < -0.5) {
                    map[i] = Tiles.get("Infinite Fall").id;
                } else if (val > 0.5 && mval < -1.0) {
                    map[i] = Tiles.get("rock").id;
                } else {
                    map[i] = Tiles.get("grass").id;
                }
            }
        }

        for (int i = 0; i < w * h / 200; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            for (int j = 0; j < 200; j++) {
                int xx = x + random.nextInt(15) - random.nextInt(14);
                int yy = y + random.nextInt(15) - random.nextInt(14);
                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                    if (map[xx + yy * w] == Tiles.get("grass").id) {
                        map[xx + yy * w] = Tiles.get("oak tree").id;
                    }
                }
            }
        }

        return new short[][]{map, data};
    }

    public static void main(String[] args) {
        /*
		 * This is used to see seeds without having to run the game I mean, this is a
		 * world viewer that uses the same method as above using perlin noise, to
		 * generate a world, and be able to see it in a JPane according to the size of
		 * the world generated
         */

        LevelGen.worldSeed = 0x100; // ???

        // Fixes to get this method to work
        // AirWizard needs this in constructor
        Game.gameDir = "";

        Tiles.initTileList();
        // End of fixes

        int idx = -1;

        int[] maplvls = new int[args.length];
        boolean valid = true;
        if (maplvls.length > 0) {
            for (int i = 0; i < args.length; i++) {
                try {
                    int lvlnum = Integer.parseInt(args[i]);
                    maplvls[i] = lvlnum;
                    maplvls[i] = lvlnum;
                } catch (NumberFormatException ex) {
                    valid = false;
                    break;
                }
            }
        } else {
            valid = false;
        }

        if (!valid) {
            maplvls = new int[1];
            maplvls[0] = 0;
        }

        boolean hasquit = false;
        while (!hasquit) { // stop the loop and close the program.)

            long startTime = System.nanoTime();
            int w = 256, h = 256;
            int mapScale = 0;

            if ((w == 128) && (h == 128)) {
                mapScale = 3;
            } else if ((w == 256) && (h == 256)) {
                mapScale = 2;
            } else if ((w == 512) && (h == 512)) {
                mapScale = 1;
            }

            int lvl = maplvls[idx++ % maplvls.length];
            if (lvl > 1 || lvl < -4) {
                continue;
            }

            short[][] fullmap = LevelGen.createAndValidateTopMap(w, h);

            if (fullmap == null) {
                continue;
            }
            short[] map = fullmap[0];

            // Create the map image
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); // Creates an image
            int[] pixels = new int[w * h]; // The pixels in the image. (an integer array, the size is Width * height)

            for (int y = 0; y < h; y++) { // Loops through the height of the map
                for (int x = 0; x < w; x++) { // (inner-loop)Loops through the entire width of the map
                    int i = x + y * w; // Current tile of the map.

                    /*The colors used in the pixels are hexadecimal (0xRRGGBB).
                      0xff0000 would be fully red
                      0x00ff00 would be fully blue
                      0x0000ff would be fully green
                      0x000000 would be black
                      and 0xffffff would be white etc.
                    */

                    // Surface tiles
                    if (map[i] == Tiles.get("grass").id) pixels[i] = 0x54a854;
                    if (map[i] == Tiles.get("lawn").id) pixels[i] = 0x60a560;
                    if (map[i] == Tiles.get("orange tulip").id) pixels[i] = 0x60a560;
                    if (map[i] == Tiles.get("dirt").id) pixels[i] = 0x836c6c;
                    if (map[i] == Tiles.get("flower").id) pixels[i] = 0x60A560;
                    if (map[i] == Tiles.get("water").id) pixels[i] = 0x1A2C89;
                    if (map[i] == Tiles.get("lava").id) pixels[i] = 0xC82020;
                    if (map[i] == Tiles.get("rock").id) pixels[i] = 0x7a7a7a;
                    if (map[i] == Tiles.get("Up rock").id) pixels[i] = 0x939393;

                    if (map[i] == Tiles.get("iron Ore").id) pixels[i] = 0x452728;
                    if (map[i] == Tiles.get("gold Ore").id) pixels[i] = 0x948028;
                    if (map[i] == Tiles.get("gem Ore").id) pixels[i] = 0x821DB6;

                    if (map[i] == Tiles.get("sand").id) pixels[i] = 0xe2e26f;
                    if (map[i] == Tiles.get("cactus").id) pixels[i] = 0xe8e86d;
                    if (map[i] == Tiles.get("snow").id) pixels[i] = 0xf0f0f0;
                    if (map[i] == Tiles.get("ice spike").id) pixels[i] = 0xe6e6e6;
                    if (map[i] == Tiles.get("Stone Bricks").id) pixels[i] = 0xa0a040;
                    if (map[i] == Tiles.get("Oak tree").id) pixels[i] = 0x255325;
                    if (map[i] == Tiles.get("Birch tree").id) pixels[i] = 0x0c750c;
                    if (map[i] == Tiles.get("Fir tree").id) pixels[i] = 0x138b62;
                    if (map[i] == Tiles.get("Pine tree").id) pixels[i] = 0x117f59;

                    // Village
                    if (map[i] == Tiles.get("Wood Planks").id) pixels[i] = 0x914f0e;
                    if (map[i] == Tiles.get("Wood Wall").id) pixels[i] = 0x7a430c;
                    if (map[i] == Tiles.get("Wood Door").id) pixels[i] = 0x7a4817;

                    // Ores tiles
                    if (map[i] == Tiles.get("Iron Ore").id) pixels[i] = 0xC4B1AA;
                    if (map[i] == Tiles.get("Lapis").id) pixels[i] = 0x2D2D92;
                    if (map[i] == Tiles.get("Gold Ore").id) pixels[i] = 0xCE9612;
                    if (map[i] == Tiles.get("Gem Ore").id) pixels[i] = 0xD25BD2;

                    // Dungeon tiles
                    if (map[i] == Tiles.get("Obsidian Wall").id) pixels[i] = 0x480887;
                    if (map[i] == Tiles.get("Raw obsidian").id) pixels[i] = 0x5f0aa0;
                    if (map[i] == Tiles.get("Obsidian").id) pixels[i] = 0x660aa0;

                    // Stairs
                    if (map[i] == Tiles.get("Stairs Down").id) pixels[i] = 0xffffffff;
                    if (map[i] == Tiles.get("Stairs Up").id) pixels[i] = 0xffffffff;

                    // Sky tiles
                    if (map[i] == Tiles.get("Infinite Fall").id) pixels[i] = 0x010101;
                    if (map[i] == Tiles.get("cloud").id) pixels[i] = 0xf7f7f7;
                    if (map[i] == Tiles.get("Cloud Cactus").id) pixels[i] = 0xfafafa;
                    if (map[i] == Tiles.get("Cloud tree").id) pixels[i] = 0x477044;
                    if (map[i] == Tiles.get("Golden cloud tree").id) pixels[i] = 0xBBA14F;
                    if (map[i] == Tiles.get("Blue cloud tree").id) pixels[i] = 0x00769E;
                    if (map[i] == Tiles.get("Ferrosite").id) pixels[i] = 0xcbc579;
                    if (map[i] == Tiles.get("Sky grass").id) pixels[i] = 0x5aab8a;
                    if (map[i] == Tiles.get("Sky fern").id) pixels[i] = 0x5aab8a;
                    if (map[i] == Tiles.get("Sky lawn").id) pixels[i] = 0x56a383;
                    if (map[i] == Tiles.get("Sky high grass").id) pixels[i] = 0x4f9678;
                    if (map[i] == Tiles.get("Holy rock").id) pixels[i] = 0x7a7a7a;
                    if (map[i] == Tiles.get("jungle grass").id) pixels[i] = 0x8AB33F;
                }
            }

            long endTime = System.nanoTime();
            long timeMillis = endTime - startTime;
            long timeElapsed = timeMillis / 1000000000;

            String finalGenTime;

            if (timeElapsed > 32) {
                finalGenTime = "Time: " + timeElapsed + "s" + " | " + "WARNING: Slow gen!";
            } else {
                finalGenTime = "Time: " + timeElapsed + "s";
            }

            // Print the seed, the generator version, the elapsed time
            //System.out.println("[LevelGen]" + " | " + "Seed: " + worldSeed + " | " + "Gen-Version: " + Game.BUILD + " | " + finalGenTime);
            Logger.debug("Generated: {} | Seed: {} | Gen-Version: {} | {}", lvl, worldSeed, Game.BUILD, finalGenTime);

            img.setRGB(0, 0, w, h, pixels, 0, w); // Sets the pixels into the image

            // Name of the buttons used for the window.
            String[] options = {
                "Another",
                "Quit"
            };

            int Generator = JOptionPane.showOptionDialog(
                null,
                null,
                "Map Generator",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                new ImageIcon(img.getScaledInstance(w * mapScale, h * mapScale, Image.SCALE_SMOOTH)),
                options,
                null
            );

            if (LevelGen.worldSeed == 0x100) {
                LevelGen.worldSeed = 0xAAFF20;
            } else {
                LevelGen.worldSeed = 0x100;
            }

            /* Now you noticed that we made the dialog an integer. This is because when you click a button it will return a number.
               Since we passed in 'options', the window will return 0 if you press "Another" and it will return 1 when you press "Quit".
               If you press the red "x" close mark, the window will return -1
             */
            // If the dialog returns -1 (red "x" button) or 1 ("Quit" button) then...
            if (Generator == -1 || Generator == 1) {
                hasquit = true; // Stop the loop and close the program.
            }
        }
    }
}
