package GPP_Library.terminals.GUIsupport

import groovy.transform.CompileStatic
import groovyJCSP.*
import jcsp.awt.*
import jcsp.lang.*

import java.awt.*

@CompileStatic
class GUInterface implements CSProcess {
	
	ActiveCanvas canvas
	int width
	int height
	
	void run(){
		def root = new ActiveClosingFrame ("GPP Canvas Interface")
		def main = root.getActiveFrame()
		canvas.setSize(width, height)
		main.setLayout(new BorderLayout())
		main.add(canvas, BorderLayout.CENTER)
		main.pack()
		main.setVisible(true)
		new PAR([root]).run()
	}
	// this process does NOT terminate	
}
