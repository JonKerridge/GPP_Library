package GPP_Library

import groovy.transform.CompileStatic

/**
 *
 * An object sent from one process to another indicating that the end of
 * emitted data objects has been reached.<p>
 *
 * The log property of UniversalTerminator is an initially empty List.  If a process in the
 * network has specified a LogPhaseName property then the Log property will have appended to it
 * the logging details associated with that process.
 *
 *
*/

@CompileStatic
class UniversalTerminator implements Cloneable, Serializable{
//	int terminator = -1
	List log = []		// a list of lists of LogEntry s
}
