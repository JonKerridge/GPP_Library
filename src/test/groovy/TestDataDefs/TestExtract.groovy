package TestDataDefs

class TestExtract {
    int finalSum
    int dataSetCount
    int finalInstance
    int maxClone
    int w1, w2, w3
    List cloneOrder

    String toString() {
        String s = "Final Sum = $finalSum"
        s += " from $dataSetCount data sets with"
        s += " final instance $finalInstance "
        s += "max clone is $maxClone ... W-values = "
        s += "w1: $w1, w2: $w2, w3: $w3"
        if ( ! cloneOrder.isEmpty()){
            s += "\n Clone Order = "
            cloneOrder.each { s += " $it"}
        }
        return s
    }
}
