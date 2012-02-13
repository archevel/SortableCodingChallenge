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

import org.specs2.mutable._

import sortablechallenge.tokenizers._
import sortablechallenge.model._
import sortablechallenge.cleaners._

class CategoryGivenMessageEstimatorSpec extends Specification { 
  "CategoryGivenMessageEstimator" should {
    "for all products and all listings calculate the probability of the listings belonging to the products" in {
      // long test... 
      // Given
      val prod1 = Product("FOO_Bar_Biz", "fun boat",Some("boat fun"), "angeredgod", "ignored :(")
      val prod2 = Product("eskil", "", None, "loo","")
      val estimator = new CategoryGivenMessageEstimator[Listing, Product] with MessageCategoryListEstimator[Listing,Product] {

	val messageTokenizer = Tokenizer
	val dictionary = Set("fun", "boat", "angeredgod", "loo")
	val messageCleaner = new ListingCleaner
	val categoriesToWords = Map(
	  prod1 -> List("fun","boat","angeredgod"),
	  prod2 -> List("loo")
	)
	def wordListGivenCategoryEstimator(categoryWords:List[String], dictionary:Set[String]) = 
	  new TestWordListGivenCategoryEstimator(categoryWords)	 
	def priorCategoryProbability(category:Product):Double = 0.5 
	
      } 
      val listing1 = Listing("","boat","fasdf","ONE MILLION DOLLARS!")
      val listings = List (
	listing1
      )
      // When 
      val categoryToMessage:List[MessageGivenCategories[Listing, Product]] = estimator estimate listings

      // Then
      categoryToMessage must have size(1)

      val listing1Sorted = categoryToMessage(0).categoryEstimates.sort((a, b) => a.estimate > b.estimate)
      listing1Sorted must have size(2)

      listing1Sorted(0).category must_== prod1
      listing1Sorted(1).category must_== prod2

      listing1Sorted(0).estimate must_==(0.75d/(0.2 + 0.75)) // probability of listing given category divided by total probability
      listing1Sorted(1).estimate must_==(0.2d/(0.2 + 0.75)) // probability of listing given category divided by total probability
    } 
    "clean each listing before estimating it" in {
      // Given
      val prod1 = Product("FOO_Bar_Biz", "fun boat",Some("boat fun"), "angeredgod", "ignored :(")
      val prod2 = Product("eskil", "", None, "loo","")
      val estimator = new CategoryGivenMessageEstimator[Listing, Product] with MessageCategoryListEstimator[Listing,Product] {

	val messageTokenizer = Tokenizer
	val dictionary = Set("fun", "boat", "angeredgod", "loo")
	val messageCleaner = new ListingCleaner
	val categoriesToWords = Map(
	  prod1 -> List("fun","boat","angeredgod"),
	  prod2 -> List("loo")
	)
	def wordListGivenCategoryEstimator(categoryWords:List[String], dictionary:Set[String]) = 
	  new ConcreteWordListGivenCategoryEstimator(categoryWords, dictionary, 1)
	def priorCategoryProbability(category:Product):Double = 0.5 
      } 
      val listing1 = Listing("agro boat lalala      booo","boat","fasdf angeredgod","ONE MILLION DOLLARS!")
      val listing2 = Listing("Agro!boat LalAla #__¤ booo","BoAt","fasdf&&&!_AnGerEdGOD","ONE MILLION DOLLARS!")
      val listings = List (
	listing1, listing2
      )
      // When 
      val categoryToMessage:List[MessageGivenCategories[Listing, Product]] = estimator estimate listings

      //Then
      categoryToMessage must have size(2)

      categoryToMessage(0).categoryEstimates must containAllOf(categoryToMessage(1).categoryEstimates)
      categoryToMessage(1).categoryEstimates must containAllOf(categoryToMessage(0).categoryEstimates)
      
    }
  }
}

