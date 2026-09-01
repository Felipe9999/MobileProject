package com.alcaldia.censoanimal.data

import com.alcaldia.censoanimal.model.AnimalRecord
import com.alcaldia.censoanimal.model.MunicipalIndicator
import com.alcaldia.censoanimal.model.PresetAccount
import com.alcaldia.censoanimal.model.UserRole

object Microdataset {

    val OFFICIAL_VEREDAS = listOf(
        "Vereda San Jorge",
        "Vereda El Rosal",
        "Vereda Barroblanco",
        "Vereda Santa Librada",
        "Vereda La Esperanza"
    )

    val DOG_BREEDS = listOf(
        "Criollo / Mestizo",
        "Labrador Retriever",
        "Pastor Alemán",
        "Golden Retriever",
        "Pitbull Terrier",
        "Poodle / Caniche",
        "Pinscher",
        "Beagle",
        "Bulldog Francés",
        "Rottweiler",
        "Siberian Husky",
        "Otro / Sin raza definida"
    )

    val CAT_BREEDS = listOf(
        "Criollo / Mestizo",
        "Siamés",
        "Persa",
        "Angora",
        "Maine Coon",
        "Bengala",
        "British Shorthair",
        "Otro / Sin raza definida"
    )

    val COLOR_OPTIONS = listOf(
        "Negro",
        "Blanco",
        "Café / Chocolate",
        "Dorado / Miel",
        "Atigrado / Abigarrado",
        "Bicolor (Blanco y Negro)",
        "Bicolor (Blanco y Café)",
        "Tricolor / Calicó",
        "Gris / Cenizo"
    )

    val PRESET_ACCOUNTS = listOf(
        PresetAccount(
            role = UserRole.VETERINARIO,
            label = "Veterinario Aliado",
            email = "veterinario.campo@alcaldia.gov.co",
            password = "Alcaldia2026*",
            badge = "Campo / Completo",
            badgeColor = "#DBEAFE",
            description = "Censo en veredas, registro de chips, vacunas y resolución de duplicados."
        ),
        PresetAccount(
            role = UserRole.FUNCIONARIO,
            label = "Funcionario Municipal",
            email = "salud.publica@alcaldia.gov.co",
            password = "Alcaldia2026*",
            badge = "Auditoría / Métricas",
            badgeColor = "#D1FAE5",
            description = "Tableros de control, cobertura por veredas, tasas y fiscalización."
        ),
        PresetAccount(
            role = UserRole.CIUDADANO,
            label = "Ciudadano / Propietario",
            email = "ciudadano.rural@gmail.com",
            password = "Alcaldia2026*",
            badge = "Consulta Pública",
            badgeColor = "#FEF3C7",
            description = "Consulta de microchips y ficha sanitaria básica (Habeas Data)."
        )
    )

