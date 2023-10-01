# EmojiHub

[Short application description here]

![Application Screenshot](path_to_screenshot.png)

## Features

- Feature 1: Brief description
- Feature 2: Brief description
- ...

## Getting Started

### Prerequisites

- Android Studio [version, e.g., 4.2.1]
- Minimum Android SDK Version [e.g., 21]

### Installation

[Installation link here]

- - -
### Git Conventions

- 크게 `main`, `release`, `develop` 세 가지의 브랜치를 관리합니다.
- 모든 commit은 직접 push하지 않습니다. merge 전 반드시 **Pull Request + at least 1 Code Review**를 거칩니다.
- `develop`: Unit test의 대상이 되는 feature level 코드를 위한 브랜치. 모든 작업은 이곳에 merge하는 것을 기본으로 합니다.
    - Branch naming convention examples
    - `feat/user-authentification`
    - `fix/bottom-sheet-animation`
- `release`: Iteration 단계에서 integration test 대상이 되는 코드를 위한 브랜치.
- `main`: Integration test를 통과한 배포 상태의 코드를 위한 브랜치.
