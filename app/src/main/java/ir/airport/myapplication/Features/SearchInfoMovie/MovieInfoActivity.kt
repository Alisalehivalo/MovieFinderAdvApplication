package ir.airport.myapplication.Features.SearchInfoMovie

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import ir.airport.myapplication.Features.DetailsMovie.MovieDetailActivity
import ir.airport.myapplication.Pojo.Result
import ir.airport.myapplication.R
import ir.airport.myapplication.Retrofit.DaggerFactoryComponent
import ir.airport.myapplication.Retrofit.VMFactoryProvider
import ir.airport.myapplication.Utils.Consts
import ir.airport.myapplication.Utils.Consts.Companion.MOVIE_ID
import kotlinx.android.synthetic.main.about_layout.view.*
import kotlinx.android.synthetic.main.activity_movie_info.*


class MovieInfoActivity : AppCompatActivity() {
    lateinit var vm: MovieInfoVM
    lateinit var factory: VMFactoryProvider

    private val disposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_info)

        val factory = DaggerFactoryComponent.create().provideVMFactory()
        vm = ViewModelProvider(this, factory).get(MovieInfoVM::class.java)

        vm.getLiveInfo().observe(this, Observer {
            progressBar.visibility = View.INVISIBLE
            showRecyclerList(it.results)
        })
        edtSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {

                imgSrch.performClick()
            }
            false
        })

        imgSrch.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            vm.setInfo(Consts.api_key, edtSearch.text.toString())
        }
        imgInfo.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.about_layout, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("About us:")
                .setIcon(R.drawable.about)
            val  mAlertDialog = mBuilder.show()
            mDialogView.btnClose.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
       // imgArch.setOnClickListener {
       //     val intent = Intent(this, ArchiveMovieActivity::class.java)
        //    startActivity(intent)

       // }
    }

    private fun showRecyclerList(results: List<Result>) {
        recycler.adapter = SearchAdapter(results) { showDetailsPage(it) }
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun showDetailsPage(movieId: Long) {
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra(MOVIE_ID,movieId)
        startActivity(intent)
    }
}
