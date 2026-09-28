package com.example.enums;

public enum Rule implements SelectableOption {
  STARTS_WITH("Starts with"),
  CONTAINS("Contains"),
  NOT_CONTAINS("Not contains"),
  ENDS_WITH("Ends with"),
  EQUALS("Equals"),
  NOT_EQUALS("Not equals");

  private final String label;

  Rule(String label) {
    this.label = label;
  }

  @Override
  public String getLabel() {
    return label;
  }
}
