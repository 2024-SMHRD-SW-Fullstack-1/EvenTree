package com.example.andhack

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class GroupAddFragment : Fragment() {

    lateinit var queue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group_add, container, false)

        val btnAddSolo = view.findViewById<View>(R.id.btnAddSolo)
        val btnAddFamily = view.findViewById<View>(R.id.btnAddFamily)
        val btnAddCouple = view.findViewById<View>(R.id.btnAddCouple)
        val btnAddWork = view.findViewById<View>(R.id.btnAddWork)
        val btnAddFriend = view.findViewById<View>(R.id.btnAddFriend)

        val tvSolo = view.findViewById<TextView>(R.id.tvSolo)
        val tvFamily = view.findViewById<TextView>(R.id.tvFamily)
        val tvCouple = view.findViewById<TextView>(R.id.tvCouple)
        val tvWork = view.findViewById<TextView>(R.id.tvWork)
        val tvFriend = view.findViewById<TextView>(R.id.tvFriend)

        queue = Volley.newRequestQueue(requireContext())

        btnAddSolo.setOnClickListener {
            showCustomDialog(tvSolo.text.toString(), "Solo")
        }

        btnAddFamily.setOnClickListener {
            showCustomDialog(tvFamily.text.toString(), "Family")
        }

        btnAddCouple.setOnClickListener {
            showCustomDialog(tvCouple.text.toString(), "Couple")
        }

        btnAddWork.setOnClickListener {
            showCustomDialog(tvWork.text.toString(), "Work")
        }

        btnAddFriend.setOnClickListener {
            showCustomDialog(tvFriend.text.toString(), "Friend")
        }


        return view
    }

    private fun showCustomDialog(text: String, type: String) {
        val dialog = Dialog(requireContext(), R.style.CustomDialog)
        dialog.setContentView(R.layout.dialog_custom)

        val editText = dialog.findViewById<EditText>(R.id.etGroupTitle)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)

        editText.setText(text)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnConfirm.setOnClickListener {
            // 확인 버튼 클릭 처리
            val inputText = editText.text.toString()
            dialog.dismiss()

            val jsonObject = JSONObject().apply {
                put("groupName", inputText)
                put("type", type)  // 그룹 유형 추가
            }

            val token = SharedPrefManager.getToken(requireContext())

            val request = object: StringRequest(
                Request.Method.POST,
                "http://192.168.219.43:8089/IZG/group",
                { response ->
                    println("Response: $response")
                    if (activity is MainActivity) {
                        val mainActivity = activity as MainActivity
                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.mainContent, CalendarFragment())
                            .commit()
                    } else {
                        // activity가 null이거나 MainActivity가 아닐 때의 처리
                        println("Error: Activity is not MainActivity or is null")
                    }
                },
                { error ->
                    println("Error: ${error.message}")
                }
            ) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return jsonObject.toString().toByteArray(Charsets.UTF_8)
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-AUTH-TOKEN"] = token ?: ""
                    return headers
                }
            }
            queue.add(request)
        }
        dialog.show()
    }
}