package GPP_Library.functionals.matrix


import GPP_Library.Logger
import GPP_Library.UniversalSeparator
import GPP_Library.UniversalSignal
import GPP_Library.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

class MultiCoreRoot implements CSProcess {

  ChannelInput input
  ChannelOutput output
  ChannelOutputList toNodes
  ChannelInput fromNodes
  int nodes = 0
  int iterations = 0
  double errorMargin = 0.0
  boolean finalOut = true
  String partitionMethod = ""
  String errorMethod = ""
  String updateMethod = ""

  String logPhaseName = ""
  String logPropertyName = ""

  @CompileStatic
  void runMethod() {
    boolean running
    running = true
    Object data
    data = new Object()
    while (running) {
      data = input.read()
      if ( data instanceof UniversalTerminator)
        running = false
      else {  // process a new data set
        data.&"$partitionMethod"(nodes)
        for ( i in 0 ..< nodes) ((ChannelOutput)toNodes[i]).write(data)
//                    println "sent data reference to nodes"
        for ( i in 0 ..< nodes) fromNodes.read()
//                    println "received data acknowledgements"
        if (iterations != 0){
          for ( j in 0..< iterations) {
            for ( k in 0..< nodes) ((ChannelOutput)toNodes[k]).write(new UniversalSignal())
            for ( k in 0..< nodes) fromNodes.read()
            data.&"$updateMethod"()
            if ( !finalOut) output.write(data)
          }
        } // end iterations loop
        else { // looping until errorMargin is satisfed
//                        println "error looping"
          iterations = 0
          boolean iterating = true
          while (iterating) {
            iterations += 1
            for ( i in 0 ..< nodes) ((ChannelOutput)toNodes[i]).write(new UniversalSignal())
//                            println "sent do calculation to nodes"
            for ( i in 0 ..< nodes) fromNodes.read()
//                            println "received calculation acknowledgements"
            iterating = data.&"$errorMethod"(errorMargin)
            data.&"$updateMethod"()
//                            println " done the update and iterating = $iterating after $iterations iterations"
            if ( !finalOut) output.write(data)
          }
        } // iterate or loop until differences less than errorMargin
        // send final result
//                    println "result ${data.M.getByColumn(data.n + 1)}"
        output.write(data)
        // now send separator to Nodes
        for ( i in 0 ..< nodes) ((ChannelOutput)toNodes[i]).write(new UniversalSeparator())
//                    println "sent USep to nodes"
        for ( i in 0 ..< nodes) fromNodes.read()
//                    println "received USep acknowledgements"
      } // processed data set
    } // running
    // deal with termination; first the nodes
    for ( i in 0 ..< nodes) ((ChannelOutput)toNodes[i]).write(new UniversalTerminator())
//            println "sent UT to nodes"
    for ( i in 0 ..< nodes) fromNodes.read() // get signals to indicate Node termination
//            println "received UT acknowledgements"
    output.write(data)  // data contains a UniversalTerminator
  } // run method

  void run(){
    assert partitionMethod != "" : "MultiCoreRoot: partitionMethod must be specified"
    assert updateMethod != "" : "MultiCoreRoot: updateMethod must be specified"

    boolean running
    running = true
    Object data
    data = new Object()

    if (logPropertyName == "") { //not logging
      runMethod()
    } // not logging

    else { // logging
      def timer = new CSTimer()
      Logger.startLog(logPhaseName, timer.read())
      Logger.initLog(logPhaseName, timer.read())

      while (running) {
        Logger.inputReadyEvent(logPhaseName, timer.read())
        data = input.read()
        if (data instanceof UniversalTerminator) {
          running = false
          Logger.inputCompleteEvent(logPhaseName, timer.read(), "UT")
        }
        else {  // process a new data set
          Logger.inputCompleteEvent(logPhaseName, timer.read(), data.getProperty(logPropertyName))
          data.&"$partitionMethod"(nodes)
          Logger.workStartEvent(logPhaseName, timer.read())
          for ( i in 0 ..< nodes) toNodes[i].write(data)
//                    println "sent data to nodes"
          for ( i in 0 ..< nodes) fromNodes.read()
//                    println "received data acknowledgements"
          if (iterations != 0){
            for ( j in 0..< iterations) {
              for ( k in 0..< nodes) toNodes[k].write(new UniversalSignal())
              for ( k in 0..< nodes) fromNodes.read()
              data.&"$updateMethod"()
              if ( !finalOut) output.write(data)
            }
          } // end iterations loop
          else { // looping until errorMargin is satisfed
//                        println "error looping"
            iterations = 0
            boolean iterating
            iterating = true
            while (iterating) {
              iterations += 1
              for ( i in 0 ..< nodes) toNodes[i].write(new UniversalSignal())
//                            println "sent do calculation to nodes"
              for ( i in 0 ..< nodes) fromNodes.read()
//                            println "received calculation acknowledgements"
              iterating = data.&"$errorMethod"(errorMargin)
              data.&"$updateMethod"()
//                            println " done the update and iterating = $iterating after $iterations iterations"
              if ( !finalOut) {
//                Logger.outputReadyEvent(logPhaseName, timer.read(), data.getProperty(logPropertyName))
                output.write(data)
//                Logger.outputCompleteEvent(logPhaseName, timer.read(), data.getProperty(logPropertyName))
              }
            }
          } // iterate or loop until differences less than errorMargin
          Logger.workEndEvent(logPhaseName, timer.read())
          // send final result
//                    println "result ${data.M.getByColumn(data.n + 1)}"
          Logger.outputReadyEvent(logPhaseName, timer.read(), data.getProperty(logPropertyName))
          output.write(data)
          // now send separator to Nodes
          for ( i in 0 ..< nodes) toNodes[i].write(new UniversalSeparator())
//                    println "sent USep to nodes"
          for ( i in 0 ..< nodes) fromNodes.read()
          Logger.outputCompleteEvent(logPhaseName, timer.read(), data.getProperty(logPropertyName))
//                    println "received USep acknowledgements"
        } // processed data set
      } // running
      // deal with termination; first the nodes
      for ( i in 0 ..< nodes) toNodes[i].write(new UniversalTerminator())
      for ( i in 0 ..< nodes) {
        def ended = fromNodes.read()
      }
      output.write(data)  // data contains a UniversalTerminator
      Logger.endEvent(logPhaseName, timer.read())
    } // logging
  }// run

}
