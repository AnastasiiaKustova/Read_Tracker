package com.example.readtracker.android.presentation.mainScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.example.readtracker.android.core.componentScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class MainScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: MainScreenStoreFactory,
    @Assisted("onCloseAppClicked") private val onCloseAppClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : MainScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect{
                when(it){
                    MainScreenStore.Label.ClickCloseApp -> onCloseAppClicked()
                }
            }
        }
    }

    override fun onCloseAppClick() {
        store.accept(MainScreenStore.Intent.ClickCloseApp)
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("onCloseAppClicked") onCloseAppClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): MainScreenComponentImpl
    }
}