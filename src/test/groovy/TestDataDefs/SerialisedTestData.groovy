package TestDataDefs


import groovyParallelPatterns.DataClass
import jcsp.lang.CSTimer

//@CompileStatic
class SerialisedTestData extends DataClass {

  def timer = new CSTimer()
  int data = 0
  int instanceNumber = 0
  int cloneNumber = 0
  int w1 = 0
  int w2 = 0
  int w3 = 0
 // int instance
//  static int instances
//  static int cloneInstance = 1
//
//  static String totalInitialise = "initClass"
//  static String partialInitialise = "init"
//  static String create = "createInstance"
//  static String createFromInput = "createFromInput"
//  static String createFromLocal = "createFromLocal"
    static String nullFunc = "nullFunc"
//  static String f1 = "doubleData"
    String func1 = "func1"
    String func2 = "func2"
    String func3 = "func3"
//  static String finaliseMethod = "finalise"
//  static String combineMethod = "combine"
//  static String mergeMethod = "mergeMethod"

//  int initClass ( List d){
//    instances = d[0]
//    instance = 1
//    cloneInstance = 1
//    w1 = 0
//    w2 = 0
//    w3 = 0
//    return completedOK
//  }
//
//  int init ( List d){
//    cloneInstance = 1
//    return completedOK
//  }
//
//  int createInstance (List d){
//    if ( instance > instances) return normalTermination
//    else {
//      data = instance
//      instanceNumber = instance
//      instance = instance + 1
//      return normalContinuation
//    }
//  }
//
//  int createFromInput (List d){
//    SerialisedTestData td = d[0]
//    def createData = d[1] // null in this case
//    if ( instance > instances) return normalTermination
//    else {
//      data = td.data
//      instanceNumber = instance
//      instance = instance + 1
//      cloneNumber = td.cloneNumber
//      return normalContinuation
//    }
//  }
//
//  int createFromLocal(List d){
//    TestWorker localWorker = d[0]
//    if ( instance > instances) return normalTermination
//    else {
//      data = localWorker.consts[instance]
//      instanceNumber = instance
//      instance = instance + 1
//      return normalContinuation
//    }
//  }
//
//  int doubleData(List p){
//    data = 2 * data
////        timer.sleep(10)
//    return completedOK
//  }

  int nullFunc(List p){
    // do nothing
    return completedOK
  }

  int func1(List p){
    List modifier = p[0]
    TestWorker wc = p[1]
    w1 += modifier[0] + wc.consts[instanceNumber]
    return completedOK
  }

  int func2(List p){
    List modifier = p[0]
    TestWorker wc = p[1]
    w2 += modifier[0] + wc.consts[instanceNumber]
    return completedOK
  }

  int func3(List p){
    List modifier = p[0]
    TestWorker wc = p[1]
    w3 += modifier[0] + wc.consts[instanceNumber]
    return completedOK
  }

//  int finalise (SerialisedTestData o){
//    data = o.data
//    instanceNumber = o.instanceNumber
//    return completedOK
//  }
//
//  int combine(SerialisedTestData o){
//    data += o.data
//    instanceNumber = o.instanceNumber
//    return completedOK
//  }
//
  @Override
  public SerialisedTestData clone() {
    //println "Creating clone $cloneInstance"
    SerialisedTestData newTD = new SerialisedTestData()
    newTD.data = this.data
    newTD.instanceNumber = this.instanceNumber
    newTD.cloneNumber = this.cloneNumber
    newTD.w1 = this.w1
    newTD.w2 = this.w2
    newTD.w3 = this.w3
    return newTD
  }

//  int mergeMethod (List <SerialisedTestData> buffers){
//    // should output smallest first
//    int bufferSize = buffers.size()
//    int currentMin = Integer.MAX_VALUE
//    int currentLocation = -1
//    for ( i in 0..< bufferSize){
//      if (buffers[i] != null ) {
//        if (buffers[i].cloneNumber < currentMin) {
//          currentMin = buffers[i].cloneNumber
//          currentLocation = i
//        }
//      }
//    }
//    return currentLocation
//  }


  String toString() {
    String s = "SerialisedTestData: $data, $instanceNumber, $cloneNumber"
    return s
  }
}
