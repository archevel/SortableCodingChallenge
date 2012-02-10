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

import org.specs2.mutable._

class ParallelizerSpec extends Specification {
  "A Parallelizer" should {
    "given a function compute a list of results from applying the function to the list of inputs" in {
      val func = (l:List[Int]) => { l.sum }
      val parallelizer = new Parallelizer(func)
      val input = (1 to 1000).toList.grouped(5)
      val results = parallelizer compute input
      results(0) must_== 15
      results(15) must_== 390
      results.sum must_==(1000*1001/2)	
    }

  }
}
