package TestDataDefs


import groovyParallelPatterns.DataClass
import groovy.transform.CompileStatic

@CompileStatic
class TestWorker extends DataClass {

	List <Integer> consts = []
	static String init = "initClass"
	static String finalise = "nullFinalise"

	int initClass ( List d){
		int instances = d[0]
		int initialValue = d[1]
		for ( i in 0 .. instances) consts << initialValue + i
		return completedOK
	}

	int nullFinalise (List d)	{
		return completedOK
	}

}
