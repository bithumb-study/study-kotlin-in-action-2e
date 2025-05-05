package io.study.practice.lotto

/**
 * Created by woonsungbaek on 2025. 5. 5..
 */
data class LottoGame(
    val price: Long,
    val numbers: List<Int>,
)

fun LottoGame.matchCount(winNumbers: List<Int>): Int {
    val union = winNumbers + this.numbers
    return union.groupBy { it }
        .filter { it.value.size > 1 }
        .flatMap { it.value }
        .distinct()
        .count()
}