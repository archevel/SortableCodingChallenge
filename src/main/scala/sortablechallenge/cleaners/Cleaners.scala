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
  val numberSeparation = """(\d+[\.]{0,1}\d+)""".r
  def clean(dirty:String):String = {
    numberSeparation.replaceAllIn(dirty.toUpperCase.toLowerCase, x => " " + x.toString + " ").replace(".", "dot").replaceAll("[^a-z0-9]", " ")
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
