import { useEffect, useState } from 'react';
import axios from 'axios';

const DATABASES = [
    { id: 'db-prod', nombre: 'Mi DB', param: 'real' },
    { id: 'db-opt', nombre: 'Google', param: 'optimo' },
    { id: 'db-sal', nombre: 'Ministerio de Educación', param: 'saludable' },
    { id: 'db-adv', nombre: 'Microsoft', param: 'advertencia' },
    { id: 'db-deg', nombre: 'P&G', param: 'degradado' },
    { id: 'db-cap', nombre: 'Amazon', param: 'critico_cap' },
    { id: 'db-real', nombre: 'Tesla', param: 'critico_real' }
];

// Helper para convertir el valor a un solo decimal
const formatNum = (val) => {
    if (val === null || val === undefined) return '0.0';
    return Number(val).toFixed(1);
};

// Helper para el índice global y de áreas (0-100)
const getEstadoInfo = (score) => {
    const num = Number(score) || 0;
    if (num >= 90) return { text: 'Óptimo', color: 'text-green-600', bg: 'bg-green-100', border: 'border-green-500' };
    if (num >= 75) return { text: 'Saludable', color: 'text-emerald-500', bg: 'bg-emerald-100', border: 'border-emerald-500' };
    if (num >= 60) return { text: 'Advertencia', color: 'text-yellow-500', bg: 'bg-yellow-100', border: 'border-yellow-500' };
    if (num >= 40) return { text: 'Degradado', color: 'text-orange-500', bg: 'bg-orange-100', border: 'border-orange-500' };
    return { text: 'Crítico', color: 'text-red-600', bg: 'bg-red-100', border: 'border-red-500' };
};

// Helper para evaluar las métricas individuales
const getMetricBadge = (type, value) => {
    const val = Number(value);
    if (['bloqueos', 'offline', 'invalidos', 'inaccesibles', 'overAlloc'].includes(type)) {
        return val === 0
            ? { text: 'Óptimo', css: 'bg-green-100 text-green-700' }
            : { text: 'Crítico', css: 'bg-red-100 text-red-700' };
    }
    if (type === 'prolongadas') {
        return val === 0
            ? { text: 'Óptimo', css: 'bg-green-100 text-green-700' }
            : { text: 'Advertencia', css: 'bg-yellow-100 text-yellow-700' };
    }
    if (type === 'uso') {
        if (val < 80) return { text: 'Óptimo', css: 'bg-green-100 text-green-700' };
        if (val < 95) return { text: 'Advertencia', css: 'bg-yellow-100 text-yellow-700' };
        return { text: 'Crítico', css: 'bg-red-100 text-red-700' };
    }
    if (type === 'cache') {
        if (val >= 90) return { text: 'Óptimo', css: 'bg-green-100 text-green-700' };
        if (val >= 75) return { text: 'Advertencia', css: 'bg-yellow-100 text-yellow-700' };
        return { text: 'Crítico', css: 'bg-red-100 text-red-700' };
    }
    return null;
};

// Componente reutilizable para cada métrica con Tooltip y Badge
const MetricItem = ({ label, tooltip, value, evalType }) => {
    const badge = evalType ? getMetricBadge(evalType, value) : null;

    return (
        <div className="bg-white p-3 rounded-lg shadow-sm border border-slate-100 flex flex-col justify-between">
            <div className="group relative inline-block w-fit">
        <span className="text-slate-400 block text-xs cursor-help border-b border-dashed border-slate-300 pb-[1px] mb-1">
          {label}
        </span>
                <div className="pointer-events-none absolute bottom-full left-0 mb-2 w-48 opacity-0 transition-opacity duration-200 group-hover:opacity-100 z-10 bg-slate-800 text-slate-100 text-[11px] leading-relaxed rounded-md py-2 px-3 shadow-xl">
                    {tooltip}
                    <div className="absolute left-4 -bottom-1.5 border-[6px] border-transparent border-t-slate-800"></div>
                </div>
            </div>
            <div className="flex items-center mt-1">
                <span className="font-bold text-slate-700 text-base">{value}</span>
                {badge && (
                    <span className={`ml-2 px-2 py-[2px] rounded text-[10px] font-bold tracking-wide ${badge.css}`}>
            {badge.text}
          </span>
                )}
            </div>
        </div>
    );
};

