package aoc.year2023
import aoc.Day
import kotlin.math.abs

typealias ExpandedUniverseInformation = Pair<List<Boolean>, List<Boolean>>

class Day11 : Day {
  override fun part1(input: String) {
    val result = getShortestPathsBetweenGalaxies(data = parseInput(input), expansionScale = 2)
    println("Sum of all shortest paths is $result")
  }

  override fun part2(input: String) {
    val result = getShortestPathsBetweenGalaxies(data = parseInput(input), expansionScale = 1_000_000)
    println("Sum of all shortest paths is $result")
  }

  private fun getShortestPathsBetweenGalaxies(data: List<String>, expansionScale: Int) =
    findGalaxies(data).let { galaxyPositions ->
      expandUniverse(data).let { (emptyRowIndices, emptyColIndices) ->
        galaxyPositions.mapIndexed { i, (srcI, srcJ) ->
          galaxyPositions.mapIndexed { j, (targetI, targetJ) ->
            if (i >= j) 0
            else {
              val rows = if (srcI > targetI) srcI downTo targetI else srcI..targetI
              val cols = if (srcJ > targetJ) srcJ downTo targetJ else srcJ..targetJ
              var emptyAmt = 0

              for (row in rows) if (emptyRowIndices[row]) emptyAmt++
              for (col in cols) if (emptyColIndices[col]) emptyAmt++

              abs(srcI - targetI) + abs(srcJ - targetJ) + (emptyAmt * expansionScale.dec())
            }
          }
        }
      }.flatten().fold(0L) { acc, sum -> acc + sum }
    }

  private fun findGalaxies(data: List<String>) =
    data.mapIndexed { i, row ->
      row.mapIndexedNotNull { j, char -> (i to j).takeIf { char == '#' } }
    }.flatten()

  private fun expandUniverse(data: List<String>): ExpandedUniverseInformation {
    val emptyRowIndices = data.map { row -> isAllEmptyRow(row) }
    val emptyColIndices = data.first().indices.map { j -> isAllEmptyCol(data, j) }

    return emptyRowIndices to emptyColIndices
  }

  private fun isAllEmptyRow(row: String) =
    row.all { it == '.' }

  private fun isAllEmptyCol(data: List<String>, j: Int) =
    data.all { it[j] == '.' }

  private fun parseInput(input: String) =
    input.lines()
}

