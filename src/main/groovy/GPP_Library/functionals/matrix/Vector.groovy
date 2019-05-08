package GPP_Library.functionals.matrix

import groovy.transform.CompileStatic

/**
 * Vector implements a List based structure with the ability to manipulate elements by indeces
 *
 * @param elements the number of entries in the vector
 * @param entries a List holding the vector values, the type of the entires is not specified
 * and the vector is initially empty
 */
@CompileStatic
class Vector implements Serializable, Cloneable {
    int elements
    List entries = []

    /**
     * The vector is set to the values cointained in entryValues
     *
     * @param entryValues a List containing elements values
     */
    void setEntries (List entryValues){
		for ( e in 0 ..< elements) entries[e] = entryValues[e]
	}

    /**
     * Returns a List of values the size of the range from the Vector
     * corresponding to the indices in the range
     *
     * @param range specifies the indices to be returned
     * @return a List of size range values
     */
    List getEntries (Range range){
		List entryValues = []
		for ( e in range) entryValues << entries[(int)e]
		return entryValues
	}

    /**
     * The elements of the vector in the specified range are set to the values
     * contained in entryValues
     *
     * @param entryValues a List containing, size of the range, values
     */
    void setEntries (List entryValues, Range range){
		for ( e in range) entries[(int)e] = entryValues[(int)e]
	}

    /**
     * Returns a List of elements values that are the current value
     * held in the Vector
     *
     * @return a List of element values
     */
    List getEntries (){
		List entryValues = []
		for ( e in 0 ..< elements) entryValues << entries[e]
		return entryValues
	}

    String toString() {
		String s = "$entries"
		return s
	}

}
