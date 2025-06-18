document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ users.js cargado correctamente");

    checkAuth(); // Verifica la autenticación antes de cargar la página
    loadUsers();

    document.getElementById("userForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        await createUser();
    });
});

// **Verificar autenticación y permisos**
async function checkAuth() {
    const token = localStorage.getItem("token");
    const username = localStorage.getItem("username");

    if (!token || !username) {
        alert("⚠ No estás autenticado. Redirigiendo al login...");
        window.location.href = "login.html";
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/users/${username}`, {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error(`⚠ API Error: ${response.status} ${response.statusText}`);

        const user = await response.json();

        if (!user.roles.includes("ROLE_ADMIN")) {
            alert("⚠ No tienes permisos para acceder a esta página.");
            window.location.href = "dashboard.html";
            return;
        }

        console.log("✅ Acceso permitido: Usuario es administrador.");
    } catch (error) {
        console.error("⚠ Error de autenticación:", error);
        alert("⚠ Hubo un problema con la autenticación.");
        window.location.href = "login.html";
    }
}

// **Cargar la lista de usuarios**
async function loadUsers() {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("⚠ No estás autenticado. Inicia sesión primero.");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/users", {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error(`⚠ API Error: ${response.status} ${response.statusText}`);

        const users = await response.json();
        const tbody = document.querySelector("#usersTable tbody");
        tbody.innerHTML = "";

        users.forEach(user => {
            const deleteBtn = `<button onclick="deleteUser('${user.username}')">❌ Delete</button>`;
            const row = `<tr>
                <td>${user.username}</td>
                <td>${user.roles.join(", ")}</td>
                <td>${deleteBtn}</td>
            </tr>`;
            tbody.innerHTML += row;
        });

    } catch (error) {
        console.error("⚠ Error al cargar usuarios:", error);
    }
}

// **Crear usuario**
async function createUser() {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("⚠ No estás autenticado. Inicia sesión primero.");
        return;
    }

    const userData = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value,
        roles: document.getElementById("roles").value.split(",").map(role => role.trim())
    };

    try {
        const response = await fetch("http://localhost:8080/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(userData),
        });

        if (!response.ok) throw new Error(`⚠ API Error: ${response.status} ${response.statusText}`);

        console.log("✅ Usuario creado:", await response.json());
        hideUserForm();
        loadUsers();
    } catch (error) {
        console.error("⚠ Error al crear usuario:", error);
        alert("⚠ Error al crear usuario. Verifica la API.");
    }
}

// **Eliminar usuario**
async function deleteUser(username) {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("⚠ No estás autenticado. Inicia sesión primero.");
        return;
    }

    const confirmDelete = confirm(`¿Estás seguro de que quieres eliminar al usuario '${username}'?`);
    if (!confirmDelete) return;

    try {
        const response = await fetch(`http://localhost:8080/users/${username}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` },
        });

        if (!response.ok) throw new Error(`⚠ API Error: ${response.status} ${response.statusText}`);

        console.log(`🗑️ Usuario '${username}' eliminado correctamente.`);
        alert("✅ Usuario eliminado con éxito.");
        loadUsers();
    } catch (error) {
        console.error("⚠ Error al eliminar usuario:", error);
        alert("⚠ Error al eliminar usuario. Verifica la API.");
    }
}

// **Mostrar formulario**
function showUserForm() {
    document.getElementById("userFormContainer").classList.remove("hidden");
}

// **Ocultar formulario**
function hideUserForm() {
    document.getElementById("userFormContainer").classList.add("hidden");
}
