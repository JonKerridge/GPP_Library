package groovyParallelPatterns.functionals.groups

import groovyParallelPatterns.LocalDetails
import groovyParallelPatterns.functionals.workers.ThreePhaseWorker
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * The ListThreePhaseWorkerList process implements a group of {@link groovyParallelPatterns.functionals.workers.ThreePhaseWorker} connected
 * to input and output processes by channel lists.
 *
 * @param inputList The ChannelInputList from which input objects are read
 * @param outputList The ChannelOutputList to which each ThreePhaseWorker writes to one element
 * @param workers The number of ThreePhaseWorker processes
 * @param inputMethod The method in the input object used to input objects
 * @param workMethod The method in the input object used to process or do work on input objects
 * @param outFunction The name of the method in the input object that is the output function definition
 * @param lDetails specifies the {@link groovyParallelPatterns.LocalDetails} object describing the local worker class used by
 * each ThreePhaseWorker process.  All such processes use the same object.
 *
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 *
 *
 * @see groovyParallelPatterns.functionals.workers.ThreePhaseWorker
 */


@CompileStatic
class ListThreePhaseWorkerList implements CSProcess {
	
	ChannelInputList inputList
	ChannelOutputList outputList
	
	String inputMethod, workMethod, outFunction
	List dataModifier = null
	LocalDetails lDetails = null
	int workers

	String logPhaseName = ""
	String inputLogPropertyName = ""
	String outputLogPropertyName = ""

	void run(){
		assert (inputList.size() == outputList.size()) : "ListThreePhaseWorkerList: inputList and outputList must be same size"
		List network = (0 ..< workers).collect { e ->
			new ThreePhaseWorker ( input: (ChannelInput)inputList[e],
						 output: (ChannelOutput)outputList[e],
						 lDetails: lDetails == null ? null : lDetails,
						 inputMethod: inputMethod,
						 workMethod: workMethod,
						 outFunction: outFunction,
						 dataModifier : dataModifier == null ? null : dataModifier,
						 logPhaseName: logPhaseName == "" ?  "" : (String)"$e, "  + logPhaseName ,
						 inputLogPropertyName: inputLogPropertyName,
						 outputLogPropertyName: outputLogPropertyName)
		}
		new PAR (network).run()

	}

}
