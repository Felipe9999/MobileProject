# Agent Instructions

- **Language Policy**: The project UI must be in Spanish, but all code, comments, and communication with the user MUST be in English.
- **Build & Test**: Use `./gradlew` for building. Ensure a compatible JDK (17+) is set in your environment. Always follow `lint` -> `typecheck` -> `test` when applicable.
- **Project Scope & Domain Rules**:
    - **Domain**: Android native application for municipal companion animal characterization (Secretaría de Desarrollo Rural y Ambiente).
    - **Terminology**: Always use "animales de compañía" (companion animals), NEVER "mascotas".
    - **Tech Stack**: Pure Android native (Kotlin, Android Studio, Gradle). No cross-platform frameworks (Flutter, React Native).
    - **Territory / Location**: Must use controlled catalogs for territories (veredas/barrios), NEVER free text for territory fields.
    - **Offline-first**: The app must handle offline scenarios (offline sync, local versioning) given connectivity limitations in rural zones.
- **Project Structure**:
    - `app/`: Main Android application code.
    - `no_git/`: Holds design prototypes, mock data, and additional project info; not part of the Android build.
- **Entry Points**: 
    - `MainActivity` is the main entry point defined in `AndroidManifest.xml`.
