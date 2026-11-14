package persistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import modelo.Expediente;
import modelo.FichaConsulta;
import modelo.SolicitudConsulta;
import modelo.TipoMovimiento;
import servicio.EvaluadorCasos;
import servicio.GestorExpedientes;
import servicio.GestorSolicitudes;
import servicio.SistemaEstudio;

/**
 * Datos de prueba precargados del Estudio Jurídico M&M.
 * Simulan el ingreso desde la Landing Page y pasan por el mismo flujo
 * del sistema (cola -> evaluación -> árbol -> pila), por lo que los
 * códigos y estados quedan coherentes.
 */
public final class DatosIniciales {

    private DatosIniciales() {
    }

    public static SistemaEstudio crearSistema() {
        SistemaEstudio sis = new SistemaEstudio();
        GestorSolicitudes gs = sis.getGestorSolicitudes();
        GestorExpedientes ge = sis.getGestorExpedientes();
        EvaluadorCasos ev = sis.getEvaluadorCasos();

        // ---------------- Casos ya aceptados -> expedientes abiertos ----------------
        Expediente e1 = casoAceptado(gs, ev,
                "Rosa María Quispe Huamán", "45879632", "987 654 321",
                "Despido arbitrario y pago de beneficios sociales", "Laboral",
                "20100047218", "Transportes Rápidos del Norte S.A.",
                LocalDateTime.of(2026, 2, 9, 9, 15), 3500, LocalDate.of(2026, 2, 12));
        e1.registrarMovimiento(LocalDate.of(2026, 2, 20), TipoMovimiento.ESCRITO,
                "Presentación de demanda laboral ante el Juzgado de Trabajo", "DEM-0012-2026");
        e1.registrarMovimiento(LocalDate.of(2026, 3, 10), TipoMovimiento.RESOLUCION,
                "Resolución N.° 1: admite a trámite la demanda", "RES-01");
        e1.registrarMovimiento(LocalDate.of(2026, 3, 18), TipoMovimiento.NOTIFICACION,
                "Notificación a la empresa demandada", "CED-2231");
        e1.registrarMovimiento(LocalDate.of(2026, 5, 6), TipoMovimiento.AUDIENCIA,
                "Audiencia de conciliación: sin acuerdo entre las partes", "ACTA-AC-05");
        e1.registrarMovimiento(LocalDate.of(2026, 8, 19), TipoMovimiento.AUDIENCIA,
                "Audiencia de juzgamiento programada", "RES-04");

        Expediente e2 = casoAceptado(gs, ev,
                "Constructora Pacífico Sur S.A.C.", "20601234567", "ventas@pacificosur.pe",
                "Cobro de facturas impagas por suministro de materiales", "Comercial",
                "20523698741", "Ferretería El Constructor S.R.L.",
                LocalDateTime.of(2026, 3, 2, 10, 40), 6800, LocalDate.of(2026, 3, 5));
        e2.registrarMovimiento(LocalDate.of(2026, 3, 16), TipoMovimiento.ESCRITO,
                "Carta notarial de requerimiento de pago", "CN-0331");
        e2.registrarMovimiento(LocalDate.of(2026, 4, 8), TipoMovimiento.ESCRITO,
                "Demanda de obligación de dar suma de dinero", "DEM-0045-2026");
        e2.registrarMovimiento(LocalDate.of(2026, 6, 22), TipoMovimiento.RESOLUCION,
                "Mandato ejecutivo a favor del cliente", "RES-03");
        // Registro erróneo que luego se anuló (queda en la pila de anulados)
        e2.registrarMovimiento(LocalDate.of(2026, 6, 25), TipoMovimiento.NOTIFICACION,
                "Notificación registrada en expediente equivocado", "CED-9999");
        e2.anularUltimoMovimiento();

        Expediente e3 = casoAceptado(gs, ev,
                "Jorge Luis Paredes Salazar", "41236587", "945 112 233",
                "Régimen de visitas y tenencia compartida", "Familia",
                "43215678", "Carmen Rosa Paredes Vega",
                LocalDateTime.of(2026, 4, 14, 16, 5), 2800, LocalDate.of(2026, 4, 16));
        e3.registrarMovimiento(LocalDate.of(2026, 4, 30), TipoMovimiento.ESCRITO,
                "Solicitud de conciliación extrajudicial", "SOL-CONC-77");
        e3.registrarMovimiento(LocalDate.of(2026, 6, 3), TipoMovimiento.AUDIENCIA,
                "Audiencia de conciliación: acuerdo parcial", "ACTA-118");

        Expediente e4 = casoAceptado(gs, ev,
                "Distribuidora Andina E.I.R.L.", "20547896321", "contabilidad@dandina.pe",
                "Reclamo contra resolución de multa tributaria", "Tributario",
                "20131312955", "Superintendencia Nacional de Aduanas y Administración Tributaria",
                LocalDateTime.of(2026, 6, 1, 11, 30), 4200, LocalDate.of(2026, 6, 4));
        e4.registrarMovimiento(LocalDate.of(2026, 6, 15), TipoMovimiento.ESCRITO,
                "Recurso de reclamación presentado", "REC-2026-118");
        e4.registrarMovimiento(LocalDate.of(2026, 9, 2), TipoMovimiento.RESOLUCION,
                "Resolución de intendencia: declara fundado en parte", "RI-0458");

        Expediente e5 = casoAceptado(gs, ev,
                "Lucía Fernanda Torres Ríos", "70214589", "999 321 654",
                "Resolución de contrato de compraventa de departamento", "Civil",
                "20512345678", "Inmobiliaria Los Andes S.A.C.",
                LocalDateTime.of(2026, 9, 7, 8, 50), 5000, LocalDate.of(2026, 9, 9));
        e5.registrarMovimiento(LocalDate.of(2026, 9, 21), TipoMovimiento.ESCRITO,
                "Demanda de resolución de contrato", "DEM-0102-2026");
        e5.registrarMovimiento(LocalDate.of(2026, 10, 14), TipoMovimiento.NOTIFICACION,
                "Notificación de admisión de demanda", "CED-4410");

        // ---------------- Casos que terminaron rechazados / archivados ----------------
        SolicitudConsulta sr = gs.registrarSolicitud("Transportes Rápidos del Norte S.A.", "20100047218",
                "rrhh@trnorte.pe", "Asesoría por demanda laboral de extrabajadora", "Laboral",
                LocalDateTime.of(2026, 9, 15, 12, 0));
        gs.atenderSiguiente();
        ev.recibirFicha(new FichaConsulta(sr, "45879632", "Rosa María Quispe Huamán", "",
                LocalDateTime.of(2026, 9, 15, 12, 20)));
        ev.existeConflicto(ev.fichaActual());
        ev.rechazarCaso(ev.getUltimoMotivoConflicto());

        SolicitudConsulta sa = gs.registrarSolicitud("Mariela Huamán Castro", "47851236",
                "956 778 899", "Asesoría para constitución de empresa", "Comercial",
                LocalDateTime.of(2026, 10, 1, 9, 45));
        gs.atenderSiguiente();
        ev.recibirFicha(new FichaConsulta(sa, "", "", "Interesada en S.A.C. familiar",
                LocalDateTime.of(2026, 10, 1, 10, 0)));
        ev.existeConflicto(ev.fichaActual());
        ev.registrarPropuesta(1500, "Constitución de S.A.C. y trámites registrales", LocalDate.of(2026, 10, 2));
        ev.registrarDecision(false, LocalDate.of(2026, 10, 6));

        // ---------------- Ficha atendida, pendiente de evaluación ----------------
        SolicitudConsulta se = gs.registrarSolicitud("Roberto Carlos Flores Díaz", "40123987",
                "912 345 678", "Defensa por denuncia de apropiación ilícita", "Penal",
                LocalDateTime.of(2026, 11, 2, 9, 5));
        gs.atenderSiguiente();
        ev.recibirFicha(new FichaConsulta(se, "20498765432", "Comercial Lima Norte S.A.C.",
                "Citación fiscal para el 10/11/2026", LocalDateTime.of(2026, 11, 2, 9, 30)));

        // ---------------- Solicitudes pendientes en la cola (orden de llegada) ----------------
        gs.registrarSolicitud("Miguel Ángel Rojas Campos", "46987512", "987 111 222",
                "Despido sin causa justa, 6 años de servicio", "Laboral",
                LocalDateTime.of(2026, 11, 2, 10, 12));
        gs.registrarSolicitud("Inmobiliaria Los Andes S.A.C.", "20512345678", "legal@losandes.pe",
                "Defensa ante demanda de resolución de contrato", "Civil",
                LocalDateTime.of(2026, 11, 2, 11, 40));          // -> conflicto (es contraparte)
        gs.registrarSolicitud("Andrés Felipe Gutiérrez Soto", "72589634", "934 567 890",
                "Indemnización por obra mal ejecutada", "Civil",
                LocalDateTime.of(2026, 11, 2, 15, 25));          // -> conflicto si la contraparte es Constructora Pacífico Sur
        gs.registrarSolicitud("Sofía Valentina Chávez Mendoza", "74125896", "961 222 333",
                "Demanda de alimentos para menor de edad", "Familia",
                LocalDateTime.of(2026, 11, 3, 8, 35));
        gs.registrarSolicitud("Inversiones Santa Rosa S.A.C.", "20609876543", "gerencia@santarosa.pe",
                "Incumplimiento de contrato de arrendamiento comercial", "Comercial",
                LocalDateTime.of(2026, 11, 3, 9, 50));

        return sis;
    }

    /** Recorre el flujo completo del BPMN para un caso que terminó con expediente abierto. */
    private static Expediente casoAceptado(GestorSolicitudes gs, EvaluadorCasos ev,
            String nombre, String id, String contacto, String motivo, String especialidad,
            String idContraparte, String nombreContraparte,
            LocalDateTime llegada, double monto, LocalDate fechaApertura) {
        SolicitudConsulta s = gs.registrarSolicitud(nombre, id, contacto, motivo, especialidad, llegada);
        gs.atenderSiguiente();
        ev.recibirFicha(new FichaConsulta(s, idContraparte, nombreContraparte, "", llegada.plusMinutes(20)));
        ev.existeConflicto(ev.fichaActual());
        LocalDate emision = llegada.toLocalDate().plusDays(1);
        ev.registrarPropuesta(monto, "Patrocinio del caso hasta primera instancia", emision);
        ev.registrarDecision(true, fechaApertura);
        return ev.abrirExpediente(fechaApertura);
    }
}
