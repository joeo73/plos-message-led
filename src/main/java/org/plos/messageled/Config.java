package org.plos.messageled;

public class Config {
  private int messageCount;
  private int waitTime;
  private int stayTime;
  private int speed;
  private String port;
  private String couchHost = new String();
  private String couchDB = new String();
  private String[] profaneWords = new String[] {};
  private String dbDriver;
  private String dbUrl;
  private String dbUsername;
  private String dbPassword;
  private String dbQuery;
  private String solrHost;
  private int dbQueryLimit;

  public void setMessageCount(int messageCount) {
    this.messageCount = messageCount;
  }

  public int getMessageCount() {
    return this.messageCount;
  }

  public void setWaitTime(int waitTime) {
    this.waitTime = waitTime;
  }

  public int getWaitTime() {
    return this.waitTime;
  }

  public void setProfaneWords(String wordlist[]) {
    this.profaneWords = wordlist;
  }

  public String[] getProfaneWords() {
    return this.profaneWords;
  }

  public String getCouchHost() {
    return couchHost;
  }

  public void setCouchHost(String couchHost) {
    this.couchHost = couchHost;
  }

  public String getCouchDB() {
    return couchDB;
  }

  public void setCouchDB(String couchDB) {
    this.couchDB = couchDB;
  }

  public int getStayTime() {
    return stayTime;
  }

  public void setStayTime(int stayTime) {
    this.stayTime = stayTime;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getDbDriver() {
    return dbDriver;
  }

  public void setDbDriver(String dbDriver) {
    this.dbDriver = dbDriver;
  }

  public String getDbUrl() {
    return dbUrl;
  }

  public void setDbUrl(String dbUrl) {
    this.dbUrl = dbUrl;
  }

  public String getDbUsername() {
    return dbUsername;
  }

  public void setDbUsername(String dbUsername) {
    this.dbUsername = dbUsername;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }

  public String getDbQuery() {
    return dbQuery;
  }

  public void setDbQuery(String dbQuery) {
    this.dbQuery = dbQuery;
  }

  public int getDbQueryLimit() {
    return dbQueryLimit;
  }

  public void setDbQueryLimit(int dbQueryLimit) {
    this.dbQueryLimit = dbQueryLimit;
  }

  public String getSolrHost() {
    return solrHost;
  }

  public void setSolrHost(String solrHost) {
    this.solrHost = solrHost;
  }
}
