package GPP_Library.connectors.reducers

import GPP_Library.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * AnyFanOne is used to connect many source processes to one destination process such that
 * any incoming data object will be written to the next process using the output channel.
 * <p>
 * Once the UniversalTerminator is read from the any end of the input channel, a
 * tally will be kept until all the UniversalTerminator objects are read from all the source processes.
 * The process will then output a single UniversalTerminator object.
 * The incoming data is not modified in any manner.
 * <p>
 *
 * <pre>
 * <b>Behaviour:</b>
 *     while true
 *         output.write( inputAny.read() )
 * </pre>
 *
 * @param output A one2one Channel used to write data objects to the next process
 * @param inputAny An any2one channel from which incoming data objects are read
 * @param sources The number of source processes connected to the Any channel end. <p>
 *
 */

@CompileStatic
class AnyFanOne  implements CSProcess{

	ChannelInput inputAny
	ChannelOutput output
	int sources = 0

	void run() {
		def o = inputAny.read()
		boolean running = true
		int terminated = 0
		while ( running ){
			if ( !( o instanceof UniversalTerminator)){
				output.write(o)
//				println "AFO has written ${o.toString()}"
				o = inputAny.read()
			}
			else {
				terminated = terminated + 1
				if (terminated == sources ) running = false
				else o = inputAny.read()
			}
		}
		output.write(o)
//		println "AFO has terminated"
	}

}
