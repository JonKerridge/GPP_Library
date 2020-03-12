package gppJunitTests

import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.connectors.reducers.AnyFanOne
import groovyParallelPatterns.connectors.spreaders.AnyFanAny
import groovyParallelPatterns.connectors.spreaders.OneFanAny
import groovyParallelPatterns.functionals.groups.AnyGroupAny
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test07 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def connect1 = Channel.any2any()
        def connect2 = Channel.any2any()
        def connect3 = Channel.any2any()
        def connect4 = Channel.any2any()
        def destinations = 3
        def sources = 3


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

        def outFan = new OneFanAny (input: chan1.in(),
            outputAny: connect1.out(),
            destinations: destinations)

        def aga1 = new AnyGroupAny (
            inputAny: connect1.in(),
            outputAny: connect2.out(),
            workers: destinations,
            function: TestData.nullFunc
        )

        def afa = new AnyFanAny (
            inputAny: connect2.in(),
            outputAny: connect3.out(),
            destinations: destinations,
            sources: sources
        )

        def aga2 = new AnyGroupAny (
            inputAny: connect3.in(),
            outputAny: connect4.out(),
            workers: destinations,
            function: TestData.nullFunc
        )

        def inFan = new AnyFanOne( inputAny: connect4.in(),
            output: chan2.out(),
            sources: sources)

        def collector = new Collect( input: chan2.in(),
            rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, inFan, outFan, aga1, aga2, afa, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "7: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
    }

}
