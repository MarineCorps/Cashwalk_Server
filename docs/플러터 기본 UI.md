# 📘 Flutter UI 코드 구조 해석 가이드

Flutter는 선언형(declarative) UI 프레임워크로, 코드를 보면 바로 **화면 구조와 구성 요소**가 머릿속에 떠올라야 합니다. 실무에서는 "이 코드는 어떤 화면을 구성하는가?"를 빠르게 파악하는 능력이 중요합니다.

이 문서는 Flutter UI 코드를 **직관적으로 이해하는 방법**을 설명하고, 대표 예제들과 함께 시각화하는 연습을 돕습니다.

---

## ✅ 기본 원칙: Flutter는 위젯 트리

Flutter는 모든 UI 요소를 "위젯"으로 구성합니다. 이 위젯들은 중첩된 트리(Tree) 형태로 화면을 구성합니다.

### 예시 1: 기본 Column 구조
```dart
Column(
  children: [
    Text("제목"),
    Text("내용"),
  ],
)
```

**해석**: 수직 방향으로 텍스트 2개가 나란히 있는 구조입니다.
- Column: 수직 레이아웃
- Text("제목"): 위에 있는 텍스트
- Text("내용"): 아래에 있는 텍스트

### 예시 2: Column + Container
```dart
Column(
  children: [
    Text("제목"),
    Container(
      padding: EdgeInsets.all(8),
      color: Colors.grey[300],
      child: Text("컨테이너 내부"),
    ),
  ],
)
```

**해석**:
- 텍스트 "제목"
- 그 밑에 회색 배경 + 여백을 가진 컨테이너 안에 텍스트

---

## ✅ 위젯 종류 구분: 레이아웃 vs 콘텐츠

| 분류       | 대표 위젯                              | 설명 |
|------------|------------------------------------------|------|
| 📐 레이아웃 | `Column`, `Row`, `Stack`, `Padding`      | 구조를 만드는 위젯 |
| 🧩 콘텐츠   | `Text`, `Image`, `Icon`, `ListView`     | 실제로 보이는 요소 |

### 실전 예시 3: 레이아웃 + 콘텐츠 결합
```dart
Column(
  children: [
    Padding(
      padding: const EdgeInsets.all(16),
      child: Text("오늘의 한 줄", style: TextStyle(fontSize: 20)),
    ),
    Image.asset('assets/sun.png'),
  ],
)
```

**해석**:
- Column: 전체를 수직 배치
- Padding: 텍스트에 여백을 줌
- Text: 상단에 큰 글씨
- Image.asset: 그 아래에 이미지 배치

---

## ✅ 복잡한 UI 구조 해석 예제

### 예시 4: 걸음 수 UI (Circular Percent 포함)
```dart
Center(
  child: Column(
    mainAxisAlignment: MainAxisAlignment.center,
    children: [
      Text("4월 18일"),
      Text("10:56"),
      CircularPercentIndicator(
        percent: 0.3,
        center: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text("하루 만보 걷기"),
            Text("2915"),
            Text("걸음"),
          ],
        ),
      ),
      Text("97kcal | 2.4km"),
    ],
  ),
)
```

**해석 요령**:
1. 가장 바깥은 `Center` → 중앙 정렬
2. 내부는 `Column` → 세로 정렬
3. 텍스트 → 날짜, 시간
4. `CircularPercentIndicator` → 원형 그래프 중앙에 또 Column → 걸음수 텍스트
5. 마지막 줄: 칼로리 & 거리 표시

---

## ✅ 자주 등장하는 Flutter 문법/속성 용어 정리

| 용어 | 의미 | 예시 | 설명 |
|------|------|------|------|
| `const` | 상수 위젯 선언 | `const Text("Hello")` | 변하지 않는 위젯에 붙여 성능 최적화 가능 |
| `child` | 하나의 자식 위젯 | `Container(child: Text("Hi"))` | 하나만 포함할 때 사용 |
| `children` | 복수의 자식 위젯 | `Column(children: [...])` | 여러 위젯을 넣을 때 사용 (List 형태) |
| `Widget` | 모든 UI 구성 요소 | `Text`, `Image`, `Container` 등 | Flutter의 모든 UI는 위젯이다 |
| `StatelessWidget` | 상태 없는 위젯 | `class A extends StatelessWidget` | 값 변경 없음, 고정된 화면 |
| `StatefulWidget` | 상태 있는 위젯 | `class A extends StatefulWidget` | 값이 바뀌면 화면도 갱신 가능 |
| `setState()` | 상태 업데이트 + UI 다시 그림 | `setState(() { ... })` | 값 바꾸면 UI 즉시 반영됨 |
| `build()` | UI를 그리는 함수 | `Widget build(BuildContext context)` | 화면을 구성하는 핵심 메서드 |
| `context` | 현재 위젯의 위치 정보 | `Navigator.of(context).push(...)` | 화면 이동, 테마 접근 등 가능 |
| `Expanded` | 남는 공간을 차지 | `Row(children: [Expanded(child: ...)])` | 비율로 공간 나눌 때 사용 |
| `SizedBox` | 고정 크기/공백 위젯 | `SizedBox(height: 20)` | 간격 넣을 때 자주 사용 |
| `Padding` | 바깥 여백 설정 | `Padding(padding: ..., child: ...)` | 여백으로 UI 정리할 때 사용 |

