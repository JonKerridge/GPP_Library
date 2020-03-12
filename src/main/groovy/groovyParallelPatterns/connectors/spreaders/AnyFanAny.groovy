package groovyParallelPatterns.connectors.spreaders

import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * AnyFanAny is used to connect many source processes to many destination process such that 
 * any incoming data object will be written to the next process using the output channel. 
 * It essentially provides a one place buffer in that as soon as input is ready 
 * it can be read and then written to any ready output channel. <p>

 * Once the UniversalTerminator is read from the any end of the input channel, a
 * tally will be kept until all the UniversalTerminator objects are read from all the source processes.
 * The process will then output a UniversalTerminator object to each of the destination processes.<p>
 * The incoming data is not modified in any manner.<p>
 * 
 * <pre>
 * <b>Behaviour:</b>
 *     while true       
 *         outputAny.write( inputAny.read() ) 
 * </pre>
 * @param outputAny A one2any Channel used to write data objects to the next process
 * @param inputAny An any2one channel from which incoming data objects are read
 * @param sources The number of source processes. 
 * @param destinations The number of destination processes.
 * <p>
 */
@CompileStatic
class AnyFanAny  implements CSProcess{

	ChannelInput inputAny
	ChannelOutput outputAny
	int sources = 0
	int destinations = 0
	
	void run() {
		boolean running
		int terminated
		Object inputObject

		running = true
		terminated = 0
		inputObject = inputAny.read()
		while ( running ){
			if ( !( inputObject instanceof UniversalTerminator)){
				outputAny.write(inputObject)
				inputObject = inputAny.read()
			}
			else {
				terminated = terminated + 1
				if (terminated == sources ) running = false
				else inputObject = inputAny.read()
			}
		}
		for ( i in 1..destinations) outputAny.write(inputObject)
	}

}
