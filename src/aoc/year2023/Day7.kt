package aoc.year2023

import aoc.Day

class Day7 : Day {
  override fun part1(input: String) {
    val data = parseInput(input)
    val order = data.map { pair ->
      handOrder(pair.first) to pair
    }.fold(mutableMapOf<Int, MutableList<Pair<String, Int>>>()) { map, pair ->
      map.putIfAbsent(pair.first, mutableListOf())
      map[pair.first]!!.add(pair.second)
      map
    }

    val keys = order.keys
    keys.forEach { key ->
      order[key]!!.sortWith { (a, _), (b, _) ->
        var cmp = 0
        for (i in a.indices) {
          val aCharOrder = charOrder(a[i])
          val bCharOrder = charOrder(b[i])
          if (aCharOrder > bCharOrder) {
            cmp = -1
            break
          } else if (aCharOrder < bCharOrder) {
            cmp = 1
            break
          }
        }
        cmp
      }
    }
    val sorted = keys.sorted()
    var i = 1
    var result = 0

    sorted.forEach { rank ->
      val values = order[rank]!!
      values.forEach { (_, bid) ->
        result += bid * i++
      }
    }
    println(result)
  }

  override fun part2(input: String) {
  }

  private fun parseInput(input: String) =
    input.lines().map {
      val (hand, bid) = it.split(" ")
      hand to bid.toInt()
    }

  private fun handOrder(hand: String) =
    hand.toCharArray().let { handArr ->
      if (fiveOfaKind(handArr)) 7
      else if (fourOfaKind(handArr)) 6
      else if (fullHouse(handArr)) 5
      else if (threeOfAKind(handArr)) 4
      else if (twoPair(handArr)) 3
      else if (onePair(handArr)) 2
      else 1
    }

  private fun onePair(hand: CharArray) =
    hand.count { it == hand[0] } == 2 ||
    hand.count { it == hand[1] } == 2 ||
    hand.count { it == hand[2] } == 2 ||
    hand.count { it == hand[3] } == 2

  private fun twoPair(hand: CharArray) =
    hand.toSet().size == 3 // ???

  private fun threeOfAKind(hand: CharArray) =
    hand.count { it == hand[0] } == 3 ||
    hand.count { it == hand[1] } == 3 ||
    hand.count { it == hand[2] } == 3

  private fun fullHouse(hand: CharArray) =
    onePair(hand) && threeOfAKind(hand)

  private fun fourOfaKind(hand: CharArray) =
    hand.count { it == hand[0] } == 4 ||
    hand.count { it == hand[1] } == 4

  private fun fiveOfaKind(hand: CharArray) =
    hand.all { it == hand[0] }

  private fun charOrder(char: Char) =
    "AKQJT98765432".indexOf(char)
}