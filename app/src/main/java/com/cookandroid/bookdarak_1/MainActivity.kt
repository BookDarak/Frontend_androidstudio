package com.cookandroid.bookdarak_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.cookandroid.bookdarak_1.databinding.ActivityMainBinding  // <-- 여기를 추가해주세요
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"
    private lateinit var binding: ActivityMainBinding  // <-- View Binding 인스턴스를 저장하기 위한 변수 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)  // <-- View Binding 초기화
        setContentView(binding.root)  // <-- 바뀐 setContentView

        binding.buttonLogin.setOnClickListener {  // <-- 'binding.'을 사용하여 뷰에 접근
            val id = binding.editId.text.toString()  // <-- 'binding.'을 사용하여 뷰에 접근
            val pw = binding.editPw.text.toString()  // <-- 'binding.'을 사용하여 뷰에 접근

            val loginRequest = LoginRequest(id, pw)

            ApiClient.service.login(loginRequest).enqueue(object: Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val userId = response.body()?.result?.id ?: -1  // <-- 'userId'를 'id'로 수정
                        Log.d(TAG, "Logged in user ID: $userId")

                        // 로그인 성공시 NaviActivity로 이동하며 userId 전달
                        val intent = Intent(this@MainActivity, NaviActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    } else {
                        when(response.body()?.code) {
                            2014, 2015 -> dialog("이메일 오류")
                            2016, 2027 -> dialog("비밀번호 오류")
                            else -> dialog("fail")
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    dialog("서버 연결 실패")
                }
            })
        }

        // 회원가입 버튼
        binding.buttonRegister.setOnClickListener {  // <-- 'binding.'을 사용하여 뷰에 접근
            val intent = Intent(this, activity_singup::class.java)
            startActivity(intent)
        }
    }

    fun dialog(type: String){
        var dialog = AlertDialog.Builder(this)

        when(type) {
            "success" -> {
                dialog.setTitle("로그인 성공")
                dialog.setMessage("로그인 성공!")
            }
            "fail" -> {
                dialog.setTitle("로그인 실패")
                dialog.setMessage("아이디와 비밀번호를 확인해주세요")
            }
            "이메일 오류" -> {
                dialog.setTitle("로그인 실패")
                dialog.setMessage("이메일 주소가 올바르지 않습니다.")
            }
            "비밀번호 오류" -> {
                dialog.setTitle("로그인 실패")
                dialog.setMessage("비밀번호가 올바르지 않습니다.")
            }
            "서버 연결 실패" -> {
                dialog.setTitle("로그인 실패")
                dialog.setMessage("서버와 연결할 수 없습니다. 다시 시도해주세요.")
            }
        }

        dialog.setPositiveButton("확인") { _, _ ->
            Log.d(TAG, "User acknowledged the dialog")
        }
        dialog.show()
    }
}