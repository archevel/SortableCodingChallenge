/*
 * Copyright 2012 Emil Hellman
 *  
 * This file is part of SortableChallenge.
 *
 * SortableChallenge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SortableChallenge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SortableChallenge.  If not, see <http://www.gnu.org/licenses/>.
 */

package sortablechallenge.parallelization

import scala.actors.Futures._
import scala.actors.Future

class Parallelizer[I,R](workFunc:I => R) {

  def compute(inputs:Iterator[I]) = {
    var results = List[Future[R]]()
    for (i <- inputs) {
      val f = future { 
	workFunc(i)
      }
      results = results :+ f
    }
    results.map( f => f() )

  }
}

