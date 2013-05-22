package org.plos.messageled.util.hidlyled;

public enum TRANSITIONS {
  CYCLIC("A"), IMMEDIATE("B"), OPEN_FROM_RT("C"), OPEN_FROM_LT("D"), OPEN_FROM_CTR("E"),
    OPEN_TO_CTR("F"), COVER_FROM_CTR("G"), COVER_FROM_RT("H"), COVER_FROM_LT("I"),
    COVER_TO_CTR("J"), SCROLL_UP("K"), SCROLL_DOWN("L"), INTLCE_TO_CTR("M"), INTLCE_CVR("N"),
    COVER_UP("O"), COVER_DOWN("P"), SCAN_LINE("Q"), EXPLODE("R"), PAC_MAN("S"), FALL_AND_STACK("T"), SHOOT("U"),
    FLASH("V"), RANDOM("W"), SLIDE_IN("X");

  public static TRANSITIONS[] preferred = { COVER_FROM_CTR, COVER_FROM_RT, COVER_FROM_LT, COVER_TO_CTR,
    SCROLL_UP, SCROLL_DOWN, INTLCE_CVR, COVER_UP, COVER_DOWN, EXPLODE, PAC_MAN, FALL_AND_STACK };

  //Too jittery: CYCLIC, OPEN_FROM_RT, OPEN_FROM_LT, OPEN_FROM_CTR, OPEN_TO_CTR, INTLCE_TO_CTR
  //Annoying: FLASH
  //Takes too long, but cool: SCAN_LINE, SHOOT, SLIDE_IN

  private final String stringValue;

  private TRANSITIONS(final String s) { stringValue = s; }

  public String toString() { return stringValue; }

  public String getName() {
    return super.name().toLowerCase().replace("_", "");
  }
}
