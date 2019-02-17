package com.josephstephens.plate.calculator;

import java.util.Map;
import java.util.TreeMap;

import com.josephstephens.plate.calculator.PlateCalculator.PlateResult;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.joining;

public class PlateMessageBuilder
{
  private static final String MESSAGE_TEMPLATE = "You require %s";

  private static final String PLATE_TEMPLATE = "%d x %slb plates";

  private static final String AND = " and ";

  private static final String EMPTY = "an empty bar";

  private static final String WEIGHT_NOT_MET_MESSAGE = "Required weight could not be met. However with %s you can " +
      "reach %slbs which is %slb short of what you require";

  private final PlateCalculator plateCalculator;

  public PlateMessageBuilder(final PlateCalculator plateCalculator) {
    this.plateCalculator = checkNotNull(plateCalculator);
  }

  public String buildMessage(final double requiredWeight) {
    PlateResult result = plateCalculator.calculatePlates(requiredWeight);

    String platesMessage = buildPlateMessage(result);

    if (result.getWeightAchieved() < requiredWeight) {
      return format(WEIGHT_NOT_MET_MESSAGE, platesMessage, formatWeight(result.getWeightAchieved()),
          formatWeight(requiredWeight - result.getWeightAchieved()));
    }
    else {
      return format(MESSAGE_TEMPLATE, platesMessage);
    }
  }

  private String buildPlateMessage(final PlateResult result) {
    Map<Double, Integer> aggregatedPlates = aggregatePlates(result.getPlates());

    String platesMessage;
    if (aggregatedPlates.isEmpty()) {
      platesMessage = EMPTY;
    }
    else {
      platesMessage = aggregatedPlates.entrySet().stream()
          .map(entry -> buildPlateMessage(entry.getKey(), entry.getValue()))
          .collect(joining(AND));
    }
    return platesMessage;
  }

  private Map<Double, Integer> aggregatePlates(final double[] plates) {
    Map<Double, Integer> aggregatedPlates = new TreeMap<>(reverseOrder());

    for (double plate : plates) {
      if (aggregatedPlates.containsKey(plate)) {
        aggregatedPlates.put(plate, aggregatedPlates.get(plate) + 2);
      }
      else {
        aggregatedPlates.put(plate, 2);
      }
    }

    return aggregatedPlates;
  }

  private String buildPlateMessage(final Double weight, final Integer count) {
    return String.format(PLATE_TEMPLATE, count, formatWeight(weight));
  }

  private String formatWeight(final double weight) {
    return weight - (int) weight > 0 ? format("%.1f", weight) : format("%d", (int) weight);
  }
}
