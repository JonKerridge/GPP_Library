package groovy_parallel_patterns.functionals.groups

import groovy_parallel_patterns.GroupDetails
import groovy_parallel_patterns.functionals.workers.Worker
import groovy.transform.CompileStatic
import groovy_jcsp.ChannelOutputList
import groovy_jcsp.PAR
import jcsp.lang.Barrier
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 *
 * The AnyGroupList is an implementation of a Group in which all the outputs from the 
 * Worker processes are written to the ChannelOutputList outputList.  The input comes from a single
 * process using an any channel.  Typically the sending processes will be a sequence of an
 * Emit and a FanOutAny process and the AnyGroupList will be the first such Group in a sequence of
 * such groups.  The advantage of using the AnyGroupList is that as members of the group finish
 * processing a data object they can move onto another one, if one should exist.  Thus the
 * Group mechanism allows the elements of processing to get out of sync with each other.
 * <p>
 * @param inputAny A Channel from which each Worker process reads .
 * @param outputList A ChannelOutputList with as many channels as the value of {@code workers}.
 * 				  	  Each Worker process writes to just one of the output channels
 * @param gDetails A {@link groovy_parallel_patterns.GroupDetails} object defining any local
 * class of each worker, defaults to null.
 * @param function The name of the function identifying the operation to be undertaken 
 * 					by the Worker processes, it must be specified
 * @param modifier Contains a possible modifier for the operation, with each Worker
 * 					accessing the element that corresponds to the index of the Worker.
 * 					Each element is itself a list of values
 * @param workers The number of Worker processes that will be created
 * 					when the Group is run
 * @param outData If true the worker processes will output each processed input object.
 * If false the process will output the workerClass once only, after it has processed all the
 * input data objects. The output only happens after the finalise method has been called.
 * outData defaults to true.
 * @param synchronised If true the worker processes will synchronise with each other before
 * they output any data, thereby providing a means whereby each worker process will output
 * results once the process reading from the final stage of the pipeline has read that data.
 * @param logPhaseName an optional string property, which if specified indicates that the process
 * should be logged otherwise the process will not be logged.  Each process in the group will
 * be uniquely indexed.
 * @param logPropertyName the name of a property in the input object that will uniquely identify
 * an instance of the object.  LogPropertyName must be specified if logPhaseName is specified
 * 
 * @see groovy_parallel_patterns.functionals.workers.Worker
 * @see jcsp.lang.Barrier					
 */
@CompileStatic
class AnyGroupList implements CSProcess{

	ChannelInput inputAny
	ChannelOutputList outputList
	GroupDetails gDetails = null
	String function = ""
	List <List> modifier = null
	int workers = 0
	boolean outData = true
	boolean synchronised = false

	String logPhaseName = ""
	String logPropertyName = ""
	
	void run() {
		assert function != "": "AnyGroupList: function not specified"
		assert workers > 0: "AnyGroupList: workers not specified"
        if (gDetails != null)
            assert (workers == gDetails.workers): "AnyGroupList: Number of workers mismatch, Process exepcted $workers, Details specified ${gDetails.workers}"
		Barrier barrier
		if (synchronised) barrier = new Barrier(workers)
//		if ((gDetails != null)&&(workers != gDetails.workers)) 
//			groovy_parallel_patterns.DataClass.unexpectedReturnCode("AnyGroupList: Number of workers mismatch, Process exepcted $workers, Details specified ${gDetails.workers}", -1)
		List network = (0 ..< workers).collect { e ->
			new Worker ( input: inputAny,
						 output: (ChannelOutput)outputList[e],
						 lDetails: gDetails == null ? null : gDetails.groupDetails[e],
						 function: function,
						 dataModifier : modifier == null ? null : modifier[e],
						 outData: outData,
						 barrier: synchronised ? (Barrier) barrier : null,
						 logPhaseName: logPhaseName == "" ?  "" : (String)"$e, "  + logPhaseName ,
						 logPropertyName: logPropertyName)
		}
		new PAR (network).run()

	}

}
