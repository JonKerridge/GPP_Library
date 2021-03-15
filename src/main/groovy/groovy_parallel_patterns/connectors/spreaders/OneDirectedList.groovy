package groovy_parallel_patterns.connectors.spreaders

import groovy_parallel_patterns.UniversalTerminator
import groovy_jcsp.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The OneDirectedList process reads a data object from its input channel and depending
 * on the value contained in its indexProperty will write the object to the corresponding element
 * of the channel output list outputLList.<p>
 * The process does NOT check that the value of the indexProperty to ensure that<br>
 * 0 <= indexProperty < outputList.szie()
 * 
 * @param input The channel input from which data objects are read
 * @param outputList the channel output list to which data objects are written
 * @param indexProperty A String containing the property of the input object that
 * contains the subscript of the outputList element to which will be written.
 *
 */
//@CompileStatic
class OneDirectedList implements CSProcess {	
	
	ChannelInput input
	ChannelOutputList outputList
	String indexProperty
	
	void run(){
		int destinations = outputList.size()
		Object inputObject
		inputObject = input.read()
		while ( ! (inputObject instanceof UniversalTerminator ) ){
			int destination = (int) inputObject.getProperty(indexProperty)
			assert ((destination >= 0) && (destination < destinations)): "OneDirectedList:  $indexProperty  < 0 or >= $destinations"
			((ChannelOutput)outputList[destination]).write(inputObject)
			inputObject = input.read()
		}
		for ( i in 0 ..< destinations) ((ChannelOutput)outputList[i]).write(inputObject)
	}

}
