package gppJunitTests

import TestDataDefs.SerialisedTestData
import TestDataDefs.TestData
import TestDataDefs.TestExtract
import TestDataDefs.TestResult
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.cluster.connectors.List2Net
import groovyParallelPatterns.cluster.connectors.NetInputs2List
import groovyParallelPatterns.cluster.connectors.NodeRequestingFanList
import groovyParallelPatterns.cluster.connectors.OneNodeRequestedList
import groovyParallelPatterns.connectors.reducers.AnyFanOne
import groovyParallelPatterns.connectors.reducers.ListFanOne
import groovyParallelPatterns.functionals.groups.ListGroupList
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test38a {

  @Test
  void test() {

    int clusters = 2  // with two such clusters
    int workers = 3
    def chan1 = Channel.one2one()
    def chan2 = Channel.one2oneArray(workers)
    def chan3 = Channel.one2oneArray(workers)
    def chan4 = Channel.one2oneArray(workers)
    def chan5 = Channel.one2oneArray(workers)

    def chan6 = Channel.one2oneArray(workers)
    def chan7 = Channel.one2oneArray(workers)
    def chan8 = Channel.one2oneArray(workers)
    def chan9 = Channel.one2oneArray(workers)
    def chan10 = Channel.one2oneArray(workers)
    def chan11 = Channel.one2oneArray(workers)

    def chan12 = Channel.any2one()
    def chan13 = Channel.one2one()
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
    def chan6InList = new ChannelInputList(chan6)
    def chan6OutList = new ChannelOutputList(chan6)
    def chan7InList = new ChannelInputList(chan7)
    def chan7OutList = new ChannelOutputList(chan7)
    def chan8InList = new ChannelInputList(chan8)
    def chan8OutList = new ChannelOutputList(chan8)
    def chan9InList = new ChannelInputList(chan9)
    def chan9OutList = new ChannelOutputList(chan9)
    def chan10InList = new ChannelInputList(chan10)
    def chan10OutList = new ChannelOutputList(chan10)
    def chan11InList = new ChannelInputList(chan11)
    def chan11OutList = new ChannelOutputList(chan11)

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

    def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails )

    def onrl = new OneNodeRequestedList(input: chan1.in(),
        request: requestInList,
        response: responseOutList )

    def rfl0 = new NodeRequestingFanList(request: request[0].out(),
        response: response[0].in(),
        outList: chan2OutList)

    def rfl1 = new NodeRequestingFanList(request: request[1].out(),
        response: response[1].in(),
        outList: chan4OutList)

    def lgl0 = new ListGroupList(
        inputList: chan2InList,
        outputList: chan3OutList,
        function: SerialisedTestData.nullFunc,
        workers: workers
    )

    def lgl1 = new ListGroupList(
        inputList: chan4InList,
        outputList: chan5OutList,
        function: SerialisedTestData.nullFunc,
        workers: workers
    )

    def ln0 = new List2Net(
        inputList: chan3InList,
        outputList: chan7OutList
    )

    def ln1 = new List2Net(
        inputList: chan5InList,
        outputList: chan6OutList
    )

    def nl0 = new NetInputs2List(
        inputList: chan7InList,
        outputList: chan10OutList
    )

    def nl1 = new NetInputs2List(
        inputList: chan6InList,
        outputList: chan8OutList
    )

    def lgl3 = new ListGroupList(
        inputList: chan10InList,
        outputList: chan11OutList,
        function: SerialisedTestData.nullFunc,
        workers: workers
    )

    def lgl2 = new ListGroupList(
        inputList: chan8InList,
        outputList: chan9OutList,
        function: SerialisedTestData.nullFunc,
        workers: workers
    )


    def lfo0 = new ListFanOne(
        inputList: chan9InList,
        output: chan12.out()
    )

    def lfo1 = new ListFanOne(
        inputList: chan11InList,
        output: chan12.out()
    )

    def afo = new AnyFanOne(inputAny: chan12.in(),
        output: chan13.out(),
        sources: clusters)

    def collector = new Collect( input: chan13.in(),
        rDetails: resultDetails)

    PAR testParallel = new PAR([emitter, onrl, rfl1, rfl0, lgl0, lgl1,
                                ln0, ln1,nl0, nl1, lgl2, lgl3,
                                lfo1, lfo0, afo, collector])
    testParallel.run()
    testParallel.removeAllProcesses()

    println "38a: $er"

    assertTrue (er.finalSum == 210)
    assertTrue (er.dataSetCount == 20)
    assertTrue (er.finalInstance == 20)
    assertTrue (er.maxClone == 0)
  }
}


