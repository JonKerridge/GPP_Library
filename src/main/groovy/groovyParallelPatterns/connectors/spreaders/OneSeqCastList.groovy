package groovyParallelPatterns.connectors.spreaders

import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput

/**
 * OneSeqCastList is used to connect a source process to any number of destination processes such that 
 * an object will be read from input and then copied in sequence to all the outputList channels.<p>
 * Once the UniversalTerminator is read it will be copied to all of the output channel ends.
 * The incoming data is not modified in any manner.
 * <p>
 * <pre>
 * <b>Behaviour:</b>
 *     while true
 *         outputList.broadcastSeq(input.read())
 * </pre>
 * <p>
 *  where broadcastSeq is a method in groovyJCSP; it writes a clone of the inputObject to
 *  all elements of the outputList in sequence
 *  <p>
 * @param input A one2one Channel used to read data objects from the previous process
 * @param outputList An OutputChannelList to which the incoming data object is written in sequence
 *
 */
@CompileStatic
class OneSeqCastList  implements CSProcess{
	
	ChannelInput input
	ChannelOutputList outputList

	void run() {
		Object inputObject
		inputObject = input.read()
		while ( ! (inputObject instanceof UniversalTerminator ) ){
			outputList.broadcastSeq(inputObject)
			inputObject = input.read()
		}
		outputList.broadcastSeq(inputObject)
		
	}

}
