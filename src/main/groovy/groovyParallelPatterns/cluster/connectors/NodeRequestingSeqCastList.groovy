package groovyParallelPatterns.cluster.connectors

import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
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
//		println "NRSCL invoked"
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
//		println "NRSCL running"
		while (running) {
//			println "NRSCL: sending request signal"
			request.write(signal)
//			println "NRSCL: awaiting response"
			o = response.read()
//			println "NRSCL: response has been read"
			if ( !( o instanceof UniversalTerminator)) {
				outList.broadcastSeq(o)
//				println "NRSCL: processing data ${o.toString()}"
			}
			else {
				running = false
//				println "NRSCL: terminating"
			}
		}
		outList.broadcastSeq(new UniversalTerminator())
//		println "NRSCL: terminated"
	}

}

