package org.plos.messageled.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolrMessage {
  private static final Logger log = LoggerFactory.getLogger(SolrMessage.class);

  //I love/hate regexes, but basically from this string:
  //"[] webapp=/solr path=/select/ params={indent=on&start=0&q=genetics&version=2.2&rows=10} hits=315 status=0 QTime=438
  //parse out "genetics"  (Anything after q= and before "&" in a lazy fashion
  final static Pattern pattern = Pattern.compile("q=(.+?)\\&");

  public static String parse(String message) {
    Matcher matcher = pattern.matcher(message);

    while(matcher.find()) {
      if(matcher.groupCount() > 1) {
        log.error("Strange Query received: {}", message);

        throw new RuntimeException("Strange Query received: " + message);
      }

      String parsedMessage = matcher.group(1);

      //If the parsed message contains a ":", assume it's some advanced search
      //And it won't be renderable by the LED Sign.
      if(!parsedMessage.contains(":")) {
        try {
          return URLDecoder.decode(parsedMessage, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
          throw new RuntimeException(ex.getMessage(), ex);
        }
      }
    }

    return null;
  }
}
