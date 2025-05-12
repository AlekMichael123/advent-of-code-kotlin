package aoc.year2023

import aoc.Day

class Day13 : Day {
  override fun part1(input: String) {
    val data = parseInput(input)
    val result = data.foldIndexed(0) { i, acc, yard ->
      acc + (100 * findReflectiveRowIndex(yard)) + findReflectiveColumnIndex(yard)
    }
    println("Result is $result")
  }

  override fun part2(input: String) {
    val data = parseInput(input)
    val reflectionRows = data.map { findReflectiveRowIndex(it) }
    val reflectionColumns = data.map { findReflectiveColumnIndex(it) }
    var result = 0

    data.forEachIndexed forEachIndexed@{ i, mirror ->
      mirror.indices.forEach { y ->
        mirror[y].indices.forEach { x ->
          val copy = mirror.map { StringBuilder(it) }
          copy[y][x] = if (copy[y][x] == '.') '#' else '.'

          val rowReflection = findReflectiveRowIndex(copy.map { it.toString() }, originalAnswer = reflectionRows[i]-1)
          val columnReflection = findReflectiveColumnIndex(copy.map { it.toString() }, originalAnswer = reflectionColumns[i]-1)

          if (rowReflection != 0 || columnReflection != 0) {
            result += (100 * rowReflection) + columnReflection
            return@forEachIndexed
          }
        }
      }
    }
    println("Result is $result")
  }

  private fun findReflectiveRowIndex(data: List<String>, originalAnswer: Int = -1) =
    data.indices.indexOfFirst { i ->
      if (i == data.lastIndex || i == originalAnswer) return@indexOfFirst false
      var first = i
      var last = i+1
      while (first >= 0 && last <= data.lastIndex) {
        if (data[first] != data[last]) {
          return@indexOfFirst false
        }
        first--
        last++
      }
      true
    } + 1

  private fun findReflectiveColumnIndex(data: List<String>, originalAnswer: Int = -1) =
    data.first().indices.indexOfFirst { j ->
      if (j == data.first().lastIndex || j == originalAnswer) return@indexOfFirst false
      var first = j
      var last = j + 1
      while (first >= 0 && last <= data.first().lastIndex) {
        if (data.map { it[first] } != data.map { it[last] }) {
          return@indexOfFirst false
        }
        first--
        last++
      }
      true
    } + 1

  private fun parseInput(input: String) =
    input.split("\n\n").map(String::lines)
}