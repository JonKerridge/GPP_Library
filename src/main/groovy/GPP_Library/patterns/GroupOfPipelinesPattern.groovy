package GPP_Library.patterns

import GPP_Library.CompositeDetails
import GPP_Library.DataDetails
import GPP_Library.ResultDetails
import GPP_Library.connectors.reducers.AnyFanOne
import GPP_Library.connectors.spreaders.OneFanAny
import GPP_Library.functionals.composites.AnyGroupOfPipelineCollects
import GPP_Library.functionals.composites.AnyGroupOfPipelines
import GPP_Library.terminals.Collect
import GPP_Library.terminals.Emit
import groovy.transform.CompileStatic
import groovyJCSP.PAR
import jcsp.lang.Channel

/**
 * A GroupOfPipelinesPattern comprises a group of pipelines.  The sequence comprises
 * Emit; OneFanAny; AnyGroupOfPipelines; AnyFanOne, Collect. The properties of the pattern provide all the
 * external values required to run the pattern.  The internal channels required to
 * connect the processes together are all declared within the class outwith programmer concerns.
 * <p>
 * @param eDetails A {@link GPP_Library.DataDetails}  object containing information concerning the DataClass
 * used by the Emit process
 * @param lDetails A {@link GPP_Library.LocalDetails}  list object containing information concerning each stage of the Pipeline
 * @param stageOp A List of String values identifying the operation to be undertaken
 * 					by each stage process
 * @param stageModifier A List containing the possible modifiers for the operation, within each stage by each worker in a group
 * 					accessing the element that corresponds to the index of the stage.
 * @param groups An int specifying the number of parallel pipelines
 * @param cDetails A List of {@link GPP_Library.CompositeDetails} objects containing data pertaining to each group of processes.
 * @param rDetails A {@link GPP_Library.ResultDetails} containing data pertaining to result class used by the Collect process, it MUST be specified.
 * @param stages The number of stages in the pipeline of processes that will be created
 * 					when the Pipeline is run
 * @param outData a list of groups lists each entry of which comprises stages entries.  Each entry is a boolean value
 * 			such that if true the worker processes in that stage
 * 			will output each processed input object. If false the process will output
 * 			the workerClass once only, after it has processed all the input data objects. If omitted the value defaults to true.
 * @see GPP_Library.functionals.workers.Worker
 * <p>
 *
 */
@CompileStatic
class GroupOfPipelinesPattern {

  DataDetails eDetails
  int stages
  List <String> stageOp = null
  List stageModifier = null
  int groups
  CompositeDetails cDetails = null
  ResultDetails rDetails
  List <List<Boolean>> outData = null

  def run() {
    def toFanOut = Channel.one2one()
    def toGoP = Channel.one2any()
    def gopOut = Channel.any2one()
    def collectChan = Channel.one2one()

    def emitter = new Emit( output: toFanOut.out(),
        eDetails: eDetails )

    def fanOut = new OneFanAny(input: toFanOut.in(),
        outputAny: toGoP.out(),
        destinations: groups)

    def gop = new AnyGroupOfPipelines(
        inputAny: toGoP.in(),
        outputAny: gopOut.out(),
        stages: stages,
        cDetails: cDetails,
        stageOp: stageOp,
        stageModifier: stageModifier,
        groups: groups,
        outData: outData)

    def fanIn = new AnyFanOne(inputAny: gopOut.in(),
        output: collectChan.out(),
        sources: groups)

    def collector = new Collect( input: collectChan.in(),
        rDetails: rDetails )


    new PAR([emitter, fanOut, gop, fanIn, collector]).run()
  }
}
