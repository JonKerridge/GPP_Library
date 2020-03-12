package groovyParallelPatterns

import groovy.transform.CompileStatic
import jcsp.lang.ChannelOutput

/**
 * The Logger class provides a number of static methods that are used internally,
 * within other processes, that cause the recording of timing
 * data pertaining to the input and output communications associated with that process.
 *
 * Processes can be optionally logged simply by specifying a string property logPhaseName and the
 * associated name of a property within the object being processed that uniquely identifies the object instance.
 *
 * The log data is output as part of a Collect process.
 * The logFileName property must be specified and the log data will be written to a file as text values,
 * simply as the content of each tagged log message, comma separated, each on a separate line.
 * Time values are output as the long representation of system millisecond time. <p>
 * The property logChan holds a channel that can be used to output log data directly to a
 * LoggingVisualiser process.  This channel is created automatically by GPP_Builder.
 *
 * The user does not have to call any of the methods as this is done during the act of logging a process.
 */
@CompileStatic
class Logger implements Cloneable, Serializable {
    static int startTag = 0
    static int initTag = 1
    //
    static int inputReadyTag = 2
    static int inputCompleteTag = 3
    //
    static int outputReadyTag = 4
    static int outputCompleteTag = 5
    //
    static int endTag = 6
    static int workStartTag = 7
    static int workEndTag = 8
    static ChannelOutput logChan = null

/**
 * initLogChannel is used to initialise the logging channel, if used. Invoked by GPP_Builder
 * @param logChan the any2one logging channel output connecting processes to the LoggingVisualiser
 */
    static void initLogChannel(ChannelOutput loggingChan) {
        logChan = loggingChan
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void startLog(String logID, long time) {
        logChan.write([time, startTag, logID, " "])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void initLog(String logID, long time) {
        logChan.write([time, initTag, logID, " "])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 * @param o the property value being tracked
 */
    /////////
    static void inputReadyEvent(String logID, long time) {
        logChan.write([time, inputReadyTag, logID, " "])
    }

    static void inputCompleteEvent(String logID, long time, Object o) {
        logChan.write([time, inputCompleteTag, logID, o])
    }
    /////////

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 * @param o the property value being tracked
 */
    static void outputReadyEvent(String logID, long time, Object o) {
        logChan.write([time, outputReadyTag, logID, o])
    }

    static void outputCompleteEvent(String logID, long time, Object o) {
        logChan.write([time, outputCompleteTag, logID, o])
    }
    /////////

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void endEvent(String logID, long time) {
        logChan.write([time, endTag, logID, " "])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void workStartEvent(String logID, long time) {
        logChan.write([time, workStartTag, logID, " "])
    }

/**
 *
 * @param logID the name of the log phase
 * @param time millisecond time tag generated
 */

    static void workEndEvent(String logID, long time) {
        logChan.write([time, workEndTag, logID, " "])
    }


}