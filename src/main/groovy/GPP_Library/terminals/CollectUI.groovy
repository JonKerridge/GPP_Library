package GPP_Library.terminals

import GPP_Library.*
import GPP_Library.terminals.GUIsupport.*
import groovyJCSP.*
import jcsp.awt.*
import jcsp.lang.*

/**
 * The CollectUI process provides a means of displaying  a graphical
 * representation of the result of a calculation.  It uses two further
 * internal processes that provide the required parallel functionality
 * of the interface.  The process utilises the ActiveCanvas process 
 * contained in jcsp.awt and the associated DisplayList and GraphicsCommands.
 * <pre>
 * <b>Methods required by class resultClassName:</b>
 *     initClass( initialData )
 *     updateDisplayList( inputObject )
 *     finalise( finaliseData )
 *     
 * <b>Behaviour:</b>  (implemented in GUImanager)
 *     resultsClass.initClass(initData)
 *     o = input.read()  
 *     while ( o != UniversalTerminator )
 *         resultClass.updateDisplayList(o)
 *         o = input.read()
 *     resultsClass.finalise(finaliseData)    
 * </pre>
 * 	<p>	
 * 
 * @param input the channel from which incoming data objects are read
 * @param guiDetails A {@link GPP_Library.ResultDetails}  object specifying the result object in which
 * the rCollectmethod updates a DisplayList. It MUST be specified
 * <p>
 * The canvas window does not close
 * and thus the process network does not terminate.  It has to be closed by
 * closing the Canvas Window.  As such the normal means of timing a network does
 * not work so the user interface processes do the timing to provide an approximation
 * of the length of time.  The idea of timing something that includes a display is,
 * in any case, somewhat dubious as the time will be greatly influenced by the 
 * slowness of the display mechanism itself.
 */

class CollectUI implements CSProcess {
	
	ChannelInput input
	ResultDetails guiDetails

	void run(){
		DisplayList dList = new DisplayList()
		ActiveCanvas canvas = new ActiveCanvas()
		canvas.setPaintable(dList)	
		def manager = new GUImanager(input: input,
									  guiDetails: guiDetails,
									  dList: dList)
		def gui = new GUInterface(canvas: canvas,
									width: guiDetails.rInitData[0],
									height: guiDetails.rInitData[1])
		new PAR([manager, gui]). run()
	}

}
