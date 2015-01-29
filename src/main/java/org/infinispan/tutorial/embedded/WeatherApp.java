package org.infinispan.tutorial.embedded;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class WeatherApp {

   static final String[] locations = { "Rome, Italy", "Como, Italy", "Basel, Switzerland", "Bern, Switzerland",
         "London, UK", "Newcastle, UK", "Bucarest, Romania", "Cluj-Napoca, Romania", "Ottawa, Canada",
         "Toronto, Canada", "Lisbon, Portugal", "Porto, Portugal", "Raleigh, USA", "Washington, USA" };
   private final EmbeddedCacheManager cacheManager;
   private final WeatherService weatherService;
   private Cache<String, LocationWeather> cache;
   private final ClusterListener listener;

   public WeatherApp() throws InterruptedException {
      GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
      global.transport().clusterName("WeatherApp");
      cacheManager = new DefaultCacheManager(global.build());
      listener = new ClusterListener(2);
      cacheManager.addListener(listener);
      ConfigurationBuilder config = new ConfigurationBuilder();
      config
          .expiration().lifespan(5, TimeUnit.SECONDS)
          .clustering().cacheMode(CacheMode.DIST_SYNC);
      cacheManager.defineConfiguration("weather", config.build());
      cache = cacheManager.getCache("weather");
      weatherService = initWeatherService(cache);

      System.out.println("---- Waiting for cluster to form ----");
      listener.clusterFormedLatch.await();
   }

   private WeatherService initWeatherService(Cache<String,LocationWeather> cache) {
      String apiKey = System.getenv("OWMAPIKEY");
      if (apiKey == null) {
         System.out.println("WARNING: OWMAPIKEY environment variable not set, using the RandomWeatherService.");
         return new RandomWeatherService(cache);
      } else {
         return new OpenWeatherMapService(apiKey, cache);
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

   public void shutdown() throws InterruptedException {
      if (!cacheManager.isCoordinator()) {
         listener.shutdownLatch.await();
      }
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
