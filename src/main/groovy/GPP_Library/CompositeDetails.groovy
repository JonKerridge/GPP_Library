package GPP_Library

import groovy.transform.CompileStatic

/**
 * CompositeDetails is used to define the workers and stages of the skeletons appearing in the
 * {@link GPP_Library.functionals.composites} package.  These are either Groups of Pipelines or
 * Pipelines of Groups.  The data structure cDetails is a 2-dimensional array of LocalDetails;
 * where the first index (rows) is subscripted by the number of workers in each Group
 * and the second index (columns) is subscripted by the number of stages in the pipeline.<p>
 * It is assumed that all workers in the same group undertake the same function and modifier which
 * are specified as parameters of the Composite structure itself.<p>
 * Methods are provided to extract the required details by Group or by Stage as necessary, but user code
 * will not need to call these methods.<p>
 * A method is provided to insert the details into the object called insertCompositeDetails.<p>
 *
 * @param workers The number of worker processes in each group
 * @param stages The number of stages in the pipeline
 * @param cDetails A LocalDetails[workers][stages] structure
 *
 */
@CompileStatic
class CompositeDetails implements Serializable, Cloneable {
	int workers = 2
	int stages = 2
	// all of cDetails MUST be non-null
    List < List <LocalDetails>> cDetails = null // subscripted by workers (rows) then stages (columns)

	GroupDetails extractByStage (int stage){
		// extracts details for all the workers in a group corresponding to the stage
        GroupDetails gDetails = new GroupDetails(workers: workers)
        gDetails.groupDetails = []
		for ( w in 0 ..< workers){
			gDetails.groupDetails << new LocalDetails(
				lName: cDetails[w][stage].lName,
				lInitMethod: cDetails[w][stage].lInitMethod,
				lInitData: cDetails[w][stage].lInitData,
				lFinaliseMethod:  cDetails[w][stage].lFinaliseMethod,
				lFinaliseData:  cDetails[w][stage].lFinaliseData)
		}
//		println "CD-ebg: stage $stage \n${gDetails.groupDetails.toString()}"
		return gDetails
	}

	PipelineDetails extractByPipe(int pipe){
		// extracts details for all the stages in the pipe
		PipelineDetails pDetails = new PipelineDetails(stages: stages) 
        pDetails.stageDetails = []       
		for ( s in 0 ..< stages) {
			pDetails.stageDetails << new LocalDetails(
				lName: cDetails[pipe][s].lName,
				lInitMethod: cDetails[pipe][s].lInitMethod,
				lInitData: cDetails[pipe][s].lInitData,
				lFinaliseMethod:  cDetails[pipe][s].lFinaliseMethod,
				lFinaliseData:  cDetails[pipe][s].lFinaliseData)
		}
//		println "CD-ebp: pipe $pipe\n${pDetails.stageDetails.toString()}"
		return pDetails
	}
    
    CompositeDetails (int workers, int stages) {
        this.workers = workers
        this.stages = stages
        this.cDetails = []
        for ( w in 0..< workers) {
            List <LocalDetails> stageDetails = []
//            for ( s in 0..< stages) {
//                stageDetails << new LocalDetails()
//                println "creating W: $w, S:$s = "
//            }
            cDetails[w] = stageDetails
        }
//        println "$cDetails"
    }

	/** Used to insert specific instances of a LocalDetails object into cDetails
	 * @param group the index of the group being defined
	 * @param stage the index of the pipeline stage being defined
	 * @param name A String specifying the name of any local data class used by a worker
	 * @param initMethod A String specifying the initMethod of any local data class
	 * @param initData A list containing parameters for the initMethod
	 * @param finaliseMethod A String specifying the finalise method of any local data class
	 * @param finaliseData A list containing parameters for the finaliseMethod
	 */
	void insertCompositeDetails(int group,
								int stage,
								String name,
								String initMethod,
								List initData,
								String finaliseMethod,
								List finaliseData) {
//        println "Inserting $group, $stage"
		cDetails [group][stage] = new LocalDetails( lName : name,
													lInitMethod: initMethod,
													lInitData: initData,
													lFinaliseMethod: finaliseMethod,
													lFinaliseData: finaliseData)
//        println "${cDetails [group][stage]}"
	}

	String toString(){
		String str = "Composite Details Workers: $workers, Stages: $stages\n"
		for ( p in 0 ..< workers){
			for ( s in 0..< stages){
				str = str + "p: $p; s: $s - ${cDetails[p][s].toString()}"
			}
		}
		return str
	}
}
