package gppJunitTests

import GPP_Library.DataDetails
import GPP_Library.ResultDetails
import GPP_Library.connectors.reducers.ListFanOne
import GPP_Library.connectors.reducers.ListMergeOne
import GPP_Library.connectors.spreaders.OneParCastList
import GPP_Library.functionals.groups.ListGroupList
import GPP_Library.terminals.Collect
import GPP_Library.terminals.Emit
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test15 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def connect1 = Channel.one2oneArray(4)
        def inList1 = new ChannelInputList (connect1)
        def outList1 = new ChannelOutputList(connect1)

        def connect2 = Channel.one2oneArray(4)
        def inList2 = new ChannelInputList (connect2)
        def outList2 = new ChannelOutputList(connect2)

        def er = new TestExtract()

        def emitterDetails = new DataDetails(dName: TestData.getName() ,
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.create)

        def resultDetails = new ResultDetails(rName: TestResult.getName(),
        rInitMethod: TestResult.init,
        rCollectMethod:  TestResult.collector,
        rFinaliseMethod: TestResult.finalise,
        rFinaliseData: [er])

        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails )

        def outFan = new OneParCastList(input: chan1.in(),
        outputList: outList1)

        def lgl = new ListGroupList(
            inputList: inList1,
            outputList: outList2,
            workers: 4,
            function: TestData.nullFunc
        )

        def inFan = new ListMergeOne( inputList: inList2,
        output: chan2.out())

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, inFan, lgl, outFan, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "15: $er"

        assertTrue (er.finalSum == 840)
        assertTrue (er.dataSetCount == 80)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 80)
    }
}
