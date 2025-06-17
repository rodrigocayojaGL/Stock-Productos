document.addEventListener("DOMContentLoaded", () => {
  loadPredictor();

  document.getElementById("predictorForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const prediction = {
      id: parseInt(document.getElementById("id").value),
      date: document.getElementById("date").value,
      productId: document.getElementById("productId").value,
      unitsSold: parseInt(document.getElementById("unitsSold").value),
      avgSalePrice: parseFloat(document.getElementById("avgSalePrice").value),
      promotionActive: document.getElementById("promotionActive").checked,
      specialEvent: document.getElementById("specialEvent").value
    };

    await apiFetch("http://localhost:8080/predictor-stocks", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(prediction),
    });
    hideForm();
    loadPredictor();
  });
});

function showForm() {
  document.getElementById("formContainer").classList.remove("hidden");
}

function hideForm() {
  document.getElementById("formContainer").classList.add("hidden");
}

async function loadPredictor() {
  const res = await apiFetch("http://localhost:8080/predictor-stocks");
  const data = await res.json();
  const tbody = document.querySelector("#predictorTable tbody");
  tbody.innerHTML = "";
  data.forEach(p => {
    const row = `<tr>
      <td>${p.id}</td><td>${p.date}</td><td>${p.productId}</td><td>${p.unitsSold}</td>
      <td>${p.avgSalePrice}</td><td>${p.promotionActive}</td><td>${p.specialEvent}</td>
      <td><button onclick="deletePredictor(${p.id})">Delete</button></td>
    </tr>`;
    tbody.innerHTML += row;
  });
}

async function deletePredictor(id) {
  await apiFetch("http://localhost:8080/predictor-stocks/" + id, { method: "DELETE" });
  loadPredictor();
}
