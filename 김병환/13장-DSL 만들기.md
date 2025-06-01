# 📚 13장 DSL 만들기

## 📖 13.1 API에서 DSL로: 표현력이 좋은 커스텀 코드 구조 만들기

| 일반 구문                         | 간결한 구문                                    | 사용한 언어 특성         |
|-------------------------------|-------------------------------------------|-------------------|
| `StringUtil.capitalizes(s)`   | `s.capitalize()`                          | 확장 함수             |
| `1.to("one")`                 | `1 to "one"`                              | 중위 호출             |
| `set.add(2)`                  | `set += 2`                                | 연산자 오버로딩          |
| `map.get("key")`              | `map["key"]`                              | `get` 메소드에 대한 관례  |
| `file.use({ f -> f.read() })` | `file.use { it.read() }`                  | 람다를 괄호 밖으로 빼내는 관례 |
| `sb.append("yes")`            | `with(sb) { append("yes") append("no") }` | 수신 객체 지정 람다       |

- 깔끔한 API를 작성할 수 있게 돕는 코틀린 기능에는 위와 같은 기능들이 있다.

### 🔖 13.1.1 도메인 특화 언어

- 가장 익숙한 DSL은 SQL과 정규식일 것이다.
- DSL이 범용 프로그래밍 언어와 달리 더 선언적이다.
  - 범용 프로그래밍 언어는 명령적이다.
- 선언적 언어는 원하는 결과를 기술하기만 하고 그 결과를 달성하기 위해 필요한 세부 실행은 언어를 해석하는 엔진에 맡긴다.
- DSL의 가장 큰 단점은 범용 언어로 만든 호스트 애플리케이션과 DSL을 함께 조합하기가 어렵다는 것
  - DSL은 자체 문법이 있기 때문에 다른 언어의 프로그램 안에 직접 포함시킬 수가 없다.
- 이러한 단점을 해결하면서 DSL의 다른 이점을 살리는 방법으로 코틀린에서는 내부 DSL을 만들 수 있게 해준다.

### 🔖 13.1.2 내부 DSL은 프로그램의 나머지 부분과 매끄럽게 통합된다

- 독립적인 문법 구조를 갖는 외부 DSL과는 반대로 내부 DSL은 범용 언어로 작성된 프로그램의 일부며, 범용 언어와 동일한 문법을 사용한다.

```sql
SELECT country.name, COUNT(customer.id)
FROM country
         JOIN customer ON customer.country_id = country.id
GROUP BY country.name
ORDER BY COUNT(customer.id) DESC LIMIT 1;
```

- 이를 코틀린과 익스포즈드(코틀린으로 작성된 데이터베이스 프레임워크)를 사용해 구현한 예는 아래와 같다.

```kotlin
(Country innerJoin Customer)
    .slice(Country.name, Count(Customer.id))
    .selectAll()
    .groupBy(Country.name)
    .orderBy(Count(Customer.id), order = SortOrder.DESC)
    .limit(1)
```

- 이를 내부 DSL이라고 부른다.

### 🔖 13.1.3 DSL의 구조

- DSL에만 존재하는 특징은 구조(문법)이다.
- 일반적인 API는 명령-질의 API이다.
- DSL은 문법에 의해 정해진다.
  - 이러한 문법 때문에 내부 DSL을 언어라고 부를 수 있다.
  - 여러 함수 호출을 조합해서 연산을 만들며 타입 검사기는 여러 함수 호출이 바르게 조합됐는지를 검사한다.
- 메서드 호출 연쇄는 DSL 구조를 만드는 또 다른 방법이다.

### 🔖 13.1.4 내부 DSL로 HTML 만들기

- `kotlinx.html`로 HTML을 만들 수 있다.
- 직접 HTML 텍스트를 작성하지 않고 코틀린 코드로 HTML을 만들면 타입 안전성을 보장한다.
- 내부에 코틀린 코드를 원하는 대로 사용할 수 있다.
- HTML은 고전적인 마크업 언어 예제이며 DSL의 개념을 제대로 보여준다.
- XML과 같이 HTML과 구조가 비슷한 다른 모든 언어에 이와 비슷한 접근 방식을 택할 수 있다.

## 📖 13.2 구조화된 API 구축: DSL에서 수신 객체 지정 람다 사용

### 🔖 13.2.1 수신 객체 지정 람다와 확장 함수 타입

```kotlin
fun buildString(
    builderAction: (StringBuilder) -> Unit
): String {
    val sb = StringBuilder()
    builderAction(sb)
    return sb.toString()
}

fun main() {
    val s = buildString {
        it.append("Hello, ")
        it.append("World!")
    }
    println(s)
}
```

- 람다를 인자로 받는 `buildString` 정의
- 람다 본문에서 매번 it을 사용해 `StringBuilder` 인스턴스를 참조해야 한다.

