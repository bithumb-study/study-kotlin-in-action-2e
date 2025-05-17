# 📚 9장 연산자 오버로딩과 다른 관례

- 코틀린은 관례에 의존한다. 이런 관례를 채택한 이유는 기존 자바 클래스를 코틀린 언어에 적용하기 위함이다.

___

## 📖 9.1 산술 연산자를 오버로드해서 임의의 클래스에 대한 연산을 더 편리하게 만들기

### 🔖 9.1.1 plus, times, divide 등: 이항 산술 연산 오버로딩

```kotlin
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}
```

- 연산자를 오버로딩 하는 함수 앞에는 반드시 operator 가 있어야 한다.
- 해당 키워드를 붙임으로써 관례를 따르는 함수임을 명확히 알수 있고 실수로 관례에서 사용하는 함수 이름을 사용하는 경우를 막아준다.

```kotlin

operator fun Point.plus(other: Point): Point {
    return Point(x + other.x, y + other.y)
}
```

- 중위 함수는 커스텀 연산자의 장점을 제공하면서 기억하기 힘든 임의의 기호의 조합으로 이름을 붙였을때 커스텀 연산자가 줄 수 있는
  고통을 줄여준다.
- 일반 함수와 마찬가지로 operator 함수도 오버로딩 할수 있다.

### 🔖 9.1.2 연산을 적용한 다음에 그 결과를 바로 대입: 복합 대입 연산자 오버로딩

```kotlin
a += b
a = a.plus
a.plusAssign(b)
```

- +와 -는 항상 새로운 컬렉션을 반환하며 += 와 -=연산자는 항상 변경 가능한 컬렉션에 작용해 메모리에 있는 객체 상태를 반환한다.
- 읽기 전용 컬렉션에서 +=, -= 는 변경을 적용한 복사본을 반환한다.
- 이런 연산자의 피연산자로는 개별 원소를 사용하거나 원소 탕비이 일치하는 다른 컬렉션을 사용가능

```kotlin
fun main() {
    val list = mutableListOf(1, 2)
    list += 3
    val newList = list + listOf(4, 5)
}
```

### 🔖 9.1.3 피연산자가 1개뿐인 연산자: 단항 연산자 오버로딩

- 단항 연산자를 오버로딩하는 절차도 이항 연산자와 같다. 미리 정해진 이름의 함수를 선언하면서 operator 로 표시하면된다.

```kotlin
operator fun Point.unaryMinus(): point {
    return Point(-x, -y)
}
```

- 단항 연산자를 오버로딩하기 위해 사용하는 함수는 인자를 취하지 않는다.

## 📖 9.2 비교 연산자를 오버로딩해서 객체들 사이의 관계를 쉽게 검사

- 코틀린에서는 == 를 사용하여 자바의 equals 나 compareTo 처럼 비교가능 하여 읽기 쉽다.

### 🔖 9.2.1 동등성 연산자: equals

- == 연산자는 equals 메서드 호출로 컴파일 된다.
- != 연산자도 마찬가지 이고 두 연산자는 내부에서 null 인지 검사하므로 다른 연산과 달리 null 이 될수 있는 값에도 적용할수 있다.

### 🔖 9.2.2 순서 연산자: compareTo (<,>,<=,>=)

- 코틀린에서도 Comparable 인터페이스를 지원하므로 <,>,<=,>= 사용시 compareTo 호출로 컴파일 한다.

## 📖 9.3 컬렉션과 범위에 대해 쓸 수 있는 관례

### 🔖 9.3.1 인덱스로 원소 접근: get 과 set

```kotlin
val value = map[key]
map[key] = newValue
```

- 인덱스 접근 연산자를 사용해 원소를 읽는 연산은 get 연산자
- 원소를 쓰는 연산은 set 연산자 메서드로 변환된다.

### 🔖 9.3.2 어떤 객체가 컬렉션에 들어있는지 검사: in 관례

- in은 객체가 컬렉션에 들어있는지 검사 한다.
- 대응하는 함수는 contains 이다.

### 🔖 9.3.3 객체로부터 범위 만들기: rangeTo와 rangeUntil 관례

- 범위를 만들때 .. 구문을 사용한다.
- .. 연산자는 rangeTo 함수 를 간략하게 표현하는 방법
- Comparable 인터페이스를 구현하면 rangeTo 를 정의 할필요없음
- rangeUntil 연산자는 ..< 열린구문으로 만든다.

### 🔖 9.3.4 자신의 타입에 대해 루프 수행: iterator 관례

```kotlin
operator fun CharSequence.iterator(): CharIterator
```

- 코틀린도 ioterator 메서드를 확장함수로 정의 가능
- hasNext 와 next 호출을 반복하는 식으로 변환된다.

## 📖 9.4 component 함수를 사용해 구조 분해 선언 제공

- 구조 분해를 사용하면 복합적인 값을 분해해서 별도의 여러 지역 변수를 한꺼번에 초기화 할수 있다.

```kotlin
fun main() {
    val p = Point(10, 20)
    val (x, y) = p
    println(p)
}

val (a, b) = p
val a = p.component1()
val b = p.component2()
```

