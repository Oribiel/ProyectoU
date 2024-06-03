package presentacion;

import dtos.AlumnoLecturaDTO;
import dtos.GuardarAlumnoDTO;
import dtos.AlumnoTablaDTO;
import dtos.EditarAlumnoDTO;
import enumerador.OpcionesCRUDEnum;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import negocio.AlumnoNegocio;
import negocio.IAlumnoNegocio;
import negocio.NegocioException;
import utilerias.JButtonCellEditor;
import utilerias.JButtonRenderer;

public class frmCRUD extends javax.swing.JFrame {

    private int pagina = 1;
    private final int LIMITE = 2;
    private IAlumnoNegocio alumnoNegocio;
    private OpcionesCRUDEnum opcionAccionPantalla;
    private AlumnoLecturaDTO alumnoLectura;

    public frmCRUD(IAlumnoNegocio alumnoNegocio) {
        initComponents();

        this.alumnoNegocio = alumnoNegocio;
        this.cargarMetodosIniciales();
    }

    private void cargarMetodosIniciales() {
        this.cargarConfiguracionInicialPantalla();
        this.cargarConfiguracionInicialTablaAlumnos();
        this.cargarAlumnosEnTabla();
        this.opcionAccionPantalla = OpcionesCRUDEnum.LEER;
        btnAcciones.setVisible(false);
        btnCancelar.setVisible(false);
    }

