package TestDataDefs

import GPP_Library.DataClass

class TPWdata extends DataClass{

    static String initMethod = "initClass"
    static String inputMethod = "inputMethod"
    static String workMethod = "workMethod"
    static String outFunction = "outFunction"

    int wData = 0
    static int maxInstances = 0
    static int currentInstance = 1

    int initClass (List d){
        maxInstances = d[0]
        return completedOK
    }

    int inputMethod(List params){ //[ [null, inputObject]
        TestDataDefs.TestData inputObject = params[1]
        wData += inputObject.data
//        println "In: $wData, ${inputObject.data}"
        return completedOK
    }

    int workMethod() {
        wData = wData * 2
//        println "Work: new wData = $wData"
        return completedOK
    }

    TestDataDefs.TestData outFunction() {
//        println "outFunction $currentInstance; max = $maxInstances"
        if (currentInstance > maxInstances) {
            currentInstance = 1 // so works in AllTests
            return null
        }
        else {
            TestDataDefs.TestData td = new TestDataDefs.TestData(data: wData)
            td.instanceNumber = currentInstance
            currentInstance += 1
//            println "returning ${td.toString()}"
            return td
        }
    }

}
