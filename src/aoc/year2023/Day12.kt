package aoc.year2023

import aoc.Day

class Day12 : Day {
  override fun part1(input: String) {
    val data = parseInput(input)
    val result = data.fold(0) { acc, (record, sizes) ->
      acc + countArrangements(record, sizes)
    }
    println("Total number of viable arrangements: $result")
  }

  override fun part2(input: String) {
    val data = unfold(parseInput(input))
    val result = data.fold(0) { acc, (record, sizes) ->
      acc + countArrangements(record, sizes)
    }
    println("Total number of viable arrangements after unfolding: $result")
  }

  private fun countArrangements(record: String, sizes: List<Int>, i: Int = 0): Int {
    if (i >= record.length) {
      var currLength = 0
      val filledInLocationsLengths = ("$record.").fold(mutableListOf<Int>()) { acc, c ->
        if (c != '#' && currLength > 0) {
          acc.add(currLength)
          currLength = 0
        }
        else if (c == '#') currLength++
        else currLength = 0
        acc
      }
      return if (filledInLocationsLengths == sizes) 1 else 0
    }
    if (record[i] != '?') return countArrangements(record, sizes, i + 1)

    val fill = countArrangements(record.replaceRange(range = i..i, replacement = "#"), sizes, i + 1)
    val notFilled = countArrangements(record.replaceRange(range = i..i, replacement = "."), sizes, i + 1)

    return fill + notFilled
  }

  private fun unfold(data: List<Pair<String, List<Int>>>) =
    data.map { (record, sizes) ->
      List(5) { record }.joinToString("?") to List(5) { sizes }.flatten()
    }

  private fun parseInput(input: String) =
    input
      .lines()
      .map {
        val (record, sizes) = it.split(" ")
        record to sizes.split(",").map(String::toInt)
      }
}