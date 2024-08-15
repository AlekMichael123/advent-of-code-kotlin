package aoc.year2023

import aoc.Day

class Day4 : Day {
  override fun part1(input: String) {
    println("Part 1")
    val result = playGame(splitInput(input))
    println("Total score is $result")
  }

  override fun part2(input: String) {
    println("Part 2")
    val cards = splitInput(input)
    val copies = IntArray(cards.size)
    val result = cards.foldIndexed(0) {  i, total, (winningNumbers, myNumbers) ->
      total
    }
    println("Total scorecards is $result")
  }

  private fun playGame(cards: List<Pair<List<String>, List<String>>>): Int =
    cards.fold(0) { total, (winningNumbers, myNumbers) ->
      total + myNumbers.fold(0) { acc, num ->
        if (winningNumbers.contains(num)) {
          if (acc == 0) 1 else acc * 2
        } else {
          acc
        }
      }
    }

  private fun splitInput(input: String): List<Pair<List<String>, List<String>>> =
    input.lines().map { line ->
      val (_, winningNumbers, myNumbers) = line.split(":", "|")

      winningNumbers.trim().split(" ").filter { it.isNotEmpty() } to
      myNumbers.trim().split(" ").filter { it.isNotEmpty() }
    }

}