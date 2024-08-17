package aoc.year2023

import aoc.Day

class Day5 : Day {
  private operator fun <T> List<T>.component6(): T = get(5)
  private operator fun <T> List<T>.component7(): T = get(6)
  private operator fun <T> List<T>.component8(): T = get(7)

  /**
   * INPUT RANT: WTF decided to have the ranges start with destination then source???
   * it should be source destination but WHATEVER
   */

  override fun part1(input: String) {
    println("Part 1")
    val (
      initialSeeds,
      seedToSoil,
      soilToFertilizer,
      fertilizerToWater,
      waterToLight,
      lightToTemperature,
      temperatureToHumidity,
      humidityToLocation,
    ) = parseInput(input)

    val seedToSoilRanges = rangesStringToRanges(seedToSoil)
    val soilToFertilizerRanges = rangesStringToRanges(soilToFertilizer)
    val fertilizerToWaterRanges = rangesStringToRanges(fertilizerToWater)
    val waterToLightRanges = rangesStringToRanges(waterToLight)
    val lightToTemperatureRanges = rangesStringToRanges(lightToTemperature)
    val temperatureToHumidityRanges = rangesStringToRanges(temperatureToHumidity)
    val humidityToLocationRanges = rangesStringToRanges(humidityToLocation)

    val seedToSoilMap = createMapBasedOffRanges(initialSeeds[0], seedToSoilRanges)
    val soilToFertilizerMap = createMapBasedOffRanges(flattenMap(seedToSoilMap), soilToFertilizerRanges)
    val fertilizerToWaterMap = createMapBasedOffRanges(flattenMap(soilToFertilizerMap), fertilizerToWaterRanges)
    val waterToLightMap = createMapBasedOffRanges(flattenMap(fertilizerToWaterMap), waterToLightRanges)
    val lightToTemperatureMap = createMapBasedOffRanges(flattenMap(waterToLightMap), lightToTemperatureRanges)
    val temperatureToHumidityMap = createMapBasedOffRanges(flattenMap(lightToTemperatureMap), temperatureToHumidityRanges)
    val humidityToLocationMap = createMapBasedOffRanges(flattenMap(temperatureToHumidityMap), humidityToLocationRanges)

    val closestUsableLocation = flattenMap(humidityToLocationMap).minOrNull()

    println("The closest usable location is $closestUsableLocation")
  }

  override fun part2(input: String) {
    println("Part 2")
    val (
      initialSeeds,
      seedToSoil,
      soilToFertilizer,
      fertilizerToWater,
      waterToLight,
      lightToTemperature,
      temperatureToHumidity,
      humidityToLocation,
    ) = parseInput(input)

    val initialSeed = seedRangesStringToRanges(initialSeeds[0])
    val seedToSoilRanges = rangesStringToRanges(seedToSoil)
    val soilToFertilizerRanges = rangesStringToRanges(soilToFertilizer)
    val fertilizerToWaterRanges = rangesStringToRanges(fertilizerToWater)
    val waterToLightRanges = rangesStringToRanges(waterToLight)
    val lightToTemperatureRanges = rangesStringToRanges(lightToTemperature)
    val temperatureToHumidityRanges = rangesStringToRanges(temperatureToHumidity)
    val humidityToLocationRanges = rangesStringToRanges(humidityToLocation)

    val seedToSoilRangesMap = createMapBasedOffRangesFromRanges(initialSeed, seedToSoilRanges)
    val soilToFertilizerRangesMap = createMapBasedOffRangesFromRanges(flattenRangesMap(seedToSoilRangesMap), soilToFertilizerRanges)
    val fertilizerToWaterRangesMap = createMapBasedOffRangesFromRanges(flattenRangesMap(soilToFertilizerRangesMap), fertilizerToWaterRanges)
    val waterToLightRangesMap = createMapBasedOffRangesFromRanges(flattenRangesMap(fertilizerToWaterRangesMap), waterToLightRanges)
    val lightToTemperatureRangesMap = createMapBasedOffRangesFromRanges(flattenRangesMap(waterToLightRangesMap), lightToTemperatureRanges)
    val temperatureToHumidityRangesMap = createMapBasedOffRangesFromRanges(flattenRangesMap(lightToTemperatureRangesMap), temperatureToHumidityRanges)
    val humidityToLocationRangesMap = createMapBasedOffRangesFromRanges(flattenRangesMap(temperatureToHumidityRangesMap), humidityToLocationRanges)

    val closestAvailableIsland = flattenRangesMap(humidityToLocationRangesMap).filter { it.first != 0L }.minByOrNull { it.first }?.first
    println("Closest available location is: " + (closestAvailableIsland ?: "None Available"))
  }

