package gppJunitTests

import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.GroupDetails
import groovyParallelPatterns.LocalDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.connectors.reducers.*
import groovyParallelPatterns.connectors.spreaders.*
import groovyParallelPatterns.functionals.groups.*
import groovyParallelPatterns.terminals.*
import groovyJCSP.*
import jcsp.lang.*
import org.junit.Test

import static org.junit.Assert.assertTrue
import TestDataDefs.*

class Test20 {

    @Test
    public void test() {
        def workers = 3

        def chan1 = Channel.one2one()
        def chan2 = Channel.one2one()
        def any1 = Channel.one2any()
        def any2 = Channel.any2one()
        def emitList = Channel.one2oneArray(workers)
        def inEmitList = new ChannelInputList(emitList)
        def outEmitList = new ChannelOutputList(emitList)
        def collectList = Channel.one2oneArray(workers)
        def inCollectList = new ChannelInputList(collectList)
        def outCollectList = new ChannelOutputList(collectList)
        def cList1 = Channel.one2oneArray(workers)
        def cList2 = Channel.one2oneArray(workers)
        def inCList1 = new ChannelInputList(cList1)
        def inCList2 = new ChannelInputList(cList2)
        def outCList1 = new ChannelOutputList(cList1)
        def outCList2 = new ChannelOutputList(cList2)

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

        def group1Details  = new GroupDetails( workers : workers,
        groupDetails: new LocalDetails[workers])
        def group2Details  = new GroupDetails( workers : workers,
        groupDetails: new LocalDetails[workers])
        def group3Details  = new GroupDetails( workers : workers,
        groupDetails: new LocalDetails[workers])

        for ( w in 0..< workers){
            group1Details.groupDetails[w] = new LocalDetails( lName: TestWorker.getName(),
            lInitMethod: TestWorker.init,
            lFinaliseMethod: TestWorker.finalise )
            group2Details.groupDetails[w] = new LocalDetails( lName: TestWorker.getName(),
            lInitMethod: TestWorker.init,
            lFinaliseMethod: TestWorker.finalise )
            group3Details.groupDetails[w] = new LocalDetails( lName: TestWorker.getName(),
            lInitMethod: TestWorker.init,
            lFinaliseMethod: TestWorker.finalise )
        }
        group1Details.groupDetails[0].lInitData = [100, 0]
        group1Details.groupDetails[1].lInitData = [100, 0]
        group1Details.groupDetails[2].lInitData = [100, 0]

        group2Details.groupDetails[0].lInitData = [100, 0]
        group2Details.groupDetails[1].lInitData = [100, 0]
        group2Details.groupDetails[2].lInitData = [100, 0]

        group3Details.groupDetails[0].lInitData = [100, 0]
        group3Details.groupDetails[1].lInitData = [100, 0]
        group3Details.groupDetails[2].lInitData = [100, 0]


        def emitter = new Emit( output: chan1.out(),
        eDetails: emitterDetails )

        def outFan = new OneFanList (input: chan1.in(),
        outputList: outEmitList)

        def stage1 = new ListGroupList(inputList: inEmitList,
        outputList : outCList1,
        gDetails: group1Details,
        function: TestData.func1,
        modifier: m1,
        workers: workers)

        def stage2 = new ListGroupList(inputList: inCList1,
        outputList: outCList2,
        gDetails: group2Details,
        function: TestData.func2,
        modifier: m2,
        workers: workers)

        def stage3 = new ListGroupAny(inputList: inCList2,
        outputAny: any2.out(),
        gDetails: group3Details,
        function: TestData.func3,
        modifier: m3,
        workers: workers)

        def inFan = new AnyFanOne( inputAny: any2.in(),
        output: chan2.out(),
        sources: workers)


        def collector = new Collect( input: chan2.in(),
        rDetails: resultDetails)

        PAR testParallel = new PAR([emitter, inFan, stage1, stage2, stage3, outFan, collector])
        testParallel.run()
        testParallel.removeAllProcesses()

        println "20: $er"

        assertTrue (er.finalSum == 210)
        assertTrue (er.dataSetCount == 20)
        assertTrue (er.finalInstance == 20)
        assertTrue (er.maxClone == 0)
        assertTrue (er.w1 == 210)
        assertTrue (er.w2 == 2210)
        assertTrue (er.w3 == 20210)
    }
}
