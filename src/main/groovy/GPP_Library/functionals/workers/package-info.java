/**
 * Package workers defines processes that can used to populate processing skeletons with the
 * processes that actually undertake operations on data objects.<p>
 *
 * Worker reads an input data object from the input channel.<br>
 * The input data object must provide a method that is called by the Worker process.  This
 * can be given any name but must implement the function to be carried out by the Worker process<br>
 *
 * int function([dataModifer, wc]) used to implement a function of the object<br>
 *     a data object may have many such functions associated with different operations<br>
 *     dataModifer is passed as a list parameter to the Worker process<br>
 *     wc refers to, a possibly null, local worker class object<br>
 *
 *
 * Any local worker class MUST provide methods as follows, even if they do nothing:<br>
 * int initClass(workerInitData) used to initialise the local worker class,<br>
 *     workerInitData is passed as a list parameter to the Worker process<br>
 * int finalise(finaliseData)    used to provide finalisation code for the local worker class<br>
 *     typically used to prepare a local worker object that is to be output
 *     to the rest of the parallel network<br>
 *     finaliseData is passed as a list parameter to the worker process<br>

 * In all cases the required methods are passed to the Worker process as String parameters using the .& notation.<p>
 * Thus a Closure parameter has the form:<br>
 * Closure name: className.&methodName<p>
 *
 * All methods must return an int value as a return code, typically the value<br>
 * {@link GPP_Library.DataClassInterface#completedOK}, indicating the method was successful.  In cases
 * where the method fails, it should return a user defined error value that will cause
 * the whole system to exit with an appropriate error message by means of a call to
 * {@link GPP_Library.DataClass#unexpectedReturnCode()}.<p>
 *
 * ThreePhaseWorker is a specialisation of a worker process that has an input phase, then a phase that undertakes
 * an operation on the input data and finally a phase that outputs the data to a following process.<p>
 *
 *<pre>
 * Author, Licence and Copyright statement
 * author  Jon Kerridge
 * 		   School of Computing
 * 		   Edinburgh Napier University
 * 		   Merchiston Campus,
 * 		   Colinton Road
 * 		   Edinburgh EH10 5DT
 *
 * Author contact: j.kerridge (at) napier.ac.uk
 *
 * Copyright  Jon Kerridge Edinburgh Napier University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *</pre>
 *
  */

package GPP_Library.functionals.workers;