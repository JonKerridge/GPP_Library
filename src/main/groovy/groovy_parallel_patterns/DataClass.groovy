package groovy_parallel_patterns

import groovy.transform.CompileStatic

/**
 * DataClass provides the super class for all data classes used in groovy_parallel_patterns, it provides
 * a null implementation of all the abstract methods defined in DataClassInterface,
 * thus  all data classes used in groovy_parallel_patterns should extend DataClass, overriding the methods
 * required for that data class only.<p>
 * The class also implements the interfaces Serializable, so that objects can be transferred over TCP/IP connections
 * and Cloneable so that copies of objects can be created.  If the Class contains some other classes then method clone
 * may need to be overwritten.
 *  <p>
 *
 */
@CompileStatic
class DataClass implements DataClassInterface, Serializable, Cloneable {

  List <String> callerMessages = [
      "Worker: error during Local Class Initialise: ",                        //0
      "Worker: error during Function: ",                                      //1
      "Worker: error during Local Class Finalise: ",                          //2
      "Emit: error during Emit Class Intialise: ",                            //3
      "Emit: error during Emit Class Create: ",                               //4
      "Collect: error during Collect Class Initialise: ",                     //5
      "Collect: error during Collecting: ",                                   //6
      " ",                                                                    //7
      "Collect: error during Finalise: ",                                     //8
      "ThreePhaseWorker: error during Local Class Initialise: ",              //9
      "Three PhaseWorker : error during Input Method: ",                      //10
      "Three PhaseWorker : error during Work method: ",                       //11
      "EmitWithLocal: error during Local Class Initialise: ",                 //12
      "EmitWithLocal: error during Emit Class Intialise: ",                   //13
      "EmitWithLocal: error during Emit Class Create: ",                      //14
      "EmitFromInput: error during Emit Class Create: ",                      //15
      "CombineNto1: error during Local Class Init: ",                         //16
      "CombineNto1: error during Output Class init: ",                        //17
      "CombineNto1: error during call of combineMethod: ",                    //18
      "CombineNto1: error during Output Class finaliseMethod:",               //19
      "EmitWithFeedback: error during Emit Class Intialise: ",                //20
      "EmitFromInput: error during Local Class Init: ",                       //21
      "FeedbackBool: error during Feedback Class Initialise",                 //22
      "N_WayMerge: error during mergeChoice method",                          //23
      "OneIndexedList: destination index invalid",                            //24
      "FeedbackObject: error during init phase",                              //25
      "FeedbackObject: error during invoke",                                  //26
      "Evolutionary: Client Process initialisation method failed",           //27
      "Evolutionary: Client process call to request initialisation failed",  //28
      "Evolutionary: Server process call to request initialisation failed",  //29
      "Evolutionary: Server attempt to initialise server class failed",      //30
      "Evolutionary: Server failed to incorporate a new child",              //31
      "Evolutionary: Server failed to finalise correctly",                   //32
      "Evolutionary: Client process failed to create new individual",        //33
      "EmitWithFeedback: error during feedbackMethod call: "	,				       //34
      "FeedbackSensor: error during fbCreateMethod call:",					         //35
      "ThreePhaseWorker: error during outFunction processing:"                //36
  ]

  /**
   * Invokes a call of the method named {@code methodName} in the Object {@code o} which always
   * returns a value.  If the return value is negative, the complete process network will terminate
   * with an appropriate message indicating where the call occurred.
   * If the return value is not negative the value is returned to the calling process.
   *
   * @param o the object in which the method method name occurs
   * @param methodName String containing the name of the method to be called in object {@code o}
   * @param parameters as passed into the method, passed in a List
   * @param caller index of message in {@code callerMessages}
   * @return the method's return code
   */
  int callUserFunction ( Object o, String methodName, List parameters, int caller){
    int returnCode
    returnCode = o.&"$methodName" ( parameters)
    if ( returnCode < 0){
      unexpectedReturnCode (callerMessages[caller], methodName, parameters, returnCode)
      return -1
//			the system exits on error
    }
    else
      return returnCode
  }



