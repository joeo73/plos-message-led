package org.plos.messageled.util.gnuio;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SerialPort {
  private static final Logger log = LoggerFactory.getLogger(SerialPort.class);

  public static void sendMessage(CommPortIdentifier portId, String message) {
    try {
      gnu.io.SerialPort port = (gnu.io.SerialPort) portId.open(
        "led-message", // Name of the application asking for the port
        10000   // Wait max. 10 sec. to acquire port
      );

      port.setSerialPortParams(9600, gnu.io.SerialPort.DATABITS_8, gnu.io.SerialPort.STOPBITS_1, gnu.io.SerialPort.PARITY_NONE);

      log.debug("Sending message: {}", message);

      PrintStream ps = new PrintStream(port.getOutputStream(), true, "UTF-8");
      ps.write(message.getBytes(Charset.forName("UTF-8")));

      port.close();

    } catch(PortInUseException e) {
      throw new RuntimeException(e.getMessage(), e);
    } catch(UnsupportedCommOperationException e) {
      throw new RuntimeException(e.getMessage(), e);
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public static String[] getPorts() {
    List<String> ports = new ArrayList<String>();

    Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

    while(portIdentifiers.hasMoreElements()) {
      CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();

      ports.add(pid.getName());
    }

    return ports.toArray(new String[ports.size()]);
  }

  public static CommPortIdentifier getPort(String name) {
    CommPortIdentifier portId = null;  // will be set if port found
    Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

    while (portIdentifiers.hasMoreElements())
    {
      CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();

      log.info("PID: {}", pid.getName());
      log.info("PID Serial: {}", pid.getPortType() == CommPortIdentifier.PORT_SERIAL);

      if(pid.getPortType() == CommPortIdentifier.PORT_SERIAL && pid.getName().equals(name))
      {
        portId = pid;
        break;
      }
    }

    return portId;
  }
}
