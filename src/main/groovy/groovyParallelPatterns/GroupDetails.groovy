package groovyParallelPatterns

import groovy.transform.CompileStatic

/**
 * GroupDetails holds local class details for each of the workers in a Group process.
 * It is only required if the Worker processes in the Group use a local worker class
 *
 * @param workers The number of Worker processes in the Group
 * @param groupDetails a List of {@code workers} elements each holding the {@link groovyParallelPatterns.LocalDetails} for each Worker process
 * in the Group
 *
 */

@CompileStatic

class GroupDetails implements Serializable, Cloneable {
	int workers
	List <LocalDetails> groupDetails = null	//one local detail per worker in the group

	String toString() {
		String s = "GroupDetails: workers $workers \n"
		groupDetails.each{ ld ->
			s = s + "${ld.toString()}\n"
		}
	}
}
