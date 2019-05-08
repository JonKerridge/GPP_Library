package GPP_Library.cluster.connectors

import GPP_Library.UniversalSignal
import GPP_Library.UniversalTerminator
import groovyJCSP.*
import jcsp.lang.*

/**
 * The process NodeRequestingSeqCastList makes a request for data on its request channel and reads the
 * response on its response channel.  The object is output to all of the outputAny channel ends in seqeuence.<p>
 * 
 * <pre>
 * <b>Behaviour:</b>
 *     while true
 *         request.write(signal)
 *         outputList.broadcastSeq( response.read() )
 * </pre>
 * 
 * @param request A net output channel to which a request for data is written
 * @param response A net input channel from which an input data object is read
 * @param outputList A channel output list to which the received data object is written to all elements in sequence
 */

class NodeRequestingSeqCastList implements CSProcess {
	
	ChannelOutput request
	ChannelInput response
	ChannelOutputList outList
	
	void run(){
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
		while (running) {
			request.write(signal)
			o = response.read()
			if ( !( o instanceof UniversalTerminator))
				outList.broadcastSeq(o)
			else
				running = false
		}
		outList.broadcastSeq(new UniversalTerminator())

	}

}

