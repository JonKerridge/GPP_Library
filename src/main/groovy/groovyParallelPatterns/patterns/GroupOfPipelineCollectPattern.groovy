package groovyParallelPatterns.patterns

import groovyParallelPatterns.CompositeDetails
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.connectors.spreaders.OneFanAny
import groovyParallelPatterns.functionals.composites.AnyGroupOfPipelineCollects
import groovyParallelPatterns.terminals.Emit
import groovy.transform.CompileStatic
import groovyJCSP.PAR
import jcsp.lang.Channel

/**
 * A GroupOfPipelineCollectPattern comprises a group of pipelines.  The sequence comprises
 * Emit; OneFanAny; AnyGroupOfPipelineCollect. The properties of the pattern provide all the
 * external values required to run the pattern.  The internal channels required to
 * connect the processes together are all declared within the class outwith programmer concerns.
 * <p>
 * @param eDetails A {@link groovyParallelPatterns.DataDetails}  object containing information concerning the DataClass
 * used by the Emit process. It MUST be specified.
 * @param stageOp A List of String values identifying the operation to be undertaken
 * 					by each stage process
 * @param stageModifier A List containing the possible modifiers for the operation, within each stage by each worker in a group
 * 					accessing the element that corresponds to the index of the stage.
 * @param groups A number specifying the number of parallel pipelines
 * @param cDetails A List of {@link groovyParallelPatterns.CompositeDetails} objects containing data pertaining to
 * each group of processes.
 * @param rDetails A list of {@link groovyParallelPatterns.ResultDetails} , of size groups ,containing data
 * pertaining to the result class used by each of the Collect processes, it MUST be specified.
 * @param stages The number of stages in the pipeline of processes that will be created
 * 					when the Pipeline is run, not including the final Collect process
 * @param outData a list of groups lists each entry of which comprises stages entries.  Each entry is a boolean value
 * 			such that if true the worker processes in that stage
 * 			will output each processed input object. If false the process will output
 * 			the workerClass once only, after it has processed all the input data objects. If omitted the value defaults to true.
 *
 */
@CompileStatic
class GroupOfPipelineCollectPattern {

  DataDetails eDetails = null
  int stages = 0
  List <String> stageOp = null
  List stageModifier = null
  int groups = 0
  CompositeDetails cDetails = null
  List rDetails = null
  List <List<Boolean>> outData = null

  def run() {
    assert (eDetails != null) : "GoPCPattern: eDetails must be specified"
    assert (rDetails != null) : "GoPCPattern: rDetails must be specified"
    assert (stageOp != null) : "GoPCPattern: stageOp List must be specified"
    assert (stages != 0) : "GoPCPattern: stages must be specified"
    assert (groups != 0) : "GoPCPattern: groups must be specified"
    def toFanOut = Channel.one2one()
    def toGoP = Channel.one2any()

    def emitter = new Emit( output: toFanOut.out(),
        eDetails: eDetails )

    def fanOut = new OneFanAny(input: toFanOut.in(),
        outputAny: toGoP.out(),
        destinations: groups)

    def gop = new AnyGroupOfPipelineCollects(
        inputAny: toGoP.in(),
        stages: stages,
        cDetails: cDetails,
        rDetails: rDetails,
        stageOp: stageOp,
        stageModifier: stageModifier,
        groups: groups,
        outData: outData)

        new PAR([emitter, fanOut, gop]).run()
  }
}
