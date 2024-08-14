package aoc.year2023

import aoc.Day

class Day4 : Day {
  override fun part1(input: String) {
    println("Part 1")
    val numbers = splitInput(input)
    val result = numbers.fold(0) { total, (winning, myNumbers) ->
      total + myNumbers.fold(0) { acc, num ->
        if (winning.contains(num)) {
          if (acc == 0) 1 else acc * 2
        } else {
          acc
        }
      }
    }
    println("Total score is $result")
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