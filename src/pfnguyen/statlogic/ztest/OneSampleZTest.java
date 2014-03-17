/**
 * Copyright 2014 Peter "Felix" Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pfnguyen.statlogic.ztest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;

import pfnguyen.statlogic.options.CalculatorOptions.Hypothesis;

public class OneSampleZTest {
    private NormalDistribution normal = new NormalDistribution();
    private BigDecimal testStatistic;
    private BigDecimal criticalRegion; // move to an if else in constructor???
    private Hypothesis hypothesis;
    private BigDecimal lowerRegion; // move to an if else in constructor???
    private BigDecimal upperRegion; // move to an if else in constructor???

    public OneSampleZTest(Hypothesis hypothesis, BigDecimal testValue,
            ArrayList<BigDecimal> x, BigDecimal stdDev, double significance) {
        getCriticalRegion(hypothesis, significance);
        calcTestStatistic(hypothesis, calcSampleMean(x), testValue, stdDev, x);
        this.hypothesis = hypothesis;
    }

    public OneSampleZTest(Hypothesis hypothesis, BigDecimal testValue,
            BigDecimal xBar, BigDecimal stdDev, int n, double significance) {
        getCriticalRegion(hypothesis, significance);
        calcTestStatistic(hypothesis, xBar, testValue, stdDev, n);
        this.hypothesis = hypothesis;
    }

    private BigDecimal getCriticalRegion(Hypothesis hypothesis,
            double significance) {
        if (hypothesis == Hypothesis.NOT_EQUAL) {
            lowerRegion = new BigDecimal(
                    normal.inverseCumulativeProbability(significance / 2));
            upperRegion = new BigDecimal(
                    normal.inverseCumulativeProbability(1 - (significance / 2)));
        } else
            criticalRegion = new BigDecimal(
                    normal.inverseCumulativeProbability(significance));
        return criticalRegion;
    }

    public boolean testHypothesis() {
        if (hypothesis == Hypothesis.LESS_THAN
                && testStatistic.compareTo(criticalRegion) < 0)
            return true;
        else if (hypothesis == Hypothesis.GREATER_THAN
                && testStatistic.compareTo(criticalRegion) > 0)
            return true;
        else if (hypothesis == Hypothesis.NOT_EQUAL
                && (testStatistic.compareTo(lowerRegion) < 0 || testStatistic
                        .compareTo(upperRegion) > 0))
            return true;
        else
            return false;
    }

    private BigDecimal calcSampleMean(ArrayList<BigDecimal> x) {
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal sampleMean;

        for (int i = 0; i < x.size(); i++) {
            sum = sum.add(x.get(i));
        }

        sampleMean = sum.divide(new BigDecimal(x.size()), 4,
                RoundingMode.HALF_UP);
        return sampleMean;
    }

    /** This version does not return. */
    private void calcTestStatistic(Hypothesis hypothesis,
            BigDecimal sampleMean, BigDecimal populationMean,
            BigDecimal stdDev, ArrayList<BigDecimal> x) {

        testStatistic = sampleMean.subtract(populationMean).divide(
                stdDev.divide(new BigDecimal(Math.sqrt(x.size())), 4,
                        RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP);
    }

    /** This version returns. Not sure which to pick. */
    private void calcTestStatistic(Hypothesis hypothesis,
            BigDecimal sampleMean, BigDecimal populationMean,
            BigDecimal stdDev, int n) {

        testStatistic = sampleMean.subtract(populationMean).divide(
                stdDev.divide(new BigDecimal(Math.sqrt(n)), 4,
                        RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP);

        System.out.println("OneSampleZTest test statistic: " + testStatistic);
    }
}
