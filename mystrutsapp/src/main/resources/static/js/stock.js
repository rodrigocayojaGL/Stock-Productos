document.addEventListener("DOMContentLoaded", () => {
  loadStock();

  document.getElementById("stockForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const stock = {
      id: parseInt(document.getElementById("id").value),
      productId: document.getElementById("productId").value,
      quantity: parseInt(document.getElementById("quantity").value),
      lastUpdated: document.getElementById("lastUpdated").value,
      totalInventoryCost: parseFloat(document.getElementById("totalInventoryCost").value)
    };

    await apiFetch("http://localhost:8080/current-stock", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(stock),
    });
    hideForm();
    loadStock();
  });
});

function showForm() {
  document.getElementById("formContainer").classList.remove("hidden");
}

function hideForm() {
  document.getElementById("formContainer").classList.add("hidden");
}

async function loadStock() {
  const res = await apiFetch("http://localhost:8080/current-stock");
  const data = await res.json();
  const tbody = document.querySelector("#stockTable tbody");
  tbody.innerHTML = "";
  data.forEach(s => {
    const row = `<tr>
      <td>${s.id}</td><td>${s.productId}</td><td>${s.quantity}</td>
      <td>${s.lastUpdated}</td><td>${s.totalInventoryCost}</td>
      <td><button onclick="deleteStock(${s.id})">Delete</button></td>
    </tr>`;
    tbody.innerHTML += row;
  });
}

async function deleteStock(id) {
  await apiFetch("http://localhost:8080/current-stock/" + id, { method: "DELETE" });
  loadStock();
}
