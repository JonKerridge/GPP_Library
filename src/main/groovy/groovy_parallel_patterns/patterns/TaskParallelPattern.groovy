package groovy_parallel_patterns.patterns

import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.PipelineDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.functionals.pipelines.OnePipelineOne
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import groovy.transform.CompileStatic
import groovy_jcsp.PAR
import jcsp.lang.Channel

/**
 * A TaskParallelPattern comprises a sequence of processes in a so-called pipeline.  The sequence comprises
 * Emit; Pipeline; Collect. The properties of the pattern provide all the
 * external values required to run the pattern.  The internal channels required to
 * connect the processes together are all declared within the class outwith programmer concerns.
 * <p>
 * @param eDetails A {@link groovy_parallel_patterns.DataDetails}  object containing information concerning the DataClass
 * used by the Emit process
 * @param stageOp A List of String values identifying the operation to be undertaken
 * 					by each stage process
 * @param stageModifier A List Containing a possible modifier for the operation, with each stage
 * 					accessing the element that corresponds to the index of the stage. The values for
 * 				each stage are stored as a List.
 * @param pDetails A List of {@link groovy_parallel_patterns.LocalDetails} objects containing data pertaining to any local class used by the stage processes.
 * @param rDetails A {@link groovy_parallel_patterns.ResultDetails} object containing data pertaining to result class used by the Collect process, it MUST be specified.
 * @param stages The number of stages in the pipeline of processes that will be created
 * 					when the Pipeline is run
 * @param outData A List of booleans. If true the stage with the same index will output each processed input object. If false the stage will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output
 *                only happens after the finalise method has been called. outData defaults to [true, ... true]
 *
 *
*/

@CompileStatic
class TaskParallelPattern {

	DataDetails eDetails = null
	int stages = 0
	List stageOp = null
	List stageModifier = null
	PipelineDetails pDetails = null
	ResultDetails rDetails = null
	List <Boolean> outData = null

	def run() {
		assert (eDetails != null) : "TaskParallelPattern: eDetails must be specified"
		assert (rDetails != null) : "TaskParallelPattern: rDetails must be specified"
		assert (stageOp != null) : "TaskParallelPattern: stageOp List must be specified"
		assert (stages != 0) : "TaskParallelPattern: stages must be specified"

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
