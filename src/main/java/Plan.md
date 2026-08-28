scyang.mutilboard
├── global
│    ├── security (SecurityConfig, PasswordEncoder 등 공통 보안 설정)
│    └── jwt      (JwtTokenProvider, JwtFilter 등 JWT 관련)
│
└── domain
├── member   (의존성 방향: 화살표를 받기만 함)
│    ├── domain     (Member 엔티티, Role Enum)
│    ├── repository (MemberRepository)
│    ├── dto        (MemberRequest.Update 등)
│    ├── service    (MemberService - 정보수정, 탈퇴)
│    └── controller (MemberController)
│
└── auth     (의존성 방향: member를 참조함 ➔)
├── dto        (AuthRequest.SignUp, Login 등)
├── service    (AuthService - 가입, 로그인, 비번재발급)
└── controller (AuthController)