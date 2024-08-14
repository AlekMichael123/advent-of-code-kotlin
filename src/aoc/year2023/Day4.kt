package aoc.year2023

import aoc.Day

class Day4 : Day {
  override fun part1(input: String) {
    println(input)
    println("Part 1")
  }

  override fun part2(input: String) {
    println("Part 2")
  }

  private fun splitInput(input: String) {
    val (_, winningNumbers, myNumbers) = input.split(":", "|")
    println(winningNumbers)
    println(myNumbers)
  }
}