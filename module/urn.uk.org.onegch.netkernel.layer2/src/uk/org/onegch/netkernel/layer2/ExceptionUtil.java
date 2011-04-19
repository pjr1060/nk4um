package uk.org.onegch.netkernel.layer2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;

public class ExceptionUtil {
  private static boolean jabberWarningIssued= false;
  public static void logJabber(INKFRequestContext aContext, AccessorUtil util, String message) {
    try {
      String jabberPeopleConfig= aContext.source("netkernel:/config/netkernel.install.path") + "dxml-etc/jabberPeopleConfig.xml";
      IHDSNode peopleNodes= aContext.source(jabberPeopleConfig, IHDSNode.class);
      for (Object personToJabber : peopleNodes.getValues("//person")) {
        util.issueSourceRequest("active:jabberSend",
                                null,
                                new Arg("to", (String)personToJabber),
                                new ArgByValue("message", message));
      }
    } catch (Exception e) {
      if (jabberWarningIssued) {
        aContext.logRaw(INKFRequestContext.LEVEL_WARNING, "Jabber not available: \n" + fullStackTrace(e));
        jabberWarningIssued= true;
      }
      aContext.logRaw(INKFRequestContext.LEVEL_FINE, message);
    }
  }
  
  public static String fullStackTrace(Throwable ex) {
    StringWriter stackTrace= new StringWriter();
    ex.printStackTrace(new PrintWriter(stackTrace));
    
    return stackTrace.toString();
  }
  
  public static String causeStack(Throwable ex) {
    String causeStack= "";
    
    Throwable currentCause= ex;
    while (currentCause != null) {
      String message= currentCause.getMessage();
      
      causeStack= "            " + currentCause.getClass().getCanonicalName() + (message != null ? " (" + message + ")" : "") + "\n" + causeStack;
      currentCause= currentCause.getCause();
    } 
    
    causeStack= truncate(causeStack, 5);
    
    return causeStack;
  }
  
  private static String truncate(String string, int depth) {
    String truncStackTrace= "";
    String[] lines= string.split("\n", depth + 1);
    
    for (int i= 0; i < Math.min(lines.length, depth); i++) {
      truncStackTrace+= lines[i] + "\n";
    }
    
    return truncStackTrace;
  }
  
  public static String getIpAddress(INKFRequestContext aContext, AccessorUtil util) {
    String ipAddress= "unknown";
    try {
      if (aContext.exists("httpRequest:/header/X-Forwarded-For")) {
        ipAddress= aContext.source("httpRequest:/header/X-Forwarded-For", String.class);
      } else if (aContext.exists("httpRequest:/remote-host")) {
        ipAddress= aContext.source("httpRequest:/remote-host", String.class);
      } else {
        ipAddress= "unknown";
      }
    } catch (Exception e) {
      ExceptionUtil.logJabber(aContext, util, "Could not look up IP Address: " + ExceptionUtil.causeStack(e));
      aContext.logRaw(INKFRequestContext.LEVEL_WARNING, ExceptionUtil.fullStackTrace(e));
    }
    
    return ipAddress;
  }
    
  public static String getUserAgent(INKFRequestContext aContext, AccessorUtil util) throws NKFException {
    String userAgent= "unknown";
    try {
      if (aContext.exists("httpRequest:/header/User-Agent")) {
        userAgent= aContext.source("httpRequest:/header/User-Agent", String.class);
      }
    } catch (Exception e) {
      ExceptionUtil.logJabber(aContext, util, "Could not look up user agent: " + ExceptionUtil.causeStack(e));
      aContext.logRaw(INKFRequestContext.LEVEL_WARNING, ExceptionUtil.fullStackTrace(e));
    }
    
    return userAgent;
  }

  @SuppressWarnings("unchecked")
  public static <T> T getCause(Throwable exception, Class<? extends Throwable> cause) {
    Throwable currentCause= exception;
    T foundCause= null;
    while (currentCause != null && foundCause == null) {
      if (cause.isInstance(currentCause)) {
        foundCause= (T)currentCause;
      }
      
      currentCause= currentCause.getCause();
    }
    
    return foundCause;
  }
  
  public static boolean hasCause(Throwable exception, Class<? extends Throwable> cause) {
    return (getCause(exception, cause) != null);
  }
  
  public static void handleException(INKFRequestContext aContext, Throwable exception, String identifier) {
    String currentUserId= "unknown";
    try {
      if (aContext.exists("session:/currentUser")) {
        currentUserId= aContext.source("session:/currentUser", String.class);
      }
    } catch (Exception e) {}
    
    try {
      String date= DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.UK).format(Calendar.getInstance().getTime());
      
      String causeStack= ExceptionUtil.causeStack(exception);
      
      String extUrl= "unknown";
      if (aContext.exists("arg:ext-url")) {
        extUrl= aContext.source("arg:ext-url", String.class);
      } else if (aContext.exists("httpRequest:/url")) {
        extUrl= aContext.source("httpRequest:/url", String.class);
      }
      
      String message= "Exception thrown at " + date + " with " + extUrl + " (" + identifier + ")\n" +
                      "  IP Address: " + ExceptionUtil.getIpAddress(aContext, new AccessorUtil(aContext)) + "\n" +
                      "  Current User: " + currentUserId;
      
      // log the exception
      String stackTrace= fullStackTrace(exception);
      aContext.logRaw("dxml", INKFRequestContext.LEVEL_SEVERE, message + "\n" + stackTrace);
      
      logJabber(aContext, new AccessorUtil(aContext), message + "\n  deepest causes:\n" + causeStack);
    } catch (Throwable t) {
      aContext.logRaw(INKFRequestContext.LEVEL_SEVERE, "Error in exception handling, will now attempt to log stacktrace");
      aContext.logRaw(INKFRequestContext.LEVEL_SEVERE, ExceptionUtil.fullStackTrace(t));
    }
  }
}
