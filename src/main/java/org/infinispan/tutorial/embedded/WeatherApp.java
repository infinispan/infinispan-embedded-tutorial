package org.infinispan.tutorial.embedded;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class WeatherApp {

   static final String[] locations = { "Rome, Italy", "Como, Italy", "Basel, Switzerland", "Bern, Switzerland",
         "London, UK", "Newcastle, UK", "Bucarest, Romania", "Cluj-Napoca, Romania", "Ottawa, Canada",
         "Toronto, Canada", "Lisbon, Portugal", "Porto, Portugal", "Raleigh, USA", "Washington, USA" };
   private final EmbeddedCacheManager cacheManager;
   private final WeatherService weatherService;

   public WeatherApp() throws InterruptedException {
      cacheManager = new DefaultCacheManager();
      weatherService = initWeatherService();
   }

   private WeatherService initWeatherService() {
      String apiKey = System.getenv("OWMAPIKEY");
      if (apiKey == null) {
         System.out.println("WARNING: OWMAPIKEY environment variable not set, using the RandomWeatherService.");
         return new RandomWeatherService();
      } else {
         return new OpenWeatherMapService(apiKey);
      }
   }

   public void fetchWeather() {
      System.out.println("---- Fetching weather information ----");
      long start = System.currentTimeMillis();
      for (String location : locations) {
         LocationWeather weather = weatherService.getWeatherForLocation(location);
         System.out.printf("%s - %s\n", location, weather);
      }
      System.out.printf("---- Fetched in %dms ----\n", System.currentTimeMillis() - start);
   }

   public void shutdown() {
      cacheManager.stop();
   }

   public static void main(String[] args) throws Exception {
      WeatherApp app = new WeatherApp();

      app.fetchWeather();

      app.fetchWeather();

      app.shutdown();
   }

}
