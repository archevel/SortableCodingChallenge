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

package sortablechallenge.tokenizers

import sortablechallenge.model._

trait CategoryTokenizer[C<:Category] {
  def createDictionary(categories:List[C]) = {
    extractFromCategories(categories).toSet
  }
  def extractFromCategories(categories:List[C]):List[String] = {
    categories.flatMap(c => {
      extractFromCategory(c)
    })
  }

  def extractFromCategory(category:C):List[String]
}

trait MessageTokenizer[M<:Message] {
  def extractFromMessages(messages:List[M]):List[String] = {
    messages.flatMap(m => {
      extractFromMessage(m)
    })
  }
  
  def extractFromMessage(message:M):List[String]
}


object Tokenizer extends CategoryTokenizer[Product] with MessageTokenizer[Listing] {

  def extractFromCategory(category:Product):List[String] = category match {
    case Product(_, man, fam, mod, _) => {
      val manList = man.split(" ").filter(_ != "")
      val famList = fam.getOrElse("").split(" ").filter(_ != "")
      val modList = mod.split(" ").filter(_ != "")
      (manList ++ famList ++ modList).toList
    }
  }
  

  def extractFromMessage(message:Listing):List[String] = message match {
    case Listing(t, m, _,_) => {
      val tList = t.split(" ").filter(_ != "")
      val mList = m.split(" ").filter(_ != "")
      (tList ++ mList).toList
     
    }
  }
}
