# 📖 REFERENCIA RÁPIDA - Java 8 Compatibility Fixes

**Quick Reference Card - Soluciones Java 8**

---

## 🆘 Si ves este error...

### Error 1: "cannot find symbol - method toList()"

```
[ERROR] cannot find symbol
[ERROR]   symbol:   method toList()
[ERROR]   location: interface java.util.stream.Stream<...>
```

**SOLUCIÓN:**
```java
// ❌ INCORRECTO
.toList()

// ✅ CORRECTO
.collect(Collectors.toList())
```

**Requiere:**
```java
import java.util.stream.Collectors;
```

---

### Error 2: "cannot find symbol - method getIdServicio()" o similar

```
[ERROR] cannot find symbol
[ERROR]   symbol:   method getIdServicio()
[ERROR]   location: class ConfiguracionEstimacion
```

**SOLUCIÓN:**
```java
// ❌ INCORRECTO - El método no existe
configuracion.getIdServicio()

// ✅ CORRECTO - Acceder a través del objeto
configuracion.getServicio().getId()
```

---

## 📋 Checkliste Rápido - Antes de Commit

```
[ ] Busqué .toList() en mi código → Si existe → CAMBIAR
[ ] Compilé con: mvn clean compile → Resultado: BUILD SUCCESS
[ ] Verifiqué acceso a relaciones: objeto.getId() ✓
[ ] Verifiqué setters: setObjeto(objeto) ✓ (no ID)
[ ] Reviséque tengo: import java.util.stream.Collectors;
```

---

## 🔍 Búsqueda Rápida en Código

### Buscar .toList() (Java 16+ - INCORRECTO)
```bash
# PowerShell
Select-String -Path "src/**/*.java" -Pattern "\.toList\(\)" -Recurse

# Bash
grep -r "\.toList()" src/
```

### Buscar getIdXXX() (INCORRECTO en JPA)
```bash
# PowerShell
Select-String -Path "src/**/*.java" -Pattern "\.getId[A-Z]" -Recurse

# Bash
grep -r "\.getIdServicio\|\.getIdGroomer\|\.getIdCliente" src/
```

---

## ✅ Patrones Correctos (Copiar-Pegar)

### Stream con Filtro
```java
// ✅ CORRECTO
List<Configuracion> lista = repository.findAll().stream()
    .filter(c -> c.getServicio() != null && 
                 c.getServicio().getId().equals(idServicio))
    .collect(Collectors.toList());
```

### Optional con Seguridad
```java
// ✅ CORRECTO
Optional<Usuario> usuario = repository.findById(id);
if (usuario.isPresent()) {
    Usuario u = usuario.get();
}
```

### Actualizar Relación
```java
// ✅ CORRECTO
Servicio servicio = servicioRepo.findById(id)
    .orElseThrow(() -> new NotFoundException("No encontrado"));
    
configuracion.setServicio(servicio);  // Objeto completo
repository.save(configuracion);
```

---

## 🚨 ERRORES COMUNES

| Error | Causa | Solución |
|-------|-------|----------|
| `.toList()` | Java 16+ | `.collect(Collectors.toList())` |
| `getIdXXX()` | No existe | `getObjeto().getId()` |
| `var` | Java 10+ | Especificar tipo: `List<...>` |
| `record` | Java 14+ | Usar clase normal |

---

## 📞 Si Necesitas Ayuda

**Consulta estos documentos:**
1. `JAVA8_COMPATIBILITY_VERIFICATION.md` - Detalles completos
2. `JAVA8_MAINTENANCE_GUIDE.md` - Guía de referencia
3. `JAVA8_RESOLUTION_SUMMARY.md` - Resumen de correcciones

---

**Status:** ✅ 0 ERRORES  
**Compatibilidad:** Java 8, 11, 16, 17+ ✓  
**Listo para Producción:** SÍ ✓

