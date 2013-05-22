package org.plos.messageled.util.hidlyled;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Message {
  private List<MessageLine> lines = new ArrayList<MessageLine>();

  public static Message create() {
    return new Message();
  }

  public Message addLine(String message, int speed, int staytime, boolean small) {
    TRANSITIONS t = TRANSITIONS.preferred[(new Random()).nextInt(TRANSITIONS.preferred.length)];

    this.lines.add(new MessageLine(message, speed, staytime, t, small));

    return this;
  }

  public Message addLine(String message, int speed, int staytime, TRANSITIONS transition, boolean small) {
    this.lines.add(new MessageLine(message, speed, staytime, transition, small));

    return this;
  }

  public Message addLine(String message, TRANSITIONS transition, boolean small) {
    this.lines.add(new MessageLine(message, 7, 6, transition, small));

    return this;
  }

  public String render()
  {
    StringBuilder s = new StringBuilder();

    //Only support one message for now
    s.append("~128~f01");

    for(MessageLine line : lines) {
      //\Z7\Y6\sTest-1
      s.append(line.toString());
    }

    s.append("\r\r");

    return s.toString();
  }

  private class MessageLine {
    String message;
    int speed;
    int staytime;
    TRANSITIONS transition;
    boolean small;

    public MessageLine(String message, int speed, int staytime, TRANSITIONS transition, boolean small) {
      this.message = message;
      this.speed = speed;
      this.staytime = staytime;
      this.transition = transition;
      this.small = small;
    }

    public String toString() {
      StringBuilder s = new StringBuilder();

      s.append(this.transition.toString());

      s.append("\\Z");
      s.append(this.speed);

      s.append("\\Y");
      s.append(this.staytime);

      if(this.small) {
        s.append("\\q");
      } else {
        s.append("\\s");
      }

      s.append(this.message);

      s.append("\r");

      return s.toString();
    }
  }
}
