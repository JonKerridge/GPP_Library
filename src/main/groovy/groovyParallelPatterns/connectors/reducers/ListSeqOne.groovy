package groovyParallelPatterns.connectors.reducers

import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelOutput

/**
 * ListSeqOne is used to connect many source processes to one destination process such that 
 * incoming data objects will be read in sequence from all the elements of the inpitList
 * and then written in sequence to the next process using the output channel.
 * <p>
 * Once a UniversalTerminator object has been read from all the  source processes,
 * the process will then output a single UniversalTerminator object.
 * The incoming data is not modified in any manner.
 * <p>
 * 
 * <pre>
 * <b>Behaviour:</b>
 *     while true  
 *         values = inputList.readSeq()
 *         for ( i in 0..< inputList.size()) output.write( values[i] ) 
 * </pre>
 * 
 * <p>
 * @param output A one2one Channel used to write data objects to the next process
 * @param inputList A ChannelInputList from which incoming data objects are read in sequence
 */
@CompileStatic
class ListSeqOne  implements CSProcess{

	ChannelInputList inputList
	ChannelOutput output
	
	void run() {
		int elements = inputList.size()
		List valueList
		valueList  = (List)inputList.readSeq()
		while ( ! (valueList[0] instanceof UniversalTerminator ) ){
			for ( e in 0 ..< elements) output.write(valueList[e])
			valueList  = (List)inputList.readSeq()
		}
		output.write(valueList[0])
	}
}
