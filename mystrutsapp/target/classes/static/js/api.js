async function apiFetch(url, options = {}) {
  const token = localStorage.getItem("token");
  if (!options.headers) options.headers = {};
  options.headers["Authorization"] = "Bearer " + token;
  return fetch(url, options);
}