    val INITIAL_ANIMAL_RECORDS: MutableList<AnimalRecord> = mutableListOf(
        AnimalRecord(
            registro_id = "CEN-2026-001",
            microchip = "981098102938401",
            especie = "Perro",
            animal_nombre = "Lucas",
            sexo = "Macho",
            raza = "Criollo / Mestizo",
            color = "Café / Chocolate",
            edad_meses = 36,
            esterilizado = true,
            fecha_esterilizacion = "2024-03-15",
            fecha_vacuna_rabia = "2025-11-20",
            lote_vacuna = "RAB-2025-X8",
            responsable_nombre = "Carlos Rodríguez",
            documento_tipo = "CC",
            documento_numero = "19283746",
            telefono = "3104829102",
            territorio = "Vereda San Jorge",
            territorio_catalogo = "Vereda San Jorge",
            direccion_finca = "Finca El Porvenir Lote 4",
            latitud = 4.9842,
            longitud = -73.9562,
            fecha_censo = "2026-02-10",
            censo_por = "Dr. Ruiz (Vet. Rural)",
            observaciones = "Animal dócil, esquema de desparasitación al día.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-002",
            microchip = "981098102938402",
            especie = "Perro",
            animal_nombre = "Rocky",
            sexo = "Macho",
            raza = "Pastor Alemán",
            color = "Negro",
            edad_meses = 48,
            esterilizado = false,
            fecha_esterilizacion = null,
            fecha_vacuna_rabia = "2025-08-14",
            lote_vacuna = "RAB-2025-M2",
            responsable_nombre = "Hernando Gómez",
            documento_tipo = "CC",
            documento_numero = "79883192",
            telefono = "3128492019",
            territorio = "Vereda San Jorge",
            territorio_catalogo = "Vereda San Jorge",
            direccion_finca = "Hacienda La Colina Km 2",
            latitud = 4.9885,
            longitud = -73.9518,
            fecha_censo = "2026-02-10",
            censo_por = "Dr. Ruiz (Vet. Rural)",
            observaciones = "Guardián de predio. Propietario solicita jornada de esterilización.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-003",
            microchip = "981098102938403",
            especie = "Gato",
            animal_nombre = "Michi",
            sexo = "Hembra",
            raza = "Criollo / Mestizo",
            color = "Tricolor / Calicó",
            edad_meses = 18,
            esterilizado = true,
            fecha_esterilizacion = "2025-05-10",
            fecha_vacuna_rabia = "2025-10-05",
            lote_vacuna = "RAB-2025-F4",
            responsable_nombre = "María Forero",
            documento_tipo = "CC",
            documento_numero = "52839201",
            telefono = "3209182301",
            territorio = "Vereda San Jorge",
            territorio_catalogo = "Vereda San Jorge",
            direccion_finca = "Sector Los Pinos Casa 2",
            latitud = 4.9815,
            longitud = -73.9591,
            fecha_censo = "2026-02-11",
            censo_por = "Dr. Ruiz (Vet. Rural)",
            observaciones = "Gata de casa con hábito semi-exterior.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-004",
            microchip = "981098102938404",
            especie = "Perro",
            animal_nombre = "Canela",
            sexo = "Hembra",
            raza = "Golden Retriever",
            color = "Dorado / Miel",
            edad_meses = 24,
            esterilizado = true,
            fecha_esterilizacion = "2025-02-18",
            fecha_vacuna_rabia = "2025-12-01",
            lote_vacuna = "RAB-2025-X8",
            responsable_nombre = "Ana Patricia Beltrán",
            documento_tipo = "CC",
            documento_numero = "41928374",
            telefono = "3157291048",
            territorio = "Vereda El Rosal",
            territorio_catalogo = "Vereda El Rosal",
            direccion_finca = "Finca Villa Luz Entrada 3",
            latitud = 4.9921,
            longitud = -73.9534,
            fecha_censo = "2026-02-12",
            censo_por = "Dra. Mora (Bienestar Animal)",
            observaciones = "Excelente condición corporal.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-005",
            microchip = null,
            especie = "Perro",
            animal_nombre = "Zeus",
            sexo = "Macho",
            raza = "Pitbull Terrier",
            color = "Atigrado / Abigarrado",
            edad_meses = 30,
            esterilizado = false,
            fecha_esterilizacion = null,
            fecha_vacuna_rabia = "2025-07-22",
            lote_vacuna = "RAB-2025-M2",
            responsable_nombre = "Javier Morales",
            documento_tipo = "CC",
            documento_numero = "80192847",
            telefono = "3148920194",
            territorio = "Vereda El Rosal",
            territorio_catalogo = "Vereda El Rosal",
            direccion_finca = "Lote La Herradura",
            latitud = 4.9904,
            longitud = -73.9588,
            fecha_censo = "2026-02-12",
            censo_por = "Dra. Mora (Bienestar Animal)",
            observaciones = "Raza de manejo especial (Ley 1801). Requiere registro de póliza y microchipado pendiente.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-006",
            microchip = "981098102938406",
            especie = "Gato",
            animal_nombre = "Simba",
            sexo = "Macho",
            raza = "Siamés",
            color = "Dorado / Miel",
            edad_meses = 12,
            esterilizado = true,
            fecha_esterilizacion = "2025-11-30",
            fecha_vacuna_rabia = "2025-11-30",
            lote_vacuna = "RAB-2025-F4",
            responsable_nombre = "Laura Mendoza",
            documento_tipo = "CC",
            documento_numero = "1019283746",
            telefono = "3178291048",
            territorio = "Vereda El Rosal",
            territorio_catalogo = "Vereda El Rosal",
            direccion_finca = "Cabaña El Mirador",
            latitud = 4.9938,
            longitud = -73.9502,
            fecha_censo = "2026-02-13",
            censo_por = "Dra. Mora (Bienestar Animal)",
            observaciones = "Microchip implantado en jornada municipal.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-007",
            microchip = "981098102938407",
            especie = "Perro",
            animal_nombre = "Toby",
            sexo = "Macho",
            raza = "Beagle",
            color = "Tricolor / Calicó",
            edad_meses = 60,
            esterilizado = false,
            fecha_esterilizacion = null,
            fecha_vacuna_rabia = "2025-06-18",
            lote_vacuna = "RAB-2025-M2",
            responsable_nombre = "Guillermo Páez",
            documento_tipo = "CC",
            documento_numero = "11394820",
            telefono = "3119283746",
            territorio = "Vereda Barroblanco",
            territorio_catalogo = "Vereda Barroblanco",
            direccion_finca = "Finca San Isidro Sector Bajo",
            latitud = 4.9792,
            longitud = -73.9612,
            fecha_censo = "2026-02-15",
            censo_por = "Dr. Ruiz (Vet. Rural)",
            observaciones = "Perro de cacería/campo.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-008",
            microchip = "981098102938408",
            especie = "Perro",
            animal_nombre = "Luna",
            sexo = "Hembra",
            raza = "Poodle / Caniche",
            color = "Blanco",
            edad_meses = 84,
            esterilizado = true,
            fecha_esterilizacion = "2020-04-12",
            fecha_vacuna_rabia = "2025-09-09",
            lote_vacuna = "RAB-2025-X8",
            responsable_nombre = "Esperanza Silva",
            documento_tipo = "CC",
            documento_numero = "23948192",
            telefono = "3138291048",
            territorio = "Vereda Barroblanco",
            territorio_catalogo = "Vereda Barroblanco",
            direccion_finca = "Casa Quinta Los Nogales",
            latitud = 4.9808,
            longitud = -73.9635,
            fecha_censo = "2026-02-15",
            censo_por = "Dr. Ruiz (Vet. Rural)",
            observaciones = "Paciente geriátrico con soplo cardíaco controlado.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-009",
            microchip = "981098102938409",
            especie = "Gato",
            animal_nombre = "Oliver",
            sexo = "Macho",
            raza = "Criollo / Mestizo",
            color = "Negro",
            edad_meses = 8,
            esterilizado = false,
            fecha_esterilizacion = null,
            fecha_vacuna_rabia = "2026-01-10",
            lote_vacuna = "RAB-2026-A1",
            responsable_nombre = "Felipe Cárdenas",
            documento_tipo = "CC",
            documento_numero = "1028374619",
            telefono = "3189283746",
            territorio = "Vereda Barroblanco",
            territorio_catalogo = "Vereda Barroblanco",
            direccion_finca = "Granja Avícola La Paz",
            latitud = 4.9822,
            longitud = -73.9601,
            fecha_censo = "2026-02-16",
            censo_por = "Dr. Ruiz (Vet. Rural)",
            observaciones = "Programado para próxima jornada de castración felina.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-010",
            microchip = "981098102938410",
            especie = "Perro",
            animal_nombre = "Max",
            sexo = "Macho",
            raza = "Labrador Retriever",
            color = "Negro",
            edad_meses = 40,
            esterilizado = true,
            fecha_esterilizacion = "2023-08-20",
            fecha_vacuna_rabia = "2025-10-18",
            lote_vacuna = "RAB-2025-X8",
            responsable_nombre = "Rosa Elena Duarte",
            documento_tipo = "CC",
            documento_numero = "39481928",
            telefono = "3167291048",
            territorio = "Vereda Santa Librada",
            territorio_catalogo = "Vereda Santa Librada",
            direccion_finca = "Finca Arrayanes Lote B",
            latitud = 4.9861,
            longitud = -73.9495,
            fecha_censo = "2026-02-18",
            censo_por = "Dra. Mora (Bienestar Animal)",
            observaciones = "Excelente estado de salud. Chip verificado.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-011",
            microchip = "981098102938411",
            especie = "Perro",
            animal_nombre = "Princesa",
            sexo = "Hembra",
            raza = "Pinscher",
            color = "Negro",
            edad_meses = 52,
            esterilizado = true,
            fecha_esterilizacion = "2022-11-15",
            fecha_vacuna_rabia = "2025-05-12",
            lote_vacuna = "RAB-2025-M2",
            responsable_nombre = "Andrés Camargo",
            documento_tipo = "CC",
            documento_numero = "74819283",
            telefono = "3129182736",
            territorio = "Vereda Santa Librada",
            territorio_catalogo = "Vereda Santa Librada",
            direccion_finca = "Vivienda Rural Los Alisos",
            latitud = 4.9878,
            longitud = -73.9482,
            fecha_censo = "2026-02-18",
            censo_por = "Dra. Mora (Bienestar Animal)",
            observaciones = "Vacuna próxima a vencer (requiere refuerzo mayo 2026).",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-012",
            microchip = null,
            especie = "Gato",
            animal_nombre = "Nieve",
            sexo = "Hembra",
            raza = "Angora",
            color = "Blanco",
            edad_meses = 20,
            esterilizado = false,
            fecha_esterilizacion = null,
            fecha_vacuna_rabia = "2025-11-05",
            lote_vacuna = "RAB-2025-F4",
            responsable_nombre = "Cecilia Vargas",
            documento_tipo = "CC",
            documento_numero = "51928374",
            telefono = "3218291048",
            territorio = "Vereda Santa Librada",
            territorio_catalogo = "Vereda Santa Librada",
            direccion_finca = "Finca El Manantial",
            latitud = 4.9854,
            longitud = -73.9510,
            fecha_censo = "2026-02-19",
            censo_por = "Dra. Mora (Bienestar Animal)",
            observaciones = "Propietaria no ha podido desplazarse para chip ni esterilización.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-013",
            microchip = "981098102938413",
            especie = "Perro",
            animal_nombre = "Bruno",
            sexo = "Macho",
            raza = "Rottweiler",
            color = "Negro",
            edad_meses = 28,
            esterilizado = false,
            fecha_esterilizacion = null,
            fecha_vacuna_rabia = "2025-08-30",
            lote_vacuna = "RAB-2025-M2",
            responsable_nombre = "Óscar Benavides",
            documento_tipo = "CC",
            documento_numero = "80918273",
            telefono = "3109281726",
            territorio = "Vereda La Esperanza",
            territorio_catalogo = "Vereda La Esperanza",
            direccion_finca = "Predio La Fortuna",
            latitud = 4.9912,
            longitud = -73.9628,
            fecha_censo = "2026-02-21",
            censo_por = "Dr. Ruiz (Vet. Rural)",
            observaciones = "Manejo especial. Póliza de responsabilidad civil vigente.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-014",
            microchip = "981098102938414",
            especie = "Perro",
            animal_nombre = "Maya",
            sexo = "Hembra",
            raza = "Siberian Husky",
            color = "Gris / Cenizo",
            edad_meses = 32,
            esterilizado = true,
            fecha_esterilizacion = "2024-09-10",
            fecha_vacuna_rabia = "2025-12-15",
            lote_vacuna = "RAB-2025-X8",
            responsable_nombre = "Tatiana Riaño",
            documento_tipo = "CC",
            documento_numero = "1032918273",
            telefono = "3192837465",
            territorio = "Vereda La Esperanza",
            territorio_catalogo = "Vereda La Esperanza",
            direccion_finca = "Chalet Los Pinos Altos",
            latitud = 4.9930,
            longitud = -73.9642,
            fecha_censo = "2026-02-21",
            censo_por = "Dr. Ruiz (Vet. Rural)",
            observaciones = "Condición óptima. Esquema de vacunas completo.",
            estado_animal = "Activo",
            sincronizado_alcaldia = true,
            version_registro = 1
        ),
        AnimalRecord(
            registro_id = "CEN-2026-015",
            microchip = "981098102938401", // Duplicado intencional de CEN-2026-001 para alerta de conflicto
            especie = "Gato",
            animal_nombre = "Tom",
            sexo = "Macho",
            raza = "Criollo / Mestizo",
            color = "Bicolor (Blanco y Negro)",
            edad_meses = 15,
            esterilizado = false,
            fecha_esterilizacion = null,
            fecha_vacuna_rabia = "2025-11-18",
            lote_vacuna = "RAB-2025-F4",
            responsable_nombre = "Samuel Niño",
            documento_tipo = "CC",
            documento_numero = "71829304",
            telefono = "3158291029",
            territorio = "Vereda La Esperanza",
            territorio_catalogo = "Vereda La Esperanza",
            direccion_finca = "Sector El Triunfo Finca 2",
            latitud = 4.9898,
            longitud = -73.9615,
            fecha_censo = "2026-02-22",
            censo_por = "Dr. Ruiz (Vet. Rural)",
            observaciones = "ALERTA: Código de microchip duplicado con CEN-2026-001 (Lucas). Requiere rectificación de lectura en campo.",
            estado_animal = "Activo",
            sincronizado_alcaldia = false,
            offline_pending = true,
            alerta_duplicado = true,
            version_registro = 1
        )
    )

