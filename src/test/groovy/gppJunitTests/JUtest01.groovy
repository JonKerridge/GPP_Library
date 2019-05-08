package gppJunitTests

import GPP_Library.DataDetails
import GPP_Library.ResultDetails
import GPP_Library.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test
import TestDataDefs.*

import static org.junit.Assert.assertTrue


class JUtest01 {

    @Test
    public void test() {
        def chan = Channel.one2one()

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


        def emitter = new Emit( output: chan.out(),
                                eDetails: emitterDetails )

        def collector = new Collect( input: chan.in(),
                                     rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "1: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
    }

}
