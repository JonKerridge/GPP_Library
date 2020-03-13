package groovyParallelPatterns.functionals.pipelines

import groovyParallelPatterns.PipelineDetails
import groovyParallelPatterns.functionals.workers.Worker
import groovyJCSP.PAR
import jcsp.lang.*

/**
 * 
 * A Pipeline is a collection of Worker processes running in sequence each
 * processing instances of the same Class but applying a 
 * different operation in each of the Workers.
 * <p>
 * @param input The channel from which data objects are read.  The channel 
 * 					is a one2one typically connected to an Emit process.
 * @param output The channel upon which processed data objects are written.  The
 * 					channel must be a one2one channel and will typically be connected to 
 * 					a Collect process.
 * @param stages The number of {@link groovyParallelPatterns.functionals.workers.Worker Worker} processes in the pipeline.  Normally there will
 * 				 be as many stages as there are operations performed on each data object 
 * @param stageOp A List containing the name identifying the operation to be undertaken
 * 					by each Worker process.  The operation names must be specified in the
 * 					order in which they are to be carried out.
 * @param stageModifier A List containing a modifier value, if any, for each stage of the pipeline.
 * 						There must be as many elements in the List as there are stages. Any stage 
 * 						not requiring a modifier must specify null.  If omitted completely a null will
 * 						passed to each Worker process. There must be as many elements in the List as 
 * 						there are stages. Any stage not requiring a modifier must specify null.  
 * 						If omitted completely a null will passed to each Worker process
 * @param pDetails A {@link groovyParallelPatterns.PipelineDetails} object defining any local class of each stage, default to null
 * @param outData A list of boolean values one entry per stage, such that if true the worker process in that stage 
 * 				 will output each processed input object. If false the process will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output 
 *                only happens after the finalise method has been called. outData defaults to true.
 * @param logPhaseName an optional list of string values, which if specified indicates that the processes in the Pipeline should be logged
 * otherwise the process will not be logged.  Specific stages in the Pipeline can be logged by specifying a string value, otherwise the value must
 * be an empty string
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.  
 * LogPropertyName must be specified if logPhaseName is specified 
 *           
 * @see groovyParallelPatterns.functionals.workers.Worker
 * @see jcsp.lang.Barrier					
 */
class OnePipelineOne implements CSProcess{

	ChannelInput input
	ChannelOutput output
	int stages = 2
	List <String> stageOp = null
	List stageModifier = null
	PipelineDetails pDetails = null	// array with one entry per stage
    List <Boolean> outData = null

    List <String> logPhaseNames = null
	String logPropertyName = ""

	void run() {
        assert (stages >= 2): "OnePipelineOne: insufficient worker stages "
        assert (stageOp != null): "OnePipelineone: stageOp MUST be specified, one for each stage of the pipeline"
       if (pDetails != null)
            assert (stages == pDetails.stages): "OnePipelineOne: Number of stages mismatch, Process exepcted $stages, Details specified ${pDetails.stages}"
		if (logPhaseNames == null) logPhaseNames = (0..<stages).collect{i -> return ""}
		if (outData == null) outData = (0..<stages).collect{i -> return true}
		def lastIndex = stages - 1
		def interConnect = Channel.one2oneArray(stages-1)
		def firstStage = new Worker( input: input,
												output: interConnect[0].out(),
												lDetails: pDetails == null ? null : pDetails.stageDetails[0],
												function: stageOp[0],
												dataModifier: stageModifier == null ? null : stageModifier[0],
												outData: outData[0],
												logPhaseName: logPhaseNames[0],
												logPropertyName: logPropertyName)
		def lastStage = new Worker( input: interConnect[lastIndex-1].in(),
											   output: output,
												lDetails: pDetails == null ? null : pDetails.stageDetails[lastIndex],
											   function: stageOp[lastIndex],
											   dataModifier: stageModifier == null ? null : stageModifier[lastIndex],
											   outData: outData[lastIndex],
												logPhaseName: logPhaseNames[lastIndex],
												logPropertyName: logPropertyName)
		List stageProcesses = []
		for (s in 0..<(stages-2)){
			stageProcesses << new Worker( input: interConnect[s].in(),
													 output: interConnect[s+1].out(),
													 lDetails: pDetails == null ? null : pDetails.stageDetails[s+1],
													 function: stageOp[s+1],
													 dataModifier: stageModifier == null ? null : stageModifier[s+1],
													 outData: outData[s+1],
													 logPhaseName: logPhaseNames[s+1],
													 logPropertyName: logPropertyName)
		}
		stageProcesses << firstStage
		stageProcesses << lastStage
		new PAR(stageProcesses).run()

	}

}
