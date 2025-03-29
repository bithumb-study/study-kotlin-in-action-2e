# 5장 람다를 사용한 프로그래밍

## 5.1 람다식과 멤버 참조

### 5.1.1 람다 소개: 코드 블록을 값으로 다루기

#### 함수형 프로그래밍

- 일급 시민 함수(= fist class citizen) : 함수를 값으로 핸들링
  - 함수를 변수에 할당, 파라미터 전달 가능
- 불변성 : 객체 만들 때 내부 상태 변하지 않는 점 보장
- 부수 효과 없음 : 함수가 같은 입력에 같은 결과 보장 &rarr; pure

```kotlin

// method
button.setOnClickListener(object: OnClickListener {
    override fun onClick(view: View) {
        println("I was Clicked")
    }
})

// lambda
button.setOnClickListener {
    println("I was Clicked")
}
```

### 5.1.2 람다와 컬렉션

```kotlin
val people = listOf(Person("Alice", 29), Person("Bob", 31))
println(people.maxBy { it.age }) // Person(name=Bob, age=31)

people.maxBy(Person::age) // lambda 개선
```

- lambda로 코드량 개선

### 5.1.3 람다식의 문법

- 코틀린 람다식 &rarr; 항상 중괄호({}) 둘러 쌓임

#### run

```kotlin
fun <T, R> T.run(block: T.() -> R): R

val person = Person("H43RO", 23)
val ageNextYear = person.run {
  ++age  // Return
}

println("$ageNextYear")  // 24

val person = run {
  val name = "H43RO"
  val age = 23
  Person(name, age)  // Return
}
```

- T 의 확장함수로 선언

#### lambda를 괄호 밖에 전달

```kotlin
fun main() {
    val people = listof(Person("Alice", 29), Person("Bob", 31)) 
    val names = people.joinToString(
        seperator = " ",
        transform = { p: Person -> p.name}
    )
    val names2 = people.joinToString(" ") { p: Person -> p.name }
    val names3 = people.joinToString(" ") { p -> p.name }
    val names4 = people.joinToString(" ") { it.name }
    val names5 = people.joinToString(" ") { Person::name }
  
    println(names) // Alice Bob
    print(names2) // Alice Bob 결과 동일
    print(names3) // Alice Bob 결과 동일 -> 컴파일러가 파라미터 타입 추론
    print(names4) // Alice Bob 결과 동일 -> it은 자동 생성된 파라미터 이름
    print(names5) // Alice Bob 결과 동일 -> lambda 식 적용
}
```

### 5.1.4 현재 영역에 있는 변수 접근

```kotlin
fun printProblemCounts (responses: Collection<String>) {
    var clientErrors = 0
    var serverErrors = 0

    responses.forEach {
        if (it.startsWith("4")) {
            clientErrors++
        } else if (it.startsWith("5")) {
            serverErrors++
        }
    }
    println("$clientErrors client errors, $serverErrors server errors")
}

fun main() {
    val responses = listOf("200 OK", "418 I'm a teapot", "500 Internal Server Error")
    printProblemCounts(responses) // 1 client errors, 1 server errors
}
```

- 자바와 달리, 람다에서 람다 밖 함수에 있는 final이 아닌 변수에 접근, 변경 가능

```kotlin
fun tryToCountButtonClicks(button: Button): Int {
    var clicks = 0
    button.onClick { clicks++ }
    return clicks // 항상 0 반환, 변경된 값 확인 불가
}
```

### 5.1.5 멤버 참조

```kotlin
val getAge = Person::age
val getAge2 = { person: Person -> person.age
val createPerson = ::Person // person 인스턴스 생성
}
```

- 멤버 참조 : ::을 사용하는 식


```kotlin
fun Person.isAdult() = age >= 21
val predicate = Person::isAdult
```

- 확장 함수도 멤버 함수와 동일하게 참조 가능

### 5.1.6 값과 엮인 호출 가능 참조

```kotlin
fun main() { 
  val seb = Person("hello", 26)
  val personAgeFunction = Person::age
  println(personAgeFunction(seb)) // 26
  
  val sebAgeFunction = seb::age
  println(sebAgeFunction()) // 26
}
```

- 인스턴스를 파라미터로 받아 값 반환
- 자신과 엮인 객체 멤버 값 반환

## 5.2 자바의 함수형 인터페이스 사용: 단일 추상 메서드

### 5.2.1 람다를 자바 메서드의 파라미터로 전달

```java
void postponeComputation(int delay, Runnable computation);
```

- java code

```kotlin
postponeComputation(1000) { println(42) }
```

- java 메서드를 코틀린 코드로 호출

```kotlin
fun handleComputation(id: String) {
    postponeComputation(1000) { // handleComputation 호출마다 Runnable 인스턴스 생성
        println(id)
    }
}
```

### 5.2.2 SAM 변환: 람다를 함수형 인터페이스로 명시적 변환

- `SAM` 인터페이스 : 함수형 인터페이스 &rarr; java 정의된 메서드를 kotlin에서 사용하는 경우
  - sam : single abstract method
  - 추상 메서드가 단 1개인 인터페이스

## 5.3 코틀린에서 SAM 인터페이스 정의: fun interface

- SAM 인터페이스는 추상 메서드가 단 1개

```kotlin
fun interface IntCondition {
  fun check(i: Int): Boolean // 추상 메서드 1개 존재
  fun checkString(s: String) = check(s.toInt())
  fun checkChar(c: Char) = check(c.digitToInt()) // 비추상 메서드 추가 정의 가능
}
```

## 5.4 수신 객체 지정 람다: with, apply, also

```kotlin
fun alphabet(): String {
    val result = StringBuilder()
    for (letter in 'A'..'Z') {
        result.append(letter)
    }
    result.append("\nNow I Know the alphabet!")
    return result.toString()
}
```

- alphabet 출력 함수

### 5.4.1 with 함수

```kotlin
fun alphabet() = with(StringBuilder()) {
    for (letter in 'A'..'Z') {
        this.append(letter) // stringbuilder가 해당 this
    }
    this.append("\nNow I Know the alphabet!")
    this.toString() // with 결과 반환
}
```

- 첫번째 인자 : 수신객체
- 두번째 인자: 첫번째에 받은 인자가 수신 객체인 람다
- 람다에서는 첫번째 인자로 받은 StringBuilder 인스턴스가 수신객체
- 람다안에서 마치 StringBuilder 내부 함수처럼 사용
- this 생략 가능

### 5.4.2 apply 함수

```kotlin
fun alphabet() = StringBuilder().apply {
    for (letter in 'A'..'Z') {
        append(letter) // stringbuilder가 해당 this
    }
    append("\nNow I Know the alphabet!")
    toString() // with 결과 반환
}
```

- apply를 임의 타입 확장 함수로 호출 가능
- 인스턴스 만들면서 프로퍼티 일부 초기화할 때 유용

### 5.4.3 객체에 추가 작업 수행: also

```kotlin
val x = listof(1, 2, 3).also {
    // ...
}
```

- 수신 객체를 받으며, 수신 객체 특정 동작 수행 후 수신 객체 반환
- 차이점 : 수신 객체를 인자로 참조
- 파라미터 내부 이름 부여하거나, 디폴트 네이밍 it 사용