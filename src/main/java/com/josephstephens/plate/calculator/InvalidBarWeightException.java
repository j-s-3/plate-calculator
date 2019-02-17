package com.josephstephens.plate.calculator;

public class InvalidBarWeightException
    extends RuntimeException
{
  public InvalidBarWeightException(final String message) {
    super(message);
  }
}
