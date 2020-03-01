package GPP_Library.cluster;

import GPP_Library.*;
import groovyJCSP.*;
import jcsp.lang.*;
import jcsp.net2.*;
import jcsp.net2.tcpip.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

public class NodeNetwork
  extends java.lang.Object  implements
    java.io.Serializable,    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  GPP_Library.cluster.NodeInterface getNodeProcesses() { return (GPP_Library.cluster.NodeInterface)null;}
public  void setNodeProcesses(GPP_Library.cluster.NodeInterface value) { }
public  java.util.List getInConnections() { return (java.util.List)null;}
public  void setInConnections(java.util.List value) { }
public  java.util.List getOutConnections() { return (java.util.List)null;}
public  void setOutConnections(java.util.List value) { }
public static  jcsp.lang.ProcessManager BuildNodeNetwork(jcsp.net2.NetChannelOutput hostRequest, jcsp.net2.NetChannelInput loadChannel, java.lang.String nodeIP) { return (jcsp.lang.ProcessManager)null;}
public static  java.util.List BuildHostInChannels(java.util.List emitterInConnections, java.util.List collectorInConnections) { return (java.util.List)null;}
public static  java.util.List BuildHostOutChannels(java.util.List emitterOutConnections, java.util.List collectorOutConnections) { return (java.util.List)null;}
public static  java.util.List BuildHostNetwork(GPP_Library.cluster.NodeNetwork emit, GPP_Library.cluster.NodeNetwork collector, java.util.List inChannels, java.util.List outChannels) { return (java.util.List)null;}
}
