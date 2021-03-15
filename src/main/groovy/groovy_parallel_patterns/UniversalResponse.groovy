package groovy_parallel_patterns

import groovy.transform.CompileStatic
/**
 * Class {@code UniversalResponse} is used internally to return values in response
 * to a {@code UniversalRequest}
 *
 * <p>This object is used internally within the library.
 * @param payload a list of value
 * */
@CompileStatic
class UniversalResponse implements Cloneable, Serializable{
    List payload = []
}
