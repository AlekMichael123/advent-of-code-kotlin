package aoc.year2023

import aoc.Day

class Day7 : Day {
  enum class HandRanks(val value: Int) {
    FIVE_OF_A_KIND(7),
    FOUR_OF_A_KIND(6),
    FULL_HOUSE(5),
    THREE_OF_A_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1),
    BUST(0);
  }

  override fun part1(input: String) {
    val result = calculateTotalBidsViaRank(input, jokers = false)
    println(result)
  }

  override fun part2(input: String) {
    val result = calculateTotalBidsViaRank(input, jokers = true)
    println(result)
  }

  private fun calculateTotalBidsViaRank(input: String, jokers: Boolean): Int {
    val data = parseInput(input)
    // store each hand/bid with the hand's respective rank
    val order = data.map { pair ->
      handRank(pair.first, jokers) to pair
    }.fold(mutableMapOf<Int, MutableList<Pair<String, Int>>>()) { map, pair ->
      map.putIfAbsent(pair.first, mutableListOf())
      map[pair.first]!!.add(pair.second)
      map
    }

    // For each hand rank, sort the hands/bids by the card's rank
    val keys = order.keys
    keys.forEach { key ->
      order[key]!!.sortWith { (a, _), (b, _) ->
        var cmp = 0
        for (i in a.indices) {
          val aCharOrder = cardRank(a[i], jokers)
          val bCharOrder = cardRank(b[i], jokers)
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

    // I'm really too lazy to just make a list of 1 to 7 but whatever
    val sorted = keys.sorted()
    var i = 1
    var result = 0

    // add each bid in order multiplied by its position
    sorted.forEach { rank ->
      val values = order[rank]!!
      values.forEach { (_, bid) ->
        result += bid * i++
      }
    }

    return result
  }

  private fun parseInput(input: String) =
    input.lines().map {
      val (hand, bid) = it.split(" ")
      hand to bid.toInt()
    }

  private fun handRank(hand: String, jokers: Boolean) =
    hand.toCharArray().let { handArr ->
      val fiveOfaKindCheck = fiveOfaKind(handArr)
      val fourOfaKindCheck = fourOfaKind(handArr, jokers)
      val fullHouseCheck = fullHouse(handArr, jokers)
      val threeOfaKindCheck = threeOfaKind(handArr, jokers)
      val twoPairCheck = twoPair(handArr, jokers)
      val onePairCheck = onePair(handArr, jokers)
      val highCardCheck = highCard(handArr, jokers)

      if (pass(fiveOfaKindCheck)) fiveOfaKindCheck
      else if (pass(fourOfaKindCheck)) fourOfaKindCheck
      else if (pass(fullHouseCheck)) fullHouseCheck
      else if (pass(threeOfaKindCheck)) threeOfaKindCheck
      else if (pass(twoPairCheck)) twoPairCheck
      else if (pass(onePairCheck)) onePairCheck
      else highCardCheck
    }.value

  private fun highCard(hand: CharArray, jokers: Boolean) =
    if (jokers && hand.contains('J')) HandRanks.ONE_PAIR
    else HandRanks.HIGH_CARD

  private fun onePair(hand: CharArray, jokers: Boolean = false) =
    if (
      hand.count { it == hand[0] } == 2 ||
      hand.count { it == hand[1] } == 2 ||
      hand.count { it == hand[2] } == 2 ||
      hand.count { it == hand[3] } == 2
    )
      if (jokers && (hand.count { it == 'J' } == 2 || hand.count { it == 'J' } == 1)) HandRanks.THREE_OF_A_KIND
      else HandRanks.ONE_PAIR
    else HandRanks.BUST

  private fun twoPair(hand: CharArray, jokers: Boolean) =
    if (hand.toSet().size == 3)
      if (jokers && hand.count { it == 'J' } == 2) HandRanks.FOUR_OF_A_KIND
      else if (jokers && hand.count { it == 'J' } == 1) HandRanks.FULL_HOUSE
      else HandRanks.TWO_PAIR
    else HandRanks.BUST

  private fun threeOfaKind(hand: CharArray, jokers: Boolean = false) =
    if (
      hand.count { it == hand[0] } == 3 ||
      hand.count { it == hand[1] } == 3 ||
      hand.count { it == hand[2] } == 3
    )
      if (jokers && (hand.count { it == 'J' } == 3 || hand.count { it == 'J' } == 1)) HandRanks.FOUR_OF_A_KIND
      else HandRanks.THREE_OF_A_KIND
    else HandRanks.BUST

  private fun fullHouse(hand: CharArray, jokers: Boolean) =
    if (pass(onePair(hand)) && pass(threeOfaKind(hand)))
      if (jokers && (hand.count { it == 'J' } == 2 || hand.count { it == 'J' } == 3)) HandRanks.FIVE_OF_A_KIND
      else HandRanks.FULL_HOUSE
    else HandRanks.BUST

  private fun fourOfaKind(hand: CharArray, jokers: Boolean) =
    if (
      hand.count { it == hand[0] } == 4 ||
      hand.count { it == hand[1] } == 4
    )
      if (jokers && (hand.count { it == 'J' } == 1 || hand.count { it == 'J' } == 4)) HandRanks.FIVE_OF_A_KIND
      else HandRanks.FOUR_OF_A_KIND
    else HandRanks.BUST

  // this does not rely on jokers existing
  private fun fiveOfaKind(hand: CharArray) =
    if (hand.all { it == hand[0] }) HandRanks.FIVE_OF_A_KIND
    else HandRanks.BUST

  private fun pass(result: HandRanks) = result != HandRanks.BUST

  private fun cardRank(char: Char, jokers: Boolean = false) =
    if (!jokers)
      "AKQJT98765432".indexOf(char)
    else
      "AKQT98765432J".indexOf(char)
}