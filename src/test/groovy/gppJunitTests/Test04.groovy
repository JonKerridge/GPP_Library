package gppJunitTests

import GPP_Library.DataDetails
import GPP_Library.LocalDetails
import GPP_Library.ResultDetails
import GPP_Library.functionals.workers.ThreePhaseWorker
import GPP_Library.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test
import TestDataDefs.*

import static org.junit.Assert.assertTrue


class Test04 {

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

        def tpwLocal = new LocalDetails(lName: TPWdata.getName(),
            lInitMethod: TPWdata.initMethod,
            lInitData: [2])


        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails )

        def worker = new ThreePhaseWorker(input: chan1.in(),
        output: chan2.out(),
        lDetails: tpwLocal,
        inputMethod: TPWdata.inputMethod,
        workMethod: TPWdata.workMethod,
        outFunction: TPWdata.outFunction)

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, worker, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

         println "4: $er"

       assertTrue (er.finalSum == 840)
        assertTrue (er.dataSetCount == 2)
        assertTrue (er.finalInstance == 2)
    }
}
