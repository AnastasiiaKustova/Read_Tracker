package com.example.readtracker.android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import com.example.readtracker.android.presentation.root.RootComponentImpl
import com.example.readtracker.android.presentation.root.RootContent
import com.example.readtracker.android.ReadTrackerApp
import javax.inject.Inject
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(

) {
    install(Postgrest)
}

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootComponentFactory: RootComponentImpl.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as ReadTrackerApp).applicationComponent.inject(this)

        super.onCreate(savedInstanceState)

        setContent {

            RootContent(component = rootComponentFactory.create(
                onExitApp = { finish() },
                componentContext = defaultComponentContext()
            ))
        }
    }
}