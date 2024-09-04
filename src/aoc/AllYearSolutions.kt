package aoc

import aoc.year2023.*

private val yearSolutions =
  mapOf(
    2023 to Year2023Solutions(),
  )

fun getYearSolution(year: Int): YearSolutions? {
  return yearSolutions[year]
}
