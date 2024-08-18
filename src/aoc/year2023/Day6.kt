package aoc.year2023

import aoc.Day

/**
 * Definitely not the most efficient solution, just using imperative approach, but it's quick enough that I don't really
 * care. Maybe I should investigate a better solution I bet there's an O(1) math way of doing it but waiting like half
 * a second isn't that big of a deal for an advent of code problem.
 */
class Day6 : Day {
  override fun part1(input: String) {
    val (times, distances) = parseInput(input)
    val result = times.indices.fold(1L) { acc, i ->
      val time = times[i]
      val distance = distances[i]
      acc * calculateAmtOfDifferentWins(time, distance)
    }
    println("Total amount of ways to beat races are: $result")
  }

  override fun part2(input: String) {
    val (time, distance) = parseInput2(input)
    val result = calculateAmtOfDifferentWins(time, distance)
    println("Total amount of ways to beat races are: $result")
  }

  private fun calculateAmtOfDifferentWins(time: Long, distance: Long): Long {
    var buttonHoldTime = 1L
    var amtWinning = 0L
    while ((time - buttonHoldTime) * buttonHoldTime > 0) {
      buttonHoldTime++

      if ((time - buttonHoldTime) * buttonHoldTime > distance)
        amtWinning++
    }
    return amtWinning
  }

  private fun parseInput(input: String) =
    input
      .split("Time:", "Distance:")
      .drop(1)
      .map {
        it.trim()
          .split(" ")
          .filter(String::isNotEmpty)
          .map(String::toLong)
      }

  private fun parseInput2(input: String) =
    parseInput(input).map {
      it.joinToString("").toLong()
    }
}