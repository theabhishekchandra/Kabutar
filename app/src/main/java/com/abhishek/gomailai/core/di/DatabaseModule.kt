package com.abhishek.gomailai.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.abhishek.gomailai.core.local.database.AppDatabase
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import com.abhishek.gomailai.core.local.entities.UsersEntity
import com.abhishek.gomailai.core.utils.DatabaseConst
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    /*@Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }*/
    @Provides
    @Singleton
    fun providedDatabase(@ApplicationContext context: Context)
    = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DatabaseConst.DB_NAME)
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideEmailDao(database: AppDatabase) = database.emailDao()

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) = database.userDao()

    // Entity
    @Provides
    @Singleton
    fun provideEmailEntity() = EmailDataEntity()

    @Provides
    @Singleton
    fun provideUserEmail() = UsersEntity()


    /*@Provides
    @Singleton
    fun provideTestsDao(db: TestDB) = db.testDao


    @Provides
    @Singleton
    fun provideEntity() = TestEntity()*/
}