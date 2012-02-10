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

package sortablechallenge.model

trait Category
trait Message

case class Product(
  product_name: String,   // A unique id for the product
  manufacturer: String,
  family: Option[String],         // optional grouping of products
  model: String,
  announced_date: String
) extends Category


case class Listing(
  title: String,         // description of product for sale
  manufacturer: String, // who manufactures the product for sale
  currency: String,      // currency code, e.g. USD, CAD, GBP, etc.
  price: String         // price, e.g. 19.99, 100.00
) extends Message

case class Result(
  product_name: String,
  listings: List[Listing]
)


case class CategoryEstimate[C <: Category](category:C, estimate:Double)
case class MessageGivenCategories[M <: Message, C <: Category](message:M, categoryEstimates:List[CategoryEstimate[C]])
