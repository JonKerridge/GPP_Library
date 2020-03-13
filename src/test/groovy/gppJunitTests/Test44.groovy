package gppJunitTests

import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.LocalDetails
import groovyParallelPatterns.PipelineDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.functionals.pipelines.OnePipelineOne
import groovyParallelPatterns.patterns.TaskParallelPattern
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import TestDataDefs.TestWorker
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test44 {

  @Test
  public void test() {
    def stages = 3

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

    def tpp = new TaskParallelPattern(
        eDetails: emitterDetails,
        stages: stages,
        stageOp: [TestData.func1, TestData.func2, TestData.func3],
        stageModifier: [[0], [0], [0]],
        pDetails: pipeDetails,
        rDetails: resultDetails
    )

    tpp.run()

    println "44: $er"

    assertTrue (er.finalSum == 210)
    assertTrue (er.dataSetCount == 20)
    assertTrue (er.finalInstance == 20)
    assertTrue (er.maxClone == 0)
    assertTrue (er.w1 == 410)
    assertTrue (er.w2 == 2210)
    assertTrue (er.w3 == 4210)
  }
}
