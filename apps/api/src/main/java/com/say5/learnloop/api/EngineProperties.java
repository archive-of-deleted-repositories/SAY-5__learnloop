package com.say5.learnloop.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Tunables for the adaptive engine and the selector. */
@ConfigurationProperties(prefix = "learnloop")
public record EngineProperties(Engine engine, Selection selection) {

  public record Engine(double kFactor, double defaultRating) {}

  public record Selection(double targetSuccess, int cooldown) {}

  public double kFactor() {
    return engine.kFactor();
  }

  public double defaultRating() {
    return engine.defaultRating();
  }

  public double targetSuccess() {
    return selection.targetSuccess();
  }

  public int cooldown() {
    return selection.cooldown();
  }
}
