package com.alcaldia.censoanimal.data

import com.alcaldia.censoanimal.model.ActaVisita
import com.alcaldia.censoanimal.model.AnimalRecord
import com.alcaldia.censoanimal.model.ClinicalRecord
import com.alcaldia.censoanimal.model.LostFoundAlert
import com.alcaldia.censoanimal.model.MistreatmentReport
import com.alcaldia.censoanimal.model.MunicipalIndicator
import com.alcaldia.censoanimal.model.PresetAccount
import com.alcaldia.censoanimal.model.UserRole

object Microdataset {

    val OFFICIAL_VEREDAS = listOf(
        "Vereda San Jorge",
        "Vereda El Rosal",
        "Vereda Barroblanco",
        "Vereda Santa Librada",
        "Vereda La Esperanza",
        "Vereda San Benito",
        "Vereda Ventalarga",
        "Vereda Portachuelo",
        "Vereda Río Frío",
        "Vereda El Tunal",
        "Vereda Páramo de Guerrero",
        "Vereda Barandillas",
        "Vereda La Granja",
        "Vereda Empalizado",
        "Casco Urbano / Barrios"
    )

    val SPECIES_LIST = listOf(
        "Perro",
        "Gato",
        "Conejo / Pequeña Especie",
        "Equino de Compañía"
    )

    val GUARDIAN_TYPES = listOf(
        "Propietario / Tenedor Permanente",
        "Tenedor Temporal / Cuidador",
        "Animal Comunitario (Custodia colectiva)",
        "Animal en Situación de Calle / Albergue"
    )