function App() {
    const [resultados, setResultados] = useState([]);
    const [loading, setLoading] = useState(true);

    const [selectedDb, setSelectedDb] = useState(null);
    const [selectedArea, setSelectedArea] = useState(null);
    const [detalles, setDetalles] = useState({ procesos: null, memoria: null, archivos: null });
    const [loadingDetails, setLoadingDetails] = useState(false);

    useEffect(() => {
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
        fetchAllDatabases();
    }, []);

    const abrirDetalle = async (db) => {
        setSelectedDb(db);
        setSelectedArea(null);
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
        setSelectedArea(null);
        setDetalles({ procesos: null, memoria: null, archivos: null });
    };

    return (
        <div className="min-h-screen bg-slate-100 p-8 font-sans">
            <div className="max-w-7xl mx-auto">
                <h1 className="text-3xl font-bold text-slate-800 mb-2">Monitor de bases de datos</h1>
                <p className="text-slate-500 mb-8">Seleccione una instancia para analizar sus componentes principales.</p>

                {loading ? (
                    <p className="text-slate-500 animate-pulse">Analizando instancias...</p>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6">
                        {resultados.map((item) => {
                            const globalScore = item.salud.isbd !== undefined ? item.salud.isbd : item.salud.indiceSalud;
                            const estado = getEstadoInfo(globalScore);
                            return (
                                <div
                                    key={item.id}
                                    onClick={() => abrirDetalle(item)}
                                    className={`bg-white rounded-xl shadow p-6 border-t-4 ${estado.border} cursor-pointer hover:shadow-xl transition transform hover:-translate-y-1 flex flex-col justify-between`}
                                >
                                    <h2 className="text-lg font-bold text-slate-700 mb-4">{item.nombre}</h2>

                                    <div>
                                        <div className={`text-5xl font-black mb-2 ${estado.color}`}>
                                            {formatNum(globalScore)}
                                        </div>

                                        <span className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${estado.bg} ${estado.color}`}>
                      {item.salud.estado}
                    </span>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>

            {/* VENTANA MODAL / DRILL-DOWN */}
            {selectedDb && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-2xl max-w-4xl w-full p-6 shadow-2xl overflow-y-auto max-h-[90vh]">

                        {/* CABECERA DEL MODAL */}
                        <div className="flex justify-between items-center mb-6 border-b pb-4">
                            <div>
                                <h2 className="text-2xl font-bold text-slate-800">
                                    {selectedArea ? `Métricas de ${selectedArea.charAt(0).toUpperCase() + selectedArea.slice(1)}` : 'Desglose por Áreas'}
                                </h2>
                                <p className="text-slate-500 text-sm">{selectedDb.nombre}</p>
                            </div>
                            <div className="flex space-x-3">
                                {selectedArea && (
                                    <button
                                        onClick={() => setSelectedArea(null)}
                                        className="text-indigo-600 hover:text-indigo-800 font-semibold px-4 py-1 rounded-lg bg-indigo-50 hover:bg-indigo-100 transition-colors"
                                    >
                                        ← Volver a Áreas
                                    </button>
                                )}
                                <button
                                    onClick={cerrarModal}
                                    className="text-slate-400 hover:text-slate-600 font-bold text-xl px-3 py-1 rounded-lg bg-slate-100"
                                >
                                    ✕
                                </button>
                            </div>
                        </div>

                        {loadingDetails ? (
                            <p className="text-center py-12 text-slate-500 animate-pulse">Cargando diagnóstico técnico...</p>
                        ) : (
                            <div className="space-y-6">

                                {/* VISTA DE LAS 3 ÁREAS */}
                                {!selectedArea && (
                                    <div>
                                        <p className="text-slate-500 mb-4">Seleccione un área para observar las variables técnicas de origen.</p>
                                        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

                                            {(() => {
                                                const score = selectedDb.salud.indiceProcesos;
                                                const est = getEstadoInfo(score);
                                                return (
                                                    <div onClick={() => setSelectedArea('procesos')} className={`p-5 rounded-xl border-2 ${est.border} cursor-pointer hover:bg-slate-50 transition-colors`}>
                                                        <h3 className="font-bold text-slate-700 text-lg mb-2">Procesos</h3>
                                                        <div className={`text-4xl font-black mb-2 ${est.color}`}>{formatNum(score)}</div>
                                                        <span className={`px-2 py-1 rounded text-xs font-bold ${est.bg} ${est.color}`}>{est.text}</span>
                                                    </div>
                                                );
                                            })()}

                                            {(() => {
                                                const score = selectedDb.salud.indiceMemoria;
                                                const est = getEstadoInfo(score);
                                                return (
                                                    <div onClick={() => setSelectedArea('memoria')} className={`p-5 rounded-xl border-2 ${est.border} cursor-pointer hover:bg-slate-50 transition-colors`}>
                                                        <h3 className="font-bold text-slate-700 text-lg mb-2">Memoria</h3>
                                                        <div className={`text-4xl font-black mb-2 ${est.color}`}>{formatNum(score)}</div>
                                                        <span className={`px-2 py-1 rounded text-xs font-bold ${est.bg} ${est.color}`}>{est.text}</span>
                                                    </div>
                                                );
                                            })()}

                                            {(() => {
                                                const score = selectedDb.salud.indiceArchivos;
                                                const est = getEstadoInfo(score);
                                                return (
                                                    <div onClick={() => setSelectedArea('archivos')} className={`p-5 rounded-xl border-2 ${est.border} cursor-pointer hover:bg-slate-50 transition-colors`}>
                                                        <h3 className="font-bold text-slate-700 text-lg mb-2">Archivos</h3>
                                                        <div className={`text-4xl font-black mb-2 ${est.color}`}>{formatNum(score)}</div>
                                                        <span className={`px-2 py-1 rounded text-xs font-bold ${est.bg} ${est.color}`}>{est.text}</span>
                                                    </div>
                                                );
                                            })()}

                                        </div>
                                    </div>
                                )}

                                {/* DETALLES DE PROCESOS */}
                                {selectedArea === 'procesos' && detalles.procesos && (
                                    <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 animate-fade-in">
                                        <h3 className="font-bold text-indigo-700 mb-3">Monitor de Procesos</h3>
                                        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                                            <MetricItem label="Actuales" tooltip="Número de procesos (del sistema y usuarios) que se encuentran actualmente activos en la instancia." value={detalles.procesos.procesosActuales} />
                                            <MetricItem label="Máximos" tooltip="Límite configurado de procesos simultáneos permitidos por la base de datos." value={detalles.procesos.procesosMaximos} />
                                            <MetricItem label="Sesiones Actuales" tooltip="Total de sesiones lógicas conectadas a la base de datos en este instante." value={detalles.procesos.sesionesActuales} />
                                            <MetricItem label="Sesiones Activas" tooltip="Sesiones que están ejecutando comandos SQL de forma activa en la CPU." value={detalles.procesos.sesionesActivas} />
                                            <MetricItem label="Sesiones Inactivas" tooltip="Sesiones conectadas que no están realizando ninguna operación (Idle)." value={detalles.procesos.sesionesInactivas} />
                                            <MetricItem evalType="bloqueos" label="Bloqueadas" tooltip="Sesiones en pausa esperando que otra sesión libere un recurso de la base de datos." value={detalles.procesos.sesionesBloqueadas} />
                                            <MetricItem evalType="prolongadas" label="Op. Prolongadas" tooltip="Operaciones o consultas pesadas que han tardado más de 6 segundos en completarse." value={detalles.procesos.operacionesProlongadas} />
                                            <MetricItem evalType="uso" label="Uso de Recursos" tooltip="Porcentaje global de utilización actual de los procesos respecto al máximo permitido." value={`${formatNum(detalles.procesos.usoRecursos)}%`} />
                                        </div>
                                    </div>
                                )}

                                {/* DETALLES DE MEMORIA */}
                                {selectedArea === 'memoria' && detalles.memoria && (
                                    <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 animate-fade-in">
                                        <h3 className="font-bold text-indigo-700 mb-3">Monitor de Memoria</h3>
                                        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                                            <MetricItem label="Tamaño SGA" tooltip="Cantidad de memoria total asignada al System Global Area, compartida por todos los usuarios." value={detalles.memoria.sgaTotal} />
                                            <MetricItem label="Libre SGA" tooltip="Porción de memoria SGA que se encuentra actualmente disponible para ser utilizada." value={detalles.memoria.sgaLibre} />
                                            <MetricItem label="Shared Pool" tooltip="Memoria utilizada para el caché del diccionario de datos y planes de ejecución de SQL." value={detalles.memoria.sharedPool} />
                                            <MetricItem label="Buffer Cache" tooltip="Memoria destinada a almacenar en caché los bloques de datos leídos directamente del disco." value={detalles.memoria.bufferCache} />
                                            <MetricItem label="PGA Asignada" tooltip="Memoria total asignada al Program Global Area, que es privada para cada sesión." value={detalles.memoria.pgaAsignada} />
                                            <MetricItem label="PGA Utilizada" tooltip="Memoria PGA que está siendo consumida activamente por las sesiones en curso." value={detalles.memoria.pgaUtilizada} />
                                            <MetricItem label="PGA Máxima" tooltip="Valor máximo histórico de memoria PGA que se ha asignado desde que arrancó el sistema." value={detalles.memoria.pgaMaxima} />
                                            <MetricItem evalType="overAlloc" label="Over-Allocation" tooltip="Número de veces que el sistema tuvo que exceder el límite establecido de memoria PGA." value={detalles.memoria.pgaOverAllocation} />
                                            <MetricItem evalType="cache" label="Cache Hit PGA" tooltip="Porcentaje de aciertos en la memoria PGA. Indica qué tan eficiente está siendo el uso de memoria." value={`${formatNum(detalles.memoria.pgaCacheHit)}%`} />
                                        </div>
                                    </div>
                                )}

                                {/* DETALLES DE ARCHIVOS */}
                                {selectedArea === 'archivos' && detalles.archivos && (
                                    <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 animate-fade-in">
                                        <h3 className="font-bold text-indigo-700 mb-3">Monitor de Archivos</h3>
                                        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                                            <MetricItem label="Datafiles Online" tooltip="Número de archivos de datos que están operativos y completamente funcionales." value={detalles.archivos.datafilesOnline} />
                                            <MetricItem evalType="offline" label="Datafiles Offline" tooltip="Archivos de datos que han sido detectados fuera de línea o inaccesibles por la BD." value={detalles.archivos.datafilesOffline} />
                                            <MetricItem label="Tamaño Datafiles" tooltip="Tamaño total ocupado por los archivos de datos físicos, expresado en bytes." value={detalles.archivos.tamanoDatafiles} />
                                            <MetricItem label="Espacio Tablespaces" tooltip="Espacio total de almacenamiento lógico asignado a los tablespaces de la base de datos." value={detalles.archivos.espacioTablespaces} />
                                            <MetricItem label="Tempfiles" tooltip="Archivos temporales utilizados por la BD para operaciones de ordenamiento y consultas masivas." value={detalles.archivos.tempfiles} />
                                            <MetricItem label="Redo Logs" tooltip="Archivos de registro (Redo Logs) utilizados para garantizar la recuperación ante desastres." value={detalles.archivos.redoLogs} />
                                            <MetricItem evalType="invalidos" label="Archivos Inválidos" tooltip="Archivos que han sido marcados con corrupción o algún estado inválido por Oracle." value={detalles.archivos.archivosInvalidos} />
                                            <MetricItem evalType="inaccesibles" label="Archivos Inaccesibles" tooltip="Archivos que el sistema operativo subyacente no permite leer ni escribir debido a permisos o fallos." value={detalles.archivos.archivosInaccesibles} />
                                        </div>
                                    </div>
                                )}

                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

export default App;
