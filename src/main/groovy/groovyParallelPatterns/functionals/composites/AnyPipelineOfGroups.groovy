package groovyParallelPatterns.functionals.composites

import groovyParallelPatterns.CompositeDetails
import groovyParallelPatterns.functionals.groups.AnyGroupAny
import groovy.transform.CompileStatic
import groovyJCSP.PAR
import jcsp.lang.*

/**
 *
 * An AnyPipelineOfGroups comprises a collection of Groups, linked together to form a pipeline.
 * The pipeline comprises two or more
 * {@link groovyParallelPatterns.functionals.groups.AnyGroupAny AnyGroupAny} groups.
 * <p>
 * @param inputAny The channel upon which data objects are read.
 * @param outputAny the channel to which processed objects are written
 * @param stages The number of stages in the pipeline .
 * @param stageOp a List of operation code names identifying the operation to be undertaken
 * 					by the Worker processes in each stage of the pipeline.
 * @param cDetails A {@link groovyParallelPatterns.CompositeDetails} object that defines each of
 * the stages and groups, when a {@link groovyParallelPatterns.LocalDetails} specifying a local
 * worker object is required.
 * @param stageModifier Contains a possible modifier for the operation, with each Stage
 * 					accessing the element that corresponds to the index of the Stage.
 * @param workers The number of Worker processes that will be created when each Group is run.
 * @param outData a list of {@code groups} lists each entry of which comprises a List of
 * {@code stages} entries.
 * Each entry is a boolean value such that if true the worker processes in that stage will output
 * each processed input object. If false the process will output the workerClass once only,
 * after it has processed all the input data objects. If omitted the value defaults to true.
 * @param logPhaseName an optional list of string values, which if specified indicates that the
 * processes in the Pipeline should be logged otherwise the process will not be logged.
 * Particular stages in the Pipeline can be logged by specifying a string value,
 * otherwise the value must be an empty string indicating that stage is not to be logged.
 * Specific stages in the Pipeline can be logged by giving a string value, otherwise the value must
 * be an empty string.  Thus some of the pipeline stages can be logged while others are not.
 * Thus ["first", "", "third"] will result in the pipeline processes 0 and 2 being logged and
 * labelled {@code first} and {@code third}.  Process with index 1 will not be logged.
 * @param logPropertyName the name of a property in the input object that will uniquely identify
 * an instance of the object.
 * It must be specified if {@code logPhaseName} is specified.  It is assumed that the same
 * property is used throughout the process network.
 *
 * @see groovyParallelPatterns.functionals.groups.AnyGroupAny
 *
 */
@CompileStatic
class AnyPipelineOfGroups implements CSProcess {

  ChannelInput inputAny
  ChannelOutput outputAny
  int stages = 2
  int workers = 2
  List<String> stageOp = null
  List <List> stageModifier = null
  List<Boolean> outData = null
  CompositeDetails cDetails = null

  List<String> logPhaseNames = null
  String logPropertyName = ""

  void run() {
    assert (stageOp != null): "AnyPipelineOfGroups : stageOp MUST be specified, one for each stage of the pipeline"
    assert stages > 0: "AnyPipelineOfGroups : value of stages not specified, $stages"
    assert stageOp.size() == stages: "AnyPipelineOfGroups : size of stageOp, ${stageOp.size()}, not equal to number of stages, $stages"
    if (cDetails != null) {
      int cgSize = cDetails.cDetails.size() // number of groups
      int csSize = cDetails.cDetails[0].size() // number of stages
      assert cgSize == workers: "AnyPipelineOfGroups :  number of groups in cDetails, $cgSize, not equal number of workers, $workers"
      assert csSize == stages: "AnyPipelineOfGroups :  number of stages in cDetails, $csSize, not equal number of groups, $stages"
    }
    if (logPhaseNames == null) logPhaseNames = (0..<stages).collect { i -> return "" }
    if (outData == null) outData = (0..<stages).collect { i -> return true }
    int lastIndex = stages - 1
    List <Any2AnyChannel> interConnect = []
    for (s in 0..<lastIndex) {
      interConnect.add( Channel.any2any())
    }
    def firstStage = new AnyGroupAny(inputAny: inputAny,
        outputAny: ((Any2AnyChannel) interConnect[0]).out(),
        gDetails: cDetails == null ? null : cDetails.extractByStage(0),
        function: (String) stageOp[0],
        modifier: stageModifier == null ? null :  stageModifier[0],
        workers: workers,
        outData: outData[0],
        logPhaseName: logPhaseNames[0],
        logPropertyName: logPropertyName)

    def stageProcesses = []
    for (s in 1..<lastIndex) {
      stageProcesses << new AnyGroupAny(inputAny: ((Any2AnyChannel) interConnect[s-1]).in(),
          outputAny: ((Any2AnyChannel) interConnect[s]).out(),
          gDetails: cDetails == null ? null : cDetails.extractByStage(s),
          function: (String) stageOp[s],
          modifier: stageModifier == null ? null : stageModifier[s],
          outData: outData[s],
          workers: workers,
          logPhaseName: logPhaseNames[s],
          logPropertyName: logPropertyName)
    }

    def lastStage = new AnyGroupAny(inputAny: ((Any2AnyChannel) interConnect[lastIndex-1]).in(),
        gDetails: cDetails == null ? null : cDetails.extractByStage(lastIndex),
        function: (String) stageOp[lastIndex],
        modifier: stageModifier == null ? null : stageModifier[lastIndex],
        outputAny: outputAny,
        workers: workers,
        outData: outData[lastIndex],
        logPhaseName: logPhaseNames[lastIndex],
        logPropertyName: logPropertyName)
    stageProcesses << firstStage
    stageProcesses << lastStage
    new PAR(stageProcesses).run()
  }
}
