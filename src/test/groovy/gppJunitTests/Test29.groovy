package gppJunitTests

import groovyParallelPatterns.CompositeDetails
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.connectors.reducers.*
import groovyParallelPatterns.connectors.spreaders.*
import groovyParallelPatterns.functionals.composites.ListGroupOfPipelines
import groovyParallelPatterns.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue
import TestDataDefs.*


class Test29 {

    @Test
    public void test() {
        def stages = 3
        def workers = 3

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def connect1 = Channel.one2oneArray(workers)
        def connect2 = Channel.one2oneArray(workers)
        def c1ListIn = new ChannelInputList(connect1)
        def c2ListIn = new ChannelInputList(connect2)
        def clListOut = new ChannelOutputList(connect1)
        def c2ListOut = new ChannelOutputList(connect2)

        def er = new TestExtract()

        def emitterDetails = new DataDetails(dName: TestData.getName() ,
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.create)

        def resultDetails =
                new ResultDetails(rName: TestResult.getName(),
                rInitMethod: TestResult.init,
                rCollectMethod:  TestResult.collector,
                rFinaliseMethod: TestResult.finalise,
                rFinaliseData: [er])


        List [][] initData = new List[workers][stages]
        initData[0][0] = [100, 0]
        initData[1][0] = [100, 0]
        initData[2][0] = [100, 0]

        initData[0][1] = [100, 0]
        initData[1][1] = [100, 0]
        initData[2][1] = [100, 0]

        initData[0][2] = [100, 0]
        initData[1][2] = [100, 0]
        initData[2][2] = [100, 0]

        String wName = TestWorker.getName()
        String initMethod = TestWorker.init
        String finaliseMethod = TestWorker.finalise
        List finalData = null

        CompositeDetails compDetails = new CompositeDetails(workers, stages)
        for ( w in 0..< workers)
            for ( s in 0 ..< stages)
                compDetails.insertCompositeDetails( w,
                        s,
                        wName,
                        initMethod,
                        initData[w][s],
                        finaliseMethod,
                        finalData)

        //println "${compDetails.toString()}"

        def stageOps = [TestData.func1, TestData.func2, TestData.func3]

        def pipeModifiers = [[[0], [0], [0]], [[0], [0], [0]], [[0], [0], [0]]]

        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails )

        def ofl = new OneFanList(input: chan1.in(),
        outputList: clListOut)

        def gop = new ListGroupOfPipelines( groups: workers,
        stages: stages,
        inputList: c1ListIn,
        outputList: c2ListOut,
        cDetails: compDetails,
        stageOp: stageOps,
        stageModifier : pipeModifiers)

        def lfo = new ListFanOne ( inputList: c2ListIn,
        output: chan2.out())

        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)



        PAR testParallel = new PAR([emitter, ofl, gop, lfo, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        //

        println "29: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 210)
        assertTrue (er.w2 == 210)
        assertTrue (er.w3 == 210)
    }
}
