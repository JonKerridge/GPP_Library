package groovy_parallel_patterns

import groovy.transform.CompileStatic

/**
 * PipelineDetails holds details for each stage in a Pipeline process.  It is only required
 * if any of the Worker processes in the Pipeline use a local class to undertake the processing
 *
 * @param stages An integer holding the number of stages in the Pipeline
 * @param stageDetails an array of {@code stages} elements each holding the
 * {@link groovy_parallel_patterns.LocalDetails} for each Worker process in the Pipeline
 *
 */
@CompileStatic
class PipelineDetails implements Serializable, Cloneable {
	int stages = 2
	List <LocalDetails> stageDetails = null //one local detail per stage

/**+
 * PipelineDetails constructor for {@code stages} elements in {@code StageDetails}
  * @param stages the number of stages in the
 * {@link groovy_parallel_patterns.functionals.workers.Worker} processes in the pipeline
 */
	PipelineDetails (int stages){
		this.stages = stages
		this.stageDetails = []
	}

/**+
 * Inserts values into {@code stageDetails} for the LocalDetails element indicated by {@code stage}
 *
 * @param stage index of the stage
 * @param lName the name of the local class
 * @param lInitMethod the name of the local class' initialise method
 * @param lInitData	a List of data values used by the initialise method
 * @param lFinaliseMethod the name of the local class' finalise method
 * @param lFinaliseData a List of data values used by the finalise method
 */
	void insertPipelineDetails(
			int stage,
			String lName,
			String lInitMethod,
			List lInitData,
			String lFinaliseMethod,
			List lFinaliseData	)
	{
		stageDetails[stage] = new LocalDetails(
				lName: lName,
				lInitMethod: lInitMethod,
				lInitData: lInitData,
				lCreateMethod: "",
				lCreateData: [],
				lFinaliseMethod: lFinaliseMethod,
				lFinaliseData: lFinaliseData)
	}

	String toString(){
		String s = "PipelineDetails: stages $stages \n"
		stageDetails.each{ ld ->
			s = s + "${ld.toString()}\n"
		}
		return s
	}
}
