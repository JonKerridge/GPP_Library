package groovy_parallel_patterns.cluster.connectors

import groovy.transform.CompileStatic
import groovy_jcsp.ALT
import groovy_jcsp.ChannelInputList
import groovy_jcsp.ChannelOutputList
import groovy_parallel_patterns.UniversalTerminator
import jcsp.lang.CSProcess

/**
 * NetInputs2List is assumed t6o be the first process in the  Collect phase of a Cluster
 * based solution.  It will capture inputs from other cluster elements which are assumed
 * to have a List2Net process as the last in the cluster definition.
 *
 * The NetInputs2List process will read from clusters x cores channels; where cores is the
 * number of workers used in each cluster.
 *
 * In use the NetInputs2List process will be followed by a ListFanOne, ListMergeOne or
 * ListGroupCollect process.  The latter will be useful in cases where each output
 * stram of data can be collected in isolation from the others.
 */
class NetInputs2List implements CSProcess{

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
