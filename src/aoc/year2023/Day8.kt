package aoc.year2023

import aoc.Day

class Day8 : Day {
  private val start = "AAA"
  private val destination = "ZZZ"
  override fun part1(input: String) {
    val (instructions, nodeMap) = parseInput(input)
    val steps = followInstructions(
      instructions,
      nodeMap,
    )
    println("Total steps taken: $steps")
  }

  override fun part2(input: String) {

  }

  private fun followInstructions(
    instructions: String,
    nodeMap: Map<String, Pair<String, String>>,
  ): Int {
    var currLocation = start
    var i = 0
    var steps = 0
    while (currLocation != destination) {
      if (instructions[i] == 'L')
        currLocation = nodeMap[currLocation]!!.first
      else
        currLocation = nodeMap[currLocation]!!.second

      steps++
      i = (i + 1) % instructions.length
    }
    return steps
  }

  private fun parseInput(input: String): Pair<String, Map<String, Pair<String, String>>> {
    val lines = input.lines().filter(String::isNotEmpty)
    val nodeMap = lines.drop(1).associate { relation ->
      val (src, destinations) = relation.split(" = ")
      val (leftDestination, rightDestination) = destinations.split("(", ", ", ")").drop(1).dropLast(1)
      src to (leftDestination to rightDestination)
    }
    return lines[0] to nodeMap
  }
}