```kotlin
fun buildString(
    builderAction: StringBuilder.() -> Unit
): String {
    val sb = StringBuilder()
    sb.builderAction()
    return sb.toString()
}

fun main() {
    val s = buildString {
        this.append("Hello, ")
        this.append("World!")
    }
    println(s)
}
```

- 수신 객체 지정 람다를 인자로 넘기기 때문에 람다 안에서 it을 사용하지 않아도 된다.

```kotlin
val appendExcl: StringBuilder.() -> Unit =
    { this.append("!") }


fun main() {
    val stringBuilder = StringBuilder("Hi")
    stringBuilder.appendExcl()
    println(stringBuilder)
    println(buildString(appendExcl))
}
```

- 코드에서 수신 객체 지정 람다는 일반 람다와 똑같다 보인다.

```kotlin
fun builderString(builderAction: StringBuilder.() -> Unit): String =
    StringBuilder().apply(builderAction).toString()
```

- 표준 라이브러리의 `builderString` 구현은 더 짧다.
- 기본적으로 `apply`와 `with`는 모두 자신이 제공받은 수신 객체를 갖고 확장 함수 타입의 람다를 호출

### 🔖 13.2.2 수신 객체 지정 람다를 HTML 빌더 안에서 사용

- HTML을 만들기 위한 코틀린 DSL을 보통은 HTML 빌더라고 부른다.

```kotlin
fun createSimpleTable() = createHTML().table {
    tr {
        td { +"cell" }
    }
}
```

- 모두 평범한 함수다.
- 각 수신 객체 지정 람다가 이름 결정 규칙을 바꾼다.

```kotlin
open class Tag

class TABLE : Tag {
    fun tr(init: TR.() -> Unit)
}

class TR : Tag {
    fun td(init: TD.() -> Unit)
}

class TD : Tag
```

- 모두 유틸리티 클래스다.

```kotlin
fun createSimpleTable() = createHTML().table {
    this@table.tr {
        (this@tr).td {
            +"cell"
        }
    }
}
```

- this 참조를 쓰지 않아도 되면 빌더 문법이 간단해지고 전체적인 구문이 원래의 HTML 구문과 비슷해진다.

```kotlin
@DslMarker
annotation class HtmlTagMarker
```

- `@DslMarker` 어노테이션을 사용해 내포된 람다에서 외부 람다의 수신 객체에 접근하지 못하게 제한할 수 있다.
- 메타 어노테이션이다.

```kotlin
@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
open class Tag(val name: String) {
    private val children = mutableListOf<Tag>()

    protected fun <T : Tag> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children.add(child)
    }

    override fun toString() = "<$name>${children.joinToString("")}</$name>"
}

fun table(init: TABLE.() -> Unit) = TABLE().apply(init)

class TABLE : Tag("table") {
    fun tr(init: TR.() -> Unit) = doInit(TR(), init)
}

class TR : Tag("tr") {
    fun td(init: TD.() -> Unit) = doInit(TD(), init)
}

class TD : Tag("td")

fun createTable() = table {
    tr {
        td {
        }
    }
}
```

- 간단한 HTML 빌더의 전체 구현

### 🔖 13.2.3 코틀린 빌더: 추상화와 재사용을 가능하게 해준다

```kotlin
fun buildBookList() = createHTML().body {
    ul {
        li { a("#1") { +"The Three-Body Problem" } }
        li { a("#2") { +"The Cartesian Product Problem" } }
        li { a("#3") { +"The Conjugation Problem" } }
    }

    h2 { id = "1"; +"The Three-Body Problem" }
    p { +"The Three-Body Problem is a classic physics problem from the early 20th century." }

    h2 { id = "2"; +"The Cartesian Product Problem" }
    p { +"The Cartesian product is a useful mathematical construct for a wide variety of problems." }

    h2 { id = "3"; +"The Conjugation Problem" }
    p { +"The Conjugation Problem is a classic computational problem from the 1970s." }
}
```

- 목차로 시작하는 페이지를 코틀린 HTML 빌더로 만들기
- 이걸 좀 더 이쁘게 다듬을 수 있다.

```kotlin
fun buildBookList() = createHTML().body {
    listWithToc {
        item(
            "The Three-Body Problem",
            "The Three-Body Problem is a classic physics problem from the early 20th century."
        )
        item(
            "The Cartesian Product Problem",
            "The Cartesian product is a useful mathematical construct for a wide variety of problems."
        )
        item(
            "The Conjugation Problem",
            "The Conjugation Problem is a classic computational problem from the 1970s."
        )

    }
}
```

```kotlin
@HtmlTagMarker
class LISTWITHTOC {
    val entries = mutableListOf<Pair<String, String>>()
    fun item(headline: String, body: String) {
        entries += headline to body
    }
}
```

