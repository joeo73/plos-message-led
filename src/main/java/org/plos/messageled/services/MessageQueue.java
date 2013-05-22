package org.plos.messageled.services;

import java.util.Collection;

public interface MessageQueue {
  public void put(String message);
  public String take() throws InterruptedException;
  public int drainTo(Collection<String> c) throws InterruptedException;
  public int size();
}
