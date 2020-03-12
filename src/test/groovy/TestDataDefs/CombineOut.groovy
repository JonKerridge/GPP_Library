package TestDataDefs


import groovyParallelPatterns.DataClass
import groovyParallelPatterns.DataClassInterface
import groovy.transform.CompileStatic


@CompileStatic
class CombineOut extends DataClass {

    int data
    static String initMethod = "init"
    static String finaliseMethod = "finalise"

    int init ( List d){
        return DataClassInterface.completedOK
    }

    int finalise (CombineData o){
        data = o.data
        return DataClassInterface.completedOK
    }

}
