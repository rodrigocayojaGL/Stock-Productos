document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ predictor.js cargado correctamente");
    loadPredictor();
    loadPredictorBarGraphic();
    loadPredictionGraphic();

    document.getElementById("predictorForm").addEventListener("submit", async (e) => {
        e.preventDefault();

        // Obtener el token antes de enviar la solicitud
        const token = localStorage.getItem("token");

        if (!token) {
            alert("⚠ No estás autenticado. Inicia sesión primero.");
            return;
        }

        // Obtener fecha en formato correcto
        const now = new Date().toISOString().slice(0, 19) + "Z";

        const prediction = {
            id: parseInt(document.getElementById("id").value),
            date: now, // Ahora con formato ISO correcto
            productId: document.getElementById("productId").value,
            unitsSold: parseInt(document.getElementById("unitsSold").value),
            avgSalePrice: parseFloat(document.getElementById("avgSalePrice").value),
            promotionActive: document.getElementById("promotionActive").checked,
            specialEvent: document.getElementById("specialEvent").value
        };

        try {
            const response = await fetch("http://localhost:8080/predictor-stocks", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(prediction),
            });

            if (!response.ok) throw new Error(`⚠ API Error: ${response.status} ${response.statusText}`);

            const data = await response.json();
            console.log("✅ API Response:", data);

            hideForm();
            loadPredictor();
            loadPredictorBarGraphic();
            loadPredictionGraphic();
        } catch (error) {
            console.error(error);
            alert("⚠ Error al guardar la predicción. Verifica la API.");
        }
    });
});

// **Mostrar formulario**
function showForm() {
    const formContainer = document.getElementById("formContainer");
    if (formContainer) {
        formContainer.classList.remove("hidden");
        formContainer.style.opacity = "1"; // Transición suave
    }
}

// **Ocultar formulario**
function hideForm() {
    const formContainer = document.getElementById("formContainer");
    if (formContainer) {
        formContainer.style.opacity = "0"; // Transición suave
        setTimeout(() => {
            formContainer.classList.add("hidden");
        }, 300); // Espera para ocultar
    }
}

// **Abrir formulario con datos precargados**
function openPredictorForm(productId) {
    document.getElementById("productId").value = productId;
    showForm();
    document.getElementById("date").value = new Date().toISOString().slice(0, 16);
}
// **Cargar gráfico de barras (Histórico de ventas)**
async function loadPredictorBarGraphic() {
    const res = await apiFetch("http://localhost:8080/predictor-stocks");
    const data = await res.json();

    const ctx = document.getElementById("unitsSoldChart").getContext("2d");
    const labels = data.map(p => p.date.split("T")[0]);
    const unitsSold = data.map(p => p.unitsSold);

    if (window.unitsSoldBarChart) {
        window.unitsSoldBarChart.destroy();
    }

    window.unitsSoldBarChart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "Units Sold",
                data: unitsSold,
                backgroundColor: "rgba(0, 123, 255, 0.5)",
                borderColor: "blue",
                borderWidth: 1,
            }],
        },
        options: {
            responsive: true,
            plugins: { legend: { display: true } },
            scales: { y: { beginAtZero: true } },
        },
    });
}

// **Cargar gráfico de predicción**
async function loadPredictionGraphic() {
    const res = await apiFetch("http://localhost:8080/predictor-stocks");
    const data = await res.json();

    const ctx = document.getElementById("predictionChart").getContext("2d");
    const labels = data.map(p => p.date.split("T")[0]);
    const unitsSold = data.map(p => p.unitsSold);

    if (unitsSold.length === 0) {
        console.warn("⚠ No hay datos suficientes para hacer la predicción.");
        return;
    }

    console.log("Unidades vendidas:", unitsSold);
    console.log("Etiquetas (Fechas):", labels);

    const avgSales = unitsSold.reduce((a, b) => a + b, 0) / unitsSold.length;
    const predictedNextSales = labels.map(() => avgSales);

    if (typeof window.predictionChart !== "undefined" && window.predictionChart instanceof Chart) {
        window.predictionChart.destroy();
    }

    window.predictionChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: labels,
            datasets: [{
                label: "Ventas Reales",
                data: unitsSold,
                borderColor: "red",
                borderWidth: 2,
            }, {
                label: "Predicción",
                data: predictedNextSales,
                borderColor: "blue",
                borderWidth: 2,
                borderDash: [5, 5],
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: true } },
            scales: { y: { beginAtZero: true } },
        }
    });
}

async function deletePredictor(id) {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("⚠ No estás autenticado. Inicia sesión primero.");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/predictor-stocks/${id}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` },
        });

        if (!response.ok) throw new Error(`⚠ API Error: ${response.status} ${response.statusText}`);

        console.log(`🗑️ Predicción con ID ${id} eliminada correctamente.`);
        loadPredictor(); // Refresca la tabla después de eliminar el registro
        loadPredictorBarGraphic();
        loadPredictionGraphic();
    } catch (error) {
        console.error(error);
        alert("⚠ Error al eliminar la predicción. Verifica la API.");
    }
}

// **Cargar la tabla de predicciones con el botón "Delete" funcional**
async function loadPredictor() {
    const res = await apiFetch("http://localhost:8080/predictor-stocks");
    const data = await res.json();
    const tbody = document.querySelector("#predictorTable tbody");
    tbody.innerHTML = "";

    data.forEach(p => {
        const row = `<tr>
            <td>${p.id}</td><td>${p.date}</td><td>${p.productId}</td><td>${p.unitsSold}</td>
            <td>${p.avgSalePrice}</td><td>${p.promotionActive}</td><td>${p.specialEvent}</td>
            <td>
                <button onclick="deletePredictor(${p.id})">🗑️ Delete</button>
            </td>
        </tr>`;
        tbody.innerHTML += row;
    });

    loadPredictorBarGraphic();
    loadPredictionGraphic();
}

// **Abrir formulario con datos precargados**
function openPredictorForm(productId) {
    document.getElementById("productId").value = productId;
    document.getElementById("formContainer").classList.remove("hidden");
    document.getElementById("date").value = new Date().toISOString().slice(0, 16);
}
