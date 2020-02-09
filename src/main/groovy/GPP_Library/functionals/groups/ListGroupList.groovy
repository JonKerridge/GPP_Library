package GPP_Library.functionals.groups

import GPP_Library.GroupDetails
import GPP_Library.functionals.workers.Worker
import groovy.transform.CompileStatic

//mimport GPP_Library.functionals.workers.Worker

import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.Barrier
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 *
 * A ListGroupList is essentially a loop unrolling of a sequential FOR statement such
 * that each loop of the for is allocated to a different Worker process thereby 
 * enabling the implementation of a parallel For loop.<p>
 * It is assumed that it is not possible to write to the same (or part of a) data object
 * in more than ONE of the Workers.  In other words parallel access to a data object
 * is not permitted for write operations where the operation does not have exclusive 
 * access to the data object.  This is a requirement but is not checked by the system.
 * There is no synchronisation between the Workers in the group unless synchronised is set true.<p>
 * <p>
 * @param inputList A ChannelInputList with as many channels as the value of workers. 
 * 					Each Worker process reads from just one element of the input.
 * @param outputList A ChannelOutputList with as many channels as the value of workers. 
 * 				  	  Each Worker process writes to just one of the channels
 * @param gDetails A {@link GPP_Library.GroupDetails} object defining any local class of each worker, default to null
 * @param function The name of the function identifying the operation to be undertaken 
 * 					by the Worker processes.  They all undertake the same operation.
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
 * @see GPP_Library.functionals.workers.Worker
 * @see jcsp.lang.Barrier					
*/

@CompileStatic
class ListGroupList implements CSProcess{

	ChannelInputList inputList
	ChannelOutputList outputList
	GroupDetails gDetails
	String function
	List modifier = null
	int workers
	boolean outData = true
	boolean synchronised = false
	
	String logPhaseName = ""
	String logPropertyName = ""

	void run() {
        if (gDetails != null)            
            assert (workers == gDetails.workers): "ListGroupList: Number of workers mismatch, Process exepcted $workers, Details specified ${gDetails.workers}"
		def barrier = null
//		if ((gDetails != null)&&(workers != gDetails.workers)) 
//			gpp.DataClass.unexpectedReturnCode("ListGroupList: Number of workers mismatch, Process expected $workers, Details specified ${gDetails.workers}", -1)
		if (synchronised) barrier = new Barrier(workers)
		List network = (0 ..< workers).collect { e ->
			new Worker ( input: (ChannelInput)inputList[e],
						 output: (ChannelOutput)outputList[e],
						 lDetails: gDetails == null ? null : gDetails.groupDetails[e],
						 function: function,
						 dataModifier : modifier == null ? null : (List)modifier[e],
						 outData: outData,
						 barrier: synchronised ? (Barrier)barrier : null,
						 logPhaseName: logPhaseName == "" ?  "" : (String)"$e, "  + logPhaseName ,
						 logPropertyName: logPropertyName)
		}
		new PAR (network).run()

	}

}
