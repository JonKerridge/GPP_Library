package GPP_Library.functionals.matrix

import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.lang.*

/**
 *
 * MultiCoreEngine creates a process network comprising a {@link GPP_Library.functionals.matrix.MultiCoreRoot} together with a
 * number of {@link GPP_Library.functionals.matrix.MultiCoreNode} processes. The goal of the complete process
 * network is to undertake operations on one or more large matrices by partitioning the matrix operations over the node
 * processes.  The matrix data structure is shared among all the processes, thus the data structure has to be partitioned
 * in such a way that every process can read all the elelemnts in the matrix but only one of the nodes can write to one of the
 * partitions.  The matrix has to have sufficient space to store intermediate results. At the ned of each iteration dat will
 * be transferred from the intermediate space to the original space for the next iteration.<p>
 * The processing can be organised as a fixed number of iterations or can be terminated when a termianl condition has been reached.
 * The object that contains the matrix will also require other structures such as a means of defining the partitions into
 * which the matrix is subdivided.<p>
 * The MultiCoreRoot process has been designed to read a stream of data objects comprising one or more matrices.
 * The structure of the input file is user defined.  An example can be found in gpp_demos.jacobi.  The Emit process will
 * access the matrix data structure in order to read the input file and then to create the data object. <p>
 * The MultiCoreEngine can also process arrays of objects which similarly have to be partitioned.  An example showing this
 * is given in gpp_demos.solarSystem and the equivalent matrix version is given in gpp_demos.nbody both of which solve
 * the same N-body problem.<p>
 * The methods required by the matrix data object are: <br>
 * initMethod (List d) called by Emit to initialise the data object, d contains any required parameteres<br>
 * createMethod (List d) called by Emit to populate the data object from the input file, d contains any required parameteres<br>
 * partitionMethod (nodes) creates the partition ranges based on the size of the matric and the number of nodes<br>
 * errorMethod (errorMargin) determines whether the end of iterations has been reached, returns boolean,
 * true if the iteration needs to be repeated, false otherwise<br>
 * calaculationMethod (nodeIndex) undertakes the required calculation on the partition identified by nodeIndex<br>
 * updateMethod () copies the latest update to the matrix into the normal space<br>
 *
 *@param input The channel from which input objects are read
 *@param output The channel to which processed objects are written
 *@param nodes The number of the MultiCoreNode processes in the network
 *@param iterations The number of times the process will iterate
 *@param errorMargin The value used to determine whether the termination condition has been reached.
 *Only one of iterations or errorMargin may be specified.
 *@param finalOut If true only the final processed object is written to output.  If false the current state
 *of each iteration of the processed object is output.  The default value is true.
 *
 *@param partitionMethod The method that determines the partitions into which the matrix will be partitioned. It has a single paramter,
 *the number of nodes.  Each partition will be indexed by the node subscript. Called in MultiCoreRoot.
 *@param calculationMethod  The method that undertakes the required operation on the matrix.
 *It has a single parameter, the index of the node. Called in MultiCoreNode.  It reads from all parts of the matrix, if required, and
 *writes to a dedicated area of the matrix corresponding to the node's partition.  In the update phase these node specific
 *areas will be copied into designated area of the matrix.
 *@param errorMethod The method, called in MultiCoreRoot, that determines whether the termiantion condition has been reached.
 *It has a single parameter, errorMargin.  It returns true if the process should be repeated and false if the termination
 *condition has been reached.
 *@param updateMethod The method, called in MultiCoreRoot, that copies a set of intermediate results into the location where
 *they are accessed during the calculationMethod.
 *
 * @param logPropertyName the name of a property in the matrix object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logging is required.  The names associated with the log phases are generated internally.
 */

@CompileStatic
class MultiCoreEngine implements CSProcess {

  ChannelInput input
  ChannelOutput output
  int nodes = 0
  int iterations = 0
  double errorMargin = 0.0d
  boolean finalOut = true
  String partitionMethod = ""
  String calculationMethod = ""
  String errorMethod = ""
  String updateMethod = ""

  //String logPhaseName = ""  // will get set internally
  String logPropertyName = ""

  void run(){
    assert nodes > 0 : "nodes must be greater than 0"
    assert (iterations != 0) && (errorMargin == 0.0) || (iterations == 0) && (errorMargin != 0.0) :
        "One of iterations or errorMargin must be specified"
    def toNodesChannels = Channel.one2oneArray(nodes)
    def toNodes = new ChannelOutputList(toNodesChannels)
    def nodesToRoot = Channel.any2one()
    def network = []

    network << new MultiCoreRoot( input: input,
        output: output,
        toNodes: toNodes,
        fromNodes: nodesToRoot.in(),
        nodes: nodes,
        iterations: iterations,
        errorMargin: errorMargin,
        finalOut: finalOut,
        partitionMethod: partitionMethod,
        errorMethod: errorMethod,
        updateMethod: updateMethod,
        logPhaseName: "root",
        logPropertyName: logPropertyName
    )
    for ( i in 0 ..< nodes) {
      network << new MultiCoreNode( input: toNodesChannels[i].in(),
          output: nodesToRoot.out(),
          calculationMethod: calculationMethod,
          nodeId: i,
          logPhaseName: logPropertyName == "" ? "" : (String)"$i, node" ,
          logPropertyName: logPropertyName
      )
    }
    new PAR(network).run()
  }

}
