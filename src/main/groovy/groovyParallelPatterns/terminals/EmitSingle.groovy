package groovyParallelPatterns.terminals

import groovyParallelPatterns.DataClass
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.Logger
import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.CSProcess
import jcsp.lang.CSTimer
import jcsp.lang.ChannelOutput

/**
 *
 * The EmitSingle process is used to send ONE newly instanced data object of type {@code eDetails.dName} to the rest of the
 * parallel structure.  It sends the output data object to one output channel.
 * Once the single data object has been created the process terminates and
 * writes a UniversalTerminator object to the output channel. <p>
 *
 * <pre>
 * <b>Methods required by class emitClassName:</b>
 *     initClass( initialData )
 *     createInstance( createData )
 *
 * <b>Behaviour:</b>
 *     ec = emitClass.newInstance()
 *     ec.initClass(initialData)
 *     ec.createInstance(createData)
 *     output.write(ec)
 *     output.write(new UniversalTerminator)
 * </pre>
 * 	<p>
 * @param output The one2one channel to which data objects are written
 * @param eDetails A {@link groovyParallelPatterns.DataDetails}  object containing information concerning the DataClass
 * used by the Emit process
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the eDetails object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 */

class EmitSingle extends DataClass implements CSProcess {

    ChannelOutput output
    DataDetails eDetails

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        Class EmitClass = Class.forName(eDetails.dName)
        int returnCode
        Object ecInit = EmitClass.newInstance()
        callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 3)
        // this process only emits a single object that can be generated in either
        // init or create or both, once output the process outputs a UT object

        Object ec = EmitClass.newInstance()
        returnCode = callUserFunction(ec, eDetails.dCreateMethod, eDetails.dCreateData, 4)
        output.write(ec)
        output.write(new UniversalTerminator())
    }

    void run(){
        if (logPhaseName == "") {
            runMethod()
        }
        else {
            //logging required
			logPhaseName = "" + logPhaseName
            def timer = new CSTimer()

            Logger.startLog(logPhaseName, timer.read())
            Class EmitClass = Class.forName(eDetails.dName)
            boolean running = true
            int returnCode
            Object ecInit = EmitClass.newInstance()
            callUserMethod(ecInit, eDetails.dInitMethod, eDetails.dInitData, 3)

            Logger.initLog(logPhaseName, timer.read())

//            while (running){
                Object ec = EmitClass.newInstance()
                returnCode = callUserFunction(ec, eDetails.dCreateMethod, eDetails.dCreateData, 4)
//            if ( returnCode == normalContinuation)  {

                    //////
                    Logger.outputReadyEvent(logPhaseName, timer.read(), ec.getProperty(logPropertyName))
                    //////

                    output.write(ec)

                    //////
                    Logger.outputCompleteEvent(logPhaseName, timer.read(), ec.getProperty(logPropertyName))
                    //////
//                }
//                else
//                    running = false
//
//            }
            Logger.endEvent(logPhaseName, timer.read())
            output.write(new UniversalTerminator())
        }
    }

}
