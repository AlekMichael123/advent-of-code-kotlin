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
   *  "when a ghost loops every X steps, then it will be at a finish point after Y steps if and only if Y is a multiple of X.
   *
   *  n theory, it would be possible that a ghost loops every 3 steps, but visits the finish point at steps 2, 5, 8, 11, ...
   *  It would also be possible that a ghost loops every 5 steps and is at a finish point at steps 3, 5, 8, 10, 13, 15, ...
   *
   *  But no, it just happens that the 3 ghost is at the finish at steps 3, 6, 9, 12, 15, 18 and the 5 ghost is at the finish point at steps 5, 10, 15, 20, ...
   *
   *  The first time these two ghosts are at the finish point at the same time is exactly at the least common multiple of 3 and 5, namely 15"
   *
   * I don't really get this solution but maybe if I draw it out it'll make more sense. I hate that the example inputs
   * AoC gives don't expose what the input is doing, and you're just supposed to waste time deciphering the ghosts to figure it out.
   *
   * EDIT: Shout out to Todd Ginsberg for a great explanation of this problem and exposing tailrec to me:
   * https://todd.ginsberg.com/post/advent-of-code/2023/day8/
   */

  override fun part2(input: String) {
    val (instructions, nodeMap) = parseInput(input, reversed = true)
    val startingLocations = nodeMap.keys.filter { it[0] == 'A' }
    val steps =
      startingLocations
        .map { followInstructions(instructions, nodeMap, it) }
        .fold(1L) { acc, steps -> acc.lcm(steps.toLong()) }

    println("Total steps taken: $steps")
  }

  private tailrec fun followInstructions(
    instructions: String,
    nodeMap: Map<String, Pair<String, String>>,
    currLocation: String = "AAA",
    i: Int = 0,
    steps: Int = 0,
  ): Int =
    if (currLocation[0] == 'Z')
      steps
    else if (instructions[i] == 'L')
      followInstructions(
        instructions,
        nodeMap,
        currLocation = nodeMap[currLocation]!!.first,
        i = (i + 1) % instructions.length,
        steps = steps + 1,
      )
    else
      followInstructions(
        instructions,
        nodeMap,
        currLocation = nodeMap[currLocation]!!.second,
        i = (i + 1) % instructions.length,
        steps = steps + 1,
      )


  private tailrec fun Long.gcd(a: Long): Long =
    if (a == 0L) this
    else a.gcd(this % a)

  private fun Long.lcm(a: Long) =
    this * a / this.gcd(a)


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