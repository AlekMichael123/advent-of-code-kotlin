package aoc.year2023

import aoc.Day

class Day10 : Day {
  private enum class PipeChar(val char: Char) {
    NorthToSouth('|'), // 0 self pair
    EastToWest('-'), // 1 self pair
    NorthToEast('L'),// 2
    NorthToWest('J'), // 2
    SouthToWest('7'),// 3
    SouthToEast('F'), //3
    Nothing('.'),
    CreatureStart('S'),
    Filler('0'),
    ExpandedSpace('#');
    fun expandPipe() = when (this) {
      NorthToSouth ->
        listOf(
          ExpandedSpace, NorthToSouth, ExpandedSpace,
          ExpandedSpace, NorthToSouth, ExpandedSpace,
          ExpandedSpace, NorthToSouth, ExpandedSpace,
        )
      EastToWest ->
        listOf(
          ExpandedSpace, ExpandedSpace, ExpandedSpace,
          EastToWest,    EastToWest,    EastToWest,
          ExpandedSpace, ExpandedSpace, ExpandedSpace,
        )
      NorthToEast ->
        listOf(
          ExpandedSpace, NorthToSouth,  ExpandedSpace,
          ExpandedSpace, NorthToEast,   EastToWest,
          ExpandedSpace, ExpandedSpace, ExpandedSpace,
        )
      NorthToWest ->
        listOf(
          ExpandedSpace, NorthToSouth,  ExpandedSpace,
          EastToWest, NorthToWest,      ExpandedSpace,
          ExpandedSpace, ExpandedSpace, ExpandedSpace,
        )
      SouthToWest ->
        listOf(
          ExpandedSpace, ExpandedSpace, ExpandedSpace,
          EastToWest, SouthToWest,      ExpandedSpace,
          ExpandedSpace, NorthToSouth,  ExpandedSpace,
        )
      SouthToEast ->
        listOf(
          ExpandedSpace, ExpandedSpace, ExpandedSpace,
          ExpandedSpace, SouthToEast,   EastToWest,
          ExpandedSpace, NorthToSouth,  ExpandedSpace,
        )
      CreatureStart ->
        listOf(
          CreatureStart, CreatureStart, CreatureStart,
          CreatureStart, CreatureStart, CreatureStart,
          CreatureStart, CreatureStart, CreatureStart,
        )
      else ->
        listOf(
          ExpandedSpace, ExpandedSpace, ExpandedSpace,
          ExpandedSpace, Nothing,       ExpandedSpace,
          ExpandedSpace, ExpandedSpace, ExpandedSpace,
        )
    }
    companion object {
      fun from(findValue: Char) = entries.first { it.char == findValue }
     }
  }

  private data class Route(var position: Pair<Int, Int>, var prev: Pair<Int, Int>, var steps: Int = 0)

  override fun part1(input: String) {
    val data = parseInput(input)
    val (i, j) = findCreatureStart(data)
    val result = findFurthestPositionFromStart(i, j, data)
    println("Furthest distance is $result")
  }

  override fun part2(input: String) {
    val data = parseInput(input)
    val result =
      countNothingSpaces(
        fillOutsideNothingSpaces(
          expandGrid(
            fillNonLoopPipes(
              data))))
    println("Total enclosed empty spaces: $result")
  }

  private fun fillNonLoopPipes(data: List<MutableList<PipeChar>>): List<MutableList<PipeChar>> {
    val (startI, startJ) = findCreatureStart(data)
    val (lengthI, lengthJ) = findGridLengths(data)
    var routes = generateRoutes(data, startI, startJ, lengthI, lengthJ)
    val loopingPipes = mutableSetOf(startI to startJ).also { it.addAll(routes.map { r -> r.position }) }
    while (routes.isNotEmpty()) {
      routes = applyPipeLogic(routes, data).filter {
        val (i, j) = it.position
        val endOfRoute =
          outOfBounds(i, j, lengthI, lengthJ)
            || data[i][j] == PipeChar.Nothing
            || data[i][j] == PipeChar.CreatureStart

        loopingPipes.add(it.position)
        !endOfRoute
      }
    }

    return data.mapIndexed { i, row ->
      row.mapIndexed { j, cell ->
        if (cell != PipeChar.Nothing && !loopingPipes.contains(i to j))
          PipeChar.ExpandedSpace
        else
          cell
      }.toMutableList()
    }
  }

