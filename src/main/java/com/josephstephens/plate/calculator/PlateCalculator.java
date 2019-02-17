package com.josephstephens.plate.calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.sort;

public class PlateCalculator
{
  static final String TOTAL_WEIGHT_TOO_LOW_MESSAGE = "Required weight %.1f is less than the bar weight " +
      "%.1f, this is not possible.";

  static final String TOTAL_WEIGHT_TOO_HIGH_MESSAGE = "Required weight %.1f is higher than the total possible weight " +
      "%.1f, this is not possible.";

  static final String EMPTY_PLATE_LIST_MESSAGE = "Plate list cannot be empty";

  static final String INVALID_PLATE_WEIGHT_MESSAGE = "%.1f is not a valid plate weight. All plate weights must be " +
      "greater than zero";

  static final String INVALID_BAR_WEIGHT_MESSAGE = "%.1f is not a valid bar weight. Bar weight must be greater than zero.";

  private static final int NUMBER_OF_PLATES_PER_BAR = 2;

  private final double[] plates;

  private final double barWeight;

  public PlateCalculator(final double[] plates, final double barWeight) {
    validatePlates(plates);
    validateBarWeight(barWeight);

    this.plates = plates;
    this.barWeight = barWeight;

    sort(plates);
  }

  public PlateResult calculatePlates(final double requiredWeight) {
    validateRequiredWeight(requiredWeight);

    double weightDifference = (requiredWeight - barWeight) / NUMBER_OF_PLATES_PER_BAR;

    List<Double> foundPlates = new ArrayList<>();

    for (int i = plates.length - 1; i >= 0; i--) {
      double plate = plates[i];

      if (weightDifference == 0.0) {
        break;
      }

      if (plate <= weightDifference) {
        foundPlates.add(plate);
        weightDifference -= plate;
      }
    }

    return buildResult(foundPlates);
  }

  private PlateResult buildResult(final List<Double> foundPlates) {
    double[] requiredPlates = foundPlates.stream().mapToDouble(Double::doubleValue).toArray();
    double plateWeightAchieved =
        (foundPlates.stream().mapToDouble(Double::doubleValue).sum() * NUMBER_OF_PLATES_PER_BAR) + barWeight;

    return new PlateResult(requiredPlates, plateWeightAchieved);
  }

  private void validatePlates(final double[] plates) {
    if (plates.length == 0) {
      throw new InvalidPlateArrayException(EMPTY_PLATE_LIST_MESSAGE);
    }

    for (double plate : plates) {
      if (plate <= 0.0) {
        throw new InvalidPlateArrayException(format(INVALID_PLATE_WEIGHT_MESSAGE, plate));
      }
    }
  }

  private void validateBarWeight(final double barWeight) {
    if (barWeight <= 0.0) {
      throw new InvalidBarWeightException(format(INVALID_BAR_WEIGHT_MESSAGE, barWeight));
    }
  }

  private void validateRequiredWeight(final double requiredWeight) {
    if (requiredWeight < barWeight) {
      throw new InvalidRequiredWeightException(format(TOTAL_WEIGHT_TOO_LOW_MESSAGE, requiredWeight, barWeight));
    }

    if (requiredWeight > barWeight + (Arrays.stream(plates).sum() * 2)) {
      throw new InvalidRequiredWeightException(format(TOTAL_WEIGHT_TOO_HIGH_MESSAGE, requiredWeight, barWeight));
    }
  }

  public static class PlateResult
  {
    private final double[] plates;

    private final double weightAchieved;

    public PlateResult(final double[] plates,
                       final double weightAchieved)
    {
      this.plates = plates;
      this.weightAchieved = weightAchieved;
    }

    public double[] getPlates() {
      return plates;
    }

    public double getWeightAchieved() {
      return weightAchieved;
    }
  }
}
