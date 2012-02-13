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
import sortablechallenge.cleaners._

trait LikeliestCategoryFinder[C <: Category] {



  def threshold:CategoryEstimate[C]
  def getLikeliest(mandatoryCondition: CategoryEstimate[C] => Boolean, estimates:List[CategoryEstimate[C]]) = {
    val viable = estimates.filter(mandatoryCondition)
    val likeliest = (threshold::viable).maxBy(_.estimate)
    if(likeliest == threshold)
      None
    else {
      Some(likeliest)
    }
  }
}

abstract class ResultSelector(products:List[Product], thresholdVal:Double) extends LikeliestCategoryFinder[Product] {
  val threshold = CategoryEstimate(Product("THRESHOLD","THRESHOLD",None,"THRESHOLD","THRESHOLD"), thresholdVal)
  var all = List[(Message, CategoryEstimate[Product])]()

  private val initialResult:Map[String,List[Listing]] =
    products.map(x => (x.product_name, Nil)).toMap

  def clean(str:String):String

  private def tokenize(str:String) = str.split(" ").filter(_ != "")
  
  def mapToResults(listingToProductProbability:List[MessageGivenCategories[Listing, Product]]):List[Result] = {
    var result = listingToProductProbability.foldLeft(initialResult)((acc, mgc) => {
      
      val cleanManufacturerWords = tokenize(clean(mgc.message.manufacturer))
      
      val condition = (ce:CategoryEstimate[Product]) => {
	val manTokens = tokenize(ce.category.manufacturer)
	cleanManufacturerWords.find(x => manTokens.contains(x)) != None
      }
      val likeliest = getLikeliest(condition, mgc.categoryEstimates)


      likeliest match {
	case None => acc
	case Some(l) => {
	  val prod = l.category
	  all = (mgc.message, l):: all 
	  acc.get(prod.product_name) match {
	    case None => acc + ((prod.product_name, mgc.message::Nil))
	    case Some(list) => acc + ((prod.product_name, mgc.message::list))
	  }
	}
      }
    }) map {
      case (name, listings) => Result(name, listings)
    } toList    

    if(all != Nil) {
      println("avg: " + all.foldLeft(0d)((acc,e) => acc + e._2.estimate) / all.size)
      println("min: " + all.minBy(_._2.estimate))
      println("max: " + all.maxBy(_._2.estimate))
    }
    result
  }
  
}

class ConcreteResultSelector(products:List[Product], thresholdVal:Double) extends ResultSelector(products, thresholdVal) with StringCleaner
