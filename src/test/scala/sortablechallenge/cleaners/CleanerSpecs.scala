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

import org.specs2.mutable._

class StringCleanerSpec extends Specification {
  "A StringCleaner" should {
    "ensure the string is all lower case" in {
      val norm = new StringCleaner {}
      norm.clean("ABCDEFGHijKLMNOPQRSTUVXYZ1234567890") must beEqualTo("abcdefghijklmnopqrstuvxyz 1234567890 ")
      norm.clean("QWERTYZXCV") must beEqualTo("qwertyzxcv")
    }
    "replace . with dot" in {
      val norm = new StringCleaner {}
      norm.clean("AA.YZXCV") must beEqualTo("aadotyzxcv")
    }
    "replace other non-word characters with space" in {
      val norm = new StringCleaner {}
      norm.clean("AA_YZXCV") must beEqualTo("aa yzxcv")
      norm.clean("__YZ_XC___V") must beEqualTo("  yz xc   v")
      norm.clean("""1#!2@?~3//4\\5-,6.0""") must beEqualTo("1  2   3  4  5   6dot0 ")
    } 
    "separate numbers from letters when occuring together" in {
      val norm = new StringCleaner {}
      norm.clean("a foo123bar of") must beEqualTo("a foo 123 bar of")
      norm.clean("a foo12.3bar of") must beEqualTo("a foo 12dot3 bar of")

    }
  }
}

