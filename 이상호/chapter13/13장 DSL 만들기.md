# 13장 DSL 만들기

- API와 DSL의 차이를 설명
- DSL 형식의 API를 데이터베이스 접근, HTML 생성, 테스트, 빌드 스크립트 작성, 안드로이드 UI 레이아웃 정의 등의 여러 작업에 사용

## 13.1 API에서 DSL로: 표현력이 좋은 커스텀 코드 구조 만들기

궁극적인 목표는 코드의 가독성과 유지 보수성을 가장 좋게 유지하는 것
- 클래스의 API를 살펴봐야함
- 모든 개발자는 API를 훌륭하게 만들기 위해 노력해야 함
- 모든 클래스는 다른 클래스에게 자신과 상호작용할 수 있는 가능성을 제공
- 이런 상호작용을 이해하기 쉽고 명확하게 표현할 수 있도록 만들어야 프로젝트를 계속 유지보수 가능

API가 깔끔하다는 말
- 코드를 읽는 독자가 어떤 일이 벌어질지 명확하게 이해할 수 있어야함
  - 이름과 개념을 잘 선택하면 이런 목적을 달성 가능
- 코드에 불필요한 구문이나 번잡한 준비 코드가 가능한 적어야함
  - 깔끔한 API는 언어에 내장된 기능과 거의 구분할 수 없음

DSL은 메서드 호출만을 제공하는 API에 비해 더 표현력이 풍부해지고 사용하기 편함

### 13.1.1 도메인 특화 언어

- 범용 프로그래밍 언어
  - 명령적, 어떤 연산을 완수하기 위해 필요한 각 단계를 순서대로 정확히 기술
- 특정 과업 또는 영역에 초점을 맞춘 도메인 특화 언어
  - 선언적, 원하는 결과를 기술하기만 하고 그 결과를 달성하기 위해 필요한 세부 실행은 언어를 해석하는 엔진에 맡김
  - ex) SQL, 정규식
- DSL의 단점
  - 범용 언어로 만든 호스트 애플리케이션과 DSL을 함께 조합하기 어려움

### 13.1.2 내부 DSL은 프로그램의 나머지 부분과 매끄럽게 통합된다

외부 DSL
```sql
SELECT Country.name, COUNT(Customer.id)
FROM Country
INNER JOIN Customer 
        ON Country.id = Customer.country_id
GROUP BY Country.name
ORDER BY COUNT(Customer.id) DESC
LIMIT 1;
```

내부 DSL
```kotlin
(Country innerJoin Customer
    .slice(Country.name, Customer.id.count())
    .selectAll()
    .groupBy(Country.name)
    .orderBy(Count(Customer.id), order = SortOrder.DESC)
    .limit(1))
```

### 13.1.3 DSL의 구조

- DSL과 일반 API의 경계는 명확하지 않음
  - 구조적으로 명확한 기준 없음
- DSL에는 일반 API에는 없는 독특한 구조 존재
  - 함수 호출이 중첩되거나 일련의 호출 흐름을 통해 구조화된 데이터
- 전통적인 라이브러리는 여러 메서드를 명령처럼 나열하는 방식(command-query style)을 사용.
  - 호출의 흐름이 명확히 나뉘어 있음.
- DSL은 구조와 문맥을 통해 흐름이 연결되고 의미가 파생되는 방식.
  - 블록 구조로 표현.
- 내부 DSL은 프로그래밍 언어의 문법을 활용하여 표현력 있는 구조
  - 영어 문장처럼 읽히는 문장 구성 가능 (예: Country.name.groupBy().orderBy() 등).
  - 내부 DSL은 반복되는 호출과 구성에서 의미 있는 구조를 드러냄.

- DSL 방식
```kotlin
dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.exposed:exposed-core:0.40.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
}
```

일반 API 방식
```kotlin
project.dependencies.add("testImplementation", kotlin("test"))
project.dependencies.add("implementation", "org.jetbrains.exposed:exposed-core:0.40.1")
project.dependencies.add("implementation", "org.jetbrains.exposed:exposed-dao:0.40.1")
```

