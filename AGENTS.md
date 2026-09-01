# Agent Instructions

- **Language Policy**: The project UI must be in Spanish, but all code, comments, and communication with the user MUST be in English.
- **Build & Test**: Use `./gradlew` (or `gradlew.bat` on Windows) for building. Ensure JDK 17+ is set. Verification flow: `./gradlew lint` -> `./gradlew test`.
- **Project Scope & Domain Rules (TRD-driven)**:
    - **Domain**: Android native application for municipal companion animal characterization (Secretaría de Desarrollo Rural y Ambiente, Zipaquirá).
    - **Terminology**: Always use "animales de compañía" (companion animals), NEVER "mascotas".
    - **Tech Stack**: Pure Android native (Kotlin, Android Studio, Gradle). No cross-platform frameworks (Flutter, React Native).
    - **Territory / Location**: Must use controlled catalogs for territories (14 *veredas* + urban *barrios*), NEVER free text for territory fields.
    - **Offline-First & Sync**: Must handle offline scenarios with store-and-forward sync windows, local versioning (`version_local`), and sync status flags (`sincronizado`).
    - **Key Priorities**: 
        1. Animal & Guardian Registration (sub-3 min flow, tattoo checks, manual-only deduplication and merge).
        2. Lost & Found (structured alerts, mandatory manual validation for matches, 20-day stray-to-abandoned rule).
        3. Clinical History & Mortality Events (persistent clinical logs, first-class mortality/death recording closing open cases/commitments).
        4. Mistreatment Reports & Case Management (anonymous / reserved-identity privacy modes, *actas de visita*, severity classification, corrective commitments).
        5. Apprehension & Adoption (police coordination, extended custody rules for apprehensions, adoption workflows).
- **Project Structure**:
    - `app/`: Main Android application code.
    - `no_git/`: Holds design prototypes, mock data, and additional project info; not part of the Android build.
- **Entry Points**: 
    - `MainActivity` is the main entry point defined in `AndroidManifest.xml`.
