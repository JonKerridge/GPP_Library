package groovyParallelPatterns.connectors.spreaders

import groovyParallelPatterns.DataClass
import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The OneIndexedList process reads a data object from its input channel and depending
 * on the value returned by a call of indexFunction(indexBounds) will write the object to the corresponding element
 * of the channel output list outputLList.<p>
 * The indexFunction MUST check that the value of the indexProperty to ensure that<br>
 * 0 <= index < outputList.size().
 * This index function should return a negative return code if the index lies outside the range specified
 * that will then cause the process network to terminate
 *
 * @param input The channel input from which data objects are read
 * @param outputList the channel output list to which data objects are written
 * @param indexFunction A String containing the name of a function in the input object that returns
 * the element of the outputList to which the object should be written.
 * @param indexBounds a list of values passed to indexMethod that can be used to check
 * that the index returned is within required limits. Expected value is [ lowerBound, upperBound, divisor].
 * Divisor will typically be the size of the outputList to ensures a stronger check.
 */
@CompileStatic
class OneIndexedList extends DataClass implements CSProcess {

  ChannelInput input
  ChannelOutputList outputList
  String indexFunction    // the name of the object's index function
  List indexBounds = null  // indexFunction parameters ( [ lowerBound, upperBound, divisor)

  void run(){
    assert (indexBounds != null) : "OneIndexedList: bounds must be specified"
    assert (indexBounds.size() == 3) : "OneIndexedList: bounds specified as $indexBounds should be [lower, upper, divisor]"
    int destinations = outputList.size()
    Object inputObject
    inputObject = input.read()
    while ( ! (inputObject instanceof UniversalTerminator ) ){
      int index = callUserFunction(inputObject, indexFunction, indexBounds, 24)
      ((ChannelOutput)outputList[index]).write(inputObject)
      inputObject = input.read()
    }
    for ( i in 0 ..< destinations) ((ChannelOutput)outputList[i]).write(inputObject)
  }

}
