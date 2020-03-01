package GPP_Library;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.transform.CompileStatic() public class CompositeDetails
  extends java.lang.Object  implements
    java.io.Serializable,    java.lang.Cloneable,    groovy.lang.GroovyObject {
;
public CompositeDetails
(int workers, int stages) {}
@groovy.transform.Generated() @groovy.transform.Internal() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  int getWorkers() { return (int)0;}
public  void setWorkers(int value) { }
public  int getStages() { return (int)0;}
public  void setStages(int value) { }
public  java.util.List<java.util.List<GPP_Library.LocalDetails>> getcDetails() { return (java.util.List<java.util.List<GPP_Library.LocalDetails>>)null;}
public  void setcDetails(java.util.List<java.util.List<GPP_Library.LocalDetails>> value) { }
public  GPP_Library.GroupDetails extractByStage(int stage) { return (GPP_Library.GroupDetails)null;}
public  GPP_Library.PipelineDetails extractByPipe(int pipe) { return (GPP_Library.PipelineDetails)null;}
public  void insertCompositeDetails(int group, int stage, java.lang.String name, java.lang.String initMethod, java.util.List initData, java.lang.String finaliseMethod, java.util.List finaliseData) { }
public  java.lang.String toString() { return (java.lang.String)null;}
}
