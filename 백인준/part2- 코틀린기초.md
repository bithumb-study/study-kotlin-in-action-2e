# 📚 2장 코틀린 기초

___

## 📖 2.1 기본 요소: 함수와 변수

___

### 🔖 2.1.1 첫 번째 코른린 프로그램 작성: Hello, World!

```kotlin
fun main() {
    println("Hello, world!")
}
```

- 함수를 선언할 때 fun 키워드 사용
- 함수를 모든 코틀린 파일의 최상위 수준에 정의할 수 있으므로 클래스안 안에 함수를 넣을 필요없음
- 최상위에 있는 main 함수를 애플리케이션의 진입점으로 지정할수 있고 main 함수에 인자가 필요없다.
- 코틀린은 간결성을 강조
- 세미콜론 필요없음 (붙이지 않는것을 권장)

### 🔖 2.1.2 파라미터와 반환값이 있는 함수 선언

```kotlin
fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
}

fun main() {
    println(max(1, 2))
}
```

- 코틀린에서는 파라미터 이름이 먼저오고 그뒤에 타입을 지정 , 타입과 이름을 콜른(:) 으로 구분한다.
- 함수의 반환 타입은 파라미터의 목록을 닫는 괄호 다음에 오고 닫는 괄호와 반환 타입 사이를 콜론(:) 으로 구분한다.

#### main 함수는 진입점이고 어떤 경우는 아무 값도 반환하지 않는다.

### 🔖 2.1.3 식 본문을 사용해 함수를 더 간결하게 정의

```kotlin
fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
} // 블록 본문 함수

fun max(a: Int, b: Int): Int = if (a > b) a else b
// 본문 함수

fun max(a: Int, b: Int) = if (a > b) a else b
// 본문 함수 (반환 타입 선언 X)
```

- 코틀린에서는 식 본문 함수가 자주 쓰임
- 식 본문 함수에서는 굳이 사용자가 반환타입을 작성하지 않아도 컴파일러가 본문 식을 분석해 식의 결과 타입을 함수 반환타입으로 정해준다.
- 이것을 타입추론이라 부른다.
- 식 본문 함수만 반환타입 생략 가능

### 🔖 2.1.4 데이터를 저장하기 위해 변수 선언

```kotlin
val question: String = "테스트"
val answer: Int = 42

val question = "테스트"
val answer = 42

val yearsToCompute = 7.5e6

fun main() {
    val answer: Int
    answer = 42
}
```

- 코틀린의 변수 선언은 키워드로 시작하고 그뒤에 변수이름이 온다.
- 타입선언을 생략 가능
- 변수 선언시 즉시 초기화 하지 않고 나중에 값을 대입 하고 싶을때는 컴파일러가 변수타입을 추론할수 없어서 명시적으로
  변수 타입을 선언해야 한다.

### 🔖 2.1.5 변수를 읽기 전용 변수나 재대입 가능 변수로 표시

- val
- 읽기 전용 참조
- 단 한번만 대입 가능 초기화 하면 다른 값을 대입 할수 없다.
- java 의 final 과 같다.
- var
- 재대입 가능 참조
- 다른 값 대입 가능

기본적으로 코틀린에서 변든 변수를 val 키워드를 사용해 선언하는 방식을 지켜야 한다.

불변 변수는 함수형과 만나면 사이드 이펙트 없이 함수형 프로그램을 사용하는 이점을 지킬수 있음

```kotlin
fun canPerformOpertaion(): Boolean {
    return true
}

fun main() {
    val result: String
    if (canPerformOpertaion()) {
        result = "Success"
    } else {
        result = "can't perform opertaion"
    }
}
```

- val 참조 자체가 읽기 전용이서 한번 대입된 값을 바꿀수 없더라도 참조가 가리키는 내부 객체값은 변경되수 있음

```kotlin
fun main() {
    val languages = mutableListOf("java")
    languages.add("Kotlin") // 참조가 가리키는 객체에 원소를 하나 추가하는 변경을 수행
}
```

