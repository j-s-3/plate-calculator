package com.josephstephens.plate.calculator;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class PlateMessageBuilderTest
{
  private static final double[] PLATES = {2.5, 5.0, 5.0, 10.0, 10.0, 25.0, 45.0};

  private static final double BAR_WEIGHT = 45.0;

  private PlateMessageBuilder underTest;

  @Before
  public void setup() throws Exception {
    underTest = new PlateMessageBuilder(new PlateCalculator(PLATES, BAR_WEIGHT));
  }

  @Test(expected = NullPointerException.class)
  public void throwExceptionWhenNullPlateCalculatorPassed() throws Exception {
    new PlateMessageBuilder(null);
  }

  @Test
  public void buildMessage() throws Exception {
    double requiredWeight = BAR_WEIGHT + 45.0;

    String message = underTest.buildMessage(requiredWeight);

    assertThat(message, is(equalTo("You require 4 x 10lb plates and 2 x 2.5lb plates")));
  }

  @Test
  public void buildMessageInOrder() throws Exception {
    double requiredWeight = BAR_WEIGHT + 100.0;

    String message = underTest.buildMessage(requiredWeight);

    assertThat(message, is(equalTo("You require 2 x 45lb plates and 2 x 5lb plates")));
  }

  @Test
  public void emptyBar() throws Exception {
    double requiredWeight = BAR_WEIGHT;

    String message = underTest.buildMessage(requiredWeight);

    assertThat(message, is(equalTo("You require an empty bar")));
  }

  @Test
  public void messageThatPlateWeightNotMet() throws Exception {
    double requiredWeight = BAR_WEIGHT + 101.0;

    String message = underTest.buildMessage(requiredWeight);

    assertThat(message, is(equalTo("Required weight could not be met. However with 2 x 45lb plates and 2 x " +
        "5lb plates you can reach 145lbs which is 1lb short of what you require")));
  }

  @Test
  public void messageThatPlateWeightNotMetDecimal() throws Exception {
    double requiredWeight = BAR_WEIGHT + 101.5;

    String message = underTest.buildMessage(requiredWeight);

    assertThat(message, is(equalTo("Required weight could not be met. However with 2 x 45lb plates and 2 x " +
        "5lb plates you can reach 145lbs which is 1.5lb short of what you require")));
  }

  @Test
  public void messageThatPlateWeightNotMetEmptyBar() throws Exception {
    double requiredWeight = BAR_WEIGHT + 1.5;

    String message = underTest.buildMessage(requiredWeight);

    assertThat(message, is(equalTo("Required weight could not be met. However with an empty bar you can " +
        "reach 45lbs which is 1.5lb short of what you require")));
  }

  @Test
  public void plateCalculator() throws Exception {
    System.out.println(underTest.buildMessage(250.0));
  }
}
