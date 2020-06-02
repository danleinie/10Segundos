package com.danielleiva.diezsegundos.common

import com.danielleiva.diezsegundos.api.NetworkModule
import com.danielleiva.diezsegundos.ui.auth.LoginActivity
import com.danielleiva.diezsegundos.ui.auth.RegisterActivity
import com.danielleiva.diezsegundos.ui.leaderboard.LeaderboardFragment
import com.danielleiva.diezsegundos.ui.quiz.QuizActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component( modules = [NetworkModule::class])
interface AplicationComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
    fun inject(leaderboardFragment: LeaderboardFragment)
    fun inject(quizActivity: QuizActivity)
}