import aoc.getYearSolution

fun main(args: Array<String>) {
  val (year, day) = args
  val yearSolutions = getYearSolution(year.toInt())
  val solution = yearSolutions?.getSolution(day.toInt())
  solution?.let {
    it.part1("")
    it.part2("")
  }
}