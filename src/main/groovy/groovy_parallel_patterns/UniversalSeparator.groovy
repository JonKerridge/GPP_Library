package groovy_parallel_patterns

import groovy.transform.CompileStatic

/**
 *
 * Class {@code UniveralSeparator} is sent from one to process to another used as a signal to the receiving
 * process that it should undertake some operation for the sending process.
 *
 * Typically the sender and receiver processes will be in a client-server relationship
 * The sender of the signal will be the client and will be ready to receive any response
 * from the receiver immediately.
 * The receiver will act as a server and will respond to the sending client, if there is
 * a response, in finite time.<p>
 *
 * In some cases both a {@code UniversalSignal} and a {@code UniversalSeparator} are required in
 * more complex process engines.  <p>This object is used internally within the library.
*/

@CompileStatic
class UniversalSeparator implements Cloneable, Serializable{
	int signal = -3
}
