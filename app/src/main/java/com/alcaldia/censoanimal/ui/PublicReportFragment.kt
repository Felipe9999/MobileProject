package com.alcaldia.censoanimal.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.alcaldia.censoanimal.R

class PublicReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_public_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnOpenFullMistreatmentForm = view.findViewById<Button>(R.id.btnOpenFullMistreatmentForm)
        val btnGoToLogin = view.findViewById<Button>(R.id.btnGoToLogin)

        btnOpenFullMistreatmentForm.setOnClickListener {
            val intent = Intent(requireContext(), ReportMistreatmentActivity::class.java)
            startActivity(intent)
        }

        btnGoToLogin.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
