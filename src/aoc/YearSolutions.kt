package aoc

abstract class YearSolutions {
  protected abstract val solutions: Map<Int, Day>
  fun getSolution(day: Int): Day? {
    return solutions[day]
  }
}