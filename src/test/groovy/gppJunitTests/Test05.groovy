package gppJunitTests

import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.functionals.workers.Worker
import groovy_parallel_patterns.terminals.*
import groovy_jcsp.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue
import TestDataDefs.*


class Test05 {

    @Test
    public void test() {
        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()

        def er = new TestExtract()

        def emitterDetails = new DataDetails(dName: TestData.getName() ,
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.createFromLocal,
        lName: TestWorker.getName(),
        lInitMethod: TestWorker.init,
        lInitData: [25, 100])

        def resultDetails = new ResultDetails(rName: TestResult.getName(),
        rInitMethod: TestResult.init,
        rCollectMethod:  TestResult.collector,
        rFinaliseMethod: TestResult.finalise,
        rFinaliseData: [er])


        def emitter = new EmitWithLocal( output: chan1.out(),
        eDetails: emitterDetails )

        def worker = new Worker(input: chan1.in(),
            output: chan2.out(),
            function: TestData.nullFunc)

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, worker, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "5: $er"

        assertTrue (er.finalSum == 2210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
    }
}
