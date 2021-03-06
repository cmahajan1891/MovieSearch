package com.example.moviesearch

import android.app.Application
import com.example.moviesearch.di.component.ApplicationComponent
import com.example.moviesearch.di.component.DaggerApplicationComponent
import com.example.moviesearch.di.module.ApplicationModule

class MovieSearchApplication : Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        injectDependencies()
    }

    private fun injectDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }

    // Needed to replace the component with a test specific one
    fun setComponent(applicationComponent: ApplicationComponent) {
        this.applicationComponent = applicationComponent
    }
}
