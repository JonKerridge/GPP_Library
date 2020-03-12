package gppJunitTests

import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.functionals.workers.Worker
import groovyParallelPatterns.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test
import TestDataDefs.*

import static org.junit.Assert.assertTrue

class Test01 {

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
        function: TestData.nullFunc)

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, worker, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

         println "1: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
    }
}
