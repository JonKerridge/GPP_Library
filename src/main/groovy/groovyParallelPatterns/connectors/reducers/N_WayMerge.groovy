package groovyParallelPatterns.connectors.reducers

import groovyParallelPatterns.DataClass
import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The N_WayMerge process accepts inputs from its inputList and uses the method mergeChoice to determine
 * which of the available inputs will be written to the output channel in such a way as to maintain an
 * overall sorted order.  It is presumed that the input data stream will be read in a sorted order.<p>
 *
 *@param inputList the channel input list used to read input data objects
 *@param output the channel to which data objects are written
 *@param inClassName A String containing the name of the class that the process will read
 *@param mergeChoice the name of a method contained in the input class that chooses the next input
 *object to write to the output channel<p>
 *
 *The method mergeChoice has the declaration:<br>
 *static int mergeChoice (List <inputClass> buffers)
 *
 *Each input channel reads its input into one of the elements of the buffers<br>
 *mergeChoice then returns the index of the element of buffers that has been selected<br>
 *It is guaranteed that at least one of the buffers elements will contain valid data<br>
 *If a terminating value has been read from a channel list element then the corresponding
 *buffers element will be null.
 *
 */
@CompileStatic
class N_WayMerge extends DataClass implements CSProcess{

	ChannelInputList inputList
	ChannelOutput output
	String inClassName	// the name of the class that will be input and contains the static merge process
	String mergeChoice		// the name of a method in the input object o

	void run(){
		int sources = inputList.size()
		int terminated, returnCode
		terminated = 0
		List buffers = []
		for ( i in 0 ..< sources) buffers[i] = null
		Object inputObject
		inputObject = null
		boolean running
		// read in the initial values from each inputList element
		for ( i in 0 ..< sources){
			inputObject =  ((ChannelInput)inputList[i]).read()
			if ( ! (inputObject instanceof UniversalTerminator)){
				buffers[i] = inputObject
			}
			else {
				terminated = terminated + 1
			}
		}
		running = terminated != sources
		Class dataClass = Class.forName(inClassName)
		def dc = dataClass.newInstance()
		while (running){
			//returnCode = dc.&"$mergeChoice"(buffers)
			returnCode = callUserFunction(dc, mergeChoice, buffers, 25)
			if ((returnCode >= 0) && (returnCode < sources)) {
				output.write(buffers[returnCode])
				inputObject =  ((ChannelInput)inputList[returnCode]).read()
				if ( ! (inputObject instanceof UniversalTerminator))
					buffers[returnCode] = inputObject
				else {
					buffers[returnCode]= null
					terminated = terminated + 1
					running = terminated != sources
				}
			}
//			else
//				groovyParallelPatterns.DataClass.unexpectedReturnCode("N_WayMerge: error during $mergeChoice", returnCode)
		}
		output.write(new UniversalTerminator())
	}

}
