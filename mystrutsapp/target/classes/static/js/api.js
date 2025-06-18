async function apiFetch(url, options = {}) {
  try {
    const token = localStorage.getItem("token");

    // Inicializar encabezados si no existen
    options.headers = options.headers || {};

    // Agregar token solo si está presente
    if (token) {
      options.headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(url, options);

    // Manejo de errores HTTP
    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(`Error ${response.status}: ${response.statusText} \n${errorMessage}`);
    }

    return response;
  } catch (error) {
    console.error("Error en apiFetch:", error);
    throw error; // Propagar error para que el código que llama esta función pueda manejarlo
  }
}
