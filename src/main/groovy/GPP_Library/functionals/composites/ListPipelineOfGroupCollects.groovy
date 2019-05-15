package GPP_Library.functionals.composites

import GPP_Library.CompositeDetails
import GPP_Library.ResultDetails
import GPP_Library.functionals.groups.ListGroupCollect
import GPP_Library.functionals.groups.ListGroupList
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.Channel
import jcsp.lang.ChannelOutput
import jcsp.lang.One2OneChannel

/**
 *
 * A ListPipelineOfGroupCollects comprises a collection of Groups, linked together to form a pipeline.
 * The pipeline starts with an AnyGroupList followed by zero of more ListGroupList processes followed by a
 * GroupCollect.
 * <p>
 * @param inputList The channel input list from which data objects are read.
 * @param stages The number of stages in the pipeline including the initial
 * 				  GroupFromAny but excluding the GroupCollect stage.  There must be at least 1, the AnyGroupList, stage.
 * @param stageOp a List of operation code values identifying the operation to be undertaken
 * 					by the Worker processes in each stage of the pipeline excluding the Collect stage
 * @param stageModifier Contains a possible modifier for the operation, with each Stage
 * 					accessing the element that corresponds to the index of the Stage excluding the Collect stage.
 * @param workers The number of Worker processes that will be created
 * 					when each Group is run
 * @param cDetails A {@link GPP_Library.CompositeDetails} object defining the object that defines each of the stages and groups
 * @param rDetails A list of {@link GPP_Library.ResultDetails} object defining the result class used by each Collect process in the group
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
 * @param visLogChan the output end of an any2one channel to which log data will be sent to an instance of the LoggingVisualiser
 * process running in parallel with the application network.  If not specified then it is assumed that no visualiser process is running.
 *
 *
 * @see GPP_Library.functionals.groups.ListGroupCollect* @see GPP_Library.functionals.groups.ListGroupList* @see GPP_Library.functionals.workers.Worker* <p>
 *
 */
@CompileStatic
class ListPipelineOfGroupCollects implements CSProcess {

  ChannelInputList inputList
  int stages = -1
  int workers = 2
  List<String> stageOp = null
  List stageModifier = null
  CompositeDetails cDetails = null
  List<ResultDetails> rDetails
  List<Boolean> outData = null

  List<String> logPhaseNames = null  // includes the Collect phase as well
  String logPropertyName = ""
  ChannelOutput visLogChan = null

  void run() {
    int inSize = inputList.size()
    int rSize = rDetails.size()
    int opSize = stageOp.size()
    assert (inSize == workers): "ListPipelineOfGroupCollects : size of inputList $inSize must match number of workers $workers"
    assert (rSize == workers): "ListPipelineOfGroupCollects : size of rDetails $rSize must match number of workers $workers"
    assert (stageOp != null): "ListPipelineOfGroupCollects: stageOp MUST be specified, one for each stage of the pipeline"
    assert stages > 0: "ListPipelineOfGroupCollects: value of stages not specified, $stages, must be >= 2"
    assert stageOp.size() == stages: "ListPipelineOfGroupCollects : size of stageOp, $opSize, not equal to number of stages, $stages"
    if (cDetails != null) {
      int cgSize = cDetails.cDetails.size() // number of groups
      int csSize = cDetails.cDetails[0].size() // number of stages
      assert cgSize == workers: "ListPipelineOfGroupCollects:  number of groups in cDetails, $cgSize, not equal number of workers, $workers"
      assert csSize == stages: "ListPipelineOfGroupCollects:  number of stages in cDetails, $csSize, not equal number of groups, $stages"
    }
    if (logPhaseNames == null) logPhaseNames = (0..stages).collect { i -> return "" }
    if (outData == null) outData = (0..<stages).collect { i -> return true }
    int lastIndex = stages - 1
    List chanArray = []
    List chanOutLists = []
    List chanInLists = []
    for (s in 0..lastIndex) {
      chanArray << Channel.one2oneArray(workers)
      chanOutLists << new ChannelOutputList((One2OneChannel[]) chanArray[s])
      chanInLists << new ChannelInputList((One2OneChannel[]) chanArray[s])
    }
    def firstStage = new ListGroupList(inputList: inputList,
        outputList: (ChannelOutputList) chanOutLists[0],
        gDetails: cDetails == null ? null : cDetails.extractByStage(0),
        function: (String) stageOp[0],
        modifier: stageModifier == null ? null : (List) stageModifier[0],
        workers: workers,
        outData: outData[0],
        logPhaseName: logPhaseNames[0],
        logPropertyName: logPropertyName)

    def lastStage = new ListGroupCollect(inputList: (ChannelInputList) chanInLists[lastIndex],
        rDetails: rDetails,
        workers: workers,
        logPhaseName: logPhaseNames[stages],
        logPropertyName: logPropertyName,
        visLogChan: visLogChan)

    def stageProcesses = []
    for (s in 1..<stages) {
      stageProcesses << new ListGroupList(inputList: (ChannelInputList) chanInLists[s - 1],
          outputList: (ChannelOutputList) chanOutLists[s],
          gDetails: cDetails == null ? null : cDetails.extractByStage(s),
          function: (String) stageOp[s],
          modifier: stageModifier == null ? null : (List) stageModifier[s],
          outData: outData[s],
          workers: workers,
          logPhaseName: logPhaseNames[s],
          logPropertyName: logPropertyName)
    }
    stageProcesses << firstStage
    stageProcesses << lastStage
    new PAR(stageProcesses).run()
  }
}
