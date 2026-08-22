import { useEffect, useState } from 'react';
import axios from 'axios';

const DATABASES = [
    { id: 'db-prod', nombre: 'DB Producción (Real)', param: 'real' },
    { id: 'db-hr', nombre: 'DB Recursos Humanos', param: 'procesos' },
    { id: 'db-fin', nombre: 'DB Finanzas', param: 'memoria' },
    { id: 'db-dev', nombre: 'DB Desarrollo', param: 'archivos' }
];

function App() {
    const [resultados, setResultados] = useState([]);
    const [loading, setLoading] = useState(true);

    // Estados para el Modal de Detalle
    const [selectedDb, setSelectedDb] = useState(null);
    const [detalles, setDetalles] = useState({ procesos: null, memoria: null, archivos: null });
    const [loadingDetails, setLoadingDetails] = useState(false);

    const fetchAllDatabases = async () => {
        setLoading(true);
        try {
            const promesas = DATABASES.map(async (db) => {
                const res = await axios.get(`http://localhost:8080/api/test/salud?escenario=${db.param}`);
                return { ...db, salud: res.data };
            });
            const data = await Promise.all(promesas);
            setResultados(data);
        } catch (err) {
            console.error("Error consultando bases de datos", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAllDatabases();
    }, []);

    // Función para abrir el modal y consultar las métricas específicas del escenario
    const abrirDetalle = async (db) => {
        setSelectedDb(db);
        setLoadingDetails(true);
        try {
            const [resProc, resMem, resFile] = await Promise.all([
                axios.get(`http://localhost:8080/api/test/procesos?escenario=${db.param}`),
                axios.get(`http://localhost:8080/api/test/memoria?escenario=${db.param}`),
                axios.get(`http://localhost:8080/api/test/archivos?escenario=${db.param}`)
            ]);
            setDetalles({
                procesos: resProc.data,
                memoria: resMem.data,
                archivos: resFile.data
            });
        } catch (err) {
            console.error("Error al cargar detalles de la base de datos", err);
        } finally {
            setLoadingDetails(false);
        }
    };

    const cerrarModal = () => {
        setSelectedDb(null);
        setDetalles({ procesos: null, memoria: null, archivos: null });
    };

    return (
        <div className="min-h-screen bg-slate-100 p-8 font-sans">
            <div className="max-w-7xl mx-auto">
                <h1 className="text-3xl font-bold text-slate-800 mb-2">Centro de Monitoreo Multi-Instancia</h1>
                <p className="text-slate-500 mb-8">Haz clic en cualquier tarjeta para ver el desglose técnico y la causa raíz del rendimiento.</p>

                {loading ? (
                    <p className="text-slate-500 animate-pulse">Analizando instancias...</p>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                        {resultados.map((item) => (
                            <div
                                key={item.id}
                                onClick={() => abrirDetalle(item)}
                                className="bg-white rounded-xl shadow p-6 border-t-4 border-indigo-600 cursor-pointer hover:shadow-xl transition transform hover:-translate-y-1"
                            >
                                <h2 className="text-lg font-bold text-slate-700">{item.nombre}</h2>
                                <p className="text-sm text-slate-500 mb-4">Índice Global</p>

                                <div className={`text-5xl font-black mb-2 ${item.salud.indiceSalud >= 90 ? 'text-green-600' : item.salud.indiceSalud >= 60 ? 'text-yellow-500' : 'text-red-600'}`}>
                                    {item.salud.indiceSalud}
                                </div>

                                <span className="px-3 py-1 rounded-full bg-slate-100 text-slate-700 text-sm font-semibold">
                  {item.salud.estado}
                </span>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* VENTANA MODAL / DRILL-DOWN */}
            {selectedDb && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-2xl max-w-3xl w-full p-6 shadow-2xl overflow-y-auto max-h-[90vh]">
                        <div className="flex justify-between items-center mb-6 border-b pb-4">
                            <div>
                                <h2 className="text-2xl font-bold text-slate-800">Diagnóstico Detallado</h2>
                                <p className="text-slate-500 text-sm">{selectedDb.nombre}</p>
                            </div>
                            <button
                                onClick={cerrarModal}
                                className="text-slate-400 hover:text-slate-600 font-bold text-xl px-3 py-1 rounded-lg bg-slate-100"
                            >
                                ✕
                            </button>
                        </div>

                        {loadingDetails ? (
                            <p className="text-center py-12 text-slate-500 animate-pulse">Cargando desglose de variables técnicas...</p>
                        ) : (
                            <div className="space-y-6">

                                {/* 1. SECCIÓN PROCESOS ($p_n$) */}
                                <div className="bg-slate-50 p-4 rounded-xl border border-slate-200">
                                    <h3 className="font-bold text-indigo-700 mb-3 flex items-center justify-between">
                                        <span>Métricas de Procesos ($p_n$)</span>
                                    </h3>
                                    {detalles.procesos && (
                                        <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
                                            <div className="bg-white p-3 rounded shadow-sm">
                                                <span className="text-slate-400 block text-xs">Procesos Actuales ($p_1$)</span>
                                                <span className="font-bold text-slate-700 text-lg">{detalles.procesos.procesosActuales}</span>
                                            </div>
                                            <div className="bg-white p-3 rounded shadow-sm">
                                                <span className="text-slate-400 block text-xs">Límite de Procesos ($p_2$)</span>
                                                <span className="font-bold text-slate-700 text-lg">{detalles.procesos.limiteProcesos}</span>
                                            </div>
                                            <div className={`bg-white p-3 rounded shadow-sm ${detalles.procesos.sesionesBloqueadas > 0 ? 'border-2 border-red-400 bg-red-50' : ''}`}>
                                                <span className="text-slate-400 block text-xs">Sesiones Bloqueadas ($p_4$)</span>
                                                <span className={`font-bold text-lg ${detalles.procesos.sesionesBloqueadas > 0 ? 'text-red-600' : 'text-slate-700'}`}>
                          {detalles.procesos.sesionesBloqueadas} {detalles.procesos.sesionesBloqueadas > 0 ? '🔴' : '✅'}
                        </span>
                                            </div>
                                        </div>
                                    )}
                                </div>

                                {/* 2. SECCIÓN MEMORIA ($m_n$) */}
                                <div className="bg-slate-50 p-4 rounded-xl border border-slate-200">
                                    <h3 className="font-bold text-indigo-700 mb-3">Métricas de Memoria ($m_n$)</h3>
                                    {detalles.memoria && (
                                        <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
                                            <div className="bg-white p-3 rounded shadow-sm">
                                                <span className="text-slate-400 block text-xs">PGA Cache Hit Ratio ($m_1$)</span>
                                                <span className="font-bold text-slate-700 text-lg">{detalles.memoria.pgaCacheHit}%</span>
                                            </div>
                                            <div className={`bg-white p-3 rounded shadow-sm ${detalles.memoria.pgaOverAllocation > 0 ? 'border-2 border-red-400 bg-red-50' : ''}`}>
                                                <span className="text-slate-400 block text-xs">PGA Over Allocation ($m_2$)</span>
                                                <span className={`font-bold text-lg ${detalles.memoria.pgaOverAllocation > 0 ? 'text-red-600' : 'text-slate-700'}`}>
                          {detalles.memoria.pgaOverAllocation} {detalles.memoria.pgaOverAllocation > 0 ? '⚠️' : '✅'}
                        </span>
                                            </div>
                                        </div>
                                    )}
                                </div>

                                {/* 3. SECCIÓN ARCHIVOS ($a_n$) */}
                                <div className="bg-slate-50 p-4 rounded-xl border border-slate-200">
                                    <h3 className="font-bold text-indigo-700 mb-3">Métricas de Archivos ($a_n$)</h3>
                                    {detalles.archivos && (
                                        <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
                                            <div className={`bg-white p-3 rounded shadow-sm ${detalles.archivos.datafilesOffline > 0 ? 'border-2 border-red-400 bg-red-50' : ''}`}>
                                                <span className="text-slate-400 block text-xs">Datafiles Offline ($a_1$)</span>
                                                <span className={`font-bold text-lg ${detalles.archivos.datafilesOffline > 0 ? 'text-red-600' : 'text-slate-700'}`}>
                          {detalles.archivos.datafilesOffline} {detalles.archivos.datafilesOffline > 0 ? '❌' : '✅'}
                        </span>
                                            </div>
                                            <div className={`bg-white p-3 rounded shadow-sm ${detalles.archivos.redoLogsProblemas > 0 ? 'border-2 border-red-400 bg-red-50' : ''}`}>
                                                <span className="text-slate-400 block text-xs">Redo Logs con Problemas ($a_2$)</span>
                                                <span className={`font-bold text-lg ${detalles.archivos.redoLogsProblemas > 0 ? 'text-red-600' : 'text-slate-700'}`}>
                          {detalles.archivos.redoLogsProblemas} {detalles.archivos.redoLogsProblemas > 0 ? '❌' : '✅'}
                        </span>
                                            </div>
                                        </div>
                                    )}
                                </div>

                                <div className="pt-2 text-center">
                                    <button
                                        onClick={cerrarModal}
                                        className="bg-slate-800 hover:bg-slate-900 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                                    >
                                        Cerrar Diagnóstico
                                    </button>
                                </div>

                            </div>
                        )}
                    </div>
                </div>
            )}

        </div>
    );
}

export default App;
