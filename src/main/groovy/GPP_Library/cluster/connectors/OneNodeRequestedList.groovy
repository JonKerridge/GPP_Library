package GPP_Library.cluster.connectors

import GPP_Library.UniversalTerminator
import groovyJCSP.*
import jcsp.lang.*

/**
 * The process OneNodeRequestedList reads an input data object.  It then reads a request for data
 * and responds by writing the data object to the corresponding element of the response channel list.
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
		def o = input.read()
		while (!(o instanceof UniversalTerminator)){
			index = alt.fairSelect()
//			println "ONRL at processed at $index"
			request[index].read()
//			println "ONRL has read signal and processing ${o.toString()}"
			def smp = o.serialize()
//			println "ONRL has serialized to ${smp.toString()}"
			response[index].write(smp) 
//			println "ONRL has completed output "
			o = input.read()
		}
		int terminated = 0
		while ( terminated < nodes){
			index = alt.fairSelect()
			request[index].read()
			response[index].write(new UniversalTerminator())
			terminated = terminated + 1
		}
	}

}
