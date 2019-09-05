package GPP_Library.patterns

import GPP_Library.CompositeDetails
import GPP_Library.DataDetails
import GPP_Library.ResultDetails
import GPP_Library.connectors.reducers.AnyFanOne
import GPP_Library.connectors.spreaders.OneFanAny
import GPP_Library.functionals.composites.AnyPipelineOfGroupCollects
import GPP_Library.functionals.composites.AnyPipelineOfGroups
import GPP_Library.terminals.Collect
import GPP_Library.terminals.Emit
import groovy.transform.CompileStatic
import groovyJCSP.PAR
import jcsp.lang.Channel

/**
 * A PipelineOfGroupsPattern comprises a pipeline og process groups.  The sequence comprises
 * Emit; OneFanAny; AnyPipelineOfGroups ; AnyFanOne; Collector. The properties of the pattern provide all the
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
 * @param workers An int specifying the number of workers in the AnyPipelineOfGroupCollects
 * @param cDetails A List of {@link GPP_Library.CompositeDetails} objects containing data pertaining to each group of processes.
 * @param rDetails A {@link GPP_Library.ResultDetails} containing data pertaining to result class used by the Collect process, it MUST be specified.
 * @param stages The number of stages in the pipeline of processes that will be created
 * 					when the Pipeline is run
 * @param outData A List of booleans. If true the stage with the same index will output each processed input object. If false the stage will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output
 *                only happens after the finalise method has been called. outData defaults to [true, ... true]
 * @see GPP_Library.functionals.workers.Worker
 * <p>
 *
 */
@CompileStatic
class PipelineOfGroupsPattern {

  DataDetails eDetails
  int stages
  List <String> stageOp = null
  List stageModifier = null
  int workers
  CompositeDetails cDetails = null
  ResultDetails rDetails
  List <Boolean> outData = null

  def run() {
    def toFanOut = Channel.one2one()
    def toPoG = Channel.one2any()
    def pogOut = Channel.any2one()
    def collectChan = Channel.one2one()

    def emitter = new Emit(
        output: toFanOut.out(),
        eDetails: eDetails )

    def fanOut = new OneFanAny(
        input: toFanOut.in(),
        outputAny: toPoG.out(),
        destinations: workers)

    def pog = new AnyPipelineOfGroups(
        inputAny: toPoG.in(),
        outputAny: pogOut.out(),
        stages: stages,
        cDetails: cDetails,
        stageOp: stageOp,
        stageModifier: stageModifier,
        outData: outData,
        workers: workers)

    def fanIn = new AnyFanOne(
        inputAny: pogOut.in(),
        output: collectChan.out(),
        sources: workers)

    def collector = new Collect(
        input: collectChan.in(),
        rDetails: rDetails )

    new PAR([emitter, fanOut, pog, fanIn, collector ]).run()
  }
}