class MessageCategoryEstimatorSpec extends Specification { 
  "MessageCategoryListEstimator" should {
    "if there are no words in the listing then the estimate should equal the priorProbability for the product" in {
      val prod1 = Product("FOO_Bar_Biz", "foo the",Some("bar the foo"), "biz", "")
      val prod2 = Product("salut!", "salut", None, "","")
      val estimator = 
	new MessageCategoryListEstimator[Listing, Product] {
	  val messageTokenizer = Tokenizer
	  val categoriesToWords = Map(
	    prod1 -> List("foo", "the", "bar", "the", "foo", "biz"),
	    prod2 -> List("salut")
	  )
	  val dictionary = Set("foo", "the", "bar", "biz")
	  def wordListGivenCategoryEstimator(categoryWords:List[String], dictionary:Set[String]) = 
	    new ConcreteWordListGivenCategoryEstimator(categoryWords, dictionary, 1)
	  def priorCategoryProbability(product:Product):Double = {
	    if(product == prod1) {
	      0.6
	    } else if (product == prod2) {
	      0.3
	    } else {
	      throw new IllegalArgumentException("Expected only prod1 and prod2...")
	    }
	  }
	}

      val listing1 = Listing("", "", "MCE", "2.55")
      val estimates:List[CategoryEstimate[Product]] = estimator estimate listing1
      estimates(0).estimate must_== 0.6
      estimates(1).estimate must_== 0.3
      
    }
    "estimate, for all products, the probability of the words in the listing given product words" in {
      val prod1 = Product("FOO_Bar_Biz", "foo the",Some("bar the foo"), "biz", "")
      val prod2 = Product("salut!", "salut", None, "","")

      val estimator = new MessageCategoryListEstimator[Listing,Product] { 
	val messageTokenizer = Tokenizer
	val K = 1 
	val categoriesToWords = Map(
	  prod1 -> List("foo", "the", "bar", "the", "foo", "biz"),
	  prod2 -> List("salut")
	)
	val dictionary = Set("foo", "the", "bar", "biz", "salut")
	def wordListGivenCategoryEstimator(categoryWords:List[String], dictionary:Set[String]) = 
	  new ConcreteWordListGivenCategoryEstimator(categoryWords, dictionary, 1)
	def priorCategoryProbability(product:Product):Double = 0.5
      }

      val listing1 = Listing("the biz", "", "MCE", "2.55")
      val estimates:List[CategoryEstimate[Product]] = estimator estimate listing1
      estimates(0).estimate must_==(0.5 * (2d /(6 + 5))*(3d /(6 + 5)))
      estimates(1).estimate must_==(0.5 * (1d /(1 + 5))*(1d /(1 + 5)))

    }
    "filter out words not in the dictionary " in {
      val prod1 = Product("FOO_Bar_Biz", "foo the",Some("bar the foo"), "biz", "")
      val prod2 = Product("salut!", "salut", None, "","")

      val estimator = new MessageCategoryListEstimator[Listing,Product] { 
	val messageTokenizer = Tokenizer
	val K = 1 
	val categoriesToWords = Map(
	  prod1 -> List("foo", "the", "bar", "the", "foo", "biz"),
	  prod2 -> List("salut")
	)
	val dictionary = Set("foo", "the", "bar", "biz", "salut")
	def wordListGivenCategoryEstimator(categoryWords:List[String], dictionary:Set[String]) = 
	  new ConcreteWordListGivenCategoryEstimator(categoryWords, dictionary, 1)
	def priorCategoryProbability(product:Product):Double = 0.5
      }

      val listing1 = Listing("the lollipop biz", "", "MCE", "2.55")
      val estimates:List[CategoryEstimate[Product]] = estimator estimate listing1
      estimates(0).estimate must_==(0.5 * (2d /(6 + 5))*(3d /(6 + 5)))
      estimates(1).estimate must_==(0.5 * (1d /(1 + 5))*(1d /(1 + 5)))

    }
  }
}

class WordListGivenCategoryEstimatorSpec extends Specification {
  "WordListGivenCategoryEstimator" should {
    "estimate the joined probability of all words in the listing given a product" in {

      val estimator = new WordListGivenCategoryEstimator with SmoothedProbabilityCalculator { 
	val K = 1
	val categoryWords = List("foo", "the", "bar", "the", "foo", "biz")
	val dictionary = Set("foo", "the", "bar", "biz")
      }
      val wordList1 = List("lollipop", "foo")
      val categoryProbability = 0.5
      estimator.estimate(categoryProbability, wordList1) must_==(0.5 * (1d /(6 + 4))*(3d /(6 + 4)))

      estimator.estimate(categoryProbability, Nil) must_==(0.5)
    }
    "given a list of product words and a dictionary estimate the probability for the word given the product" in {
      val estimator = new WordListGivenCategoryEstimator with SmoothedProbabilityCalculator {
	val K = 1
	val categoryWords = List("foo", "the", "bar", "the", "foo", "biz")
	val dictionary = Set("foo", "the", "bar", "biz")
      }
      
      val word1 = "lollipop"
      estimator estimate word1 must_==(1d /(6 + 4))

      val word2 = "foo"
      estimator estimate word2 must_==(3d /(6 + 4))
      
    }
    "given a different result when K is different" in {
      val estimator = new WordListGivenCategoryEstimator with SmoothedProbabilityCalculator {
	val K = 2
	val categoryWords = List("foo", "the", "bar", "the", "foo", "biz")
	val dictionary = Set("foo", "the", "bar", "biz")
      }
      
      val word1 = "lollipop"
      estimator estimate word1 must_==(2d /(6 + 8))

      val word2 = "foo"
      estimator estimate word2 must_==(4d /(6 + 8))
      
    }
  }
}

class SmoothedProbabilityCalculatorSpec extends Specification {
  "A SmoothedProbabilityCalculator" should {
    "calculate a probability with laplacian smoothing so probabilites are never 0 given a possitive K" in {
      val calculator = new SmoothedProbabilityCalculator { val K = 1 }
      val matches = 0
      val words = 10
      val dictSize = 30
      calculator.computeProbability(matches, words, dictSize) must_== 0.025
    }
    "calculate another probability with laplacian smoothing so probabilites are never 0 given a possitive K" in {
      val calculator = new SmoothedProbabilityCalculator { val K = 1 }
      val matches = 5
      val words = 10
      val dictSize = 30
      calculator.computeProbability(matches, words, dictSize) must_== 0.15
    }
  }
  
}
class  TestWordListGivenCategoryEstimator(testWord:List[String]) extends WordListGivenCategoryEstimator with SmoothedProbabilityCalculator {
  
  val categoryWords:List[String] = null
  val dictionary:Set[String] = null
  val K = 1
  override def estimate(word:String):Double = 0

  override def estimate(categoryProbability:Double, wordList:List[String]):Double = {
    if(testWord.contains(wordList(0))) {
      0.75d
    } else {
      0.2d
    }
  }
}
