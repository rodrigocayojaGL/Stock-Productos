document.addEventListener("DOMContentLoaded", () => {
  loadStock();

  document.getElementById("stockForm").addEventListener("submit", async (e) => {
    e.preventDefault();

      // Obtener fecha en formato correcto
    const now = new Date().toISOString().slice(0, 19) + "Z";
    const stock = {
      id: Math.floor(Date.now() * Math.random()),
      productId: document.getElementById("productId").value,
      quantity: parseInt(document.getElementById("quantity").value),
      lastUpdated:  now, // Ahora con formato ISO correcto
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

async function deleteStock(id) {
  await apiFetch("http://localhost:8080/current-stock/" + id, { method: "DELETE" });
  loadStock();
}
