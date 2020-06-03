package groovyParallelPatterns.cluster.connectors

import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
import groovyJCSP.*
import jcsp.lang.*

/**
 * The process NodeRequestingParCastList makes a request for data on its request channel and reads the
 * response on its response channel.  The object is output to all of the channels in the 
 * outList in parallel.<p>
 * 
 * <pre>
 * <b>Behaviour:</b>
 *     currentIndex = 0
 *     while true
 *         request.write(signal)
 *         outputList.broadcast( response.read() )
 * </pre>
 * 
 * 
 * @param request A net output channel to which a request for data is written
 * @param response A net input channel from which an input data object is read
 * @param outList A list of output channels to which the received data object is written in parallel to all elements of the list
 */

class NodeRequestingParCastList implements CSProcess {
	
	ChannelOutput request
	ChannelInput response
	ChannelOutputList outList
	int id

	void run(){
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
		while (running) {
			request.write(signal)
			o = response.read()
			if (!(o instanceof UniversalTerminator)) {
//				println "$id broadcasting ${o.instanceNumber}"
				outList.broadcast(o)
			}
			else
				running = false
		}
		outList.broadcast(new UniversalTerminator())

	}

}

