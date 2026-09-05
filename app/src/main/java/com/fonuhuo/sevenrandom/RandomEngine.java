package com.fonuhuo.sevenrandom;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Generates three unbiased random integers and maps values through a six-position cycle.
 * Default production randomness uses Android/Java's cryptographically strong SecureRandom.
 */
public final class RandomEngine {
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 999;
    public static final int CYCLE_SIZE = 6;

    private final Random random;

    public RandomEngine() {
        this(new SecureRandom());
    }

    RandomEngine(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        this.random = random;
    }

    public int[] generateThree() {
        return new int[] { nextValue(), nextValue(), nextValue() };
    }

    private int nextValue() {
        return random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE;
    }

    public static Analysis analyze(int value) {
        if (value <= CYCLE_SIZE) {
            return Analysis.notDivided();
        }
        int mathematicalRemainder = value % CYCLE_SIZE;
        int cycleRemainder = mathematicalRemainder == 0 ? CYCLE_SIZE : mathematicalRemainder;
        return Analysis.divided(value / CYCLE_SIZE, cycleRemainder);
    }

    public static final class Analysis {
        private final boolean hasDivision;
        private final int quotient;
        private final int remainder;

        private Analysis(boolean hasDivision, int quotient, int remainder) {
            this.hasDivision = hasDivision;
            this.quotient = quotient;
            this.remainder = remainder;
        }

        static Analysis notDivided() {
            return new Analysis(false, 0, 0);
        }

        static Analysis divided(int quotient, int remainder) {
            return new Analysis(true, quotient, remainder);
        }

        public boolean hasDivision() {
            return hasDivision;
        }

        public int getQuotient() {
            return quotient;
        }

        public int getRemainder() {
            return remainder;
        }
    }
}
