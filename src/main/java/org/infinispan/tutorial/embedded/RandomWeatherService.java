package org.infinispan.tutorial.embedded;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomWeatherService implements WeatherService {
   final Random random;

   public RandomWeatherService() {
      random = new Random();
   }

   @Override
   public LocationWeather getWeatherForLocation(String location) {
      try {
         TimeUnit.MILLISECONDS.sleep(200);
      } catch (InterruptedException e) {}
      String[] split = location.split(",");
      return new LocationWeather(random.nextFloat() * 20f + 5f, "sunny", split[1].trim());
   }

}
