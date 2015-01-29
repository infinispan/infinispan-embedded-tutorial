package org.infinispan.tutorial.embedded;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class WeatherApp {

   static final String[] locations = { "Rome, Italy", "Como, Italy", "Basel, Switzerland", "Bern, Switzerland",
         "London, UK", "Newcastle, UK", "Bucarest, Romania", "Cluj-Napoca, Romania", "Ottawa, Canada",
         "Toronto, Canada", "Lisbon, Portugal", "Porto, Portugal", "Raleigh, USA", "Washington, USA" };
   private final EmbeddedCacheManager cacheManager;
   private final WeatherService weatherService;
   private Cache<String, LocationWeather> cache;

   public WeatherApp() throws InterruptedException {
      ConfigurationBuilder config = new ConfigurationBuilder();
      config.expiration().lifespan(5, TimeUnit.SECONDS);
      cacheManager = new DefaultCacheManager(config.build());
      cache = cacheManager.getCache();
      weatherService = initWeatherService(cache);
   }

   private WeatherService initWeatherService(Cache<String,LocationWeather> cache) {
      if (System.getProperty("random.weather.service") != null) {
         return new RandomWeatherService(cache);
      } else {
         return new OpenWeatherMapService(cache);
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

      TimeUnit.SECONDS.sleep(5);

      app.fetchWeather();

      app.shutdown();
   }

}
