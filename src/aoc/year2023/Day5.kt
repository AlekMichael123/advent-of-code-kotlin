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

    val closestUsableLocation = humidityToLocationMap.values.toList().flatten().minOrNull()

    println("The closest usable location is $closestUsableLocation")
  }

  override fun part2(input: String) {
    println("Part 2")
  }

  private fun createMapBasedOffRanges(initialValues: List<Long>, ranges: List<Pair<LongRange, LongRange>>) =
    initialValues.fold(mutableMapOf<Long, MutableList<Long>>()) { map, value ->
      map[value] = mutableListOf()

      ranges.forEach { (destinationRange, sourceRange) ->
        if (value in sourceRange) {
          // LOL first I was using indexOf which was slow, but then I wised up
          val index = value - sourceRange.first
          map[value]?.add(destinationRange.first + index)
        }
      }

      if (map[value]?.isEmpty() == true) {
        map[value]?.add(value) // if not mapped, map with same value
      }

      map
    }

  private fun rangesStringToRanges(rangesString: List<List<Long>>) =
    rangesString.map { (aStart, bStart, rangeLength) ->
      (aStart until aStart + rangeLength) to (bStart until bStart + rangeLength)
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

  private fun flattenMap(map: MutableMap<Long, MutableList<Long>>) =
    map.values.toList().flatten()
}