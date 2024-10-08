package aoc.year2023

import aoc.Day

class Day9 : Day {
  override fun part1(input: String) {
    val data = parseInput(input)
    val result = data.fold(0) { acc, d ->
      acc + calculateDifferences(d, beginning = false).fold(0) { total, diff -> total + diff }
    }
    println("Total is $result")
  }

  override fun part2(input: String) {
    val data = parseInput(input)
    val result = data.fold(0) { acc, d ->
      acc + calculateDifferences(d, beginning = true).reversed().foldIndexed(0) { i, total, diff ->
        if (i == 0) diff
        else diff - total
      }
    }
    println("Total is $result")
  }

  private fun calculateDifferences(values: List<Int>, beginning: Boolean): List<Int> {
    val differences = mutableListOf(if (beginning) values.first() else values.last())
    var currDifferences = values.mapIndexed { i, value ->
      if (i < values.size - 1) values[i + 1] - value
      else null
    }.filterNotNull()

    while (currDifferences.any { it != 0 }) {
      differences.add(if (beginning) currDifferences.first() else currDifferences.last())
      currDifferences = currDifferences.mapIndexed { i, value ->
        if (i < currDifferences.size - 1)
          currDifferences[i + 1] - value
        else null
      }.filterNotNull()
    }

    return differences
  }

  private fun parseInput(input: String) =
    input
      .lines()
      .map {
        it
          .split(" ")
          .map(String::toInt)
      }
}