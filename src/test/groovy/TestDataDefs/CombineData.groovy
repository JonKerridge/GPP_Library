package TestDataDefs

import groovy_parallel_patterns.DataClass
import groovy_parallel_patterns.DataClassInterface
import groovy.transform.CompileStatic

@CompileStatic
class CombineData extends DataClass {

    int data = 0

    static String initMethod = "init"
    static String combineMethod = "combine"

    int init ( List d){
        return DataClassInterface.completedOK
    }

    int combine(TestDataDefs.TestData o){
        data += o.data
        return DataClassInterface.completedOK
    }

}
