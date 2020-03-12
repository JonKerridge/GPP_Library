package gppJunitTests

import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.connectors.reducers.AnyFanOne
import groovyParallelPatterns.connectors.spreaders.OneFanAny
import groovyParallelPatterns.functionals.groups.AnyGroupAny
import groovyParallelPatterns.patterns.DataParallelPattern
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test43 {

  @Test
  public void test() {
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

    def dpp = new DataParallelPattern(
        eDetails: emitterDetails,
        workers: 3,
        function: TestData.nullFunc,
        rDetails: resultDetails    )

    dpp.run()

    println "43: $er"

    assertTrue (er.finalSum == 210)
    assertTrue (er.dataSetCount == 20)
    assertTrue (er.finalInstance == 20)
  }

}
