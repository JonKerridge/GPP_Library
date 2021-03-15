package groovy_parallel_patterns

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

/**
 *
 * An object sent from one process to another indicating that the end of
 * emitted data objects has been reached.<p>
 *
 * <p>This object is used internally within the library.
*/
@CompileStatic
@AutoClone(style= AutoCloneStyle.SERIALIZATION)

class       UniversalTerminator  implements Serializable{
	int terminator = -1

	// log removed in version 1.1.11
//	List log = []		// a list of lists of LogEntry s

}
