package gppJunitTests

import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import TestDataDefs.TestSingle
import groovy_jcsp.PAR
import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.functionals.workers.Worker
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.EmitSingle
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test49 {

    @Test
    void test() {

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()

        def er = new TestExtract()

        def emitterDetails = new DataDetails(dName: TestSingle.getName() ,
        dInitMethod: TestSingle.init,
        dInitData: [20],
        dCreateMethod: TestSingle.create)

        def resultDetails = new ResultDetails(rName: TestResult.getName(),
        rInitMethod: TestResult.init,
        rCollectMethod:  TestResult.collectorSingle,
        rFinaliseMethod: TestResult.finaliseSingle,
        rFinaliseData: [er])


        def emitter = new EmitSingle( output: chan1.out(),
        eDetails: emitterDetails )

        def worker = new Worker(input: chan1.in(),
        output: chan2.out(),
        function: TestSingle.nullFunc)

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, worker, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

         println "49: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 1)
    }
}
