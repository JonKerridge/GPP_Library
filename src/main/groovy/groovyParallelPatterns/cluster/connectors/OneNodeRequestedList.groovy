package groovyParallelPatterns.cluster.connectors

import groovyParallelPatterns.UniversalTerminator
import groovyJCSP.*
import jcsp.lang.*

/**
 * The process OneNodeRequestedList reads an input data object.  It then reads a request for data
 * and responds by writing a serialized version of the input data object to the
 * corresponding element of the response channel list.  The Request and Response channels will be
 * implemented as net channels.
 * <p>
 * @param input
 * @param request
 * @param response
 *
 */
class OneNodeRequestedList implements CSProcess {
	
	ChannelInput input
	ChannelInputList request
	ChannelOutputList response
	
	void run(){
		int nodes = request.size()	// assume list have same number of elements
		def alt = new ALT(request)
		int index = -1
//		println "ONRL running"
		def o = input.read()
//		println "ONRL has read ${o.toString()}"
		while (!(o instanceof UniversalTerminator)){
			index = alt.fairSelect()
//			println "ONRL processed at $index"
			request[index].read()
//			println "ONRL has read signal $index and processing ${o.toString()}"
//			def smp = o.serialize() // removed in v 1.0.9
//			println "ONRL has serialized to ${smp.toString()}"
			response[index].write(o) // o MUST be serializable
//			println "ONRL has completed output "
			o = input.read()
//			println "ONRL has read ${o.toString()}"
		}
//		println "ONRL Terminating"
		int terminated = 0
		while ( terminated < nodes){
			index = alt.fairSelect()
//			println "ONRL-T processed at $index"
			request[index].read()
//			println "ONRL-T has read from $index"
			response[index].write(new UniversalTerminator())
//			println "ONRL-T written UT to $index"
			terminated = terminated + 1
		}
//		println "ONRL has terminated"
	}

}
