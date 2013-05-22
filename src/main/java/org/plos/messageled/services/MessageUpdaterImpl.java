package org.plos.messageled.services;

import gnu.io.CommPortIdentifier;
import org.plos.messageled.Config;
import org.plos.messageled.util.gnuio.SerialPort;
import org.plos.messageled.util.hidlyled.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MessageUpdaterImpl implements MessageUpdater {
  private static final Logger log = LoggerFactory.getLogger(MessageUpdaterImpl.class);

  private MessageQueue queue;
  private Config config;
  private List<String> messages = new ArrayList<String>();
  private Map<String, Pattern> profanePatterns = new HashMap<String, Pattern>();

  public void start() {
    log.info("Starting up message update thread.");

    (new Thread(new MessageUpdate())).start();
  }

  public void setMessageQueue(MessageQueue queue) {
    this.queue = queue;
  }

  public void setConfig(Config config) {
    this.config = config;

    final Map<String, Pattern> patterns = new HashMap<String, Pattern>(this.config.getProfaneWords().length);
    for (final String profaneWord : this.config.getProfaneWords()) {
      final Pattern pattern = Pattern.compile("\\b" + profaneWord.toLowerCase() + "\\b");
      patterns.put(profaneWord, pattern);
    }
    this.profanePatterns = patterns;
  }

  /**
   * Check for naughty words
   *
   * @param content
   *
   * @return
   */
  private List<String> validate(final String content) {
    final List<String> profaneWordsFound = new ArrayList<String>();
    if (content != null) {
      final String contentLowerCase = content.toLowerCase();

      for (final Map.Entry<String,Pattern> patternEntry : profanePatterns.entrySet()) {
        final Pattern pattern = patternEntry.getValue();
        if (pattern.matcher(contentLowerCase).find()) {
          profaneWordsFound.add(patternEntry.getKey());
        }
      }
    }
    return profaneWordsFound;
  }

  private void sendMessages(String[] messages) {
    log.info("Sending {} messages", messages.length);

    CommPortIdentifier portId = SerialPort.getPort(config.getPort());

    Message message = Message.create();

    for(int a = 0; a < messages.length; a++) {
      String s = messages[a];

      List<String> naughtyWords = validate(s);

      if(naughtyWords.size() > 0) {
        for(String word : naughtyWords) {
          log.debug("Naughty word found: {}", word);
        }
      } else {
        log.debug("Adding Message: {}", s);
        message = message.addLine(s, config.getStayTime(), config.getSpeed(), true);
      }
    }

    log.info("Message: {}", message.render());

    if(portId == null) {
      log.error("Can't open port: {}, retrying later", config.getPort());
    } else {
      SerialPort.sendMessage(portId, message.render());
    }
  }

  private class MessageUpdate implements Runnable {
    public void run() {
      boolean running = true;

      while(running) {
        try {
          log.debug("Waiting for status update");
          Collection<String> deQueuedMessages = new ArrayList<String>();

          if(queue.drainTo(deQueuedMessages) > 0) {
            for(String message : deQueuedMessages) {
              log.info("Got message: {}", message);

              if(messages.size() == config.getMessageCount()) {
                messages.remove(0);
              }

              messages.add(message);
            }

            sendMessages(messages.toArray(new String[messages.size()]));
          }

          //Wait n seconds, so the LED can complete at least one cycle
          Thread.sleep(config.getWaitTime() * 1000);

        } catch(InterruptedException ex) {
          running = false;
        }
      }
    }
  }

}