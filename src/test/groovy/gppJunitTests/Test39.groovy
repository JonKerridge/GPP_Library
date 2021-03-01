package gppJunitTests

import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.cluster.connectors.NodeRequestingParCastList
import groovyParallelPatterns.cluster.connectors.OneNodeRequestedList
import groovyParallelPatterns.connectors.reducers.AnyFanOne
import groovyParallelPatterns.connectors.reducers.ListFanOne
import groovyParallelPatterns.functionals.groups.ListGroupList
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import TestDataDefs.SerialisedTestData
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test39 {

  @Test
  void test() {

    int clusters = 2
    int workers = 3
    def chan1 = Channel.one2one()
    def chan2 = Channel.one2oneArray(workers)
    def chan3 = Channel.one2oneArray(workers)
    def chan4 = Channel.one2oneArray(workers)
    def chan5 = Channel.one2oneArray(workers)
    def chan6 = Channel.any2one()
    def chan7 = Channel.one2one()
    def request = Channel.one2oneArray(clusters)
    def response = Channel.one2oneArray(clusters)
    def requestInList = new ChannelInputList(request)
    def responseOutList = new ChannelOutputList(response)

    def chan2InList = new ChannelInputList(chan2)
    def chan2OutList = new ChannelOutputList(chan2)
    def chan3InList = new ChannelInputList(chan3)
    def chan3OutList = new ChannelOutputList(chan3)
    def chan4InList = new ChannelInputList(chan4)
    def chan4OutList = new ChannelOutputList(chan4)
    def chan5InList = new ChannelInputList(chan5)
    def chan5OutList = new ChannelOutputList(chan5)

    def er = new TestExtract()

    def emitterDetails = new DataDetails(dName: TestData.getName(),
        dInitMethod: TestData.totalInitialise,
        dInitData: [20],
        dCreateMethod: TestData.create)

    def resultDetails = new ResultDetails(rName: TestResult.getName(),
        rInitMethod: TestResult.init,
        rCollectMethod: TestResult.collector,
        rFinaliseMethod: TestResult.finalise,
        rFinaliseData: [er])

    def emitter = new Emit(output: chan1.out(),
        eDetails: emitterDetails)

    def onrl = new OneNodeRequestedList(input: chan1.in(),
        request: requestInList,
        response: responseOutList)

    def rfl0 = new NodeRequestingParCastList(request: request[0].out(),
        response: response[0].in(),
        id: 0,
        outList: chan2OutList)

    def rfl1 = new NodeRequestingParCastList(request: request[1].out(),
        response: response[1].in(),
        id: 1,
        outList: chan4OutList)

    def lgl0 = new ListGroupList(inputList: chan2InList,
        outputList: chan3OutList,
        function: SerialisedTestData.nullFunc,
        workers: workers)

    def lgl1 = new ListGroupList(inputList: chan4InList,
        outputList: chan5OutList,
        function: SerialisedTestData.nullFunc,
        workers: workers)

    def lfo0 = new ListFanOne(inputList: chan3InList,
        output: chan6.out())

    def lfo1 = new ListFanOne(inputList: chan5InList,
        output: chan6.out())

    def afo = new AnyFanOne(inputAny: chan6.in(),
        output: chan7.out(),
        sources: clusters)

    def collector = new Collect(input: chan7.in(),
        rDetails: resultDetails)

    PAR testParallel = new PAR([emitter, onrl, rfl1, rfl0, lgl0, lgl1, afo, lfo1, lfo0, collector])
    testParallel.run()
    testParallel.removeAllProcesses()

    println "39: $er"

    assertTrue(er.finalSum == 630)
    assertTrue(er.dataSetCount == 60)
    assertTrue(er.finalInstance == 20)
//    assertTrue(er.maxClone == 59)  // clone is unaltered because SerialisedTD does not increment it
  }
}


