package GPP_Library

import groovy.transform.CompileStatic

/**
 * PipelineDetails holds details for each stage in a Pipeline process.
 *
 * @param stages An int holding the number of stages in the Pipeline
 * @param stageDetails an array of stages elements each holding the {@link GPP_Library.LocalDetails} for each Worker process
 * in the Pipeline
 *
 */
@CompileStatic
class PipelineDetails implements Serializable, Cloneable {
	int stages = 2
	List <LocalDetails> stageDetails = null //one local detail per stage

	String toString(){
		String s = "PipelineDetails: stages $stages \n"
		stageDetails.each{ ld ->
			s = s + "${ld.toString()}\n"
		}
		return s
	}
}
