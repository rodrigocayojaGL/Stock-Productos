document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ users.js cargado correctamente");
    loadUsers();

    document.getElementById("userForm").addEventListener("submit", async (e) => {
        e.preventDefault();

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
            console.error(error);
            alert("⚠ Error al crear usuario. Verifica la API.");
        }
    });
});

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
            const deleteBtn = user.roles.includes("ROLE_ADMIN")
                ? `<button onclick="deleteAdminUser('${user.username}')">❌ Delete Admin</button>`
                : `<button onclick="deleteAdminUser('${user.username}')">❌ Delete Admin</button>`;

            const row = `<tr>
                <td>${user.id}</td><td>${user.username}</td><td>${user.roles.join(", ")}</td>
                <td>${deleteBtn}</td>
            </tr>`;
            tbody.innerHTML += row;
        });
    } catch (error) {
        console.error(error);
        alert("⚠ Error al cargar usuarios. Verifica la API.");
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

// **Eliminar usuario administrador**
async function deleteAdminUser(username) {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("⚠ No estás autenticado. Inicia sesión primero.");
        return;
    }

    // Confirmación antes de eliminar
    const confirmDelete = confirm(`¿Estás seguro de que quieres eliminar al usuario '${username}'?`);
    if (!confirmDelete) return;

    try {
        const response = await fetch(`http://localhost:8080/users/${username}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` },
        });

        if (!response.ok) throw new Error(`⚠ API Error: ${response.status} ${response.statusText}`);

        console.log(`🗑️ Usuario administrador '${username}' eliminado correctamente.`);
        alert("✅ Usuario eliminado con éxito.");
        loadUsers(); // Refresca la lista después de eliminar
    } catch (error) {
        console.error(error);
        alert("⚠ Error al eliminar usuario. Verifica la API.");
    }
}
