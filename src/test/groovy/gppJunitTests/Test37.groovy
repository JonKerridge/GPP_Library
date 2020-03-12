package gppJunitTests

import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.connectors.reducers.*
import groovyParallelPatterns.cluster. connectors.*
import groovyParallelPatterns.functionals.groups.AnyGroupAny
import groovyParallelPatterns.terminals.*
import TestDataDefs.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue

class Test37 {

  @Test
  void test() {

    int clusters = 2
    int workers = 3
    def chan1 = Channel.one2one()
    def chan2 = Channel.one2any()
    def chan3 = Channel.any2any()
    def chan4 = Channel.one2any()
    def chan5 = Channel.any2any()
    def chan6 = Channel.any2one()
    def chan7 = Channel.one2one()
    def request = Channel.one2oneArray(clusters)
    def response = Channel.one2oneArray(clusters)
    def requestInList = new ChannelInputList(request)
    def responseOutList = new ChannelOutputList(response)

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

    def rfa0 = new NodeRequestingFanAny(request: request[0].out(),
        response: response[0].in(),
        outputAny: chan2.out(),
        destinations: workers)

    def rfa1 = new NodeRequestingFanAny(request: request[1].out(),
        response: response[1].in(),
        outputAny: chan4.out(),
        destinations: workers)

    def aga0 = new AnyGroupAny(
        inputAny: chan2.in(),
        outputAny: chan3.out(),
        function: SerialisedTestData.nullFunc,
        workers: workers
    )

    def aga1 = new AnyGroupAny(
        inputAny: chan4.in(),
        outputAny: chan5.out(),
        function: SerialisedTestData.nullFunc,
        workers: workers
    )

    def afo0 = new AnyFanOne(
        inputAny: chan3.in(),
        output: chan6.out(),
        sources: workers
    )

    def afo1 = new AnyFanOne(
        inputAny: chan5.in(),
        output: chan6.out(),
        sources: workers
    )

    def afo = new AnyFanOne(inputAny: chan6.in(),
        output: chan7.out(),
        sources: clusters)

    def collector = new Collect( input: chan7.in(),
        rDetails: resultDetails)

    PAR testParallel = new PAR([emitter, onrl, rfa1, rfa0, aga0, aga1, afo, afo1, afo0, collector])
    testParallel.run()
    testParallel.removeAllProcesses()

    println "37: $er"

    assertTrue (er.finalSum == 210)
    assertTrue (er.dataSetCount == 20)
    assertTrue (er.finalInstance == 20)
    assertTrue (er.maxClone == 0)
  }
}


