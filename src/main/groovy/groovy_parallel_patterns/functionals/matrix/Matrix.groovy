package groovy_parallel_patterns.functionals.matrix

import groovy.transform.CompileStatic

/**
 * Matrix implements a 2-dimensional array of objects
 *
 * @param rows the number of rows
 * @param columns the number of columns
 * @param entries the elements of the matrix indexed by row and column; initially
 * the entries are initialised to null
 *
 */
@CompileStatic
class Matrix implements Serializable, Cloneable {
    int    rows
    int columns
    Object [][] entries = null	// ordered by rows then columns

    /**
     * Returns the values in column c of the matrix
     *
     * @param c the required column number
     * @return a list of the column values
     */
    List getByColumn (int c) {
		List colValues = []
		for ( r in 0..<rows) colValues << entries[(int)r][c]
		return colValues
	}

    /**
     * Returns the values in column c of the matrix for a limited range of rows
     * @param c the required column number
     * @param range specifes a range of row indices
     * @return a list of column values one for each member of range
     */
    List getByColumn (int c, Range range) {
		List colValues = []
		for ( r in range) colValues << entries[(int)r][c]
		return colValues
	}

    /**
     * Returns the values in column r of the matrix
     *
     * @param r the required row number
     * @return a List of row values
     */
    List getByRow (int r) {
		List rowValues = []
		for ( c in 0..<columns) rowValues << entries[r][(int)c]
		return rowValues
	}

    /**
     * Returns the values in row r for of the matrix for a limited range of columns
     *
     * @param r the required row number
     * @param range the set of column numbers from which the values will be returned
     * @return a List of row values for the specified range
     */
    List getByRow(int r, Range range){
		List rowValues = []
		for ( c in range) rowValues << entries[r][(int)c]
		return rowValues
	}

    /**
     * Set the values in a column of the matrix
     *
     * @param colValues a List of column values
     * @param c the index of the column to be set
     */
    void setByColumn(List colValues, int c){
		for ( r in 0..<rows) entries[(int)r][c] = colValues[(int)r]
	}

    /**
     * Set the values in a row of the matrix
     *
     * @param rowValues a List of row values
     * @param r the index of the row to be set
     */
    void setByRow(List rowValues, int r){
		for ( c in 0..< columns)entries[r][c] = rowValues[(int)c]
	}

    /**
     * Set the values in a column of the matrix in a limited range of rows
     *
     * @param colValues a List of column values
     * @param c the index of the column to be set
     * @param range the set of rows to be set
     */
    void setByColumn(List colValues, int c, Range range){
		int i = 0
		for ( r in range) {
			entries[(int)r][c] = colValues[i]
			i = i + 1
		}
	}

    /**
     * Set the values in a row of the matrix for a limited range of columns
     *
     * @param rowValues a List of row values
     * @param r the index of the row to be set
     * @param range the set of columns to be set
     */
    void setByRow(List rowValues, int r, Range range){
		int i = 0
		for ( c in range){
			entries[r][(int)c] = rowValues[i]
			i = i + 1
		}
	}

    /**
     * Returns a 3 element array, the centre of which is specified as [r, c]
     *
     * @param r row index of the centre element of the returned matrix
     * @param c column index of the centre element of the returned matrix
     * @return an array of three ints
     */
    int[] getImageRows (int r, int c){
        // c indicate the pixel column being calculated
        // assumes kernel is 3x3
        int [] rowValues = new int[3]
        int index = 0
        for ( p in (c-1)..(c+1)) {
            rowValues[index] =  (int)entries[r][(int)p]
            index = index + 1
        }
//      println "GIR: [$r, $c] = $rowValues"
        return rowValues
    }

    /**
     * Returns a span-sized element array, the centre of which is specified as [r, c]
     *
     * @param r row index of the centre element of the returned matrix
     * @param c column index of the centre element of the returned matrix
     * @param span the size of the row to be returned, assumed to be odd
     * @return an array of span ints
     */
    int[] getImageRows (int r, int c, int span){
        // c indicate the pixel column being calculated
        // assumes kernel is span x span and span is odd
        int [] rowValues = new int[span]
        int wide = (int)(span / 2)
        int index = 0
//        print "GIR: [$r, $c] $span $wide ="
        for ( p in (c - wide)..(c + wide)) {
            rowValues[index] =  (int)entries[r][(int)p]
            index = index + 1
        }
//        println " $rowValues"
        return rowValues
    }

    String toString() {
		String s = ""
		for ( r in 0..(rows-1)){
			s = s + "$r: "
			for ( c in 0..(columns - 1)){
				s = s + "\t${entries[(int)r][(int)c]}"
			}
			s = s + "\n"
		}
		s = s + "\n"
		return s
	}

}
