package com.starter.fullstack.api;

import lombok.Getter;

/**
 * Unit of Measurement.
 */
public enum UnitOfMeasurement {
  CUP("c"),
  GALLON("gal"),
  OUNCE("oz"),
  PINT("pt"),
  POUND("lb"),
  QUART("qt");

  @Getter
  private final String abbreviation;

  /**
   * Default Constructor.
   * @param abbreviation abbreviation.
   */
  UnitOfMeasurement(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  /**
   * Check if string is an enum value
   * @param test value to check
   * @return true if the value is contained in the enum
   *         false otherwise
   */
  public static boolean contains(String test) {

    for (UnitOfMeasurement val : UnitOfMeasurement.values()) {
      if (val.name().equals(test)) {
        return true;
      }
    }

    return false;
  } 

}