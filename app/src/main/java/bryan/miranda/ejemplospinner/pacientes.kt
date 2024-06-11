package bryan.miranda.ejemplospinner

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassDoctores
import java.util.Calendar
import java.util.UUID

class pacientes : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //

        val root = inflater.inflate(R.layout.fragment_pacientes, container, false)


                    //Mando a llamar mi spinner PARA PROGRAMARLO
                    val spDoctores = root.findViewById<Spinner>(R.id.spDoctores)

                    //1- Creamos la función que haga un select a los datos que quiero obtener

                    fun obtenerDoctores(): List<dataClassDoctores> {

                        val objConexion = ClaseConexion().cadenaConexion()

                        //Creo un statement que me ejecute el select
                        val statement = objConexion?.createStatement()

                        val resultSet = statement?.executeQuery("select * from tbDoctores")!!

                        val listaDoctores = mutableListOf<dataClassDoctores>()

                        while (resultSet.next()) {
                            val uuid = resultSet.getString("DoctorUUID")
                            val nombre = resultSet.getString("nombreDoctor")
                            val especialidad = resultSet.getString("especialidad")
                            val telefono = resultSet.getString("telefono")
                            val unDoctorCompleto =
                                dataClassDoctores(uuid, nombre, especialidad, telefono)
                            listaDoctores.add(unDoctorCompleto)


                        }
                        return listaDoctores
                    }

                    //Ultimo punto:
                    //Programar el spinner y llenarlo de datos

                    CoroutineScope(Dispatchers.IO).launch {

                        //1- Obtener el listado de datos que quiero mostrar
                        val listadoDeDoctores = obtenerDoctores()
                        val nombreDoctores = listadoDeDoctores.map { it.nombreDoctor }

                        withContext(Dispatchers.Main) {
                            //2 Creo y  configuto el adaptador
                            val miAdaptadorr = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                nombreDoctores
                            )
                            spDoctores.adapter = miAdaptadorr
                        }

                    }

        val txtNombrePaciente = root.findViewById<EditText>(R.id.txtNombrePaciente)
        val spnombreDoctores = root.findViewById<Spinner>(R.id.spDoctores)
        val txtFecha = root.findViewById<TextView>(R.id.txtFechaNacimiento)
        val txtDireccion = root.findViewById<EditText>(R.id.txtDireccionPaciente)
        val btnGuardar = root.findViewById<Button>(R.id.btnGuardarPaciente)


        //Importar lo que esta en rojo con ALT + ENTER
        txtFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada =
                        "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtFecha.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )
            datePickerDialog.show()
        }

        btnGuardar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //Guardar datos
                //1- Creo un objeto de la clase conexion
                val claseConexion = ClaseConexion().cadenaConexion()
                val doctores = obtenerDoctores()
                val doctorid = doctores[spDoctores.selectedItemPosition].DoctorUUID

                //2- creo una variable que contenga un PrepareStatement
                val addPaciente =
                    claseConexion?.prepareStatement("insert into tbPacientes(PacienteUUID, DoctorUUID, Nombre, FechaNacimiento, Direccion) values(?, ?, ?, ?,?)")!!
                addPaciente.setString(1, UUID.randomUUID().toString())
                addPaciente.setString(2, doctorid)
                addPaciente.setString(3, txtNombrePaciente.text.toString())
                addPaciente.setString(4, txtFecha.text.toString())
                addPaciente.setString(5, txtDireccion.text.toString())
                addPaciente.executeUpdate()

                //Abro una corrutina para mostrar una alerta y limpiar los campos
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                    txtNombrePaciente.setText("")
                    txtDireccion.setText("")
                    txtFecha.setText("")
                }

            }

                }
        return root

    }


    }







