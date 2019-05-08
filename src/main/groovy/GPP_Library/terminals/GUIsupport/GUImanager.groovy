package GPP_Library.terminals.GUIsupport

import GPP_Library.*
import groovy.transform.CompileStatic
import jcsp.awt.*
import jcsp.lang.*

@CompileStatic
class GUImanager implements CSProcess {

    ChannelInput input
    DisplayList dList
    ResultDetails guiDetails
//	def resultsClass
//	List finaliseData = null

    void run(){
        def guiClass = Class.forName(guiDetails.rName)
        def rc = guiClass.newInstance()
        int retCode = rc.&"${guiDetails.rInitMethod}"( guiDetails.rInitData << dList )
        // can do the timing from here
        def startime = System.currentTimeMillis()
        def inputObject = input.read()
        while (!(inputObject instanceof UniversalTerminator)){
            retCode = rc.&"${guiDetails.rCollectMethod}"( inputObject, dList )
            if (retCode == DataClassInterface.completedOK )
                inputObject = input.read()
            else
                GPP_Library.DataClass.unexpectedReturnCode("GUImanager: error while displaying", retCode)
        }
        retCode = rc.&"${guiDetails.rFinaliseMethod}"( guiDetails.rFinaliseData )
        if (retCode != DataClassInterface.completedOK)
            GPP_Library.DataClass.unexpectedReturnCode("GUImanager: error while finalising", retCode)
        // print out the elapsed time
        def endtime = System.currentTimeMillis()
        def elapsedTime = endtime - startime
        println "Time taken = ${elapsedTime} milliseconds"
    }

}
