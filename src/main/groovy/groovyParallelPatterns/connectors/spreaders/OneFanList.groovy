package groovyParallelPatterns.connectors.spreaders

import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ChannelOutputList
import jcsp.lang.*

/**
 * OneFanList is used to connect a source process to any number of destination processes such that 
 * objects read from input are output in a circular pattern through the elements of the outputList channels.<p> 
 * The incoming data object will be written to the next process in sequence. 
 * Once the UniversalTerminator is read it will be copied to all of the output channel ends.
 * The incoming data is not modified in any manner.
 * <p>
 * <pre>
 * <b>Behaviour:</b>
 *     c = 0
 *     while true       
 *         outputList[c].write( input.read() ) 
 *         c = (c + 1) modulus outputList.size()
 * </pre>
 * @param input A one2one Channel used to read data objects from the previous process
 * @param outputList An OutputChannelList to which the incoming data object is written in sequence
 *
 */
@CompileStatic
class OneFanList  implements CSProcess{

	ChannelInput input
	ChannelOutputList outputList
	
	void run() {
		int destinations = outputList.size()
		int currentIndex, c
		currentIndex = 0
		Object inputObject
		inputObject = input.read()
		while ( ! (inputObject instanceof UniversalTerminator ) ){
			((ChannelOutput)outputList[currentIndex]).write(inputObject)
			currentIndex = currentIndex + 1
			if (currentIndex == destinations) currentIndex = 0
			inputObject = input.read()
		}
		c = currentIndex
		while ( c < destinations){
			((ChannelOutput)outputList[c]).write(inputObject)
			c = c + 1
		}
		c = 0
		while ( c < currentIndex){
			((ChannelOutput)outputList[c]).write(inputObject)
			c = c + 1
		}
	}
}

