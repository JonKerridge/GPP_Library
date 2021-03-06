package groovy_parallel_patterns.cluster.connectors

import groovy_jcsp.ALT
import groovy_jcsp.ChannelInputList
import groovy_jcsp.ChannelOutputList
import groovy_parallel_patterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput

/**
 * The process OneNodeRequestedCastList reads an input data object.  It then read requests for data
 * from all the requesting Nodes and responds by writing a serialized version of the input data
 * object to all the elements of the response channel list.  The Request and Response channels will be
 * implemented as net channels.
 * <p>
 * @param input
 * @param request
 * @param response
 *
 */
class OneNodeRequestedCastList implements CSProcess {
	
	ChannelInput input
	ChannelInputList request
	ChannelOutputList response
	
	void run(){
		int nodes = request.size()	// assume list have same number of elements
		def alt = new ALT(request)
		def o = input.read()
		while (!(o instanceof UniversalTerminator)){
			for ( index in 0 ..< nodes) request[index].read()
//			println "ONRL has read all signals and processing ${o.toString()}"
//			def smp = o.serialize() // removed in v 1.0.9
//			println "ONRL has serialized to ${smp.toString()}"
			for ( index in 0 ..< nodes) response[index].write(o) // o MUST be serializable
//			println "ONRL has completed output "
				o = input.read()
		}
//		println "ONRL Terminating"
		for ( index in 0 ..< nodes) request[index].read()
		for ( index in 0 ..< nodes) response[index].write(new UniversalTerminator())
//		println "ONRL has terminated"
	}

}
