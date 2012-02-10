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

import org.specs2.mutable._

import sortablechallenge.model._

class TokenizerSpec extends Specification {
  "A Tokenizer" should {
    "from a list of Products create a Set[String] with all words in manufacturer, family and model" in {
      val products1 = List(
	Product("FOO_Bar_Biz", "foo the",Some("bar the foo"),      "biz", ""),
	Product("Amazon",      "flu",None,"al", "")
      )
      val dict1 = Tokenizer.createDictionary(products1)
      dict1.size must be equalTo(6)

      val products2 = List(
	Product("FOO_Bar_Biz", "foo the",Some("bar the foo"),      "biz", "asdf"),
	Product("Amazon",      "flu",None,"al edo", "")
      )
      val dict2 = Tokenizer.createDictionary(products2)
      dict2.size must be equalTo(7)

    }
    "from a list of Listings extract a List[String] with all words in title and manufacturer" in {
      val dirty = Listing("A Foo BaR of tHe aweSOME BIZ_model", "FOO", "MICE", "2.5")
      val clean = Listing("a foo bar of the awesome biz model","foo","MICE","2.5")
      val listings = List(dirty, clean)
      val words = Tokenizer.extractFromMessages(listings)
      words.size must be equalTo(17)
    }

  }

}
