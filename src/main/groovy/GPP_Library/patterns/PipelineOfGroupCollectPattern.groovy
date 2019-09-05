package GPP_Library.patterns

import GPP_Library.CompositeDetails
import GPP_Library.DataDetails
import GPP_Library.connectors.spreaders.OneFanAny
import GPP_Library.functionals.composites.AnyPipelineOfGroupCollects
import GPP_Library.terminals.Emit
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * A TaskParallelOFGroupCollects comprises a sequence of processes in a so-called Farm.  The sequence comprises
 * Emit; OneFanAny; AnyPipelineOfGroupCollects. The properties of the pattern provide all the
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
 * @param rDetails A list of {@link GPP_Library.ResultDetails} objects containing data pertaining to result class used by each of the Collect process, it MUST be specified.
 * @param stages The number of stages in the pipeline of processes that will be created
 * 					when the Pipeline is run, not incl=uding the final Collect process.
 * @param outData A List of booleans. If true the stage with the same index will output each processed input object. If false the stage will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output
 *                only happens after the finalise method has been called. outData defaults to [true, ... true]
 * @see GPP_Library.functionals.workers.Worker
 * <p>
 *
*/
@CompileStatic
class PipelineOfGroupCollectPattern {

	DataDetails eDetails
	int stages
	List <String> stageOp = null
	List stageModifier = null
	int workers
	CompositeDetails cDetails = null
	List rDetails
	List <Boolean> outData = null

	def run() {
		def toFanOut = Channel.one2one()
		def toPoG = Channel.one2any()

		def emitter = new Emit( output: toFanOut.out(),
								eDetails: eDetails )

		def fanOut = new OneFanAny(input: toFanOut.in(),
									outputAny: toPoG.out(),
									destinations: workers)

		def poG = new AnyPipelineOfGroupCollects( inputAny: toPoG.in(),
										stages: stages,
										cDetails: cDetails,
										stageOp: stageOp,
										stageModifier: stageModifier,
										rDetails: rDetails,
										outData: outData,
										workers: workers)

		new PAR([emitter, fanOut, poG]).run()
	}
}
