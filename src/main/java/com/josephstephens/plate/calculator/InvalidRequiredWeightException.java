package com.josephstephens.plate.calculator;

public class InvalidRequiredWeightException
    extends RuntimeException
{
  public InvalidRequiredWeightException(final String message) {
    super(message);
  }
}
