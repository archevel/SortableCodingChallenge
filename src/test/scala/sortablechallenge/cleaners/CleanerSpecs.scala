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
    "ensure all original letters in the string is lower case" in {
      val norm = new StringCleaner {}
      norm.clean("ABCDEFGHIJKLMNOPQRSTUVXYZ 1234567890") must beEqualTo("abcdefghijklmnopqrstuvxyz 1234567890")
      norm.clean("QWERTYZXCV") must beEqualTo("qwertyzxcv")
    }
    "replace . with 'DOT'" in {
      val norm = new StringCleaner {}
      norm.clean("AA.YZXCV") must beEqualTo("aaDOTyzxcv")
    }
    "replace - and _ with 'DASH' if it separates letters and/or numbers" in {
      val norm = new StringCleaner {}
      norm.clean("Andromeda SX-12") must beEqualTo("andromeda sxDASH12")
      norm.clean("12-SFX") must beEqualTo("12DASHsfx")
      norm.clean("K-m") must beEqualTo("kDASHm")
      norm.clean("QQ-12-SFX") must beEqualTo("qqDASH12DASHsfx")
      norm.clean("12-QQ-12SFX") must beEqualTo("12DASHqqDASH12DASHsfx")
      norm.clean("LL-99---12QQ12SFX") must beEqualTo("llDASH99DASH DASH12DASHqqDASH12DASHsfx")
      norm.clean("xx-00-gg 4444-s i-9 6-7_r") must beEqualTo("xxDASH00DASHgg 4444DASHs iDASH9 6DASH7DASHr")

    }
    "replace other non-word characters with space" in {
      val norm = new StringCleaner {}
      norm.clean("AA#YZXCV") must beEqualTo("aa yzxcv")
      norm.clean("__YZ_XC___V") must beEqualTo("  yzDASHxc   v")
      norm.clean("""1#!2@?~3//4\\5-,6.0""") must beEqualTo("1  2   3  4  5DASH 6DOT0")
    } 
    "separate numbers from letters occuring together with a 'DASH' and " in {
      val norm = new StringCleaner {}
      norm.clean("a foo123bar of") must beEqualTo("a fooDASH123DASHbar of")
      norm.clean("a foo12.3bar of") must beEqualTo("a fooDASH12DOT3DASHbar of")
    }
    "separate camel cased words with ' '" in {
      val norm = new StringCleaner {}
      norm.clean("Ba") must beEqualTo("ba")
      norm.clean("aB") must beEqualTo("ab")
      norm.clean("aaB") must beEqualTo("aab")
      norm.clean("aaBb") must beEqualTo("aa bb")
      norm.clean("aaBB") must beEqualTo("aa bb")
      norm.clean("AaBb") must beEqualTo("aabb")
      norm.clean("AaaBb") must beEqualTo("aaa bb")
      norm.clean("camelCase CamelCase cAMELcASE") must beEqualTo("camel case camel case camelcase")
    }
  }
}

