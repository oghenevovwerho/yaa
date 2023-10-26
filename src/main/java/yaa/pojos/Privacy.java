package yaa.pojos;

public enum Privacy {
  Public, Private, Protected;

  public String stringName() {
    return switch (this) {
      case Public -> "public";
      case Private -> "private";
      default -> "protected";
    };
  }
  }