  private fun createMapBasedOffRangesFromRanges(initialRanges:  List<Pair<Long, Long>>, ranges: List<Pair<Pair<Long, Long>, Pair<Long, Long>>>) =
    initialRanges.associateWith { initialRange ->
      val list = mutableListOf<Pair<Long, Long>>()
      ranges.forEach { (destinationRange, sourceRange) ->
        if (
          !(initialRange.first <= sourceRange.second && sourceRange.first <= initialRange.second)
        ) {
          return@forEach
        }

        /**
         * AN: WHY DOES PART 1 CONSIDER UNMAPPED VALUES BUT NOT PART 2??? I MUST BE STUPID BUT I THINK THIS PROBLEM
         * WAS VERY POORLY WORDED!!!! Wasted two days on this because I was considering cases such as:
         * [1,2,3]       -- init
         *       [4,5,6] -- source
         * [1,2,3]       -- mapped values
         * but NOPE we just don't want these values at all. WTF?
         */
        val overlapFirst = sourceRange.first.coerceAtLeast(initialRange.first)
        val overlapLast = sourceRange.second.coerceAtMost(initialRange.second)
        if (overlapFirst < overlapLast) {
          val offset = destinationRange.first - sourceRange.first
          list.add(overlapFirst+offset to overlapLast+offset)
        }


      }
      // add unmapped values from initialRange
      if (list.isEmpty()) {
        list.add(initialRange)
      }
      list
    }

  private fun createMapBasedOffRanges(initialValues: List<Long>, ranges: List<Pair<Pair<Long, Long>, Pair<Long, Long>>>) =
    initialValues.associateWith { value ->
      val list =  mutableListOf<Long>()
      ranges.forEach { (destinationRange, sourceRange) ->
        if (value in (sourceRange.first .. sourceRange.second)) {
          // LOL first I was using indexOf which was slow, but then I wised up
          val index = value - sourceRange.first
          list.add(destinationRange.first + index)
        }
      }

      if (list.isEmpty()) {
        list.add(value) // if not mapped, map with same value
      }
      list
    }

  private fun seedRangesStringToRanges(seedRanges: List<Long>) =
    seedRanges
      .mapIndexed { i, seedInitial ->
        if (i % 2 != 0) null
        else seedInitial to seedInitial + seedRanges[i + 1]
      }
      .filterNotNull()

  private fun rangesStringToRanges(rangesString: List<List<Long>>) =
    rangesString.map { (aStart, bStart, rangeLength) ->
      (aStart to aStart + rangeLength - 1) to (bStart to bStart + rangeLength - 1)
    }

  private fun parseInput(input: String) =
    input
      .split(
        "seeds:",
        "seed-to-soil map:",
        "soil-to-fertilizer map:",
        "fertilizer-to-water map:",
        "water-to-light map:",
        "light-to-temperature map:",
        "temperature-to-humidity map:",
        "humidity-to-location map:",
      )
      .slice(1..8)
      .map(String::trim)
      .map { line -> line.split("\n").map { it.split(" ").map(String::toLong) } }

  private fun flattenMap(map: Map<Long, MutableList<Long>>) =
    map.values.toList().flatten()

  private fun flattenRangesMap(map:  Map<Pair<Long, Long>, MutableList<Pair<Long, Long>>>) =
    map.values.toList().flatten()
}