### 13.1.4 내부 DSL로 HTML 만들기

기본 구조
```kotlin
import kotlinx.html.stream.createHTML
import kotlinx.html.*

fun createSimpleTable() = createHTML().table {
    tr {
        td { +"cell" }
    }
}
```
이 함수는 하나의 셀만 있는 HTML 테이블을 생성합니다. 생성된 결과는 다음과 같은 HTML입니다.
```html
<table>
  <tr>
    <td>cell</td>
  </tr>
</table>
```

- 문자열 반환: createSimpleTable 함수는 HTML 조각을 포함하는 문자열을 반환
- 코틀린 코드로 HTML 생성: HTML을 직접 작성하지 않고 코틀린 DSL을 이용해 HTML 코드를 생성
- 컴파일 안정성: 잘못된 HTML 작성 시 컴파일 타임에서 오류를 잡을 수 있어 안정성이 높음
- 일반 코드와 통합 사용 가능: 일반 코틀린 코드 내부에 HTML 구조를 선언하고 사용 가능

동적인 HTML 생성 예제
```kotlin
import kotlinx.html.stream.createHTML
import kotlinx.html.*

fun createAnotherTable() = createHTML().table {
    val numbers = mapOf(1 to "one", 2 to "two")
    for ((num, string) in numbers) {
        tr {
            td { +"$num" }
            td { +string }
        }
    }
}
```
이 함수는 숫자와 문자열을 행 단위로 출력하는 HTML 테이블을 생성합니다.
```html
<table>
  <tr><td>1</td><td>one</td></tr>
  <tr><td>2</td><td>two</td></tr>
</table>
```

- DSL을 사용하면 HTML을 안전하고 유연하게 생성할 수 있으며, 기존 코드와의 통합도 용이
- DSL 문법은 일반 코드와 비슷하게 작동하여 학습 곡선이 낮음

## 13.2 구조화된 API 구축: DSL에서 수신 객체 지정 람다 사용

### 13.2.1 수신 객체 지정 람다와 확장 함수 타입

- 일반 람다와의 차이
  - 일반 람다는 it 키워드로 전달받은 인자를 참조해야 함.
  - 예를 들어 StringBuilder 인스턴스를 다룰 때 it.append(...) 형식 사용.
- 수신 객체 지정 람다
  - 수신 객체 지정 람다를 사용하면 this를 통해 대상 객체의 멤버에 직접 접근 가능.
  - 예를 들어 buildString 함수를 다음과 같이 정의하면:

```kotlin
fun buildString(builderAction: StringBuilder.() -> Unit): String {
    val sb = StringBuilder()
    sb.builderAction()  // 수신 객체 지정 람다 호출
    return sb.toString()
}
```
- 이 경우 람다 내부에서 append(...)만으로도 StringBuilder의 메서드 호출 가능

- 확장 함수 타입
  - 타입 형식: StringBuilder.() -> Unit
  - 수신 객체는 StringBuilder
  - 람다 내부에서 this는 StringBuilder 인스턴스
- 이점
  - 코드가 간결하고 명확해짐.
  - it. 없이 메서드 바로 호출 가능.
  - DSL 문법에서 매우 유용하게 사용됨.
- Kotlin 표준 함수 예
  - apply, with 함수도 이런 확장 함수 타입을 활용.

```kotlin
val result = buildString {
    append("Hello, ")
    append("World!")
}
```

### 13.2.2 수신 객체 지정 람다를 HTML 빌더 안에서 사용

- HTML 빌더란?
  - HTML을 코드로 생성하는 DSL에서 흔히 사용되는 구조.
  - 수신 객체 지정 람다를 사용하면 HTML 구조를 마치 실제 HTML처럼 중첩된 블록 형태로 작성할 수 있음.
  - ex) kotlinx.html 라이브러리
- 빌더 DSL 구조 예시
```kotlin
fun createSimpleTable() = createHTML().table {
    tr {
        td { +"cell" }
    }
}
```
- table, tr, td 각각은 수신 객체 지정 람다로 정의되어 있어 this를 생략하고도 메서드를 직접 호출 가능.
- +"cell"은 unaryPlus 연산자 오버로딩으로 구현됨.

