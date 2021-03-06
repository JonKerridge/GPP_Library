package groovy_parallel_patterns

import groovy.transform.CompileStatic

/**
 * Contains data pertaining to any local class used in the processes that make up a network.
 * The local class itself will be defined by extend-ing the DataClass object.
 * LocalDetails specifies values associated with the class such as the name of the
 * class and key method names.  It also specifies data values associated with these
 * methods.  All properties have default values.
 *
 *@param lName String containing the name of the local class
 *@param lInitMethod String containing the name of the local class' initClass Method
 *@param lInitData a List containing parameter values for the initClass Method
 *@param lCreateMethod String containing the name of the local class' create Method
 *@param lCreateData a List containing parameter values for the create Method
 *@param lFinaliseMethod String containing the name of the local class' finalise Method
 *@param lFinaliseData a List containing parameter values for the finalise Method
 *
 * Note {@code lCreateMethod} and {@code lCreateData}are ONLY used in an
 * {@link groovy_parallel_patterns.terminals.EmitFromInput} process
 */

@CompileStatic
class LocalDetails implements Serializable, Cloneable {
	String lName = ""
	String lInitMethod = ""
	String lCreateMethod = ""
	List lInitData = null
	List lCreateData = null
	String lFinaliseMethod = ""
	List lFinaliseData = null

	String toString() {
		String s = "$lName, $lInitMethod, $lInitData, $lCreateMethod, $lCreateData, $lFinaliseMethod, $lFinaliseData\n"
		return s
	}
}
