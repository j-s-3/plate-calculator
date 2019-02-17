package com.josephstephens.plate.calculator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.josephstephens.plate.calculator.PlateCalculator.PlateResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.josephstephens.plate.calculator.PlateCalculator.EMPTY_PLATE_LIST_MESSAGE;
import static com.josephstephens.plate.calculator.PlateCalculator.INVALID_BAR_WEIGHT_MESSAGE;
import static com.josephstephens.plate.calculator.PlateCalculator.INVALID_PLATE_WEIGHT_MESSAGE;
import static com.josephstephens.plate.calculator.PlateCalculator.TOTAL_WEIGHT_TOO_HIGH_MESSAGE;
import static com.josephstephens.plate.calculator.PlateCalculator.TOTAL_WEIGHT_TOO_LOW_MESSAGE;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class PlateCalculatorTest
{
  private static final double[] PLATES = {2.5, 5.0, 5.0, 10.0, 10.0, 25.0, 45.0};

  private static final double BAR_WEIGHT = 45.0;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  private PlateCalculator underTest;

  @Before
  public void setup() throws Exception {
    underTest = new PlateCalculator(PLATES, BAR_WEIGHT);
  }

  @Test
  public void weights() throws Exception {
    Map<Double, Double[]> weights = new HashMap<>();
    weights.put(BAR_WEIGHT, new Double[]{});
    weights.put(BAR_WEIGHT + 5.0, new Double[]{2.5});
    weights.put(BAR_WEIGHT + 10.0, new Double[]{5.0});
    weights.put(BAR_WEIGHT + 15.0, new Double[]{5.0, 2.5});
    weights.put(BAR_WEIGHT + 20.0, new Double[]{10.0});
    weights.put(BAR_WEIGHT + 25.0, new Double[]{10.0, 2.5});
    weights.put(BAR_WEIGHT + 30.0, new Double[]{10.0, 5.0});
    weights.put(BAR_WEIGHT + 35.0, new Double[]{10.0, 5.0, 2.5});
    weights.put(BAR_WEIGHT + 40.0, new Double[]{10.0, 10.0});
    weights.put(BAR_WEIGHT + 45.0, new Double[]{10.0, 10.0, 2.5});
    weights.put(BAR_WEIGHT + 50.0, new Double[]{25.0});
    weights.put(BAR_WEIGHT + (Arrays.stream(PLATES).sum() * 2), new Double[]{45.0, 25.0, 10.0, 10.0, 5.0, 5.0, 2.5});

    for (Entry<Double, Double[]> entry : weights.entrySet()) {
      double[] requiredPlates = underTest.calculatePlates(entry.getKey()).getPlates();

      double[] expectedPlates = stream(entry.getValue()).mapToDouble(Double::doubleValue).toArray();

      assertThat(requiredPlates, is(equalTo(expectedPlates)));
    }
  }

  @Test
  public void handleWeightsPassedOutOfOrder() throws Exception {
    underTest = new PlateCalculator(new double[]{25.0, 2.5, 5.0, 5.0, 10.0, 10.0, 45.0}, BAR_WEIGHT);

    double[] requiredPlates = underTest.calculatePlates(BAR_WEIGHT + 50.0).getPlates();

    assertThat(requiredPlates, is(equalTo(new double[]{25.0})));
  }

  @Test
  public void alwaysUseSmallestNumberOfPlates() throws Exception {
    // Assuming 45lb bar then 95lbs could be bar + 2 x 25lbs OR it could be bar + 2 x 10lbs + 2 x lbs
    // We want the smallest number of plates possible

    double requiredWeight = BAR_WEIGHT + 50.0;

    double[] requiredPlates = underTest.calculatePlates(requiredWeight).getPlates();

    assertThat(requiredPlates, is(equalTo(new double[]{25.0})));
  }

  @Test
  public void throwExceptionWhenRequiredWeightLessThanBar() throws Exception {
    double requiredWeight = BAR_WEIGHT - 1.0;

    expectedEx.expect(InvalidRequiredWeightException.class);
    expectedEx.expectMessage(format(TOTAL_WEIGHT_TOO_LOW_MESSAGE, requiredWeight, BAR_WEIGHT));

    underTest.calculatePlates(requiredWeight);
  }

  @Test
  public void throwExceptionWhenRequiredWeightMoreThanTotalPossible() throws Exception {
    double requiredWeight = BAR_WEIGHT + (Arrays.stream(PLATES).sum() * 2) + 1.0;

    expectedEx.expect(InvalidRequiredWeightException.class);
    expectedEx.expectMessage(format(TOTAL_WEIGHT_TOO_HIGH_MESSAGE, requiredWeight, BAR_WEIGHT));

    underTest.calculatePlates(requiredWeight);
  }

  @Test
  public void throwExceptionWhenEmptyPlateListPassed() throws Exception {
    expectedEx.expect(InvalidPlateArrayException.class);
    expectedEx.expectMessage(EMPTY_PLATE_LIST_MESSAGE);

    underTest = new PlateCalculator(new double[]{}, BAR_WEIGHT);
  }

  @Test
  public void throwExceptionWhenNegativePlateWeightPassed() throws Exception {
    expectedEx.expect(InvalidPlateArrayException.class);
    expectedEx.expectMessage(String.format(INVALID_PLATE_WEIGHT_MESSAGE, -1.0));

    underTest = new PlateCalculator(new double[]{-1.0}, BAR_WEIGHT);
  }

  @Test
  public void throwExceptionWhenZeroPlateWeightPassed() throws Exception {
    expectedEx.expect(InvalidPlateArrayException.class);
    expectedEx.expectMessage(String.format(INVALID_PLATE_WEIGHT_MESSAGE, 0.0));

    underTest = new PlateCalculator(new double[]{0.0}, BAR_WEIGHT);
  }

  @Test
  public void throwExceptionWhenNegativeBarWeightPassed() throws Exception {
    double barWeight = -1.0;

    expectedEx.expect(InvalidBarWeightException.class);
    expectedEx.expectMessage(format(INVALID_BAR_WEIGHT_MESSAGE, barWeight));

    underTest = new PlateCalculator(PLATES, barWeight);
  }

  @Test
  public void throwExceptionWhenZeroBarWeightPassed() throws Exception {
    double barWeight = 0.0;

    expectedEx.expect(InvalidBarWeightException.class);
    expectedEx.expectMessage(format(INVALID_BAR_WEIGHT_MESSAGE, barWeight));

    underTest = new PlateCalculator(PLATES, barWeight);
  }

  @Test
  public void returnClosestPlatesWhenWeightNotPossible() throws Exception {
    double requiredWeight = BAR_WEIGHT + 6.0; // there are no 1.0lb plates in the passed collection

    PlateResult result = underTest.calculatePlates(requiredWeight);

    assertThat(result.getPlates(), is(equalTo(new double[]{2.5})));
    assertThat(result.getWeightAchieved(), is(equalTo(BAR_WEIGHT + 5.0)));
  }
}
