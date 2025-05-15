package aoc.year2023

import aoc.Day
import kotlin.streams.toList

class Day15 : Day {
  override fun part1(input: String) {
    val data = parseInput(input)
    val result = data.fold(0) { acc, str ->
      acc + hash(str)
    }
    println("Result is $result")
  }

  override fun part2(input: String) {
    val data = parseInput(input)
    val boxes = MutableList(256) { mutableListOf<Pair<String, Int>>() }
    data.forEach { value ->
      if (value.contains("-")) {
        val label = value.filter { it != '-'}
        val hashCode = hash(label)
        boxes[hashCode] = boxes[hashCode].filter { it.first != label }.toMutableList()
      } else {
        val (label, lengthStr) = value.split("=")
        val length = lengthStr.toInt()
        val hashCode = hash(label)
        if (boxes[hashCode].any { it.first == label }) {
          val i = boxes[hashCode].indexOfFirst { it.first == label }
          boxes[hashCode][i] = boxes[hashCode][i].copy(second = length)
        } else {
          boxes[hashCode].add(label to length)
        }
      }
    }

    val result = boxes.foldIndexed(0) { i, acc, items ->
      acc + items.foldIndexed(0) { j, acc, (_, length) ->
        acc + ((i + 1) * (j + 1) * length)
      }
    }

    println("Result is $result")
  }

  private fun hash(str: String) =
    str.chars().toList().fold(0) { acc, char ->
      ((acc + char) * 17) % 256
    }

  private fun parseInput(input: String) =
    input.split(",")
}