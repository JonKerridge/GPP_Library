package groovyParallelPatterns.connectors.reducers

import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * ListMergeOne is used to connect many source processes to one destination process such that
 * any incoming data object will be written to the next process using the output channel.  The inputList
 * is processed in a sequential manner such that all active inputs are given equal access to the
 * output bandwidth in the order of the inputList index.
 * <p>
 * Once the UniversalTerminator is read from the any element of the input channel list, a
 * UniversalTerminator object will be read from all the  source processes.
 * The process will then output a single UniversalTerminator object.
 * The incoming data is not modified in any manner.
 *
 * This was added in version 1.0.0-alpha-4 and the required change in gppBuilder
 * <p>
 *
 * <pre>
 * <b>Behaviour:</b>
 *     i = 0
 *     while true
 *        output.write( inputList[i].read() )
 *        i = i + 1
 *        if (i == inputList.size) i = 0
 * </pre>
 * @param output A one2one Channel used to write data objects to the next process
 * @param inputList A ChannelInputList from which incoming data objects are read in sequence
 */


@CompileStatic
class ListMergeOne implements CSProcess {

  ChannelInputList inputList
  ChannelOutput output

  void run() {
    int sources = inputList.size()
    int currentIndex
    currentIndex = 0
    boolean running
    running = true
    Object inputObject
    inputObject = null
    while (running) {
      inputObject = ((ChannelInput) inputList[currentIndex]).read()
//            println "read ${inputObject} from $currentIndex"
      if (!(inputObject instanceof UniversalTerminator)) {
        output.write(inputObject)
        currentIndex = currentIndex == sources - 1 ? 0 : currentIndex + 1
      } else {
        running = false
      }
    }
    int c = currentIndex == sources - 1 ? 0 : currentIndex + 1
//        println "terminating from $c at $currentIndex"
    if (c > 0) {
      while (c < sources) {
        ((ChannelInput) inputList[c]).read()
//                println "read 1: ${inputObject} from $c"
        c = c + 1
      }
    }
    c = 0
    while (c < currentIndex) {
      ((ChannelInput) inputList[c]).read()
//            println "read 2: ${inputObject} from $c"
      c = c + 1
    }
    output.write(inputObject)
  }

}

