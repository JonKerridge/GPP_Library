package groovyParallelPatterns.functionals.groups

import groovyParallelPatterns.GroupDetails
import groovyParallelPatterns.functionals.workers.Worker
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 *
 * The AnyGroupAny is an implementation of a Group in which inputs are read from a one2any channel
 *  and all the outputs of the 
 * Worker processes are written to an any2One channel so they can be connected to 
 * a single process.  
 * <p>
 * @param inputAny A the any end of a one2any channel from which input objects are read. 
 * 					Each Worker process reads from just one element of the input.
 * @param outputAny The any channel end upon which processed data objects are written.  The
 * 					channel must be an any2one or any2any channel and this process 
 * 					must be allocated the any end of the channel
 * @param gDetails A {@link groovyParallelPatterns.GroupDetails} object defining any local class of each worker, default to null
 * @param function The name of the function identifying the operation to be undertaken 
 * 					by the Worker processes
 * @param modifier Contains a possible modifier for the operation, with each Worker
 * 					accessing the element that corresponds to the index of the Worker.
 * @param workers The number of Worker processes that will be created 
 * 					when the Group is run
 * @param outData If true the process will output each processed input object. If false the process will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output 
 *                only happens after the finalise method has been called. outData defaults to true.
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged.  Each process in the group will be uniquely indexed.
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.  
 * LogPropertyName must be specified if logPhaseName is specified 
 *
 *
 * @see jcsp.lang.Barrier
 * @see groovyParallelPatterns.functionals.workers.Worker
 */

@CompileStatic
class AnyGroupAny implements CSProcess{

    ChannelInput inputAny
    ChannelOutput outputAny
    GroupDetails gDetails = null  // one entry per worker
    String function
    List modifier = null
    int workers
    boolean outData = true

    String logPhaseName = ""
    String logPropertyName = ""

    void run() {
        if (gDetails != null)
            assert (workers == gDetails.workers): "AnyGroupAny: Number of workers mismatch, Process exepcted $workers, Details specified ${gDetails.workers}"
        //		if ((gDetails != null)&&(workers != gDetails.workers))
        //			groovyParallelPatterns.DataClass.unexpectedReturnCode("AnyGroupAny: Number of workers mismatch, Process exepcted $workers, Details specified ${gDetails.workers}", -1)
        List network = (0 ..< workers).collect {e ->
            new Worker ( 
            input: inputAny,
            output: outputAny,
            lDetails: gDetails == null ? null : gDetails.groupDetails[e],
            function: function,
            dataModifier : modifier == null ? null : (List)modifier[e],
            outData: outData,
            logPhaseName: logPhaseName == "" ? "" : (String)"$e, "  + logPhaseName ,
            logPropertyName: logPropertyName)
        }
        new PAR (network).run()
    }
}