    val DOG_BREEDS = listOf(
        "Criollo / Mestizo",
        "Labrador Retriever",
        "Pastor Alemán",
        "Golden Retriever",
        "Pitbull Terrier (Ley 1801)",
        "Poodle / Caniche",
        "Pinscher",
        "Beagle",
        "Bulldog Francés",
        "Rottweiler (Ley 1801)",
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
            role = UserRole.ADMINISTRADOR,
            label = "Administrador Municipal",
            email = "admin.ambiental@alcaldia.gov.co",
            password = "Alcaldia2026*",
            badge = "Administración / Control",
            badgeColor = "#D1FAE5",
            description = "Control total de censo, auditoría, indicadores y usuarios."
        ),
        PresetAccount(
            role = UserRole.CIUDADANO,
            label = "Usuario Registrado",
            email = "ciudadano.rural@gmail.com",
            password = "Alcaldia2026*",
            badge = "Mis Animales",
            badgeColor = "#FEF3C7",
            description = "Gestión de animales propios, carné virtual con QR y reportes."
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
            responsable_nombre = "Carlos Mendoza",
            documento_tipo = "CC",
            documento_numero = "19384921",
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
            responsable_nombre = "Carlos Mendoza",
            documento_tipo = "CC",
            documento_numero = "19384921",
            telefono = "3119283746",
            territorio = "Vereda Barroblanco",
            territorio_catalogo = "Vereda Barroblanco",
            direccion_finca = "Finca San Isidro Sector Bajo",
            latitud = 5.0075,
            longitud = -73.9812,
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
            latitud = 5.0090,
            longitud = -73.9835,
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
            latitud = 5.0068,
            longitud = -73.9805,
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
            latitud = 5.0295,
            longitud = -73.9542,
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
            latitud = 5.0312,
            longitud = -73.9565,
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
            latitud = 5.0285,
            longitud = -73.9535,
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
            latitud = 4.9810,
            longitud = -74.0138,
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
            latitud = 4.9792,
            longitud = -74.0165,
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
            latitud = 4.9820,
            longitud = -74.0142,
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

    val INITIAL_CLINICAL_RECORDS: MutableMap<String, MutableList<ClinicalRecord>> = mutableMapOf(
        "CEN-2026-001" to mutableListOf(
            ClinicalRecord(
                id = "CLI-001-01",
                registro_id = "CEN-2026-001",
                fecha = "2024-03-15",
                tipo_evento = "Esterilización Quirúrgica",
                profesional = "Dra. Mariana Gómez (M.V. Z-4821)",
                anamnesis = "Ingreso a campaña municipal de control reproductivo rural.",
                hallazgos = "Orquiectomía bilateral sin complicaciones. Se verifica y confirma ausencia de ectoparásitos. Paciente estable.",
                compromiso_correctivo = "Reposo relativo por 5 días, uso de collar isabelino y retiro de puntos en puesto de salud.",
                cumplido = true
            ),
            ClinicalRecord(
                id = "CLI-001-02",
                registro_id = "CEN-2026-001",
                fecha = "2025-11-20",
                tipo_evento = "Jornada Vacunación Antirrábica",
                profesional = "Dr. Ruiz (Secretaría de Salud)",
                anamnesis = "Renovación anual biológico antirrábico rural.",
                hallazgos = "Constantes fisiológicas normales (T° 38.6°C, FC 90 lpm). Aplicación subcutánea Lote RAB-2025-X8.",
                compromiso_correctivo = "Vigilar posibles reacciones locales durante 24 horas.",
                cumplido = true
            )
        ),
        "CEN-2026-002" to mutableListOf(
            ClinicalRecord(
                id = "CLI-002-01",
                registro_id = "CEN-2026-002",
                fecha = "2025-10-14",
                tipo_evento = "Jornada Vacunación Antirrábica",
                profesional = "Dra. Mariana Gómez",
                anamnesis = "Vacunación felina comunitaria.",
                hallazgos = "Hembra felina en buena condición corporal (3/5). Aplicación de vacuna antirrábica y desparasitación oral.",
                compromiso_correctivo = "Agendada para próxima jornada de esterilización en Vereda San Jorge.",
                cumplido = false
            )
        ),
        "CEN-2026-003" to mutableListOf(
            ClinicalRecord(
                id = "CLI-003-01",
                registro_id = "CEN-2026-003",
                fecha = "2026-01-10",
                tipo_evento = "Consulta Canino de Manejo Especial (Ley 1801)",
                profesional = "Dr. Alejandro Casas (M.V. Inspección)",
                anamnesis = "Evaluación etológica y registro de raza de manejo especial (Pitbull Terrier).",
                hallazgos = "Macho en excelente musculatura. Sin signos de agresividad no provocada. Microchip verificado.",
                compromiso_correctivo = "Obligatoriedad de uso de bozal tipo canastilla y traílla en espacios públicos. Presentar póliza de responsabilidad civil en Inspección de Policía.",
                cumplido = true
            )
        )
    )

    val INITIAL_LOST_FOUND_ALERTS: MutableList<LostFoundAlert> = mutableListOf(
        LostFoundAlert(
            id = "ALR-2026-01",
            tipo = "PERDIDO",
            especie = "Perro",
            nombre = "Toby",
            raza = "Golden Retriever",
            color = "Dorado / Miel",
            vereda = "Vereda San Jorge",
            fecha_evento = "2026-02-27",
            microchip = "981098102938411",
            contacto_nombre = "Elena Suárez",
            contacto_telefono = "3129847162",
            estado = "Candidato Coincidencia Detectado",
            descripcion = "Se asustó con truenos en la Finca La Colina. Lleva collar azul reflectivo. Requiere medicación.",
            posible_coincidencia_registro_id = "CEN-2026-011",
            dias_custodia_albergue = 0,
            validacion_manual_aprobada = false
        ),
        LostFoundAlert(
            id = "ALR-2026-02",
            tipo = "HALLADO",
            especie = "Perro",
            nombre = null,
            raza = "Golden Retriever",
            color = "Dorado / Miel",
            vereda = "Vereda San Jorge",
            fecha_evento = "2026-02-28",
            microchip = null,
            contacto_nombre = "Policía Ambiental Zipaquirá",
            contacto_telefono = "3209876543",
            estado = "Candidato Coincidencia Detectado",
            descripcion = "Canino hallado desorientado cerca al puente de la quebrada. Muy dócil, collar azul desgastado.",
            posible_coincidencia_registro_id = "ALR-2026-01",
            dias_custodia_albergue = 2,
            validacion_manual_aprobada = false
        ),
        LostFoundAlert(
            id = "ALR-2026-03",
            tipo = "HALLADO",
            especie = "Perro",
            nombre = "Negro",
            raza = "Criollo / Mestizo",
            color = "Negro",
            vereda = "Vereda Barroblanco",
            fecha_evento = "2026-02-05",
            microchip = null,
            contacto_nombre = "Albergue Municipal Zipaquirá",
            contacto_telefono = "01800091234",
            estado = "Custodia Municipal (+20 Días - Declarado Abandonado)",
            descripcion = "Ingresó por presunto abandono en vía pública. Cumplió 24 días de custodia oficial sin reclamo de propietario. Pasa a estado de adoptabilidad formal según TRD FR-2.5.",
            posible_coincidencia_registro_id = null,
            dias_custodia_albergue = 24,
            validacion_manual_aprobada = true
        )
    )

    val INITIAL_MISTREATMENT_REPORTS: MutableList<MistreatmentReport> = mutableListOf(
        MistreatmentReport(
            id = "DEN-2026-001",
            fecha_radicado = "2026-02-25",
            modo_privacidad = "Identidad Reservada",
            denunciante_nombre = "Vecino Sector Rural (Protegido)",
            denunciante_contacto = "3119876543",
            vereda = "Vereda Santa Librada",
            direccion_exacta = "Finca Los Sauces, casa esquinera cerca al acueducto",
            descripcion_hechos = "Canino mestizo permanece encadenado a la intemperie bajo lluvia y sol sin plato de agua potable. Presenta delgadez marcada.",
            nivel_gravedad_aparente = "Moderado",
            estado_caso = "Acta Diligenciada",
            acta_visita = ActaVisita(
                id = "ACTA-2026-01",
                reporte_id = "DEN-2026-001",
                fecha_visita = "2026-02-26",
                inspector_veterinario = "Dr. Alejandro Casas (Secretaría Rural)",
                condicion_corporal = "2 - Bajo peso / Costillas visibles",
                condicion_espacio = "Restringido / Atado permanente sin resguardo",
                condicion_alimentacion = "Alimento insuficiente, sin agua limpia permanente",
                clasificacion_final = "Maltrato Moderado",
                dias_plazo_compromiso = 10,
                compromiso_texto = "El propietario se compromete a desatar al animal, construir canil con techo adecuado de 3x3m, proporcionar alimentación 2 veces al día y agua a voluntad. Se fija visita de seguimiento para el 2026-03-08.",
                requiere_aprehension_policia = false,
                compromiso_cumplido = false
            )
        ),
        MistreatmentReport(
            id = "DEN-2026-002",
            fecha_radicado = "2026-02-28",
            modo_privacidad = "Completamente Anónimo",
            denunciante_nombre = null,
            denunciante_contacto = null,
            vereda = "Vereda El Rosal",
            direccion_exacta = "Sector La Esperanza, galpón abandonado",
            descripcion_hechos = "Cachorros abandonados en caja de cartón cerca a zanja de aguas lluvias.",
            nivel_gravedad_aparente = "Grave / Urgente",
            estado_caso = "Pendiente Visita",
            acta_visita = null
        )
    )

    // Current in-memory stores
    val currentRecords: MutableList<AnimalRecord> = INITIAL_ANIMAL_RECORDS.toMutableList()
    val clinicalRecordsStore: MutableMap<String, MutableList<ClinicalRecord>> = INITIAL_CLINICAL_RECORDS.toMutableMap()
    val lostFoundAlertsStore: MutableList<LostFoundAlert> = INITIAL_LOST_FOUND_ALERTS.toMutableList()
    val mistreatmentReportsStore: MutableList<MistreatmentReport> = INITIAL_MISTREATMENT_REPORTS.toMutableList()

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

    // Clinical History Methods
    fun getClinicalRecords(animalId: String): List<ClinicalRecord> {
        return clinicalRecordsStore[animalId] ?: emptyList()
    }

    fun addClinicalRecord(animalId: String, record: ClinicalRecord) {
        val list = clinicalRecordsStore.getOrPut(animalId) { mutableListOf() }
        list.add(0, record)
    }

    // Lost & Found Methods
    fun getAllLostFoundAlerts(): List<LostFoundAlert> = lostFoundAlertsStore

    fun addLostFoundAlert(alert: LostFoundAlert) {
        lostFoundAlertsStore.add(0, alert)
    }

    fun approveManualMatch(alertId: String) {
        val alert = lostFoundAlertsStore.find { it.id == alertId } ?: return
        alert.validacion_manual_aprobada = true
        alert.estado = "Reunificado con Guardián"
    }

    // Mistreatment Methods
    fun getAllMistreatmentReports(): List<MistreatmentReport> = mistreatmentReportsStore

    fun addMistreatmentReport(report: MistreatmentReport) {
        mistreatmentReportsStore.add(0, report)
    }

    fun saveActaVisita(reportId: String, acta: ActaVisita) {
        val report = mistreatmentReportsStore.find { it.id == reportId } ?: return
        report.acta_visita = acta
        report.estado_caso = if (acta.clasificacion_final == "Sin Maltrato") "Cerrado / Sancionado" else "Acta Diligenciada"
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
