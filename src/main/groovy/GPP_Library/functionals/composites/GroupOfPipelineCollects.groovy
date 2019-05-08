package GPP_Library.functionals.composites

import GPP_Library.CompositeDetails
import GPP_Library.ResultDetails
import GPP_Library.functionals.pipelines.OnePipelineCollect
import groovy.transform.CompileStatic
import groovyJCSP.PAR
import jcsp.lang.*

/**
 * A GroupOfPipelineCollects comprises a network of groups parallel occurrences and each pipeline comprises stages
 * Worker processes, excluding the final Collect stage.  Inputs from the preceding process are assumed
 * to come from the any end of a channel.
 *
 * @param inputAny the any end of channel used to read objects into the network created by GroupOfPipelineCollects
 * @param stages the number of stages in each pipeline excluding the Collect process
 * @param groups the number of parallel pipelines in the network
 * @param stageOp a list of function identifiers to be associated with each stage of the pipeline
 * @param stageModifier a list of groups lists, each containing stages elements that are the
 * 			stage modifier data for that combination of group and stage. Each entry could itself be a list of values.
 * @param outData a list of groups lists each entry of which comprises stages entries.  Each entry is a boolean value
 * 			such that if true the worker processes in that stage
 * 			will output each processed input object. If false the process will output
 * 			the workerClass once only, after it has processed all the input data objects. If omitted the value defaults to true.
 * @param cDetails A {@link GPP_Library.CompositeDetails} object defining the object that defines each of the stages and groups
 * @param rDetails A list of {@link GPP_Library.ResultDetails} object defining the result class used by each Collect process in the group
 * @param logPhaseName an optional list of string values, which if specified indicates that the processes in the Pipeline should be logged
 * otherwise the process will not be logged.  Specific stages in the Pipeline can be logged by specifying a string value, otherwise the value must
 * be an empty string
 * @param logPropertyName the name of a property in the input object that will uniquely identify an instance of the object.
 * LogPropertyName must be specified if logPhaseName is specified
 * @param visLogChan the output end of an any2one channel to which log data will be sent to an instance of the LoggingVisualiser
 * process running in parallel with the application network.  If not specified then it is assumed that no visualiser process is running.
 *
 * @see GPP_Library.functionals.pipelines.OnePipelineCollect
 */
@CompileStatic
class GroupOfPipelineCollects implements CSProcess {
	ChannelInput inputAny
	int stages = -1
	int groups = 2
	List <List <Boolean> > outData = null  //list of lists one set per group then one boolean entry per stage
    List <String> stageOp = null
	List stageModifier = null
	CompositeDetails cDetails = null
	List <ResultDetails> rDetails

    List <String> logPhaseNames = null	// includes Collect stage
	String logPropertyName = ""
	ChannelOutput visLogChan = null

	void run() {
        int rSize = rDetails.size()
        int opSize = stageOp.size()
        assert (stageOp != null): "Group of Pipeline Collects: stageOp MUST be specified, one for each stage of the pipeline"
        assert stages > 0 : "Group of Pipeline Collects: value of stages not specified, $stages"
        assert rSize == groups : "Group of Pipeline Collects: size of rDetails, $rSize, not equal to number of groups, $groups"
        assert opSize == stages : "Group of Pipeline Collects: size of stageOp, $opSize, not equal to number of stages, $stages"
        if ( cDetails != null){
            int cgSize = cDetails.cDetails.size() // number of groups
            int csSize = cDetails.cDetails[0].size() // number of stages
            assert cgSize == groups : "Group of Pipeline Collects:  number of groups in cDetails, cgSize, not equal number of groups, $groups"
            assert csSize == stages : "Group of Pipeline Collects:  number of stages in cDetails, csSize, not equal number of groups, $stages"
        }
        List <List <String> >  logNames = []
		if (logPhaseNames != null) {
            assert logPhaseNames.size() == stages+1 : "Group of Pipeline Collects: " +
                    "logPhaseNames wrong size should be ${stages + 1}, currently $logPhaseNames"
            for ( g in 0 ..< groups){
				List <String> phaseNames = (0 .. stages).collect{s ->	return (String)"$g, "  + logPhaseNames[s]}
                logNames[g] = phaseNames
			}
		}
		else {
            List <String> phaseNames = (0 .. stages).collect{s -> return ""}
            logNames[0] = phaseNames
        }
		if (outData == null) {
			outData = []
			for ( g in 0 ..< groups){
				List <Boolean> gList = (0..<stages).collect{i -> return true}
				outData << gList
			}
		}
		def network = (0 ..< groups).collect { g ->
			new OnePipelineCollect(input: inputAny,
								stages: stages,
								pDetails: cDetails == null ? null : cDetails.extractByPipe(g),
								rDetails: rDetails[g],
								stageOp: stageOp,
								stageModifier: stageModifier == null ? null : (List)stageModifier[g],
                                outData: outData[g],
								logPhaseNames: logPhaseNames == null ?  logNames[0] : (List) logNames[g],
								logPropertyName: logPropertyName,
                                visLogChan: visLogChan)
		}
		new PAR (network).run()
	}

}
