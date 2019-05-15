package gppJunitTests

import GPP_Library.DataDetails
import GPP_Library.LocalDetails
import GPP_Library.ResultDetails
import GPP_Library.functionals.transformers.CombineNto1
import GPP_Library.functionals.workers.Worker
import GPP_Library.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue

import TestDataDefs.*

class Test02 {

    @Test
    public void test() {

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def chan3 = Channel.one2one()

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

        def localData = new LocalDetails( lName: TestData.getName(),
                                          lInitMethod: TestData.partialInitialise)

        def outData = new LocalDetails( lName: TestData.getName(),
                                        lInitMethod: TestData.partialInitialise,
                                        lFinaliseMethod: TestData.finaliseMethod)

        def emitter = new Emit( output: chan1.out(),
                                eDetails: emitterDetails )

        def combiner = new CombineNto1( input: chan1.in(),
                                        output: chan2.out(),
                                        localDetails: localData,
                                        outDetails: outData,
                                        combineMethod: CombineData.combineMethod)

        def worker = new Worker(input: chan2.in(),
            output: chan3.out(),
            function: TestData.nullFunc)

        def collector = new Collect( input: chan3.in(),
                                     rDetails: resultDetails)


        PAR testParallel = new PAR([emitter, combiner, worker, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "2: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 1)
        assertTrue (er.finalInstance == 20)
    }
}
