package groovy_parallel_patterns.cluster

import groovy_jcsp.*
import jcsp.lang.*

/**
 * NodeInterface provides an interface that is used by nodes in a cluster
 *
 *@usage connect(inChannels, outChannels)
 *@param inChannels A ChannelInputList containing the channels used as input channels to the Node
 *@param outChannels A ChannelOutputList containing the channels used as output channels of the node
 */
interface NodeInterface extends CSProcess, Serializable{

	void connect(ChannelInputList inChannels, ChannelOutputList outChannels)  
}
