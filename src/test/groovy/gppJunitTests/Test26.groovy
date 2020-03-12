package gppJunitTests

import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.LocalDetails
import groovyParallelPatterns.PipelineDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.connectors.reducers.*
import groovyParallelPatterns.connectors.spreaders.*
import groovyParallelPatterns.functionals.groups.*
import groovyParallelPatterns.functionals.pipelines.OnePipelineOne
import groovyParallelPatterns.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue
import TestDataDefs.*


class Test26 {

    @Test
    public void test() {
        def stages = 3

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()

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

        def pipeDetails = new PipelineDetails(stages: stages,
        stageDetails: new LocalDetails[stages])

        for ( s in 0..< stages){
            pipeDetails.stageDetails[s] = new LocalDetails()
            pipeDetails.stageDetails[s].lName = TestWorker.getName()
            pipeDetails.stageDetails[s].lInitMethod = TestWorker.init
            pipeDetails.stageDetails[s].lFinaliseMethod = TestWorker.finalise
        }

        pipeDetails.stageDetails[0].lInitData = [25, 10]
        pipeDetails.stageDetails[1].lInitData = [25, 100]
        pipeDetails.stageDetails[2].lInitData = [25, 200]

        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails )

        def pipe = new OnePipelineOne( input: chan1.in(),
        output: chan2.out(),
        stages: 3,
        stageOp: [TestData.func1, TestData.func2, TestData.func3],
        stageModifier: [[0], [0], [0]],
        pDetails: pipeDetails)


        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, pipe, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "26: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 410)
        assertTrue (er.w2 == 2210)
        assertTrue (er.w3 == 4210)
    }
}
