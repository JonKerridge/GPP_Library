package groovyParallelPatterns.cluster


import jcsp.lang.*
import jcsp.net2.*
import jcsp.net2.mobile.*
import jcsp.net2.tcpip.TCPIPNodeAddress
import jcsp.userIO.*

/**
 * RunNode is the script that runs on a node in the cluster.  It does not need to be changed
 * provided the host process runs both the solution's emit and collector processes.
 *
 */
String nodeAddr4 = Ask.Int( "what is the fourth part of the node's IP-address?  ", 2, 254)
String nodeIP = "127.0.0." + nodeAddr4
def nodeAddr = new TCPIPNodeAddress(nodeIP, 1000)
//def nodeAddr = new TCPIPNodeAddress(1000)  // create nodeAddr for most global IP address
// create node instance 
Node.getInstance().init(nodeAddr)
String nodeIPstring = nodeAddr.getIpAddress()
println "Node is located at $nodeIPstring "
// create load channel
NetChannelInput loadChannel = NetChannel.numberedNet2One(1, 
									new CodeLoadingChannelFilter.FilterRX())
// make connection to host
// must be passed in as an argument or read from keyboard
//String hostIP = args[0]
//String hostIP = Ask.string("Host IP address?")
String hostIP = "127.0.0.1"
def hostAddr = new TCPIPNodeAddress(hostIP, 1000)
// create host request channel
def hostRequest = NetChannel.any2net(hostAddr, 1)
ProcessManager nodePM = NodeNetwork.BuildNodeNetwork(hostRequest, 
													 loadChannel, 
													 nodeIPstring) 
nodePM.start()
// and wait for it to terminate
nodePM.join()