- `@HtmlTagMarker` 어노테이션을 붙여 DSL 영역 규칙을 따르게 할 수 있다.

```kotlin
fun BODY.listWithToc(block: LISTWITHTOC.() -> Unit) {
    val listWithToc = LISTWITHTOC()
    listWithToc.block()
    ul {
        for ((index, entry) in listWithToc.entries.withIndex()) {
            li {
                a("#${index}") { +entry.first }
            }
        }
    }
    for ((index, entry) in listWithToc.entries.withIndex()) {
        h2 { id = "$index"; +entry.first }
        p { +entry.second }
    }
}
```

- 추상화와 재사용을 통해 코드를 개선하고 이해하기 쉽게 만드는 방법이다.

## 📖 13.3 invoke 관례를 사용해 더 유연하게 블록 내포시키기

### 🔖 13.3.1 invoke 관례를 사용해 더 유연하게 블록 내포시키기

```kotlin
class Greeter(val greeting: String) {
    operator fun invoke(name: String) {
        println("$greeting, $name!")
    }
}

fun main() {
  val bavarianGreeter = Greeter("Servus")
  bavarianGreeter("Dmitry")
}
```

- 클래스 안에서 `invoke` 메소드 정의
- `invoke` 메서드의 시그니처에 대한 요구사항은 없다.
- 원하는대로 파라미터 개수나 타입을 지정할 수 있다.

### 🔖 13.3.2 DSL의 invoke 관례: 그레이들 의존관계 선언

```groovy
dependencies {
  testImplementation(kotlin("test"))
  implementation("org.jetbrains.exposed:exposed-core:0.40.1")
  implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
}
```

- 이 코드처럼 내포된 블록 구조를 허용하는 한편, 평평한 함수 호출 구조도 함께 제공하는 API를 만들고 싶다.

```kotlin
dependencies.implementation("org.jetbrains.exposed:exposed-core:0.40.1")

dependencies {
  implementation("org.jetbrains.exposed:exposed-core:0.40.1")
}
```

- 첫 번째 구문은 `invoke`를 사용한 것이다.

```kotlin
class DependencyHandler {
    fun implementation(coordinate: String) {
        println("Added dependency on $coordinate")
    }

    operator fun invoke(body: DependencyHandler.() -> Unit) {
        body()
    }
}

fun main() {
    val dependencies = DependencyHandler()
    dependencies.implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
    }
}
```

- 유연한 DSL 문법을 제공하기 위해 `invoke` 사용
- 꽤 적은 양의 코드지만 이렇게 재정의한 `invoke` 메서드로 인해 DSL API의 유연성이 훨씬 커진다.

## 📖 13.4 실전 코틀린 DSL

### 🔖 13.4.1 중위 호출 연쇄시키기: 테스트 프레임워크의 should 함수

```kotlin
class PrefixTest {
    @Test
    fun testKPrefix() {
        val s = "kotlin".uppercase()
        s should startWith("K")
    }
}
```

- 일반 영어처럼 코드를 읽을 수 있다.

```kotlin
infix fun <T> T.should(matcher: Matcher<T>) = matcher.test(this)

interface Matcher<T> {
  fun test(value: T)
}

fun startWith(prefix: String): Matcher<String> {
  return object : Matcher<String> {
    override fun test(value: String) {
      if (!value.startsWith(prefix)) {
        throw AssertionError("$value does not start with $prefix")
      }
    }
  }
}
```

- DSL 에서 사용하려면 infix 변경자를 붙여야 한다.
- 중위 호출과 object로 정의한 싱글턴 객체 인스턴스를 조합하면 DSL에 상당히 복잡한 문법을 도입할 수 있고, 그 문법을 사용하면 DSL 구문을 깔끔하게 만들 수 있다.

### 🔖 13.4.2 원시 타입에 대해 확장 함수 정의하기: 날짜 처리

```kotlin
val Int.days: Duration
    get() = this.toDuration(DurationUnit.DAYS)

val Int.hours: Duration
    get() = this.toDuration(DurationUnit.HOURS)
```

- 코틀린에서는 아무 타입이나 확장 함수의 수신 객체 타입이 될 수 있다.

### 🔖 13.4.3 멤버 확장 함수: SQL을 위한 내부 DSL

- 클래스 안에서 확장 함수와 확장 프로퍼티를 선언하면 그들이 선언된 클래스의 멤버인 동시에 그들이 확장하는 다른 타입의 멤버이기도 하다.
  - 이런 함수나 프로퍼티를 멤버 확장이라 부른다.
- 멤버 확장도 여전히 멤버다.
- 예를 들어, `Table` 클래스의 `integer`, `varchar` 등의 메서드는 외부에서 호출할 수 없지만, 멤버 확장 함수를 통해 제한된 범위 내에서만 사용하도록 할 수 있다.