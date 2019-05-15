package GPP_Library.functionals.composites

import GPP_Library.CompositeDetails
import GPP_Library.ResultDetails
import GPP_Library.functionals.groups.AnyGroupAny
import GPP_Library.functionals.groups.AnyGroupCollect
import GPP_Library.functionals.groups.AnyGroupList
import GPP_Library.functionals.groups.ListGroupCollect
import GPP_Library.functionals.groups.ListGroupList
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.*

/**
 *
 * A AnyPipelineOfGroupCollects comprises a collection of Groups, linked together to form a pipeline.
 * The pipeline starts with an AnyGroupList followed by zero of more ListGroupList processes followed by a
 * GroupCollect.
 * <p>
 * @param inputAny The channel upon which data objects are read.  This will be from a FanOutAny process.
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
 * @see GPP_Library.functionals.groups.AnyGroupList* @see GPP_Library.functionals.groups.ListGroupCollect* @see GPP_Library.functionals.groups.ListGroupList* @see GPP_Library.functionals.workers.Worker* <p>
 *
 */
@CompileStatic
class AnyPipelineOfGroupCollects implements CSProcess {

  ChannelInput inputAny
  int stages = -1
  int workers = 2
  List<String> stageOp = null
  List stageModifier = null
  CompositeDetails cDetails = null
  List <ResultDetails> rDetails
  List<Boolean> outData = null

  List<String> logPhaseNames = null  // includes the Collect phase as well
  String logPropertyName = ""
  ChannelOutput visLogChan = null

  void run() {
    int rSize = rDetails.size()
    int opSize = stageOp.size()
    assert (stageOp != null): "AnyPipelineOfGroupCollects: stageOp MUST be specified, one for each stage of the pipeline"
    assert stages > 0: "AnyPipelineOfGroupCollects: value of stages not specified, $stages, must be >= 2"
    assert opSize == stages: "AnyPipelineOfGroupCollects : size of stageOp, $opSize, not equal to number of stages, $stages"
    assert rSize == stages: "AnyPipelineOfGroupCollects : size of rDetails, $rSize, not equal to number of stages, $workers"
    if (cDetails != null) {
      int cgSize = cDetails.cDetails.size() // number of groups
      int csSize = cDetails.cDetails[0].size() // number of stages
      assert cgSize == workers: "AnyPipelineOfGroupCollects:  number of groups in cDetails, $cgSize, not equal number of workers, $workers"
      assert csSize == stages: "AnyPipelineOfGroupCollects:  number of stages in cDetails, $csSize, not equal number of groups, $stages"
    }
    if (logPhaseNames == null) logPhaseNames = (0..stages).collect { i -> return "" }
    if (outData == null) outData = (0..<stages).collect { i -> return true }
    int lastIndex = stages - 1
    List <Any2AnyChannel> interConnect = []
    for (s in 0..lastIndex) {
      interConnect.add( Channel.any2any())
    }
    def firstStage = new AnyGroupAny(inputAny: inputAny,
        outputAny: ((Any2AnyChannel) interConnect[0]).out(),
        gDetails: cDetails == null ? null : cDetails.extractByStage(0),
        function: (String) stageOp[0],
        modifier: stageModifier == null ? null : (List) stageModifier[0],
        workers: workers,
        outData: outData[0],
        logPhaseName: logPhaseNames[0],
        logPropertyName: logPropertyName)

    def lastStage = new AnyGroupCollect(inputAny: ((Any2AnyChannel) interConnect[lastIndex]).in(),
        rDetails: rDetails,
        collectors: workers,
        logPhaseName: logPhaseNames[stages],
        logPropertyName: logPropertyName,
        visLogChan: visLogChan)

    def stageProcesses = []
    for (s in 1..<stages) {
      stageProcesses << new AnyGroupAny(inputAny: ((Any2AnyChannel) interConnect[s-1]).in(),
          outputAny: ((Any2AnyChannel) interConnect[s]).out(),
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
