package aoc.year2023

import aoc.Day
import kotlin.math.*

/**
 * Definitely not the most efficient solution, just using an imperative brute force approach, but it's quick enough that
 * I don't really care. Maybe I should investigate a better solution I bet there's an O(1) math way of doing it but waiting
 * like half a second isn't that big of a deal for an advent of code problem.
 *
 * EDIT: I did fix it, you can look back on my pushes to see the imperative solution. It was basically just add one to
 * hold time until I found all the different ways to both win and beat the record.
 */
class Day6 : Day {
  override fun part1(input: String) {
    val (times, distances) = parseInput(input)
    val result = times.indices.fold(1L) { acc, i ->
      val time = times[i]
      val distance = distances[i]
      // for each time and record distance, calculate the total amount of different ways to beat the record
      acc * calculateAmtOfDifferentWins(time, distance)
    }
    println("Total amount of ways to beat races are: $result")
  }

  override fun part2(input: String) {
    // combine times and distances to get some fat numbers first then calculate just that one set of time/distance record
    val (time, distance) = parseInput2(input)
    val result = calculateAmtOfDifferentWins(time, distance)
    println("Total amount of ways to beat races are: $result")
  }

  /**
   * Note: I changed my mind and made this faster, I ended up looking online and saw a great breakdown of the algebra
   * needed to reduce the amount of wins to a formula. I'll try to explain it here if anyone ever happens to look at this!
   *
   * distance = travel distance
   * buttonHoldTime = length of time holding the button
   * time = total race time
   *
   * So we can calculate how far a boat will travel with just buttonHoldTime & time:
   * distance = buttonHoldTime * (time - buttonHoldTime)
   * SO, we can isolate buttonHoldTime to find max and min values
   * distance = buttonHoldTime * (time - buttonHoldTime) = -buttonHoldTime^2 + (time * buttonHoldTime)
   * therefore, buttonHoldTime = -time (+ or -) sqrt(time^2 - (4 * distance))
   * Great! that means we can get two values for buttonHoldTime
   * buttonHoldTime = { minButtonHoldTime, maxButtonHoldTime }
   * Now we can get the total amount of different ways to win by finding the difference!
   * Note: The min and max are probably going to have decimals which don't make sense so we'll need to floor/ceil the values
   * I believe the + 1 is due to the ceil / floor operations, but I couldn't find a perfect run down on the logic of
   * the math. There's no way I would think like this in a real world problem, but it's good to see pretty math solutions!
   * amtWinning = abs(ceil(minButtonHoldTime) - floor(maxButtonHoldTime)) + 1
   * I guess had a intuition that there was nice math way of doing this, they probably should have made this problem's
   * input be way larger to force you to not use an imperative solution.
   *
   * Still neat and makes the calculation O(1) complexity!
   */
  private fun calculateAmtOfDifferentWins(time: Long, distance: Long): Long {
    val buttonHoldTime1 = (-time) + sqrt(((time * time) - 4L * distance).toDouble()) / -2
    val buttonHoldTime2 = (-time) - sqrt(((time * time) - 4L * distance).toDouble()) / -2
    val amtWinning = abs(ceil(buttonHoldTime1) - floor(buttonHoldTime2)) + 1
    return amtWinning.toLong()
  }

  private fun parseInput(input: String) =
    input
      .split("Time:", "Distance:")
      .drop(1)
      .map {
        it.trim()
          .split(" ")
          .filter(String::isNotEmpty)
          .map(String::toLong)
      }

  private fun parseInput2(input: String) =
    parseInput(input).map {
      it.joinToString("").toLong()
    }
}