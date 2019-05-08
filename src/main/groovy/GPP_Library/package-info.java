/**
 *
 * Groovy Parallel Patterns.<p>
 *
 * A Library to Support Parallel Programming using<br>
 * Communicating Sequential Processes CAR Hoare CACM 1978,<br>
 * the JCSP Library, Peter Welch, University of Kent and<br>
 * Groovy Helper Classes to Support JCSP, Jon Kerridge, Edinburgh Napier University.<p>
 *
 * Package GPP_Library defines some basic classes and interfaces used by the rest of the library.<p>
 *
 * All user defined data classes utilising the library should extend DataClass.<p>
 *
 * In addition, a number of methods are required, depending upon the use of the class.
 * These are described more fully in the information in the packages; terminals, workers and transformers.  A list
 * of the required methods follows: <br>
 * initClass([initialData]) used to initialise an object<br>
 * createInstance([createData]) used to create an instance of the class<br>
 * finalise([finaliseData]) used to undertake final operations on an object<br>
 * collector() used to collect and save results<br>
 * function([dataModifier, wc]) carries out a function on an object using an optional local worker class wc<p>
 *
 * More specialised methods are required for more specific tasks: <br>
 * updateDisplayList( ) used to update the data structure used in a graphical user interface in CollectUI<br>
 * feedbackBool() used to send a boolean value to a previous process<br>
 * feedbackObject() used to send an object instance to a previous process<p>
 *
  */

package GPP_Library;