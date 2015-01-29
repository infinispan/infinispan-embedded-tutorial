package org.infinispan.tutorial.embedded;

import java.io.Serializable;

import org.infinispan.distribution.group.Grouper;

public class LocationWeather implements Serializable {
   final float temperature;
   final String conditions;
   final String country;

   public LocationWeather(float temperature, String conditions, String country) {
      this.temperature = temperature;
      this.conditions = conditions;
      this.country = country;
   }

   @Override
   public String toString() {
      return String.format("Temperature: %.1f° C, Conditions: %s", temperature, conditions);
   }

   public static class LocationGrouper implements Grouper<String> {

      @Override
      public String computeGroup(String key, String group) {
         return key.split(",")[1].trim();
      }

      @Override
      public Class<String> getKeyType() {
         return String.class;
      }
   }

}
