# MerchPulse - Android Merchant Management Application

MerchPulse is a production-ready Android application scaffold built with KMP, Jetpack Compose, and Clean Architecture. It is designed for electronic merchants to manage inventory, track employee attendance, and enforce role-based access control.

## Architecture

The project follows **Clean Architecture** principles and **MVI (Model-View-Intent)** presentation pattern.

### Module Structure
- `:app-android`: Main Android entry point, Navigation, and App container.
- `:shared`: Kotlin Multiplatform module containing domain models, generic MVI contracts, and platform-agnostic logic.
- `:core:common`: Shared utilities, Resource wrappers, and Dispatcher abstractions.
- `:core:designsystem`: Reusable Compose components and theme tokens.
- `:core:database-android`: Room database implementation, entities, and DAOs.
- `:feature:auth`: Authentication, Session Management, and Employee Profile.
- `:feature:products`: Product management, stock tracking, and inventory views.
- `:feature:punching`: Employee time punching (Attendance).

### Tech Stack
- **Language**: Kotlin 1.9.23
- **UI**: Jetpack Compose
- **DI**: Koin 3.5.3
- **Local DB**: Room (Android)
- **Concurrency**: Coroutines + Flow
- **Architecture**: MVI + Repository Pattern
- **Date/Time**: kotlinx-datetime

## Features

### 1. Authentication & RBAC
- Local-first sign-in/sign-up.
- Permissions enforced at the domain layer (`AuthorizationPolicy`).
- Default role: `STAFF`. Higher roles (`ADMIN`, `MANAGER`) assigned by authorized users.

### 2. Inventory Management
- Full CRUD for products (Permission: `PRODUCT_CREATE`, `PRODUCT_EDIT`).
- Low-stock indicator driven by `lowStockThreshold`.
- Soft-delete capability.

### 3. Attendance Tracking
- Simple PUNCH IN/OUT system.
- Daily summary showing the first punch in and the last punch out.
- Automatic sequence validation (prevents double entry of same type).

## Getting Started

1. **Prerequisites**: Android Studio Hedgehog or newer.
2. **Setup**: Run `./gradlew build` to verify the scaffold.
3. **Run**: Deploy `:app-android` to a device or emulator.
4. **Seeding**: The `DataSeeder` class in `:core:database-android` provides an initial admin account (`admin@merchpulse.com` / `1234`).

## Quality Standards
- **Clean Code**: Adheres to SOLID principles.
- **Offline-First**: Room serves as the single source of truth.
- **Scalability**: Feature-modularized Gradle structure allows independent development.