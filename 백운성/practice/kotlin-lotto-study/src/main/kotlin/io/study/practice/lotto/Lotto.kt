package io.study.practice.lotto

const val GAME_PRICE: Long = 1_000
const val NUMBER_COUNT = 6

fun main() {

    val availableNumbers = mutableListOf<Int>().apply {
        for (n in 1..45) this.add(n)
    }

//    LottoMission01(availableNumbers).process()
    LottoMission02(availableNumbers).process()
}

fun generateRandomNumbers(availableNumbers: MutableList<Int>, count: Int): List<Int> {
    return availableNumbers.shuffled()
        .slice(1..count)
}