package gppJunitTests

import groovy_parallel_patterns.CompositeDetails
import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.patterns.PipelineOfGroupsPattern
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import TestDataDefs.TestWorker
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test46 {

  @Test
  void test() {
    def stages = 3
    def workers = 3

    def er = new TestExtract()

    def emitterDetails = new DataDetails(dName: TestData.getName(),
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.create)

    def resultDetails =
        new ResultDetails(rName: TestResult.getName(),
            rInitMethod: TestResult.init,
            rCollectMethod: TestResult.collector,
            rFinaliseMethod: TestResult.finalise,
            rFinaliseData: [er])


    List[][] initData = new List[workers][stages]
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
    for (w in 0..<workers) for (s in 0..<stages) compDetails.insertCompositeDetails(w,
        s,
        wName,
        initMethod,
        initData[w][s],
        finaliseMethod,
        finalData)

    //println "${compDetails.toString()}"

    def stageOps = [TestData.func1, TestData.func2, TestData.func3]

    def pipeModifiers = [[[0], [0], [0]], [[0], [0], [0]], [[0], [0], [0]]]

    def pogP = new PipelineOfGroupsPattern(
        eDetails: emitterDetails,
        workers: workers,
        stages: stages,
        cDetails: compDetails,
        stageOp: stageOps,
        stageModifier: pipeModifiers,
        rDetails: resultDetails
    )

    pogP.run()

    println "46: $er"

    assertTrue(er.finalSum == 210)
    assertTrue(er.dataSetCount == 20)
    assertTrue(er.finalInstance == 20)
    assertTrue(er.maxClone == 0)
    assertTrue(er.w1 == 210)
    assertTrue(er.w2 == 210)
    assertTrue(er.w3 == 210)
  }
}
