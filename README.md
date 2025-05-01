
## API 문서 및 배포 정보

- **Swagger 문서**: [https://uhyun.shop/swagger-ui/index.html](https://uhyun.shop/swagger-ui/index.html)
- **실서버 배포 주소**: [https://uhyun.shop](https://uhyun.shop)
- **JWT 인증 필요**: 로그인 후 발급받은 토큰을 Swagger 우측 상단 Authorize 버튼으로 입력

---

## 서비스 흐름 요약

- 회원 로그인 시 JWT 토큰을 발급받아 이후 모든 API 요청에 인증 헤더 사용
- 상품 등록 → 옵션 등록 → 옵션값 등록 순으로 흐름 구성
- 상품 상세 조회 시 옵션 리스트까지 함께 반환 (옵션값 제외)
- 옵션 단건 조회 시 해당 옵션의 옵션값 리스트 포함
- 삭제는 모두 soft delete (deletedAt 사용)

---

## 추가 고려 사항

- 옵션 타입(INPUT/SELECT)에 따라 유효성 분기 처리
- 옵션값을 별도 테이블로 분리하여 재고/추가금 필드 관리
- 조회 API는 실제 사용처 기준으로 응답 DTO를 구성함. → 필요한 정보만 반환하도록 설계하여 불필요한 연관 데이터 로딩 방지
- Cloud 서버 배포에 있어 도메인, HTTPS 적용, GitHub Actions 자동 배포 포함
- 상품 삭제 및 옵션 삭제 시, 연관된 옵션/옵션값 데이터에 대해
  `QueryDSL 기반 Bulk Update`를 적용하여 deletedAt을 일괄 업데이트.
  → 성능 최적화를 고려하여 반복 루프 대신 단일 쿼리 처리 방식 채택

---

## 📦 코드 구조

- com.productmanagement
  - common
    - response: 공통 응답 객체 (ApiResponse 등)
    - exception: 공통 예외 처리 (CustomException, ErrorCode 등)
    - entity: 공통 엔티티 (BaseTimeEntity 등)
    - security: JWT 인증 관련 구성 요소
  - config
    - Spring Security, Swagger, QueryDSL 설정
  - domain
    - member
      - controller: 회원 API (로그인, 회원가입)
      - service: 회원 관련 비즈니스 로직
      - repository: 회원 JPA
      - dto: 로그인/회원가입 요청 및 응답 DTO
      - entity: Member 엔티티
    - product
      - controller: 상품 API
      - service: 상품 CRUD 로직
      - repository: 상품 JPA + QueryDSL
      - dto: 상품 관련 요청/응답 DTO
      - entity: Product 엔티티
    - productoption
      - controller: 상품 옵션 API
      - service: 상품 옵션 로직
      - repository: 옵션 JPA + QueryDSL
      - dto: 옵션 관련 DTO
      - entity: ProductOption 엔티티
    - productoptionvalue
      - controller: 옵션값 API
      - service: 옵션값 관련 로직
      - repository: 옵션값 JPA + QueryDSL
      - dto: 옵션값 DTO
      - entity: ProductOptionValue 엔티티
