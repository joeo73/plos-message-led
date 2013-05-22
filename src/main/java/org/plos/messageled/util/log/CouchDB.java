package org.plos.messageled.util.log;

import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouchDB {
  private static final Logger log = LoggerFactory.getLogger(CouchDB.class);
  private String host;
  private String db;

  public CouchDB(String host, String db) {
    this.host = host;
    this.db = db;
  }

  public String[] getDocumentList() {
    log.debug("Fetching document list");
    List<String> docIds = new ArrayList<String>();

    URL serverURLPath = null;
    HttpURLConnection connection = null;

    try {
      serverURLPath = new URL(host + "/" + db + "/_all_docs");
      log.debug(serverURLPath.toString());
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }

    try {
      connection = (HttpURLConnection)serverURLPath.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(false);

      JsonReader jr = new JsonReader(new InputStreamReader(connection.getInputStream()));

      docIds = readAllDocsResponse(jr);

    } catch (IOException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    } finally {
      // Release the connection.
      if(connection != null) {
        connection.disconnect();
      }
    }

    return docIds.toArray(new String[docIds.size()]);
  }

  public void deleteDocument(String id, String rev) {
    log.debug("Deleting document id: {},rev:{}", id, rev);
    URL serverURLPath = null;
    HttpURLConnection connection = null;

    try {
      serverURLPath = new URL(host + "/" + db + "/" + id + "?rev=" + rev);
      log.debug(serverURLPath.toString());
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }

    try {
      connection = (HttpURLConnection)serverURLPath.openConnection();
      connection.setRequestMethod("DELETE");
      connection.setDoOutput(false);

      connection.getInputStream();

      if(connection.getResponseCode() != 200) {
        throw new RuntimeException("Document not deleted.");
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    } finally {
      // Release the connection.
      if(connection != null) {
        connection.disconnect();
      }
    }
  }

  public Map getDocument(String id) {
    log.debug("Get document id: {} ", id);
    URL serverURLPath = null;
    HttpURLConnection connection = null;

    try {
      serverURLPath = new URL(host + "/" + db + "/" + id);
      log.debug(serverURLPath.toString());
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }

    try {
      connection = (HttpURLConnection)serverURLPath.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(false);

      JsonReader jr = new JsonReader(new InputStreamReader(connection.getInputStream()));

      return readLogEntry(jr);
    } catch (IOException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    } finally {
      // Release the connection.
      if(connection != null) {
        connection.disconnect();
      }
    }
  }

  public List<String> readAllDocsResponse(JsonReader reader) throws IOException {
    reader.beginObject();
    reader.nextName(); //total_rows
    reader.nextLong(); //total_rows value
    reader.nextName(); //offset
    reader.nextLong(); //offset_value
    reader.nextName(); //rows

    List<String> messages = new ArrayList<String>();

    reader.beginArray();

    while (reader.hasNext()) {
      messages.add(getID(reader));
    }

    reader.endArray();

    return messages;
  }

  public String getID(JsonReader reader) throws IOException {
    String id = new String();

    reader.beginObject();

    while (reader.hasNext()) {
      String name = reader.nextName();
      if (name.equals("id")) {
        id = reader.nextString();
      } else {
        reader.skipValue();
      }
    }

    reader.endObject();

    if(id == null) {
      throw new RuntimeException("Document has no ID!");
    } else {
      return id;
    }
  }

  /**
   *
   * Generate a hashmap with the assumption the received JSON looks like this:
   *
   * {
   "_id" : "1356635889770",
   "_rev" : "1-7fbbc9d314ca8b6fc9245d315820785a",
   "hostName" : "josowski-ubuntu",
   "applicationName" : "SOLR",
   "message" : "[] Opening new SolrCore at /home/josowski/Work/tomcat/lib/./, dataDir=/home/josowski/Work/ambradata/solr/",
   "timestamp" : 1356635889770,
   "level" : "INFO",
   "level_int" : 20000,
   "loggerName" : "org.apache.solr.core.SolrCore",
   "threadName" : "main",
   "categoryName" : "org.apache.solr.core.SolrCore",
   "locationInformation" :
   {
   "className" : "org.apache.solr.core.SolrCore",
   "fileName" : "SolrCore.java",
   "lineNumber" : "524",
   "methodName" : "<init>"
   }
   }
   */
  public Map readLogEntry(JsonReader reader) throws IOException {

    Map<String, Object> logEntry = new HashMap<String, Object>();

    reader.beginObject();

    logEntry.put(reader.nextName(), reader.nextString()); // _id
    logEntry.put(reader.nextName(), reader.nextString()); // _rev
    logEntry.put(reader.nextName(), reader.nextString()); // hostName
    logEntry.put(reader.nextName(), reader.nextString()); // applicationName
    logEntry.put(reader.nextName(), reader.nextString()); // message
    logEntry.put(reader.nextName(), reader.nextLong()); // timestamp
    logEntry.put(reader.nextName(), reader.nextString()); // level
    logEntry.put(reader.nextName(), reader.nextInt()); // level_int
    logEntry.put(reader.nextName(), reader.nextString()); // loggerName
    logEntry.put(reader.nextName(), reader.nextString()); // threadName
    logEntry.put(reader.nextName(), reader.nextString()); // categoryName

    reader.nextName(); //locationInformation
    reader.beginObject();

    Map<String, Object> locationInfo = new HashMap<String, Object>();
    locationInfo.put(reader.nextName(), reader.nextString()); //className
    locationInfo.put(reader.nextName(), reader.nextString()); //fileName
    locationInfo.put(reader.nextName(), reader.nextString()); //lineNumber
    locationInfo.put(reader.nextName(), reader.nextString()); //methodName

    logEntry.put("locationInfo", locationInfo);

    reader.endObject();
    reader.endObject();

    return logEntry;
  }
}
