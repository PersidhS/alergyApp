import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myallergies.R

class AllergiesAdapter(
    private val allergies: MutableList<String>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<AllergiesAdapter.AllergyViewHolder>() {

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_allergy, parent, false)
    return AllergyViewHolder(view)
}

    override fun onBindViewHolder(holder: AllergyViewHolder, position: Int) {
        val allergy = allergies[position]
        holder.bind(allergy, position)
    }

    override fun getItemCount(): Int = allergies.size

inner class AllergyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvAllergy: TextView = itemView.findViewById(R.id.tvAllergy)
    private val btnDelete: Button? = itemView.findViewById(R.id.btnDelete)

    init {
        if (btnDelete == null) {
            Log.e("AllergyViewHolder", "btnDelete não encontrado no layout!")
        }
    }

    fun bind(allergy: String, position: Int) {
        tvAllergy.text = allergy
        btnDelete?.setOnClickListener {
            onDeleteClick(position)
        }
    }
}
}