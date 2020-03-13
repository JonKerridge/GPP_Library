/**
 * Package groovyParallelPatterns.functionals.matrix provides a number of matrix based processing engines.
 * Two basic objects are provided<br>
 *     {@link groovyParallelPatterns.functionals.matrix.Matrix} provides a two-dimensional data structure <br>
 *         {@link groovyParallelPatterns.functionals.matrix.Vector} provides  a one-dimensional data structure<br>
 *             in addition, programmers can use any form of Groovy List object <p>
 *
 * {@link groovyParallelPatterns.functionals.matrix.StencilEngine} provides an architecture where the process
 * transforms an input object and then outputs that object, possibly to another StencilEngine process.<br>
 * {@link groovyParallelPatterns.functionals.matrix.MultiCoreEngine} provides an architecture where the process
 * iterates a fixed number of times or iterates until a terminating condition is met. <br>
 * In both cases it is assumed that the data structure can be separated into disjoint partitions each of which can
 * be subsequently processed in a separate processing node.
 * <p>
 *
 * <pre>
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
 * </pre>
 *
 */

package groovyParallelPatterns.functionals.matrix;