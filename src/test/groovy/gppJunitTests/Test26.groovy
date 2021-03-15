package gppJunitTests

import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.LocalDetails
import groovy_parallel_patterns.PipelineDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.connectors.reducers.*
import groovy_parallel_patterns.connectors.spreaders.*
import groovy_parallel_patterns.functionals.groups.*
import groovy_parallel_patterns.functionals.pipelines.OnePipelineOne
import groovy_parallel_patterns.terminals.*
import groovy_jcsp.*
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

        def pipeDetails = new PipelineDetails(stages)

        for ( s in 0..< stages){
            pipeDetails.insertPipelineDetails(
                s,
                TestWorker.getName(),
                TestWorker.init,
                null,
                TestWorker.finalise,
                null
            )
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
