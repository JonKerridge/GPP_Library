package groovyParallelPatterns.functionals.matrix

import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

@CompileStatic
class StencilManager implements CSProcess {

    ChannelInput input
    ChannelOutput output
    ChannelOutputList toNodesList
    ChannelInput fromNodes
    int nodes
    String partitionMethod = ""
    String updateImageIndexMethod = ""

    String logPhaseName = ""
    String logPropertyName = ""

    void run(){
        boolean running
        running = true
        Object inputObject
        inputObject = input.read()
        while (running){
            if ( inputObject instanceof UniversalTerminator){
                running = false
            }
            else {
                // create object partitions
                if (partitionMethod != "") inputObject.&"$partitionMethod"(nodes)
                // send object to each node
                for ( n in 0 ..< nodes) {
                    ((ChannelOutput) toNodesList[n]).write(inputObject)
                }
                // read done signal from each node
                for ( n in 0 ..< nodes) {
                    fromNodes.read()
                }
                if ( updateImageIndexMethod != "") inputObject.&"$updateImageIndexMethod"()
                output.write(inputObject)
                inputObject = input.read()
            }
        } // while
        // send Universal terminator to each node
        for ( n in 0 ..< nodes)
            ((ChannelOutput) toNodesList[n]).write(new UniversalTerminator())
        // read done signal from each node
        for ( n in 0 ..< nodes) fromNodes.read()
        output.write(inputObject)
    }
}
