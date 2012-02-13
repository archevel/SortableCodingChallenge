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

package sortablechallenge.cleaners

import sortablechallenge.model._

trait Cleaner[T] {
  def clean(dirty:T):T
}

trait StringCleaner {
  
  val DASH_REPLACER = """([a-z]{0,1})([\-_]{0,1})(\d+[\.]{0,1}\d*)([\-_]{0,1})([a-z]{0,1})""".r
  val CAMEL_CASE_SEPARATOR = """([a-z]+[a-z])([A-Z][A-Za-z]+)""".r
  val DASH_BETWEEN_LETTERS_REPLACER = """([a-z])([\-_])([a-z])""".r
  def clean(dirty:String):String = {
    val uncameled = CAMEL_CASE_SEPARATOR.replaceAllIn(dirty, x => x.group(1) + " " + x.group(2))
    val lowered = uncameled.toLowerCase
    val numberDashed = DASH_REPLACER.replaceAllIn(lowered, x => { 
      val l1 =  x.subgroups(0)
      val d1 =  x.subgroups(1)
      val num = x.subgroups(2)
      val d2 =  x.subgroups(3)
      val l2 =  x.subgroups(4)
      
      val start = if(l1 != "" || d1 != "") l1 + "DASH" else ""
      val end = if(l2 != "" || d2 != "") "DASH" + l2 else ""

      start + num + end
    })
    val dashed = DASH_BETWEEN_LETTERS_REPLACER.replaceAllIn(numberDashed, x => x.group(1) + "DASH" + x.group(3))
    val dotted = dashed.replace(".","DOT")
    
    val noNonWordChars = dotted.replaceAll("[^A-Za-z0-9]", " ")
    noNonWordChars
  }

}

trait CategoryCleaner[C <: Category] extends Cleaner[C]
trait MessageCleaner[M <: Message] extends Cleaner[M]

class ProductCleaner extends CategoryCleaner[Product] with StringCleaner {
  def clean(dirty:Product):Product = dirty match {
    case Product(pn, man, Some(fam), mod, ad) => {
      Product(pn, clean(man), Some(clean(fam)), clean(mod), ad)
    }
    case Product(pn, man, None, mod, ad) => {
      Product(pn, clean(man), None, clean(mod), ad)
    }
  }
}

class ListingCleaner extends MessageCleaner[Listing] with StringCleaner {
  def clean(dirty:Listing):Listing = dirty match {
    case Listing(t,man,c,p) => Listing(clean(t), clean(man), c, p)
  }
}
