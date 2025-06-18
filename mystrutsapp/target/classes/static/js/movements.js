document.addEventListener("DOMContentLoaded", () => {
  loadMovements();

  document.getElementById("movementForm").addEventListener("submit", async (e) => {
    e.preventDefault();
            // Obtener fecha en formato correcto
            const now = new Date().toISOString().slice(0, 19) + "Z";
    const movement = {
      movementId: "M" + crypto.randomUUID(),
      date: now, // Ahora con formato ISO correcto
      productId: document.getElementById("productId").value,
      movementType: document.getElementById("movementType").value,
      quantity: parseInt(document.getElementById("quantity").value),
      orderId: "O" + crypto.randomUUID(),
      notes: document.getElementById("notes").value
    };

    await apiFetch("http://localhost:8080/inventory-movement", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(movement),
    });
    hideForm();
    loadMovements();
  });
});

function showForm() {
  document.getElementById("formContainer").classList.remove("hidden");
}

function hideForm() {
  document.getElementById("formContainer").classList.add("hidden");
}

async function loadMovements() {
  const res = await apiFetch("http://localhost:8080/inventory-movement");
  const data = await res.json();
  const tbody = document.querySelector("#movementTable tbody");
  tbody.innerHTML = "";
  data.forEach(m => {
    const row = `<tr>
      <td>${m.movementId}</td><td>${m.date}</td><td>${m.productId}</td>
      <td>${m.movementType}</td><td>${m.quantity}</td><td>${m.orderId}</td><td>${m.notes}</td>
      <td><button onclick="deleteMovement('${m.movementId}')">Delete</button></td>
    </tr>`;
    tbody.innerHTML += row;
  });

  try {
      const res = await apiFetch("http://localhost:8080/products");
      const products = await res.json();
      const productSelect = document.getElementById("productId");
      productSelect.innerHTML = ""; // Clear existing options

      products.forEach(product => {
        const option = document.createElement("option");
        option.value = product.productId;
        option.textContent = product.productId; // Display productName in the combo box
        productSelect.appendChild(option);
      });
    } catch (error) {
      console.error("Error loading product options:", error);
    }
}

async function deleteMovement(id) {
  await apiFetch("http://localhost:8080/inventory-movement/" + id, { method: "DELETE" });
  loadMovements();
}
