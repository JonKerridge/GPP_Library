package groovyParallelPatterns


import groovyParallelPatterns.gppVis.Visualiser
import groovy.transform.CompileStatic
import javafx.application.Platform
import jcsp.lang.*

/**
 * Author: Jon Kerridge, augmented by Scott Hendrie
 * LoggingVisualiser is a JCSP process which receives logging events from other processes, appends the event to a log
 * file and communicates the nature of the event to the Visualier so the visual network can display the event.
 */

@CompileStatic
class LoggingVisualiser implements CSProcess {

    Visualiser gui
    ChannelInput logInput
    int collectors      // the number of parallel Collector processes in the network
    String logFileName  // the full name of the file with path to which the log data will be output

    final int TIME = 0,
              TAG = 1,
              GROUP_AND_PHASE = 2,
              PACKET = 3;

    void run() {
        assert logFileName != "": "LogFileName must be specified"
        def file = new File(logFileName + "log.csv")
        if (file.exists()) file.delete()
        def writer = file.newPrintWriter()
        boolean running
        int terminated
        terminated = 0
        running = true
        while (running) {
            def logEntry = logInput.read()
            if (logEntry instanceof UniversalTerminator)
                terminated += 1
            else {
                Platform.runLater(new Runnable() {
                    @Override
                    void run() {
                        gui.updateProcess("${((List) logEntry)[GROUP_AND_PHASE]}", Integer.parseInt("${((List) logEntry)[TAG]}"),
                                            "${((List) logEntry)[PACKET]}")
                    }
                })

                writer.println "${((List) logEntry)[TIME]}, " +
                        "${((List) logEntry)[TAG]}, " +
                        "${((List) logEntry)[GROUP_AND_PHASE]}, " +
                        "${((List) logEntry)[PACKET]}"
            }
            if (collectors == terminated) running = false
        }
        writer.flush()
        writer.close()
    }
}