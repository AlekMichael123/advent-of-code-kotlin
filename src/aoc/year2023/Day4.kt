package aoc.year2023

import aoc.Day

class Day4 : Day {
  override fun part1(input: String) {
    println("Part 1")
    val result = playGame(splitInput(input))
    println("Total score: $result")
  }

  override fun part2(input: String) {
    println("Part 2")
    val cards = splitInput(input)
    val totalCards = countCards(cards)
    println("Total cards: $totalCards")
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

  private fun countCards(cards: List<Pair<List<String>, List<String>>>): Int =
    cards.foldIndexed(IntArray(cards.size)) { i, instances, (winningNumbers, myNumbers) ->
      instances[i]++ // count cards[i] itself
      val totalWins = myNumbers.count { winningNumbers.contains(it) }
      for (index in i+1..i+totalWins) {
        if (index >= cards.size) break
        instances[index] += instances[i]
      }
      instances
    }.fold(0) { totalCards, instance ->
      totalCards + instance
    }

  private fun splitInput(input: String): List<Pair<List<String>, List<String>>> =
    input.lines().map { line ->
      val (_, winningNumbers, myNumbers) = line.split(":", "|")

      winningNumbers.trim().split(" ").filter { it.isNotEmpty() } to
      myNumbers.trim().split(" ").filter { it.isNotEmpty() }
    }

}