import aoc.getYearSolution
import java.io.File

fun main(args: Array<String>) {
  val (year, day) = args
  val input = readFileDirectlyAsText("input.txt")
  val yearSolutions = getYearSolution(year.toInt())
  val solution = yearSolutions?.getSolution(day.toInt())
  solution?.let {
    println("Part 1")
    it.part1(input)
    println("Part 2")
    it.part2(input)
  }
}

fun readFileDirectlyAsText(fileName: String): String
    = File(fileName).readText(Charsets.UTF_8)