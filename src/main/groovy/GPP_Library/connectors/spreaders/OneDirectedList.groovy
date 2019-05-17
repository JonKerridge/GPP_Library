package GPP_Library.connectors.spreaders

import GPP_Library.UniversalTerminator
import groovyJCSP.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The OneDirectedList process reads a data object from its input channel and depending
 * on the value contained in its indexProperty will write the object to the corresponding element
 * of the channel output list outputLList.<p>
 * The process does NOT check that the value of the indexProperty to ensure that<br>
 * 0 <= indexProperty < outputList.szie()
 * 
 * @param input The channel input from which data objects are read
 * @param outputList the channel output list to which data objects are written
 * @param indexProperty A String containing the property of the input object that
 * conatins the subscript of the outputList element which is written.
 *
 */
//@CompileStatic
class OneDirectedList implements CSProcess {	
	
	ChannelInput input
	ChannelOutputList outputList
	String indexProperty
	
	void run(){
		int destinations = outputList.size()
		def o = input.read()
		while ( ! (o instanceof UniversalTerminator ) ){
			int destination = (int) o.getProperty(indexProperty)
			assert ((destination >= 0) && (destination < destinations)): "OneDirectedList:  $indexProperty  < 0 or >= $destinations"
//			println "writing to $batch from ${o.toString()}"
			((ChannelOutput)outputList[destination]).write(o)
			o = input.read()
		}
//		println "ODL: has read UT"
		for ( i in 0 ..< destinations) ((ChannelOutput)outputList[i]).write(o)
//		println "ODL: has sent $destinations UT objects"
	}

}
