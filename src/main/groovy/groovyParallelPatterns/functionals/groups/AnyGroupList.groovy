package groovyParallelPatterns.functionals.groups

import groovyParallelPatterns.GroupDetails
import groovyParallelPatterns.functionals.workers.Worker
import groovy.transform.CompileStatic
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.Barrier
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

//mimport groovyParallelPatterns.functionals.workers.Worker
/**
 *
 * The AnyGroupList is an implementation of a Group in which all the outputs from the 
 * Worker processes are written to the ChannelOutputList outputList.  The input comes from a single
 * process using a one2any channel.  Typically the sending processes will be a sequence of an 
 * Emit and a FanOutAny process and the AnyGroupList will be the first such Group in a sequence of
 * such groups.  The advantage of using the AnyGroupList is that as members of the group finish
 * processing a data object they can move onto another one, if one should exist.  Thus the
 * Group mechanism allows the elements of processing to get out of sync with each other.
 * <p>
 * @param inputAny A ChannelInputfrom which each Worker process reads .
 * @param outputList A ChannelOutputList with as many channels as the value of workers. 
 * 				  	  Each Worker process writes to just one of the channels
 * @param gDetails A {@link groovyParallelPatterns.GroupDetails} object defining any local class of each worker, default to null
 * @param function The name of the function identifying the operation to be undertaken 
 * 					by the Worker processes
 * @param modifier Contains a possible modifier for the operation, with each Worker
 * 					accessing the element that corresponds to the index of the Worker.
 * @param workers The number of Worker processes that will be created 
 * 					when the Group is run
 * @param outData If true the worker processes will output each processed input object. If false the process will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output 
 *                only happens after the finalise method has been called. outData defaults to true.
 * @param synchronised If true the worker processes will synchronise with each other before they output any data,
 * 					thereby providing a means whereby each worker process will output results once the process 
 * 					reading from the final stage of the pipeline has read that data
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged.  Each process in the group will be uniquely indexed.
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.  
 * LogPropertyName must be specified if logPhaseName is specified 
 * 
 * @see groovyParallelPatterns.functionals.workers.Worker
 * @see jcsp.lang.Barrier					
 */
@CompileStatic
class AnyGroupList implements CSProcess{

	ChannelInput inputAny
	ChannelOutputList outputList
	GroupDetails gDetails = null
	String function
	List modifier = null
	int workers
	boolean outData = true
	boolean synchronised = false

	String logPhaseName = ""
	String logPropertyName = ""
	
	void run() {
        if (gDetails != null)            
            assert (workers == gDetails.workers): "AnyGroupList: Number of workers mismatch, Process exepcted $workers, Details specified ${gDetails.workers}"
		def barrier = null
		if (synchronised) barrier = new Barrier(workers)
//		if ((gDetails != null)&&(workers != gDetails.workers)) 
//			groovyParallelPatterns.DataClass.unexpectedReturnCode("AnyGroupList: Number of workers mismatch, Process exepcted $workers, Details specified ${gDetails.workers}", -1)
		List network = (0 ..< workers).collect { e ->
			new Worker ( input: inputAny,
						 output: (ChannelOutput)outputList[e],
						 lDetails: gDetails == null ? null : gDetails.groupDetails[e],
						 function: function,
						 dataModifier : modifier == null ? null : (List)modifier[e],
						 outData: outData,
						 barrier: synchronised ? (Barrier) barrier : null,
						 logPhaseName: logPhaseName == "" ?  "" : (String)"$e, "  + logPhaseName ,
						 logPropertyName: logPropertyName)
		}
		new PAR (network).run()

	}

}
