package groovy_parallel_patterns.cluster

import groovy_parallel_patterns.UniversalSignal
import groovy_jcsp.*
import jcsp.net2.*
import jcsp.net2.mobile.*
import jcsp.net2.tcpip.TCPIPNodeAddress

/**
 * The script used to create a host process network containing an emit and collector processes.
 * It also defines the process networks for each of the nodes in the cluster, in the List nodeObjects.
 * Thus RunHost has to be modified for each solution.
 */
def nodes = 4
String hostIP = "127.0.0.1"
def hostAddr = new TCPIPNodeAddress(hostIP, 1000)
//def hostAddr = new TCPIPNodeAddress(1000)
Node.getInstance().init(hostAddr)
println "Host running on ${hostAddr.getIpAddress()} for $nodes worker nodes"
// create request channel
def hostRequest = NetChannel.numberedNet2One(1)
// read requests from nodes
def loadChannels = new ChannelOutputList()
def nodeIPs = []
for ( w in 1 .. nodes ) {
	RequestNodeNetwork nodeRequest = hostRequest.read()
	def nodeLoadChannel = NetChannel.one2net(nodeRequest.loadLocation, new CodeLoadingChannelFilter.FilterTX())
	loadChannels.append(nodeLoadChannel)
	nodeIPs << nodeRequest.nodeIP
}
// construct nodeObject for each node
def nodeObjects = []
//nodeObjects << new NodeNetwork ( nodeProcesses: new UserProcess(),
//								   inConnections : [100, 101, 102],
//								   outConnections: [[nodeIPs[1], 100], [nodeIPs[2], 100]])



// send worker objects to each node
for ( w in 0 ..< nodes ) {
	loadChannels[w].write(nodeObjects[w])
}
println "Sent nodeObjects to nodes"
// read a signal form each node to indicate input channel have been created
for ( w in 0 ..< nodes ) {
	hostRequest.read()    
}
println "Read in channel creation complete signals from nodes"
// create in channel lists for Emitter and Collector Networks
def emitterInConnections = []
def collectorInConnections = [100]
def inChanLists = NodeNetwork.BuildHostInChannels(emitterInConnections, collectorInConnections)

println "Sending signals to nodes to create out channel connections"

// write a signal to each node to indicate all input channels have been created
for ( w in 0 ..< nodes ) {
	loadChannels[w].write(new UniversalSignal())
}

// create out channel lists for Emitter and Collector
def emitterOutConnections = [[nodeIPs[0], 100]]
def collectorOutConnections = []
def outChanLists = NodeNetwork.BuildHostOutChannels(emitterOutConnections, collectorOutConnections)

// construct local processes on host node
def emit = null
def collector = null
def processManagers = NodeNetwork.BuildHostNetwork(emit, 
													collector, 
													inChanLists, 
													outChanLists)

processManagers[0].start()
processManagers[1].start()
println "started processes running on Host"

processManagers[0].join()
processManagers[1].join()




