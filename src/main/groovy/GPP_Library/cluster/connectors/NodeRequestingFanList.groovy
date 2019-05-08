package GPP_Library.cluster.connectors

import GPP_Library.UniversalSignal
import GPP_Library.UniversalTerminator
import groovyJCSP.*
import jcsp.lang.*

/**
 * The process NodeRequestingFanList makes a request for data on its request channel and reads the
 * response on its response channel.  The object is output to the channels of outList.  
 * The received data is output in turn to the elements of outList.<p>
 * 
 * <pre>
 * <b>Behaviour:</b>
 *     currentIndex = 0
 *     while true
 *         request.write(signal)
 *         outList[currentIndex].write( response.read() )
 *         currentIndex = (currentIndex + 1) modulus outList.size()   
 * </pre>
 * 
 * 
 * @param request A net output channel to which a request for data is written
 * @param response A net input channel from which an input data object is read
 * @param outList A channel output list to which the received data object is written
 */

class NodeRequestingFanList implements CSProcess {
	
	ChannelOutput request
	ChannelInput response
	ChannelOutputList outList
	
	void run(){
		int destinations = outList.size()
		int currentIndex = 0
		def signal = new UniversalSignal()
		def o = null
		boolean running = true
		while (running) {
			request.write(signal)
			o = response.read()
			if ( !( o instanceof UniversalTerminator)){
				outList[currentIndex].write(o)
				currentIndex = currentIndex + 1
				if (currentIndex == destinations) currentIndex = 0
			}
			else
				running = false
		}
		int c = currentIndex
		while ( c < destinations){
			outList[c].write(new UniversalTerminator())
			c = c + 1
		}
		c = 0
		while ( c < currentIndex){
			outList[c].write(new UniversalTerminator())
			c = c + 1
		}

	}

}