- 이름 충돌 방지
  - HTML 태그들이 중첩될 경우, 수신 객체가 중복되어 혼동될 수 있음.
  - 이를 해결하기 위해 this@태그명 식으로 명시적 수신 객체 참조 사용
```kotlin
fun createSimpleTable() = createHTML().table {
    this@table.tr {
        this@tr.td {
            +"cell"
        }
    }
}
```

- DSL 충돌 방지 방법: @DslMarker
  - 여러 수신 객체가 혼동되지 않도록 제한하는 데 사용됨.
  - @DslMarker를 선언하고, HTML 태그 클래스에 붙이면 암시적 수신 객체의 범위 중복을 방지할 수 있음.
```kotlin
@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
open class Tag
```

- 구조화된 빌더 구현 예시
  - TABLE, TR, TD 클래스 정의하고 각각 자식 태그를 리스트로 가짐.
  - 각 태그 안에 doInit 함수로 자식 요소 초기화 및 추가 수행.
  - toString()을 통해 HTML 문자열로 렌더링
```kotlin
class TABLE : Tag("table") {
    fun tr(init: TR.() -> Unit) = doInit(TR(), init)
}

class TR : Tag("tr") {
    fun td(init: TD.() -> Unit) = doInit(TD(), init)
}
```

- 동적 HTML 생성
  - 반복문 등을 이용하여 HTML 요소를 동적으로 생성할 수 있음
```kotlin
fun createAnotherTable() = table {
    for (i in 1..2) {
        tr {
            td { }
        }
    }
}
```

### 13.2.3 코틀린 빌더: 추상화와 재사용을 가능하게 해준다

- 배경
  - DSL을 만들 때 수신 객체 지정 람다는 매우 유용하지만, 중복 코드나 구조 반복이 발생할 수 있음.
  - 예를 들어 HTML을 구성할 때 목록 항목이나 본문 요약 같은 패턴이 반복됨.
- 문제점
  - DSL 코드가 커지면 중복된 블록이 많아지고 가독성이 떨어짐.
  - 각 부분에 이름을 부여하거나, 재사용 가능한 방식으로 분리할 필요가 있음.
- 해결 방법: 추상화
  - listWithToc 같은 **도우미 함수(helper)**를 만들어 추상화할 수 있음.
  - 반복되는 HTML 구조(목차 + 본문)를 도우미 함수로 분리하면 코드가 간결해지고 재사용 가능성 증가.
```kotlin
fun buildBookList() = createHTML().body {
    listWithToc {
        item("Title1", "Description1")
        item("Title2", "Description2")
    }
}
```
- listWithToc 함수는 커스텀 수신 객체인 LISTWITHTOC를 사용하여 항목을 구성함

LISTWITHTOC 클래스 정의
```kotlin
class LISTWITHTOC {
    val entries = mutableListOf<Pair<String, String>>()

    fun item(headline: String, body: String) {
        entries += headline to body
    }
}
```
- DSL 사용자가 item()을 통해 데이터를 추가할 수 있게 함

- HTML 출력 처리
  - BODY.listWithToc 확장 함수에서 entries를 읽어 목차(ul > li)와 본문(h2, p)을 각각 생성.
  - 이 과정에서 entries.withIndex()를 사용하여 ID까지 포함한 HTML을 동적으로 생성
```kotlin
fun BODY.listWithToc(block: LISTWITHTOC.() -> Unit) {
    val listWithToc = LISTWITHTOC().apply(block)
    ul {
        for ((i, entry) in listWithToc.entries.withIndex()) {
            li { a("#$i") { +entry.first } }
        }
    }
    for ((i, entry) in listWithToc.entries.withIndex()) {
        h2 { id = "$i"; +entry.first }
        p { +entry.second }
    }
}
```

- 핵심 개념
  - 추상화: 도우미 함수(listWithToc)와 클래스(LISTWITHTOC)를 사용해 공통 구조 분리
  - 재사용: 여러 HTML 페이지에서 동일한 방식으로 목록 + 본문 구조 생성 가능
  - 수신 객체 지정 람다의 확장성: BODY 안에서 LISTWITHTOC를 수신 객체로 사용 가능

