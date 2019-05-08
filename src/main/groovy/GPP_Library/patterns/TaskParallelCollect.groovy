package GPP_Library.patterns

import GPP_Library.DataDetails
import GPP_Library.PipelineDetails
import GPP_Library.ResultDetails
import GPP_Library.functionals.pipelines.OnePipelineOne
import GPP_Library.terminals.Collect
import GPP_Library.terminals.Emit
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * A TaskParallelCollect comprises a sequence of processes in a so-called Farm.  The sequence comprises
 * Emit; Pipeline; Collect. The properties of the pattern provide all the
 * external values required to run the pattern.  The internal channels required to
 * connect the processes together are all declared within the class outwith programmer concerns.
 * <p>
 * @param eDetails A {@link GPP_Library.DataDetails}  object containing information concerning the DataClass
 * used by the Emit process
 * @param lDetails A {@link GPP_Library.LocalDetails}  list object containing information concerning each stage of the Pipeline
 * @param stageOp A List of String values identifying the operation to be undertaken
 * 					by each stage process
 * @param stageModifier A List Containing a possible modifiers for the operation, with each stage
 * 					accessing the element that corresponds to the index of the stage.
 * @param pDetails A List of {@link GPP_Library.PipelineDetails} objects containing data pertaining to any local class used by the stage processes.
 * @param rDetails A {@link GPP_Library.ResultDetails} object containing data pertaining to result class used by the Collect process, it MUST be specified.
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
class TaskParallelCollect {

	DataDetails eDetails
	int stages
	List stageOp = null
	List stageModifier = null
	PipelineDetails pDetails = null
	List modifier = null
	ResultDetails rDetails
	List <Boolean> outData = null

	def run() {
		def chan1 = Channel.one2one()
		def chan2 = Channel.one2one()

		def emitter = new Emit( output: chan1.out(),
								eDetails: eDetails )

		def pipeline = new OnePipelineOne ( input: chan1.in(),
											  output: chan2.out(),
											  stages: stages,
											  pDetails: pDetails,
											  outData: outData,
											  stageOp:stageOp,
											  stageModifier: stageModifier)

		def collector = new Collect( input: chan2.in(),
									 rDetails: rDetails )

		new PAR([emitter, pipeline, collector]).run()
	}
}
