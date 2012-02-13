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

package sortablechallenge.probability

import sortablechallenge.model._
import sortablechallenge.tokenizers._
import sortablechallenge.cleaners._

trait CategoryGivenMessageEstimator[M <:Message,C <: Category] extends {
  def dictionary:Set[String]
  def messageCleaner:MessageCleaner[M]
  def estimate(message:M):List[CategoryEstimate[C]]

  def estimate(messages:List[M]):List[MessageGivenCategories[M,C]] = {
    messages.map(calcEstimatesFor _)
  }

  private def calcEstimatesFor(dirtyMessage:M) = {
    val cleanMessage = messageCleaner.clean(dirtyMessage)
    val messageGivenCatEstimates = estimate(cleanMessage).toList
    
    val totalProbability = messageGivenCatEstimates.foldLeft(0d)((acc, catAndProb) => {
      catAndProb.estimate + acc
    })
    val estimates = if(totalProbability > 0d)
      messageGivenCatEstimates.map(catAndProb => {
	CategoryEstimate(catAndProb.category, catAndProb.estimate / totalProbability)
      }) else messageGivenCatEstimates

    MessageGivenCategories(dirtyMessage, estimates)
  }

}

trait MessageCategoryListEstimator[M <:Message,C <: Category] {
  def categoriesToWords:Map[C, List[String]]
  def dictionary:Set[String]
  def wordListGivenCategoryEstimator(categoryWords:List[String], dictionary:Set[String]):WordListGivenCategoryEstimator
  def priorCategoryProbability(category:C):Double
  def messageTokenizer:MessageTokenizer[M]

  def estimate(message:M):List[CategoryEstimate[C]] = {
    val messageWords = messageTokenizer.extractFromMessage(message, dictionary)

    categoriesToWords.map(catAndWords => {
      val categoryProbability = priorCategoryProbability(catAndWords._1)
      val estimator = wordListGivenCategoryEstimator(catAndWords._2, dictionary)
      val estimate = estimator.estimate(categoryProbability, messageWords)
      CategoryEstimate(catAndWords._1, estimate)
    }).toList
  }
}

trait WordListGivenCategoryEstimator {
  def categoryWords:List[String]
  def dictionary:Set[String]
  def computeProbability(matches:Int, possibleMatches:Int, totalSize:Int):Double

  def estimate(categoryProbability:Double, wordList:List[String]):Double = {
    wordList.foldLeft(categoryProbability)((curProb,word) => {
      val wordProb = estimate(word)
      curProb * wordProb
    })
  }

  def estimate(word:String):Double = {
    val matches = categoryWords count(_ == word)
    computeProbability(matches, categoryWords.size, dictionary.size)
  }

}

trait SmoothedProbabilityCalculator {
  def K:Int

  def computeProbability(matches:Int, possibleMatches:Int, totalSize:Int) = {
    (matches.toDouble + K) / (possibleMatches + K*totalSize)
  }
}

class ConcreteProductGivenListingEstimator(
  val categoriesToWords:Map[Product, List[String]],
  val dictionary:Set[String], 
  k:Int) extends CategoryGivenMessageEstimator[Listing,Product] with MessageCategoryListEstimator[Listing,Product] {
  val messageCleaner = new ListingCleaner
  val messageTokenizer = Tokenizer

  lazy val constantPriorCategoryProbability = 1
  // All categories have the same prior probability.
  // This means that the probability of a Message being classified as belonging to
  // a Category is essentially the same as the probability of a Message 
  // given Category (modified by constant). 
  // i.e. we realy only need to estimate using WordListGivenCategory if the priorProbability 
  // is the same for all categories...
  def priorCategoryProbability(category:Product) = constantPriorCategoryProbability
  def wordListGivenCategoryEstimator(categoryWords:List[String], dictionary:Set[String]) = {
    new ConcreteWordListGivenCategoryEstimator(categoryWords, dictionary,k)
  }
}

class ConcreteWordListGivenCategoryEstimator(
  val categoryWords:List[String], 
  val dictionary:Set[String],
  val K:Int
) extends WordListGivenCategoryEstimator with SmoothedProbabilityCalculator 
