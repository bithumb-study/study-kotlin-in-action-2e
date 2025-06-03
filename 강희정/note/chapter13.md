# 13장. DSL 만들기

DSL(Domain Specific Language)

- 도메인 특화 언어

코틀린 DSL 설계에 사용되는 코틀린의 특성

- 수신 객체 지정 람다
    - 코드 블록에서 이름이 가리키는 대상을 결정하는 방식을 변경
- invoke 관례
    - DSL코드 안에서 람다와 프로퍼티 대입을 더 유연하게 조합

# 13.1 API에서 DSL로: 표현력이 좋은 커스텀 코드 구조 만들기

깔끔한 API의 의미

- 코드를 읽는 독자가 어떤 일이 벌어질지 명확하게 이해할 수 있어야 함
- 코드에 불필요한 구문이나 번잡한 준비 코드가 가능한 한 적어야 한다.

→ 코틀린의 확장 함수, 중위 함수 호출, 연산자 오버로딩, 문법적 편의 등등 코틀린의 여러 기능으로 깔끔한 API 설계 가능

코틀린 DSL

- 코틀린의 문법적 특성과 여러 메서드 호출에서 구조를 만들어내는 능력 위에 구축됨
- 메서드 호출만을 제공하는 API에 비해 더 표현력이 풍부해지고 사용하기 편해짐
- 컴파일 시점에 타입이 정해짐 → 정적 타입 지정 언어의 장점을 누릴 수 있음

코틀린 DSL 예시

```kotlin
// 하루 전 날 반환
val yesterday = Clock.System.now() - 1.days

// HTML 표 생성
fun createSimpleTable() = createHTML().
		table {
				tr {
						td { +"cell" }
				}
	}
```

## 13.1.1 도메인 특화 언어

범용 프로그래밍 언어

- 컴퓨터로 풀 수 있는 모든 문제를 풀 수 있는 기능을 제공
- 명령적(imperative)
    - 어떤 연산을 완수하기 위해 필요한 각 단계를 순서대로 정확히 기술
    - 각 연산에 대한 구현을 독립적으로 최적화해야 함

도메인 특화 언어

- 특정 과업 또는 영역에 초점을 맞추고 그 영역에 필요하지 않은 기능을 없앰
- SQL, 정규식
- 스스로 제공하는 기능을 제한함으로써 더 효율적으로 자신의 목표를 달성할 수 있음
- 범용 언어를 사용하는 경우보다 특정 영역에 대한 연산을 더 간결하게 기술할 수 있음
- 선언적
    - 원하는 결과를 기술하기만 하고 그 결과를 달성하기 위해 필요한 세부 실행 → 해석 엔진에 맡김 → 더 효율적

DSL의 단점

- 범용 언어로 만든 호스트 애플리케이션과 조합하기 어려움
- 컴파일 시점에 검증하기 어려움
- IDE의 기능을 제공하기 어려움
- DSL언어와 호스트 언어를 함께 배워야 함
- 코드를 읽기 어려움

## 13.1.2 내부 DSL은 프로그램의 나머지 부분과 매끄럽게 통합된다

내부 DSL

- 범용 언어로 작성된 프로그램의 일부
- 범용 언어와 동일한 문법 사용

→ DSL의 핵심 장점을 유지하면서 주 언어를 별도의 문법으로 사용

## 13.1.3 DSL의 구조

DSL은 API와 다르게 **구조(문법)**적 특징이 있음

- 명령-질의 API
    - 전형적인 라이브러리
    - 여러 메서드로 이루어짐
    - 클라이언트는 메서드를 한 번에 하나씩 호출함으로써 라이브러리 사용
- DSL 구조
    - 람다를 내포시키거나 메서드 호출을 연쇄시키는 방식
    - 메서드를 조합하여 질의
    - 타입 검사기는 여러 함수 호출이 바르게 조합되었는지 검사

DSL의 장점 → 같은 맥락을 매 함수 호출 시마다 반복하지 않고도 재사용할 수 있음

```kotlin
dependencies {
		testImplementation(kotlin("test"))
		implementation("org.jetbrains.exposed:exposed-core:0.40.1")
}
```

→ 같은 맥락에서 사용

```kotlin
project.dependencies.add("testImplementation", kotlin("test"))
project.dependencies.add("implementation", kotlin("org.jetbrains.exposed:exposed-core:0.40.1"))
```

→ 일반 명령-질의 API

```kotlin
str should startWith("kot")
```

- 메서드 호출을 연쇄시켜 구조를 만듦
- 중위 호출 구문(should) 사용하여 가족성 향상

## 13.1.4 내부 DSL로 HTML 만들기

kotlinx.html 라이브러리 사용하여 코틀린 코드로 HTML을 만들 수 있음

→ 왜 이렇게 하는게 더 좋을까?

- 코틀린 버전은 타입 안정성을 보장함
- DSL도 코틀린 코드이므로, 동적으로 코틀린 코드를 원하는대로 사용할 수 있다.

# 13.2 구조화된 API 구축: DSL에서 수신 객체 지정 람다 사용

## 13.2.1 수신 객체 지정 람다와 확장 함수 타입

