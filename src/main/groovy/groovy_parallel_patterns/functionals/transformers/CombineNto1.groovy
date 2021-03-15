package groovy_parallel_patterns.functionals.transformers

import groovy.transform.CompileStatic
import groovy_parallel_patterns.DataClass
import groovy_parallel_patterns.LocalDetails
import groovy_parallel_patterns.Logger
import groovy_parallel_patterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.CSTimer
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 * CombineNto1 takes any number of input data objects and then combines them into a single
 * output data class.  Intermediate values are formed in a local class. Only a single
 * instance of the local class is created.
 * No modifications are performed on the input data objects, they are only read.
 *
 * Methods required by inputClass none<p>
 * Methods required by localClass:<br>
 * initClass(localInitData)<br>
 * combineMethod (inputClass): operation to transfer data from input class to local class<p>
 * Methods required by outputClass:<br>
 * initClass(outputInitData)<br>
 * finalise (localClass) : copies data from local class into output class
 *
 * <p>
 * @param input A one2one channel from which input data objects are read.
 * @param output A one2one channel to which the final single data output object is written
 * @param localDetails A {@link groovy_parallel_patterns.LocalDetails} object that specifies the details of a local class
 * @param outDetails A {@link groovy_parallel_patterns.LocalDetails} object that defines the single output object that results from this process.
 * @param combineMethod A String specifying the name of the operation to be undertaken that combine input data objects into the local worker class
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param inputLogPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * inputLogPropertyName must be specified if logPhaseName is specified
 * @param outputLogPropertyName the name of a property in the output object that will uniquely identify an instance of the object.
 * outputLogPropertyName must be specified if logPhaseName is specified
 *
 *  log properties modified and added in version 1.0.0-alpha-4
 *
 *
 * <p>
 *
 */

class CombineNto1 extends DataClass implements CSProcess {
  ChannelInput input
  ChannelOutput output
  LocalDetails localDetails = null
  LocalDetails outDetails = null
  String combineMethod = ""
//	List dataModifier = null		// is this required???

  String logPhaseName = ""
  String inputLogPropertyName = ""
  // two property names required to refer to input and output objects
  String outputLogPropertyName = ""

  @CompileStatic
  void runMethod() {
    int returnCode
    Class lClass = Class.forName(localDetails.lName)
    def localClass = lClass.newInstance()
    callUserMethod(localClass, localDetails.lInitMethod, localDetails.lInitData, 16)

    Class oClass = Class.forName(outDetails.lName)
    def outputObject = oClass.newInstance()
    callUserMethod(outputObject, outDetails.lInitMethod, outDetails.lInitData, 17)

    boolean running
    running = true
    Object inputObject
    inputObject = new Object()
    while (running) {
      inputObject = input.read()
      if (inputObject instanceof UniversalTerminator) {
        running = false
      } else {
        callUserMethod(localClass, combineMethod, inputObject, 18)
        // does this need data modifier as well???? if so
      }
    }
    callUserMethod(outputObject, outDetails.lFinaliseMethod, [localClass], 19)
    output.write(outputObject)
    output.write(inputObject)   // the Universal Terminator previously read
  }

  void run() {
    assert localDetails.lName != null: "CombineNto1: A local class MUST be defined"
    assert outDetails.lName != null: "CombineNto1: An output class MUST be defined"
    assert combineMethod != "": "CombineNto1: combine method must be defined"
    if (logPhaseName == "") {
      runMethod()
    } else { // logging
      assert inputLogPropertyName != "": "CombineNto1 is logged so inputLogPropertyName must be specified"
      assert outputLogPropertyName != "": "CombineNto1 is logged so outputLogPropertyName must be specified"
      def timer = new CSTimer()

      Logger.startLog(logPhaseName, timer.read())

      int returnCode
      Class lClass = Class.forName(localDetails.lName)
      def localClass = lClass.newInstance()
      callUserMethod(localClass, localDetails.lInitMethod, localDetails.lInitData, 16)

      Class oClass = Class.forName(outDetails.lName)
      def outputObject = oClass.newInstance()
      callUserMethod(outputObject, outDetails.lInitMethod, outDetails.lInitData, 17)

      boolean running
      running = true
      Object inputObject
      inputObject = new Object()
      Logger.initLog(logPhaseName, timer.read())

      while (running) {

        ////////
        Logger.inputReadyEvent(logPhaseName, timer.read())
        ////////

        inputObject = input.read()
        if (inputObject instanceof UniversalTerminator) {
          running = false
        } else {
          ////////
          Logger.inputCompleteEvent(logPhaseName, timer.read(), inputObject.getProperty(inputLogPropertyName))
          ////////

          callUserMethod(localClass, combineMethod, inputObject, 18)
          // does this need data modifier as well???? if so
        }
      }
      callUserMethod(outputObject, outDetails.lFinaliseMethod, [localClass], 19)

      //////
      Logger.outputReadyEvent(logPhaseName, timer.read(), outputObject.getProperty(outputLogPropertyName))
      //////

      output.write(outputObject)

      ////////
      Logger.outputCompleteEvent(logPhaseName, timer.read(), outputObject.getProperty(outputLogPropertyName))
      ////////

      // now write the terminating UT that was read previously with log data appended
      Logger.endEvent(logPhaseName, timer.read())

      output.write(inputObject)
    }
  }

}
