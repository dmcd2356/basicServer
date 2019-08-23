/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicserver;

import java.io.IOException;
import java.util.Map;
import fi.iki.elonen.NanoHTTPD;
import java.util.Iterator;

/**
 *
 * @author dmcd2356
 */
public class BasicServer extends NanoHTTPD {
  
  private static final int  PORT = 6000;

  public BasicServer() throws IOException {
    super(PORT);
    start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    System.out.println("Server running on http://localhost:" + PORT + "/");
  }
  
  public static void main(String[] args) {
    try {
      new BasicServer();
    } catch (IOException e) {
      System.err.println("Error starting nano server:\n" + e);
    }
  }

  @Override
  public Response serve(IHTTPSession session) {
    String res = "<html><body>";
    Map<String, String> parms = session.getParms();
    try {
      session.parseBody(parms);
      Method method = session.getMethod();
      String postBody = session.getQueryParameterString();
      System.out.println("type: " + method.toString() + ", body: " + postBody);
      
      if (parms.isEmpty()) {
        res += "<h1>Missing arguments!</h1>\n";
      } else if (method.equals(Method.GET)) {
        if (parms.size() > 1) {
          res += "<h1>Only 1 argument allowed for GET!</h1>\n";
        } else {
          if(parms.containsKey("result")) {
            res += "<h1>This is the result value: 42</h1>\n";
          } else {
            res += "<h1>Unknown GET argument!</h1>\n";
          }
        }
      } else if (method.equals(Method.POST)) {
        // iterate through the list of settable parameters received
        Iterator it = parms.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry pair = (Map.Entry)it.next();
          String name = pair.getKey().toString();
          //String value = pair.getValue().toString();
          switch (name) {
            case "jar":
              System.out.println("jar filename: " + pair.getValue().toString());
              res += "<h1>OK</h1>\n";
              break;
            case "class":
              System.out.println("class path: " + pair.getValue().toString());
              res += "<h1>OK</h1>\n";
              break;
            case "method":
              System.out.println("method name: " + pair.getValue().toString());
              res += "<h1>OK</h1>\n";
              break;
            case "run":
              System.out.println("Begining RUN...");
              res += "<h1>OK</h1>\n";
              break;
            default:
              res += "<h1>Unknown POST argument '" + name + "' </h1>\n";
              break;
          }
          it.remove(); // avoids a ConcurrentModificationException
        }
      }
    } catch (IOException | ResponseException ex) {
      res += "<h1>IOException</h1>\n";
    }

    return newFixedLengthResponse(res + "</body></html>\n");
  }
  
}
