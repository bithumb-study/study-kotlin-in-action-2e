const val LOTTO_COUNT: Int = 6
const val LOTTO_PRICE: Int = 1000

// 로또 번호 입력받는 함수
fun readLottoAnswer(str: String): List<Int> {

    while (true) {
        println(str)
        val answersString: String = readln()
        val answers: List<Int> = answersString
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() } // 숫자가 아닌 값 제거
            .filter { it in 1..45 } // 유효한 범위 내 필터링
            .distinct() // 중복 제거

        // 유효성 검증 통과에 실패하면 필터링되어 배열 길이가 6개가 아님
        if (answers.size != LOTTO_COUNT) {
            println("당첨 번호는 ${LOTTO_COUNT}개입니다. ${LOTTO_COUNT}개의 서로 다른 유효한 숫자를 다시 입력해 주세요.")
            continue
        }
        println()
        return answers.sorted() // 정렬된 결과 리턴
    }
}

// 보너스 번호 입력
fun readBonusNumber(list: List<Int>): Int {
    while(true) {
        println("보너스 볼을 입력해 주세요.")
        val input: Int = readln().trim().toIntOrNull() ?: 0
        if (!(input in 1 .. 45)) {
            println("1부터 45 사이의 유효한 숫자 형태의 값을 입력해 주세요.")
            continue
        }
        if (list.contains(input)) {
            println("당첨 번호와 중복된 숫자는 입력이 불가능합니다.")
            continue
        }
        return input
    }
}