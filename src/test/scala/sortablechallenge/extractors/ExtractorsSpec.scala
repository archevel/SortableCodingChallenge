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

import org.specs2.mutable._

class ProductExtractorSpec extends Specification {
  "ProductExtractor" should {
    "parse product text files to List[Product]" in {
      val pe = new ProductExtractor
      val products = pe.parseFile("src/main/resources/products.txt")
      products.size must_==(743)
    }
    "extract products without modifying data" in {
      val pe = new ProductExtractor
      val products = pe.parseFile("src/main/resources/products.txt")
      products(0).product_name must_==("Sony_Cyber-shot_DSC-W310")
      products(0).manufacturer must_==("Sony")
      products(0).family must_==(Some("Cyber-shot"))
      products(0).model must_==("DSC-W310")
    }
  }
}

class ListingExtractorSpec extends Specification {
  "ListingExtractor" should {
    "parse listing text files to List[Listing]" in {
      val le = new ListingExtractor
      val listings = le.parseFile("src/main/resources/listings.txt")
      listings.size must_==(20196)
    }
    "extract listings without modifying data" in {
      val le = new ListingExtractor
      val listings = le.parseFile("src/main/resources/listings.txt")
      listings(0).currency must_== "CAD"
      listings(0).price must_== "35.99"
      listings(0).manufacturer must_== "Neewer Electronics Accessories"
      listings(0).title must_== "LED Flash Macro Ring Light (48 X LED) with 6 Adapter Rings for For Canon/Sony/Nikon/Sigma Lenses"
    }

  }
}
