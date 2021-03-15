package gppJunitTests

import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.connectors.reducers.AnyFanOne
import groovy_parallel_patterns.connectors.spreaders.OneFanAny
import groovy_parallel_patterns.functionals.groups.AnyGroupAny
import groovy_parallel_patterns.patterns.DataParallelPattern
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import groovy_jcsp.PAR
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
