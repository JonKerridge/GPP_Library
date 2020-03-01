package GPP_Library.cluster;

import groovyJCSP.*;
import jcsp.lang.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

public interface NodeInterface
  extends
    jcsp.lang.CSProcess,    java.io.Serializable {
;
 void connect(groovyJCSP.ChannelInputList inChannels, groovyJCSP.ChannelOutputList outChannels);
}