---

## ✅ StepDisplayWidget 전체 주석 예시 (걸음 수 위젯)
```dart
class StepDisplayWidget extends StatefulWidget {
  const StepDisplayWidget({super.key});

  @override
  State<StepDisplayWidget> createState() => _StepDisplayWidgetState();
}

class _StepDisplayWidgetState extends State<StepDisplayWidget> {
  final StepService _stepService = StepService(); // 걸음 수 측정 서비스 인스턴스
  int _currentSteps = 0; // 현재 걸음 수 저장 변수

  static const int stepGoal = 10000; // 목표 걸음 수 (10000보)

  @override
  void initState() {
    super.initState();
    _stepService.init(); // 센서 스트림 초기화
    _stepService.stepStream.listen((steps) {
      setState(() {
        _currentSteps = steps; // 걸음 수 업데이트 시 화면 갱신
      });
    });
  }

  @override
  void dispose() {
    _stepService.dispose(); // 스트림 해제 (메모리 누수 방지)
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    double progress = (_currentSteps / stepGoal).clamp(0.0, 1.0); // 퍼센트 계산
    double distanceKm = _currentSteps * 0.0008; // 거리 추정 (평균 0.8m)
    double calories = _currentSteps * 0.033; // 칼로리 추정

    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text("걸음 수", style: TextStyle(fontSize: 18)),

          CircularPercentIndicator(
            radius: 100,
            lineWidth: 12,
            percent: progress,
            center: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text("$_currentSteps", style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
                Text("걸음")
              ],
            ),
            progressColor: Colors.orange,
          ),

          SizedBox(height: 16),
          Text("${calories.toStringAsFixed(1)} kcal  |  ${distanceKm.toStringAsFixed(2)} km",
              style: TextStyle(color: Colors.black54)),
        ],
      ),
    );
  }
}
```

---

## ✅ 마무리

Flutter UI를 잘 이해하기 위해선 **트리 구조에 익숙해지고, 레이아웃 위젯과 콘텐츠 위젯을 구분하는 습관**이 가장 중요합니다. 코드를 읽는 순간, "이건 이런 모양의 화면이겠구나"가 떠오르도록 반복 훈련하세요.

원한다면 다음엔 위젯 구조 해석 퀴즈나 훈련용 예제도 만들어줄 수 있습니다 💪

# 📘 Flutter UI 코드 구조 해석 가이드

Flutter는 선언형(declarative) UI 프레임워크로, 코드를 보면 바로 **화면 구조와 구성 요소**가 머릿속에 떠올라야 합니다. 실무에서는 "이 코드는 어떤 화면을 구성하는가?"를 빠르게 파악하는 능력이 중요합니다.

이 문서는 Flutter UI 코드를 **직관적으로 이해하는 방법**을 설명하고, 대표 예제들과 함께 시각화하는 연습을 돕습니다.

---

## ✅ 기본 원칙: Flutter는 위젯 트리

Flutter는 모든 UI 요소를 "위젯"으로 구성합니다. 이 위젯들은 중첩된 트리(Tree) 형태로 화면을 구성합니다.

### 예시 1: 기본 Column 구조
```dart
Column(
  children: [
    Text("제목"),
    Text("내용"),
  ],
)
```

**해석**: 수직 방향으로 텍스트 2개가 나란히 있는 구조입니다.
- Column: 수직 레이아웃
- Text("제목"): 위에 있는 텍스트
- Text("내용"): 아래에 있는 텍스트

### 예시 2: Column + Container
```dart
Column(
  children: [
    Text("제목"),
    Container(
      padding: EdgeInsets.all(8),
      color: Colors.grey[300],
      child: Text("컨테이너 내부"),
    ),
  ],
)
```

**해석**:
- 텍스트 "제목"
- 그 밑에 회색 배경 + 여백을 가진 컨테이너 안에 텍스트

---

## ✅ 위젯 종류 구분: 레이아웃 vs 콘텐츠

| 분류       | 대표 위젯                              | 설명 |
|------------|------------------------------------------|------|
| 📐 레이아웃 | `Column`, `Row`, `Stack`, `Padding`      | 구조를 만드는 위젯 |
| 🧩 콘텐츠   | `Text`, `Image`, `Icon`, `ListView`     | 실제로 보이는 요소 |

### 실전 예시 3: 레이아웃 + 콘텐츠 결합
```dart
Column(
  children: [
    Padding(
      padding: const EdgeInsets.all(16),
      child: Text("오늘의 한 줄", style: TextStyle(fontSize: 20)),
    ),
    Image.asset('assets/sun.png'),
  ],
)
```

**해석**:
- Column: 전체를 수직 배치
- Padding: 텍스트에 여백을 줌
- Text: 상단에 큰 글씨
- Image.asset: 그 아래에 이미지 배치

---

