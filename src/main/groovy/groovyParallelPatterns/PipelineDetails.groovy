package groovyParallelPatterns

import groovy.transform.CompileStatic

/**
 * PipelineDetails holds details for each stage in a Pipeline process.  It is only required
 * if any of the Worker processes in the Pipeline use a local class to undertake the processing
 *
 * @param stages An integer holding the number of stages in the Pipeline
 * @param stageDetails an array of {@code stages} elements each holding the {@link groovyParallelPatterns.LocalDetails} for each Worker process
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
