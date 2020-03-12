package TestDataDefs

import groovyParallelPatterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class NullTestWorker implements CSProcess {
	
	ChannelInput input
	ChannelOutput output
	
	void run(){
		def o = input.read()
		while (!( o instanceof UniversalTerminator)){
			output.write(o)
			o = input.read()
		}
		output.write(new UniversalTerminator())
	}

}