    val MUNICIPAL_INDICATORS = listOf(
        MunicipalIndicator(
            id = "IND-01",
            codigo = "IND-SAN-01",
            nombre = "Tasa de Cobertura de Vacunación Antirrábica Rural",
            categoria = "Salud Pública",
            valor = "80.0%",
            formula = "(Animales con vacuna antirrábica vigente / Total animales censados) * 100",
            estado = "Alerta",
            decisionInstitucional = "Priorizar jornada masiva en Vereda Barroblanco y Santa Librada antes del vencimiento masivo del segundo semestre."
        ),
        MunicipalIndicator(
            id = "IND-02",
            codigo = "IND-SAN-02",
            nombre = "Proporción de Población Esterilizada (Control Reproductivo)",
            categoria = "Población",
            valor = "46.7%",
            formula = "(Animales esterilizados quirúrgicamente / Total censados) * 100",
            estado = "Critico",
            decisionInstitucional = "Solicitar adición presupuestal al Fondo Municipal de Bienestar Animal para unidad móvil quirúrgica rural."
        ),
        MunicipalIndicator(
            id = "IND-03",
            codigo = "IND-ID-03",
            nombre = "Porcentaje de Identificación con Microchip Oficial",
            categoria = "Calidad de Datos",
            valor = "86.7%",
            formula = "(Animales con microchip RFID válido / Total censados) * 100",
            estado = "Optimo",
            decisionInstitucional = "Mantener estándar de microchipado obligatorio en cada jornada de esterilización o vacunación municipal."
        ),
        MunicipalIndicator(
            id = "IND-04",
            codigo = "IND-DEM-04",
            nombre = "Razón Especie Canina / Felina Rural",
            categoria = "Población",
            valor = "2.0 : 1",
            formula = "Total Perros Censados (10) / Total Gatos Censados (5)",
            estado = "Optimo",
            decisionInstitucional = "Dimensionar insumos de biológicos considerando 67% dosis caninas y 33% dosis felinas."
        ),
        MunicipalIndicator(
            id = "IND-05",
            codigo = "IND-ESP-05",
            nombre = "Registro y Póliza de Razas de Manejo Especial (Ley 1801)",
            categoria = "Salud Pública",
            valor = "13.3%",
            formula = "(Animales manejo especial registrados / Total caninos) * 100",
            estado = "Alerta",
            decisionInstitucional = "Inspección de policía citará a propietarios de Pitbull y Rottweiler para verificación de póliza y tenencia responsable."
        ),
        MunicipalIndicator(
            id = "IND-06",
            codigo = "IND-TER-06",
            nombre = "Cobertura Georreferenciada de Veredas Oficiales",
            categoria = "Territorio",
            valor = "100.0%",
            formula = "(Veredas con al menos 1 registro / 5 veredas oficiales) * 100",
            estado = "Optimo",
            decisionInstitucional = "Meta cumplida en la fase de línea base rural 2026."
        ),
        MunicipalIndicator(
            id = "IND-07",
            codigo = "IND-CAL-07",
            nombre = "Tasa de Integridad de Datos y Validación de Catálogos",
            categoria = "Calidad de Datos",
            valor = "93.3%",
            formula = "(Registros sin anomalías críticas / Total registros) * 100",
            estado = "Alerta",
            decisionInstitucional = "Resolver duplicado de microchip en Vereda La Esperanza (Tom vs Lucas)."
        ),
        MunicipalIndicator(
            id = "IND-08",
            codigo = "IND-SIN-08",
            nombre = "Índice de Sincronización Móvil Offline/Online",
            categoria = "Territorio",
            valor = "93.3%",
            formula = "(Registros consolidados en servidor Alcaldía / Total censos en campo) * 100",
            estado = "Optimo",
            decisionInstitucional = "Capacidad offline validada; 1 registro pendiente de transmisión local."
        )
    )

