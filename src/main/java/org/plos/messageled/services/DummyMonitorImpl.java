package org.plos.messageled.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DummyMonitorImpl implements MessageMonitor {
  private static final Logger log = LoggerFactory.getLogger(DummyMonitorImpl.class);
  private SecureRandom random = new SecureRandom();
  private MessageQueue queue;

  public void start() {
    log.info("Starting up DummyMonitorImpl thread.");

    (new Thread(new DummyMessage())).start();
  }

  public void setMessageQueue(MessageQueue queue) {
    this.queue = queue;
  }

  private class DummyMessage implements Runnable {
    public void run() {
      boolean running = true;

      while(running) {
        log.debug("Dummy Message running");

        String s = new BigInteger(130, random).toString(32);

        s = s.substring(0, 6);

        log.debug("Dummy Message created {}", s);

        queue.put("fuck");
        queue.put(s);

        try {
          //Wait 2 seconds, create another
          log.debug("Sleeping for 5 seconds.");
          Thread.sleep(5000);
        } catch(InterruptedException ex) {
          running = false;
        }
      }
    }
  }

}


