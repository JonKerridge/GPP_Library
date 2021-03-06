package TestDataDefs


import groovy_parallel_patterns.DataClass
import groovy_parallel_patterns.DataClassInterface

//@CompileStatic
class TestResult extends DataClass {
    int sum = 0
    int dataSets = 0
    int finalInstance = 0
    int maxCloneNumber = 0
    int w1 = 0
    int w2 = 0
    int w3 = 0
    List cloneOrder = []

    static String init = "initClass"
    static String collector = "collector"
    static String collectorSingle = "collectorSingle"
    static String finalise = "finalise"
    static String finaliseSingle = "finaliseSingle"

    int initClass ( List d){
        return completedOK
    }

    int collector (Object d) { // d can be of many types eg TestData, CombineData Serialised testData
        sum += d.data
        finalInstance = d.instanceNumber > finalInstance ? d.instanceNumber : finalInstance
        dataSets += 1
        if ( d.cloneNumber > maxCloneNumber) {
            maxCloneNumber = d.cloneNumber
        }
        cloneOrder.add(d.cloneNumber)
        w1 += d.w1
        w2 += d.w2
        w3 += d.w3
        return completedOK
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
        er.cloneOrder = cloneOrder
        //println "Final sum = $sum from $dataSets dataSets with final instance $finalInstance and maxClone = $maxCloneNumber"
        //println "Worker out values "
        return completedOK
    }

    int len
    int collectorSingle(TestSingle d){
        for ( i in 0 .. d.len) sum = sum + d.consts[i]
        this.len = d.len
        dataSets = 1
        return completedOK
    }

    int finaliseSingle(List d){
        TestExtract er = d[0]
        er.finalSum = sum
        er.dataSetCount = dataSets
        return completedOK
    }
}
