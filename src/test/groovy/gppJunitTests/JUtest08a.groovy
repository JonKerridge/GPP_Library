package gppJunitTests

import GPP_Library.DataDetails
import GPP_Library.ResultDetails
import GPP_Library.connectors.reducers.ListMergeOne
import GPP_Library.connectors.spreaders.OneFanList
import GPP_Library.terminals.Collect
import GPP_Library.terminals.Emit
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue
import TestDataDefs.*

class JUtest08a {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def connect = Channel.one2oneArray(4)
        def inList = new ChannelInputList (connect)
        def outList = new ChannelOutputList(connect)

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

        def outFan = new OneFanList (input: chan1.in(),
        outputList: outList)

        def inFan = new ListMergeOne( inputList: inList,
        output: chan2.out())

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, inFan, outFan, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "8a: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
    }
}
