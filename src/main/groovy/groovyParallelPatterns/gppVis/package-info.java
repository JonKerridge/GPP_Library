/**
 * The gppVis package contains the means of visualising process network operation and also the logging capability<p>
 * It contains no methods or classes the user needs to be aware of because the required declarations are
 * automatically incorporated by the GPP_Builder program.
 *
 * In order to invoke logging the user must specify at least one process
 * that specifies a logPropertyName and a logPhaseName.
 *
 * The GPP_Builder program then requires an annotation.
 * <pre>
 * //@log collectors "./fileName"
 * </pre>
 * <p>
 * This MUST be placed immediately BEFORE the specification of the Emit process in the network specification.
 *
 * The value of collectors should be the number of Collect processes in the network,
 * typically 1 but when there is a group of collectors either explicitly or implicitly
 * the value should be modified accordingly.
 *
 * The fileName will be the name of the file to which the log file will be written in the current directory.
 * It will also be the title that appears in the visualiser window.
 *
 * The Logging/Visualising capability uses JavaFX which is incorporated into the library build automatically.
 * */

package groovyParallelPatterns.gppVis;