- data 클래스의 주 생성자에 들어있는 프로퍼티에 대해서는 컴파일러가 자동으로 componentN 함수를 만들어준다.
- 구조 분해 선언은 함수에서 여러값을 반환할때 유용하다.
- 코틀린 표준 라이브러리에서는 맨 앞의 다섯 원소에 대한 componentN을 제공

### 🔖 9.4.1 구조 분해 선언과 루프

- 함수 본문 내의 선언문 뿐만 아니라 변수 선언이 들어갈 수 있는 장소라면 어이든 구조 분해 선언을 사용할수 있다.

```kotlin
fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key->$value")
    }
}
```

### 🔖 9.4.2 _ 문자를 사용해 구조 분해 값 무시

```kotlin
data class Person(
    val firstName: String,
    val secondName: String,
    val age: Int,
    val city: String,
)

fun introducePerson(p: Person) {
    val (firstName, _, age) = p
}
```

- 코틀린은 사용하지 않는 구조 분해 선언에 대해 _ 문자를 쓸 수 있게 해준다.

## 📖 9.5 프로퍼티 접근자 로직 재활용: 위임 프로퍼티

- 위임 프로퍼티를 사용하면 값을 뒷받침하는 필드에 단순히 저장하는 것보다 더 복잡한 방식으로 작동하는 프로퍼티를 접근자 로직을
  구현할 매번 재구현할 필요 없이 쉽게 구현할 수 있다.
- 위임은 객체가 직접 작업을 수행하지 않고 다른 도우미 객체가 그 작업을 처리하도록 맡기는 디자인 패턴을 말한다.
- 이때 작업을 처리하는 도우미 객체를 위임 객체라고 부른다.

### 🔖 9.5.1 위임 프로퍼티의 기본 문법과 내부 동작

```kotlin
var p: Type by Delegate()
```

- p 프로퍼티는 접근자 로직을 다른 객체에 위임
- Delegate 클래스의 인스턴스를 위임 객체로 사용한다.

```kotlin
class Foo {
    private val delegate = Delegate()
    val p: Type
        set(value: Type) = delegate.setValue()
        get() = delegate.getValue()
}

class Delegate {
    operator fun getValue()
    operator fun setValue()
}
```

- p 는 내부적으로는 Delegate 타입의 위임 프로퍼티 객체에 있는 메서드를 호출한다.

### 🔖 9.5.2 위임 프로퍼티 사용: by lazy() 를 사용한 지연 초기화

- 지연초기화는 객체의 일부분을 초기화하지 않고 남겨뒀다가 실제로 그 부분의 값이 필요할 경우 초기화 할때 흔히 쓰이는 패턴이다.

```kotlin
class Person(val name: String) {
    val emails by lazy { loadEmails(this) }
}
```

- 위임 프로퍼티는 데이터를 저장할 때 쓰이는 뒷받침하는 프로퍼티와 값이 오직 한번만 초기화됨을 보장하는 게터 로직을 함께 캡슐화 해준다.

### 🔖 9.5.3 위임 프로퍼티 구현

```kotlin
import kotlin.reflect.KProperty

class ObservableProperty(var propValue: Int, val observable: Observable) {
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): Int = propValue
    operator fun setValue(thisRef: Any?, prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        observable.notifyObservers(prop.name, oldValue, newValue)
    }
}
class Person(val name: String, age: Int, salary: Int) : Observable() {
    val age by ObservableProperty(age, this)
    val salary by ObservableProperty(salary, this)
}
```

- by 키워드를 사용해 위임 객체를 지정하면 여러 작업을 코틀린 컴파일러가 자동으로 처리 해준다.

```kotlin

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class Person(val name: String, age: Int, salary: Int) : Observable() {
    private val onChange =
        { property: KProperty<*>, oldValue: Any?, newValue: Any? -> notifyObservers(property.name, oldValue, newValue) }
    val age by Delegates.observable(age, this)
    val salary by Delegates.observable(salary, this)
}
```

### 🔖 9.5.4 위임 프로퍼티는 커스텀 접근자가 있는 감춰진 프로퍼티로 변환된다.

```kotlin
class C {
    val prop: Type by MyDelegate()
}

val c = C()

val x = c.prop // val x = <delegate>.getValue(c,<property>)
c.prop = x // <delegate>.setValue(c,<property>,x)
```

- MyDelegate 클래스의 인스턴스는 감춰진 프로퍼티에 저장되며, 그 프로퍼티를 <delegate> 라는 이름으로 부른다.
- 이 메커니즘은 상당히 단순하지만 흥미로운 활용법이 있음
    - 프로퍼티 값이 저장될 장소를 바꿀 수도 있고 프로퍼티를 읽거나 쓸 때 벌어질 일을 변경 할수도 있다.

### 🔖 9.5.5 맵에 위임해서 동적으로 애트리뷰트 접근

- by 키워드 뒤에 맵을 직접 넣으면 된다.

### 🔖 9.5.6 실전 프레임워크가 위임 프로퍼티를 활용하는 방법
- 
