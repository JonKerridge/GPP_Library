package groovyParallelPatterns.functionals.composites

import groovyParallelPatterns.CompositeDetails
import groovyParallelPatterns.functionals.pipelines.OnePipelineOne
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 An ListGroupOfPipelines comprises a network of {@code groups} parallel pipeline occurrences and
 each pipeline comprises {@code stages} Worker processes.<p>
 Inputs from the preceding process are assumed to come from the any end of a channel.
 Similarly, outputs go to an Any-ended channel.<p>
 Internally, a collection of
  {@code groups} {@link groovyParallelPatterns.functionals.pipelines.OnePipelineOne OnePipelineOne}
 pipelines are created.

  @param inputList the ChannelInputList used to read objects into the network created by GroupOfPipeline
  @param outputList the ChannelOutputList used to output processed data or worker objects
 @param stages the number of stages in each pipeline
 @param groups the number of parallel pipelines in the network
 @param stageOp a list of function identifiers to be associated with each stage of the pipeline
 @param stageModifier a list of {@code groups} lists, each containing {@code stages} elements that
  are the stage modifier data for that combination of group and stage. Each entry is itself  a
  list of values.
 @param outData a list of {@code groups} lists each entry of which comprises {@code stages}
  entries.  Each entry is a boolean value such that if true the worker processes in that stage
  will output each processed input object. If false the process will output the workerClass once
  only, after it has processed all the input data objects. If omitted the value defaults to true.
 @param cDetails A {@link groovyParallelPatterns.CompositeDetails} object defining the
  object that defines each of the stages and groups where a local worker object is required.
 @param logPhaseName an optional list of string values, which if specified indicates that the
  processes in the Pipeline should be logged otherwise the process will not be logged.
  Specific stages in the Pipeline can be logged by giving a string value, otherwise the value must
  be an empty string.  Thus some of the pipeline stages can be logged while others are not.
  Thus ["first", "", "third"] will result in the pipeline processes 0 and 2 being logged and
  labelled {@code first} and {@code third}.  Process with index 1 will not be logged.
 @param logPropertyName the name of a property in the input object that will uniquely identify
  an instance of the object. It must be specified if {@code logPhaseName} is specified.
  It is assumed that the same property is used throughout the process network

 @see groovyParallelPatterns.functionals.pipelines.OnePipelineOne
 */

@CompileStatic
class ListGroupOfPipelines implements CSProcess {

  ChannelInputList inputList
  ChannelOutputList outputList
  int stages = 2
  int groups = 2
  List<String> stageOp = null
  List <List> stageModifier = null
  List<List<Boolean>> outData = null
  CompositeDetails cDetails = null

  List<String> logPhaseNames = null
  String logPropertyName = ""

  void run() {
    int inListSize = inputList.size()
    int outListSize = outputList.size()
    assert stages >= 2: "ListGroupOfPipelines: insufficient worker stages, value supplied $stages "
    assert (stageOp != null): "ListGroupOfPipelines: stageOp MUST be specified, one for each stage of the pipeline"
    assert stageOp.size() == stages: "ListGroupOfPipelines : size of stageOp, ${stageOp.size()}, not equal to number of stages, $stages"
    assert groups == inListSize: "ListGroupOfPipelines: inputList size , $inListSize, not equal to number of groups, $groups"
    assert groups == outListSize: "ListGroupOfPipelines: outputList size , $outListSize, not equal to number of groups, $groups"
    if (cDetails != null) {
      int cgSize = cDetails.cDetails.size() // number of groups
      int csSize = cDetails.cDetails[0].size() // number of stages
      assert cgSize == groups: "ListGroupOfPipelines:  number of groups in cDetails, $cgSize, not equal number of workers, $groups"
      assert csSize == stages: "ListGroupOfPipelines:  number of stages in cDetails, $csSize, not equal number of groups, $stages"
    }
    List<List<String>> logNames = []
    if (logPhaseNames != null) {
      for (g in 0..<groups) {
        List<String> phaseNames = (0..<stages).collect { s -> return (String) "$g, " + logPhaseNames[s] }
        logNames[g] = phaseNames
      }
    } else {
      List<String> phaseNames = (0..<stages).collect { s -> return "" }
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
      new OnePipelineOne(input: (ChannelInput) inputList[g],
          output: (ChannelOutput) outputList[g],
          stages: stages,
          pDetails: cDetails == null ? null : cDetails.extractByPipe(g),
          stageOp: stageOp,
          stageModifier: stageModifier == null ? null : stageModifier[g],
          outData: outData[g],
          logPhaseNames: logPhaseNames == null ? logNames[0] : logNames[g],
          logPropertyName: logPropertyName)
    }
    new PAR(network).run()
  }

}
