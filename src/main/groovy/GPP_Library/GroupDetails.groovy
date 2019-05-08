package GPP_Library

import groovy.transform.CompileStatic

/**
 * GroupDetails holds details for each of the workers in a Group process.
 *
 * @param workers The number of workers in the Group
 * @param groupDetails a List of workers elements each holding the {@link GPP_Library.LocalDetails} for each Worker process
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
