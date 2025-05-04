import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smdproject.R
import com.example.smdproject.ViewSearchResult

class CarAdapter(private val carList: MutableList<Car>) :
    RecyclerView.Adapter<CarAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carNameTextView: TextView = itemView.findViewById(R.id.CarName)
        val carPriceTextView: TextView = itemView.findViewById(R.id.Price)
        val carModelYearTextView: TextView = itemView.findViewById(R.id.modelYeartext)
        val carMileageTextView: TextView = itemView.findViewById(R.id.mileagetext)
        val carFuelTypeTextView: TextView = itemView.findViewById(R.id.FuelTypetext)
        val carLocationTextView: TextView = itemView.findViewById(R.id.locationtext)
        val carResultLayout: View = itemView.findViewById(R.id.CarResult)
        val carImgView: ImageView = itemView.findViewById(R.id.Carimg)


        init {
            carResultLayout.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val context = itemView.context
                    val intent = Intent(context, ViewSearchResult::class.java)
                    intent.putExtra("car", carList[position])
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.car_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val car = carList[position]
        holder.carNameTextView.text = "${car.carCompany} ${car.carModel}"
        holder.carPriceTextView.text = car.price
        holder.carModelYearTextView.text = car.model
        holder.carMileageTextView.text = car.km
        holder.carFuelTypeTextView.text = car.fuelType
        holder.carLocationTextView.text = car.city
        // Load image using Glide library
        Glide.with(holder.itemView)
            .load(car.imageUrl) // Load image from URL
            .placeholder(R.drawable.img) // Placeholder image
            .error(R.drawable.img) // Error image
            .into(holder.carImgView) // ImageView to load the image
    }

    override fun getItemCount(): Int {
        return carList.size
    }
}
