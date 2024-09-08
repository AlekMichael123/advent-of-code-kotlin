package aoc.year2023

import aoc.Day

class Day13 : Day {
  override fun part1(input: String) {
    val data = parseInput(input)
    val result = data.foldIndexed(0) { i, acc, yard ->
      acc + (100 * findReflectiveRowIndex(yard)) + findReflectiveColumnIndex(yard)
    }
    println("Result is $result")
  }

  override fun part2(input: String) {
  }

  private fun findReflectiveRowIndex(data: List<String>) =
    data.indices.indexOfFirst { i ->
      if (i == data.lastIndex) return@indexOfFirst false
      var first = i
      var last = i+1
      while (first >= 0 && last <= data.lastIndex) {
        if (data[first] != data[last]) {
          return@indexOfFirst false
        }
        first--
        last++
      }
      true
    } + 1

  private fun findReflectiveColumnIndex(data: List<String>) =
    data.first().indices.indexOfFirst { j ->
      if (j == data.first().lastIndex) return@indexOfFirst false
      var first = j
      var last = j + 1
      while (first >= 0 && last <= data.first().lastIndex) {
        if (data.map { it[first] } != data.map { it[last] }) {
          return@indexOfFirst false
        }
        first--
        last++
      }
      true
    } + 1

  private fun parseInput(input: String) =
    input.split("\n\n").map(String::lines)
}