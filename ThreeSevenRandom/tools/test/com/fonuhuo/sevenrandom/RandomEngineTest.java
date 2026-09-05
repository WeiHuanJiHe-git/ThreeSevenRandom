package com.fonuhuo.sevenrandom;

import java.util.Random;

public final class RandomEngineTest {
    public static void main(String[] args) {
        testTenProducesOneAndThree();
        testEightProducesOneAndOne();
        testFourteenProducesTwoAndZero();
        testSevenDoesNotProduceDerivedValues();
        testThreeDoesNotProduceDerivedValues();
        testConfiguredRangeIsOneToNineHundredNinetyNine();
        testGeneratedValuesStayWithinConfiguredRange();
        System.out.println("RandomEngineTest: ALL TESTS PASSED");
    }

    private static void testTenProducesOneAndThree() {
        RandomEngine.Analysis result = RandomEngine.analyze(10);
        require(result.hasDivision(), "10 should be divided");
        require(result.getQuotient() == 1, "10/7 quotient should be 1");
        require(result.getRemainder() == 3, "10%7 remainder should be 3");
    }

    private static void testEightProducesOneAndOne() {
        RandomEngine.Analysis result = RandomEngine.analyze(8);
        require(result.hasDivision(), "8 should be divided");
        require(result.getQuotient() == 1, "8/7 quotient should be 1");
        require(result.getRemainder() == 1, "8%7 remainder should be 1");
    }

    private static void testFourteenProducesTwoAndZero() {
        RandomEngine.Analysis result = RandomEngine.analyze(14);
        require(result.hasDivision(), "14 should be divided");
        require(result.getQuotient() == 2, "14/7 quotient should be 2");
        require(result.getRemainder() == 0, "14%7 remainder should be 0");
    }

    private static void testSevenDoesNotProduceDerivedValues() {
        RandomEngine.Analysis result = RandomEngine.analyze(7);
        require(!result.hasDivision(), "7 is not greater than 7");
    }

    private static void testThreeDoesNotProduceDerivedValues() {
        RandomEngine.Analysis result = RandomEngine.analyze(3);
        require(!result.hasDivision(), "3 is not greater than 7");
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
