# Technical Requirements Document (TRD)
## Android Application for Companion Animal Welfare Management — Municipality of Zipaquirá

**Document type:** Technical Requirements Document
**Prepared from:** Stakeholder meeting (Secretaría de Desarrollo Rural y Ambiente, Zipaquirá — Área de Protección y Bienestar Animal), meeting transcript, meeting summary, assigned project brief, and suggested data dictionary.
**Status:** Draft v1.0
**Language of source material:** Spanish (interviews conducted in Spanish; this TRD is written in English per project requirement)

---

## 1. Document Control

| Field | Value |
|---|---|
| Client / Sponsor | Alcaldía de Zipaquirá — Secretaría de Desarrollo Rural y Ambiente, Área de Protección y Bienestar Animal |
| Key informants | Secretaría representative (program lead); Dra. Valentina (veterinarian, Animal Welfare team) |
| Project type | Academic pilot / proof-of-concept mobile application |
| Target platform | Android (native) |
| Source documents | Meeting summary (PDF), verbatim meeting transcript, assigned scenario brief ("Información general"), suggested field dictionary (data.md) |

---

## 2. Purpose and Scope

### 2.1 Purpose
This TRD defines the technical and functional requirements for a native Android mobile application that supports the Municipality of Zipaquirá's Animal Welfare Program (*Protección y Bienestar Animal*) in registering, tracking, and managing information about companion animals, their guardians, and the welfare events (veterinary care, sterilization, mistreatment cases, lost/found reports, custody, and adoption) associated with them.

### 2.2 Scope
The application is an academic pilot intended to replace the current fragmented workflow (WhatsApp line + physical documents + Excel spreadsheets) with a structured, traceable digital registry. The scope covers:
- Registration of animals and their guardians/holders (owned, community, and stray animals).
- Traceability of veterinary attentions, sterilizations, and clinical history.
- Reporting and triage of animal welfare cases (illness, injury, emergencies, presumed mistreatment).
- Lost-and-found animal matching and alerts.
- Role-based routing of cases to the on-duty veterinarian.
- Basic statistics/indicators for institutional decision-making.

The scope explicitly **excludes**, unless otherwise noted in Section 13, back-office server infrastructure procurement, payment/e-commerce functionality, and integration with the Secretaría de Salud's separate departmental information system.

### 2.3 Intended Use
This is a **student/academic deliverable**. Per the stakeholder's own framing, the application does not need to solve the entirety of the institution's problem; it must operate within the scope, timeframe, and technical constraints assigned to the working group, while remaining useful as a real pilot that could inform a future production system.

---

## 3. Background and Problem Context

### 3.1 Institutional context
Zipaquirá's Secretaría de Desarrollo Rural y Ambiente manages animal protection and welfare (*Protección y Bienestar Animal*), an area that has grown significantly in recent years due to new national regulations — referenced in the meeting as the *Ley Ángel*, *Ley Kiara*, and *Ley Lorenzo* — which impose new obligations on municipalities regarding mistreatment response, veterinary care, sterilization for population control, and recordkeeping.

