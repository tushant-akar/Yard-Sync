package com.example.yardsync.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.yardsync.R
import com.example.yardsync.databinding.FragmentVehicleOutgoingBinding
import com.example.yardsync.viewModel.VehicleViewModel

class VehicleOutgoingFragment : Fragment() {
    private var _binding: FragmentVehicleOutgoingBinding? = null
    private val binding get() = _binding!!
    private lateinit var vehicleNumber: String
    private val args by navArgs<VehicleOutgoingFragmentArgs>()
    private lateinit var viewModel: VehicleViewModel
    private lateinit var driverID: String
    private lateinit var vehicleID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVehicleOutgoingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[VehicleViewModel::class.java]
        vehicleNumber = args.vehicleNumber

        fetchVehicleDetails(vehicleNumber)

        viewModel.vehicle.observe(viewLifecycleOwner) { vehicle ->
            vehicle?.let {
                binding.edtPersons.text = Editable.Factory.getInstance()
                    .newEditable(vehicle.accompaniedPersons.toString())
                binding.edtVehicletype.text =
                    Editable.Factory.getInstance().newEditable(vehicle.vehicleType)
                binding.edtVehicleNumber.text =
                    Editable.Factory.getInstance().newEditable(vehicle.vehicleNumber)
                binding.edtIncomingWeight.text =
                    Editable.Factory.getInstance().newEditable(vehicle.incomingWeight.toString())
                binding.vehicleImage.load(vehicle.vehicleImageUrl) {
                    crossfade(true)
                }
                driverID = vehicle.driverID ?: ""
                vehicleID = vehicle.vehicleNumber
            } ?: run {
                Toast.makeText(requireContext(), "Data not fetched", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelBtn.setOnClickListener {
            findNavController().navigate(R.id.action_vehicleOutgoingFragment_to_mainActivity2)
        }

        binding.continueBtn.setOnClickListener {
            val action =
                VehicleOutgoingFragmentDirections.actionVehicleOutgoingFragmentToDriverOutgoingFragment(
                    driverID = driverID,
                    vehicleID = vehicleID
                )
            findNavController().navigate(action)
        }
    }

    private fun fetchVehicleDetails(vehicleNumber: String) {
        viewModel.fetchVehicleDetails(vehicleNumber) { success, message ->
            if (!success) {
                Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}