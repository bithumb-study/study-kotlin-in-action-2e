import java.math.RoundingMode

fun main() {

    // 구입 금액 입력 및 로또 구매
    println("구입금액을 입력해 주세요.")
    val purchaseAmount = readln().toInt()
    val buyCount = purchaseAmount / LOTTO_PRICE

    // 수동 구매
    val boughtLottos: MutableList<List<Int>> = mutableListOf()
    for (i in 1 .. buyCount) {
        val lottos = readLottoAnswer("구매할 로또 번호를 입력해 주세요. - 현재 ${i}번째, 총 ${buyCount}개 구매 가능")
        boughtLottos.add(lottos)
    }

    println("${buyCount}개를 구매했습니다.")
    println()

    // 당첨 번호 입력
    val answer = readLottoAnswer("지난 주 당첨 번호를 입력해 주세요.")

    // 보너스 번호 입력
    val bonus = readBonusNumber(answer)
    // 보너스 당첨 카운트
    var bonusCount: Int = 0

    // 당첨 통계 계산
    val resultMap: MutableMap<Int, Int> = mutableMapOf() // <맞춘 숫자 수, 그게 몇개인지>
    boughtLottos.forEach { subList ->
        val matchedCount = subList.count { item -> item in answer }
        // 5개 당첨 + 보너스 당첨이면 따로 계산
        if (matchedCount == 5 && subList.contains(bonus)) {
            bonusCount++
        } else {
            resultMap.put(matchedCount, resultMap.getOrDefault(matchedCount, 0) + 1) // count 수 추가
        }
    }

    // 수익률 계산
    val calculateMap: Map<Int, Int> = mapOf(
        3 to 5000,
        4 to 50000,
        5 to 1500000,
        6 to 2000000000
    )
    val bonusPrice: Int = 3000000
    val bonusCalculate: Double = bonusPrice.toDouble() * bonusCount.toDouble()

    // n개짜리를 몇 번 맞췄는지 꺼낸다
    val resultAmount: Int = resultMap.entries.sumOf {(correctCount, havingCount) ->
        val price = calculateMap[correctCount] ?: 0 // 가격 맵에서 얼마짜리인지 가져옴
        price * havingCount
    }
    val resultPriceRate = ((resultAmount.toDouble()+bonusCalculate) / purchaseAmount.toDouble()).toBigDecimal().setScale(2, RoundingMode.DOWN)

    // 당첨 통계 출력
    println("당첨 통계")
    println("---------")
    println("3개 일치 (${calculateMap[3]}원)- ${resultMap.getOrDefault(3, 0)}개")
    println("4개 일치 (${calculateMap[4]}원)- ${resultMap.getOrDefault(4, 0)}개")
    println("5개 일치 (${calculateMap[5]}원)- ${resultMap.getOrDefault(5, 0)}개")
    println("5개 일치, 보너스볼 일치 (${bonusPrice}원)- ${bonusCount}개")
    println("6개 일치 (${calculateMap[6]}원)- ${resultMap.getOrDefault(6, 0)}개")
    println("총 수익률은 ${resultPriceRate}입니다.")
}