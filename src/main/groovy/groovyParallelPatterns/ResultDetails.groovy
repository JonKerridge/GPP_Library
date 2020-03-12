package groovyParallelPatterns

import groovy.transform.CompileStatic

/**
 * ResultDetails contains data pertaining to a result class used in Collect processes.
 * The result class itself will be defined by extend-ing the DataClass object.
 * ResultDetails specifies values associated with the class such as the name of the
 * class and key method names.  It also specifies data values associated with these
 * methods.  All properties have default values.
 *
 *@param rName String containing the name of the result class
 *@param rInitMethod String containing the name of the result class' initClass Method
 *@param rInitData a List containing parameter values for the initClass Method
 *@param rCollectMethod String containing the name of the result class' collect Method in
 *the case of a GUI based collector then this method must update the display list in
 *{@link groovyParallelPatterns.terminals.CollectUI}
 *@param rFinaliseMethod String containing the name of the result class' finalise Method
 *@param rFinaliseData a List containing parameter values for the finalise Method
 */

@CompileStatic
class ResultDetails implements Serializable, Cloneable {
	String rName = ""
	String rInitMethod = ""
	List rInitData = null
	String rCollectMethod = ""
	String rFinaliseMethod = ""
	List rFinaliseData = null

}
