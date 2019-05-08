package TestDataDefs


import GPP_Library.DataClass
import GPP_Library.DataClassInterface
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
		return DataClassInterface.completedOK
	}

	int nullFinalise (List d)	{
		return DataClassInterface.completedOK
	}

}