    // Current in-memory list
    val currentRecords: MutableList<AnimalRecord> = INITIAL_ANIMAL_RECORDS.toMutableList()

    fun getAllRecords(): List<AnimalRecord> = currentRecords

    fun findRecordById(id: String): AnimalRecord? = currentRecords.find { it.registro_id == id }

    fun addRecord(record: AnimalRecord) {
        currentRecords.add(0, record)
    }

    fun updateRecord(record: AnimalRecord) {
        val index = currentRecords.indexOfFirst { it.registro_id == record.registro_id }
        if (index != -1) {
            currentRecords[index] = record
        }
    }

    fun registerDeath(recordId: String, deathDate: String, deathReason: String) {
        val record = findRecordById(recordId) ?: return
        record.estado_animal = "Fallecido"
        record.fecha_fallecimiento = deathDate
        record.motivo_fallecimiento = deathReason
        record.version_registro += 1
        record.offline_pending = true
        record.sincronizado_alcaldia = false
    }

    fun toggleSterilization(recordId: String, esterilizado: Boolean, fecha: String?) {
        val record = findRecordById(recordId) ?: return
        record.esterilizado = esterilizado
        record.fecha_esterilizacion = if (esterilizado) fecha ?: "2026-02-28" else null
        record.version_registro += 1
        record.offline_pending = true
        record.sincronizado_alcaldia = false
    }

    fun updateVaccineDate(recordId: String, fecha: String) {
        val record = findRecordById(recordId) ?: return
        record.fecha_vacuna_rabia = fecha
        record.version_registro += 1
        record.offline_pending = true
        record.sincronizado_alcaldia = false
    }

    fun resolveDuplicateChip(recordId: String, newChip: String) {
        val record = findRecordById(recordId) ?: return
        record.microchip = newChip
        record.alerta_duplicado = false
        record.version_registro += 1
        record.offline_pending = false
        record.sincronizado_alcaldia = true
    }

    fun syncAllOffline(): Int {
        var count = 0
        currentRecords.forEach {
            if (it.offline_pending && !it.alerta_duplicado) {
                it.offline_pending = false
                it.sincronizado_alcaldia = true
                count++
            }
        }
        return count
    }

    fun getPendingSyncCount(): Int {
        return currentRecords.count { it.offline_pending }
    }
}
