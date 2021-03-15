package groovy_parallel_patterns.patterns

import groovy_parallel_patterns.DataDetails
import groovy_parallel_patterns.GroupDetails
import groovy_parallel_patterns.ResultDetails
import groovy_parallel_patterns.connectors.reducers.AnyFanOne
import groovy_parallel_patterns.connectors.spreaders.OneFanAny
import groovy_parallel_patterns.functionals.groups.AnyGroupAny
import groovy_parallel_patterns.terminals.Collect
import groovy_parallel_patterns.terminals.Emit
import groovy.transform.CompileStatic
import groovy_jcsp.PAR
import jcsp.lang.Channel

/**
 * A DataParallelPattern comprises a sequence of processes in a so-called Farm.  The sequence comprises
 * Emit; OneFanAny; AnyGroupAny; AnyFanOne; Collect. The properties of the pattern provide all the
 * external values required to run the pattern.  The internal channels required to
 * connect the processes together are all declared within the class outwith programmer concerns.
 * <p>
 * @param eDetails A {@link groovy_parallel_patterns.DataDetails}  object containing information concerning the
 * DataClass used by the Emit process, it MUST be specified.
 * @param gDetails A {@link groovy_parallel_patterns.GroupDetails}  object containing information concerning
 * the contained group of workers used by the AnyGroupAny process specifying any local worker classes.
 * @param function A String value identifying the operation to be undertaken by the Worker processes
 * in AnyGroupAny
 * @param modifier Contains a possible modifier for the operation, with each Worker
 * 					accessing the element that corresponds to the index of the Worker.
 * @param rDetails A {@link groovy_parallel_patterns.ResultDetails} object containing data pertaining to the
 * result class used by the Collect process, it MUST be specified.
 * @param workers The number of Worker processes that will be created when the Group is run
 * @param outData If true the process will output each processed input object. If false the process
 * will output the workerClass once only, after it has processed all the input data objects.
 * The output only happens after the finalise method has been called. outData defaults to true.
 *
 */

@CompileStatic
class DataParallelPattern {

  DataDetails eDetails = null
  int workers = 0
  GroupDetails gDetails = null
  String function = ""
  List modifier = null
  ResultDetails rDetails = null
  boolean outData = true


  def run = {
    assert (eDetails != null) : "DataParallelPattern: eDetails must be specified"
    assert (rDetails != null) : "DataParallelPattern: rDetails must be specified"
    assert (function != "") : "DataParallelPattern: function must be specified"
    assert (workers != 0) : "DataParallelPattern: workers must be specified"
    def toFanOut = Channel.one2one()
    def toFarm = Channel.one2any()
    def fromFarm =Channel.any2one()
    def collectChan = Channel.one2one()

    def emitter = new Emit( output: toFanOut.out(),
        eDetails: eDetails)

    def fanOut = new OneFanAny(input: toFanOut.in(),
        outputAny: toFarm.out(),
        destinations: workers)

    def farmer = new AnyGroupAny ( inputAny: toFarm.in(),
        outputAny: fromFarm.out(),
        gDetails : gDetails,
        workers: workers,
        function: function,
        modifier: modifier,
        outData: outData )

    def fanIn = new AnyFanOne(inputAny: fromFarm.in(),
        output: collectChan.out(),
        sources: workers)

    def collector = new Collect( input: collectChan.in(),
        rDetails: rDetails )

    new PAR([emitter, fanOut, farmer, fanIn, collector]).run()
  }
}
