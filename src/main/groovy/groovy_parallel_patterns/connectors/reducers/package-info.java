/**
 * Package groovy_parallel_patterns.connectors.reducers defines some basic processes that can be use to connect
 * other processes together.  As such the processes defined in this package do no
 * data transformations. They simply take an input from many channels and output it to
 * one output channel.
 * <p>
 *
 * The processes are supplied in a number of different variations depending
 * on the nature of the channel connections provided by the process as follows.<p>
 *
 * Any expects the any end of a channel<br>
 * List expects a channel list<br>
 * One expects a one2one channel<br>
 *
 * Fan processes one object at a time in such a way as to ensure
 *      all inputs are given an equal share of the available input bandwidth
 * Merge inputs a single value from an element of a channel list and then outputs it,
 *      the processes then iterates through each input channel in turn
 * Seq inputs a single input value from all the input channels in sequence and then outputs them in sequence
 * Par inputs a single input value from all the input channels in parallel and then outputs them in sequence
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


package groovy_parallel_patterns.connectors.reducers;