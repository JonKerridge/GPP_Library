package GPP_Library.functionals.composites

import GPP_Library.CompositeDetails
import GPP_Library.functionals.groups.*
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 *
 * A PipelineOfGroups comprises a collection of Groups, linked together to form a pipeline.
 * The pipeline starts with an AnyGroupList followed by zero or more ListGroupList processes followed by
 * a ListGroupAny process.
 * <p>
 * @param inputAny The channel upon which data objects are read.  This will be from a FanOutAny process.
 * @param stages The number of stages in the pipeline including the initial GroupFromAny.
 * @param dataClassName The name of the Class to be processed by each of the
 * 					contained Worker processes
 * @param resultsName The Class name of the data object used to hold the results
 * @param workerClassName a List of worker class names one for each stage of the pipeline excluding the GroupCollect stage
 * @param workerInitData a list of lists of initial data for each worker, each list contains workers elements such that
 * 						[ [S0W0, S0W1, ... ,S0Wl], ..., [SgW0, SgW1, ... ,SgWl]]
 * 						where l is the index of the last Worker process, that is workers - 1 and
 * 						and g is the index of the last group, that is stages -1
 * @param resultsInitData Values used to initialise the results class
 * @param resultsFinaliseData Values used by the finalise method in the results class
 * @param resultInit The name of the method that implements the initialise method of the result class
 * @param resultCollector The name of the method that implements the collector method of the result class
 * @param resultFinalise The name of the method that implements the finalise method of the result class
 * @param stageOp a List of operation code values identifying the operation to be undertaken
 * 					by the Worker processes in each stage of the pipeline excluding the Collect stage
 * @param modifier Contains a possible modifier for the operation, with each Stage
 * 					accessing the element that corresponds to the index of the Stage excluding the Collect stage.
 * @param workers The number of Worker processes that will be created
 * 					when each Group is run
 * @param outData A list of boolean values one entry per stage, excluding the GroupCollect stage,
 * 					such that if true the worker processes in that stage
 * 				 will output each processed input object. If false the process will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output
 *                only happens after the finalise method has been called. outData defaults to true.
 * @param logPhaseName an optional list of string values, which if specified indicates that the processes in the Pipeline should be logged
 * otherwise the process will not be logged.  Specific stages in the Pipeline can be logged by specifying a string value, otherwise the value must
 * be an empty string
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 * @see GPP_Library.functionals.groups.AnyGroupList
 * @see GPP_Library.functionals.groups.ListGroupCollect
 * @see GPP_Library.functionals.groups.ListGroupList
 * @see GPP_Library.functionals.workers.Worker
 * <p>
 *
*/
@CompileStatic
class PipelineOfGroups implements CSProcess {

	ChannelInput inputAny
	ChannelOutput outputAny
	int stages = -1
	int workers = 2
	List <String> stageOp = null
	List stageModifier = null
	CompositeDetails cDetails = null
    List <Boolean> outData = null

    List <String> logPhaseNames = null
	String logPropertyName = ""
    
	void run() {
        assert (stageOp != null): "Pipeline of Group : stageOp MUST be specified, one for each stage of the pipeline"
        assert stages > 0 : "Pipeline of Group : value of stages not specified, $stages"
        assert stageOp.size() == stages : "Pipeline of Groups : size of stageOp, ${stageOp.size()}, not equal to number of stages, $stages"
        if ( cDetails != null){
            int cgSize = cDetails.cDetails.size() // number of groups
            int csSize = cDetails.cDetails[0].size() // number of stages
            assert cgSize == workers : "Pipeline of Group :  number of groups in cDetails, $cgSize, not equal number of workers, $workers"
            assert csSize == stages : "Pipeline of Group :  number of stages in cDetails, $csSize, not equal number of groups, $stages"
        }
		if (logPhaseNames == null) logPhaseNames = (0..<stages).collect{i -> return ""}
		if (outData == null) outData = (0..<stages).collect{i -> return true}
        int lastIndex = stages - 1
        List  chanArray = []
        List interConnect = []
        List chanOutLists = []
        List chanInLists = []
        for (s in 0 ..< lastIndex) {
            chanArray  <<  Channel.one2oneArray(workers)
            chanOutLists << new ChannelOutputList((One2OneChannel[])chanArray[s])
            chanInLists << new ChannelInputList((One2OneChannel[])chanArray[s])
        }
		def firstStage = new AnyGroupList( inputAny: inputAny,
											  outputList: (ChannelOutputList) chanOutLists[0],
											  gDetails: cDetails  == null ? null : cDetails.extractByStage(0),
											  function: (String)stageOp[0],
											  modifier: stageModifier == null ? null : (List)stageModifier[0],
											  workers: workers,
											  outData: outData[0],
											  logPhaseName: logPhaseNames[0],
											  logPropertyName: logPropertyName)

        def stageProcesses = []
        for (s in 1 ..< lastIndex){
            stageProcesses << new ListGroupList( inputList: (ChannelInputList)chanInLists[s-1],
                                                 outputList: (ChannelOutputList) chanOutLists[s],
                                                 gDetails: cDetails  == null ? null : cDetails.extractByStage(s),
                                                 function: (String)stageOp[s],
                                                 modifier: stageModifier == null ? null : (List)stageModifier[s],
                                                 outData: outData[s],
                                                 workers: workers,
                                                 logPhaseName: logPhaseNames[s],
                                                 logPropertyName: logPropertyName)
        }

		def lastStage = new ListGroupAny(inputList: (ChannelInputList)chanInLists[lastIndex-1],
											  gDetails: cDetails  == null ? null : cDetails.extractByStage(lastIndex),
											  function: (String)stageOp[lastIndex],
											  modifier: stageModifier == null ? null : (List)stageModifier[lastIndex],
											  outputAny: outputAny,
											  workers: workers,
											  outData: outData[lastIndex],
											  logPhaseName: logPhaseNames[lastIndex],
											  logPropertyName: logPropertyName )
		stageProcesses << firstStage
		stageProcesses << lastStage
		new PAR(stageProcesses).run()
	}
}
