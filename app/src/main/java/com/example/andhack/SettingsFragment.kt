package com.example.andhack


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class SettingsFragment : Fragment() {
    lateinit var queue: RequestQueue
    private lateinit var tvGetNick: TextView
    private lateinit var tvGetWord: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val token = SharedPrefManager.getToken(requireContext())
        Log.d("SettingsFragment", "Token: $token")
        tvGetNick = view.findViewById(R.id.tvGetNick)
        tvGetWord = view.findViewById(R.id.tvGetWord)
        // 토큰 디코딩
        token?.let {
            val nick = extractNickFromToken(it)
            Log.d("nick", nick.toString())
            tvGetNick.text = nick
            tvGetWord.text = nick?.substring(0,1) ?:""
        }

        queue = Volley.newRequestQueue(requireContext())

        val btnInfoEdit = view?.findViewById<Button>(R.id.btnInfoEdit)
        val btnLogOut = view?.findViewById<LinearLayout>(R.id.lloLogOut)
        val btnDelete = view?.findViewById<LinearLayout>(R.id.lloDelete)
        val btnEmail = view?.findViewById<LinearLayout>(R.id.lloEmail)
        val btnPremium = view?.findViewById<LinearLayout>(R.id.lloPremium)

        // 프로필 편집
        btnInfoEdit?.setOnClickListener{
            val intent = Intent(requireContext(),InfoActivity::class.java)
            startActivity(intent)
            ToastUtil.showInfoToast(requireContext(),"프로필 편집")
        }

        // 로그아웃
        btnLogOut?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton(
                    R.string.yes,
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        // 확인시 처리 로직
                        ToastUtil.showInfoToast(requireContext(),"로그아웃되었습니다.")
                        SharedPrefManager.clearToken(requireContext())
                        val intent =Intent(requireContext(), StartActivity::class.java)
                        startActivity(intent)
                    })
                .setNegativeButton(
                    R.string.no,
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        // 취소시 처리 로직
                        ToastUtil.showInfoToast(requireContext(),"취소하였습니다.")
                    })
                .show()
        }

        // 회원탈퇴 버튼 클릭 이벤트 설정
        btnDelete?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("회원 탈퇴")
                .setMessage("계정을 삭제하시겠습니까?")
                .setPositiveButton(
                    R.string.yes,
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        // 확인시 처리 로직
                        val url = "http://192.168.219.63:8089/IZG/delete"
                        val request = object : StringRequest(
                            Request.Method.POST,
                            url,
                            Response.Listener { response ->
                                // 성공 시 처리 로직
                                ToastUtil.showSuccessToast(requireContext(), "삭제 성공")
                                SharedPrefManager.clearToken(requireContext())
                                val intent = Intent(requireContext(), StartActivity::class.java)
                                startActivity(intent)
                            },
                            Response.ErrorListener { error ->
                                // 실패 시 처리 로직
                                ToastUtil.showFailureToast(requireContext(), "삭제 실패: ${error.message}")
                            }
                        ) {
                            override fun getHeaders(): Map<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Authorization"] = "Bearer $token"
                                return headers
                            }
                        }
                        // RequestQueue에 추가
                        queue.add(request)
                    })
                .setNegativeButton(
                    R.string.no,
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        // 취소시 처리 로직
                        ToastUtil.showInfoToast(requireContext(), "취소하였습니다.")
                    })
                .show()
        }

        // 고객센터
        btnEmail?.setOnClickListener{
            ToastUtil.showInfoToast(requireContext(), "고객센터 : 010-2666-7210 김성용")
        }

        // 프리미엄 시작하기
        btnPremium?.setOnClickListener{

            ToastUtil.showInfoToast(requireContext(), "프리미엄입니다.")
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val token = SharedPrefManager.getToken(requireContext())
        token?.let {
            val nick = extractNickFromToken(it)
            tvGetNick.text = nick
            tvGetWord.text = nick?.substring(0, 1) ?: ""
        }
    }
    private fun extractNickFromToken(token: String): String? {
        return try {
            val split = token.split(".")
            val decodedPayload = getJson(split[1])
            val jsonObject = JSONObject(decodedPayload)
            jsonObject.getString("nick")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getJson(strEncoded: String): String {
        val decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charsets.UTF_8)
    }

    // 닉네임을 업데이트하는 메서드
    fun updateNickname(newNick: String) {
        tvGetNick.text = newNick
        tvGetWord.text = newNick.substring(0,1)
    }
}