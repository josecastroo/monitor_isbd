import { useEffect, useState } from 'react';
import axios from 'axios';

function App() {
  const [salud, setSalud] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Hacemos la petición al backend en cuanto carga la página
    axios.get('http://localhost:8080/api/test/salud')
        .then(response => {
          setSalud(response.data);
        })
        .catch(err => {
          console.error("Error conectando al backend:", err);
          setError("No se pudo conectar al backend. Verifica que Spring Boot esté corriendo.");
        });
  }, []);

  return (
      <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl shadow-xl p-8 max-w-sm w-full text-center">
          <h1 className="text-2xl font-bold text-slate-800 mb-6">Monitor ISBD</h1>

          {error && <p className="text-red-500 font-medium">{error}</p>}

          {salud ? (
              <div>
                <p className="text-slate-500 text-sm uppercase tracking-wider mb-2">Índice Global</p>
                <p className="text-6xl font-black text-indigo-600 mb-2">
                  {salud.indiceSalud}
                </p>
                <span className="inline-block px-4 py-1 rounded-full bg-green-100 text-green-800 font-semibold text-lg">
              {salud.estado}
            </span>
              </div>
          ) : (
              !error && <p className="text-slate-500 animate-pulse">Consultando a Oracle...</p>
          )}
        </div>
      </div>
  );
}

export default App;
