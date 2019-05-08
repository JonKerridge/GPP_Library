package gppJunitTests

import GPP_Library.DataDetails
import GPP_Library.ResultDetails
import GPP_Library.connectors.reducers.*
import GPP_Library.connectors.spreaders.*
import GPP_Library.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue
import TestDataDefs.*


class JUtest12 {

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

        def outFan = new OneParCastList (input: chan1.in(),
        outputList: outList)

        def inFan = new ListParOne( inputList: inList,
        output: chan2.out())

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, inFan, outFan, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "12: $er"

        assertTrue (er.finalSum == 840)
        assertTrue (er.dataSetCount == 80)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 80)
    }
}
