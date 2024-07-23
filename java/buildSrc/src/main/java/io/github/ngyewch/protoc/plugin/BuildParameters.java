package io.github.ngyewch.protoc.plugin;

public class BuildParameters {
  private final String goos;
  private final String goarch;
  private final String classifier;

  public BuildParameters(String goos, String goarch, String classifier) {
    super();

    this.goos = goos;
    this.goarch = goarch;
    this.classifier = classifier;
  }

  public String getGoos() {
    return goos;
  }

  public String getGoarch() {
    return goarch;
  }

  public String getClassifier() {
    return classifier;
  }
}
