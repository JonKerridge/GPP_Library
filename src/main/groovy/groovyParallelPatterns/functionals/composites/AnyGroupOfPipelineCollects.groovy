package groovyParallelPatterns.functionals.composites

import groovyParallelPatterns.CompositeDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.functionals.pipelines.OnePipelineCollect
import groovy.transform.CompileStatic
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * An AnyGroupOfPipelineCollects comprises a network of {@code groups} parallel occurrences and
 * each pipeline comprises {@code stages} Worker processes, excluding the final
 * {@link groovyParallelPatterns.terminals.Collect Collect} stage.
 * Inputs from the preceding process are assumed to come from the any end of a channel.
 * Internally, a collection of {@code groups}
 * {@link groovyParallelPatterns.functionals.pipelines.OnePipelineCollect OnePipeLineCollect}
 * pipelines are created.
 *
 * @param inputAny the any end of channel used to read objects into the network created by AnyGroupOfPipelineCollects
 * @param stages the number of stages in each pipeline excluding the Collect process
 * @param groups the number of parallel pipelines in the network
 * @param stageOp a list of function identifiers to be associated with each stage of the pipeline
 * @param stageModifier a list of {@code groups} lists, each containing {@code stages} elements that are the
 * 			stage modifier data for that combination of group and stage. Each entry is itself be a list of values.
 * @param outData a list of {@code groups} lists each entry of which comprises {@code stages} entries.  Each entry is a boolean value
 * 			such that if true the worker processes in that stage will output each processed input object. If false the process will output
 * 			the workerClass once only, after it has processed all the input data objects. If omitted the value defaults to true.
 * @param cDetails A {@link groovyParallelPatterns.CompositeDetails CompositeDetails} object defining the object that defines each of the stages and groups
 *       where a local worker object is required
 * @param rDetails A list of {@link groovyParallelPatterns.ResultDetails ResultDetails} object defining the result class used by each Collect process in the group
 * @param logPhaseName an optional list of string values, which if specified indicates that the processes in the Pipeline should be logged
 * otherwise the process will not be logged.  Particular stages in the Pipeline can be logged by specifying a string value,
 * otherwise the value must be an empty string indicating that stage is not to be logged.
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * It must be specified if {@code logPhaseName} is specified.  It is assumed that the same property is used throughout the process network
 * @param visLogChan the output end of an any2one channel to which log data will be sent to an instance of the LoggingVisualiser
 * process running in parallel with the application network.  If {@code logPhaseName} is not specified then it is assumed that no visualiser process is running.
 *
 */
@CompileStatic
class AnyGroupOfPipelineCollects implements CSProcess {
  ChannelInput inputAny
  int stages = -1
  int groups = 2
  List<List<Boolean>> outData = null
  List<String> stageOp = null
  List <List> stageModifier = null
  List<ResultDetails> rDetails
  CompositeDetails cDetails = null

  List<String> logPhaseNames = null  // includes Collect stage
  String logPropertyName = ""
  ChannelOutput visLogChan = null

  void run() {
    int rSize = rDetails.size()
    int opSize = stageOp.size()
    assert (stageOp != null): "AnyGroupOfPipelineCollects: stageOp MUST be specified, one for each stage of the pipeline"
    assert stages > 0: "AnyGroupOfPipelineCollects: value of stages not specified, $stages"
    assert rSize == groups: "AnyGroupOfPipelineCollects: size of rDetails, $rSize, not equal to number of groups, $groups"
    assert opSize == stages: "AnyGroupOfPipelineCollects: size of stageOp, $opSize, not equal to number of stages, $stages"
    if (cDetails != null) {
      int cgSize = cDetails.cDetails.size() // number of groups
      int csSize = cDetails.cDetails[0].size() // number of stages
      assert cgSize == groups: "AnyGroupOfPipelineCollects:  number of groups in cDetails, cgSize, not equal number of groups, $groups"
      assert csSize == stages: "AnyGroupOfPipelineCollects:  number of stages in cDetails, csSize, not equal number of groups, $stages"
    }
    List<List<String>> logNames = []
    if (logPhaseNames != null) {
      assert logPhaseNames.size() == stages + 1: "AnyGroupOfPipelineCollects: " + "logPhaseNames wrong size should be ${stages + 1}, currently $logPhaseNames"
      for (g in 0..<groups) {
        List<String> phaseNames = (0..stages).collect { s -> return (String) "$g, " + logPhaseNames[s] }
        logNames[g] = phaseNames
      }
    } else {
      List<String> phaseNames = (0..stages).collect { s -> return "" }
      logNames[0] = phaseNames
    }
    if (outData == null) {
      outData = []
      for (g in 0..<groups) {
        List<Boolean> gList = (0..<stages).collect { i -> return true }
        outData << gList
      }
    }
    def network = (0..<groups).collect { g ->
      new OnePipelineCollect(input: inputAny,
          stages: stages,
          pDetails: cDetails == null ? null : cDetails.extractByPipe(g),
          rDetails: rDetails[g],
          stageOp: stageOp,
          stageModifier: stageModifier == null ? null : stageModifier[g],
          outData: outData[g],
          logPhaseNames: logPhaseNames == null ? logNames[0] : logNames[g],
          logPropertyName: logPropertyName,
          visLogChan: visLogChan)
    }
    new PAR(network).run()
  }

}
