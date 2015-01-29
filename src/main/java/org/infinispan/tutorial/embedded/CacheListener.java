package org.infinispan.tutorial.embedded;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;

@Listener(clustered = true)
public class CacheListener {

   @CacheEntryCreated
   public void entryCreated(CacheEntryCreatedEvent<String, LocationWeather> event) {
      if (!event.isOriginLocal()) {
         System.out.printf("-- Entry for %s modified by another node in the cluster\n", event.getKey());
      }
   }
}
