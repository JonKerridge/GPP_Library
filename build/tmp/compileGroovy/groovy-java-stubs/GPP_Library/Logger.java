package GPP_Library;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.transform.CompileStatic() public class Logger
  extends java.lang.Object  implements
    java.lang.Cloneable,    java.io.Serializable,    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public static  int getStartTag() { return (int)0;}
public static  void setStartTag(int value) { }
public static  int getInitTag() { return (int)0;}
public static  void setInitTag(int value) { }
public static  int getInputReadyTag() { return (int)0;}
public static  void setInputReadyTag(int value) { }
public static  int getInputCompleteTag() { return (int)0;}
public static  void setInputCompleteTag(int value) { }
public static  int getOutputReadyTag() { return (int)0;}
public static  void setOutputReadyTag(int value) { }
public static  int getOutputCompleteTag() { return (int)0;}
public static  void setOutputCompleteTag(int value) { }
public static  int getEndTag() { return (int)0;}
public static  void setEndTag(int value) { }
public static  int getWorkStartTag() { return (int)0;}
public static  void setWorkStartTag(int value) { }
public static  int getWorkEndTag() { return (int)0;}
public static  void setWorkEndTag(int value) { }
public static  jcsp.lang.ChannelOutput getLogChan() { return (jcsp.lang.ChannelOutput)null;}
public static  void setLogChan(jcsp.lang.ChannelOutput value) { }
public static  void initLogChannel(jcsp.lang.ChannelOutput loggingChan) { }
public static  void startLog(java.lang.String logID, long time) { }
public static  void initLog(java.lang.String logID, long time) { }
public static  void inputReadyEvent(java.lang.String logID, long time) { }
public static  void inputCompleteEvent(java.lang.String logID, long time, java.lang.Object o) { }
public static  void outputReadyEvent(java.lang.String logID, long time, java.lang.Object o) { }
public static  void outputCompleteEvent(java.lang.String logID, long time, java.lang.Object o) { }
public static  void endEvent(java.lang.String logID, long time) { }
public static  void workStartEvent(java.lang.String logID, long time) { }
public static  void workEndEvent(java.lang.String logID, long time) { }
}
