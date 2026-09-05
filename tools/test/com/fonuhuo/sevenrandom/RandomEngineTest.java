package com.fonuhuo.sevenrandom;

import java.util.Random;

public final class RandomEngineTest {
    public static void main(String[] args) {
        testTenThreeEightFollowsContinuousSixPalaceCounting();
        testEachStageCountsCurrentPalaceAsOne();
        testExactMultiplesWrapToSixthPalace();
        testPalaceNamesMatchStandardOrder();
        testConfiguredRangeIsOneToNineHundredNinetyNine();
        testGeneratedValuesStayWithinConfiguredRange();
        testTouchEntropyChangesWhenTouchInputsChange();
        testRollingSessionUsesFreshSampleWhenStopped();
        System.out.println("RandomEngineTest: ALL TESTS PASSED");
    }

    private static void testTenThreeEightFollowsContinuousSixPalaceCounting() {
        int[] palaces = RandomEngine.calculatePalaces(10, 3, 8);
        require(palaces.length == 3, "must return exactly three palace positions");
        require(palaces[0] == 4, "10 from Da An should land on Chi Kou (4)");
        require(palaces[1] == 6, "3 from Chi Kou should land on Kong Wang (6)");
        require(palaces[2] == 1, "8 from Kong Wang should land on Da An (1)");
    }

    private static void testEachStageCountsCurrentPalaceAsOne() {
        int[] palaces = RandomEngine.calculatePalaces(1, 1, 1);
        require(palaces[0] == 1, "first count of 1 must stay on Da An");
        require(palaces[1] == 1, "second count of 1 must stay on current palace");
        require(palaces[2] == 1, "third count of 1 must stay on current palace");
    }

    private static void testExactMultiplesWrapToSixthPalace() {
        int[] palaces = RandomEngine.calculatePalaces(6, 1, 1);
        require(palaces[0] == 6, "6 from Da An must land on Kong Wang, not zero");
        require(palaces[1] == 6, "counting 1 from Kong Wang must remain Kong Wang");
        require(palaces[2] == 6, "counting 1 again must remain Kong Wang");
    }

    private static void testPalaceNamesMatchStandardOrder() {
        String[] expected = {"大安", "留连", "速喜", "赤口", "小吉", "空亡"};
        for (int i = 1; i <= expected.length; i++) {
            require(expected[i - 1].equals(RandomEngine.palaceName(i)),
                    "palace " + i + " name mismatch");
        }
    }

    private static void testConfiguredRangeIsOneToNineHundredNinetyNine() {
        require(RandomEngine.MIN_VALUE == 1, "minimum must be 1");
        require(RandomEngine.MAX_VALUE == 999, "maximum must be 999");
    }

    private static void testGeneratedValuesStayWithinConfiguredRange() {
        RandomEngine engine = new RandomEngine(new Random(123456789L));
        for (int i = 0; i < 10000; i++) {
            int[] values = engine.generateThree();
            require(values.length == 3, "must generate exactly three values");
            for (int value : values) {
                require(value >= RandomEngine.MIN_VALUE, "value below minimum");
                require(value <= RandomEngine.MAX_VALUE, "value above maximum");
            }
        }
    }

    private static void testTouchEntropyChangesWhenTouchInputsChange() {
        byte[] base = RandomEngine.buildTouchEntropy(100L, 200L, 12.5f, 30.25f);
        byte[] timeChanged = RandomEngine.buildTouchEntropy(101L, 200L, 12.5f, 30.25f);
        byte[] eventChanged = RandomEngine.buildTouchEntropy(100L, 201L, 12.5f, 30.25f);
        byte[] xChanged = RandomEngine.buildTouchEntropy(100L, 200L, 12.6f, 30.25f);
        byte[] yChanged = RandomEngine.buildTouchEntropy(100L, 200L, 12.5f, 30.35f);

        require(base.length >= 24, "touch entropy should preserve all input material");
        require(!sameBytes(base, timeChanged), "nano time must affect touch entropy");
        require(!sameBytes(base, eventChanged), "event time must affect touch entropy");
        require(!sameBytes(base, xChanged), "touch X must affect touch entropy");
        require(!sameBytes(base, yChanged), "touch Y must affect touch entropy");
    }

    private static boolean sameBytes(byte[] left, byte[] right) {
        if (left.length != right.length) {
            return false;
        }
        for (int i = 0; i < left.length; i++) {
            if (left[i] != right[i]) {
                return false;
            }
        }
        return true;
    }

    private static void testRollingSessionUsesFreshSampleWhenStopped() {
        SequenceRandom source = new SequenceRandom(0, 1, 2, 9, 2, 7, 4, 5, 6);
        RandomEngine engine = new RandomEngine(source);
        RandomEngine.RollingSession session = new RandomEngine.RollingSession(engine);

        int[] preview = session.previewValues();
        require(preview[0] == 1 && preview[1] == 2 && preview[2] == 3,
                "preview must use the first random sample");

        RandomEngine.RollResult result = session.stop(100L, 200L, 12.5f, 30.25f);
        int[] values = result.values();
        int[] palaces = result.palaces();
        require(values[0] == 10 && values[1] == 3 && values[2] == 8,
                "stopping must take a fresh final sample, not freeze the preview frame");
        require(palaces[0] == 4 && palaces[1] == 6 && palaces[2] == 1,
                "fresh final sample must use continuous six-palace counting");
        require(!session.isRolling(), "session must be stopped after finalizing");

        session.restart();
        require(session.isRolling(), "restart must resume rolling");
        int[] restartedPreview = session.previewValues();
        require(restartedPreview[0] == 5 && restartedPreview[1] == 6 && restartedPreview[2] == 7,
                "restart must continue with fresh preview values");
    }

    private static final class SequenceRandom extends Random {
        private final int[] values;
        private int index;

        SequenceRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            if (index >= values.length) {
                throw new AssertionError("sequence random exhausted");
            }
            int value = values[index++];
            if (value < 0 || value >= bound) {
                throw new AssertionError("sequence value out of bound: " + value + " / " + bound);
            }
            return value;
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
