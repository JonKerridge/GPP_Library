package groovyParallelPatterns.cluster.connectors

import groovy.transform.CompileStatic
import groovyJCSP.ALT
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyParallelPatterns.UniversalTerminator
import jcsp.lang.CSProcess

class Net2List implements CSProcess{

  ChannelInputList inputList    // assumed to be a list of net channels
  ChannelOutputList outputList  // an internal channel list

  @Override
  @CompileStatic
  void run() {
    int listSize = inputList.size()   // input and outputs assumed to be of same size
    int terminated = 0
    int index
    ALT inputAlt = new ALT(inputList)
    while (terminated != listSize){
      index = inputAlt.fairSelect()
      def inputObject = inputList[index].read()
      if ( inputObject instanceof UniversalTerminator) terminated++
      outputList[index].write(inputObject)
    }
  }
}
