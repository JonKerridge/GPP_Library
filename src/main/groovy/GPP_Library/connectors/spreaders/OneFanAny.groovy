package GPP_Library.connectors.spreaders

import GPP_Library.UniversalTerminator
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
 * @param destinations The number of receiving processes connected to the Any channel end.<p>
 * 
 *
 */
@CompileStatic
class OneFanAny  implements CSProcess{

	ChannelInput input
	ChannelOutput outputAny
	int destinations = 0
	
	void run() {
		def o = input.read()
		while ( ! (o instanceof UniversalTerminator ) ){
			outputAny.write(o)
			o = input.read()
		}
		for ( i in 1 .. destinations) {
			outputAny.write(o)
		}
	}

}
