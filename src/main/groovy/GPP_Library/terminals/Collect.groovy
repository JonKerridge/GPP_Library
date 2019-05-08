package GPP_Library.terminals

import GPP_Library.*
import groovy.transform.CompileStatic
import jcsp.lang.*

/**
 * The Collect process collects results from the rest of the process network.
 * It reads an inputObject from the input channel which it then processes using
 * methods defined in the results class.<p>
 * <pre>
 * <b>Methods required by class resultClassName:</b>
 *     initClass( initialData )
 *     collector( inputObject )
 *     finalise( finaliseData )
 *
 * <b>Behaviour:</b>
 *     resultsClass.initClass(initData)
 *     o = input.read()
 *     while ( o != UniversalTerminator )
 *         resultClass.collector(o)
 *         o = input.read()
 *     resultsClass.finalise(finaliseData)
 * </pre>
 * 	<p>
 * @param input The one2one input channel used to receive results
 * @param rDetails A ResultDetails object containing data pertaining to result class used by the Collect process, it MUST be specified.
 * @param logPhaseName an optional string property, which if specified indicates that the process should be logged
 * otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 *
 * @param visLogChan the output end of an any2one channel to which log data will be sent to an instance of the LoggingVisualiser
 * process running in parallel with the application network.  If not specified then it is assumed that no visualiser process is running.
 *
 *
 *
*/

class Collect extends DataClass implements CSProcess {

	ChannelInput input
	ResultDetails rDetails
	int collected = 0

    String logPhaseName = ""
    String logPropertyName = ""
    ChannelOutput visLogChan = null
    Object inputObject = null


    @CompileStatic
    void runMethod() {
        Class resultsClass = Class.forName(rDetails.rName)
        def rc = resultsClass.newInstance()
        int returnCode //= -1
        callUserMethod(rc, rDetails.rInitMethod, rDetails.rInitData, 5 )
        inputObject = input.read()
        while (!(inputObject instanceof UniversalTerminator)){
            collected += 1
            callUserMethod(rc, rDetails.rCollectMethod, inputObject, 6)
            inputObject = input.read()
        }
        callUserMethod(rc, rDetails.rFinaliseMethod, rDetails.rFinaliseData, 7)
	}

   	void run(){
        if (logPhaseName == "")
            runMethod()
        else {
            assert visLogChan != null :"Collector is logged so visLogChan must not be null"
            assert logPropertyName != "" : "Collector is logged so logPropertyName must not be null"
            def timer = new CSTimer()
            Logger.startLog(logPhaseName, timer.read())
            Class resultsClass = Class.forName(rDetails.rName)
            def rc = resultsClass.newInstance()
            int returnCode
            callUserMethod(rc, rDetails.rInitMethod, rDetails.rInitData, 5 )
            Logger.initLog(logPhaseName, timer.read())

            //////
            Logger.inputReadyEvent(logPhaseName, timer.read())
            //////

            inputObject = input.read()
            while (!(inputObject instanceof UniversalTerminator)){

                //////
                Logger.inputCompleteEvent(logPhaseName, timer.read(), inputObject.getProperty(logPropertyName))
                //////

                collected += 1
                callUserMethod(rc, rDetails.rCollectMethod, inputObject, 6)
                inputObject = input.read()

                //////
                Logger.inputReadyEvent(logPhaseName, timer.read())
                //////

            }
            Logger.workStartEvent(logPhaseName, timer.read())
            callUserMethod(rc, rDetails.rFinaliseMethod, rDetails.rFinaliseData, 7)
            Logger.workEndEvent(logPhaseName, timer.read())
            Logger.endEvent(logPhaseName, timer.read())
            visLogChan.write(new UniversalTerminator())
        }
	}

}
