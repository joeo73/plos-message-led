package org.plos.messageled.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.plos.messageled.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: josowski
 * Date: 10/1/13
 * Time: 9:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class SOLRMonitorImpl implements MessageMonitor {
  private static final Logger log = LoggerFactory.getLogger(SOLRMonitorImpl.class);

  private MessageQueue queue;
  private Config config;

  public void start() {
    log.info("Starting up SQLMonitorImpl thread.");

    (new Thread(new SOLRMonitor())).start();
  }

  public void setMessageQueue(MessageQueue queue) {
    this.queue = queue;
  }

  public void setConfig(Config config) {
    this.config = config;
  }

  private long getSOLRDocumentCount() throws MalformedURLException, IOException {
    String query = config.getSolrHost() + "/search?q=*:*&fq=doc_type:full&fq=article_type:%22Research%20Article%22&rows=0&facet=false&hl=false&omitHeader=true&wt=json";
    URL serverURLPath = new URL(query);

    log.debug("SOLRMonitor Query: {}", query);

    HttpURLConnection conn = (HttpURLConnection)serverURLPath.openConnection();
    conn.setRequestMethod("GET");
    conn.setDoOutput(false);

    JsonReader jr = new JsonReader(new InputStreamReader(conn.getInputStream()));
    jr.beginObject();
    jr.nextName();
    jr.beginObject();
    jr.nextName();

    long response = jr.nextLong();

    return response;
  }

  private class SOLRMonitor implements Runnable {
    public void run() {
      boolean running = true;

      log.debug("SOLRMonitor running");

      config.getSolrHost();
      //api.plos.org/search?q=*:*&fq=doc_type:full&fq=article_type:"Research%20Article"&rows=0&facet=false&hl=false&omitHeader=true&wt=json
      Gson gson = new Gson();

      while(running) {
        log.debug("Calling SOLR");

        try {
          //Get the count of research documents
          long articles = getSOLRDocumentCount();

          log.debug("SOLRMonitor Value parsed: {}", articles);
          queue.put(String.format("%d articles and counting.", articles));

        } catch(Exception e) {
          log.error("Error: " + e.getMessage());
        }

        try {
          log.debug("Sleeping for 2 hours.");
          Thread.sleep(7200000);
        } catch(InterruptedException ex) {
          running = false;
        }
      }
    }
  }
}
