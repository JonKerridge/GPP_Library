/**
 * Package groovyParallelPatterns.connectors.spreaders defines some basic processes that can be use to connect
 * other processes together which take an input from one channel and output it to
 * one or several output channels.  As such the processes defined in this package do no
 * data transformations. On reading a UniversalTerminator object each of the spreader processes
 * will ensure that sufficient UniversalTerminator objects are written to subsequent processes in the network.
 * <p>
 *
 * The processes are supplied in a number of different variations depending
 * on the nature of the channel connections provided by the process as follows.<p>
 *
 * Any expects the any end of a channel<br>
 * List expects a channel list<br>
 * One expects a one2one channel end<br>
 * Requested expects a channel pair, one requesting data and the other used to transfer the data<br>
 * Requesting the opposite end to a Requested end; typically used in cluster based networks<p>
 *
 * The nature of the process is defined by the central part of the name;<br>
 *
 * Fan processes one object at a time and in the case of a List output will
 * write the object to the next list out channel end in sequence<br>
 * SeqCast outputs a single input value to all the outputs in sequence<br>
 * ParCast outputs a single input value to all the outputs in parallel
 * <p>
 * <b>SeqCast and ParCast do not check that all the processes to which the output channels are connected are
 * synchronised, thus to be sure of any required synchronisation the receiving processes should use a Barrier
 * to ensure any required synchronisation is achieved by using a ListGroupList in {@link skeletons.groups}.</b>
 * <p>
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

package groovyParallelPatterns.connectors.spreaders;