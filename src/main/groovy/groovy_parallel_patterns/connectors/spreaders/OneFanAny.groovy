package groovy_parallel_patterns.connectors.spreaders

import groovy_parallel_patterns.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * OneFanAny is used to connect a source process to any number of destination processes such that  
 * an object read from input will be written to the next process that is waiting on outputAny.<p>
 * Once the UniversalTerminator is read it will be copied to all of the Any channel ends.
 * The incoming data is not modified in any manner.
 * <p>
 * <pre>
 * <b>Behaviour:</b>
 *     while true       
 *         outputAny.write( input.read() ) 
 * </pre>
 * @param input A one2one Channel used to read data objects from the previous process
 * @param outputAny A one2Any channel to which the incoming data object is written
 * @param destinations The number of receiving processes connected to the outputAny channel.<p>
 * 
 *
 */
@CompileStatic
class OneFanAny  implements CSProcess{

	ChannelInput input
	ChannelOutput outputAny
	int destinations = 0
	
	void run() {
		Object inputObject
		inputObject = input.read()
		while ( ! (inputObject instanceof UniversalTerminator ) ){
			outputAny.write(inputObject)
			inputObject = input.read()
		}
		for ( i in 1 .. destinations) {
			outputAny.write(inputObject)		// Universal terminator
		}
	}

}
