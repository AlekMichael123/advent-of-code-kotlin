package aoc.year2023

import aoc.Day

class Day14 : Day {
  override fun part1(input: String) {
    val board = parseInput(input)
    val result = findLoadAfterTilingNorth(board)
    println("Result is $result")
  }

  override fun part2(input: String) {
    var currBoard = parseInput(input)
    val totalCycles = 1_000_000_000
    var cycle = 0
    val stateCycleTracker = mutableMapOf<String, Int>()
    while (true) {
      val state = boardToString(currBoard)
      if (stateCycleTracker.contains(state)) {
        val whenSeenFirstState = stateCycleTracker[state]!!
        val cycleTimeSince = cycle - whenSeenFirstState

        val remainingCycles = (totalCycles - whenSeenFirstState) % cycleTimeSince

        repeat(remainingCycles) {
          currBoard = performCycle(currBoard)
        }

        break
      }

      stateCycleTracker[state] = cycle++
      currBoard = performCycle(currBoard)
    }
    val result = calcLoadEast(tilt(currBoard, 'E').first, currBoard)
    println("Result is $result")
  }

  private fun boardToString(board: List<List<Char>>) = board.joinToString(",") { it.joinToString(",") }

  private fun findLoadAfterTilingNorth(board: List<List<Char>>): Int {
    val (loads, _) = tilt(board, 'N')
    return calcLoadNorth(loads, board)
  }

  private fun calcLoadEast(loads: MutableList<MutableList<Int>>, board: List<List<Char>>): Int {
    var result = 0
    var height = board.size
    loads.forEach { row ->
      row.forEach { amt ->
        if (amt > 0) {
          result += amt * height
        }
      }
      height--
    }

    return result
  }

  private fun calcLoadNorth(loads: MutableList<MutableList<Int>>, board: List<List<Char>>): Int {
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

  private fun tilt(board: List<List<Char>>, direction: Char): Pair<MutableList<MutableList<Int>>, MutableList<MutableList<Int>>> {
    val loads = MutableList(board.size) { MutableList(board.first().size) { 0 } }
    var highestPoints = mutableListOf<MutableList<Int>>()
    when (direction) {
      'N' -> {
        highestPoints = MutableList(board.first().size) { mutableListOf(0) }
        board.forEachIndexed { i, row ->
          row.forEachIndexed { j, cell ->
            if (cell == 'O') {
              loads[highestPoints[j][highestPoints[j].lastIndex]][j]++
            } else if (cell == '#') {
              highestPoints[j].add(i + 1)
            }
          }
        }
      }
      'S' -> {
        highestPoints = MutableList(board.first().size) { mutableListOf(board.size - 1) }
        board.indices.reversed().forEach { i ->
          val row = board[i]
          row.forEachIndexed { j, cell ->
            if (cell == 'O') {
              loads[highestPoints[j][highestPoints[j].lastIndex]][j]++
            } else if (cell == '#') {
              highestPoints[j].add(i - 1)
            }
          }
        }
      }
      'W' -> {
        highestPoints = MutableList(board.first().size) { mutableListOf(0) }
        board.first().indices.forEach { j ->
          board.forEachIndexed { i, row ->
            val cell = row[j]
            if (cell == 'O') {
              loads[i][highestPoints[i][highestPoints[i].lastIndex]]++
            } else if (cell == '#') {
              highestPoints[i].add(j + 1)
            }
          }
        }
      }
      'E' -> {
        highestPoints = MutableList(board.first().size) { mutableListOf(board.first().size - 1) }
        board.first().indices.reversed().forEach { j ->
          board.forEachIndexed { i, row ->
            val cell = row[j]
            if (cell == 'O') {
              loads[i][highestPoints[i][highestPoints[i].lastIndex]]++
            } else if (cell == '#') {
              highestPoints[i].add(j - 1)
            }
          }
        }
      }
    }

    return loads to highestPoints
  }

  private fun buildBoard(loads: MutableList<MutableList<Int>>, highestPoints: MutableList<MutableList<Int>>, fromDirection: Char): List<List<Char>> {
    val result = MutableList(loads.size) { MutableList(loads.first().size) { '.' } }
    return when (fromDirection) {
      'N' -> {
        loads.forEachIndexed { i, row ->
          row.forEachIndexed { j, load ->
            var i = i
            if (load > 0)
              (1..load).forEach { _ ->
                result[i++][j] = 'O'
              }
            highestPoints[j].forEach { point ->
              if (point != 0) {
                result[point - 1][j] = '#'
              }
            }
          }
        }
        result
      }
      'S' -> {
        loads.indices.reversed().forEach { i ->
          val row = loads[i]
          row.forEachIndexed { j, load ->
            var i = i
            if (load > 0)
              (1..load).forEach { _ ->
                result[i--][j] = 'O'
              }
            highestPoints[j].forEach { point ->
              if (point != loads.size - 1) {
                result[point + 1][j] = '#'
              }
            }
          }
        }
        result
      }
      'W' -> {
        loads.first().indices.forEach { j ->
          loads.forEachIndexed { i, row ->
            var j = j
            val load = row[j]
            if (load > 0)
              (1..load).forEach { _ ->
                result[i][j++] = 'O'
              }
            highestPoints[i].forEach { point ->
              if (point != 0) {
                result[i][point - 1] = '#'
              }
            }
          }
        }
        result
      }
      'E' -> {
        loads.first().indices.reversed().forEach { j ->
          loads.forEachIndexed { i, row ->
            var j = j
            val load = row[j]
            if (load > 0)
              (1..load).forEach { _ ->
                result[i][j--] = 'O'
              }
            highestPoints[i].forEach { point ->
              if (point != loads.first().size - 1) {
                result[i][point + 1] = '#'
              }
            }
          }
        }
        result
      }
      else -> {
        result
      }
    }
  }

  private fun performCycle(board: List<List<Char>>): List<List<Char>> {
    val north = tilt(board, 'N')
    val west = tilt(buildBoard(north.first, north.second, 'N'), 'W')
    val south = tilt(buildBoard(west.first, west.second, 'W'), 'S')
    val east = tilt(buildBoard(south.first, south.second, 'S'), 'E')
    return buildBoard(east.first, east.second, 'E')
  }

  private fun parseInput(input: String) =
    input.lines().map { it.toList() }
}