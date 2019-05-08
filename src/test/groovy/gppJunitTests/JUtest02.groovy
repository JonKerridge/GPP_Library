package gppJunitTests

import GPP_Library.DataDetails
import GPP_Library.ResultDetails
import GPP_Library.functionals.workers.Worker
import GPP_Library.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test
import TestDataDefs.*

import static org.junit.Assert.assertTrue


class JUtest02 {

    @Test
    public void test() {

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()

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

        def worker = new Worker(input: chan1.in(),
        output: chan2.out(),
        function: TestData.f1)

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, worker, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

         println "2: $er"

       assertTrue (er.finalSum == 420)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
    }
}
