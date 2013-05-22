package org.plos.messageled.services;

import org.plos.messageled.Config;
import org.plos.messageled.util.log.CouchDB;
import org.plos.messageled.util.log.SolrMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLMonitorImpl implements MessageMonitor {
  private static final Logger log = LoggerFactory.getLogger(SQLMonitorImpl.class);

  private MessageQueue queue;
  private List<String> last10terms;
  private Config config;

  public void start() {
    log.info("Starting up SQLMonitorImpl thread.");

    (new Thread(new SQLMonitor())).start();

    last10terms = new ArrayList<String>();
  }

  private Connection getConnection() throws SQLException {
    Connection con = null;
    try {
      Class.forName(config.getDbDriver());
      con = DriverManager.getConnection(config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
    }
    catch(ClassNotFoundException e) {
      log.error("Error: " + e.getMessage());
      System.exit(-1);
    }
    return con;
  }


  public void setMessageQueue(MessageQueue queue) {
    this.queue = queue;
  }

  public void setConfig(Config config) {
    this.config = config;
  }

  private class SQLMonitor implements Runnable {
    public void run() {
      boolean running = true;

      log.debug("SQLMonitor running");

      while(running) {
        log.debug("Calling Mysql");

        ResultSet rs = null;
        Connection conn = null;

        try {
          conn = getConnection();
          Statement s = conn.createStatement();
          rs = s.executeQuery(config.getDbQuery());

          while (rs.next()) {
            String term = rs.getString(1).intern();

            //Only send the term to the queue if it hasn't appeared in the last set of terms sent
            if(!last10terms.contains(term)) {
              log.debug("Found New Term: " + term);
              if(last10terms.size() == config.getDbQueryLimit()) {
                last10terms.remove(0);
              }
              last10terms.add(term);
              queue.put(term);
            }
          }

          conn.close();
        } catch(SQLException e) {
          log.error("Error: " + e.getMessage());
        }

        try {
          log.debug("Sleeping for 30 seconds.");
          Thread.sleep(30000);
        } catch(InterruptedException ex) {
          running = false;
        }
      }
    }
  }

}
