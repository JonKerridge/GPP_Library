package groovy_parallel_patterns

import groovy.transform.CompileStatic

/**
 * DataDetails contains data pertaining to a data class used in all of the processes.
 * The data class itself will be defined by extend-ing the DataClass object.
 * DataDetails specifies values associated with the class such as the name of the
 * class and key method names.  It also specifies user defined data values associated with these
 * methods.  All properties have default values.
 * In some cases where the defined class is used in a process which requires a local
 * worker class the parameters {@code lName}, {@code lInitMethod}, {@code lInitData}
 * will need to be specified
 *
 * @param dName String containing the name of the data class
 * @param dInitMethod String containing the name of the data class' initClass Method
 * @param dInitData a List containing parameter values for the initClass Method
 * @param dCreateMethod String containing the name of the data class' createInstance Method
 * @param dCreateData a List containing parameter values for the createInstance Method
 * @param lName String containing the name of any local worker class
 * @param lInitMethod String containing the name of the local class' initClass Method
 * @param lInitData a List containing parameter values for the initClass Method of the local class
 */

@CompileStatic
class DataDetails implements Serializable, Cloneable {
  String dName = ""
  String dInitMethod = ""
  List dInitData = null
  String dCreateMethod = ""
  List dCreateData = null
/*
 * In some cases a local class is required, for example, EmitWithLocal
 * thus the following properties will only be used when required
 */
  String lName = ""
  String lInitMethod = ""
  List lInitData = null
}