  private fun expandGrid(data: List<MutableList<PipeChar>>) =
    data.fold(mutableListOf<MutableList<PipeChar>>()) { acc, row ->
      val values = row.map { it.expandPipe() }
      acc.addAll(List(3) { mutableListOf() })

      values.forEach { value ->
        acc[acc.size - 3].addAll(value.subList(0, 3))
        acc[acc.size - 2].addAll(value.subList(3, 6))
        acc[acc.size - 1].addAll(value.subList(6, 9))
      }

      acc
    }

  private fun fillOutsideNothingSpaces(data: List<MutableList<PipeChar>>): List<MutableList<PipeChar>> {
    val dataCopy = data.map { it.toMutableList() }
    val (lengthI, lengthJ) = findGridLengths(dataCopy)
    val directions = listOf(
      1 to 0,
      -1 to 0,
      0 to 1,
      0 to -1,
    )

    val candidates = mutableListOf(0 to 0)
    while (candidates.isNotEmpty()) {
      val position = candidates.removeLast()
      val (i, j) = position

      if (outOfBounds(i, j, lengthI, lengthJ) || (dataCopy[i][j] != PipeChar.Nothing && dataCopy[i][j] != PipeChar.ExpandedSpace))
        continue
      dataCopy[i][j] = PipeChar.Filler
      for ((iOff, jOff) in directions)
        candidates.add((iOff + i) to (jOff + j))
    }

    return dataCopy
  }

  private fun countNothingSpaces(data: List<MutableList<PipeChar>>) =
    data.fold(0) { acc, row -> acc + row.count { it == PipeChar.Nothing } }

  private fun findCreatureStart(data: List<MutableList<PipeChar>>): Pair<Int, Int> {
    val startI = data.indexOfFirst { it.contains(PipeChar.CreatureStart) }
    val startJ = data[startI].indexOf(PipeChar.CreatureStart)
    return startI to startJ
  }

  private fun findFurthestPositionFromStart(
    startI: Int,
    startJ: Int,
    data: List<MutableList<PipeChar>>,
  ): Int {
    val (lengthI, lengthJ) = findGridLengths(data)
    var routes = generateRoutes(data, startI, startJ, lengthI, lengthJ)
    var furthestDistance = 0

    while (routes.isNotEmpty()) {
      routes = applyPipeLogic(routes, data).filter {
        val (i, j) = it.position
        val endOfRoute = outOfBounds(i, j, lengthI, lengthJ) || data[i][j] == PipeChar.Nothing
        if (!endOfRoute && data[i][j] == PipeChar.CreatureStart)
          furthestDistance = furthestDistance.coerceAtLeast(it.steps / 2)
        !endOfRoute && data[i][j] != PipeChar.CreatureStart
      }
    }

    return furthestDistance + 1
  }

  private fun applyPipeLogic(
    routes: List<Route>,
    data: List<MutableList<PipeChar>>,
  ) = routes.map { route ->
    pipeInteraction(route, pipe = data[route.position.first][route.position.second])
  }

  private fun findGridLengths(data: List<MutableList<PipeChar>>): Pair<Int, Int> {
    val lengthI = data.size
    val lengthJ = data[0].size
    return lengthI to lengthJ
  }

  private fun generateRoutes(data: List<MutableList<PipeChar>>, startI: Int, startJ: Int, lengthI: Int, lengthJ: Int) =
    listOf(
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

  private fun pipeInteraction(
    route: Route,
    pipe: PipeChar,
  ): Route {
    val (i, j) = route.position
    val prev = route.prev

    val north = i - 1 to j
    val south = i + 1 to j
    val west  = i to j - 1
    val east  = i to j + 1

    val nextPosition = when (pipe) {
      PipeChar.NorthToSouth -> if (north == prev) south else north
      PipeChar.EastToWest   -> if (east == prev)  west  else east
      PipeChar.NorthToEast  -> if (north == prev) east  else north
      PipeChar.NorthToWest  -> if (north == prev) west  else north
      PipeChar.SouthToWest  -> if (south == prev) west  else south
      PipeChar.SouthToEast  -> if (south == prev) east  else south
      else -> -1 to -1
    }

    return Route(
      position = nextPosition,
      prev = route.position,
      steps = route.steps + 1,
    )
  }

  private fun outOfBounds(i: Int, j: Int, lengthI: Int, lengthJ: Int) =
    i < 0 || i >= lengthI || j < 0 || j >= lengthJ

  private fun parseInput(input: String) =
    input.lines().map { line -> line.map { PipeChar.from(it) }.toMutableList() }
}