package groovy_parallel_patterns.cluster

import groovy_parallel_patterns.*
import groovy_jcsp.*
import jcsp.lang.*
import jcsp.net2.*
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.*

/**
 * NodeNetwork defines the processes that run in parallel on a single node of the cluster and
 * the channels connections used by the node.
 * 
 * @param nodeProcess A NodeInterface that defines the processes that make up the node internal process network
 * @param inConnections A list containing the channel numbers associated with net input channels for this node
 * @param outConnections A list containing the net output channel connections; each composed of a list
 * containing the IP address of the node and the channel number to which the output channel is connected.
 */
class NodeNetwork implements Serializable {
	NodeInterface nodeProcesses		// the processes that make up the node's internal network
	List inConnections  = []		// list of channel numbers
	List outConnections = []    	// list of Net Output Connections [IP, cn] 

									
	/**
	 * BuildNodeNetwork is used to invoke an instance of a NodeNetwork.  
	 * This method is not user defined but is used within the RunNode script.
	 * 
	 * @param hostRequest The net channel output used to connect the node to the host node
	 * @param loadChannel The net channel input used to load the NodeNetwork object from the host node
	 * @param nodeIP A String containing the IP address of the requesting Node
	 * @return nodePM The ProcessManager object created as part of this method containing the nodeProcess
	 */
	static ProcessManager BuildNodeNetwork (NetChannelOutput hostRequest,
                                          NetChannelInput loadChannel,
                                          String nodeIP) {
		NetLocation loadChannelLocation = loadChannel.getLocation()
		// send request for Node to Host
		hostRequest.write(new RequestNodeNetwork (loadLocation:loadChannelLocation,
												  nodeIP: nodeIP))
		// read NodeNetwork object from node's loadChannel
		NodeNetwork nodeNetwork = loadChannel.read()
		NodeInterface internalProcess = nodeNetwork.nodeProcesses
		List inConnections =  nodeNetwork.inConnections
		List outConnections =  nodeNetwork.outConnections
		def inChannels = new ChannelInputList()
		for ( i in 0 ..< inConnections.size()){
			inChannels.append(NetChannel.numberedNet2One(inConnections[i], 
									new CodeLoadingChannelFilter.FilterRX()))
		}
		// signal that Node's input channels have been created
		hostRequest.write(new UniversalSignal())
		// wait for responding signal from Host
		loadChannel.read()
		// create the output channels
		def outChannels = new ChannelOutputList()
		for ( i in 0 ..< outConnections.size()){
			def outNodeAddr = new  TCPIPNodeAddress(outConnections[i][0], 1000)
			outChannels.append(NetChannel.any2net(outNodeAddr, outConnections[i][1], 
											new CodeLoadingChannelFilter.FilterTX()))
		}
		// connect the channel lists to the worker process
		internalProcess.connect(inChannels, outChannels)
		// create a process manager for wProcess
		def nodePM = new ProcessManager(internalProcess)		
		return nodePM
	}
									 
	/**
	 * Used to build the host's emit and collect processes channel input connections
	 * This method is not user defined but is used within the RunHost script.
	 *								 
	 * @param emitterInConnections A list of emit process input connections specified as channel numbers
	 * @param collectorInConnections A list of collector process input connections specified as channel numbers
	 * @return a list containing 
	 */
	static List BuildHostInChannels (List emitterInConnections,
							  List collectorInConnections){
		def emitterInChannelList = new ChannelInputList()
		def collectorInChannelList = new ChannelInputList()
		for ( i in 0 ..< emitterInConnections.size()){
			emitterInChannelList.append(NetChannel.numberedNet2One(emitterInConnections[i], 
									new CodeLoadingChannelFilter.FilterRX()))
		}
		for ( i in 0 ..< collectorInConnections.size()){
			collectorInChannelList.append(NetChannel.numberedNet2One(collectorInConnections[i], 
									new CodeLoadingChannelFilter.FilterRX()))
		}
		return [emitterInChannelList, collectorInChannelList]		
	}								 								
	/**
	 * Used to build the host's emit and collect processes channel output connections
	 * This method is not user defined but is used within the RunHost script.
	 *								 
	 * @param emitterOutConnections A List of emitter output connections
	 * @param collectorOutConnections A List of collector output connections
	 * @return
	 */
	static List BuildHostOutChannels (List emitterOutConnections,
									   List collectorOutConnections){
		
		def emitterOutChannelList = new ChannelOutputList()
		def collectorOutChannelList = new ChannelOutputList()		
		for ( i in 0 ..< emitterOutConnections.size()){
			def outNodeAddr = new  TCPIPNodeAddress(emitterOutConnections[i][0], 1000)
			emitterOutChannelList.append(NetChannel.any2net(outNodeAddr, emitterOutConnections[i][1], 
											new CodeLoadingChannelFilter.FilterTX()))
		}
		
		for ( i in 0 ..< collectorOutConnections.size()){
			def outNodeAddr = new  TCPIPNodeAddress(collectorOutConnections[i][0], 1000)
			collectorOutChannelList.append(NetChannel.any2net(outNodeAddr, collectorOutConnections[i][1], 
											new CodeLoadingChannelFilter.FilterTX()))
		}
		return [emitterOutChannelList, collectorOutChannelList]
	}
	
	/**
	 * Used to build the host's emit and collect processes 
	 * This method is not user defined but is used within the RunHost script.
	 *								 
	 * @param emit The emit process definition as a NodeNetwork
	 * @param collector The collector process definition as a NodeNetwork
	 * @param inChannels A list of of two elements containing the emit and collector input channels
	 * @param outChannels A list of of two elements containing the emit and collector output channels
	 * @return A list containing the ProcessManager objects for the emit and collector processes
	 */
	static List BuildHostNetwork (NodeNetwork emit,
                                NodeNetwork collector,
                                List inChannels,
                                List outChannels){
		println"BHN: in: $inChannels,\n out: $outChannels"
		emit.nodeProcesses.connect(inChannels[0], outChannels[0])
		collector.nodeProcesses.connect(inChannels[1], outChannels[1])
		def emitPM = new ProcessManager(emit.nodeProcesses)
		def collectPM = new ProcessManager(collector.nodeProcesses)
		return [emitPM, collectPM]
	}								   								   								 								
}
