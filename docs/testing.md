# Testing y Control de Calidad

Para garantizar la estabilidad a largo plazo y la fiabilidad de la plataforma, se ha generado un código bien estructurado y funcional. La lógica core del proyecto ha sido validada exhaustivamente mediante un entorno de **Pruebas Unitarias (Unit Tests)**, utilizando el estándar de la industria **JUnit 4**.

Esta capa de pruebas asegura que las reglas de negocio críticas y los cálculos matemáticos funcionen con precisión absoluta en la máquina virtual, independientemente de la interfaz gráfica del usuario o de las latencias en la conectividad de red.

### Archivo de Pruebas: `ExampleUnitTest.kt`

Se han implementado tests automatizados destinados a validar el comportamiento del sistema ante diferentes escenarios de entrada posibles:

1. **Lógica de Puntuación (Victorias y Empates):**
   Verifica y garantiza que el algoritmo condicional asigne correctamente 3 puntos a la victoria y 1 al empate, evitando sumatorias erróneas en las clasificaciones.
   https://github.com/juanma-ge/Rivalry/blob/5845d649233d43cfd6f59da4c90a6f59f94c4c61/app/src/test/java/com/example/rivalry/ExampleUnitTest.kt#L10-L18
2. **Lógica de Puntuación (Derrotas):**
   Se asegura de que un equipo reciba exactamente 0 puntos si los goles del rival son estrictamente superiores al finalizar el encuentro.
   https://github.com/juanma-ge/Rivalry/blob/5845d649233d43cfd6f59da4c90a6f59f94c4c61/app/src/test/java/com/example/rivalry/ExampleUnitTest.kt#L37-L43
3. **Control de Aforos en Pachangas:**
   Comprueba de manera condicional el límite máximo de jugadores por cada modalidad deportiva (ej. el sistema debe bloquear las inscripciones en Fútbol 11 tras alcanzar los 22 usuarios registrados).
   https://github.com/juanma-ge/Rivalry/blob/5845d649233d43cfd6f59da4c90a6f59f94c4c61/app/src/test/java/com/example/rivalry/ExampleUnitTest.kt#L29-L35
4. **Validación Preventiva de Correos Electrónicos:**
   Previene llamadas innecesarias e inválidas a los servidores de Firebase comprobando que las cadenas de texto ingresadas contengan la estructura semántica obligatoria (`@` y `.`).
   https://github.com/juanma-ge/Rivalry/blob/5845d649233d43cfd6f59da4c90a6f59f94c4c61/app/src/test/java/com/example/rivalry/ExampleUnitTest.kt#L20-L27
5. **Restricciones de Perfil y Apodos:**
   Fuerza un límite mínimo de 3 caracteres en los apodos para evitar la creación de perfiles anónimos o defectuosos en la plataforma.
   https://github.com/juanma-ge/Rivalry/blob/5845d649233d43cfd6f59da4c90a6f59f94c4c61/app/src/test/java/com/example/rivalry/ExampleUnitTest.kt#L45-L51