수신 객체

- this로 암시적으로 사용되는 메서드나 람다의 대상 객체

수신 객체 지정 람다

- 수신 객체를 명시하지 않고 람다의 본문 안에서 다른 객체의 메서드를 호출
- 람다 안에서 this 없이도 수신 객체의 메서드와 프로퍼티를 직접 호출

일반 람다

```kotlin
val normalLambda: (String) -> Unit = { str ->
    println(str.uppercase())
}
```

- `str`이 람다의 **매개변수**
- `str.uppercase()`처럼 접근해야 함

수신 객체 지정 람다

```kotlin
val receiverLambda: String.() -> Unit = {
    println(uppercase())  // this 생략 가능!
}
```

- `String.()`은 **수신 객체가 String이라는 뜻**
- `this`를 생략하고도 `String`의 메서드와 프로퍼티에 직접 접근 가능

→ 수신 객체 지정 람다를 사용하면 이름과 마침표를 명시하지 않아도 그 인자의 멤버를 바로 사용할 수 있다.

확장 함수 타입

- String.(Int, Int) → Unit
- String은 수신 객체 타입
- (Int, Int)는 파라미터 타입
- Unit은 반환 타입
- 확장 함수처럼 호출될 수 있는 코드 블록
- 객체를 인자로 넘기는 대신, 람다 변수를 확장 함수처럼 호출해야 함

```kotlin
buildString { this.append("!") }

fun buildString(builderAction: StringBuilder.() -> Unit): String {
		val sb = StringBuilder()
		sb.builderAction()
} 
```

- 람다 본문 안에서는 sb(수신객체)가 this(암시적 수신 객체)가 된다.

소스코드에서의 수신 객체 지정 람다 → 일반 람다랑 똑같아 보임

- 람다에 수신 객체가 있는지 보려면? → 람다가 전달되는 함수의 시그니처 확인
- 람다의 파라미터로 확장함수 타입을 받는다면 → 수신객체 지정 람다 사용하고 있다.

apply

- 인자로 받은 람다나 함수를 호출하며 자신의 수신 객체를 람다나 함수의 암시적 수신 객체로 사용
- 수신 객체 타입에 대한 확장 함수로 선언됨 → 수신 객체의 메서드처럼 호출, 수신 객체를 암시적 인자로 받는다.

```kotlin
inline fun <T> T.apply(block: T.() -> Unit): T{
		block()
		return this
}
```

→ 수신 객체를 반환

with

- 자신이 제공받은 수신 객체를 가지고 확장 함수 타입의 람다를 호출
- 수신 객체를 첫 번째 파라미터로 받음
- 람다를 호출해 얻은 결과를 반환

```kotlin
inline fun <T, R> with(receiver: T, block: T.() -> R): R = receiver.block()
```

## 13.2.2 수신 객체 지정 람다를 HTML 빌더 안에서 사용

HTML 빌더

- HTML을 만들기 위한 코틀린 DSL
- 빌더를 사용하면 객체 계층 구조를 선언적으로 정의할 수 있음
- 타입 안정성 보장 가능

```kotlin
fun createSimpleTable() = createHtml().
		table {
				tr {
						td { +"cell" }
				}
		}
```

- 각 함수는 고차 함수로, 수신 객체 지정 람다를 인자로 받는다.
- 각 람다는 자신의 본문에서 호출되는 함수들을 새로 추가한다
- td함수는 tr 안에서만 접근 가능하고, tr함수는 table 함수 안에서만 접근 가능하다

→ 블록의 이름 결정 규칙이 있다.

- 각 람다의 수신 객체에 의해 블록의 이름 결정 규칙이 정해진다.

```kotlin
open class Tag

class TABLE: Tag {
		fun tr(init: TR.() -> Unit)
}

class TR: Tag {
		fun td(init: TD.() -> Unit)
}

class TD: Tag
```

- tr함수는 TR타입을 수신 객체로 받는 람다를 인자로 받는다.
- td함수는 TD타입을 수신 객체로 받는 람다를 인자로 받는다.
- TR.() → Unit과 TD.() → Unit은 모두 확장 함수 타입이다.

위와 같은 구조이면 안쪽 람다에서 외부 람다에 정의된 수신 객체를 사용할 가능성이 있다

→ @DslMarker 어노테이션을 사용해 제한

@DslMarker

- 내포된 람다에서 외부 람다의 수신 객체에 접근하지 못하게 제한
- 어노테이션 클래스에 적용할 수 있는 메타어노테이션
- 같은 @DslMarker 어노테이션이 붙은 영역 안에서는 암시적 수신 객체가 결코 2개가 될 수 없다.

HTML 태그 빌더 함수

```kotlin
fun tr(init: TR.() -> Unit) {
		val tr = TR()
		tr.init()
		children.add(tr)
}
```

- 주어진 태그를 초기화하고 바깥쪽 태그의 자식으로 추가함

