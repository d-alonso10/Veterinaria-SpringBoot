#!/bin/bash

# Script para ejecutar pruebas de integración con Newman
# Uso: ./run_tests.sh

echo "=========================================="
echo "TeranVet API - Integration Tests Runner"
echo "=========================================="
echo ""

# Variables
COLLECTION_FILE="Postman_Collection.json"
ENVIRONMENT_FILE="postman_environment.json"
RESULTS_DIR="test-results"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Crear directorio de resultados
mkdir -p $RESULTS_DIR

# Verificar si Newman está instalado
if ! command -v newman &> /dev/null
then
    echo "❌ Newman no está instalado."
    echo "Instalando Newman globalmente..."
    npm install -g newman newman-reporter-htmlextra
    echo "✅ Newman instalado correctamente"
else
    echo "✅ Newman encontrado"
fi

# Verificar si los archivos existen
if [ ! -f "$COLLECTION_FILE" ]; then
    echo "❌ Error: No se encontró $COLLECTION_FILE"
    exit 1
fi

if [ ! -f "$ENVIRONMENT_FILE" ]; then
    echo "❌ Error: No se encontró $ENVIRONMENT_FILE"
    exit 1
fi

echo ""
echo "📋 Ejecutando pruebas..."
echo "   Colección: $COLLECTION_FILE"
echo "   Entorno: $ENVIRONMENT_FILE"
echo "   Resultados guardados en: $RESULTS_DIR/"
echo ""

# Ejecutar Newman con reportes
newman run "$COLLECTION_FILE" \
  --environment "$ENVIRONMENT_FILE" \
  --reporters cli,json,htmlextra \
  --reporter-json-export "$RESULTS_DIR/results_$TIMESTAMP.json" \
  --reporter-htmlextra-export "$RESULTS_DIR/report_$TIMESTAMP.html" \
  --bail \
  --verbose

# Capturar resultado
NEWMAN_EXIT_CODE=$?

echo ""
echo "=========================================="

if [ $NEWMAN_EXIT_CODE -eq 0 ]; then
    echo "✅ TODAS LAS PRUEBAS PASARON"
    echo "   📊 Resultados JSON: $RESULTS_DIR/results_$TIMESTAMP.json"
    echo "   📈 Reporte HTML: $RESULTS_DIR/report_$TIMESTAMP.html"
else
    echo "❌ ALGUNAS PRUEBAS FALLARON"
    echo "   Código de salida: $NEWMAN_EXIT_CODE"
fi

echo "=========================================="
echo ""

exit $NEWMAN_EXIT_CODE
