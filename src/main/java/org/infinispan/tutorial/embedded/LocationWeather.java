package org.infinispan.tutorial.embedded;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class LocationWeather {

   @ProtoField(number = 1, defaultValue = "0.0")
   final float temperature;

   @ProtoField(number = 2)
   final String conditions;

   @ProtoField(number = 3)
   final String country;

   @ProtoFactory
   public LocationWeather(float temperature, String conditions, String country) {
      this.temperature = temperature;
      this.conditions = conditions;
      this.country = country;
   }

   @Override
   public String toString() {
      return String.format("Temperature: %.1f° C, Conditions: %s", temperature, conditions);
   }
}

