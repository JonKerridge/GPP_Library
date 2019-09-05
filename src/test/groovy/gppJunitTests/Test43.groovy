package gppJunitTests

import GPP_Library.DataDetails
import GPP_Library.ResultDetails
import GPP_Library.connectors.reducers.AnyFanOne
import GPP_Library.connectors.spreaders.OneFanAny
import GPP_Library.functionals.groups.AnyGroupAny
import GPP_Library.patterns.DataParallelPattern
import GPP_Library.terminals.Collect
import GPP_Library.terminals.Emit
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
