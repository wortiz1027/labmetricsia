-- ==============================================================================
-- 📐 PARTE 1: PREPARACIÓN DEL ENTORNO Y DEFINICIÓN ESTRUCTURAL (DDL PURO)
-- ==============================================================================

-- 1. Encendemos la extensión de vectores nativa en el motor de Postgres
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. Creamos la tabla base vector_store según el estándar inmutable de Spring AI
CREATE TABLE IF NOT EXISTS vector_store (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    content TEXT NOT NULL,
    metadata JSONB,
    embedding VECTOR(1536)
);

-- ==============================================================================
-- 📊 PARTE 2: DEFINICIÓN DE LLAVES Y CREACIÓN DE ÍNDICES DE INTELIGENCIA ARTIFICIAL
-- ==============================================================================

-- Restricción de Llave Primaria para asegurar la unicidad de cada KnowledgeChunk
ALTER TABLE vector_store
    ADD CONSTRAINT pk_vector_store_id PRIMARY KEY (id);


-- Índice HNSW avanzado para búsquedas semánticas ultra rápidas (RAG)
-- Se crea en una sentencia independiente utilizando la métrica de distancia de coseno
CREATE INDEX IF NOT EXISTS idx_vector_store_embedding_hnsw
    ON vector_store USING hnsw (embedding vector_cosine_ops);

-- Índice complementario de rendimiento para acelerar filtros de metadatos JSONB
-- Permite que la IA busque trozos filtrando rápidamente por manual_id o páginas específicas
CREATE INDEX IF NOT EXISTS idx_vector_store_metadata_gin
    ON vector_store USING gin (metadata);
