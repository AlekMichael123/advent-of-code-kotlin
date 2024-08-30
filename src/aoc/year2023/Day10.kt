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
    ExpandedSpace('#'),
    AnyPipe('X');
    companion object {
      fun from(findValue: Char) = entries.first { it.char == findValue }
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
    val data = parseInput(input)
    val removedNonLoopPipes = fillNonLoopPipes(data)
    val expandedData = expandGrid(removedNonLoopPipes)
    val filledData = fillOutsideNothingSpaces(expandedData)
    val result = countNothingSpaces(filledData)
    println("Total enclosed empty spaces: $result")
  }

  private fun countNothingSpaces(data: List<MutableList<PipeChar>>) =
    data.fold(0) { acc, row -> acc + row.count { it == PipeChar.Nothing } }
  private fun expandGrid(data: List<MutableList<PipeChar>>) =
    data.foldIndexed(mutableListOf<MutableList<PipeChar>>()) { i, acc, row ->
      val values = row.map {
        when (it) {
          PipeChar.NorthToSouth ->
            listOf(
              PipeChar.ExpandedSpace, PipeChar.NorthToSouth, PipeChar.ExpandedSpace,
              PipeChar.ExpandedSpace, PipeChar.NorthToSouth, PipeChar.ExpandedSpace,
              PipeChar.ExpandedSpace, PipeChar.NorthToSouth, PipeChar.ExpandedSpace,
            )
          PipeChar.EastToWest ->
            listOf(
              PipeChar.ExpandedSpace, PipeChar.ExpandedSpace, PipeChar.ExpandedSpace,
              PipeChar.EastToWest, PipeChar.EastToWest, PipeChar.EastToWest,
              PipeChar.ExpandedSpace, PipeChar.ExpandedSpace, PipeChar.ExpandedSpace,
            )
          PipeChar.NorthToEast ->
            listOf(
              PipeChar.ExpandedSpace, PipeChar.NorthToSouth, PipeChar.ExpandedSpace,
              PipeChar.ExpandedSpace, PipeChar.NorthToEast, PipeChar.EastToWest,
              PipeChar.ExpandedSpace, PipeChar.ExpandedSpace, PipeChar.ExpandedSpace,
            )
          PipeChar.NorthToWest ->
            listOf(
              PipeChar.ExpandedSpace, PipeChar.NorthToSouth, PipeChar.ExpandedSpace,
              PipeChar.EastToWest, PipeChar.NorthToWest, PipeChar.ExpandedSpace,
              PipeChar.ExpandedSpace, PipeChar.ExpandedSpace, PipeChar.ExpandedSpace,
            )
          PipeChar.SouthToWest ->
            listOf(
              PipeChar.ExpandedSpace, PipeChar.ExpandedSpace, PipeChar.ExpandedSpace,
              PipeChar.EastToWest, PipeChar.SouthToWest, PipeChar.ExpandedSpace,
              PipeChar.ExpandedSpace, PipeChar.NorthToSouth, PipeChar.ExpandedSpace,
            )
          PipeChar.SouthToEast ->
            listOf(
              PipeChar.ExpandedSpace, PipeChar.ExpandedSpace, PipeChar.ExpandedSpace,
              PipeChar.ExpandedSpace, PipeChar.SouthToEast, PipeChar.EastToWest,
              PipeChar.ExpandedSpace, PipeChar.NorthToSouth, PipeChar.ExpandedSpace,
            )
          PipeChar.CreatureStart ->
            listOf(
              PipeChar.CreatureStart, PipeChar.CreatureStart, PipeChar.CreatureStart,
              PipeChar.CreatureStart, PipeChar.CreatureStart, PipeChar.CreatureStart,
              PipeChar.CreatureStart, PipeChar.CreatureStart, PipeChar.CreatureStart,
            )
          else ->
            listOf(
              PipeChar.ExpandedSpace, PipeChar.ExpandedSpace, PipeChar.ExpandedSpace,
              PipeChar.ExpandedSpace, PipeChar.Nothing, PipeChar.ExpandedSpace,
              PipeChar.ExpandedSpace, PipeChar.ExpandedSpace, PipeChar.ExpandedSpace,
            )
        }
      }
      acc.add(mutableListOf())
      acc.add(mutableListOf())
      acc.add(mutableListOf())
      values.forEach { value ->
        acc[acc.size - 3].addAll(value.subList(0, 3))
        acc[acc.size - 2].addAll(value.subList(3, 6))
        acc[acc.size - 1].addAll(value.subList(6, 9))
      }

      acc
    }

  private val directions = listOf(
    1 to 0,
    -1 to 0,
    0 to 1,
    0 to -1,
  )

  private fun fillNonLoopPipes(data: List<MutableList<PipeChar>>): List<MutableList<PipeChar>> {
    val dataCopy = data.map { it.toMutableList() }
    val startI = data.indexOfFirst { it.contains(PipeChar.CreatureStart) }
    val startJ = data[startI].indexOf(PipeChar.CreatureStart)
    val lengthI = data.size
    val lengthJ = data[0].size
    var routes = generateRoutes(dataCopy, startI, startJ, lengthI, lengthJ)
    val loopingPipes = mutableSetOf(startI to startJ).also { it.addAll(routes.map { r -> r.position }) }
    while (routes.isNotEmpty()) {
      routes = routes.map { route ->
        pipeInteraction(route, pipe = data[route.position.first][route.position.second])
      }.filter {
        val (i, j) = it.position
        val endOfRoute =
          outOfBounds(i, j, lengthI, lengthJ) ||
            data[i][j] == PipeChar.Nothing ||
            data[i][j] == PipeChar.CreatureStart

        loopingPipes.add(it.position)
        !endOfRoute
      }
    }

    return dataCopy.mapIndexed { i, row ->
      row.mapIndexed { j, cell ->
        if (cell != PipeChar.Nothing && !loopingPipes.contains(i to j))
          PipeChar.ExpandedSpace
        else
          cell
      }.toMutableList()
    }
  }

  private fun fillOutsideNothingSpaces(data: List<MutableList<PipeChar>>): List<MutableList<PipeChar>> {
    val dataCopy = data.map { it.toMutableList() }
    val lengthI = dataCopy.size
    val lengthJ = dataCopy[0].size

    fun dfs(startI: Int, startJ: Int) {
      val candidates = mutableListOf(startI to startJ)
      while (candidates.isNotEmpty()) {
        val position = candidates.removeLast()
        val (i, j) = position

        if (outOfBounds(i, j, lengthI, lengthJ) || (dataCopy[i][j] != PipeChar.Nothing && dataCopy[i][j] != PipeChar.ExpandedSpace))
          continue
        dataCopy[i][j] = PipeChar.Filler
        for ((iOff, jOff) in directions)
          candidates.add((iOff + i) to (jOff + j))
      }
    }

    for (i in 0 ..< lengthI) {
      dfs(i, startJ = 0)
      dfs(i, startJ = lengthJ - 1)
    }

    for (j in 0 ..< lengthJ) {
      dfs(startI = 0, j)
      dfs(startI = lengthI - 1, j)
    }
//    dfs(0,0)

    return dataCopy
  }

  private fun findFurthestPositionFromStart(
    startI: Int,
    startJ: Int,
    data: List<MutableList<PipeChar>>,
  ): Int {
    val lengthI = data.size
    val lengthJ = data[0].size

    var routes = generateRoutes(data, startI, startJ, lengthI, lengthJ)

    var furthestDistance = 0

    while (routes.isNotEmpty()) {
      routes = routes.map { route ->
        pipeInteraction(route, pipe = data[route.position.first][route.position.second])
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
    val west = i to j - 1
    val east = i to j + 1

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