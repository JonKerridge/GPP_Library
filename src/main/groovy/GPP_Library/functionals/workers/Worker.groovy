package GPP_Library.functionals.workers

import GPP_Library.DataClass
import GPP_Library.LocalDetails
import GPP_Library.Logger
import GPP_Library.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * Worker is a fundamental process that reads an inputObject, processes it and then writes
 * the processed object to the next process in the process network.  A Worker process
 * may have a local workerClass instance of type workerClassName that stores intermediate results.<p> *
 * The process has two parameters that govern its operation; outData and barrier
 * <p>
 * On termination of the process it outputs a UniversalTerminator object.<p>
 *
 * Methods required by inputObject: <br>
 * function( [dataModifier, wc] ) where wc is the local, possibly null, worker class instance<p>
 *
 * Methods required by workerClass, if present: <br>
 * initClass(workerInitData)<p>
 * finalise(finaliseData)<p>
 *
 * @param input			The channel from which the input object to be processed is read
 * @param output		The channel to which the processed object is written
 * @param function		The name of the method corresponding to the method in the data object that is to be employed; the method should return either
 * {@link GPP_Library.DataClassInterface#completedOK} or a negative error code
 * @param dataModifier	A list of any values to be used by the function method; it is polymorphic in type
 * 						and defaults to null
 * @param lDetails A LocalDetails object containing data pertaining to any local class used by the worker, defaults to null.
 * @param outData If true the process will output each processed input object. If false the process will output
 * 				  the workerClass once only, after it has processed all the input data objects. The output
 *                only happens after the finalise method has been called. outData defaults to true.
 * @param barrier If not null the worker process will synchronise on a Barrier with other Worker processes in the same Group;
 * 			   		barrier defaults to null, in which case no synchronisation between other Workers will take place.
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 *
 * @see jcsp.lang.Barrier
 */


class Worker extends DataClass implements CSProcess {

  ChannelInput input
  ChannelOutput output
  String function
  List dataModifier = null
  LocalDetails lDetails = null
  boolean outData = true
  Barrier barrier = null

  String logPhaseName = ""
  String logPropertyName = ""

  @CompileStatic
  void runMethod() {
    int returnCode
    int workerType
    workerType = 0
    if (outData) {
      if (barrier == null) workerType = 1
      else workerType = 2
    }
    else {
      if (barrier == null) workerType = 3
      else workerType = 4
    }
    def wc
    wc = null
    if ( lDetails != null){
      Class workerClass = Class.forName(lDetails.lName)
      wc = workerClass.newInstance()
      callUserMethod(wc, lDetails.lInitMethod, lDetails.lInitData, 0)
    }
    boolean running
    running = true
    Object inputObject
    inputObject = new Object()

    while (running){
      inputObject = input.read()
      if ( inputObject instanceof UniversalTerminator){
        running = false
      }
      else {
//        println "Worker calling $function on ${inputObject.toString()}"
        callUserMethod(inputObject, function, [dataModifier, wc], 1)
        switch (workerType) {
          case 1:
            output.write(inputObject)
            break
          case 2:
            barrier.sync()
            output.write(inputObject)
            break
          default:
            break
        }
      }
    } // end of running loop
    if ((workerType == 3) || (workerType == 4)) {
      callUserMethod(wc, lDetails.lFinaliseMethod, lDetails.lFinaliseData, 2)
      switch (workerType) {
        case 3:
          output.write(wc)
          break
        case 4:
          barrier.sync()
          output.write(wc)
          break
        default:
          break
      }
    }
    // the inputObject is a UT
    output.write(inputObject)
//    println "Worker has terminated"
  } //runMethod

  void run(){
    if (logPhaseName == "")
      runMethod()
    else {  // getProperty() of this code cannot be compiled statically
      def timer = new CSTimer()

      Logger.startLog(logPhaseName, timer.read())
      int returnCode
      int workerType
      workerType = 0
      if (outData) {
        if (barrier == null) workerType = 1
        else workerType = 2
      }
      else {
        if (barrier == null) workerType = 3
        else workerType = 4
      }
      def wc
      wc = null
      if ( lDetails != null){
        Class workerClass = Class.forName(lDetails.lName)
        wc = workerClass.newInstance()
        callUserMethod(wc, lDetails.lInitMethod, lDetails.lInitData, 0)
      }
      boolean running
      running = true
      Object inputObject
      inputObject = new Object()

      Logger.initLog(logPhaseName, timer.read())

      while (running){

        //////
        Logger.inputReadyEvent(logPhaseName, timer.read())
        //////

        inputObject = input.read()
        if ( inputObject instanceof UniversalTerminator){
          running = false
        }
        else {

          //////
          Logger.inputCompleteEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
          //////

          callUserMethod(inputObject, function, [dataModifier, wc], 1)
          switch (workerType) {
            case 1:

              //////
              Logger.outputReadyEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
              //////

              output.write(inputObject)

              //////
              Logger.outputCompleteEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
              //////

              break
            case 2:
              barrier.sync()

              //////
              Logger.outputReadyEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
              //////

              output.write(inputObject)

              //////
              Logger.outputCompleteEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
              //////

              break
            default:
              break
          }
        }
      } // end of running loop
      if ((workerType == 3) || (workerType == 4)) {
        callUserMethod(wc, lDetails.lFinaliseMethod, lDetails.lFinaliseData, 2)
        switch (workerType) {
          case 3:

            //////
            Logger.outputReadyEvent(logPhaseName, timer.read(), wc.getProperty(logPropertyName))
            //////

            output.write(wc)

            //////
            Logger.outputCompleteEvent(logPhaseName, timer.read(), wc.getProperty(logPropertyName))
            //////

            break
          case 4:
            barrier.sync()

            //////
            Logger.outputReadyEvent(logPhaseName, timer.read(), wc.getProperty(logPropertyName))
            //////

            output.write(wc)

            //////
            Logger.outputCompleteEvent(logPhaseName, timer.read(), wc.getProperty(logPropertyName))
            //////

            break
          default:
            break
        }
      }
      Logger.endEvent(logPhaseName, timer.read())

      output.write(inputObject)
    } // end of logged loop
  } // end run

}
