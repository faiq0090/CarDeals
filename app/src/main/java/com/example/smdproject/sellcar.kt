import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.smdproject.R

class sellcar : Fragment() {

    private lateinit var carType: Spinner
    private lateinit var carModel: Spinner
    private lateinit var city: Spinner
    private lateinit var model: Spinner
    private lateinit var registered: Spinner
    private lateinit var color: Spinner
    private lateinit var fuelType: Spinner
    private lateinit var bodyType: Spinner
    private lateinit var transmissionType: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sellcar, container, false)

        carType = view.findViewById(R.id.carType)
        carModel = view.findViewById(R.id.carModel)
        city = view.findViewById(R.id.city)
        model = view.findViewById(R.id.model)
        registered = view.findViewById(R.id.registered)
        color = view.findViewById(R.id.color)
        fuelType = view.findViewById(R.id.fuelType)
        bodyType = view.findViewById(R.id.bodyType)
        transmissionType = view.findViewById(R.id.transmissionType)

        // Set up spinners with hint logic
        setUpSpinner(carType, listOf("SUV", "Sedan", "Hatchback", "Truck"))
        setUpSpinner(carModel, listOf("Toyota", "Honda", "Suzuki", "Hyundai"))
        setUpSpinner(city, listOf("Karachi", "Lahore", "Islamabad"))
        setUpSpinner(model, listOf("2020", "2021", "2022", "2023"))
        setUpSpinner(registered, listOf("Yes", "No"))
        setUpSpinner(color, listOf("Black", "White", "Silver", "Blue"))
        setUpSpinner(fuelType, listOf("Petrol", "Diesel", "Electric", "Hybrid"))
        setUpSpinner(bodyType, listOf("Coupe", "Convertible", "Wagon", "Crossover"))
        setUpSpinner(transmissionType, listOf("Automatic", "Manual"))

        return view
    }

    private fun setUpSpinner(spinner: Spinner, items: List<String>) {
        val listWithHint = listOf("Select an option...") + items
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listWithHint
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                view.alpha = if (position == 0) 0.5f else 1.0f
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
    }

}
