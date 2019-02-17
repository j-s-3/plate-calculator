package com.josephstephens.plate.calculator;

public class InvalidPlateArrayException
    extends RuntimeException
{
  public InvalidPlateArrayException(final String message) {
    super(message);
  }
}
