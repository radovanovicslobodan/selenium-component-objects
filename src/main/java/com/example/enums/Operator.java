package com.example.enums;

public enum Operator implements SelectableOption {
  MATCH_ANY("Match Any"),
  MATCH_ALL("Match All");

  private final String label;

  Operator(String label) {
    this.label = label;
  }

  @Override
  public String getLabel() {
    return label;
  }
}
