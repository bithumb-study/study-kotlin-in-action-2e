# 8주 9장 연산자 오버로딩과 다른 관례 : 2025-04-21 ~ 2025-04-27

---

## 연산자 오버로딩과 다른 관례

- 어떤 언어 기능과 미리 정해진 이름의 함수를 연결해주는 기법을 코틀린에서는 `관례`라 함
- 이런 관례를 채택한 이유는 기존 자바 클래스를 코틀린 언어에 적용하기 위함

### 오버로딩 가능한 산술 연산자

- plus, times, divide 등: 이항 산술 연산 오버로딩
- 특정 메소드를 정의 해 놓으면 +, -, * 등과 같은 기호로 메소드를 실행할 수 있음
- 항상 `operator` 키워드 붙여야 함

```kotlin
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y) // plus라는 이름의 연산자 함수를 정의
    }
}

fun main() {
    val p1 = Point(10, 20)
    val p2 = Point(30, 40)
    println(p1 + p2)
    // Point (x=40, y=60) +기호를 쓰면 plus 함수가 호출
}
```

#### 오버로딩 가능한 이항 산술 연산자

| 식     | 함수 이름 |
|-------|-------|
| a * b | times |
| a / b | div   |
| a % b | mod   |
| a + b | plus  |
| a - b | minus |

### 복합 대입 연산자 오버로딩 (operator overloading)

- 연산을 적용한 다음에 그 결과를 바로 대입
- 항상 `operator` 키워드 붙여야 함
- plusAssign, minusAssign, timesAssign 같은 특정 함수 이름에 `operator` 키워드를 붙여 사용
- 클래스 안에 만들어야 함

```kotlin
class Counter(var count: Int) {
    operator fun plusAssign(value: Int) {
        count += value
    }
}

fun main() {
    val counter = Counter(5)
    counter += 3   // 내부적으로 plusAssign(3) 호출
    println(counter.count)  // 8
}
```

| 복합대입연산자 | 함수 이름       |
|---------|-------------|
| +=      | plusAssign  |
| -=      | minusAssign |
| *=      | timesAssign | 
| /=      | divAssign   |
| %=      | modAssign   |

### 단항 연산자
- 위 내용과 사용법 및 기능은 동일함 

| 단항연산자    | 함수 이름      |
|----------|------------|
| +a       | unaryPlus  |
| -a       | unaryMinus |
| !a       | not        |
| ++a, a++ | inc        |
| --a, a-- | dec        |

## 비교 연산자를 오버로딩해서 객체들 사이의 관계를 쉽게 검사
### 동등성 연산자
- `==, !=` 비교할 때 쓰는 연산자
- `==`을 쓰면 자동으로 equals() 가 호출 됨
- `operator` 필요 없음
```kotlin
class Person(val name: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true  // 참조 비교
        if (other !is Person) return false
        return name == other.name
    }
}
```

### 순서 연산자
- `>, <, >=, <=` 비교할 때 쓰는 연산자
- compareTo() 라는 함수를 오버로딩해야 하며 `operator` 필요 함
```kotlin
class Score(val value: Int) {
    operator fun compareTo(other: Score): Int {
        return this.value - other.value
    }
}

fun main() {
    val s1 = Score(80)
    val s2 = Score(90)

    println(s1 < s2)  // true
    println(s1 > s2)  // false
}
```

## 컬렉션과 범위에 대해 쓸 수 있는 관례

### 인덱스로 원소 접근: get과 Set
- `list[i]` 처럼 인덱스로 접근하면 내부적으로 `get(index), set(index, value)` 호출됨

```kotlin
class MyList {
    private val data = mutableListOf(1, 2, 3)

    operator fun get(index: Int): Int {
        return data[index]
    }

    operator fun set(index: Int, value: Int) {
        data[index] = value
    }
}

fun main() {
    val list = MyList()
    println(list[0])   // 1
    list[0] = 10
    println(list[0])   // 10
}
```

### 어떤 객체가 컬렉션에 들어있는지 검사: in 관례
- `in`을 사용하면 내부적으로 `contains`가 호출됨

### 객체로부터 범위 만들기: rangeTo와 rangeUntil 관례
- `a..b` 사용 시 내부적으로 `a.rangeTo(b)` 호출
- `a..<b` 사용 시 내부적으로 `a.rangeUntil(b)` 호출 (코틀린 1.9부터 지원)

### 자신의 타입에 대해 루프 수행: iterator 관례
- `for (x in collection)` 사용 시 내부적으로 `collection.iterator()` 호출
- iterator()는 hasNext(), next() 가진 객체를 반환해야 함


## component 함수를 사용해 구조 분해 선언 제공
- 객체를 여러 변수로 한 번에 분해해서 받을 수 있는 문법
```kotlin
val (a, b) = p
```
- 구조 분해 선언의 각 변수를 추기화 하고자 할때 componentN() 함수를 호출
  - 여기서 N은 구조 분해 선언에 있는 변수 위치에 따라 붙는 번호
- data 클래스의 주 생성자에 들어있는 프로퍼티에 대해서는 컴파일러가 자동으로 componentN 함수를 만듬
```kotlin
class Person(val name: String, val age: Int) {
    operator fun component1() = name
    operator fun component2() = age
}

fun main() {
    val person = Person("Alice", 30)
    val (n, a) = person
    println("$n is $a years old")  // Alice is 30 years old
}
```

### 구조 분해 선언과 루프
- 루프에서 구조 분해 선언을 직접 사용
- 리스트의 쌍(pair), 맵(map) 반복할 때 사용
- Pair, Map.Entry, List 같은 기본 타입들은 이미 정의해 둬서 override가 필요 없음

```kotlin
val map = mapOf(1 to "one", 2 to "two")

for ((key, value) in map) {
    println("$key maps to $value")
}
```

### _문자를 사용해 구조 분해 값 무시
- 구조 분해할 때 필요 없는 값은 _ 로 무시 가능

```kotlin
val list = listOf(Pair(1, "one"), Pair(2, "two"))

for ((num, _) in list) {
    println(num)
}
```

## 프로퍼티 접근자 로직 재활용: 위임 프로퍼티
- 프로퍼티의 get/set 로직을 다른 객체에 위임 하는 기능
- by 키워드로 사용

### 위임 프로퍼티의 기본 문법과 내부 동작
```kotlin
// 위임 프로퍼티의 일반적인 문법
var p: Type by Delegate()
```
- Delegate 클래스를 통해 해당 프로퍼티의 get/set을 위임
- Delegate 클래스에는 반드시 `getValue`와 `setValue`가 필요함
- setValue는 var에만 사용 가능  

### 위임 프로퍼티 사용: by lazy()를 사용한 지연 초기화
- val 프로퍼티에만 사용 가능
- 처음 접근할 때 값 계산 → 그 뒤엔 캐싱된 값 반환
```kotlin
val lazyValue: String by lazy {
    println("computed!")
    "Hello"
}

fun main() {
    println(lazyValue)  // computed! 출력 후 Hello
    println(lazyValue)  // 그냥 Hello만 출력
}
```