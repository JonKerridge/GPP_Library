package gppJunitTests

import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.connectors.reducers.ListFanOne
import groovy_parallel_patterns.connectors.spreaders.OneIndexedList
import groovy_parallel_patterns.functionals.groups.ListGroupList
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import groovy_jcsp.ChannelInputList
import groovy_jcsp.ChannelOutputList
import groovy_jcsp.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test42 {

  @Test
  public void test() {
    def chan1 = Channel.one2one()
    def chan2 = Channel.one2one()
    def connect1 = Channel.one2oneArray(4)
    def connect2 = Channel.one2oneArray(4)
    def inList1 = new ChannelInputList(connect1)
    def outList1 = new ChannelOutputList(connect1)
    def inList2 = new ChannelInputList(connect2)
    def outList2 = new ChannelOutputList(connect2)

    def er = new TestExtract()

    def emitterDetails = new DataDetails(dName: TestData.getName(),
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.create)

    def resultDetails = new ResultDetails(rName: TestResult.getName(),
        rInitMethod: TestResult.init,
        rCollectMethod: TestResult.collector,
        rFinaliseMethod: TestResult.finalise,
        rFinaliseData: [er])

    def emitter = new Emit(output: chan1.out(),
        eDetails: emitterDetails)

    def outFan = new OneIndexedList(input: chan1.in(),
        outputList: outList1,
        indexFunction: TestData.indexer,
        indexBounds: [0, 3, 4]) // [lower bound, upper bound, divisor]

    def lgl = new ListGroupList(inputList: inList1,
        outputList: outList2,
        workers: 4,
        function: TestData.nullFunc)

    def inFan = new ListFanOne(inputList: inList2,
        output: chan2.out())

    def collector = new Collect(input: chan2.in(),
        rDetails: resultDetails)

    PAR testParallel = new PAR([emitter, inFan, lgl, outFan, collector])
    testParallel.run()
    testParallel.removeAllProcesses()

    println "42: $er"

    assertTrue(er.finalSum == 210)
    assertTrue(er.dataSetCount == 20)
    assertTrue(er.finalInstance == 20)
    assertTrue(er.maxClone == 0)
  }
}
