package aoc.year2023

import aoc.Day

class Day14 : Day {
  override fun part1(input: String) {
    val rows = parseInput(input)
    val result = findLoadAfterTilingNorth(rows)
    println("Result is $result")
  }

  override fun part2(input: String) {
  }

  private fun findLoadAfterTilingNorth(board: List<List<Char>>): Int {
    val loads = MutableList(board.size) { MutableList(board.first().size) { 0 } }
    val highestPoints = MutableList(board.first().size) { 0 }

    board.forEachIndexed { i, row ->
      row.forEachIndexed { j, cell ->
        if (cell == 'O') {
          loads[highestPoints[j]][j]++
        } else if (cell == '#') {
          highestPoints[j] = i+1
        }
      }
    }

    var result = 0
    var height = board.size
    loads.forEach { row ->
      row.forEach { amt ->
        if (amt > 0) {
          var height = height
          var amt = amt
          while (amt > 0 && height > 0) {
            result += height
            height--
            amt--
          }
        }
      }
      height--
    }

    return result
  }

  private fun parseInput(input: String) =
    input.lines().map { it.toList() }
}