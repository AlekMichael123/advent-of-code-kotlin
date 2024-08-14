package aoc

import aoc.year2023.Year2023Solutions

private val yearSolutions =
  mapOf<Int, YearSolutions>(
    2023 to Year2023Solutions(),
  )

fun getYearSolution(year: Int): YearSolutions? {
  return yearSolutions[year]
}
