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

package sortablechallenge.extractors

import net.liftweb.json._
import net.liftweb.json.JsonParser

import sortablechallenge.model._

trait Extractor[T] {
  implicit val formats = DefaultFormats
  def parseFile(path:String) = {
    val source = scala.io.Source.fromFile(path,"utf-8")
    val lines = source.getLines.toList.map(modifyLineBeforeParse _)
    source.close()
   
    lines.map((line) => {
      val json = parse(line)
      extract(json)
    })

  }
  protected def extract(jval:JValue):T
  protected def modifyLineBeforeParse(line:String) = line
  
}

class ProductExtractor extends Extractor[Product] {
  def extract(jval:JValue) = jval.extract[Product]
  override def modifyLineBeforeParse(line:String) = AnnouncedDateHelper.makeAnnouncedDateExtractable(line)
}

class ListingExtractor extends Extractor[Listing] {
  def extract(jval:JValue) = jval.extract[Listing] 
}

object AnnouncedDateHelper {
  val announcedDate = """(.+\")announced-date(\":.+)""".r
  def makeAnnouncedDateExtractable(line:String) = line match {
    case announcedDate(before, after) => before + "announced_date" + after
    case _ => throw new IllegalArgumentException("""Argument must contain >"announced-date":<""")
  }
}
