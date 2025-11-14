# 📋 GUÍA DE MANTENCIÓN JAVA 8 - Prácticas para Evitar Futuras Incompatibilidades

**Documento:** Guía de Mantención y Mejores Prácticas  
**Fecha:** 14 de Noviembre de 2025  
**Objetivo:** Asegurar que el código permanezca compatible con Java 8 durante la evolución del proyecto

---

## 🎯 Requisito del Proyecto

**OBLIGATORIO: Mantener compatibilidad con Java 8 (JDK 1.8)**

Este documento es un manual de referencia rápida para desarrolladores que trabajen en este proyecto.

---

## ✅ Mejores Prácticas - Stream API (Java 8)

### ✅ CORRECTO: Usar .collect(Collectors.toList())

```java
// ✅ CORRECTO - Java 8 compatible
List<Usuario> usuarios = usuarioRepository.findAll()
    .stream()
    .filter(u -> u.isActivo())
    .collect(Collectors.toList());
```

**Imports necesarios:**
```java
import java.util.stream.Collectors;
import java.util.List;
```

### ❌ INCORRECTO: Usar .toList()

```java
// ❌ INCORRECTO - Java 16+ SOLO
List<Usuario> usuarios = usuarioRepository.findAll()
    .stream()
    .filter(u -> u.isActivo())
    .toList();  // ← ERROR: Method not found in Java 8
```

### Ejemplos Completos - Stream API Java 8

#### Filtrar y Convertir a List
```java
// ✅ CORRECTO
List<String> nombres = usuarios.stream()
    .map(Usuario::getNombre)
    .filter(n -> n != null)
    .collect(Collectors.toList());
```

#### Buscar Primer Elemento
```java
// ✅ CORRECTO
Optional<Usuario> usuario = usuarios.stream()
    .filter(u -> u.getEmail().equals(email))
    .findFirst();

if (usuario.isPresent()) {
    System.out.println(usuario.get().getNombre());
}
```

#### Agrupar Resultados
```java
// ✅ CORRECTO - Java 8
Map<String, List<Usuario>> porRol = usuarios.stream()
    .collect(Collectors.groupingBy(Usuario::getRol));
```

#### Operaciones Encadenadas
```java
// ✅ CORRECTO - Múltiples operaciones
List<Usuario> resultado = usuarios.stream()
    .filter(u -> u.isActivo())
    .filter(u -> u.getRol().equals("ADMIN"))
    .sorted(Comparator.comparing(Usuario::getNombre))
    .collect(Collectors.toList());
```

---

## ✅ Mejores Prácticas - Optional (Java 8)

### Métodos Seguros

| Método | Compatibilidad | Uso |
|--------|---|---|
| `.isPresent()` | ✅ Java 8 | Verificar si existe |
| `.isEmpty()` | ✅ Java 8 | Verificar si está vacío |
| `.get()` | ✅ Java 8 | Obtener valor (con cuidado) |
| `.orElse(valor)` | ✅ Java 8 | Valor por defecto |
| `.orElseThrow(() -> ex)` | ✅ Java 8 | Excepción si vacío |
| `.map(func)` | ✅ Java 8 | Transformar valor |
| `.filter(pred)` | ✅ Java 8 | Filtrar valor |
| `.ifPresent(con)` | ✅ Java 8 | Ejecutar si existe |

### Ejemplos Correctos

```java
// ✅ CORRECTO - Patrones seguros
Optional<Usuario> usuario = usuarioRepository.findById(id);

// Verificar existencia
if (usuario.isPresent()) {
    Usuario u = usuario.get();
    System.out.println(u.getNombre());
}

// Valor por defecto
String nombre = usuario
    .map(Usuario::getNombre)
    .orElse("Sin nombre");

// Lanzar excepción si no existe
Usuario u = usuario
    .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

// Ejecutar si existe
usuario.ifPresent(u -> System.out.println(u.getNombre()));
```

---

## ✅ Mejores Prácticas - Acceso a Entidades JPA

### Relaciones One-to-Many / Many-to-One

