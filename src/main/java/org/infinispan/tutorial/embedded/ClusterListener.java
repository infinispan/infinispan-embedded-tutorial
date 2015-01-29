package org.infinispan.tutorial.embedded;

import java.util.concurrent.CountDownLatch;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;

@Listener
public class ClusterListener {
   CountDownLatch clusterFormedLatch = new CountDownLatch(1);
   CountDownLatch shutdownLatch = new CountDownLatch(1);
   private final int expectedNodes;

   public ClusterListener(int expectedNodes) {
      this.expectedNodes = expectedNodes;
   }

   @ViewChanged
   public void viewChanged(ViewChangedEvent event) {
      System.out.printf("---- View changed: %s ----\n", event.getNewMembers());

      if (event.getCacheManager().getMembers().size() == expectedNodes) {
         clusterFormedLatch.countDown();
      } else if (event.getNewMembers().size() < event.getOldMembers().size()) {
         shutdownLatch.countDown();
      }
   }
}
