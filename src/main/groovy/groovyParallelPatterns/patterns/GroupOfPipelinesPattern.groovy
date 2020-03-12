package groovyParallelPatterns.patterns

import groovyParallelPatterns.CompositeDetails
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.connectors.reducers.AnyFanOne
import groovyParallelPatterns.connectors.spreaders.OneFanAny
import groovyParallelPatterns.functionals.composites.AnyGroupOfPipelines
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import groovy.transform.CompileStatic
import groovyJCSP.PAR
import jcsp.lang.Channel

/**
 * A GroupOfPipelinesPattern comprises a group of pipelines.  The sequence comprises
 * Emit; OneFanAny; AnyGroupOfPipelines; AnyFanOne, Collect. The properties of the pattern provide all the
 * external values required to run the pattern.  The internal channels required to
 * connect the processes together are all declared within the class outwith programmer concerns.
 * <p>
 * @param eDetails A {@link groovyParallelPatterns.DataDetails}  object containing information concerning the DataClass
 * used by the Emit process, it MUST be specified.
 * @param stageOp A List of String values identifying the operation to be undertaken
 * 					by each stage process
 * @param stageModifier A List containing the possible modifiers for the operation, within each stage by each worker in a group
 * 					accessing the element that corresponds to the index of the stage.
 * @param groups An int specifying the number of parallel pipelines
 * @param cDetails A List of {@link groovyParallelPatterns.CompositeDetails} objects containing data pertaining to each group of processes.
 * @param rDetails A {@link groovyParallelPatterns.ResultDetails} containing data pertaining to result class
 * used by the Collect process, it MUST be specified.
 * @param stages The number of stages in the pipeline of processes that will be created
 * 					when the Pipeline is run
 * @param outData a list of groups lists each entry of which comprises stages entries.  Each entry is a boolean value
 * 			such that if true the worker processes in that stage
 * 			will output each processed input object. If false the process will output
 * 			the workerClass once only, after it has processed all the input data objects. If omitted the value defaults to true.
 *
 */
@CompileStatic
class GroupOfPipelinesPattern {

  DataDetails eDetails = null
  int stages = 0
  List <String> stageOp = null
  List stageModifier = null
  int groups = 0
  CompositeDetails cDetails = null
  ResultDetails rDetails = null
  List <List<Boolean>> outData = null

  def run() {
    assert (eDetails != null) : "GoPPattern: eDetails must be specified"
    assert (rDetails != null) : "GoPPattern: rDetails must be specified"
    assert (stageOp != null) : "GoPPattern: stageOp List must be specified"
    assert (stages != 0) : "GoPPattern: stages must be specified"
    assert (groups != 0) : "GoPPattern: groups must be specified"
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
