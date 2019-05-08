package TestDataDefs


import GPP_Library.DataClass
import GPP_Library.DataClassInterface

//@CompileStatic
class TestResult extends DataClass {
    int sum = 0
    int dataSets = 0
    int finalInstance = 0
    int maxCloneNumber = 0
    int w1 = 0
    int w2 = 0
    int w3 = 0

    static String init = "initClass"
    static String collector = "collector"
    static String finalise = "finalise"

    int initClass ( List d){
        return DataClassInterface.completedOK
    }

    int collector (TestData d) { // d can be of many types eg TestData, CombineData
        sum += d.data
        finalInstance = d.instanceNumber > finalInstance ? d.instanceNumber : finalInstance
        dataSets += 1
        if ( d.cloneNumber > maxCloneNumber) maxCloneNumber = d.cloneNumber
        w1 += d.w1
        w2 += d.w2
        w3 += d.w3
        return DataClassInterface.completedOK
    }
    int finalise ( List d) {
        TestExtract er = d[0]
        er.finalSum = sum
        er.dataSetCount = dataSets
        er.finalInstance = finalInstance
        er.maxClone = maxCloneNumber
        er.w1 = w1
        er.w2 = w2
        er.w3 = w3
        //println "Final sum = $sum from $dataSets dataSets with final instance $finalInstance and maxClone = $maxCloneNumber"
        //println "Worker out values "
        return DataClassInterface.completedOK
    }

}
