package groovyParallelPatterns

import groovy.transform.CompileStatic

@CompileStatic
/**
 * Class {@code UniversalRequest} provides a means of sending a request signal between processes.
 * The request can be either to read into or write from and a List .<p>
 *
 * This class is used internally within the groovyParallelPatterns
 *
 * @param tag an integer where a read request is 0 and a write request is 1
 * @param count the number of elements in the list 	{@code individuals}
 * @param individuals comprising a list of valye
 * */
class UniversalRequest implements DataClassInterface, Cloneable, Serializable{
    int tag = -1   //legal values are readRequest: 0; writeRequest:1
    int count = 0   // number of individuals
    List individuals = []

    @Override
    public Object clone () {
        return null
    }

    @Override
    public Object serialize() {
        return null;
    }



}
