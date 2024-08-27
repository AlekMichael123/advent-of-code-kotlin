package aoc.year2023

import aoc.Day

class Day10 : Day {
  private enum class PipeChar(val char: Char) {
    NorthToSouth('|'),
    EastToWest('-'),
    NorthToEast('L'),
    NorthToWest('J'),
    SouthToWest('7'),
    SouthToEast('F'),
    Nothing('.'),
    CreatureStart('S');
    companion object {
      fun from(findValue: Char): PipeChar = entries.first { it.char == findValue }
    }
  }

  private data class Route(var position: Pair<Int, Int>, var prev: Pair<Int, Int>, var steps: Int = 0)

  override fun part1(input: String) {
    val data = parseInput(input)
    val i = data.indexOfFirst { it.contains(PipeChar.CreatureStart) }
    val j = data[i].indexOf(PipeChar.CreatureStart)
    val result = findFurthestPositionFromStart(i, j, data)
    println("Furthest distance is $result")
  }

  override fun part2(input: String) {
  }

  private fun findFurthestPositionFromStart(
    startI: Int,
    startJ: Int,
    data: List<MutableList<PipeChar>>,
    lengthI: Int = data.size,
    lengthJ: Int = data[0].size,
  ): Int {
    var routes = listOf(
      startI - 1 to startJ,
      startI + 1 to startJ,
      startI to startJ - 1,
      startI to startJ + 1,
    ).filter {
      val (i, j) = it
      !outOfBounds(i, j, lengthI, lengthJ) && data[i][j] != PipeChar.Nothing
    }.map {
      Route(
        position = it,
        prev = startI to startJ,
      )
    }
    var furthestDistance = 0

    while (routes.isNotEmpty()) {
      routes = routes.map { route ->
        val nextRoute = pipeInteraction(route, pipe = data[route.position.first][route.position.second])
        nextRoute
      }.filter {
        val (i, j) = it.position
        val endOfRoute = outOfBounds(i, j, lengthI, lengthJ) || data[i][j] == PipeChar.Nothing
        if (!endOfRoute && data[i][j] == PipeChar.CreatureStart)
          furthestDistance = furthestDistance.coerceAtLeast(it.steps / 2)
        !endOfRoute && data[i][j] != PipeChar.CreatureStart
      }
    }

    return furthestDistance + 1
  }

  private fun pipeInteraction(
    route: Route,
    pipe: PipeChar,
  ): Route {
    val (i, j) = route.position
    val prev = route.prev

    val north = i - 1 to j
    val south = i + 1 to j
    val west = i to j - 1
    val east = i to j + 1

    val nextPosition = when (pipe) {
      PipeChar.NorthToSouth -> if (north == prev) south else north
      PipeChar.EastToWest -> if (east == prev) west else east
      PipeChar.NorthToEast -> if (north == prev) east else north
      PipeChar.NorthToWest -> if (north == prev) west else north
      PipeChar.SouthToWest -> if (south == prev) west else south
      PipeChar.SouthToEast -> if (south == prev) east else south
      else -> -1 to -1
    }


    return Route(
      position = nextPosition,
      prev = route.position,
      steps = route.steps + 1
    )
  }


  private fun outOfBounds(i: Int, j: Int, lengthI: Int, lengthJ: Int) =
    i < 0 || i >= lengthI || j < 0 || j >= lengthJ

  private fun parseInput(input: String) =
    input.lines().map { line -> line.map { PipeChar.from(it) }.toMutableList() }
}