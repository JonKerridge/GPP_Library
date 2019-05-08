/**
 * Package gpp.functionals.pipelines provides a number of processes that can be used as
 * a component in larger networks.  Each process comprises a network of
 * other processes, typically, Worker, WorkerTerminating and Collect.<p>
 * The processes are supplied in a number of different variations depending
 * on the nature of the channel connections provided by the process as follows.<p>
 *
 * One expects the one end of a channel<br>
 * Collect means a collection containing a Collect process as the last or
 * only element in the process<br>
 *
 * The nature of the process is defined by concepts such as<br>
 *
 * Pipeline a sequence of process undertaking a series of operations on
 * a data object as it passes through the pipeline, a so called task
 * parallel architecture<br>
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

package GPP_Library.functionals.pipelines;