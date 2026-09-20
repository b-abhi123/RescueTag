# RescueTag

> **Offline-first LoRaWAN emergency distress beaconing and incident command platform for flood and landslide-prone regions.**

When conventional cellular towers, electricity, and internet infrastructure fail during extreme weather events (monsoons, flash floods, landslides), **RescueTag** enables resilient emergency signaling, community volunteer mobilization, and tactical incident command over simulated low-power mesh networks.

---

## Core Features

### 1. One-Tap Distress Signaling
- **Tactile Tag Simulation**: Simulated hardware distress tag with tactile trigger buttons (SOS, Medical, Supplies, Safe).
- **Offline Packet Telemetry**: Generates compact LoRaWAN payloads containing `devEui`, GPS coordinates, Plus Codes, elevation, battery status, and timestamp.
- **Triage Escalation**: Multi-tier priority tagging (**P1 Critical SOS**, **P2 Medical Urgent**, **P3 Essential Supplies**, **P4 Safe Confirmation**).

### 2. Incident Command & Tactical Map (Admin)
- **Live Operations Map**: Real-time canvas projection showing sector distress beacons and LoRa gateway coverage radiuses.
- **Triage Dispatch Queue**: Filterable signal roster with single-click coordinate copy, status resolution, and tactical dossier viewing.
- **Gateway Health Telemetry**: Live status monitoring for local relay nodes and base stations.

### 3. Community Volunteer Network
- **Household Welfare Checks**: Volunteer neighborhood rosters with checklist verification.
- **Search & Proximity Radar**: Visual sweep radar for locating active household beacons in dense or low-visibility terrain.
- **Direct Emergency Routing**: One-tap tel URI dispatcher for emergency helplines (`112`) and designated local responders.

### 4. Multilingual & Resilient UX
- **Multi-Language Support**: Complete interface localization in **English**, **Nepali (नेपाली)**, and **Hindi (हिंदी)**.
- **High-Contrast Tactical Design**: Dark canvas mode engineered for low-light rescue operations and battery conservation.
- **Pre-Season Preparedness**: Community drill modes, readiness checklists, and tag health diagnostics.

---

## Architecture & Tech Stack

| Layer | Technologies |
|---|---|
| **UI Framework** | Jetpack Compose / Compose Multiplatform |
| **Design System** | Material Design 3 (Adaptive Light & Dark schemes) |
| **Language** | Kotlin 2.x (Coroutines, StateFlow) |
| **State Management** | Centralized reactive repository (`RescueTagRepository`) |
| **Networking Spec** | Simulated LoRaWAN telemetry packets (EU868 / AS923) |

---

## Getting Started

### Prerequisites
- JDK 17 or higher
- Android Studio / IntelliJ IDEA (with Kotlin Multiplatform plugin)

### Running on Desktop (macOS / Windows / Linux)
```bash
./gradlew :desktopApp:run
```

### Running on Android
```bash
./gradlew :app:installDebug
```

---

## 📂 Project Structure

```
├── commonMain/
│   ├── admin/             # Tactical Map & Incident Command Dashboard
│   ├── components/        # Radar view, distress dialogs, tag simulator
│   ├── public/            # SOS screen, volunteer hub, tracking & drills
│   ├── theme/             # Material 3 Color palettes & Typography
│   ├── Localization.kt    # English, Nepali, Hindi dictionary strings
│   ├── Models.kt          # Distress signals, gateway nodes, and profile data
│   └── RescueTagRepository.kt # Reactive simulation & state manager
```

---

