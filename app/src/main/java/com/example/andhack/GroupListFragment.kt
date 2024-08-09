package com.example.andhack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class GroupListFragment : DialogFragment() {

    private lateinit var groupListContainer: LinearLayout
    private lateinit var queue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val view = inflater.inflate(R.layout.fragment_group_list, container, false)

        groupListContainer = view.findViewById(R.id.groupListContainer)
        val btnAddGroup = view.findViewById<Button>(R.id.btnAddGroup)

        queue = Volley.newRequestQueue(requireContext())
        loadGroupList()

        btnAddGroup.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, GroupAddFragment()).commit()
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun loadGroupList() {
        val token = SharedPrefManager.getToken(requireContext())

        val request = object : JsonArrayRequest(
            Request.Method.GET,
            "http://192.168.219.43:8089/IZG/groups",
            null,
            { response ->
                groupListContainer.removeAllViews()
                for (i in 0 until response.length()) {
                    val group = response.getJSONObject(i)
                    addGroupToView(group)
                }
            },
            { error ->
                println("Error: ${error.message}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["X-AUTH-TOKEN"] = token ?: ""
                return headers
            }
        }
        queue.add(request)
    }

    private fun addGroupToView(group: JSONObject) {
        val groupName = group.getString("groupName")
        val textView = TextView(requireContext()).apply {
            text = groupName
            textSize = 18f
            setPadding(16, 16, 16, 16)

            // 점을 텍스트 앞에 추가
            val drawable = resources.getDrawable(R.drawable.dot_drawable, null)
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            compoundDrawablePadding = 16 // 점과 텍스트 사이의 간격
        }
        groupListContainer.addView(textView)
    }
}
