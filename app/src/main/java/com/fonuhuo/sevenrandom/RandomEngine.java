package com.fonuhuo.sevenrandom;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Generates three unbiased random integers and maps them through the standard
 * six-palace continuous counting order used by Xiao Liu Ren.
 */
public final class RandomEngine {
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 999;
    public static final int CYCLE_SIZE = 6;

    private static final String[] PALACE_NAMES = {
            "大安", "留连", "速喜", "赤口", "小吉", "空亡"
    };

    private final Random random;
    private final SecureRandom secureRandom;

    public RandomEngine() {
        this(new SecureRandom());
    }

    RandomEngine(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        this.random = random;
        this.secureRandom = random instanceof SecureRandom ? (SecureRandom) random : null;
    }

    public int[] generateThree() {
        return new int[] { nextValue(), nextValue(), nextValue() };
    }

    /**
     * Supplements the system-backed SecureRandom state with material from the
     * exact touch that freezes a roll. This never replaces SecureRandom as the
     * primary source; it only adds entropy to the already-seeded generator.
     */
    public void mixTouchEntropy(long nanoTime, long eventTimeMillis, float x, float y) {
        if (secureRandom != null) {
            secureRandom.setSeed(buildTouchEntropy(nanoTime, eventTimeMillis, x, y));
        }
    }

    static byte[] buildTouchEntropy(long nanoTime, long eventTimeMillis, float x, float y) {
        return ByteBuffer.allocate(24)
                .putLong(nanoTime)
                .putLong(eventTimeMillis)
                .putInt(Float.floatToRawIntBits(x))
                .putInt(Float.floatToRawIntBits(y))
                .array();
    }

    private int nextValue() {
        return random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE;
    }

    /**
     * Counts the first step from Da An, then continues each later count from
     * the previous landing palace. The current palace always counts as 1.
     * Palace indexes are 1..6 in the order Da An, Liu Lian, Su Xi, Chi Kou,
     * Xiao Ji, Kong Wang.
     */
    public static int[] calculatePalaces(int first, int second, int third) {
        int firstPalace = advanceFrom(1, first);
        int secondPalace = advanceFrom(firstPalace, second);
        int thirdPalace = advanceFrom(secondPalace, third);
        return new int[] { firstPalace, secondPalace, thirdPalace };
    }

    private static int advanceFrom(int startPalace, int count) {
        return ((startPalace - 1 + count - 1) % CYCLE_SIZE) + 1;
    }

    static final class RollingSession {
        private final RandomEngine engine;
        private boolean rolling = true;

        RollingSession(RandomEngine engine) {
            if (engine == null) {
                throw new IllegalArgumentException("engine must not be null");
            }
            this.engine = engine;
        }

        boolean isRolling() {
            return rolling;
        }

        int[] previewValues() {
            if (!rolling) {
                throw new IllegalStateException("session is stopped");
            }
            return engine.generateThree();
        }

        RollResult stop(long nanoTime, long eventTimeMillis, float x, float y) {
            if (!rolling) {
                throw new IllegalStateException("session is already stopped");
            }
            engine.mixTouchEntropy(nanoTime, eventTimeMillis, x, y);
            int[] values = engine.generateThree();
            int[] palaces = calculatePalaces(values[0], values[1], values[2]);
            rolling = false;
            return new RollResult(values, palaces);
        }

        void restart() {
            rolling = true;
        }
    }

    static final class RollResult {
        private final int[] values;
        private final int[] palaces;

        RollResult(int[] values, int[] palaces) {
            this.values = values.clone();
            this.palaces = palaces.clone();
        }

        int[] values() {
            return values.clone();
        }

        int[] palaces() {
            return palaces.clone();
        }
    }

    public static String palaceName(int palaceIndex) {
        if (palaceIndex < 1 || palaceIndex > CYCLE_SIZE) {
            throw new IllegalArgumentException("palace index must be 1..6");
        }
        return PALACE_NAMES[palaceIndex - 1];
    }
}
