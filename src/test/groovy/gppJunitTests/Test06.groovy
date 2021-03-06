package gppJunitTests

import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.connectors.reducers.AnyFanOne
import groovy_parallel_patterns.connectors.spreaders.OneFanAny
import groovy_parallel_patterns.functionals.groups.AnyGroupAny
import groovy_parallel_patterns.terminals.*
import groovy_jcsp.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue
import TestDataDefs.*


class Test06 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def connect1 = Channel.any2any()
        def connect2 = Channel.any2any()
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

        def aga = new AnyGroupAny (
            inputAny: connect1.in(),
            outputAny: connect2.out(),
            workers: destinations,
            function: TestData.nullFunc
        )

        def inFan = new AnyFanOne( inputAny: connect2.in(),
            output: chan2.out(),
            sources: sources)

        def collector = new Collect( input: chan2.in(),
            rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, inFan, outFan, aga, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "6: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
    }

}