### 3.2 Current process and pain points
- **Intake channel:** Community reports (sick, injured, or at-risk animals; mistreatment; lost/found animals) currently arrive almost exclusively through a single WhatsApp line answered by one Secretaría staff member.
- **Bottleneck:** The staff member may be busy, may miss a message, or may be off-duty (nights, early mornings). Reports must pass through her before reaching a veterinarian; veterinarians are not given the phone directly because they cannot triage messages while treating an animal in the field.
- **Territorial complexity:** The urban area represents only ~4% of the municipality's territory; ~96% is rural, spread across 14 *veredas* (rural districts). Location descriptions are often informal ("next to Mr. Martínez's farm," "the green house"), which is difficult to interpret for anyone unfamiliar with the area.
- **Connectivity:** Each *vereda* has at least one digital access point (e.g., a school or a shop), but coverage is not continuous across the territory. Residents may need to travel 1–1.5 hours to reach a connectivity point. Some zones sit at approximately 3,300 meters altitude and have degraded or absent mobile signal for both calls and data; some residents' only regular connectivity window is a weekly market day.
- **Recordkeeping:** There is no dedicated animal welfare information system. Data collected during veterinary visits, sterilizations, and mistreatment investigations is manually transcribed into Excel spreadsheets and physical paper files. This makes it difficult to:
  - Retrieve prior clinical history for an animal.
  - Determine whether a stray animal has already been sterilized (currently relies on veterinarians' personal memory and visual recognition of tattoos/photos).
  - Cross-reference multiple attentions for the same animal or the same case of mistreatment.
  - Track compliance with corrective commitments given to guardians.
  - Produce statistics without manual compilation.
- **No dedicated server/platform:** The municipality does not currently have its own server or cloud platform for this purpose. Only the Secretaría de Salud has a departmental (not municipal) information system, used for a different purpose (public health, not animal welfare).
- **Terminology note (stakeholder request):** The stakeholder explicitly asked that the product and its documentation avoid the word *mascota* ("pet") in favor of **animal de compañía** ("companion animal"), reflecting a policy shift toward recognizing animals as sentient beings rather than objects. This TRD and the resulting application's UI copy should follow this convention.

### 3.3 Annual volume (institutional reference figures)
| Metric | Approximate value (per stakeholder) |
|---|---|
| Sterilizations performed last year | ~2,200 |
| Veterinary attentions performed last year | ~570 |
| Presumed mistreatment cases received last year | ~150–200 (stated inconsistently in the meeting as "~150–160" in the raw transcript and "~200" in the meeting summary; treat as an order-of-magnitude reference, not a hard figure) |

### 3.4 Legal/regulatory framing mentioned by stakeholders
- *Ley Ángel* — defines categories of vulnerable animals and related obligations.
- *Ley Kiara*, *Ley Lorenzo* — referenced as part of the recent regulatory wave driving demand for formal recordkeeping.
- Law 2374 (referenced regarding sterilization).
- SISBÉN categories A, B, and C are used as a prioritization criterion for free veterinary services to economically vulnerable guardians/animals; the exact ceiling category could not be confirmed by the stakeholder during the session and should be validated before implementation.
- A municipal public policy on animal welfare was pending a vote at the time of the meeting; it is expected to formally require a dedicated information system for the area, and includes a future "Guardianes de los Animales" community-volunteer initiative (out of scope for the pilot but relevant to future roadmap).

---

## 4. Assigned Scenario Parameters (Team-Specific Constraints)

Per the project brief ("Información general"), this working group was assigned a **specific scenario configuration** that must shape the design priorities of the pilot. These are binding constraints for this TRD, layered on top of the general institutional context in Section 3.

| Assigned component | Assigned condition | Design implication |
|---|---|---|
| **Territory** | Dispersed rural *vereda* (*vereda dispersa*) | The application's primary field-use context is a low-density rural area, not the urban core. UI/UX and connectivity handling must be optimized for this setting rather than for urban, high-connectivity use. |
| **Connectivity** | End of working day, after 6:00 p.m. | The application must function correctly when the user is offline during the day and only regains connectivity at day's end (evening). Design must assume **store-and-forward / offline-first operation with a deferred synchronization window**, not real-time connectivity. |
| **Priority actor** | Allied veterinarian (*veterinario aliado*) — i.e., a veterinarian who is not a fixed municipal employee but collaborates with the program in the field | The primary user persona for this scenario is a field veterinarian who is not necessarily a permanent administration employee, operating with intermittent connectivity and needing a lightweight, fast registration flow. |
| **Special situation** | Death / fatality of the animal (*Fallecimiento*) | The application must explicitly support recording an animal's death as a clinical/case outcome, including cause, date, and downstream effects on the animal's record status (e.g., closing open cases, halting active reminders/commitments, and — where relevant — supporting apprehension/custody cases where the animal does not survive). |
| **Scale** | 500 (records/animals/users, order of magnitude) | The data model, storage strategy, and sync design must be validated to comfortably support on the order of 500 records for the pilot, while being architecturally extensible beyond that figure. |
| **Additional restriction** | Territory must **not** be captured via free text | The `territorio` field (and any other geographic descriptor) must use a **controlled catalog/dropdown** (e.g., a predefined list of the municipality's *veredas*/*barrios*), consistent with the "Catálogo" type and "Valor controlado" analysis criterion already specified for this field in the base data dictionary (see Section 9). Free-text territory entry is explicitly disallowed. |

**Consequence for the TRD:** Sections 7–9 below are written to satisfy the general institutional need (Section 3) while treating the parameters in this table as **hard requirements** that take precedence wherever they narrow or override a general requirement (e.g., offline-first sync design, controlled-catalog territory, and mortality event handling are mandatory, not optional).

---

## 5. Stakeholders and User Roles

| Role | Description | Key needs |
|---|---|---|
| **Community member / citizen** | Any resident who wants to report an animal (sick, injured, lost, found, or mistreated), request an appointment, or register an animal. | Fast reporting (target: under 3 minutes for a basic registration), minimal data entry, works with poor connectivity, can attach photo/video/audio, can request anonymity/reserved identity. |
| **Allied veterinarian (priority actor for this scenario)** | Field veterinarian (may not be a permanent employee) who receives assigned cases, performs attentions, sterilizations, and mistreatment assessments, and logs clinical/legal findings. | Receives only relevant case data (not a shared inbox), can triage by severity, can work offline in the field and sync at day's end, can access an animal's prior history, can register outcomes including death. |
| **On-duty veterinarian coordination (*veterinario de turno*)** | Whichever veterinarian is currently responsible for incoming case assignment. | Needs a routing mechanism so new cases reach the correct/available professional without a manual intermediary. |
| **Secretaría staff / case intake (current WhatsApp operator)** | Administrative staff currently acting as a single point of contact. | The app should reduce (not necessarily eliminate) this bottleneck by allowing structured, direct routing of cases. |
| **Program lead / supervisor ("jefe")** | Oversees the welfare program, needs visibility into case assignment and status. | Dashboard/overview of open/closed cases, assignment by veterinarian, statistics. |
| **Animal guardian / holder (*tenedor* or *propietario*)** | The person responsible (owner, holder, or caretaker) for a specific animal, including for community/stray animals where applicable. | Registers and updates animal information; may be the subject of a mistreatment case; may request adoption. |
| **Administration / statistics consumer** | Municipal staff who need aggregate indicators for mandatory reporting and planning. | Read access to statistics; no need for personally identifiable data in most cases. |

---

## 6. Glossary

| Term | Definition |
|---|---|
| **Animal de compañía** | "Companion animal" — the required term for any domestic animal kept for companionship (not limited to dogs and cats; may include rabbits, hamsters, horses, etc.). The term **"mascota"** ("pet") must be avoided in the product, per explicit stakeholder guidance, because it implies instrumentalization of a sentient being. |
| **Animal comunitario** | "Community animal" — an animal with no single registered guardian, cared for informally by a neighborhood or group of residents. |
| **Animal de calle** | Stray animal with no known guardian. |
| **Tenedor** | Holder/caretaker of an animal who is not necessarily its legal owner (*propietario*). |
| **Vereda** | Rural district/subdivision of the municipality (Zipaquirá has 14). |
| **Triage** | Field/photo-video-based severity assessment used by the veterinarian to prioritize which case to attend first when multiple reports arrive simultaneously. |
| **Acta de visita** | Formal visit record produced during a mistreatment investigation, capturing who received the visit, their relationship to the animal, and conditions found. |
| **Tenencia irresponsable** | "Irresponsible ownership" — a non-mistreatment classification (e.g., not walking the animal on a leash, poor sanitation) resolved through educational/awareness actions rather than sanctions. |
| **Aprehensión** | Seizure of an animal at vital risk, executed in partnership with the National Police (the municipality cannot legally seize an animal unilaterally). |
| **SISBÉN** | National household vulnerability classification system (categories A/B/C referenced as a prioritization criterion for free services). |

---

## 7. Functional Requirements

Requirements are grouped by the three institutionally-ranked priorities, followed by supporting modules. Each requirement includes an ID for traceability. Priority reflects the stakeholder's explicit ranking; the assigned scenario constraints from Section 4 apply across all priorities.

### 7.1 PRIORITY 1 — Animal and Guardian Registration

| ID | Requirement |
|---|---|
| FR-1.1 | The system shall allow registration of a companion animal linked to a guardian/holder (owner or *tenedor*), a community animal (no fixed guardian), or a stray animal, as distinct registration types. |
| FR-1.2 | Guardian/holder registration shall capture at minimum: national ID (*cédula*), phone number, full name, and location. Location shall be captured via **device GPS auto-fill where available**, with a **controlled catalog** fallback for manual entry — free-text address/territory entry is not permitted for the territory field (see Section 4 and Section 9). |
| FR-1.3 | Animal registration shall capture at minimum: species (controlled catalog: canine, feline, and other companion animal types — see FR-9), breed or "mixed/creole" designation, color, approximate age, sex, sterilization status, and a photograph. |
| FR-1.4 | The end-to-end basic registration flow (one guardian + one animal) shall be completable in **under 3 minutes**, per the assigned usability constraint. Non-essential fields must be deferrable or optional to meet this target. |
| FR-1.5 | The system shall support a physical identification mechanism for sterilized stray animals based on a **recognition tattoo**: the app shall allow a veterinarian to record "tattoo present / tattoo absent" during an encounter to avoid duplicate sterilization procedures. |
| FR-1.6 | The system shall allow **manual, professional-validated deduplication** when a community/stray animal may already exist in the registry (e.g., matched by territory, registering actor, recorded characteristics, or photo). Automated matching may propose candidates, but a human (veterinary staff) must confirm merges — no automatic merge is permitted. |
| FR-1.7 | The system shall support **ownership/guardianship transfer** (traspaso) between guardians, preserving full traceability of prior guardians (analogous to a vehicle title transfer), so that a mistreatment case against a *current* holder does not erase the record of a *previous* holder when relevant. |
| FR-1.8 | The system shall record the **role of the actor performing the registration** (community member, veterinarian, administrative staff) from a controlled catalog, linked to permission scope. |

### 7.2 PRIORITY 2 — Lost and Found Animals

| ID | Requirement |
|---|---|
| FR-2.1 | The system shall allow a guardian to report their animal as **lost**, capturing identifying characteristics, last known location (via controlled catalog / GPS, not free text), date, and photo(s). |
| FR-2.2 | The system shall allow any user (including veterinary staff) to report an animal as **found**, with the same structured data. |
| FR-2.3 | The system shall generate **alerts** to nearby app users (optionally within a configurable radius of the loss location) when a new "lost" report is created. |
| FR-2.4 | The system shall attempt automatic **candidate matching** between "lost" and "found" records based on structured characteristics (species, breed, color, approximate age/size, sex, and location proximity); matches must be flagged for **mandatory manual validation by program staff** before being confirmed to either party — no automatic confirmation is permitted, since near-identical animals may be visually indistinguishable (explicitly cited by the stakeholder as a past false-positive risk). |
| FR-2.5 | If an animal picked up by the program is not claimed after **20 days**, the system shall support transitioning its status to "abandoned," which triggers eligibility for adoption processing (see FR-6). |

### 7.3 PRIORITY 3 — Clinical History

| ID | Requirement |
|---|---|
| FR-3.1 | The system shall maintain a persistent, chronologically ordered **clinical history record per animal**, linking every veterinary attention to the same animal identity. |
| FR-3.2 | Each clinical history entry shall capture: date, reporting/observed signs (*anamnesis* — what the guardian/reporter observes), clinical findings from physical examination, diagnosis/treatment prescribed, and any **commitments** given to the guardian (e.g., "administer medication X"). |
| FR-3.3 | The system shall allow a veterinarian to retrieve an animal's full prior clinical history when a new report/attention is logged, to support continuity of care (e.g., detect that a previously prescribed treatment was not completed). |
| FR-3.4 | The system shall support recording standard preventive-care data independent of a specific incident: vaccination status/schedule, deworming (*desparasitación*) status, and sterilization status. |
| FR-3.5 | The system shall support recording **behavioral and health flags** relevant to future adoption or handling (e.g., "not apartment-suitable," "predation risk toward poultry/livestock," known chronic viral conditions such as feline leukemia/FIV, or after-effects of diseases such as parvovirus or distemper that imply dietary or follow-up requirements). |
| FR-3.6 | **Mortality / death recording (assigned scenario requirement).** The system shall allow a veterinarian to record an animal's **death** as a distinct outcome/event type, capturing date, cause (controlled catalog with an "other/notes" option), and the context in which it occurred (e.g., during veterinary care, apprehension, custody, or reported by a third party). Recording a death shall automatically: <br>• Close any open case(s) or active commitments associated with that animal; <br>• Preserve the full historical record (no deletion); <br>• Update the animal's status to a terminal "Deceased" state in the catalog described in FR-9.1. |

### 7.4 Presumed Mistreatment Reporting and Case Management

| ID | Requirement |
|---|---|
| FR-4.1 | The system shall allow citizens to submit a mistreatment report capturing: reporter name (optional), phone (optional if anonymous), location (controlled catalog), photo, video, audio, and a free-text description of the situation. |
| FR-4.2 | The system shall support **two distinct privacy modes** for a report: (a) fully anonymous (no reporter identity captured), and (b) identified reporter with **requested reserved identity** (identity captured internally but never disclosed to the investigated party). This distinction must be explicit and enforced in access control, not merely a UI label. |
| FR-4.3 | The system shall allow a veterinarian/investigator to produce a structured **visit record (*acta de visita*)** per case, capturing: who received the visit, their relationship to the animal (owner/holder/other), and standardized assessment sections for feeding, environment, health, and behavior. |
| FR-4.4 | If multiple animals are present at a visited location, the system shall require **one visit record per animal**. |
| FR-4.5 | The system shall support case classification into: **no mistreatment**, **irresponsible ownership** (*tenencia irresponsable*), or **mistreatment**, with mistreatment further classified as **mild**, **moderate**, or **severe**, per the criteria described by the stakeholder (Section 3.2 examples: mild — confinement/space/food/water issues; moderate — underweight animal, untreated skin conditions; severe — injuries, wounds, malnutrition, life risk). |
| FR-4.6 | For mild and moderate cases, the system shall allow logging a **corrective commitment** with a **follow-up deadline** (mild: reference ~20 days; moderate: reference ~5–15 days, editable per case) and shall support scheduling/tracking of the required **follow-up visit**. |
| FR-4.7 | The system shall detect and flag **recurrence**: if a new report arrives referencing the same case (identical media/description) while a commitment is still open, the system shall link it to the existing case rather than creating a duplicate visit requirement. If the new report reflects a **different or aggravating condition**, it shall be treated as a new event within the same case history. |
| FR-4.8 | For severe cases, the system shall support initiating an **apprehension workflow** (see FR-5) without requiring a prior commitment step. |
| FR-4.9 | The system shall require a **formal closing response** to the original reporter (where not anonymous) for every case, recording one of: no mistreatment found; irresponsible ownership addressed (with actions taken); or mistreatment confirmed and referred to the competent authority (Police inspection or Prosecutor's Office / *Fiscalía*). |
| FR-4.10 | All case records, including closed/archived cases with no finding, shall remain permanently queryable to support recurrence detection on future reports. |

### 7.5 Sterilization Campaigns

| ID | Requirement |
|---|---|
| FR-5.1 | The system shall support registering sterilization events tied to a specific animal and guardian/holder (or as a stray/community animal), including date, location (controlled catalog), and outcome. |
| FR-5.2 | The system shall support batch/campaign-style data entry for field sterilization drives (*jornadas*) conducted in urban neighborhoods and rural *veredas*. |
| FR-5.3 | The system shall record the tattoo-based identification outcome (see FR-1.5) as part of the sterilization event. |

### 7.6 Apprehension, Custody, and Adoption

| ID | Requirement |
|---|---|
| FR-6.1 | The system shall support registering an **apprehension** (seizure at vital risk) performed in coordination with the National Police, including who found/identified the animal, the location, and transport/logistics details. |
| FR-6.2 | The system shall track **custody duration rules**: general custody animals become eligible for adoption after approximately 20 days to 1 month if unclaimed; animals from apprehension cases follow an **exception rule** (custody may extend 2–3+ months, or until the competent authority — Police/Prosecutor's Office — completes its legal process), because premature adoption of an animal under active legal proceedings poses medical and legal risk. |
| FR-6.3 | The system shall support the **adoption application process**, capturing: prospective adopter's *vereda*/neighborhood (controlled catalog), address, home videos, confirmation that all household members agree to the adoption, and housing tenure (owned/rented). |
| FR-6.4 | The system shall support recording a **compatibility visit/first-meeting outcome** between the prospective adopter and the animal prior to finalizing adoption. |
| FR-6.5 | The system shall support post-adoption **follow-up tracking** (vaccination schedule, veterinary check-ins). |
| FR-6.6 | The system shall surface an animal's recorded **behavioral flags and known health conditions** (see FR-3.5) during the adoption matching process, to avoid unsuitable placements (e.g., an active/high-energy animal placed in a small apartment, or an animal with a chronic transmissible condition placed with a susceptible companion animal). |

### 7.7 Case Routing, Roles, and In-App Communication

| ID | Requirement |
|---|---|
| FR-7.1 | The system shall define at least three role types with distinct permissions: **Community/Citizen**, **Program Lead / Coordinator**, and **Veterinarian** (including the assigned scenario's *veterinario aliado* persona). |
| FR-7.2 | The system shall route new cases directly to the **on-duty veterinarian** without requiring manual relay through a single administrative phone line, addressing the current WhatsApp bottleneck. |
| FR-7.3 | The system shall record, per case, which specific veterinarian is assigned/responsible, so coordination staff can see case ownership (e.g., "this case belongs to Dr. Valentina"). |
| FR-7.4 | The system shall implement a **structured (semi-closed) intake chat**: the initial exchange with a citizen shall present a constrained set of options/questions (case type: request an appointment, report a case, send a message, report an emergency) sufficient to classify and route the case; after minimum required information is captured, the chat shall become **open** to allow follow-up questions or additional details from either party. |
| FR-7.5 | For emergencies specifically, the minimum required intake data shall be: reporter name, phone number, location (controlled catalog / GPS), and a photo or video enabling the veterinarian to perform triage. |
| FR-7.6 | For general (non-emergency) veterinary intake, the minimum required data shall include, at minimum: observed signs, vaccination status, deworming status, and (for cats specifically) indoor/outdoor access pattern — reflecting the five core triage questions identified by the veterinary stakeholder. |
| FR-7.7 | The in-app communication channel shall replace direct exchange of a veterinarian's **personal phone number** with the citizen, to prevent after-hours personal contact and to preserve institutional control of the channel. |

### 7.8 Statistics and Indicators

| ID | Requirement |
|---|---|
| FR-8.1 | The system shall provide mandatory regulatory-reporting indicators at minimum: total sterilizations per period, total veterinary attentions per period, and total presumed-mistreatment cases received per period. |
| FR-8.2 | The system shall support indicators segmented by **territory** (neighborhood/*vereda*, drawn from the controlled catalog) and by **time period** (e.g., month with highest report volume), to support resource-allocation decisions (e.g., identifying territories requiring recurring sterilization campaigns due to population size or socioeconomic vulnerability). |
| FR-8.3 | The system should support a count of registered animals per territory and (where available) cross-reference with vaccination data, acknowledging that rabies vaccination records are currently owned by the Secretaría de Salud and may not be directly available to this system in the pilot phase. |

### 7.9 Reference Catalogs (Controlled Values)

| ID | Requirement |
|---|---|
| FR-9.1 | The system shall define a controlled **animal status catalog** including at minimum: Active/Healthy, In Custody, In Treatment, Lost, Found, Adopted, Abandoned, Deceased. |
| FR-9.2 | The system shall define a controlled **species/companion-animal-type catalog** that is not limited to dogs and cats, and explicitly accommodates other companion animals mentioned by the stakeholder (e.g., rabbits, hamsters, horses), while excluding production/livestock animals, which fall under a different municipal program area (*asistencia técnica en producción*) and are out of scope. |
| FR-9.3 | The system shall define a controlled **territory catalog** (the 14 *veredas* plus urban neighborhoods/*barrios* of Zipaquirá) used for every location field in the application. **Free-text entry for territory is prohibited** per the assigned scenario restriction (Section 4). |
| FR-9.4 | The system shall define controlled catalogs for: mistreatment severity (mild/moderate/severe), case classification (no finding / irresponsible ownership / mistreatment), reporting-actor role, and sex (male/female). |

---

## 8. Non-Functional Requirements

### 8.1 Platform and technology constraints (mandatory)
- **NFR-1.1** The mobile application shall be developed **natively for Android** using **Kotlin** in **Android Studio**. Cross-platform frameworks (Flutter, React Native, or equivalent) are **explicitly disallowed** for this deliverable.

### 8.2 Offline-first operation and synchronization (mandatory — assigned scenario)
- **NFR-2.1** The application shall function as an **offline-first** system: all core registration, reporting, and case-update actions must be fully operable **without an active network connection**.
- **NFR-2.2** Given the assigned connectivity scenario (dispersed rural *vereda*, connectivity only after 6:00 p.m.), the application shall queue all locally created/updated records and perform **synchronization in a deferred batch window**, triggered automatically when connectivity becomes available (e.g., end-of-day) and/or manually by the user.
- **NFR-2.3** Each record shall carry a **synchronization status flag** (synced / pending) and a **local version indicator** to support conflict handling, consistent with the base data dictionary fields `sincronizado` and `version_local` (Section 9).
- **NFR-2.4** The system shall implement a **conflict-resolution strategy** for records edited locally by multiple actors before sync (e.g., last-write-wins with an audit trail, or manual merge prompt for high-stakes fields such as case classification or death recording); the specific strategy shall be documented in the technical design phase.
- **NFR-2.5** Media attachments (photo/video/audio) captured offline shall be stored locally and uploaded opportunistically during the sync window, with graceful handling of partial/interrupted uploads over degraded rural connections.

### 8.3 Scalability
- **NFR-3.1** The data storage and sync architecture shall be validated to handle at least **500 records** (animals, guardians, and/or users, per the assigned scale parameter) with acceptable local performance (sub-second local queries) on typical mid-range Android hardware.
- **NFR-3.2** The architecture should not preclude scaling beyond the pilot's 500-record target toward the municipality's actual annual volumes (Section 3.3: ~2,200 sterilizations and ~570 attentions per year, i.e., multi-year cumulative volumes in the thousands).

### 8.4 Usability
- **NFR-4.1** The basic animal + guardian registration flow shall be completable in **under 3 minutes** (FR-1.4).
- **NFR-4.2** Location capture shall default to automatic GPS retrieval to minimize manual data entry when the device is online at the moment of registration, falling back to catalog selection when offline or GPS is unavailable.
- **NFR-4.3** The UI shall consistently use the term **"animal de compañía"** rather than "mascota" throughout all user-facing copy.

### 8.5 Data privacy and protection
- **NFR-5.1** The system shall comply with applicable Colombian personal data protection regulations (*Ley de Tratamiento de Datos Personales*) for any personally identifiable information collected (reporter identity, guardian identity, phone numbers, national ID numbers).
- **NFR-5.2** The system shall enforce the anonymity/reserved-identity distinction described in FR-4.2 at the access-control level, ensuring reserved-identity reporter data is never exposed to the investigated party or to roles without explicit authorization.
- **NFR-5.3** Statistical/reporting views (FR-8) shall be produced from **aggregated, de-identified data** by default.

### 8.6 Reliability and data integrity
- **NFR-6.1** No record shall be permanently deleted from the system; closed, archived, or "no finding" cases must remain queryable to support recurrence detection (FR-4.7, FR-4.10).
- **NFR-6.2** `registro_id` (or equivalent primary key) shall be unique and stable across the lifetime of a record, independent of any potentially duplicated attribute such as animal name (see Section 9).

### 8.7 Maintainability and institutional continuity
- **NFR-7.1** The system shall be designed for continuity across changes in municipal administration (multi-year institutional use), independent of any single political term, per the stakeholder's explicit requirement and the pending public policy mandate for a dedicated information system.
- **NFR-7.2** Documentation and code shall be structured to allow a future team to extend the pilot toward a production, cloud-hosted system, since the municipality does not currently operate its own server and anticipates a future cloud-based deployment.

---

## 9. Data Model / Data Dictionary

The following data dictionary is based on the fields suggested as a starting point, expanded with fields explicitly required by the stakeholder interviews and by the assigned scenario constraints (offline sync, controlled-catalog territory, and mortality event support). **Fields marked "(new)" are additions beyond the originally suggested set** and should be validated with the stakeholder before final implementation; the original set is explicitly described as "not necessarily ideal nor representative of all the information the app requires."

### 9.1 Core registry fields (from suggested base dictionary)

| Field | Description | Suggested type | Analysis criterion |
|---|---|---|---|
| registro_id | Record identifier | Text | Must be unique and stable |
| fecha_registro | Capture date/time | Date-time | Validate temporal sequence |
| animal_nombre | Reported animal name | Text | Do not use as unique identifier |
| especie | Species (dog/cat/…) | Catalog | Controlled value |
| sexo | Sex (male/female) | Catalog | Controlled value |
| raza | Breed or mixed/creole condition | Catalog | Normalize |
| color | Predominant color | Catalog | Avoid semantic variation |
| edad_aprox_anos | Approximate age | Decimal | Validate logical range |
| esterilizado | Sterilization status | Boolean | Useful for indicators |
| fecha_vacuna_rabia | Reported rabies vaccination date | Date | Validate against record/events |
| microchip | Physical identifier | Text | Unique when present |
| estado_animal | Current status | Catalog | Must be consistent with events (see FR-9.1) |
| responsable_id | Logical guardian ID | Text | May be empty depending on scenario (e.g., community animal) |
| responsable_nombre | Synthetic/reported guardian name | Text | Not a standalone identifier |
| telefono | Reported phone number | Text | Normalize format |
| territorio | Neighborhood/*vereda*/sector | **Catalog (mandatory — no free text, per assigned restriction)** | Key for territorial aggregation |
| zona | Urban/Rural | Catalog | Controlled value |
| latitud / longitud | Approximate location | Decimal | Evaluate need/precision; auto-filled via GPS where possible |
| actor_registro | Role capturing the record | Catalog | Relate to permissions |
| situacion_evento | Event situation | Catalog | Transfer, loss, etc. |
| fecha_evento | Event date | Date-time | Validate sequence |
| sincronizado | Sync status | Boolean | Essential for offline operation (NFR-2.3) |
| version_local | Local record version | Integer | Supports conflict handling (NFR-2.4) |
| observacion_campo | General field note | Text | Does not substitute for critical catalogs |

### 9.2 Additional fields required by stakeholder interviews and assigned scenario (new)

| Field | Description | Suggested type | Analysis criterion |
|---|---|---|---|
| tipo_tenedor | Owner vs. holder (*propietario* / *tenedor*) vs. community/stray | Catalog | Determines guardianship semantics |
| tatuaje_esterilizacion | Presence/absence of sterilization identification tattoo | Boolean | Prevents duplicate sterilization (FR-1.5) |
| animal_id_anterior / cadena_traspaso | Link to prior guardian record(s) | Text/relation | Supports guardianship transfer traceability (FR-1.7) |
| historia_clinica_id | Foreign key linking a clinical entry to the animal record | Text/relation | Enables continuous clinical history (FR-3.1) |
| anamnesis | Guardian-reported signs/symptoms | Text | Captured per attention |
| hallazgos_clinicos | Clinical findings from physical exam | Text | Captured per attention |
| compromiso_descripcion | Description of corrective commitment | Text | Linked to mistreatment/attention follow-up |
| compromiso_plazo_dias | Commitment follow-up deadline (days) | Integer | Default references: mild ≈ 20, moderate ≈ 5–15 |
| compromiso_estado | Commitment status (open/fulfilled/unfulfilled) | Catalog | Drives escalation logic (FR-4.6, FR-4.9) |
| clasificacion_caso | No finding / irresponsible ownership / mistreatment | Catalog | FR-4.5 |
| gravedad_maltrato | Mistreatment severity (mild/moderate/severe) | Catalog | FR-4.5 |
| tipo_reporte_privacidad | Anonymous vs. identified-with-reserved-identity | Catalog | Enforced at access-control level (FR-4.2, NFR-5.2) |
| fecha_fallecimiento | Death date (assigned scenario requirement) | Date | Populated only when estado_animal = Deceased |
| causa_fallecimiento | Cause of death | Catalog + free-text "other" | FR-3.6 |
| contexto_fallecimiento | Context of death (in care, apprehension, custody, third-party report) | Catalog | FR-3.6 |
| flag_comportamental | Behavioral flags relevant to handling/adoption | Multi-select catalog | FR-3.5, FR-6.6 |
| condicion_salud_cronica | Known chronic conditions (e.g., FeLV/FIV, post-parvovirus sequelae) | Multi-select catalog + notes | FR-3.5, FR-6.6 |
| adoptante_id | Prospective/confirmed adopter reference | Text/relation | FR-6.3 |
| adopcion_estado | Adoption process status | Catalog | FR-6.3–FR-6.5 |
| custodia_fecha_inicio / custodia_fecha_limite | Custody start date and applicable eligibility deadline | Date | Differentiated rule for apprehension cases (FR-6.2) |
| caso_veterinario_asignado | Veterinarian assigned/responsible for the case | Text/relation | FR-7.3 |
| media_adjunta | Photo/video/audio attachment references | File reference(s) | Stored locally, uploaded on sync (NFR-2.5) |

### 9.3 Data model notes
- `registro_id` must remain the stable unique key across the record's lifecycle; `animal_nombre` must never be relied upon for deduplication, since names are informal, may be duplicated, and may change (explicitly noted by stakeholders and reflected in the base dictionary).
- All catalog-type fields must be populated from **predefined, versioned lists** managed centrally (not hardcoded per screen) to support the "controlled value" / "normalize" analysis criteria consistently, and specifically to satisfy the mandatory territory-catalog restriction in Section 4.
- The offline/sync fields (`sincronizado`, `version_local`) are **not optional** for this project given the assigned connectivity scenario; they must be present on every syncable entity, not only on the top-level animal record.

---

## 10. Special Scenario Handling: Mortality Event (Assigned Requirement)

Because "Fallecimiento" (death) is the assigned special situation for this team, the following end-to-end behavior is a **first-class requirement**, not an edge case:

1. A veterinarian (or, where applicable, an administrative actor closing a custody/apprehension case) can record a death against an existing animal record.
2. The system requires, at minimum: date of death, cause (from catalog, with free-text override), and context (in-care, apprehension, custody, or third-party report).
3. Upon saving, the system automatically: closes any open commitments/cases tied to that animal, sets `estado_animal` to "Deceased," and preserves — never deletes — the full historical record for audit and statistical purposes.
4. Death events must be reflected in statistical indicators (Section 7.8) to support institutional reporting (e.g., mortality associated with apprehension/custody cases, which the stakeholder noted is legally and medically sensitive because adoption cannot proceed while legal proceedings are pending and an animal's condition may deteriorate).
5. This flow must be fully operable **offline**, consistent with Section 8.2, since the assigned actor persona (allied veterinarian) is expected to operate primarily in a rural, low-connectivity setting.

---

## 11. Assumptions and Constraints

- **A-1.** The pilot targets a subset of full institutional functionality; scope is bounded by the three stakeholder-ranked priorities (registration, lost & found, clinical history) plus the assigned scenario's mandatory elements (offline sync, controlled territory catalog, mortality handling, allied-veterinarian persona, ~500-record scale).
- **A-2.** No production server/cloud infrastructure currently exists; the pilot's backend/sync target environment must be defined during technical design and validated with the sponsor, since budget for a production system was explicitly described as unavailable at the time of the meeting.
- **A-3.** Rabies vaccination records are owned by the Secretaría de Salud via a separate departmental system; direct integration is out of scope for the pilot, and `fecha_vacuna_rabia` should be treated as guardian-reported data unless a future integration is defined.
- **A-4.** The exact SISBÉN category ceiling used for free-service prioritization was not confirmed during the meeting and must be validated with the stakeholder before being hardcoded into any eligibility logic.
- **A-5.** The 14-*vereda* territory catalog and urban neighborhood list must be obtained/confirmed from the municipality as an authoritative reference dataset before implementing FR-9.3.
- **C-1.** Technology stack is fixed: native Android, Kotlin, Android Studio — no cross-platform frameworks.
- **C-2.** The application must remain usable end-to-end without continuous connectivity, per the assigned scenario.

---

## 12. Out of Scope (for this pilot)

- Procurement or configuration of production server/cloud hosting (may be referenced as a future roadmap item, per Section 8.7).
- E-commerce/virtual store functionality (mentioned by the stakeholder only as a possible long-term idea, explicitly not a current objective).
- The "Guardianes de los Animales" community volunteer/points program (future public-policy initiative, not part of this pilot).
- Direct integration with the Secretaría de Salud's departmental system.
- Management of production/livestock animals (handled by a different municipal program area).
- Legal/judicial case management for Police or Prosecutor's Office proceedings (the app only records referral, not case adjudication).

---

## 13. Risks

| Risk | Impact | Mitigation |
|---|---|---|
| Offline-first sync conflicts (multiple actors editing the same record before connectivity returns) | Data integrity / lost updates | Define explicit conflict-resolution rules (NFR-2.4) during technical design; prioritize audit trail over silent overwrite for case-classification and mortality fields. |
| Automated lost/found or duplicate-animal matching producing false positives | Guardian distress, incorrect animal handover | Mandatory manual professional validation before any match is confirmed (FR-2.4, FR-1.6), as explicitly required by the stakeholder based on a real prior incident. |
| Incomplete or unconfirmed reference data (territory catalog, SISBÉN ceiling) | Delayed implementation or incorrect eligibility logic | Validate authoritative lists with the Secretaría before implementation (A-4, A-5). |
| Media capture/upload over degraded rural connectivity | Incomplete evidence for triage or mistreatment cases | Local-first media storage with resumable/opportunistic upload during the sync window (NFR-2.5). |
| Reserved-identity data leaking to an investigated party | Reporter safety / trust in the reporting channel | Enforce access control at the data layer, not only in UI (NFR-5.2). |

---

## 14. Acceptance Criteria (Pilot-Level)

1. A user can complete a basic animal + guardian registration in under 3 minutes, fully offline, with territory selected from a controlled catalog (no free text).
2. A veterinarian can record a full attention (including clinical history retrieval) and, where applicable, a death event, entirely offline, with the record correctly queued for sync.
3. Deferred synchronization succeeds once connectivity is restored (simulated end-of-day window), with `sincronizado` and `version_local` correctly updated and no data loss.
4. A lost/found match is never auto-confirmed; it always requires explicit human validation before being surfaced as a confirmed match to end users.
5. A mistreatment report can be submitted anonymously or with reserved identity, and the investigated party's view never exposes reporter identity in the reserved-identity case.
6. The system correctly supports at least 500 concurrent local records without perceptible performance degradation on a representative Android test device.
7. All user-facing text uses "animal de compañía," never "mascota."

---

## 15. Appendix — Source Material Reference

| Source | Content used |
|---|---|
| Meeting summary (PDF) | Institutional context, process description, priorities, annual volumes, regulatory references |
| Verbatim meeting transcript | Direct stakeholder statements on workflow pain points, field-level data needs, connectivity constraints, classification rules, and terminology guidance |
| Project brief ("Información general") | Application objective, mandatory technology constraint (native Android/Kotlin), and this team's assigned scenario parameters (territory, connectivity, priority actor, special situation, scale, territory-catalog restriction) |
| Suggested field dictionary (data.md) | Base data model starting point, explicitly noted by its authors as non-exhaustive and non-authoritative |

---

*End of document.*
