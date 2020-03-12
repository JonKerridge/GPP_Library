package groovyParallelPatterns.cluster

import jcsp.net2.NetLocation

/**
 * Contains a NetLocation that is to be used by the host to write to the node the NodeNetwork
 * object to the node and the node's IP address.<p>
 *
 *@param loadLocation The NetLocation of the node's net input channel used to read a NodeNetwork object
 *@param nodeIP A String containing the node's IP address
 */
class RequestNodeNetwork implements Serializable {
	
	NetLocation loadLocation 	//net channel input location used to read WorkerObject
	String nodeIP		 		// IP address of the worker node sending object to host
}
