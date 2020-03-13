package gppJunitTests

import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.GroupDetails
import groovyParallelPatterns.LocalDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.connectors.reducers.*
import groovyParallelPatterns.connectors.spreaders.*
import groovyParallelPatterns.functionals.groups.*
import groovyParallelPatterns.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue
import TestDataDefs.*


class Test25 {

    @Test
    public void test() {
        def workers = 3

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def any1 = Channel.one2any()
        def any2 = Channel.any2any()
        def any3 = Channel.any2any()
        def any4 = Channel.any2one()

        def m1 = [[0], [0], [0]]             // for stage 1
        def m2 = [[100], [100], [100]]       // for stage 2
        def m3 = [[1000], [1000], [1000]]    // for stage 3
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

        def group1Details = new GroupDetails(workers)
        def group2Details = new GroupDetails(workers)
        def group3Details = new GroupDetails(workers)

        for ( w in 0..< workers){
            group1Details.insertGroupDetails(
                w,
                TestWorker.getName(),
                TestWorker.init,
                [100, 0],
                TestWorker.finalise,
                [])
            group2Details.insertGroupDetails(
                w,
                TestWorker.getName(),
                TestWorker.init,
                [100, 0],
                TestWorker.finalise,
                [])
            group3Details.insertGroupDetails(
                w,
                TestWorker.getName(),
                TestWorker.init,
                [100, 0],
                TestWorker.finalise,
                [])
        }

        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails )

        def outFan = new OneFanAny (input: chan1.in(),
        outputAny: any1.out(),
        destinations: workers)

        def stage1 = new AnyGroupAny(inputAny: any1.in(),
        outputAny : any2.out(),
        gDetails: group1Details,
        function: TestData.func1,
        modifier: m1,
        workers: workers)

        def stage2 = new AnyGroupAny(inputAny: any2.in(),
        outputAny: any3.out(),
        gDetails: group2Details,
        function: TestData.func2,
        modifier: m2,
        workers: workers)

        def stage3 = new AnyGroupAny(inputAny: any3.in(),
        outputAny: any4.out(),
        gDetails: group3Details,
        function: TestData.func3,
        modifier: m3,
        workers: workers)

        def inFan = new AnyFanOne( inputAny: any4.in(),
        output: chan2.out(),
        sources: workers)

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, inFan, stage1, stage2, stage3, outFan, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "25: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 210)
        assertTrue (er.w2 == 2210)
        assertTrue (er.w3 == 20210)
    }
}
