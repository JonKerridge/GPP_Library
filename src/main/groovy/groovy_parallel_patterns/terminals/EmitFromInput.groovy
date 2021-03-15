package groovy_parallel_patterns.terminals

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
 * EmitFromInput reads ONE object of type {@code eDetails.lName} from its input channel; it then uses the
 * initClass method to set any static variables of the class that are as yet not initialised and then
 * the createInstance method is then called repeatedly to create new instances of the class.
 * Each object is written to the output channel. Once all the required object instances
 * have been created the process writes a {@link groovy_parallel_patterns.UniversalTerminator} to the output channel.
 * <p>
 * Methods required by class:
 * eDetails.lInitMethod(eDetails.lInitData)
 * eDetails.lCreateInstance(eDetails.lCreateData)
 *
 * <p>
 * @param input The one2one channel from which the base class is read.
 * @param output The one2one channel to which new object instances are written
 * @param eDetails A {@link groovy_parallel_patterns.LocalDetails} object that specifies the data class to be emitted
 * <p>
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 *
 */


class EmitFromInput extends DataClass implements CSProcess {
  ChannelInput input
  ChannelOutput output
  LocalDetails eDetails

  String logPhaseName = ""
  String logPropertyName = ""

  @CompileStatic
  void runMethod() {
    int returnCode
    Class LocalClass = Class.forName(eDetails.lName)
    Object lcInit = LocalClass.newInstance()
    def lcBase = input.read()
    assert (lcBase.getClass().isInstance(lcInit)): "EmitFromInput: input Class not ${eDetails.lName}"
    callUserMethod(lcInit, eDetails.lInitMethod, eDetails.lInitData, 21)
//        lcInit.&"${eDetails.lInitMethod}"(eDetails.lInitData)
    boolean running = true
    while (running) {
      Object lc = LocalClass.newInstance()
      returnCode = callUserFunction(lc, eDetails.lCreateMethod, [lcBase, eDetails.lCreateData], 15)
      if (returnCode == normalContinuation) {
        output.write(lc)
      } else running = false
    }
    UniversalTerminator ut = (UniversalTerminator) input.read()
    // terminator from previous process
    output.write(ut)

  }

  void run() {
    if (logPhaseName == "") {
      runMethod()
    } else {
      assert logPropertyName != "": "EmitFromInput is logged so logPropertyName must be specified"
      def timer = new CSTimer()

      Logger.startLog(logPhaseName, timer.read())

      int returnCode = -1
      Class LocalClass = Class.forName(eDetails.lName)
      Object lcInit = LocalClass.newInstance()
      def lcBase = input.read()
      assert (lcBase.getClass().isInstance(lcInit)): "EmitFromInput: input Class not ${eDetails.lName}"

      callUserMethod(lcInit, eDetails.lInitMethod, eDetails.lInitData, 21)
//            lcInit.&"${eDetails.lInitMethod}"(eDetails.lInitData)
      boolean running = true

      Logger.initLog(logPhaseName, timer.read())

      while (running) {
        Object lc = LocalClass.newInstance()
        returnCode = callUserFunction(lc, eDetails.lCreateMethod, [lcBase, eDetails.lCreateData], 15)
        if (returnCode == normalContinuation) {
          //////
          Logger.outputReadyEvent(logPhaseName, timer.read(), lc.getProperty(logPropertyName))
          //////

          output.write(lc)

          //////
          Logger.outputCompleteEvent(logPhaseName, timer.read(), lc.getProperty(logPropertyName))
          //////
        } else running = false
      }
      Logger.endEvent(logPhaseName, timer.read())
      UniversalTerminator ut = input.read()  // terminator from previous process
      output.write(ut)
    }
  }

}
