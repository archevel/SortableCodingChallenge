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

import sortablechallenge.model._

trait LikeliestCategoryFinder[C <: Category] {
  def threshold:CategoryEstimate[C]
  def getLikeliest(estimates:List[CategoryEstimate[C]]) = {
    val likeliest = (threshold::estimates).maxBy(_.estimate)
    if(likeliest == threshold)
      None
    else {
      Some(likeliest.category)
    }
  }
}

class ResultSelector(products:List[Product], thresholdVal:Double) extends LikeliestCategoryFinder[Product] {
  val threshold = CategoryEstimate(Product("THRESHOLD","THRESHOLD",None,"THRESHOLD","THRESHOLD"), thresholdVal)

  private val initialResult:Map[String,List[Listing]] =
    products.map(x => (x.product_name, Nil)).toMap
  
  def mapToResults(listingToProductProbability:List[MessageGivenCategories[Listing, Product]]):List[Result] = {
    listingToProductProbability.foldLeft(initialResult)((acc, mgc) => {
      val likeliest = getLikeliest(mgc.categoryEstimates)
      likeliest match {
	case None => acc
	case Some(prod) => {
	  acc.get(prod.product_name) match {
	    case None => acc + ((prod.product_name, mgc.message::Nil))
	    case Some(list) => acc + ((prod.product_name, mgc.message::list))
	  }
	}
      }
    }) map {
      case (name, listings) => Result(name, listings)
    } toList    
  }
  
}
