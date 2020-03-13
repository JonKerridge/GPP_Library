package groovyParallelPatterns.functionals.matrix

import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.*

@CompileStatic
class StencilNode implements CSProcess {

    ChannelInput input
    ChannelOutput output
    int nodeId
    String convolutionMethod = ""
    List convolutionData = null
    String functionMethod = ""
    List functionData = null

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void run(){
        if (functionData == null) functionData = []
        if (convolutionData == null) convolutionData = []
        boolean running
        running = true
        Object inputObject
        inputObject = input.read()
        while (running){
            if ( inputObject instanceof UniversalTerminator)
                running = false
            else {
                if (functionMethod == ""){
                    // doing a convolution
                    def parameterList = [nodeId] + convolutionData
//                    println "Manager-$id node $nodeId calling $convolutionMethod $parameterList"
                    inputObject.&"$convolutionMethod"(parameterList)
                }
                else {
                    def parameterList = [nodeId] + functionData
//                    println "Manager-$id node $nodeId calling $functionMethod $parameterList"
                    inputObject.&"$functionMethod"(parameterList)
                }
//                println "Manager-$id node $nodeId returning signal"
                output.write(new UniversalSignal())
                inputObject = input.read()
            }
        } // running
//        println "Manager-$id node $nodeId  has read terminator"
        output.write(inputObject) // the UT previously read
//        println "Manager-$id node $nodeId  has written terminator back to Manager"
    }
}
