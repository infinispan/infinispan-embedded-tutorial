package org.infinispan.tutorial.embedded;

import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.distexec.mapreduce.Mapper;

public class CountryTemperatureMapper implements Mapper<String, LocationWeather, String, Float> {

   @Override
   public void map(String key, LocationWeather value, Collector<String, Float> collector) {
      collector.emit(value.country, value.temperature);
   }

}