- var 키워드는 변수의 값을 변경할수 있지만 변수의 타입은 고정된다.

```kotlin
fun main() {
    val answer = 42
    answer = "no answer" // type missmatch 오류 발생
}
```

### 🔖 2.1.6 더 쉽게 문자열 형식 지정 : 문자열 템플릿

```kotlin
fun main() {
    val input = readln()
    val name = if (input.isNotBlank()) input else "kotlin"
    println("hello , $name!")
}
```

- 변수 이름앞에 $를 붙이면 변수를 문자열안에 참조할수 있다.

```kotlin
fun main() {
    val name = readln()
    if (name.isNotBlank()) {
        println("hello, ${name.length} - letter person")
    }
    println("Hello ${if (name.isBlank()) "someon else name"}!")
}
```

- 중괄호에 식을 넣으면 복잡한 식 사용가능

---

## 📖 2.2 행동과 데이터 캡슐화 : 클래스와 프로퍼티

```java
public class Person {
    private final String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

```kotlin
class Person(val name: String)
```

- JAVA 의 record 와 비슷하다.

---

### 🔖 2.2.1 클래스와 데이터를 연관시키고, 접근 가능하게 만들기 : 프로퍼티

- 프로퍼티 (java)
- java 에서는 멤버필드가 보통 private 이다.
- getter 와 setter 를 사용해 접근 (접근자 메서드)
- 코틀린은 프로퍼티를 언어 기본 기능으로 제공하며 자바의 필드와 접근자 메서드를 완전 대신한다.
- val 로 선언한 프로퍼티는 읽기전용 var 로 선언한 필드는 변경 가능 하다.

```kotlin
class Person(
    val name: String, // 읽기만 가능
    var isStudent: Boolean // 읽기 , 변경 가능
)
```

- 프로퍼티를 선언하는 방식은 프로퍼티와 관련 있는 접근자를 선언하는 것이다.

```kotlin
class Person(
    val name: String, // 읽기만 가능
    var isStudent: Boolean // 읽기 , 변경 가능
)

fun main() {
    val person = Person("Bob", true)
    person.isStudent = false // 프로퍼티 이름을 직접 호출해도 자동으로 setter 호출
}
```

### 🔖 2.2.2 프로퍼티 값을 저장하지 않고 계산: 커스텀 접근자

- 프로퍼티에 접근할 때 계산을 할 수 있는 온더고 프로 퍼티 이므로 정보를 별도의 필드에 저장할 필요 없음

```kotlin
class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
        get() {
            return height == width
        }
}

fun main() {
    val rectangle = Rectangle(1, 3)
    println(rectangle.isSquare)
}
```

### 🔖 2.2.3 코틀린 소스코드 구조 : 디렉터리 패키지

- 자바와 같은 방식으로 패키지를 구성하는 것이 좋음

---
## 📖 2.3 선택 표현과 처리 : enum 과 when
---

### 🔖 2.3.1 enum 클래스와 이넘 상수 정의

```kotlin
enum class Color(
    val r: Int,
    val g: Int,
    val b: Int,
) {
    RED(1, 2, 3),
    GREEN(2, 3, 4); // 코틀린에서 유일하게 세미콜론이 필수인 부분

    fun rgb() = (r * 256 + g) + 232 + r
    fun printColor() = println("$this is ${rgb()}")
}

fun main() {
    println(Color.GREEN.rgb())
    Color.RED.printColor()
}
```

### 🔖 2.3.2 when 으로 이넘클래스 다루기

```kotlin
enum class Color(
    val r: Int,
    val g: Int,
    val b: Int,
) {
    RED(1, 2, 3),
    GREEN(2, 3, 4); // 코틀린에서 유일하게 세미콜론이 필수인 부분

    fun rgb() = (r * 256 + g) + 232 + r
    fun printColor() = println("$this is ${rgb()}")
}

