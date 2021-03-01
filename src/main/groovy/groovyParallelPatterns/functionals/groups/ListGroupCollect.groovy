package groovyParallelPatterns.functionals.groups

import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovy.transform.CompileStatic
import groovyJCSP.ChannelInputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

/**
 *
 * A ListGroupCollect is a Group with internal Collect processes rather than a Worker.
 * This has the effect of running all the Collect processes in parallel.  It is assumed
 * that any results are fully processed by the GroupCollect as there are no output channels
 * connected to the processes.
 * <p>
 * It is assumed that it is not possible to write to the same (or part of a) data object
 * in more than ONE of the Collectors.  In other words parallel access to a data object
 * is not permitted for write operations where the operation does not have exclusive
 * access to the data object.  This is a requirement but is not checked by the system.
 * There is no synchronisation between the Collectors in the group.
 * <p>
 * @param inputList A ChannelInputList with as many channels as the value of collectors.
 * 					Each Collect process reads from just one element of the input.
 * @param rDetails A list of {@link groovyParallelPatterns.ResultDetails} object defining the
 * result class used by each Collect process in the group
 * @param collectors The number of Collect processes that will be created when the Group is run
 *
 * @param logPhaseName an optional string property, which if specified indicates that the process
 * should be logged otherwise the process will not be logged
 * @param logPropertyName the name of a property in the input object that will uniquely identify
 * an instance of the object. LogPropertyName must be specified if logPhaseName is specified
 *
 * @param visLogChan the output end of an any2one channel to which log data will be sent to an
 * instance of the LoggingVisualiser process running in parallel with the application network.
 * If not specified then it is assumed that no visualiser process is running.
 * This channel will be created automatically by GPP_Builder.
 *
 *
 *
 * @see groovyParallelPatterns.terminals.Collect
 */

@CompileStatic
class ListGroupCollect implements CSProcess{

  ChannelInputList inputList
  List <ResultDetails> rDetails
  int collectors = 0

  String logPhaseName = ""
  String logPropertyName = ""
  ChannelOutput visLogChan = null

  void run() {
    assert rDetails != null: "ListGroupCollect: rDetails not specified"
    assert collectors > 0: "ListGroupCollect: collectors not specified"
    int inSize = inputList.size()
    int rSize = rDetails.size()
    assert inSize == collectors : "ListGroupCollect: inputList size, $inSize, does not equal the number of collectors $collectors"
    assert rSize == collectors : "ListGroupCollect: rDetails size, $rSize, does not equal the number of collectors $collectors"
    List network = (0 ..< collectors).collect { e ->
      new Collect ( input: (ChannelInput)inputList[e],
          rDetails: rDetails[e],
          logPhaseName: logPhaseName == "" ?  "" : (String)"$e, "  + logPhaseName ,
          logPropertyName: logPropertyName,
          visLogChan: visLogChan)
    }
    new PAR (network).run()

  }

}
