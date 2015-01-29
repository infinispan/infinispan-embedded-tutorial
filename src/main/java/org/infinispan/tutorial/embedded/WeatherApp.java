package org.infinispan.tutorial.embedded;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.stream.CacheCollectors;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.groupingBy;

public class WeatherApp {

   static final String[] locations = { "Rome, Italy", "Como, Italy", "Basel, Switzerland", "Bern, Switzerland",
         "London, UK", "Newcastle, UK", "Bucarest, Romania", "Cluj-Napoca, Romania", "Ottawa, Canada",
         "Toronto, Canada", "Lisbon, Portugal", "Porto, Portugal", "Raleigh, USA", "Washington, USA" };
   private final EmbeddedCacheManager cacheManager;
   private final WeatherService weatherService;
   private Cache<String, LocationWeather> cache;
   private final ClusterListener listener;

   public WeatherApp() throws InterruptedException, IOException {
      cacheManager = new DefaultCacheManager(WeatherApp.class.getResourceAsStream("/weatherapp-infinispan.xml"));
      listener = new ClusterListener(2);
      cacheManager.addListener(listener);
      cache = cacheManager.getCache();
      cache.addListener(new CacheListener());
      weatherService = initWeatherService(cache);

      System.out.println("---- Waiting for cluster to form ----");
      listener.clusterFormedLatch.await();
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

   public void computeCountryAverages() {
      System.out.println("---- Average country temperatures ----");
      Map<String, Double> averages = cache.entrySet().stream()
              .collect(CacheCollectors.serializableCollector(() -> groupingBy(Map.Entry::getKey,
                      averagingDouble(e -> e.getValue().temperature))));
      for(Entry<String, Double> entry : averages.entrySet()) {
         System.out.printf("Average temperature in %s is %.1f° C\n", entry.getKey(), entry.getValue());
      }
   }

   public void shutdown() throws InterruptedException {
      if (!cacheManager.isCoordinator()) {
         listener.shutdownLatch.await();
      }
      cacheManager.stop();
   }

   public static void main(String[] args) throws Exception {
      WeatherApp app = new WeatherApp();

      if (app.cacheManager.isCoordinator()) {

         app.fetchWeather();

         app.fetchWeather();

         TimeUnit.SECONDS.sleep(5);

         app.fetchWeather();

         app.computeCountryAverages();
      }

      app.shutdown();
   }

}
