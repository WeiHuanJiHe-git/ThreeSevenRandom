package com.fonuhuo.sevenrandom;

import java.util.Random;

public final class RandomEngineTest {
    public static void main(String[] args) {
        testTenProducesOneAndFour();
        testSevenProducesOneAndOne();
        testTwelveMapsZeroRemainderToSix();
        testSixDoesNotProduceDerivedValues();
        testThreeDoesNotProduceDerivedValues();
        testConfiguredRangeIsOneToNineHundredNinetyNine();
        testGeneratedValuesStayWithinConfiguredRange();
        System.out.println("RandomEngineTest: ALL TESTS PASSED");
    }

    private static void testTenProducesOneAndFour() {
        RandomEngine.Analysis result = RandomEngine.analyze(10);
        require(result.hasDivision(), "10 should be divided");
        require(result.getQuotient() == 1, "10/6 quotient should be 1");
        require(result.getRemainder() == 4, "10 should map to cycle remainder 4");
    }

    private static void testSevenProducesOneAndOne() {
        RandomEngine.Analysis result = RandomEngine.analyze(7);
        require(result.hasDivision(), "7 is greater than 6");
        require(result.getQuotient() == 1, "7/6 quotient should be 1");
        require(result.getRemainder() == 1, "7 should map to cycle remainder 1");
    }

    private static void testTwelveMapsZeroRemainderToSix() {
        RandomEngine.Analysis result = RandomEngine.analyze(12);
        require(result.hasDivision(), "12 should be divided");
        require(result.getQuotient() == 2, "12/6 quotient should be 2");
        require(result.getRemainder() == 6, "a zero mathematical remainder must map to 6");
    }

    private static void testSixDoesNotProduceDerivedValues() {
        RandomEngine.Analysis result = RandomEngine.analyze(6);
        require(!result.hasDivision(), "6 is not greater than 6");
    }

    private static void testThreeDoesNotProduceDerivedValues() {
        RandomEngine.Analysis result = RandomEngine.analyze(3);
        require(!result.hasDivision(), "3 is not greater than 6");
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

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