  /**
   * Invokes a call of the method named {@code methodName} in the Object {@code o} which always
   * returns a value.  If the return value is negative, the complete process network will terminate
   * with an appropriate message indicating where the call occurred.  If the return value is not
   * negative then the method returns to the calling process, without returning the value of return code.
   *
   * @param o the object in which the method method name occurs
   * @param methodName String containing the name of the method to be called in object {@code o}
   * @param parameters as passed into the method, passed in a List
   * @param caller index of message in {@code callerMessages}
   */
  void callUserMethod ( Object o, String methodName, List parameters, int caller){
    int returnCode
    returnCode = o.&"$methodName" ( parameters)
    if ( returnCode < 0){
      unexpectedReturnCode (callerMessages[caller], methodName, parameters, returnCode)
//			the system exits on error
    }
  }

  /**
   * Invokes a call of the method named {@code methodName} in the Object {@code o} which always
   * returns a value.  If the return value is negative, the complete process network will terminate
   * with an appropriate message indicating where the call occurred.  If the return value is not
   * negative then the method returns to the calling process, without returning the value of return code.
   *
   * @param o the object in which the method method name occurs
   * @param methodName String containing the name of the method to be called in object {@code o}
   * @param caller index of message in {@code callerMessages}
   */
  void callUserMethod ( Object o, String methodName, int caller){
    int returnCode
    returnCode = o.&"$methodName" ( )
    if ( returnCode < 0){
      unexpectedReturnCode (callerMessages[caller], methodName, null, returnCode)
//			the system exits on error
    }
  }
  /**
   * Invokes a call of the method named {@code methodName} in the Object {@code o} which always
   * returns a value.  If the return value is negative, the complete process network will terminate
   * with an appropriate message indicating where the call occurred.  If the return value is not
   * negative then the method reurns to the calling process, without returning the value of return code.
   *
   * @param o the object in which the method method name occurs
   * @param methodName String containing the name of the method to be called in object {@code o}
   * @param parameters as passed into the method, passed in as an Object
   * @param caller index of message in {@code callerMessages}
   */
  void callUserMethod ( Object o, String methodName, Object parameter, int caller){
    int returnCode
    returnCode = o.&"$methodName" ( parameter)
    if ( returnCode < 0){
      unexpectedReturnCode (callerMessages[caller], methodName, [parameter.toString()], returnCode)
//			the system exits on error
    }
  }

  /**
   *
   * called by any process that receives an error code as the return value
   * from a call to the invoke method.
   * It causes the whole process network to terminate.
   *
   * @param component The name of the process creating the error
   * @param methodName String containing the name of the method to be called
   * @param parameters as passed into the method
   * @param errCode The negative error code that caused the call to the method
   */
  static void unexpectedReturnCode ( String component,
                                     String methodName,
                                     List parameters,
                                     int errCode){
    println "Unexpected Error from $component value $errCode"
    println "Calling $methodName with parameters $parameters"
    System.exit(errCode)
  }

  /**
   *
   * called by any process that receives an error code as the return value
   * from a call to the invoke method.
   * It causes the whole process network to terminate.
   *
   * @param component The name of the process creating the error
   * @param errCode The negative error code that caused the call to the method
   */
  static void unexpectedReturnCode ( String component,
                                     int errCode){
    println "Unexpected Error from $component value $errCode"
    System.exit(errCode)
  }



  /**
   * A null implementation of {@link groovy_parallel_patterns.DataClassInterface#clone()}<p>
   * Any clone method MUST ensure that a deep copy is created rather then the default shallow copy.
   * @return null is returned by default
   */
  @Override
  public Object clone() {
    println " method clone() needs to be implemented - see DataClass"
    return null
  }

// removed in version 1.0.9
//  /**
//   * A null implementation of {@link groovy_parallel_patterns.DataClassInterface#serialize}
//   *
//   * @usage o.Serialize(), where o is the non-Serializable object
//   * @return the Serializable version of o
//   */
//  @Override
//  public Object serialize() {
//    println " method serialize() needs to be implemented - see DataClass"
//    return null;
//  }
}
