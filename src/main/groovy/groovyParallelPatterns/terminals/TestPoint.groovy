package groovyParallelPatterns.terminals

import groovyParallelPatterns.UniversalTerminator
import groovy.transform.CompileStatic
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput

/**
 * The TestPoint process provides a means of testing a network 
 * during its development.  The process outputs any class that 
 * contains the toString() method.  It can also be used when undertaking
 * timing evaluations of a part network , in which case the toString()
 * method should return null.
 * 
 * @param input the channel from which incoming data objects are read
 * @param id a String containing some identification information
 */
@CompileStatic
class TestPoint implements CSProcess {
	ChannelInput input
	String id = ""
	
	void run() {
//		println "TestPoint Running"
		boolean running = true
		Object o = new Object()
		while (running){
			o = input.read()
			if ( o instanceof UniversalTerminator) running = false
			else {
				String s = o.toString()
				if (s != null) println "Test Point $id : $s"
			}
		}
		println "Test Point $id : Terminated"		
	}
}
