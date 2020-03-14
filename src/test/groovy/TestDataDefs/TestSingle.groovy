package TestDataDefs

import groovyParallelPatterns.DataClass

class TestSingle extends DataClass {
  List<Integer> consts = []
  static String init = "initClass"
  static String create = "createClass"
  static String nullFunc = "nullFunc"
  static int len

  int initClass(List d) {
    len = d[0]
    return completedOK
  }

  int createClass(List d){
    0.upto(len) { i ->
      consts << i
    }
    return normalContinuation
  }

  int nullFunc(List p){
    // do nothing
    return completedOK
  }


}