package aoc.year2023

import aoc.Day

class Day12 : Day {
  override fun part1(input: String) {
    val data = parseInput(input)
    println(data)
  }

  override fun part2(input: String) {
  }

  private fun parseInput(input: String) =
    input
      .lines()
      .map {
        val (record, sizes) = it.split(" ")
        record to sizes.split(",").map(String::toInt)
      }
}