#### ✅ CORRECTO: Acceder a través del objeto completo

```java
// Entidad con relación
@Entity
public class Configuracion {
    @Id
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;  // ← Objeto completo, NO id
    
    @ManyToOne
    @JoinColumn(name = "id_groomer")
    private Groomer groomer;    // ← Objeto completo, NO id
}

// LECTURA - Acceder a través del objeto
Integer servicioId = configuracion.getServicio().getId();  // ✅ CORRECTO

// ESCRITURA - Asignar objeto completo
configuracion.setServicio(servicio);  // ✅ CORRECTO

// VALIDACIÓN - Verificar null antes de acceder
if (configuracion.getServicio() != null) {
    Integer id = configuracion.getServicio().getId();
}
```

#### ❌ INCORRECTO: Intentar acceso directo a ID

```java
// ❌ INCORRECTO - Estos métodos no existen
Integer servicioId = configuracion.getIdServicio();     // ERROR
configuracion.setIdServicio(123);                       // ERROR

// ❌ INCORRECTO - Confundir con atributos
private Integer idServicio;  // ← EVITAR en JPA
```

### Patrón Correcto: CRUD con Relaciones

```java
// ✅ CORRECTO - Crear con relaciones
Servicio servicio = servicioRepository.findById(idServicio)
    .orElseThrow(() -> new NotFoundException("Servicio no encontrado"));

Groomer groomer = groomerRepository.findById(idGroomer)
    .orElseThrow(() -> new NotFoundException("Groomer no encontrado"));

Configuracion config = new Configuracion();
config.setServicio(servicio);          // ✅ Asignar objeto
config.setGroomer(groomer);            // ✅ Asignar objeto
config.setTiempoEstimado(60);
configRepository.save(config);

// ✅ CORRECTO - Actualizar relaciones
Configuracion existente = configRepository.findById(id)
    .orElseThrow(() -> new NotFoundException("Config no encontrada"));

if (nuevaConfig.getServicio() != null) {
    existente.setServicio(nuevaConfig.getServicio());   // ✅ Objeto completo
}

if (nuevaConfig.getGroomer() != null) {
    existente.setGroomer(nuevaConfig.getGroomer());     // ✅ Objeto completo
}

configRepository.save(existente);

// ✅ CORRECTO - Filtrar por relación en Stream
List<Configuracion> configs = configRepository.findAll()
    .stream()
    .filter(c -> c.getServicio() != null && 
                 c.getServicio().getId().equals(idServicio))
    .collect(Collectors.toList());
```

---

## 🚫 API Features que NO están disponibles en Java 8

| Feature | Versión | Evitar |
|---------|---------|--------|
| `.toList()` | Java 16+ | ❌ NO USAR |
| `var` keyword | Java 10+ | ❌ NO USAR |
| Records | Java 14+ | ❌ NO USAR |
| Text Blocks | Java 13+ | ❌ NO USAR |
| Sealed Classes | Java 15+ | ❌ NO USAR |
| Switch Expressions | Java 12+ | ❌ NO USAR |
| instanceof Pattern Match | Java 16+ | ❌ NO USAR |
| Default Methods Statics | Java 9+ | ❌ NO USAR |

---

## 📋 Checklist - Antes de Commit

**Antes de hacer commit a un archivo Java, verifica:**

- [ ] No hay `.toList()` → usar `.collect(Collectors.toList())`
- [ ] No hay `var` keyword → especificar tipo
- [ ] No hay Records → usar clases normales
- [ ] Imports correctos: `java.util.stream.Collectors`
- [ ] Acceso a relaciones JPA: objeto → ID, no ID directo
- [ ] Métodos Optional: `.isPresent()`, `.orElse()`, `.orElseThrow()`
- [ ] Null checks antes de acceder a objetos relacionados
- [ ] Compilación exitosa: `mvn clean compile`

---

## 🔧 Script de Verificación Local

### Buscar incompatibilidades en el código

