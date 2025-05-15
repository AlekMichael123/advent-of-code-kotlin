package aoc.year2023

import aoc.Day

class Day16 : Day {
  private enum class Directions(val direction: Pair<Int, Int>) {
    NORTH(-1 to 0),
    SOUTH(1 to 0),
    EAST(0 to 1),
    WEST(0 to -1);
  }

  private data class Light(var direction: Directions, var position: Pair<Int, Int>)

  override fun part1(input: String) {
    val mirrors = parseInput(input)
    val visited = traceLight(mirrors)
    val result = traceLight(mirrors).fold(0) { acc, visited -> acc + visited.count { it } }
    println("Result is $result")
  }

  override fun part2(input: String) {
  }

  private fun traceLight(mirrors: List<CharArray>): Array<BooleanArray> {
    val n = mirrors.size
    val m = mirrors.first().size
    val visited = Array(n) { BooleanArray(m )}
    val lights = mutableListOf(Light(direction = Directions.EAST, position = 0 to 0))
    val visitedMap = mutableMapOf<Pair<Int, Int>, MutableList<Directions>>()
    val specialSpots = listOf('\\', '/', '|', '-')
    while (!lights.isEmpty()) {
      val (direction, position) = lights.removeFirst()
      val (y, x) = position
      val (dy, dx) = direction.direction
      val northward = y + Directions.NORTH.direction.first to x + Directions.NORTH.direction.second
      val southward = y + Directions.SOUTH.direction.first to x + Directions.SOUTH.direction.second
      val westward = y + Directions.WEST.direction.first to x + Directions.WEST.direction.second
      val eastward = y + Directions.EAST.direction.first to x + Directions.EAST.direction.second
      val onward = y + dy to x + dx
      if (specialSpots.contains(mirrors[y][x])) {
        if (visitedMap.contains(position) && visitedMap[position]!!.contains(direction)) {
          continue
        } else {
          visitedMap.putIfAbsent(position, mutableListOf())
          visitedMap[position]!!.add(direction)
        }
      }
      if (outOfBounds(y, x, n, m)) continue
      // mark space as visited
      visited[y][x] = true
      when (mirrors[y][x]) {
        '|' -> if (direction == Directions.WEST || direction == Directions.EAST) {
          if (!outOfBounds(northward.first, northward.second, n, m))
            lights.add(Light(direction = Directions.NORTH, position = northward))
          if (!outOfBounds(southward.first, southward.second, n, m))
            lights.add(Light(direction = Directions.SOUTH, position = southward))
        } else {
          if (!outOfBounds(onward.first, onward.second, n, m))
            lights.add(Light(direction = direction, position = onward))
        }
        '-' -> if (direction == Directions.NORTH || direction == Directions.SOUTH) {
          if (!outOfBounds(westward.first, westward.second, n, m))
            lights.add(Light(direction = Directions.WEST, position = westward))
          if (!outOfBounds(eastward.first, eastward.second, n, m))
            lights.add(Light(direction = Directions.EAST, position = eastward))
        } else {
          if (!outOfBounds(onward.first, onward.second, n, m))
            lights.add(Light(direction = direction, position = onward))
        }
        '\\' -> when (direction) {
          Directions.NORTH -> {
            if (!outOfBounds(westward.first, westward.second, n, m))
              lights.add(Light(direction = Directions.WEST, position = westward))
          }
          Directions.EAST -> {
            if (!outOfBounds(southward.first, southward.second, n, m))
              lights.add(Light(direction = Directions.SOUTH, position = southward))
          }
          Directions.SOUTH -> {
            if (!outOfBounds(eastward.first, eastward.second, n, m))
              lights.add(Light(direction = Directions.EAST, position = eastward))
          }
          Directions.WEST -> {
            if (!outOfBounds(northward.first, northward.second, n, m))
              lights.add(Light(direction = Directions.NORTH, position = northward))
          }
        }
        '/' -> when (direction) {
          Directions.NORTH -> {
            if (!outOfBounds(eastward.first, eastward.second, n, m))
              lights.add(Light(direction = Directions.EAST, position = eastward))
          }
          Directions.EAST -> {
            if (!outOfBounds(northward.first, northward.second, n, m))
              lights.add(Light(direction = Directions.NORTH, position = northward))
          }
          Directions.SOUTH -> {
            if (!outOfBounds(westward.first, westward.second, n, m))
              lights.add(Light(direction = Directions.WEST, position = westward))
          }
          Directions.WEST -> {
            if (!outOfBounds(southward.first, southward.second, n, m))
              lights.add(Light(direction = Directions.SOUTH, position = southward))
          }
        }
        '.' ->
          if (!outOfBounds(onward.first, onward.second, n, m))
            lights.add(Light(direction = direction, position = onward))
      }
    }
    return visited
  }

  private fun outOfBounds(y: Int, x: Int, n: Int, m: Int) = x < 0 || x >= m || y < 0 || y >= n

  private fun parseInput(input: String) =
    input.lines().map { it.toCharArray() }
}