-- ==============================================================================
-- 🗄️ PARTE 1: DEFINICIÓN ESTRUCTURAL DE LAS TABLAS (DDL PURO)
-- ==============================================================================

-- 📚 1. Soporte para la clase 'TechnicalManual'
CREATE TABLE IF NOT EXISTS tb_technical_manuals (
    id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    version VARCHAR(20) NOT NULL,
    description TEXT,
    file_path VARCHAR(512) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    uploaded_by VARCHAR(100) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deprecated BOOLEAN NOT NULL DEFAULT FALSE,
    chunk_count INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 🚨 2. Soporte para la clase 'LabIncident'
CREATE TABLE IF NOT EXISTS tb_lab_incidents (
    id VARCHAR(36) NOT NULL,
    error_description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- Mapea el Enum IncidentStatus
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 📊 3. Soporte para la clase 'ServerMetrics' (Relación 1 a 1 con LabIncident)
CREATE TABLE IF NOT EXISTS tb_server_metrics (
    id BIGINT AUTO_INCREMENT,
    incident_id VARCHAR(36) NOT NULL,
    cpu_usage_percentage DOUBLE NOT NULL,
    ram_usage_gigabytes DOUBLE NOT NULL,
    gpu_vram_usage_gigabytes DOUBLE NOT NULL,
    is_swap_active BOOLEAN NOT NULL DEFAULT FALSE,
    captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 🧠 4. Soporte para la clase 'DiagnosticPlan' (Relación 1 a 0..1 con LabIncident)
CREATE TABLE IF NOT EXISTS tb_diagnostic_plans (
    id BIGINT AUTO_INCREMENT,
    incident_id VARCHAR(36) NOT NULL,
    analysis_conclusion TEXT NOT NULL,
    steps_to_solve JSON NOT NULL, -- Guardamos la lista List<String> como un arreglo JSON nativo
    requires_kernel_reboot BOOLEAN NOT NULL DEFAULT FALSE,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 📐 PARTE 2: DEFINICIÓN DE LLAVES Y ÍNDICES DE RENDIMIENTO DEL DOMINIO
-- ==============================================================================

-- Restricciones de Llaves Primarias
ALTER TABLE tb_technical_manuals ADD CONSTRAINT pk_technical_manual_id PRIMARY KEY (id);
ALTER TABLE tb_lab_incidents      ADD CONSTRAINT pk_lab_incident_id      PRIMARY KEY (id);
ALTER TABLE tb_server_metrics     ADD CONSTRAINT pk_server_metric_id     PRIMARY KEY (id);
ALTER TABLE tb_diagnostic_plans   ADD CONSTRAINT pk_diagnostic_plan_id   PRIMARY KEY (id);

-- Restricciones de Integridad y Llaves Foráneas (Relaciones de tu diagrama Mermaid)
-- Conecta LabIncident (1) --> (1) ServerMetrics
ALTER TABLE tb_server_metrics
    ADD CONSTRAINT fk_metrics_incident_id
    FOREIGN KEY (incident_id) REFERENCES tb_lab_incidents(id)
    ON DELETE CASCADE;

-- Conecta LabIncident (1) --> (0..1) DiagnosticPlan
ALTER TABLE tb_diagnostic_plans
    ADD CONSTRAINT fk_plans_incident_id
    FOREIGN KEY (incident_id) REFERENCES tb_lab_incidents(id)
    ON DELETE CASCADE;

-- Índices de Rendimiento para acelerar búsquedas y auditorías
CREATE INDEX idx_manuals_title_version    ON tb_technical_manuals (title, version);
CREATE UNIQUE INDEX uq_metrics_incident_id ON tb_server_metrics (incident_id); -- Garantiza el 1 a 1 de las métricas
CREATE UNIQUE INDEX uq_plans_incident_id   ON tb_diagnostic_plans (incident_id);   -- Garantiza el 1 a 0..1 de los planes
CREATE INDEX idx_incidents_status         ON tb_lab_incidents (status);
