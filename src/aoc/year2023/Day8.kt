package aoc.year2023

import aoc.Day

class Day8 : Day {
  override fun part1(input: String) {
    val (instructions, nodeMap) = parseInput(input)
    val steps = followInstructions(
      instructions,
      nodeMap,
    )
    println("Total steps taken: $steps")
  }

  /**
   * AN RANT: This was a stupid gimmick puzzle that entirely relies on sifting through outputs trying to find loops
   * that the ghosts take. I don't like this solution at all and has no real applicable strategy for real life since
   * the input had to be finely crafted for LCM to work. Stupid problem, IMO, but at least it showed to me that LCM
   * works in a case like this.
   *
   * Basically, you need to realize that the ghosts fall into loops of equal length then have the intuition that the LCM of
   * all the amount of steps to repeat will grant you the solution.
   *
   * From Reddit:
   *  "when a ghost loops every X steps, then it will be at a finish point after Y steps if and only if Y is a multiple of X."
   *
   * I don't really get this solution but maybe if I draw it out it'll make more sense. I hate that the example inputs
   * AoC gives don't expose what the input is doing and you're just supposed to waste time deciphering the ghosts to figure it out.
   */

  override fun part2(input: String) {
    val (instructions, nodeMap) = parseInput(input, reversed = true)
    val startingLocations = nodeMap.keys.filter { it[0] == 'A' }
    val steps =
      startingLocations
        .map { followInstructions(instructions, nodeMap, it) }
        .fold(1L) { acc, steps -> lcm(acc, steps.toLong()) }

    println("Total steps taken: $steps")
  }

  private fun followInstructions(
    instructions: String,
    nodeMap: Map<String, Pair<String, String>>,
    startLocation: String = "AAA",
  ): Int {
    var currLocation = startLocation
    var i = 0
    var steps = 0
    while (currLocation[0] != 'Z') {
      if (instructions[i] == 'L')
        currLocation = nodeMap[currLocation]!!.first
      else
        currLocation = nodeMap[currLocation]!!.second

      steps++
      i = (i + 1) % instructions.length
    }
    return steps
  }

  private fun gcd(a: Long, b: Long): Long {
    val remainder = a % b
    return if (remainder == 0L) b else gcd(b, remainder)
  }

  private fun lcm(a: Long, b: Long): Long {
    return a * b / gcd(a, b)
  }

  private fun parseInput(input: String, reversed: Boolean = false): Pair<String, Map<String, Pair<String, String>>> {
    val lines = input.lines().filter(String::isNotEmpty)
    val nodeMap = lines.drop(1).associate { relation ->
      val (src, destinations) = relation.split(" = ")
      val (leftDestination, rightDestination) = destinations.split("(", ", ", ")").drop(1).dropLast(1)
      if (reversed)
        src.reversed() to (leftDestination.reversed() to rightDestination.reversed())
      else
        src to (leftDestination to rightDestination)
    }
    return lines[0] to nodeMap
  }
}