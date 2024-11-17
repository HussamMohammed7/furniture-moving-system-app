package com.example.senior_project2

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_edit__profile_frag.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Edit_Profile_frag.newInstance] factory method to
 * create an instance of this fragment.
 */
class Edit_Profile_frag : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var name : String
    private lateinit var phone : String
    private lateinit var password : String
    private lateinit var email : String
    private var mRef : DatabaseReference ? = null
    private var client = mutableListOf<CarrierUsers>()



    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        database = Firebase.database
        super.onCreate(savedInstanceState)

         mRef = database.reference.child("Carrier").child(auth.currentUser!!.uid)


        editProfile()













    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit__profile_frag, container, false)
    }
    private fun editProfile(){
        mRef?.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                edit_name_profile.text = snapshot.child("name").getValue(String::class.java)!!
                 email  = snapshot.child("email").getValue(String::class.java)!!
                 password  = snapshot.child("password").getValue(String::class.java)!!
                 phone = snapshot.child("phone").getValue(String::class.java)!!
                //edit_name_profile.text = name
                edit_email_profile.text = email
                edit_password_profile.text = password
                edit_phone_profile.text = phone






            }


            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())

            }

        })
    }
    private fun updateText(nameOfChange : String){


            val builder = AlertDialog.Builder(requireActivity())
            val inflater =layoutInflater
            val dialog = inflater.inflate(R.layout.edit_text_view,null)
            val editText = dialog.findViewById<EditText>(R.id.edit_text_view_content)
            with(builder){

                setTitle("Enter the new $nameOfChange")
                if (nameOfChange == "Phone"){
                    setTitle("Enter the new $nameOfChange +(start with (+966)")
                }
                setPositiveButton("Confirm"){dialog, which ->
                    checkTextEdit(nameOfChange , editText)
                }
                setNegativeButton("Cancel"){dialog,which ->
                    android.widget.Toast.makeText(requireActivity(), "Something wrong try again later ", android.widget.Toast.LENGTH_SHORT).show()

                }
                setView(dialog)
                show()
            }




    }

    override fun onStart() {
        super.onStart()
        editProfile()

         edit_name.setOnClickListener{
             updateText("Name")
         }
        edit_email.setOnClickListener{
            updateText("Email")

        }
        edit_phone.setOnClickListener{
            updateText("Phone")

        }
        edit_password.setOnClickListener{
            updateText("Password")

        }


    }
    private fun checkTextEdit(type : String , editText: EditText) : Boolean{
        if(editText.text.isEmpty()){
            Toast.makeText(requireActivity(), "Enter something please", Toast.LENGTH_SHORT).show()
            return false
        }

        if(type == "Phone"){

            if(!editText.text.toString().startsWith("5")){
                Toast.makeText(requireActivity(), "Enter a valid number", Toast.LENGTH_SHORT).show()
                return false
            }
            if(editText.text.toString().length != 9 ){
                Toast.makeText(requireActivity(), "Phone number should be 9 digit", Toast.LENGTH_SHORT).show()
                return false
            }

        }
        if (type == "Email")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editText.text.toString()).matches()) {
            Toast.makeText(requireActivity(), "Enter a valid Email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (type == "Password")
        if (editText.text.toString().length < 7) {
            Toast.makeText(requireActivity(), "Password must be 8 characters or more", Toast.LENGTH_SHORT).show()
            return false
        }

        return true

    }

}