### 13.3 invoke 관례를 사용해 더 유연하게 블록 내포시키기

invoke 관례 사용하여 커스텀 타입 객체를 함수처럼 호출

### 13.3.1 invoke 관례를 사용해 더 유연하게 블록 내포시키기

- invoke 연산자를 정의하면 객체를 함수처럼 사용할 수 있음.
- ex) Greeter("Dmitry")처럼 사용하면 내부적으로 Greeter.invoke("Dmitry")를 호출.
- DSL에서 함수 호출을 더 직관적이고 간결하게 만들 수 있는 방법.
- 여러 파라미터도 지원 가능 → 함수형 인터페이스처럼 사용됨.

### 13.3.2 DSL의 invoke 관례: 그레이들 의존관계 선언

- Gradle의 dependencies { implementation(...) } 형태를 Kotlin DSL로 표현할 때 invoke 관례 사용.
- DependencyHandler 클래스에서 invoke 연산자 함수로 람다 블록을 받으면 DSL 구조를 자연스럽게 구성 가능.

```kotlin
operator fun invoke(body: DependencyHandler.() -> Unit) {
    body()
}
```
- 이렇게 하면 다음과 같이 DSL 작성 가능
```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
}
```
- dependencies.invoke { ... }로 처리되며, 내부적으로는 DependencyHandler가 수신 객체가 됨

### 13.4 실전 코틀린 DSL

### 13.4.1 중위 호출 연쇄시키기: 테스트 프레임워크의 should 함수

- 코틀린 DSL에서는 중위 호출을 통해 문장을 읽기 쉬운 형태로 만들 수 있음.
```kotlin
s should startWith("K")
```
- should 함수는 infix로 선언되어야 하며, Matcher<T> 인터페이스를 이용해 검증 로직 분리.
- startWith는 Matcher를 반환하고, should는 이를 테스트하는 구조.

### 13.4.2 원시 타입에 대해 확장 함수 정의하기: 날짜 처리

- 날짜/시간 DSL 예시에서 Int 타입에 대해 days, hours 등의 확장 프로퍼티를 정의함
```kotlin
val now = Clock.System.now()
val yesterday = now - 1.days
val later = now + 5.hours
```
- Int.days는 다음처럼 정의됨
```kotlin
val Int.days: Duration get() = this.toDuration(DurationUnit.DAYS)
```
- 기본 타입(Int 등)에 의미를 부여한 DSL 구성 방식의 예시로, 간결하고 읽기 쉬운 코드 작성 가능

### 13.4.3 멤버 확장 함수: SQL을 위한 내부 DSL

- 멤버 확장 프로퍼티란?
  - 클래스 외부에서 정의되지만 내부 멤버인 것처럼 작동하는 확장 프로퍼티입니다.
  - ex) id = integer("id").autoIncrement()처럼 자연스러운 DSL 표현이 가능해짐.

- Exposed 프레임워크 예제
  - Kotlin용 SQL DSL 라이브러리인 Exposed를 예로 들며, 테이블을 정의하는 코드를 DSL처럼 표현
```kotlin
object Country : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}
```

- select, eq 같은 조건식도 DSL로 표현
  - eq는 Column에 대한 멤버 확장 함수로 정의됨.
```kotlin
Country.select { Country.name eq "USA" }
```

- 문맥 수신 객체(Context Receiver)
  - 여러 수신 객체가 필요한 경우를 위해 Kotlin에서는 context receiver 기능을 실험적으로 지원.
  - 예: Table과 Column<Int> 양쪽 모두 접근 가능한 autoIncrement() 함수 구현 가능.

- 한계 및 고려사항
  - 일반 확장 함수의 한계
    - 기존 클래스의 멤버에는 접근 불가.
    - ex) Table에 직접 새로운 확장 함수를 추가하려면 소스 수정 없이는 불가.
  - 멤버 확장은 클래스에 연결된 맥락에서만 작동
    - DSL 설계 시 의도한 맥락을 잘 파악하고 정교하게 적용해야 함