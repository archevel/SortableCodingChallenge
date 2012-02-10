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

package sortablechallenge

import extractors._
import cleaners._
import tokenizers._
import probability._
import parallelization._
import model._
import selectors._
import output._

object  SortableChallenge {

  def main(args:Array[String]) {
    val threshold = getThreshold(args)

    println("Parsing 'src/main/resources/products.txt'...")
    // read and parse input files
    val products = new ProductExtractor().parseFile(
      "src/main/resources/products.txt")
    println("Done! (" +products.size+" products found)")
    println("Parsing 'src/main/resources/listings.txt'...")
    val listings = new ListingExtractor().parseFile(
      "src/main/resources/listings.txt")
    println("Done! (" +listings.size+" listings found)")

    println("Preparing products and computing dictionary...")
    val prodWasher = new ProductCleaner
    val cleanProds = products.map(prodWasher.clean _)
    val dictionary = Tokenizer.createDictionary(cleanProds)
    val prodsToWords = cleanProds.map(
      p => (p, Tokenizer.extractFromCategory(p))).toMap
    println("Done! (" + dictionary.size + " words in dictionary)")

    println("Computing estimates for listings... (plx wait!)")
    val estimator = new ConcreteProductGivenListingEstimator(prodsToWords, dictionary, 1)
    val estimationWork = (ls:List[Listing]) => { estimator estimate(ls) }
    val estimationParallelizer = new Parallelizer(estimationWork)
    val resultList = estimationParallelizer compute(listings.grouped(500))
    val allEstimates = resultList.flatten
    println("Done!")

    println("Selecting probable product for listings (with threshold: " + threshold + ")...")
    val selector = new ResultSelector(cleanProds,threshold)
    val results = selector.mapToResults(allEstimates)
    println("Done!")

    println("High recall low precision selection (with threshold: 0)...")
    val highRecallSelector = new ResultSelector(cleanProds,0)
    val highRecallResults = highRecallSelector.mapToResults(allEstimates)
    println("Done!")

    println("Low recall high precision selection (with threshold: 0.02)...")
    val highPrecisionSelector = new ResultSelector(cleanProds,0.02)
    val highPrecisionResults = highPrecisionSelector.mapToResults(allEstimates)
    println("Done!")

    println("Writing the results to 'result_with_threshold_"+threshold+".txt'...")
    val writer = new OutputWriter("result_with_threshold_"+threshold+".txt")
    writer.write(results)
    println("Done!")

    println("Writing the results to 'highRecall_with_threshold_0.txt'...")
    val highRecallWriter = new OutputWriter("highRecall_with_threshold_0.txt")
    highRecallWriter.write(highRecallResults)
    println("Done!")

    println("Writing the results to 'highPrecision_with_threshold_0.02.txt'...")
    val highPrecisionWriter = new OutputWriter("highPrecision_with_threshold_0.02.txt")
    highPrecisionWriter.write(highPrecisionResults)
    println("Done!")

  }

  private def getThreshold(args:Array[String]) = {
    val default = 0.0111d
    if(args.size == 1) {
      try {
	args(0) toDouble
      } catch {
	case _ => {
	  println("Invalid threshold argument: " + args(0) + ", using default: " + default)
	  default
	}
      }
    } else {
      default
    }
  }

}
