package groovyParallelPatterns

import groovy.transform.CompileStatic;

/**
 * The DataClassInterface defines the clone and  serialize methods required by the Groovy Parallel
 * Patterns library for any data class that is to be manipulated by all the processes
 * within the library.  The programmer is required to implement those methods required
 * for their application, if necessary.  The class dataClass provides a null implementation of all
 * the methods.  The programmer should extend the class DataClass for all data objects used in applications using the library.<p>
 * In addition other methods need to be implemented for each process type, their details are to be
 * found in the documentation associated with each process.<p>
 *
 */
@CompileStatic
public interface DataClassInterface {

	final int normalTermination = 0
	final int normalContinuation = 1
	final int completedOK = 2
//	final int overridenMethodNotImplemented = -100
//    final int readRequest = 0
//    final int writeRequest = 1


	/**
	 *
	 * This method creates a clone of the object; if an object contains other objects then the programmer
	 * MUST write their own clone method so that it creates a deep copy rather than the normal shallow copy.<p>
	 * Clone MUST be implemented if an object is passed through a
	 * {@link groovyParallelPatterns.connectors.spreaders.OneParCastList spreader}process that
	 * involves any form of Cast.
	 *
	 * @usage o.clone()
	 * @return a cloned copy of the object o
	 */
	@Override
	public Object clone()


//	/**
//	 * Used to create a version of an object that removes machine specific properties, such as
//	 * static variables, channel addresses that are not Serializable.<p>
//	 * serialize MUST be implemented if the object is processed by means of
//	 * {@link groovyParallelPatterns.cluster.connectors.OneNodeRequestedList}.
//	 *
//	 * @return a version of the object that implements the Serializable interface
//	 */
//	public Object serialize()

// removed in version 1.0.9 when creating gppClusterBuilder
}
