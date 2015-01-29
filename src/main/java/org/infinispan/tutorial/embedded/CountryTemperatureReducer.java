package org.infinispan.tutorial.embedded;

import java.util.Iterator;

import org.infinispan.distexec.mapreduce.Reducer;

public class CountryTemperatureReducer implements Reducer<String, Float> {

   @Override
   public Float reduce(String reducedKey, Iterator<Float> i) {
      int count = 0;
      float sum = 0f;
      while(i.hasNext()) {
         ++count;
         sum += i.next();
      }
      return sum / count;
   }

}
