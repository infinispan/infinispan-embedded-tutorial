package org.infinispan.tutorial.embedded;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.infinispan.commons.marshall.Externalizer;
import org.infinispan.commons.marshall.SerializeWith;
import org.infinispan.distribution.group.Grouper;

@SerializeWith(LocationWeather.LWExternalizer.class)
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

   public static class LWExternalizer implements Externalizer<LocationWeather> {

      @Override
      public void writeObject(ObjectOutput output, LocationWeather object) throws IOException {
         output.writeFloat(object.temperature);
         output.writeUTF(object.conditions);
         output.writeUTF(object.country);
      }

      @Override
      public LocationWeather readObject(ObjectInput input) throws IOException, ClassNotFoundException {
         float temperature = input.readFloat();
         String conditions = input.readUTF();
         String country = input.readUTF();
         return new LocationWeather(temperature, conditions, country);
      }

   }
}
