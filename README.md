# Cinema Booking (SOA)

React + API Gateway + 4 Spring Boot service (database per service).

## Cổng
Gateway 8080 | Auth 8081 | Movie 8082 | Showtime 8083 | Booking 8084 | Frontend 5173

## Cách chạy
1. Chạy `db/init.sql` trong MySQL Workbench.
2. Mở project bằng IntelliJ (JDK 21), Reload Maven.
3. Chạy lần lượt: AuthServiceApplication, MovieServiceApplication,
   ShowtimeServiceApplication, BookingServiceApplication, GatewayApplication.
4. Frontend: `cd frontend && npm install && npm run dev`, mở http://localhost:5173
5. Tài khoản mặc định: admin / admin123

## Phân công
- TV1: Auth, Gateway
- TV2: Movie, Showtime
- TV3: Booking, Frontend