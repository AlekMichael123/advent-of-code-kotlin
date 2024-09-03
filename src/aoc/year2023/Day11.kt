package aoc.year2023

import aoc.Day
import kotlin.math.abs
class Day11 : Day {
  override fun part1(input: String) {
    val result = getShortestPathsBetweenGalaxies(expandUniverse(parseInput(input)))
    println("Sum of all shortest paths is $result")
  }

  override fun part2(input: String) {
  }

  private fun getShortestPathsBetweenGalaxies(data: List<String>) =
    findGalaxies(data).let { galaxyPositions ->
      galaxyPositions.mapIndexed { i, (i1, j1) ->
        galaxyPositions.mapIndexed { j, (i2, j2) ->
          if (i >= j) 0
          else abs(i1 - i2) + abs(j1 - j2)
        }
      }.flatten().fold(0) { acc, sum -> acc + sum }
    }

  private fun findGalaxies(data: List<String>) =
    data.mapIndexed { i, row ->
      row.mapIndexedNotNull { j, char -> (i to j).takeIf { char == '#' } }
    }.flatten()

  private fun expandUniverse(data: List<String>): List<String> {
    val rowExpansion = data.map { row ->
      if (allEmptyRow(row))
        listOf(row, ".".repeat(data.first().length))
      else
        listOf(row)
    }.flatten()

    val emptyColsIndices = data.first().indices.map { j -> allEmptyCol(data, j) }

    return rowExpansion.map { row ->
      row.mapIndexed { j, char ->
        if (emptyColsIndices[j]) ".".repeat(2)
        else char
      }.joinToString("")
    }
  }

  private fun allEmptyRow(row: String) =
    row.all { it == '.' }

  private fun allEmptyCol(data: List<String>, j: Int) =
    data.all { it[j] == '.' }

  private fun parseInput(input: String) =
    input.lines()
}

