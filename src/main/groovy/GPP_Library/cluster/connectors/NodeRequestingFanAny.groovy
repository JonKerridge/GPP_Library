package GPP_Library.cluster.connectors

import GPP_Library.*
import groovyJCSP.*
import jcsp.lang.*
import jcsp.net2.*

/**
 * The process NodeRequestingFanAny makes a request for data on its request channel and reads the
 * response on its response channel.  The object is output to any of the outputAny channels.<p>
 *
 * <pre>
 * <b>Behaviour:</b>
 *     while true
 *         outRequestAny.write(signal)
 *         outputAny.write( inResponseAny.read() )   
 * </pre>
 * 
 * @param request A net output channel to which a request for data is written
 * @param response A net input channel from which an input data object is read
 * @param outputAny A one2any channel to which the received data object is written
 * @param destinations An int containing the number of processes connected to the any end of the outputAny channel
 */

class NodeRequestingFanAny implements CSProcess {
	
	ChannelOutput request
	ChannelInput response
	ChannelOutput outputAny
	int destinations
	
	void run(){
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
		while (running) {
//			println "NRFA: sending signal"
			request.write(signal)
//			println "NRFA: has written signal"
			o = response.read()
//			println "NRFA: read response ${o.toString()}"
			if ( !( o instanceof UniversalTerminator)){
				outputAny.write(o)
//				println "NRFA: has written object ${o.toString()}"
			}
			else
				running = false
		}
		for ( i in 1..destinations) outputAny.write(new UniversalTerminator())
//		println "NRFA has terminated"
	}
}