fun getMnemonic(color: Color) =
    when (color) {
        Color.RED -> "Richard"
        Color.GREEN -> "Gave"
    }

fun getWarmthFromSensor(): String {
    fun measureColor() = Color.RED

    return when (color) {
        Color.RED, Color.GREEN -> "warm (red = ${color.r})"
    }
}
fun main() {
    println(getMnemonic(Color.GREEN))
}
```

-- 자바 와 달리 break 문을 넣지 않아도 됨

### 🔖 2.3.3 when식의 대상을 변수에 캡쳐

대상 식을 바로 대입하여 사용

```kotlin
enum class Color(
    val r: Int,
    val g: Int,
    val b: Int,
) {
    RED(1, 2, 3),
    GREEN(2, 3, 4); // 코틀린에서 유일하게 세미콜론이 필수인 부분

    fun rgb() = (r * 256 + g) + 232 + r
    fun printColor() = println("$this is ${rgb()}")
}

fun measureColor() = Color.RED
fun getWarmthFromSensor() =
    when (val color = measureColor()) {
        Color.RED, Color.GREEN -> "warm (red = ${color.r})"
    }
```

### 🔖 2.3.4 when의 분기 조건에 임의의 객체 사용

```kotlin
enum class Color(
    val r: Int,
    val g: Int,
    val b: Int,
) {
    RED(1, 2, 3),
    ORANGE(1, 2, 3),
    GREEN(2, 3, 4); // 코틀린에서 유일하게 세미콜론이 필수인 부분

    fun rgb() = (r * 256 + g) + 232 + r
    fun printColor() = println("$this is ${rgb()}")
}

fun mix(c1: Color, c2: Color) =
    when (setOf(c1, c2)) {
        setOf(Color.RED, Color.GREEN) -> Color.ORANGE
        else -> throw Exception("Dirty Color");
    }
```

- setOf 는 원소의 순서는 중요하지 않다.
- 모든 분기식에서 만족하는 조건을 못찾는 경우 else 사용

### 🔖 2.3.5 인자 없는 when 사용

```kotlin
enum class Color(
    val r: Int,
    val g: Int,
    val b: Int,
) {
    RED(1, 2, 3),
    ORANGE(1, 2, 3),
    GREEN(2, 3, 4); // 코틀린에서 유일하게 세미콜론이 필수인 부분

    fun rgb() = (r * 256 + g) + 232 + r
    fun printColor() = println("$this is ${rgb()}")
}

fun mixOptimized(c1: Color, c2: Color) =
    when {
        (c1 == Color.GREEN || c2 == Color.RED) ||
                (c1 == Color.RED || c2 == Color.GREEN) -> Color.GREEN
        else -> throw Exception("Dirty Color");
    }
```

### 🔖 2.3.6 스마트 캐스트: 타입 검사와 타입 캐스트 조합

- 여러 타입의 식 객체를 아우르는 공통 타입 역할만 수행하는 인터페이스를 마커 인터페이스 라고 한다.

```kotlin
interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

fun eval(e: Expr): Int {
    if (e is Num) {
        val n = e as Num // e 를 num 으로 변환 불필요한 중복
        return n.value
    }
    if (e is Sum) {
        return eval(e.right) + eval(e.left) // e 를 스마트 캐스트
    }
    throw IllegalArgumentException("Unknown")
}

fun main() {
    println(eval(Sum(Sum(Num(1), Num(2)), Num(4))))
}
```

- 타입을 검사한 변수를 마치 그 타입의 변수인것처럼 사용가능
- 컴파일러가 타입을 대신변환 -> 스마트 캐스트
- 원하는 타입을 명시적으로 변환하기 위해서는 as 를 사용

### 🔖 2.3.7 리팩터링: if를 when 으로 변경

```kotlin
interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.right) + eval(e.left)
        else -> throw IllegalArgumentException("Unknown")
    }
