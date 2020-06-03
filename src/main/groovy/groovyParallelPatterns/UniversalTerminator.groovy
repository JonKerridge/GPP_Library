package groovyParallelPatterns

import groovy.transform.CompileStatic

/**
 *
 * An object sent from one process to another indicating that the end of
 * emitted data objects has been reached.<p>
 *
 * The {@code log} property of UniversalTerminator is an initially empty List.  If a process in the
 * network has specified a LogPhaseName property then the {@code log} property will have appended to it
 * the logging details associated with that process.<p>
 * When the {@code UniversalTerminator} arrives at a Collect process the {@code log} will be written
 * to a file specified in the annotations associated with logging and processed by GPP_Builder.
 *
 * <p>This object is used internally within the library.
*/

@CompileStatic
class       UniversalTerminator implements Cloneable, Serializable{
//	int terminator = -1
	List log = []		// a list of lists of LogEntry s
}
