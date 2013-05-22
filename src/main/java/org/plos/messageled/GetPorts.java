package org.plos.messageled;

import org.plos.messageled.util.gnuio.SerialPort;

public class GetPorts {
  public static void main( String[] args )
  {
    String[] ports = SerialPort.getPorts();

    System.out.println("\nSearching for ports...\n--------------------");

    if(ports.length > 0) {
      System.out.println("Founds Found :-)");

      for(String p : ports) {
        System.out.println("Port name:" + p);
      }
    } else {
      System.out.println("No ports found :-(");
    }

    System.out.println();
  }
}
