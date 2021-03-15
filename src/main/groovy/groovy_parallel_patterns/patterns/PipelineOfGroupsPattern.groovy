package groovy_parallel_patterns.patterns

import groovy_parallel_patterns.CompositeDetails
import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.connectors.reducers.AnyFanOne
import groovy_parallel_patterns.connectors.spreaders.OneFanAny
import groovy_parallel_patterns.functionals.composites.AnyPipelineOfGroups
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import groovy.transform.CompileStatic
import groovy_jcsp.PAR
import jcsp.lang.Channel

/**
 * A PipelineOfGroupsPattern comprises a pipeline og process groups.  The sequence comprises
 * Emit; OneFanAny; AnyPipelineOfGroups ; AnyFanOne; Collector. The properties of the pattern provide all the
 * external values required to run the pattern.  The internal channels required to
 * connect the processes together are all declared within the class outwith programmer concerns.
 * <p>
 * @param eDetails A {@link groovy_parallel_patterns.DataDetails}  object containing information concerning the DataClass
 * used by the Emit process
 * @param stageOp A List of String values identifying the operation to be undertaken
 * 					by each stage process
 * @param stageModifier A List containing the possible modifiers for the operation, within each stage by each worker in a group
 * 					accessing the element that corresponds to the index of the stage.
 * @param workers An int specifying the number of workers in the AnyPipelineOfGroupCollects
 * @param cDetails A List of {@link groovy_parallel_patterns.CompositeDetails} objects containing data pertaining to each group of processes.
 * @param rDetails A {@link groovy_parallel_patterns.ResultDetails} containing data pertaining to result class used by the Collect process, it MUST be specified.
 * @param stages The number of stages in the pipeline of processes that will be created
 * 					when the Pipeline is run
 * @param outData A List of booleans. If true the stage with the same index will output each processed input object. If false the stage will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output
 *                only happens after the finalise method has been called. outData defaults to [true, ... true]
 *
 */
@CompileStatic
class PipelineOfGroupsPattern {

  DataDetails eDetails = null
  int stages = 0
  List <String> stageOp = null
  List stageModifier = null
  int workers = 0
  CompositeDetails cDetails = null
  ResultDetails rDetails = null
  List <Boolean> outData = null

  def run() {
    assert (eDetails != null) : "PoGPattern: eDetails must be specified"
    assert (rDetails != null) : "PoGPattern: rDetails must be specified"
    assert (stageOp != null) : "PoGPattern: stageOp List must be specified"
    assert (stages != 0) : "PoGPattern: stages must be specified"
    assert (workers != 0) : "PoGPattern: workers must be specified"
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
