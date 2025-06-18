
# Equipo del Proyecto

| Nombre y Apellido         | Legajo    | Rol en el Proyecto             |
|---------------------------|----------|--------------------------------|
| Flavia Yanina Andrada    | 26364164  | Jefe de Proyecto (**JP**)      |
| Juan Marcos Fernandez    | 42426551  | Arquitecto de Software (**AS**) |
| Matias Pedro Giusti      | 419902777 | Desarrollador Java (**DJ**)    |
| Ezequiel Sebastian Godoy | 29846656  | Líder Técnico (**LT**)         |
| Rodrigo Cayoja           | 27890344  | Desarrollador Java (**DJ**)    |
| Fernando Martin Serrano  | 38200044  | Analista de Negocio (**AN**)   |


# App-Product

Aplicación relacionada con la gestión para poder llevar la contabilidad del stock de la organización.

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalados los siguientes software:

- **Java JDK 21 o superior**
- **Maven**

## Instalación

Sigue estos pasos para clonar el repositorio y levantar la aplicación localmente.

### **Clonar el repositorio**
 **https://github.com/brendsanchez/app-users.git **


## Acceso a la Base de Datos H2

La aplicación utiliza una base de datos **H2 en memoria**. Puedes acceder a la consola de administración en la siguiente URL:

🔗 **[Acceder a la consola de H2](http://localhost:8080/h2-console)**

### **Credenciales de acceso**
| Parámetro        | Valor                |
|------------------|----------------------|
| **Driver Class** | `org.h2.Driver`      |
| **JDBC URL**     | `jdbc:h2:mem:userdb` |
| **User Name**    | `SA`                 |
| **Password**     | `password`           |


## Diagrama de solucion

Diagrama para crear un nuevo usuario


                  +----------------+
                  |    WEBAPP      |
                  |  (Frontend)    |
                  +----------------+
                          |
                          v
                  +----------------+
                  |   Security     |
                  | (Spring Security |
                  | + JWT Auth)     |
                  +----------------+
                          |
                          v
                  +----------------+
                  |   Inventory    |
                  | (Gestión de Stock) |
                  |  CRUD Operations  |
                  +----------------+
                          |
                          v
                  +----------------+
                  |      H2        |
                  | (Base de Datos) |
                  |  En Memoria     |
                  +----------------+

