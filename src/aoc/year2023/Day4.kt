package aoc.year2023

import aoc.Day

class Day4 : Day {
  override fun part1(input: String) {
    val numbers = splitInput(input)
  }

  override fun part2(input: String) {
    println("Part 2")
  }

  private fun splitInput(input: String): List<Pair<List<String>, List<String>>> =
    input.lines().map { line ->
      val (_, winningNumbers, myNumbers) = line.split(":", "|")

      winningNumbers.trim().split(" ").filter { it.isNotEmpty() } to
      myNumbers.trim().split(" ").filter { it.isNotEmpty() }
    }

}