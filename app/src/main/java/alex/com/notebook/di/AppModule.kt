package alex.com.notebook.di

import alex.com.notebook.data.NoteDatabase

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideRetrofit(): Retrofit = Retrofit.Builder()
//        .baseUrl(RestaurantApi.BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    @Provides
//    @Singleton
//    fun provideRestaurantApi(retrofit: Retrofit): RestaurantApi =
//        retrofit.create(RestaurantApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, NoteDatabase::class.java, "note_database")
            .build()

    @Provides
    fun provideRestaurantDao(db: NoteDatabase) = db.noteDao()


}