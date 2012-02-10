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

package sortablechallenge.output


import java.io.{File, FileWriter}
import net.liftweb.json._
import net.liftweb.json.Serialization

import sortablechallenge.model._

class OutputWriter(path:String) {

  def write(results:List[Result]) = {    
    val writer = new FileWriter(new File(path))
    val output = results.map((r) => {
      implicit val formats = DefaultFormats
      Serialization.write(r)
    }).mkString("\r\n")
    writer.write(output)
    writer.close
  }
}


