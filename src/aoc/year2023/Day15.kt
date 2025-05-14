package aoc.year2023

import aoc.Day
import kotlin.streams.toList

class Day15 : Day {
  override fun part1(input: String) {
    val data = parseInput(input)
    val result = data.fold(0) { acc, str ->
      acc + hash(str)
    }
    println("Result is $result")
  }

  override fun part2(input: String) {
  }

  private fun hash(str: String) =
    str.chars().toList().fold(0) { acc, char ->
      ((acc + char) * 17) % 256
    }

  private fun parseInput(input: String) =
    input.split(",")
}