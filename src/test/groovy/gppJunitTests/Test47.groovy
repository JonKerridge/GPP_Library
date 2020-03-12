package gppJunitTests

import groovyParallelPatterns.CompositeDetails
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.connectors.spreaders.OneFanAny
import groovyParallelPatterns.patterns.GroupOfPipelineCollectPattern
import groovyParallelPatterns.terminals.Emit
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import TestDataDefs.TestWorker
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test47 {

    @Test
    public void test() {
        def stages = 3
        def workers = 3

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2any()
        def chan3 = Channel.any2one()


        def er = new TestExtract()
        def er1 = new TestExtract()
        def er2 = new TestExtract()
        def er3 = new TestExtract()

        def emitterDetails = new DataDetails(dName: TestData.getName() ,
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.create)

        List resultDetails = []

        resultDetails <<
                new ResultDetails(rName: TestResult.getName(),
                rInitMethod: TestResult.init,
                rCollectMethod:  TestResult.collector,
                rFinaliseMethod: TestResult.finalise,
                rFinaliseData: [er1])

        resultDetails <<
                new ResultDetails(rName: TestResult.getName(),
                rInitMethod: TestResult.init,
                rCollectMethod:  TestResult.collector,
                rFinaliseMethod: TestResult.finalise,
                rFinaliseData: [er2])

        resultDetails <<
                new ResultDetails(rName: TestResult.getName(),
                rInitMethod: TestResult.init,
                rCollectMethod:  TestResult.collector,
                rFinaliseMethod: TestResult.finalise,
                rFinaliseData: [er3])

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

//        println "Result is ${compDetails.toString()}"

        def stageOps = [TestData.func1, TestData.func2, TestData.func3]

        def pipeModifiers = [[[0], [0], [0]], [[0], [0], [0]], [[0], [0], [0]]]

        def gopC = new GroupOfPipelineCollectPattern(
        groups: workers,
        stages: stages,
        eDetails: emitterDetails,
        cDetails: compDetails,
        rDetails: resultDetails,
        stageOp: stageOps,
        stageModifier : pipeModifiers)

        gopC.run()

        er.finalSum = er1.finalSum + er2.finalSum + er3.finalSum
        er.dataSetCount = er1.dataSetCount + er2.dataSetCount + er3.dataSetCount
        er.finalInstance = Math.max(er1.finalInstance, er2.finalInstance)
        er.finalInstance = Math.max(er.finalInstance, er3.finalInstance)
        er.maxClone = Math.max(er1.maxClone, er2.maxClone)
        er.maxClone = Math.max(er.maxClone, er3.maxClone)
        er.w1 = er1.w1 + er2.w1 + er3.w1
        er.w2 = er1.w2 + er2.w2 + er3.w2
        er.w3 = er1.w3 + er2.w3 + er3.w3
        er.cloneOrder = er1.cloneOrder + er2.cloneOrder +er3.cloneOrder

        println "47: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 210)
        assertTrue (er.w2 == 210)
        assertTrue (er.w3 == 210)
    }
}
