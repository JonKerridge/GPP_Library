package groovyParallelPatterns.functionals.matrix

import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 * StencilEngine provides a means of processing stencil kernel operations and applying them to
 * matrix transformations by means of kernel operations.  The process has been designed so that the
 * output from a StencilEngine can form the input to another so that a sequence of transformations
 * can be undertaken on a sequence of sources.<p>
 * Though primarily designed for image processing the engine can also be used for any file based
 * input  where the data is processed once and then output, possibly to another StencilEngine for
 * subsequent processing where the transformation output does not have to be written to file
 * between each transformation but is saved in a memory based object. A reference to the object
 * is passed between StencilEngines.<p>
 * The StencilEngine comprises, internally, an StencilManager process and a number of
 * StencilNode processes that each carry out the required operation on a partition of the
 * complete matrix.  Each partition must access a distinct part of the total data structure.
 * Each partition will be processed by one of the StencilNode processes.
 * The number of StencilNodes equals the number of partitions. The architecture assumes
 * the data structure is double buffered in that a transformation reads from one buffer to the
 * other buffer.  The way in which the kernel  operation is carried out is left to the programmer
 * as that is part of the sequential code that has to be written.  In addition to convolutions
 * the engine can carry out a  scalar operation such as convert to grey scale; these are defined
 * by a function.  A StencilEngine can carry out a function or a convolution but not both,
 * hence the ability to chain StencilEngines together.<p>
 *
 * @param input The channel used to read the object containing the image
 * @param output The channel used to write the object after transformation
 * @param nodes The number of StencilNode processes
 * @param partitionMethod The name of the method that is used to partition the image.
 * The partitioning structure is saved within the image object.
 * @param convolutionMethod The name of the method that undertakes the convolution operation
 * @param convolutionData Any further data required by the convolution method; typically the kernel
 * and other kernel parameters that are required.
 * @param funtionMethod The name of a function that carries out a scalar operation on the image.
 * For an instance of a StencilEngine only ONE of ConvolutionMethod and FunctionMethod
 * must be specified.
 * @param functionData Any further data required by the function
 * @param updateImageIndexMethod The name of the method used to modify the index of the
 * current buffer holding the transformed image. This means that internal buffering methods
 * can be utilised that require more than double buffering.  The method is only called if an
 * instance of StencilEngine specifies the method name;  * it defaults to the empty string.
 * An Image object may have more than one updateImageIndexMethod
 * depending on the application, but only one can be called by a specific StencilEngine instance.
 *
 * @param logPropertyName the name of a property in the matrix object that will uniquely identify
 * an instance of the object.  LogPropertyName must be specified if logging is required.
 * The names associated with the log phases are generated internally.
 *
 *
 */

@CompileStatic
class StencilEngine implements CSProcess {

    ChannelInput input
    ChannelOutput output
    int nodes = 0                     // number of StencilNode processes
    String partitionMethod = ""     // name of partition method
    String convolutionMethod = ""   // name of convolution method
    List convolutionData = null     // List of parameters required by convolution method
    String functionMethod = ""      // name of any universally applied function
    List functionData = null        // List of parameters for function
    String updateImageIndexMethod = "" // name of method used to update index of double buffer

    //String logPhaseName = "" set internally
    String logPropertyName = ""

    void run(){
        assert nodes > 0 : "number of StencilEngine nodes must be greater than 0 : $nodes"
        assert (convolutionMethod == "") && (functionMethod != "") ||
            (convolutionMethod != "") && (functionMethod == ""):
            "Only one of $convolutionMethod or $functionMethod can be specified"
        def toNodes = Channel.one2oneArray(nodes)
        def toNodesList = new ChannelOutputList(toNodes)
        def fromNodes = Channel.any2one()
        def network = []
        for ( n in 0 ..< nodes){
            network << new StencilNode( input: toNodes[n].in(),
                                      output: fromNodes.out(),
                                      nodeId: n,
                                      convolutionMethod: convolutionMethod,
                                      convolutionData: convolutionData,
                                      functionMethod: functionMethod,
                                      functionData: functionData,
                                      logPhaseName: logPropertyName == "" ? "" : (String)"$n, node" ,
                                      logPropertyName: logPropertyName)
        }
        network << new StencilManager( input: input,
                                     output: output,
                                     toNodesList: toNodesList,
                                     fromNodes: fromNodes.in(),
                                     nodes: nodes,
                                     partitionMethod: partitionMethod,
                                     logPhaseName: "manager",
                                     logPropertyName: logPropertyName,
                                     updateImageIndexMethod: updateImageIndexMethod)
        new PAR(network).run()
    }
}
