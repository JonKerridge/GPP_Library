package groovy_parallel_patterns.connectors.spreaders

import groovy_parallel_patterns.UniversalTerminator
import groovy.transform.CompileStatic
import groovy_jcsp.*
import jcsp.lang.*

/**
 * OneParCastList is used to connect a source process to any number of destination processes such that 
 * an object will be read from input and then copied in parallel to all the outputList channels.<p>
 * Once the UniversalTerminator is read it will be copied to all of the output channel ends.
 * The incoming data is not modified in any manner.
 * <p>
 * <pre>
 * <b>Behaviour:</b>
 *     while true
 *         outputList.broadcast(input.read())
 * </pre>
 * <p>
 *  where broadcast is a method in groovyJCSP; it writes a clone of the inputObject to
 *  all elements of the outputList in parallel<p>
 * @param input A one2one Channel used to read data objects from the previous process
 * @param outputList An OutputChannelList to which the incoming data object is written in parallel
 *
 */
@CompileStatic
class OneParCastList  implements CSProcess{

	ChannelInput input
	ChannelOutputList outputList

	void run() {
		Object inputObject
		inputObject = input.read()
		while ( ! (inputObject instanceof UniversalTerminator ) ){
			outputList.broadcast(inputObject)
			inputObject = input.read()
		}
		outputList.broadcast(inputObject)
		
	}
}


