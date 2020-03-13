package groovyParallelPatterns

import groovy.transform.CompileStatic

/**
 * GroupDetails holds local class details for each of the workers in a Group process.
 * It is only required if the Worker processes in the Group use a local worker class
 *
 * @param workers The number of {@link groovyParallelPatterns.functionals.workers.Worker Worker} processes in the Group
 * @param groupDetails a List of {@code workers} elements each holding the {@link groovyParallelPatterns.LocalDetails} for each Worker process
 * in the Group
 *
 */

@CompileStatic

class GroupDetails implements Serializable, Cloneable {
	int workers
	List <LocalDetails> groupDetails = null	//one local detail per worker in the group
/**+
 * GroupDetails constructor for {@code workers} elements in {@code groupDetails}
 * @param workers the number of {@link groovyParallelPatterns.functionals.workers.Worker} processes in the group
 */
	GroupDetails (int workers){
		this.workers = workers
		this.groupDetails = []
	}
/**+
 * Inserts values into {@code groupDetails} for the LocalDetails element indicated by {@code worker}
 * @param worker index of the worker
 * @param name the name of the local class
 * @param initMethod the name of the local class' initialise method
 * @param initData	a List of data values used by the initialise method
 * @param finaliseMethod the name of the local class' finalise method
 * @param finaliseData a List of data values used by the finalise method
 */
	void insertGroupDetails ( int worker,
														String lName,
														String lInitMethod,
														List lInitData,
														String lFinaliseMethod,
														List lFinaliseData	)
	{
		groupDetails[worker] = new LocalDetails(
				lName : lName,
				lInitMethod: lInitMethod,
				lInitData: lInitData,
				lCreateMethod: "",
				lCreateData: [],
				lFinaliseMethod: lFinaliseMethod,
				lFinaliseData: lFinaliseData)
	}

	String toString() {
		String s = "GroupDetails: workers $workers \n"
		groupDetails.each{ ld ->
			s = s + "${ld.toString()}\n"
		}
	}
}