## ✅ 복잡한 UI 구조 해석 예제

### 예시 4: 걸음 수 UI (Circular Percent 포함)
```dart
Center(
  child: Column(
    mainAxisAlignment: MainAxisAlignment.center,
    children: [
      Text("4월 18일"),
      Text("10:56"),
      CircularPercentIndicator(
        percent: 0.3,
        center: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text("하루 만보 걷기"),
            Text("2915"),
            Text("걸음"),
          ],
        ),
      ),
      Text("97kcal | 2.4km"),
    ],
  ),
)
```

**해석 요령**:
1. 가장 바깥은 `Center` → 중앙 정렬
2. 내부는 `Column` → 세로 정렬
3. 텍스트 → 날짜, 시간
4. `CircularPercentIndicator` → 원형 그래프 중앙에 또 Column → 걸음수 텍스트
5. 마지막 줄: 칼로리 & 거리 표시

---

## ✅ 자주 등장하는 Flutter 문법/속성 용어 정리

| 용어 | 의미 | 예시 | 설명 |
|------|------|------|------|
| `const` | 상수 위젯 선언 | `const Text("Hello")` | 변하지 않는 위젯에 붙여 성능 최적화 가능 |
| `child` | 하나의 자식 위젯 | `Container(child: Text("Hi"))` | 하나만 포함할 때 사용 |
| `children` | 복수의 자식 위젯 | `Column(children: [...])` | 여러 위젯을 넣을 때 사용 (List 형태) |
| `Widget` | 모든 UI 구성 요소 | `Text`, `Image`, `Container` 등 | Flutter의 모든 UI는 위젯이다 |
| `StatelessWidget` | 상태 없는 위젯 | `class A extends StatelessWidget` | 값 변경 없음, 고정된 화면 |
| `StatefulWidget` | 상태 있는 위젯 | `class A extends StatefulWidget` | 값이 바뀌면 화면도 갱신 가능 |
| `setState()` | 상태 업데이트 + UI 다시 그림 | `setState(() { ... })` | 값 바꾸면 UI 즉시 반영됨 |
| `build()` | UI를 그리는 함수 | `Widget build(BuildContext context)` | 화면을 구성하는 핵심 메서드 |
| `context` | 현재 위젯의 위치 정보 | `Navigator.of(context).push(...)` | 화면 이동, 테마 접근 등 가능 |
| `Expanded` | 남는 공간을 차지 | `Row(children: [Expanded(child: ...)])` | 비율로 공간 나눌 때 사용 |
| `SizedBox` | 고정 크기/공백 위젯 | `SizedBox(height: 20)` | 간격 넣을 때 자주 사용 |
| `Padding` | 바깥 여백 설정 | `Padding(padding: ..., child: ...)` | 여백으로 UI 정리할 때 사용 |

---

## ✅ StepDisplayWidget 전체 주석 예시 (걸음 수 위젯)
```dart
class StepDisplayWidget extends StatefulWidget {
  const StepDisplayWidget({super.key});

  @override
  State<StepDisplayWidget> createState() => _StepDisplayWidgetState();
}

class _StepDisplayWidgetState extends State<StepDisplayWidget> {
  final StepService _stepService = StepService(); // 걸음 수 측정 서비스 인스턴스
  int _currentSteps = 0; // 현재 걸음 수 저장 변수

  static const int stepGoal = 10000; // 목표 걸음 수 (10000보)

  @override
  void initState() {
    super.initState();
    _stepService.init(); // 센서 스트림 초기화
    _stepService.stepStream.listen((steps) {
      setState(() {
        _currentSteps = steps; // 걸음 수 업데이트 시 화면 갱신
      });
    });
  }

  @override
  void dispose() {
    _stepService.dispose(); // 스트림 해제 (메모리 누수 방지)
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    double progress = (_currentSteps / stepGoal).clamp(0.0, 1.0); // 퍼센트 계산
    double distanceKm = _currentSteps * 0.0008; // 거리 추정 (평균 0.8m)
    double calories = _currentSteps * 0.033; // 칼로리 추정

    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text("걸음 수", style: TextStyle(fontSize: 18)),

          CircularPercentIndicator(
            radius: 100,
            lineWidth: 12,
            percent: progress,
            center: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text("$_currentSteps", style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
                Text("걸음")
              ],
            ),
            progressColor: Colors.orange,
          ),

          SizedBox(height: 16),
          Text("${calories.toStringAsFixed(1)} kcal  |  ${distanceKm.toStringAsFixed(2)} km",
              style: TextStyle(color: Colors.black54)),
        ],
      ),
    );
  }
}
```

---

## ✅ 마무리

Flutter UI를 잘 이해하기 위해선 **트리 구조에 익숙해지고, 레이아웃 위젯과 콘텐츠 위젯을 구분하는 습관**이 가장 중요합니다. 코드를 읽는 순간, "이건 이런 모양의 화면이겠구나"가 떠오르도록 반복 훈련하세요.

원한다면 다음엔 위젯 구조 해석 퀴즈나 훈련용 예제도 만들어줄 수 있습니다 💪

