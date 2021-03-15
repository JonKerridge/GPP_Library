package groovy_parallel_patterns.functionals.pipelines

import groovy_parallel_patterns.PipelineDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.functionals.workers.Worker
import groovy_parallel_patterns.terminals.Collect
import groovy.transform.CompileStatic
import groovy_jcsp.*
import jcsp.lang.*

/**
 * 
 * A OnePipelineCollect is a collection of {@link groovy_parallel_patterns.functionals.workers.Worker Worker}
 * processes running in sequence,  each processing instances of the same Class but applying a
 * different operation in each of the Workers. The final stage of the pipeline is a Collect process.
 * <p>
 * @param input The channel from which data objects are read.  The channel 
 * 					is a one2one typically connected to an Emit process.
 * @param stages The number of Worker processes in the pipeline.  Normally there will
 * 				 be as many stages as there are operations performed on each data object.  
 * 				The Collect process at the end of the pipeline is not included in this value.
 * 				There must be at least 2 stages in the pipeline followed by a Collect process.
 * @param stageOp A List containing the method names identifying the function to be undertaken
 * 					by each Worker process.  The names must be specified in the order in which they are to be carried out.
 * @param stageModifier A List containing a modifier, if any, for each stage of the pipeline.
 * 						There must be as many elements in the List as there are stages. Any stage 
 * 						not requiring a modifier must specify null.  If omitted completely a null will
 * 						passed to each Worker process. Each stage modifier is itself a List of values
 * @param pDetails A {@link groovy_parallel_patterns.PipelineDetails} object defining any local class of each stage, defaults to null
 * @param rDetails A {@link groovy_parallel_patterns.ResultDetails} object defining the result class used by the Collect process
 * @param outData A list of boolean values one entry per stage, such that if true the worker process in that stage 
 * 				 will output each processed input object. If false the process will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output 
 *                only happens after the finalise method has been called. outData defaults to true.
 * @param logPhaseName an optional list of string values, which if specified indicates that the processes in the Pipeline should be logged
 * otherwise the process will not be logged.  Specific stages in the Pipeline can be logged by specifying a string value, otherwise the value must
 * be an empty string.  The list must contain an entry for the Collect process.
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.  
 * LogPropertyName must be specified if logPhaseName is specified 
 * @param visLogChan n output channel used to send logging information, created by GPP_Builder
 *           
 * @see groovy_parallel_patterns.terminals.Collect
 * @see groovy_parallel_patterns.functionals.workers.Worker
 */

@CompileStatic
class OnePipelineCollect implements CSProcess{

    ChannelInput input
    int stages = 2
    List <String> stageOp = null
    List <List> stageModifier = null
    PipelineDetails pDetails = null	// one entry per stage excluding Collect
    ResultDetails rDetails = null
    List <Boolean> outData = null

    List <String> logPhaseNames = null // includes the Collect process as well
    String logPropertyName = ""
    ChannelOutput visLogChan = null

    void run() {
        assert (stages >= 2): "OnePipelineCollect: insufficient worker stages $stages should be >=2"
        assert (stageOp != null): "OnePipelineCollect: stageOp MUST be specified, one for each stage of the pipeline"
        assert (stageModifier.size() == stages): "OnePipelineCollect: size of stageModifier (${stageModifier.size()}) must match stages = $stages"
        if (pDetails != null)
            assert (stages == pDetails.stages): "OnePipelineCollect: Number of stages mismatch, Process exepcted $stages, Details specified ${pDetails.stages}"
        if (logPhaseNames == null)
            logPhaseNames = (0 .. stages).collect{i -> return ""}
        else {
            // includes the Collect stage
            assert (logPhaseNames.size() == stages + 1): "OnePipelineCollect: LogPhaseNames contains ${logPhaseNames.size()} entries; should contain ${stages = 1}"
            assert (logPropertyName != ""): "OnePipelineCollect: logPhasesNames specified but no logPropertyName defined"
        }

        if (outData == null) outData = (0..<stages).collect{i -> return true}
        def interConnect = Channel.one2oneArray(stages)
        def firstStage = new Worker(input: input,
                                    output: interConnect[0].out(),
                                    lDetails: pDetails == null ? null : pDetails.stageDetails[0],
                                    function: stageOp[0],
                                    dataModifier: stageModifier == null ? null : stageModifier[0],
                                    outData: outData[0],
                                    logPhaseName: logPhaseNames[0],
                                    logPropertyName: logPropertyName)

        List stageProcesses = []
        for (s in 1..< stages){
            stageProcesses << new Worker( input: interConnect[s-1].in(),
                                          output: interConnect[s].out(),
                                          lDetails: pDetails == null ? null : pDetails.stageDetails[s],
                                          function: stageOp[s],
                                          dataModifier: stageModifier == null ? null : stageModifier[s],
                                          outData: outData[s],
                                          logPhaseName: logPhaseNames[s],
                                          logPropertyName: logPropertyName)
        }
        def collectStage = new Collect( input: interConnect[stages - 1].in(),
                                        rDetails: rDetails,
                                        logPhaseName: logPhaseNames[stages],
                                        logPropertyName: logPropertyName,
                                        visLogChan: visLogChan)
        stageProcesses << firstStage
        stageProcesses << collectStage
        new PAR(stageProcesses).run()
    }

}