    private void cargarConfiguracionInicialPantalla() {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void cargarConfiguracionInicialTablaAlumnos() {
        ActionListener onEditarClickListener = new ActionListener() {
            final int columnaId = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                //Metodo para editar un alumno
                editar();
            }
        };
        int indiceColumnaEditar = 5;
        TableColumnModel modeloColumnas = this.tblAlumnos.getColumnModel();
        modeloColumnas.getColumn(indiceColumnaEditar)
                .setCellRenderer(new JButtonRenderer("Editar"));
        modeloColumnas.getColumn(indiceColumnaEditar)
                .setCellEditor(new JButtonCellEditor("Editar",
                        onEditarClickListener));

        ActionListener onEliminarClickListener = new ActionListener() {
            final int columnaId = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                //Metodo para eliminar un alumno
                eliminar();
            }
        };
        int indiceColumnaEliminar = 6;
        modeloColumnas = this.tblAlumnos.getColumnModel();
        modeloColumnas.getColumn(indiceColumnaEliminar)
                .setCellRenderer(new JButtonRenderer("Eliminar"));
        modeloColumnas.getColumn(indiceColumnaEliminar)
                .setCellEditor(new JButtonCellEditor("Eliminar",
                        onEliminarClickListener));
    }

    private int getIdSeleccionadoTablaAlumnos() {
        int indiceFilaSeleccionada = this.tblAlumnos.getSelectedRow();
        if (indiceFilaSeleccionada != -1) {
            DefaultTableModel modelo = (DefaultTableModel) this.tblAlumnos.getModel();
            int indiceColumnaId = 0;
            int idSocioSeleccionado = (int) modelo.getValueAt(indiceFilaSeleccionada,
                    indiceColumnaId);
            return idSocioSeleccionado;
        } else {
            return 0;
        }
    }

    private void editar() {
        try {
            //Metodo para regresar el alumno seleccionado
            int id = this.getIdSeleccionadoTablaAlumnos();
            if (id == 0) {
                return;
            }
            this.alumnoLectura = this.alumnoNegocio.obtenerPorId(id);
            this.cargarDatosAPantalla();
            this.opcionAccionPantalla = OpcionesCRUDEnum.EDITAR;
            this.procesoAccionesPantalla();
            this.btnAcciones.setText("Editar");
        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Información", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosAPantalla() {
        txtNombres.setText(this.alumnoLectura.getNombres());
        txtApellidoPaterno.setText(this.alumnoLectura.getApellidoPaterno());
        txtApellidoMaterno.setText(this.alumnoLectura.getApellidoMaterno());
        chkActivo.setSelected(this.alumnoLectura.isActivo());
    }

    private void eliminar() {

    }

   private void llenarTablaAlumnos(List<AlumnoTablaDTO> alumnosLista) {
    DefaultTableModel modeloTabla = (DefaultTableModel) this.tblAlumnos.getModel();

    // Limpiar el modelo de la tabla
    modeloTabla.setRowCount(0);

    if (alumnosLista != null) {
        alumnosLista.forEach(row -> {
            Object[] fila = new Object[7]; // Asegurar que corresponda con la cantidad de columnas
            fila[0] = row.getIdAlumno();
            fila[1] = row.getNombres();
            fila[2] = row.getApellidoPaterno();
            fila[3] = row.getApellidoMaterno();
            fila[4] = row.getEstatus();
            fila[5] = "Editar"; // Agregar columna Editar
            fila[6] = "Eliminar"; // Agregar columna Eliminar

            modeloTabla.addRow(fila);
        });
    }
}

private void cargarAlumnosEnTabla() {
    try {
        List<AlumnoTablaDTO> alumnos = this.alumnoNegocio.buscarAlumnosTabla(this.LIMITE, this.pagina);
        this.llenarTablaAlumnos(alumnos);
    } catch (NegocioException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void procesoGuardar() {
    try {
        this.validarControles();
        this.alumnoNegocio.insertar(this.obtenerControlesGuardar());
        this.cargarAlumnosEnTabla(); // Luego de agregar un nuevo registro, actualizar la tabla
        this.procesoResetearAcciones();
        JOptionPane.showMessageDialog(this, "Se registró el alumno", "Confirmación", JOptionPane.INFORMATION_MESSAGE);
    } catch (NegocioException | IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void procesoEditar() {
    try {
        this.validarControles();
        this.alumnoNegocio.editar(this.obtenerControlesEditar());
        this.cargarAlumnosEnTabla(); // Luego de editar un registro, actualizar la tabla
        this.procesoResetearAcciones();
        JOptionPane.showMessageDialog(this, "Se editó el alumno", "Confirmación", JOptionPane.INFORMATION_MESSAGE);
    } catch (NegocioException | IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void procesoResetearAcciones() {
        this.opcionAccionPantalla = OpcionesCRUDEnum.LEER;
        this.limpiarControles();
        this.procesoAccionesPantalla();
    }

    private void limpiarControles() {
        txtNombres.setText("");
        txtApellidoPaterno.setText("");
        txtApellidoMaterno.setText("");
                chkActivo.setSelected(false);
    }

    private GuardarAlumnoDTO obtenerControlesGuardar() {
        return new GuardarAlumnoDTO(txtNombres.getText(), txtApellidoPaterno.getText(), txtApellidoMaterno.getText());
    }

    private EditarAlumnoDTO obtenerControlesEditar() {
        return new EditarAlumnoDTO(this.alumnoLectura.getIdAlumno(), txtNombres.getText(), txtApellidoPaterno.getText(), txtApellidoMaterno.getText(), chkActivo.isSelected());
    }

    private void validarControles() {
        String nombres = txtNombres.getText();
        String apellidoPaterno = txtApellidoPaterno.getText();
        String apellidoMaterno = txtApellidoMaterno.getText();

        // Validar que los campos no estén vacíos
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (apellidoPaterno == null || apellidoPaterno.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido paterno no puede estar vacío.");
        }
        if (apellidoMaterno == null || apellidoMaterno.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'apellido materno' no puede estar vacío.");
        }

        // Validar que los campos superen los 3 caracteres
        if (nombres.trim().length() <= 3) {
            throw new IllegalArgumentException("El nombre debe tener más de 3 caracteres.");
        }
        if (apellidoPaterno.trim().length() <= 3) {
            throw new IllegalArgumentException("El apellido paterno debe tener más de 3 caracteres.");
        }
        if (apellidoMaterno.trim().length() <= 3) {
            throw new IllegalArgumentException("El apellido materno debe tener más de 3 caracteres.");
        }
    }

    private void procesoAccionesPantalla() {
        btnAcciones.setVisible(true);
        btnCancelar.setVisible(true);

        if (opcionAccionPantalla == OpcionesCRUDEnum.LEER) {
            btnAcciones.setVisible(false);
            btnCancelar.setVisible(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblAlumnos = new javax.swing.JTable();
        btnSiguiente = new javax.swing.JButton();
        btnAtras = new javax.swing.JButton();
        lblTituloPaginado = new javax.swing.JLabel();
        btnNuevo = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        panelDatos = new javax.swing.JPanel();
        lblApellidoMaterno = new javax.swing.JLabel();
        txtApellidoMaterno = new javax.swing.JTextField();
        chkActivo = new javax.swing.JCheckBox();
        lblNombres = new javax.swing.JLabel();
        txtNombres = new javax.swing.JTextField();
        lblApellidoPaterno = new javax.swing.JLabel();
        txtApellidoPaterno = new javax.swing.JTextField();
        btnAcciones = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblAlumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombres", "A. Paterno", "A. Materno", "Estatus", "Editar", "Eliminar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblAlumnos);

        btnSiguiente.setText("Siguiente");
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });

        btnAtras.setText("Atrás");
        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasActionPerformed(evt);
            }
        });

        lblTituloPaginado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTituloPaginado.setText("Página 1");
        lblTituloPaginado.setToolTipText("");

        btnNuevo.setText("Nuevo registro");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Administración de alumnos");

        lblApellidoMaterno.setText("Apellido Materno");

        chkActivo.setText("Activo");

        lblNombres.setText("Nombres");

        lblApellidoPaterno.setText("Apellido Paterno");

        btnAcciones.setText("Guardar registro");
        btnAcciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccionesActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

   javax.swing.GroupLayout panelDatosLayout = new javax.swing.GroupLayout(panelDatos);
panelDatos.setLayout(panelDatosLayout);
panelDatosLayout.setHorizontalGroup(
    panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(panelDatosLayout.createSequentialGroup()
        .addGap(14, 14, 14)
        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addComponent(chkActivo)
        .addContainerGap())
    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosLayout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnAcciones)
            .addComponent(btnCancelar))
        .addContainerGap())
);
panelDatosLayout.setVerticalGroup(
    panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosLayout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lblNombres)
            .addComponent(lblApellidoPaterno)
            .addComponent(lblApellidoMaterno))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(chkActivo))
        .addGap(18, 18, 18)
        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(btnAcciones)
            .addComponent(btnCancelar))
        .addContainerGap())
);


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1119, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(panelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnNuevo)
                .addGap(518, 518, 518))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTituloPaginado)
                .addGap(513, 513, 513))
            .addGroup(layout.createSequentialGroup()
                .addGap(455, 455, 455)
                .addComponent(btnAtras)
                .addGap(86, 86, 86)
                .addComponent(btnSiguiente)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSiguiente)
                    .addComponent(btnAtras))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTituloPaginado)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {                                              
        this.pagina++;
        this.cargarAlumnosEnTabla();
        this.lblTituloPaginado.setText("Página " + this.pagina);
    }                                             

    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {                                          
        if (this.pagina > 1) {
            this.pagina--;
            this.cargarAlumnosEnTabla();
            this.lblTituloPaginado.setText("Página " + this.pagina);
        }
    }                                         

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {                                          
        this.opcionAccionPantalla = OpcionesCRUDEnum.GUARDAR;
        this.procesoAccionesPantalla();
    }                                         

    private void btnAccionesActionPerformed(java.awt.event.ActionEvent evt) {                                             
        if (opcionAccionPantalla == OpcionesCRUDEnum.GUARDAR) {
            this.procesoGuardar();
        } else if (opcionAccionPantalla == OpcionesCRUDEnum.EDITAR) {
            this.procesoEditar();
        }
    }                                            

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        this.procesoResetearAcciones();
    }                                            

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        this.limpiarControles();
    }  
    public static void main(String[] args) {
    // Crea una instancia de tu implementación de IAlumnoNegocio
    IAlumnoNegocio alumnoNegocio = new AlumnoNegocio();

    // Crea una instancia de tu formulario principal y pásale la instancia de IAlumnoNegocio
    frmCRUD formulario = new frmCRUD(alumnoNegocio);

    // Hacer visible el formulario
    formulario.setVisible(true);
}


    // Variables declaration - do not modify
    private javax.swing.JButton btnAcciones;
    private javax.swing.JButton btnAtras;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JCheckBox chkActivo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblApellidoMaterno;
    private javax.swing.JLabel lblApellidoPaterno;
    private javax.swing.JLabel lblNombres;
    private javax.swing.JLabel lblTituloPaginado;
    private javax.swing.JPanel panelDatos;
    private javax.swing.JTable tblAlumnos;
    private javax.swing.JTextField txtApellidoMaterno;
    private javax.swing.JTextField txtApellidoPaterno;
    private javax.swing.JTextField txtNombres;
    // End of variables declaration
}
