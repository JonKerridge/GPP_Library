The Groovy Parallel Patterns Library (groovy_parallel_patterns)
creates a number of re-usable parallel design patterns
based on the use of Communicating Process Architectures (CPA).<p>

Typically, CPA use communications concepts as described by Hoare
in his 1978 ACM Paper entitled Communicating Sequential Processes (CSP).
The great advantage of this theory is that we are able to build parallel
system about which we can reason and more specifically can analyse a
processes network for deadlock and livelock freedom BEFORE it is executed.
In addition, we can refine one parallel solution into another one and then
reason that the behaviour of both designs is the same.<p>

This project uses the JVM based Apache Groovy scripting language and builds upon the
JCSP Library developed by the University of Kent together with some additional
Groovy helper Classes described in my books Using Concurrency and
Parallelism Effectively (parts 1 and 2), available for free download
from bookboon.com.
    http://bookboon.com/en/using-concurrency-and-parallelism-effectively-i-ebook
    http://bookboon.com/en/using-concurrency-and-parallelism-effectively-ii-ebook
<p>
The example source code is available from https://github.com/JonKerridge/UCaPE<p>
The libraries for JCSP and GroovyJCSP are available as follows:<br>

https://github.com/JonKerridge/jcsp<br>
https://github.com/CSPforJAVA/jcsp<br>
https://github.com/JonKerridge/groovyJCSP <p>

The goal is to create a number of basic processes that can be used to
implement typical components of a parallel system. These are then combined
into Parallel Design Patterns that create commonly used solution architectures.<p>

The underlying processes can be used either in multi-core solutions or on
clusters with no change to the process and pattern definitions. All that
is required is that the patterns are invoked in a different manner
depending on the architecture used for deployment. All the processes have
been designed to terminate cleanly at the end of execution so that computing
resources can be cleanly recovered and thus reused without any special intervention.<p>

The hope is that programmers will then be able to use the supplied processes
to build yet further application networks.<p>

The design goal has been to construct data objects that contain no parallel
content at all and that are solely derived from the needs of the application. <p>

The programmer can use the patterns with no underlying understanding of,
nor the need to program any communication between any of the processes. The
only concern of the programmer is to create the required communication
channels for the resulting process network. This can be easily achieved by
creating a diagram of the network and transforming that into the required code.<p>

Some example systems using the library can be found at  
https://github.com/JonKerridge/GPP_Demos<br>

Package groovy_parallel_patterns defines some basic classes and interfaces 
used by the rest of the library.<p>
 
All user defined data classes utilising the library should extend DataClass.<p>
 
In addition, a number of methods are required, depending upon the use of the class.
These are described more fully in the information in the packages; 
terminals, patterns and functionals.  A list
of the required methods follows: <br>
initClass([initialData]) used to initialise an object<br>
createInstance([createData]) used to create an instance of the class<br>
finalise([finaliseData]) used to undertake final operations on an object<br>
collector() used to collect and save results<br>
function([dataModifier, wc]) carries out a function on an object using an optional local worker class wc<p>
 
More specialised methods are required for more specific tasks: <br>
updateDisplayList( ) used to update the data structure used in a 
graphical user interface in CollectUI<br>

A program GPP_Builder in library gppBuilder is used to convert a declarative style 
Groovy script into a runnable script thereby removing the need for the user 
to construct the communications channels that are required to implement 
the process network.

https://github.com/JonKerridge/GPP_Builder  

The folder CSPm_FDR_Defintions contains a number of files containing the 
CSPm scripts that can be checked by the FDR tool to show deadlock 
and livelock freedom of the main library components
and also how some networks are refinements of others.

**Download Package from GitHub version 1.1.12**

The library is available from
https://github.com/JonKerridge/GPP_Library

The available build.gradle downloads the required libraries

jcsp: https://github.com/CSPforJAVA/jcsp 
groovyJCSP: https://github.com/JonKerridge/groovyJCSP 

In order to use jcsp, groovyJCSP and Groovy the following are required 
in the build.gradle file.
<pre>
repositories {
  mavenCentral()
  maven { // to download the jonkerridge.groovy_jcsp library
    name = "GitHubPackages"
    url = "https://maven.pkg.github.com/JonKerridge/groovyJCSP"
    credentials {
      username = project.findProperty("gpr.user")
      password = project.findProperty("gpr.key")
    }
  }
  maven { // to download the cspforjava.jcsp library
    name = "GitHubPackages"
    url = "https://maven.pkg.github.com/CSPforJAVA/jcsp"
    credentials {
      username = project.findProperty("gpr.user")
      password = project.findProperty("gpr.key")
    }
  }
}

javafx {
  modules = [ 'javafx.controls', 'javafx.fxml' ]
}

mainClassName = "gppVis/Viualiser/org.openjfx.MainApp"

dependencies {
  implementation 'org.codehaus.groovy:groovy-all:3.0.7'
  implementation 'cspforjava:jcsp:1.1.9'
  implementation 'jonkerridge:groovy_jcsp:1.1.9'
  implementation group: 'junit', name: 'junit', version: '4.13.1'
  testImplementation 'org.codehaus.groovy:groovy-all:3.0.7'
  testImplementation 'cspforjava:jcsp:1.1.9'
  testImplementation 'jonkerridge:groovy_jcsp:1.1.9'
  testImplementation group: 'junit', name: 'junit', version: '4.13.1'
}

</pre>

**Please note**

This library uses Java 11+ as it makes use of JavaFX 11 thus, Java11 JDK is required.

Further details can be seen in the repository
https://github.com/JonKerridge/GPP_Demos   
which uses this library and another called GBB_Builder. The build.gradle file in these
repositories will download the GPP_Library components automatically.

In order to download Github Packages a user requires to have a Github Personal Access Token.  
See https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token  

A gradle.properties file is required at the same directory level as the build.gradle file that contains

<pre>
gpr.user=userName
gpr.key=userPersonalAccessToken
</pre>
