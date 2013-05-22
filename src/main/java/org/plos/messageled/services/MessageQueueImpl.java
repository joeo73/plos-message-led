package org.plos.messageled.services;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageQueueImpl implements MessageQueue {
  private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(50);

  public String take() throws InterruptedException {
    return queue.take();
  }

  public int drainTo(Collection<String> c) throws InterruptedException {
    return queue.drainTo(c);
  }

  public void put(String message) {
    try {
      queue.put(message);
    } catch (InterruptedException iex) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Unexpected interruption");
    }
  }

  public int size() {
    return queue.size();
  }
}
