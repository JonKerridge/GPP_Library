package groovy_parallel_patterns

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle
import groovy.transform.CompileStatic

/**
 *
 * An object sent from one to process to another used as a signal to the receiving
 * process that it should undertake some operation for the sending process.
 *
 * <p>This object is used internally within the library.
 *
 * Typically the sender and receiver processes will be in a client-server relationship
 * The sender of the signal will be the client and will be ready to receive any response
 * from the receiver immediately.
 * The receiver will act as a server and will respond to the sending client, if there is
 * a response, in finite time.<p>
 *
 *
*/
@AutoClone (style = AutoCloneStyle.SERIALIZATION)
@CompileStatic
class UniversalSignal implements Cloneable, Serializable{
	int signal = -2
}
