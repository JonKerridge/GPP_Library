package groovy_parallel_patterns.cluster.connectors

import groovy_jcsp.ALT
import groovy_jcsp.ChannelInputList
import groovy_jcsp.ChannelOutputList
import groovy_parallel_patterns.UniversalTerminator
import jcsp.lang.CSProcess

/**
 * ListNet takes its input from and internal channel list and then copies the input
 * object to the corresponding component of the output net channel list.  The process
 * provides no buffering and also waits after reading to write the object
 */

class List2Net implements CSProcess{

  ChannelInputList inputList    // an internal channel list
  ChannelOutputList outputList  // assumed to be a list of net channels

  @Override
  void run() {
    int listSize = inputList.size()   // input and outputs assumed to be of same size
    int terminated = 0
    int index
    ALT inputAlt = new ALT(inputList)
    while (terminated != listSize){
      index = inputAlt.fairSelect()
      def inputObject = inputList[index].read() // inputObject extends DataClass
      if ( inputObject instanceof UniversalTerminator) {
        terminated++
        outputList[index].write(inputObject)  // input object is Universal Terminator
      }
      else {
        outputList[index].write(inputObject)
      }
    }
  }
}