```

- when 이 받은 대상의 타입을 검사
- 변수를 강제로 캐스팅할 필요가 없다.

### 🔖 2.3.8 if 와 when의 분기에서 블록 사용

```kotlin
interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

fun evalWithLogging(e: Expr): Int =
    when (e) {
        is Num -> {
            println("num : ${e.value}")
            e.value
        }
        is Sum -> {
            val right = evalWithLogging(e.right)
            val left = evalWithLogging(e.left)
            println("sum: $left + $ right")
            left + right
        }
        else -> throw IllegalArgumentException("Unknown")
    }
```

- 블록의 마지막 식이 블록의 결과
- 식의 본문을 하는 함수는 return 문이 필요하다.

---

## 📖 2.4 대상 이터레이션 : while과 for 루프

___

### 🔖 2.4.1 조건이 참인 동안 코드 반복: while 루프

```kotlin
while (true) {
    if (true) break
}

outer@ while (outerCondition) {
    while (innerCondition) {
        if (shouldExitInner) break // 레이블을 지정하지 않으면 안쪽루프에서 동작이 이뤄짐
        if (shouldSkipInner) break
        if (shouldExit) break@outer // 레이블을 지정하면 지정한 루프를 빠져나갈수있다.
        if (shouldSkip) break@outer
    }
}
```

- 내포된 루프의 경우 코틀린에서는 레이블을 지정할수 있다.
- 레이블은 @기호 다음에 식별자를 붙인다.

### 🔖 2.4.2 수에 대해 이터레이션 : 범위와 수열

```kotlin
fun fizzBuzz(i: Int) = when {
    i % 15 == 0 -> "fizzBuzz"
    i % 3 == 0 -> "fizz"
    1 % 5 == 0 -> "buzz"
}
fun main() {
    for (i in 1..100) {
        println(fizzBuzz(i))
    }
    for (i in 100 downTo 1 step 2) {
        println(fizzBuzz(i))
    } // 증가값으로 가능
}
```

### 🔖 2.4.3 맵에 대해 이터레이션

```kotlin
fun main() {
    val collection = listOf("red", "green", "blue")
    for (color in collection) {
        println("$color")
    }
}
```

### 🔖 2.4.4 in으로 컬렉션이나 범위의 원소 검사

- in 연산자를 사용해 어떤 값이 범위에 속하는지 검사 가능
- !in은 반대 이다.

```kotlin
fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'

fun isNotDigit(c: Char) = c !in '0'..'9'
fun main() {
    println(isLetter('z'))
    println(isNotDigit('a'))
}
fun recognize(c: Char) = when (c) {
    in '0'..'9' -> "it is a digit"
    in 'a'..'z', in 'A'..'Z' -> "it is a letter"
    else -> "asd"
}
```
- in 절은 문자에만 국한되지 않고 비교가 가능한 클래스라면 그 클래스의 인스턴스 객체를 사용해 범위를 만들수 있다.

---
## 📖 2.5 코틀린에서 예외 던지고 잡아내기
___
- 자바나 다른 예외 처리와 비슷하다.
```kotlin
if(percentage !in 0..100){
    throw IllegalArgumentException("asd") // new 가 없음
}
```
### 🔖 2.5.1 try, catch, finally 를 사용한 예외 처리와 오류 복구
```kotlin
import java.io.BufferedReader

fun readNumber(reader: BufferedReader): Int? { // throws 가 없음
    try {
        //...
    } catch (e: NumberFormatException) {
        return null
    } finally {
        return null
    }
}
```
- 코틀린은 checked exception 과 unchecked exception 을 구별하지 않는다.
### 🔖 2.5.2 try를 식으로 사용
```kotlin
import java.io.BufferedReader

fun readNumber(reader: BufferedReader) {
    val number = try {
        Integer.parseInt(reader.readLine())
    } catch (e: NumberFormatException) {
        return
    }
}
```
- try 코드 블록의 실행이 정상적으로 끝나면 그 브록의 마지막 값이 결과값