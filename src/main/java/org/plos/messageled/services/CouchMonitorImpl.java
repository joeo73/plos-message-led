package org.plos.messageled.services;

import org.plos.messageled.Config;
import org.plos.messageled.util.log.CouchDB;
import org.plos.messageled.util.log.SolrMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CouchMonitorImpl implements MessageMonitor {
  private static final Logger log = LoggerFactory.getLogger(CouchMonitorImpl.class);

  private CouchDB cc;
  private MessageQueue queue;
  private Config config;

  public void start() {
    log.info("Starting up CouchMonitorImpl thread.");

    cc = new CouchDB(config.getCouchHost(), config.getCouchDB());

    (new Thread(new CouchMonitor())).start();
  }

  public void setMessageQueue(MessageQueue queue) {
    this.queue = queue;
  }

  public void setConfig(Config config) {
    this.config = config;
  }

  private class CouchMonitor implements Runnable {
    public void run() {
      boolean running = true;

      log.debug("CouchMonitor running");

      while(running) {
        log.debug("Calling Couch");

        String[] docIds = cc.getDocumentList();

        for(String id : docIds) {
          log.debug("Fetching entry for ID: {}", id);

          Map logEntry = cc.getDocument(id);
          String message = (String)logEntry.get("message");
          log.debug("Log message: {}", message);

          String parsedMessage = SolrMessage.parse(message);

          //Null is returned if it is not a parsable user search
          if(parsedMessage != null) {
            log.debug("parsedMessage: {}", parsedMessage);
            queue.put(parsedMessage);
          }

          cc.deleteDocument((String)logEntry.get("_id"),(String)logEntry.get("_rev"));
        }

        try {
          log.debug("Sleeping for 10 seconds.");
          Thread.sleep(10000);
        } catch(InterruptedException ex) {
          running = false;
        }
      }
    }
  }

}
