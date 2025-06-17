document.addEventListener("DOMContentLoaded", () => {
  loadProducts();
  document.getElementById("productForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const product = {
      productId: document.getElementById("productId").value,
      productName: document.getElementById("productName").value,
      sku: document.getElementById("sku").value,
      salePrice: parseFloat(document.getElementById("price").value),
      category: document.getElementById("category").value,
      cost: 0, unitOfMeasure: "Unit", location: "A1", active: true
    };
    await apiFetch("http://localhost:8080/products", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(product),
    });
    hideForm(); loadProducts();
  });
});
function showForm() { document.getElementById("formContainer").classList.remove("hidden"); }
function hideForm() { document.getElementById("formContainer").classList.add("hidden"); }
async function loadProducts() {
  const res = await apiFetch("http://localhost:8080/products");
  const data = await res.json();
  const tbody = document.querySelector("#productsTable tbody");
  tbody.innerHTML = "";
  data.forEach(p => {
    const row = `<tr>
      <td>${p.productId}</td><td>${p.productName}</td><td>${p.sku}</td>
      <td>$${p.salePrice.toFixed(2)}</td><td>${p.category}</td>
      <td><button onclick="deleteProduct('${p.productId}')">Delete</button></td>
    </tr>`;
    tbody.innerHTML += row;
  });
}
async function deleteProduct(id) {
  await apiFetch("http://localhost:8080/products/" + id, { method: "DELETE" });
  loadProducts();
}
