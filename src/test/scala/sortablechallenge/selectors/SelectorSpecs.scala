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

package sortablechallenge.selectors

import org.specs2.mutable._

import sortablechallenge.model._


class ConcreteResultSelectorSpec extends Specification {
  "ConcreteResultSelector" should {
    "based on a list of listings and their likelyhood to belong to a number of products generate a list of results" in {
      val prod = Product("elsewhere","f",None,"","")
      val listing1 = Listing("title","f","","")
      val listingToProductProbability = List(
	MessageGivenCategories(listing1, List(CategoryEstimate(prod, 0.5)))
      )
      val selector = new ConcreteResultSelector(prod::Nil, 0d)
      selector.mapToResults(listingToProductProbability) must_== List(Result("elsewhere", List(listing1)))
    }
    "only consider products whos manufacturer is similar to the listings" in {
      val prod1 = Product("elsewhere","f",None,"","")
      val prod2 = Product("nowhere","blurb blopp",None,"","")
      val listing1 = Listing("title","blurb spec","","")
      val listingToProductProbability = List(
	MessageGivenCategories(listing1, List(CategoryEstimate(prod1, 0.5), CategoryEstimate(prod2, 0.2)))
      )
      val selector = new ConcreteResultSelector(prod1::prod2::Nil, 0d)
      selector.mapToResults(listingToProductProbability) must_== List(Result("elsewhere", Nil), Result("nowhere", List(listing1)))
    }
    "all product names should be present in the Result list even if they get no listings" in {
      val prod1 = Product("elsewhere","f",None,"","")
      val prod2 = Product("nowhere","f",None,"","")
      val listing1 = Listing("title","f","","")
      val listingToProductProbability = List(
	MessageGivenCategories(listing1, List(CategoryEstimate(prod1, 0.5), CategoryEstimate(prod2, 0.25)))
      )
      val selector = new ConcreteResultSelector(prod1::prod2::Nil, 0d)
      selector.mapToResults(listingToProductProbability) must containAllOf(List(Result("elsewhere", List(listing1)), Result("nowhere", Nil)))
    }
    "not fail when there are no products in listing" in {
      val prod1 = Product("elsewhere","f",None,"","")
      val prod2 = Product("nowhere","f",None,"","")
      val listing1 = Listing("title","f","","")
      val listingToProductProbability = List(
	MessageGivenCategories[Listing, Product](listing1, Nil)
      )
      val selector = new ConcreteResultSelector(prod1::prod2::Nil, 0d)
      selector.mapToResults(listingToProductProbability) must containAllOf(List(Result("elsewhere", Nil), Result("nowhere", Nil)))
    }
    "only add listings to results which probability estimate is higher than threshold" in {
      val prod1 = Product("elsewhere","f",None,"","")
      val prod2 = Product("nowhere","f",None,"","")
      val listing1 = Listing("title","f","","")
      val listing2 = Listing("other title","f","","")
      val listingToProductProbability = List(
	MessageGivenCategories(listing1, List(CategoryEstimate(prod1, 0.2), CategoryEstimate(prod2, 0.8))),
	MessageGivenCategories(listing2, List(CategoryEstimate(prod1, 0.4), CategoryEstimate(prod2, 0.3)))
      )
      val selector = new ConcreteResultSelector(prod1::prod2::Nil, 0.5d)
      selector.mapToResults(listingToProductProbability) must containAllOf(List(Result("elsewhere", Nil), Result("nowhere", List(listing1))))
    }
  }
}
