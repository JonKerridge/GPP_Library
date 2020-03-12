package groovyParallelPatterns.connectors.reducers

import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * ListFanOne is used to connect many source processes to one destination process such that
 * any incoming data object will be written to the next process using the output channel.  The inputList
 * is processed in a fair manner such that all active inputs are given equal access to the
 * output bandwidth.
 * <p>
 * Once the UniversalTerminator is read from the any element of the input channel list, a
 * UniversalTerminator object will be read from all the  source processes.
 * The process will then output a single UniversalTerminator object.
 * The incoming data is not modified in any manner.
 * <p>
 *
 * <pre>
 * <b>Behaviour:</b>
 *     alt = new ALT(inputList)
 *     while true
 *        i = alt.fairSelect()
 *        output.write( inputList[i].read() )
 * </pre>
 * @param output A one2one Channel used to write data objects to the next process
 * @param inputList A ChannelInputList from which incoming data objects are read such that
 * the elements of the input channel list are given an equal share of the available input bandwidth
 */

//@CompileStatic
@CompileStatic
class ListFanOne  implements CSProcess{

	ChannelInputList inputList
	ChannelOutput output

	void run() {
		int sources = inputList.size()
		def alt = new ALT(inputList)
		int currentIndex, terminated
		currentIndex = 0
		boolean running
		running = true
		terminated = 0
		Object outputObject
		while ( running ){
			currentIndex = alt.fairSelect()
			outputObject = ((ChannelInput)inputList[currentIndex]).read()
			if ( !( outputObject instanceof UniversalTerminator)){
				output.write(outputObject)
			}
			else {
				terminated = terminated + 1
				if ( terminated == sources ) running = false
			}
		}
		output.write(outputObject)
	}

}

