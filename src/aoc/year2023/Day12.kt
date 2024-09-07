package aoc.year2023

import aoc.Day

class Day12 : Day {
  private val memo = mutableMapOf<String, Long>()

  override fun part1(input: String) {
    val data = parseInput(input)
    val result = data.fold(0L) { acc, (record, sizes) ->
      val total = countArrangements(record, sizes)
      acc + total
    }
    println("Total number of viable arrangements: $result")
  }

  override fun part2(input: String) {
    val data = unfold(parseInput(input))
    val result = data.fold(0L) { acc, (record, sizes) ->
      acc + countArrangements(record, sizes)
    }
    println("Total number of viable arrangements after unfolding: $result")
  }

  private fun countArrangements(record: String, sizes: List<Int>): Long {
    if (record.isEmpty()) return if (sizes.isEmpty()) 1 else 0
    return memo.getOrPut("$record $sizes") {
      when (record.first()) {
        '.' -> countArrangements(record.trimStart { it == '.' }, sizes)
        '?' -> countArrangements(record.substring(1), sizes) + // skip
               countArrangements("#${record.substring(1)}", sizes) // fill
        '#' ->
          if (sizes.isEmpty()) 0
          else {
            val size = sizes.first()
            val remainingSizes = sizes.drop(1)
            if (size <= record.length && record.take(size).none { it == '.' })
              when {
                size == record.length -> if (remainingSizes.isEmpty()) 1 else 0
                record[size] == '#' -> 0
                else -> countArrangements(record.drop(size + 1), remainingSizes)
              }
            else 0
          }

        else -> throw IllegalStateException("Invalid input: $record")
      }
    }
  }

  private fun unfold(data: List<Pair<String, List<Int>>>) =
    data.map { (record, sizes) ->
      List(5) { record }.joinToString("?") to List(5) { sizes }.flatten()
    }

  private fun parseInput(input: String) =
    input
      .lines()
      .map {
        val (record, sizes) = it.split(" ")
        record to sizes.split(",").map(String::toInt)
      }
}