package org.infinispan.tutorial.embedded;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.distexec.mapreduce.MapReduceTask;
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
      ConfigurationBuilder config = new ConfigurationBuilder();
      config.expiration().lifespan(5, TimeUnit.SECONDS)
         .clustering().cacheMode(CacheMode.DIST_SYNC)
            .hash().groups().enabled().addGrouper(new LocationWeather.LocationGrouper());
      cacheManager = new DefaultCacheManager(global.build(), config.build());
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
      MapReduceTask<String, LocationWeather, String, Float> countryTemperatureTask = new MapReduceTask<String, LocationWeather, String, Float>(cache);
      countryTemperatureTask.mappedWith(new CountryTemperatureMapper()).reducedWith(new CountryTemperatureReducer());
      Map<String, Float> averages = countryTemperatureTask.execute();
      for(Entry<String, Float> entry : averages.entrySet()) {
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
