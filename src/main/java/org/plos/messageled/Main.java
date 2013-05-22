package org.plos.messageled;

import org.plos.messageled.services.MessageMonitor;
import org.plos.messageled.services.MessageUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Log4jConfigurer;
import java.io.FileNotFoundException;

public class Main
{
  private static String log4JConfig = "/etc/plos-message-led/log4j.xml";
  private static Logger log = LoggerFactory.getLogger(Main.class);

  public static void main( String[] args )
  {
    try {
      //Check the log for changes every 6000 seconds
      Log4jConfigurer.initLogging(log4JConfig, 60000);
      System.out.println("Log initialized from " + log4JConfig + ".");
    } catch(FileNotFoundException ex) {
      try {
        Log4jConfigurer.initLogging("classpath:log4j.xml");
        System.out.println("Log initialized from classpath:log4j.xml.");
      } catch(FileNotFoundException ex1) {
        System.out.println("Not log initialized.");
      }
    }

    System.setSecurityManager(null);

    Logger log = LoggerFactory.getLogger(Main.class);


    ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "plos-led-context.xml" });

    MessageMonitor monitor = (MessageMonitor)context.getBean("monitor");
    MessageUpdater updater = (MessageUpdater)context.getBean("updater");

    log.info("Starting message monitor");
    monitor.start();

    log.info("Starting message updater");
    updater.start();

    log.info("Complete");
  }
}
