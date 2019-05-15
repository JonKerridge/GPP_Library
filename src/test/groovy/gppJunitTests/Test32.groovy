package gppJunitTests

import GPP_Library.CompositeDetails
import GPP_Library.DataDetails
import GPP_Library.ResultDetails
import GPP_Library.connectors.reducers.AnyFanOne
import GPP_Library.connectors.spreaders.OneFanAny
import GPP_Library.functionals.composites.AnyGroupOfPipelineCollects
import GPP_Library.functionals.composites.AnyGroupOfPipelines
import GPP_Library.terminals.Collect
import GPP_Library.terminals.Emit
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import TestDataDefs.TestWorker
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test32 {

  @Test
  public void test() {
    def stages = 3
    def workers = 3

    def chan1 = Channel.one2one()
    def chan2 = Channel.one2any()
    def chan3 = Channel.any2one()
    def chan4 = Channel.any2one()


    def er = new TestExtract()
    def er1 = new TestExtract()
    def er2 = new TestExtract()
    def er3 = new TestExtract()

    def emitterDetails = new DataDetails(dName: TestData.getName() ,
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.create)

    def resultDetails =
        new ResultDetails(rName: TestResult.getName(),
            rInitMethod: TestResult.init,
            rCollectMethod: TestResult.collector,
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

//        println "Result is ${compDetails.toString()}"

    def stageOps = [TestData.func1, TestData.func2, TestData.func3]

    def pipeModifiers = [[[0], [0], [0]], [[0], [0], [0]], [[0], [0], [0]]]

    def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails )

    def ofa = new OneFanAny( input: chan1.in(),
        outputAny: chan2.out(),
        destinations: workers)

    def gop = new AnyGroupOfPipelines(groups: workers,
        stages: stages,
        inputAny: chan2.in(),
        outputAny: chan3.out(),
        cDetails: compDetails,
        stageOp: stageOps,
        stageModifier : pipeModifiers)

    def afo = new AnyFanOne ( inputAny: chan3.in(),
        output: chan4.out(),
        sources: workers)

    def collector = new Collect( input: chan4.in(),
        rDetails: resultDetails)



    PAR testParallel = new PAR([emitter, ofa, gop, afo, collector])
    testParallel.run()
    testParallel.removeAllProcesses()


    println "32: $er"

    assertTrue (er.finalSum == 210)
    assertTrue (er.dataSetCount == 20)
    assertTrue (er.finalInstance == 20)
    assertTrue (er.maxClone == 0)
    assertTrue (er.w1 == 210)
    assertTrue (er.w2 == 210)
    assertTrue (er.w3 == 210)
  }
}
