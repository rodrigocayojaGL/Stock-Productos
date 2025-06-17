document.addEventListener("DOMContentLoaded", () => {
  loadMovements();

  document.getElementById("movementForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const movement = {
      movementId: document.getElementById("movementId").value,
      date: document.getElementById("date").value,
      productId: document.getElementById("productId").value,
      movementType: document.getElementById("movementType").value,
      quantity: parseInt(document.getElementById("quantity").value),
      orderId: document.getElementById("orderId").value,
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
}

async function deleteMovement(id) {
  await apiFetch("http://localhost:8080/inventory-movement/" + id, { method: "DELETE" });
  loadMovements();
}
