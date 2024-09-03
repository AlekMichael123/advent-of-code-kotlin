package aoc.year2023

import aoc.Day
import kotlin.math.abs

class Day11 : Day {
  override fun part1(input: String) {
    val result = addShortestPathLengthsBetweenGalaxies(data = parseInput(input), expansionScale = 2)
    println("Sum of all shortest paths is $result")
  }

  override fun part2(input: String) {
    val result = addShortestPathLengthsBetweenGalaxies(data = parseInput(input), expansionScale = 1_000_000)
    println("Sum of all shortest paths is $result")
  }

  /**
   * LET IS AWESOME!!! Makes cool "one-liners" like this possible!
   */
  private fun addShortestPathLengthsBetweenGalaxies(data: List<String>, expansionScale: Int) =
    findGalaxies(data).let { galaxyPositions -> // find all galaxy positions
      expandUniverse(data).let { (emptyRows, emptyCols) -> // find out what rows/cols are empty
        galaxyPositions.mapIndexed { i, (iSource, jSource) -> // start at each galaxy "source"
          galaxyPositions.mapIndexed { j, (iTarget, jTarget) -> // find the shortest path from source to galaxy "target"
            if (i >= j) 0 // if we already found this path or traversing from source to itself
            else {
              // find indices that are going to be traversed.
              // being able to use count below is pretty sweet looking imo
              val rows = if (iSource > iTarget) iSource downTo iTarget else iSource..iTarget
              val cols = if (jSource > jTarget) jSource downTo jTarget else jSource..jTarget
              // count empty rows/cols
              val emptyAmt = rows.count { emptyRows[it] } + cols.count { emptyCols[it] }

              // distance between source and target + the number of empty rows you would've had to walk through
              abs(iSource - iTarget) + abs(jSource - jTarget) + (emptyAmt * expansionScale.dec())
            }
          }
        }
      }.flatten().fold(0L) { acc, sum -> acc + sum } // add up the path lengths
    }

  // finds galaxy positions
  private fun findGalaxies(data: List<String>) =
    data.mapIndexed { i, row ->
      row.mapIndexedNotNull { j, char -> (i to j).takeIf { char == '#' } }
    }.flatten()

  /**
   * Looking up other solutions, I could actually do something like this to have a list of indices to look through,
   * put them in a set that would be pretty slick too
   * i.e.
   * val emptyRowIndices = data.indices.filter { row -> row.all { it == '.' } }
   *
   * I'll never leave my List<Boolean> tho, and F ARRAY! ammirite lol (don't talk to me about Arrays)
   */
  private fun expandUniverse(data: List<String>): Pair<List<Boolean>, List<Boolean>> {
    val emptyRowIndices = data.map { row -> row.all { it == '.' } } // find indices of empty rows
    val emptyColIndices = data.first().indices.map { j -> data.all { it[j] == '.' } } // find indices of empty cols

    return emptyRowIndices to emptyColIndices
  }

  private fun parseInput(input: String) =
    input.lines()
}