```bash
# Buscar .toList() (Java 16+)
grep -r "\.toList()" src/main/java

# Buscar var keyword (Java 10+)
grep -r "\bvar\b" src/main/java

# Buscar getIdXXX() en streams (patrón incorrecto JPA)
grep -r "\.getIdServicio\|\.getIdGroomer\|\.getIdCliente" src/main/java
```

### Script PowerShell para verificar

```powershell
# Buscar patrones incompatibles
$toListUsages = Select-String -Path "src/**/*.java" -Pattern "\.toList\(\)" -Recurse
if ($toListUsages) {
    Write-Host "❌ Encontrado .toList() - Cambiar a .collect(Collectors.toList())"
    $toListUsages | ForEach-Object { Write-Host "  $_" }
}

# Buscar var keyword
$varUsages = Select-String -Path "src/**/*.java" -Pattern "\bvar\b" -Recurse
if ($varUsages) {
    Write-Host "❌ Encontrado 'var' keyword - Especificar tipo"
    $varUsages | ForEach-Object { Write-Host "  $_" }
}

Write-Host "✅ Verificación completada"
```

---

## 📚 Referencias Rápidas

### Stream API Java 8 (CORRECTA)
```java
// Collector options para Java 8
.collect(Collectors.toList())
.collect(Collectors.toSet())
.collect(Collectors.joining(","))
.collect(Collectors.groupingBy(x -> x))
.collect(Collectors.counting())
```

### Lambda Expressions (CORRECTA)
```java
// Java 8 lambdas
(x) -> x > 5
(x, y) -> x + y
() -> "valor"
x -> x.getNombre()
Usuario::getId  // Method reference
```

### Comparators (CORRECTA)
```java
// Java 8 comparators
Comparator.comparing(Usuario::getNombre)
Comparator.comparing(Usuario::getNombre).reversed()
Comparator.comparing(Usuario::getNombre).thenComparing(Usuario::getEmail)
```

---

## 🚨 Problemas Comunes y Soluciones

### Problema 1: Method not found: toList()

**Síntoma:**
```
error: cannot find symbol
  symbol:   method toList()
  location: interface java.util.stream.Stream
```

**Solución:**
```java
// ❌ INCORRECTO
list = stream.toList();

// ✅ CORRECTO
list = stream.collect(Collectors.toList());
```

### Problema 2: cannot find symbol: var

**Síntoma:**
```
error: cannot find symbol
  symbol:   class var
```

**Solución:**
```java
// ❌ INCORRECTO
var usuario = usuarioRepository.findById(1);

// ✅ CORRECTO
Optional<Usuario> usuario = usuarioRepository.findById(1);
// O
Usuario usuario = usuarioRepository.findById(1).get();
```

### Problema 3: Acceso incorrecto a relaciones JPA

**Síntoma:**
```
error: cannot find symbol
  symbol:   method getIdServicio()
```

**Solución:**
```java
// ❌ INCORRECTO
c.getIdServicio()

// ✅ CORRECTO
c.getServicio().getId()
```

---

## ✨ Resumen

Para mantener el proyecto 100% compatible con Java 8:

| Aspecto | Regla |
|---------|-------|
| **Streams** | Usar `.collect(Collectors.toList())` NO `.toList()` |
| **Tipos** | Especificar tipo `List<>` NO `var` |
| **JPA** | Acceder `objeto.getId()` NO `getId()` directo |
| **Optional** | Usar `.isPresent()`, `.orElse()` |
| **Verificación** | `mvn clean compile` antes de commit |

---

## 📞 Soporte

Si encuentras código que no compila:

1. Verifica que sea Java 8 compatible
2. Consulta esta guía o `JAVA8_COMPATIBILITY_VERIFICATION.md`
3. Ejecuta `get_errors()` para ver detalles
4. Busca patrones en `src/main/java` con los scripts anteriores

---

**Documento de Referencia:** Mantención Java 8  
**Última Actualización:** 14 de Noviembre de 2025  
**Status:** ✅ GUÍA ACTIVA - Seguir estas reglas para evitar incompatibilidades