```kotlin
@DslMarker
annotation class HtmlTagMarker

@HtmlMarker
open class Tag(val name: String) {
		private val children = mutableListOf<Tag>()  // 모든 내포 태그 저장
		
		protected fun <T: Tag> doInit(child: T, init: T.() -> Unit) {
				child.init()          // 자식 태그 초기화
				children.add(child)   // 자식 태그에 대한 참조 저장
		}
		
		override fun toString() =
				"<$name>${children.joinToString("")}<$name>" // 결과 HTML을 문자로 반환
				
		// ...
}

class TABLE: Tag("table") {
		fun tr(init: TR.() -> Unit) = doInit(TR(), init)) // 태그 인스턴스를 새로 만들고 초기화 -> 상위 태그의 자식으로 등록
}
```

- 모든 태그에는 내포 태그를 저장하는 리스트가 있음
- toString을 통해 자신을 적절히 문자열로 렌더링함
    - 자기 이름을 태그 안에 넣고 모든 자식을 재귀적으로 문자열로 렌더링

## 13.2.3 코틀린 빌더: 추상화와 재사용을 가능하게 해준다

코틀린 내부 DSL → 반복되는 내부 DSL 코드 조각을 새 함수로 묶어 재사용할 수 있다.

- 리스트에 항목을 추가하는 것을 태그가 아닌 별도 함수로 구현할 수 있다.

# 13.3 invoke 관례를 이용해 더 유연하게 블록 내포시키기

invoke 관례 사용 → 어떤 커스텀 타입의 객체를 함수처럼 호출할 수 있다.

## 13.3.1 invoke 관례를 이용해 더 유연하게 블록 내포시키기

관례 : 특별한 이름의 함수를 일반 함수처럼 호출하지 않고 더 간결한 표기로 호출

operator 변경자가 붙은 + invoke 메서드 정의가 들어있는 + 클래스의 객체를 → 함수처럼 호출 가능

시그니처에 대한 요구사항 없음 → 원하는대로 파라미터 지정 가능

모든 람다 → 함수형 인터페이스를 구현하는 클래스로 컴파일

각 함수형 인터페이스 안에는 인터페이스 이름이 가리키는 수만큼의 파라미터를 받는 invoke 메소드가 있다.

```kotlin
interface Function2<in P1, in P2, out R> {
		operator fun invoke(p1: P1, p2: P2): R
}
```

## 13.3.2 DSL의 invoke 관례: 그레이들 의존관계 선언

DSL의 장점 → 같은 맥락을 매 함수 호출 시마다 반복하지 않고도 재사용할 수 있음

```kotlin
dependencies {
		testImplementation(kotlin("test"))
		implementation("org.jetbrains.exposed:exposed-core:0.40.1")
}
```

```kotlin
dependencies.testImplementation(kotlin("test"))
dependencies.implementation("org.jetbrains.exposed:exposed-core:0.40.1")
```

- dependencies는 DependencyHandler클래스의 인스턴스
- DependencyHandler는 compile과 invoke 메서드 정의가 들어있음
- invoke는 수신 객체 지정 람다를 파라미터로 받음 → DependencyHandler

구현 형식

```kotlin
class DependencyHandler {
		fun implementation(coordinate: String) {
					println("Added dependency on $coordinate")
		}
		
		operator fun invoke(
			body: DependencyHandler.() -> Unit) {
					body()
			}
}
```

- dependencies를 함수처럼 호출하면서 람다를 인자로 넘겨 사용
- invoke 메서드는 수신 객체 지정 람다를 호출
- invoke가 DependencyHandler의 메서드이므로 그 안에서 암시적 수신 객체 this는 DependencyHandler 객체

# 13.4 실전 코틀린 DSL

## 13.4.1 중위 호출 연쇄시키기: 테스트 프레임워크의 should 함수

DSL의 특징 → 깔끔한 구문

깔끔하게 만드려면? → 코드에 쓰이는 기호의 수를 줄여야 함

- 메서드 호출을 연쇄시키는 형태로 만들어지기 때문에 → 메서드 호출 시 발생하는 잡음을 줄여야 한다

→ 중위 호출 사용

```kotlin
infix fun <T> T.should(matcher: Matcher<T>) = matcher.test(this)

s should startWith("K") // 이렇게 사용할 수 있다.
```

## 13.4.2 원시 타입에 대해 확장 함수 정의하기: 날짜 처리

```kotlin
val Int.days: Duration
		get() = this.toDuration(DurationUnit.DAYS)
```

- 코틀린의 확장 함수는 제한이 없다 → 원시 타입에도 확장 함수 지정이 가능

## 13.4.3 멤버 확장 함수

멤버 확장

- 클래스의 멤버인 동시에 그들이 확장하는 다른 타입의 멤버
- 멤버 확장으로 정의하면 메서드가 적용되는 범위/맥락을 제한할 수 있음
- 어떤 클래스에 속해 있기 때문에 기존 클래스의 소스코드를 손대지 않고 새로운 멤버 확장을 추가할 수는 없음 → 확장성이 떨어진다는 단점

콘텍스트 수신 객체

- 함수에 하나 이상의 수신 객체 타입을 지정할 수 있음
- 현재 기준 정식 기능이 아니라고 함 [https://kotlinlang.org/docs/whatsnew2020.html#language](https://kotlinlang.org/docs/whatsnew2020.html#language)